package com.systex.jbranch.ws.external.service.tx.action;

import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._067050_067101DAO;
import com.systex.jbranch.fubon.commons.cbs.service.FP032151Service;
import com.systex.jbranch.fubon.commons.cbs.service.WMS032154Service;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154InputVO;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component("kycUpdateBasicTxAction")
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
public class kycUpdateBasic extends TxAction {
    @Autowired
    private SOT701 sot701;
    @Autowired
    private FP032151Service fp032151Service;
    @Autowired
    private _067050_067101DAO _067050_067101dao;
    @Autowired
    private WMS032154Service wms032154Service;
    @Autowired
    private CBSService cbsService;

    private Map<String, String> childrenMap = new HashMap();
    private Map<String, String> marriageMap = new HashMap();
    private Map<String, String> careerMap = new HashMap();
    private Map<String, String> educationMap = new HashMap();

    @Override
    public String execute(String custId) throws Exception {
        initParameterMapping();

        String idType = cbsService.getCBSIDCode(custId);
        // 呼叫電文取得 KYC 基本資料並且轉換為新理專格式
        FP032151OutputVO txOutput = new FP032151OutputVO();
        List<CBSUtilOutputVO> txData = _067050_067101dao.search(custId, idType);
        fp032151Service._067050_067101Transfer(txOutput, txData);

        // 電文的四個欄位均沒有資訊，才去撈取 DB 資料上送電文
        KYC311InputVO dbData = null;
        if (checkTxData(txOutput)) {
            // 將 DB 的 KYC 基本資料從新理專格式轉換為電文格式，並上送電文更新欄位
            dbData = transferToKycInputVO(getKycDataFromDB(custId));
            WMS032154InputVO esbVO = new WMS032154InputVO();
            sot701.kycBasicDataTransfer(dbData, esbVO);
            wms032154Service.updateBasic067101(esbVO ,txData);
        }
        return getXml(custId, txOutput, dbData);
    }

    private void initParameterMapping() throws JBranchException {
        XmlInfo xmlInfo = new XmlInfo();
        careerMap = xmlInfo.doGetVariable("KYC.CAREER", FormatHelper.FORMAT_3);
        childrenMap = xmlInfo.doGetVariable("KYC.CHILD_NO", FormatHelper.FORMAT_3);
        educationMap = xmlInfo.doGetVariable("KYC.EDUCATION", FormatHelper.FORMAT_3);
        marriageMap = xmlInfo.doGetVariable("KYC.MARRAGE", FormatHelper.FORMAT_3);
    }

    private KYC311InputVO transferToKycInputVO(Map<String, String> dbData) {
        KYC311InputVO inputVO = new KYC311InputVO();
        inputVO.setCHILD_NO(dbData.get("CUST_CHILDREN_AFTER"));
        inputVO.setMARRAGE(dbData.get("CUST_MARRIAGE_AFTER"));
        inputVO.setCAREER(dbData.get("CUST_CAREER_AFTER"));
        inputVO.setEDUCATION(dbData.get("CUST_EDUCTION_AFTER"));
        return inputVO;
    }

    private Map getKycDataFromDB(String custId) throws JBranchException {
        List<Map> data = Manager.manage(this.getDataAccessManager())
                .append("select CUST_CHILDREN_AFTER, ") // 子女人數
                .append("       CUST_MARRIAGE_AFTER, ") // 婚姻
                .append("       CUST_CAREER_AFTER, ")   // 職業
                .append("       CUST_EDUCTION_AFTER ")  // 教育程度
                .append("from TBKYC_INVESTOREXAM_D ")
                .append("where CUST_ID = :custId ")
                .put("custId", custId)
                .query();
        if (data.isEmpty())
            throw new APException("客戶: " + custId + " 在 TBKYC_INVESTOREXAM_D 無資料。");
        return data.get(0);
    }

    private boolean checkTxData(FP032151OutputVO txData) {
        // 子女人數與職業如為 0，也代表沒有資訊
        return (isBlank(txData.getCHILD_NO()) || "0".equals(txData.getCHILD_NO())) &&  // 子女人數
                isBlank(txData.getMARRAGE()) &&  // 婚姻
                (isBlank(txData.getCAREER()) || "0".equals(txData.getCAREER())) &&   // 職業
                isBlank(txData.getEDUCATION());  // 教育程度
    }

    private String getXml(String custId, FP032151OutputVO txOutput, KYC311InputVO dbData) {
        return "<custId>" + custId + "</custId>\n" +
                "<beforeUpdate>\n" +
                getDataTemplate(txOutput.getCHILD_NO(), txOutput.getMARRAGE(), txOutput.getCAREER(), txOutput.getEDUCATION()) +
                "</beforeUpdate>\n" +

                (dbData == null? "" : "<afterUpdate>\n" +
                        getDataTemplate(
                                dbData.getCHILD_NO(),
                                dbData.getMARRAGE(),
                                dbData.getCAREER(),
                                dbData.getEDUCATION()) + "</afterUpdate>\n");
    }

    private String getDataTemplate(String childNo, String marrage, String career, String education) {
        return "<CHILD_NO>" + childrenMap.get(defaultString(childNo)) + "</CHILD_NO>\n" +
                "<MARRAGE>" + marriageMap.get(defaultString(marrage)) + "</MARRAGE>\n" +
                "<CAREER>" + careerMap.get(defaultString(career)) + "</CAREER>\n" +
                "<EDUCATION>" + educationMap.get(defaultString(education)) + "</EDUCATION>\n";
    }
}
