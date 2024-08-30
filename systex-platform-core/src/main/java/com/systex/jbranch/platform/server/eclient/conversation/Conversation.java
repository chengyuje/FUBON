package com.systex.jbranch.platform.server.eclient.conversation;

import com.systex.jbranch.platform.common.dataManager.*;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.conversation.TiaHelperIF;
import com.systex.jbranch.platform.server.conversation.TiaIF;
import com.systex.jbranch.platform.server.conversation.ToaHelperIF;
import com.systex.jbranch.platform.server.conversation.message.EnumMessageType;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 建立時機:
 * 1.在入口程式處建立，透過 TIA 的解析，回傳 UUID，
 * 並將其 TIA 的畫面資料，存放至 DataManager
 * 2.在 LocalAP 處建立，藉由 TOA 的格式組成，將資料
 * 送回 Client
 *
 * @author Ryan
 */
public class Conversation {
// ------------------------------ FIELDS ------------------------------

    public static final String TIA_HEAD = "eClient";
    private TIA tia = new TIA();
    private TOA toa = null;
    private UUID uuid = null;
    private Map<String, Object> ejDataMap = null;

// -------------------------- OTHER METHODS --------------------------

    /**
     * @param
     * @return 根據 Request 內的 Header 資料，取得此次交易 Biz 代號
     * @throws
     * @see
     * @since 1.01
     */
    public String getBizCode() {
        return tia.getBizCode();
    }

    /**
     * 依功能面，分解成 TIA、TOA
     *
     * @param
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    public TiaHelperIF getTiaHelper() {
        //if (tia == null)
        //	tia = new TIA();

        return tia;
    }

    /**
     * 依功能面，分解成 TIA、TOA
     *
     * @param
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    public ToaHelperIF getToaHelper() {
        if (toa == null) {
            toa = new TOA();
        }
        return toa;
    }

    /**
     * @param
     * @return 根據 Request 內的 Header 內容，組成 UUID 回傳
     * @throws
     * @see
     * @since 1.01
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @param req
     * @param resp
     * @throws JBranchException
     */
    public void recvFromClient(ServletRequest req, ServletResponse resp) throws JBranchException {
        java.io.PrintWriter out = null;
        try {
            resp.setContentType("text/html; charset=UTF-8");
            req.setCharacterEncoding("UTF-8");
            out = resp.getWriter();
            //javax.servlet.http.HttpServletResponse response =((javax.servlet.http.HttpServletResponse)resp);
            HttpServletRequest request = ((HttpServletRequest) req);

            tia.parseTIA(request);
            uuid = tia.getUUID();

            //charlotte 20081028 add
            if (DataManager.getWorkStation(uuid) == null) {
                WorkStation ws = new WorkStation();
                ws.setBrchID(uuid.getBranchID());
                ws.setWsID(uuid.getWsId());
                DataManager.setWorkStation(uuid, ws);
            }
            if (DataManager.getSection(uuid) == null) {
                Section section = new Section();
                section.setSectionID(uuid.getSectionID());
                DataManager.setSection(uuid, section);
            }

            DataManager.getSection(uuid).setOut(out);
            String sBizCode = tia.getBizCode();
            //for 每個trx都有獨立的servertransaction
            //=====================================================
            DataManager.getSection(uuid).setServerTransaction(null);
            //=====================================================
            DataManager.getServerTransaction(uuid).setBizCode(sBizCode);
            threadLogger.println("將 out 物件存入 DataManager 成功 uuid=" + uuid.toString() + " BizCode=" + sBizCode);
            //
            TiaIF inputFields = getTia();
            DataManager.getConversationVO(uuid).setTia(inputFields);
            threadLogger.println("將 inputFields 物件存入 DataManager 成功 " + uuid.toString());
            //
            String changeTxn = this.getTia().Headers().getStr(EnumTiaHeader.ClientTransaction);
            if (changeTxn.equalsIgnoreCase("true")) {
                threadLogger.println("建立新的 ClientTransaction=" + uuid.toString());
                changeClientTransaction(uuid, this.getTxnCode());
            }
            //
            threadLogger.println("recvFromClient Finish " + uuid.toString());
        }
        catch (Exception e) {
            threadLogger.println("Tia Parse Error!!");
            TOAMsg toaError = new TOAMsg();
            toaError.setMsg(EnumShowType.Show,
                    EnumMessageType.Error,
                    "Z999", e.getMessage());
            out.println(toaError.toString());
            out.flush();
            throw new JBranchException(e.getMessage());
        }
    }

    public TiaIF getTia() {
        return tia.getTia();
    }

    /**
     * 更替ClientTransaction<br>
     *
     * @param uuid
     * @param txnId
     * @throws JBranchException
     * @throws DAOException
     */
    private void changeClientTransaction(UUID uuid, String txnId) throws DAOException, JBranchException {
        ClientTransaction ct = new ClientTransaction();
        //ct.setTxnCode(txnId);
        DataManager.setClientTransaction(uuid, ct);
    }

    /**
     * @param
     * @return 根據 Request 內的 Header 資料，取得此次交易代號
     * @throws
     * @see
     * @since 1.01
     */
    public String getTxnCode() {
        return tia.getTxnCode();
    }
}
