package com.systex.jbranch.app.server.fps.fps250;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.systex.jbranch.app.server.fps.fps200.FPS200;
import com.systex.jbranch.app.server.fps.fps210.FPS210;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 特定目的投資組合歷史規劃
 * 
 * @author Juan
 * @since 18-03-01
 */
@Component("fps250")
@Scope("request")
public class FPS250 extends FubonWmsBizLogic {

    SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");
    private Logger logger = LoggerFactory.getLogger(FPS250.class);
    @Autowired
    @Qualifier("fps210")
    private FPS210 fps210;
    @Autowired
    @Qualifier("fps200")
    private FPS200 fps200;

    /**
     * 取得查詢結果
     * 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void inquire(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
        FPS250InputVO inputVO = (FPS250InputVO) body;
        FPS250OutputVO outputVO = new FPS250OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        String planID = inputVO.getPlanID();

        outputVO.setOutputList(StringUtils.isBlank(planID) ? getHisQuery(dam, inputVO) : fps210.getCustPlan(dam, inputVO.getPlanID())); // 回傳資料
        this.sendRtnObject(outputVO);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPlanHead(DataAccessManager dam, String planID) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT PLAN.*, (PLAN.INS_POLICY_AMT+PLAN.INS_SAV_AMT) AS INS_AMT ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD PLAN ");
        sb.append(" WHERE PLAN.PLAN_ID = :planID ");
        qc.setObject("planID", planID);

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    public List<Map<String, Object>> getHisQuery(DataAccessManager dam, FPS250InputVO inputVO) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" WITH");
        sb.append(" LOG AS (");
        sb.append("     SELECT COUNT(*) EXEC_CNT,");
        sb.append("     TO_CHAR(MAX(FILE_LOG.CREATETIME), 'YYYY/MM/DD') EXEC_TIME,");
        sb.append("     FILE_LOG.EXEC_TYPE,");
        sb.append("     FILE_LOG.PLAN_TYPE,");
        sb.append("     FILE_LOG.PLAN_ID");
        sb.append("     FROM TBFPS_PORTFOLIO_PLAN_FILE_LOG FILE_LOG");
        sb.append("     WHERE FILE_LOG.PLAN_TYPE = 'INV'");
        sb.append("     GROUP BY FILE_LOG.PLAN_ID, FILE_LOG.PLAN_TYPE, FILE_LOG.EXEC_TYPE)");
        sb.append(" SELECT HE.PLAN_STATUS,");
        sb.append("     HE.VALID_FLAG,");
        sb.append("     INFO.BRANCH_NBR,");
        sb.append("     INFO.BRANCH_NAME,");
        sb.append("     INFO.EMP_NAME,");
        sb.append("     HE.CREATOR,");
        sb.append("     HE.LASTUPDATE,");
        sb.append("     HE.CREATETIME,");
        sb.append("     HE.PLAN_ID,");
        sb.append("     HE.CUST_RISK_ATR,");
        sb.append("     HE.CUST_ID,");
        sb.append("     HE.PLAN_STEP,");
        sb.append("     CASE WHEN TMP.COUNTINV > 0 AND HE.PLAN_STEP = 4 THEN '有，' || TMP.COUNTINV || '筆' ELSE '無' END COUNTINV,");
        sb.append("     NVL((SELECT LOG.EXEC_CNT FROM LOG WHERE HE.PLAN_ID = LOG.PLAN_ID AND LOG.EXEC_TYPE = 'E'), 0) IS_EMAIL,");
        sb.append("     NVL((SELECT LOG.EXEC_CNT FROM LOG WHERE HE.PLAN_ID = LOG.PLAN_ID AND LOG.EXEC_TYPE = 'P'), 0) IS_PRINT");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD HE");
        sb.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = HE.CREATOR");
        sb.append(" LEFT JOIN (SELECT COUNT(*) COUNTINV, PLAN_ID");
        sb.append("     FROM TBFPS_PORTFOLIO_PLAN_INV WHERE 1=1");
        sb.append("     AND PRD_TYPE IS NOT NULL");
//        sb.append("     AND ((NVL(INVENTORY_ORG_AMT, 0) > 0) and (NVL(INVENTORY_ORG_AMT, 0) - NVL(PURCHASE_ORG_AMT, 0) - NVL(RDM_ORG_AMT_ORDER, 0) > 0)");
//        sb.append("     OR (NVL(INVENTORY_ORG_AMT, 0) <= 0) and (NVL(PURCHASE_ORG_AMT, 0) - NVL(INVENTORY_ORG_AMT, 0) - NVL(PURCHASE_ORG_AMT_ORDER, 0) > 0)");
        sb.append("     AND (NVL(PURCHASE_ORG_AMT_ORDER, 0) > 0 OR NVL(RDM_ORG_AMT_ORDER, 0) > 0");
        sb.append("     ) GROUP BY PLAN_ID) TMP ON TMP.PLAN_ID = HE.PLAN_ID");
        sb.append(" WHERE HE.CUST_ID = :custID");

        qc.setObject("custID", inputVO.getCustID());
        if (StringUtils.isNotBlank(inputVO.getPlanStatus())) {
            sb.append(" AND HE.PLAN_STATUS = :status ");
            qc.setObject("status", inputVO.getPlanStatus());
        }
        if (StringUtils.isNotBlank(inputVO.isDisable())) {
            sb.append(" AND substr(HE.VALID_FLAG, 0, 1) = :isDisable ");
            qc.setObject("isDisable", inputVO.isDisable());
        }
        sb.append(dateQueryStr("HE.LASTUPDATE", qc, inputVO.getSD(), inputVO.getED()));

        sb.append(" ORDER BY decode(nvl(he.valid_flag, 'Y'), 'Y', 0, 1), he.lastupdate desc");
        // sb.append(" ORDER BY decode(nvl(he.valid_flag, 'Y'), 'Y', 0, 'N1', 1, 'N2',
        // 2, 3), he.lastupdate desc, he.PLAN_STATUS, decode(he.PLAN_STATUS,
        // 'PRINT_THINK', 0, 'PLAN_STEP', 1, 'PRINT_REJECT', 2, 3) ");
        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    /**
     * 刪除
     * 
     * @param body
     * @param header
     * @throws DAOException
     * @throws JBranchException
     */
    public void deletePlan(Object body, IPrimitiveMap header) throws DAOException, JBranchException{
        FPS250InputVO inputVO = (FPS250InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String planID = inputVO.getPlanID();

        if (deletePlanHead(dam, planID) && fps200.deletePlanDetails(dam, planID)) {
            this.sendRtnObject(true);
        } else {
            throw new APException(""); // throw error
        }
    }

    public boolean deletePlanHead(DataAccessManager dam, String planID) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(" DELETE TBFPS_PORTFOLIO_PLAN_INV_HEAD ");
            sb.append(" WHERE PLAN_ID = :planID");
            qc.setObject("planID", planID);
            qc.setQueryString(sb.toString());
            dam.exeUpdate(qc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 取得計劃書查詢結果
     * 
     * @param body
     * @param header
     * @throws JBranchException
     */
    private StringBuffer inquireProposal_sql(FPS250InputVO inputVO, QueryConditionIF qc) throws JBranchException{
        StringBuffer sb = new StringBuffer();
        sb.append(" WITH TMP AS (");
        sb.append("   SELECT");
        sb.append("   COUNT (*) EXEC_CNT,");
        sb.append("   TO_CHAR(MAX(FILE_LOG.CREATETIME),'YYYY/MM/DD') EXEC_TIME,");
        sb.append("   FILE_LOG.EXEC_TYPE,");
        sb.append("   FILE_LOG.SEQ_NO,");
        sb.append("   FILE_LOG.PLAN_TYPE,");
        sb.append("   FILE_LOG.PLAN_ID");
        sb.append("   FROM TBFPS_PORTFOLIO_PLAN_FILE_LOG FILE_LOG");
        sb.append("   WHERE FILE_LOG.PLAN_TYPE = 'INV'");
        sb.append("   GROUP BY FILE_LOG.PLAN_ID, FILE_LOG.PLAN_TYPE, FILE_LOG.SEQ_NO, FILE_LOG.EXEC_TYPE");
        sb.append(" )");
        sb.append(" SELECT");
        sb.append(" PLAN_FILE.PLAN_ID,");
        sb.append(" PLAN_FILE.PLAN_TYPE,");
        sb.append(" PLAN_FILE.SEQ_NO,");
        sb.append(" PLAN_FILE.CREATETIME,");
        sb.append(" HE.CUST_ID,");
        sb.append(" NVL((");
        sb.append("   SELECT TMP.EXEC_CNT FROM TMP");
        sb.append("   WHERE PLAN_FILE.PLAN_ID = TMP.PLAN_ID");
        sb.append("   AND PLAN_FILE.SEQ_NO = TMP.SEQ_NO");
        sb.append("   AND PLAN_FILE.PLAN_TYPE = TMP.PLAN_TYPE");
        sb.append("   AND TMP.EXEC_TYPE = 'E'");
        sb.append(" ),0) IS_EMAIL,");
        sb.append(" NVL((");
        sb.append("   SELECT TMP.EXEC_CNT FROM TMP");
        sb.append("   WHERE PLAN_FILE.PLAN_ID = TMP.PLAN_ID");
        sb.append("   AND PLAN_FILE.SEQ_NO = TMP.SEQ_NO");
        sb.append("   AND PLAN_FILE.PLAN_TYPE = TMP.PLAN_TYPE");
        sb.append("   AND TMP.EXEC_TYPE = 'P'");
        sb.append(" ),0) IS_PRINT");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE PLAN_FILE");
        sb.append(" LEFT JOIN TBFPS_PORTFOLIO_PLAN_INV_HEAD HE");
        sb.append(" ON HE.PLAN_ID = PLAN_FILE.PLAN_ID");
        sb.append(" WHERE PLAN_FILE.ENCRYPT = 'Y'");
        sb.append(" AND PLAN_FILE.PLAN_TYPE = 'INV'");
        sb.append(" AND PLAN_FILE.PLAN_ID = :planId");
        qc.setObject("planId", inputVO.getPlanID());
        sb.append(" ORDER BY PLAN_FILE.CREATETIME DESC");

        return sb;
    }

    @SuppressWarnings("unchecked")
    public void inquireProposal(Object body, IPrimitiveMap header) throws JBranchException{
        FPS250InputVO inputVO = (FPS250InputVO) body;
        FPS250OutputVO outputVO = new FPS250OutputVO();
        DataAccessManager dam = this.getDataAccessManager();

        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = inquireProposal_sql(inputVO, qc);
        qc.setQueryString(sb.toString());
        outputVO.setOutputList(dam.exeQuery(qc));// 回傳資料
        this.sendRtnObject(outputVO);
        // rtnPaginQuery(dam, qc, sb, outputVO, inputVO);
    }

    public List<Map<String, Object>> api_inquireProposal(FPS250InputVO inputVO) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();

        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        qc.setQueryString(inquireProposal_sql(inputVO, qc).toString());

        return dam.exeQuery(qc);
    }

    /**
     * 使用者行為紀錄
     * 
     * @param body
     * @param inputVO
     *            , execType E:email, P:print
     * @throws JBranchException
     */
    private void execLog(FPS250InputVO inputVO, String execType) throws JBranchException{
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO TBFPS_PORTFOLIO_PLAN_FILE_LOG ");
        sb.append(" (PLAN_ID, PLAN_TYPE, SEQ_NO, LOG_SEQ, EXEC_TYPE, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
        sb.append(" VALUES(:planId, 'INV', :seqNo, :logSeq, :execType, :creator, SYSDATE, :moifier, SYSDATE) ");
        qc.setQueryString(sb.toString());
        qc.setObject("planId", inputVO.getPlanID());
        qc.setObject("seqNo", inputVO.getSEQNO());
        qc.setObject("logSeq", getSeq(inputVO));
        qc.setObject("execType", execType);
        qc.setObject("creator", loginID);
        qc.setObject("moifier", loginID);
        dam.exeUpdate(qc);
    }

    /**
     * 取得該insert的log_seq
     * 
     * @param body
     * @param inputVO
     * @throws JBranchException
     */
    private BigDecimal getSeq(FPS250InputVO inputVO) throws JBranchException{
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT MAX(LOG_SEQ) LOG_SEQ FROM TBFPS_PORTFOLIO_PLAN_FILE_LOG ");
        sb.append(" WHERE PLAN_ID = :planId ");
        sb.append(" AND SEQ_NO = :seqNo ");
        sb.append(" AND PLAN_TYPE = 'INV' ");
        sb.append(" GROUP BY PLAN_ID,PLAN_TYPE,SEQ_NO ");
        qc.setQueryString(sb.toString());
        qc.setObject("planId", inputVO.getPlanID());
        qc.setObject("seqNo", inputVO.getSEQNO());
        List<Map<String, Object>> result = dam.exeQuery(qc);
        if (result.isEmpty())
            return new BigDecimal(1);
        BigDecimal num = (BigDecimal) result.get(0).get("LOG_SEQ");
        return num.add(new BigDecimal(1));
    }

    /**
     * 轉寄
     * 
     * @param body
     * @param header
     * @throws Exception
     */
    @Deprecated
    public void sendMail(Object body, IPrimitiveMap header) throws Exception{
        FPS250InputVO inputVO = (FPS250InputVO) body;
        FPS250OutputVO outputVO = new FPS250OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT FILE_NAME, PLAN_PDF_FILE ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE ");
        sb.append(" WHERE PLAN_ID = :planId 			");
        sb.append(" AND PLAN_TYPE = 'INV' 			");
        sb.append(" AND ENCRYPT = 'Y' 				");
        sb.append(" AND SEQ_NO = :seqNo 				");
        qc.setObject("planId", inputVO.getPlanID());
        qc.setObject("seqNo", inputVO.getSEQNO());
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> result = dam.exeQuery(qc);
        if (result.isEmpty()) {
            throw new APException("無可下載的檔案"); // 無可下載的檔案
        } else {
            FubonSendJavaMail send = new FubonSendJavaMail();
            FubonMail mail = new FubonMail();

            byte[] onversion = ObjectUtil.blobToByteArr((Blob) result.get(0).get("PLAN_PDF_FILE"));

            Map<String, Object> data = new HashMap<>();
            data.put((String) result.get(0).get("FILE_NAME"), onversion);

            mail.setSubject("來自富邦 計畫報告書");
            mail.setFromMail("wmsr_bank@fbt.com");
            // 設定收件者
            List<Map<String, String>> lstMailTo = new ArrayList<Map<String, String>>();
            String[] email = { getCustEmail(inputVO.getCustID()) };
            if (StringUtils.isBlank(email[0]))
                throw new APException("沒有聯絡資訊!");
            // Email格式檢查
            String message = "信件已寄出";

            String mailAddressError = "N";
            for (int i = 0; i < email.length; i++) {
                if (isEmail(email[i]) == false) {
                    message = "信箱Email格式錯誤: " + "錯誤信箱 - " + email[i];// +"，所有郵件寄件失敗。";
                    mailAddressError = "Y";
                }
            }
            // Email 格式正確則寄信
            if (mailAddressError.equals("N")) {
                Map<String, String> mailMap = new HashMap<String, String>();
                for (int i = 0; i < email.length; i++) {
                    mailMap.put(FubonSendJavaMail.MAIL, email[i]);
                    lstMailTo.add(mailMap);
                }
                mail.setLstMailTo(lstMailTo);
                mail.setContent("HIHIHI");
                send.sendMail(mail, data);
                execLog(inputVO, "E");
            }
            outputVO.setMessage(message);
            this.sendRtnObject(outputVO);
        }
    }

    // 取得該客戶的Email
    private String getCustEmail(String custId) throws DAOException, JBranchException{
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT EMAIL ");
        sb.append(" FROM TBCRM_CUST_CONTACT ");
        sb.append(" WHERE CUST_ID = :custId ");
        qc.setQueryString(sb.toString());
        qc.setObject("custId", custId);
        List<Map<String, Object>> result = dam.exeQuery(qc);
        if (result.isEmpty())
            return "";
        return (String) result.get(0).get("EMAIL");
    }

    // 信箱Email格式檢查
    private boolean isEmail(String email){
        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 取得查詢結果
     * 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void inquirePrint(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
        FPS250InputVO inputVO = (FPS250InputVO) body;
        FPS250OutputVO outputVO = new FPS250OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" SELECT HE.PLAN_STATUS, HE.VALID_FLAG, HE.INV_PLAN_NAME, ");
        sb.append(" INFO.BRANCH_NBR, ");
        sb.append(" INFO.BRANCH_NAME, ");
        sb.append(" INFO.EMP_NAME, ");
        sb.append(" HE.CREATOR, ");
        sb.append(" HE.SPP_TYPE, ");
        sb.append(" HE.LASTUPDATE, ");
        sb.append(" HE.CREATETIME, ");
        sb.append(" HE.PLAN_ID, ");
        sb.append(" HE.TRACE_FLAG, ");
        sb.append(" HE.STEP_STAUS, ");
        sb.append(" HE.RISK_ATTR ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD HE ");
        sb.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON ");
        sb.append(" INFO.EMP_ID=HE.CREATOR ");
        sb.append(" WHERE HE.CUST_ID = :custID ");
        sb.append(" AND HE.PLAN_ID = :planID ");
        sb.append(" AND HE.PLAN_TYPE = 'INV' ");
        qc.setObject("custID", inputVO.getCustID());
        qc.setObject("planID", inputVO.getPlanID());

        qc.setQueryString(sb.toString());

        outputVO.setChecker(check(inputVO, dam));

        rtnPaginQuery(dam, qc, sb, outputVO, inputVO);
    }

    /**
     * 列印
     * 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void print(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
        FPS250InputVO inputVO = (FPS250InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT A.PLAN_PDF_FILE, A.FILE_NAME, A.SEQ_NO ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE A 	");
        sb.append(" WHERE A.PLAN_ID = :planId 			");
        sb.append(" AND A.ENCRYPT = 'Y'					");
        sb.append(" AND A.PLAN_TYPE = 'INV' 			");
        sb.append(" AND A.SEQ_NO = ( 					");
        sb.append(" 	SELECT Max(B.SEQ_NO) FROM TBFPS_PORTFOLIO_PLAN_FILE B ");
        sb.append(" 	WHERE B.PLAN_ID = A.PLAN_ID 	");
        sb.append(" 	AND B.ENCRYPT = 'Y' 			");
        sb.append(" 	AND B.PLAN_TYPE = 'INV' 		");
        sb.append(" ) 									");
        qc.setObject("planId", inputVO.getPlanID());
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> result = dam.exeQuery(qc);

        if (!result.isEmpty()) {
            String filename = (String) result.get(0).get("FILE_NAME");
            String seqNo = String.valueOf((BigDecimal) result.get(0).get("SEQ_NO"));
            byte[] blobarray;
            try {
                blobarray = ObjectUtil.blobToByteArr((Blob) result.get(0).get("PLAN_PDF_FILE"));
            } catch (Exception e) {
                throw new APException("文件下載錯誤！"); // 文件下載錯誤！
            }

            String temp_path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
            String temp_path_relative = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH_RELATIVE);

            FileOutputStream download_file = null;
            try {
                download_file = new FileOutputStream(new File(temp_path, filename));
                download_file.write(blobarray);
                inputVO.setSEQNO(seqNo);
                execLog(inputVO, "P");
            } catch (Exception e) {
                throw new APException("文件下載錯誤！"); // 下載檔案失敗
            } finally {
                try {
                    download_file.close();
                } catch (Exception e) {
                    // IGNORE
                }
            }
            notifyClientToDownloadFile(temp_path_relative + "//" + filename, filename);
        } else {
            throw new APException("無可下載的檔案!"); // 無可下載的檔案
        }
    }

    /**
     * 預覽圖
     * 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void preview(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
        FPS250InputVO inputVO = (FPS250InputVO) body;
        FPS250OutputVO outputVO = new FPS250OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT PLAN_PDF_FILE, FILE_NAME ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE  	");
        sb.append(" WHERE PLAN_ID = :planId 			");
        sb.append(" AND ENCRYPT = 'N'					");
        sb.append(" AND PLAN_TYPE = 'INV' 			");
        sb.append(" AND SEQ_NO = :seqNo				");
        qc.setObject("planId", inputVO.getPlanID());
        qc.setObject("seqNo", inputVO.getSEQNO());
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> result = dam.exeQuery(qc);

        if (!result.isEmpty()) {
            String filename = (String) result.get(0).get("FILE_NAME");
            byte[] blobarray;
            try {
                blobarray = ObjectUtil.blobToByteArr((Blob) result.get(0).get("PLAN_PDF_FILE"));
                String download_file = Base64Utils.encodeToString(blobarray);
                outputVO.setBase64(download_file);
                this.sendRtnObject(outputVO);
            } catch (Exception e) {
                throw new APException("文件載入錯誤！"); // 文件下載錯誤！
            }
        } else {
            throw new APException("無可預覽的檔案!"); // 無可下載的檔案
        }
    }

    /**
     * 轉入行動書櫃
     * 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void addShelf(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
        FPS250InputVO inputVO = (FPS250InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        WorkStation ws = DataManager.getWorkStation(uuid);
        String userID = ws.getUser().getUserID();
        // TBFPS_DOC_FILEVO vo = (TBFPS_DOC_FILEVO)
        // dam.findByPKey(TBFPS_DOC_FILEVO.TABLE_UID,
        // new BigDecimal(inputVO.getSEQNO()));
        //
        // vo.setMAO_DOC_FLAG("Y");
        // vo.setModifier(userID);
        // vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
        // dam.update(vo);
        this.sendRtnObject(null);
    }

    // 檢查 for 轉入行動書櫃
    @SuppressWarnings("unchecked")
    private int check(FPS250InputVO inputVO, DataAccessManager dam) throws JBranchException, ParseException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT COUNT(*) CHECKER FROM TBSFA_MAO_APL_MAIN A ");
        sb.append(" JOIN TBSFA_MAO_APL_CLIST B ON A.APL_SEQNO = B.APL_SEQNO ");
        sb.append(" WHERE A.STATUS = 'A' AND B.CUST_ID = :custID AND TRUNC(A.USE_DATE) >= TRUNC(SYSDATE) ");
        qc.setObject("custID", inputVO.getCustID());
        qc.setQueryString(sb.toString());
        return Integer.parseInt(((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CHECKER").toString());
    }

    // 執行查詢分頁回傳
    private void rtnPaginQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS250OutputVO outputVO,
            FPS250InputVO inputVO) throws DAOException, JBranchException{
        qc.setQueryString(sb.toString());
        ResultIF pageList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
        outputVO.setOutputList(pageList);// 回傳資料
        outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
        outputVO.setTotalPage(pageList.getTotalPage());// 總頁次
        outputVO.setTotalRecord(pageList.getTotalRecord());// 總筆數
        this.sendRtnObject(outputVO);
    }

    // 日期->字串
    private String dateQueryStr(String title, QueryConditionIF qc, Date sd, Date ed){
        String str = " AND ";
        str += "TO_CHAR(" + title + ",'YYYYMMDD') ";
        if (sd != null && ed != null) {
            str += " BETWEEN TO_CHAR(:SD,'YYYYMMDD') AND TO_CHAR(:ED,'YYYYMMDD') ";
            qc.setObject("SD", sd);
            qc.setObject("ED", ed);
        } else if (sd != null) {
            str += " >= TO_CHAR(:SD,'YYYYMMDD') ";
            qc.setObject("SD", sd);
        } else if (ed != null) {
            str += " <= TO_CHAR(:ED,'YYYYMMDD') ";
            qc.setObject("ED", ed);
        } else {
            str = "";
        }
        return str;
    }

}
