package com.systex.jbranch.fubon.commons.tx.traffic;

import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.esb.vo.*;
import com.systex.jbranch.fubon.commons.tx.tool.Navigation;
import com.systex.jbranch.fubon.jlb.FmpJRunTx;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.ENCODING_BIG5;
import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.FONTREMAPPING_OFF;
import static com.systex.jbranch.fubon.commons.tx.tool.Box.*;

@Component("ESB")
@Scope("prototype")
public class Esb extends Truck {
    /**
     * ESB 電文實作邏輯
     **/
    private FmpJRunTx tx;
    /**
     * RequestVO
     **/
    private ESBUtilInputVO request;
    /**
     * ResponseVO
     **/
    private List<ESBUtilOutputVO> response;
    /**
     * 執行狀態忽略碼
     **/
    private String ignoredCodes;
    @Autowired
    private Navigation nav;

    /**
     * 初始化 Esb 相關物件
     **/
    @Override
    public void configure() throws Exception {
        response = new ArrayList();
        tx = new FmpJRunTx(journal.getUrl(), journal.getItemId());
        queryIgnoredCode();
    }

    /**
     * 查詢需要忽略的電文執行狀態
     **/
    private void queryIgnoredCode() throws JBranchException {
        List result = Manager.manage(this.getDataAccessManager())
                .append("select PARAM_NAME_EDIT from TBSYSPARAMETER where PARAM_TYPE = 'SOT.ESB_ERROR_CODE_BYPASS' ")
                .query();
        if (result.isEmpty()) setIgnoredCodes("");
        else setIgnoredCodes(StringUtils.defaultString((String) ((Map) result.get(0)).get("PARAM_NAME"), ""));
    }

    @Override
    public void setRequestVO(Object requestVO) {
        request = (ESBUtilInputVO) requestVO;
        journal.setModule(StringUtils.defaultString(request.getModule(), "not Offer"));
        /** request 參數設置 **/
        request.setTxid(journal.getItemId());
        request.setEncoding(ENCODING_BIG5);
        request.setFontremapping(FONTREMAPPING_OFF);
        /** request header 基本參數設置 **/
        TxHeadVO head = request.getTxHeadVO();
        head.setHWSID(journal.getId());
        head.setHSTANO(journal.getSeq());
        head.setHTXTID(journal.getItemId());
        head.setHDRVQ1(nav.getHostDriveQueue(journal.getItemId()));
    }

    /**
     * 執行電文相關邏輯
     **/
    @Override
    protected void send() throws Exception {
        /** 準備上行電文 **/
        prepareOnMsg(marshal(request));
        /** 發送電文 **/
        tx.run(journal.getId(), journal.getId());
        /** 處理下行電文 **/
        processOffMsg(tx.getOutboundXMLString());
    }

    /**
     * 當主機回覆制式化錯誤訊息時, 先判斷該電文是否有特殊規格處理，否則直接將訊息顯示於畫面上
     **/
    private void checkError(String offMsg, EsbSpec spec) throws Exception {
        ESBUtilOutputVO outputVO = unmarshal(offMsg, ESBUtilOutputVO.class, new String[]{"TxHead", "TxBody"});

        String herrid = outputVO.getTxHeadVO().getHERRID();
        ESBErrorVO esbErrorVO = outputVO.getEsbErrorVO();
        if (null != spec) {
            spec.setHERRID(herrid);
            spec.setErrorVO(esbErrorVO);
            spec.process();
        }

        // 如果有 Spec，且 Spec 設定該錯誤可忽略
        if ((null != spec && spec.isHasCustomErrorProcess())
                // 忽略掉共用的特定執行狀態
                || getIgnoredCodes().contains(herrid)) return;

        /** 如果回傳錯誤，電文 XML Unmarshal 後，ESBErrorVO 的 EMSGID 會有值 **/
        if (null != esbErrorVO.getEMSGID())
            throw new APException(
                    String.format("ESB_LOG HTXTID:%s ,HERRID:%s ,EMSGID:%s ,EMSGTXT:%s , 電文發送Log檔.esb_sno:%s",
                            journal.getItemId(), herrid, esbErrorVO.getEMSGID(), esbErrorVO.getEMSGTXT(), journal.getSeq()));
    }

    /**
     * 判斷是否還有資料需要繼續上送電文，HRETRN: "C" 代表還有資料
     **/
    private void checkSendAgain(ESBUtilOutputVO outputVO, EsbSpec spec) throws Exception {
        String hretrn = outputVO.getTxHeadVO().getHRETRN();

        if (null != spec) {
            spec.setHRETRN(hretrn);
            spec.setTxData(outputVO);
            spec.process();
        }
        
        // 如果該電文有特殊規格且需要上送多筆則繼續上送，否則以 TxHead.HRETRN 為準
        if ((null != spec && spec.isMultiple()) ||
                (null == spec && StringUtils.equals(hretrn, "C"))) {
            request.getTxHeadVO().setHRETRN(hretrn);
            spec.clear(); //0615: 清除該次spec的txData
            send();
        }
    }

    /**
     * 處理下行電文
     **/
    private void processOffMsg(String offMsg) throws Exception {
        EsbSpec spec = configureIfExistsSpec();

        recordOffMsg(xmlFormat(offMsg));
        /** 確認是否有錯誤訊息 **/
        checkError(offMsg, spec);
        /** 替換 TagName 以便 unmarshal **/
        ESBUtilOutputVO outputVO = unmarshal(offMsg.replaceAll("TxBody", journal.getItemId()), ESBUtilOutputVO.class, new String[]{"TxHead", journal.getItemId()});
        /** 將下行物件 ESBUtilOutputVO 加到集合 Response 中 **/
        response.add(outputVO);
        checkSendAgain(outputVO, spec);
        

    }

    /** 部分電文有特殊規格，如果有則配置並返回規格。 **/
    private EsbSpec configureIfExistsSpec() {
        EsbSpec spec = request.getSpec();
        if (null != spec) {
            spec.setRequest(request);
        }
        return spec;
    }

    /**
     * 準備上行電文
     **/
    private void prepareOnMsg(String onMsg) {
        tx.setInboundXML(onMsg);
        recordOnMsg(onMsg);
    }

    /**
     * 取得電文回傳結果
     **/
    public List getResponseVO() {
        return response;
    }

    /**
     * Esb 的電文 RequestVO
     **/
    public static ESBUtilInputVO createRequestVO() {
        return new ESBUtilInputVO();
    }

    /**
     * Esb 的電文 Request HeaderVO
     **/
    public static TxHeadVO createTxHeadVO() {
        return new TxHeadVO();
    }

    public String getIgnoredCodes() {
        return ignoredCodes;
    }

    public void setIgnoredCodes(String ignoredCodes) {
        this.ignoredCodes = ignoredCodes;
    }
}
