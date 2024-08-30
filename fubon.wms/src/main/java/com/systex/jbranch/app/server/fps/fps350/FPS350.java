package com.systex.jbranch.app.server.fps.fps350;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
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
import com.systex.jbranch.platform.util.IPrimitiveMap;
//import com.systex.jbranch.app.common.sfa.table.TBFPS_DOC_FILEVO;

/**
 * 特定目的投資組合歷史規劃
 * @author Johnson
 * @since 18-01-31
 */
@Component("fps350")
@Scope("request")
public class FPS350 extends FubonWmsBizLogic {

    SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");
    private Logger logger = LoggerFactory.getLogger(FPS350.class);

    /**
     * 取得查詢結果
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    private StringBuffer inquire_sql(FPS350InputVO inputVO, QueryConditionIF qc) throws Exception {
    	// old code
    	StringBuffer sb = new StringBuffer();
    	sb.append(" WITH");
        sb.append(" LOG AS (");
        sb.append("   SELECT COUNT(*) EXEC_CNT,");
        sb.append("   TO_CHAR(MAX(FILE_LOG.CREATETIME), 'YYYY/MM/DD') EXEC_TIME,");
        sb.append("   FILE_LOG.EXEC_TYPE,");
        sb.append("   FILE_LOG.PLAN_TYPE,");
        sb.append("   FILE_LOG.PLAN_ID");
        sb.append("   FROM TBFPS_PORTFOLIO_PLAN_FILE_LOG FILE_LOG");
        sb.append("   WHERE FILE_LOG.PLAN_TYPE = 'SPP'");
        sb.append("   GROUP BY FILE_LOG.PLAN_ID, FILE_LOG.PLAN_TYPE, FILE_LOG.EXEC_TYPE)");
        sb.append("SELECT * FROM (");
        sb.append(" SELECT  ");
        sb.append(" CASE WHEN HE.PLAN_STATUS = 'ACTIVE' THEN 'ACTIVE' ");
        sb.append(" 	 WHEN HE.PLAN_STATUS = 'PLAN_STEP' THEN 'PLAN_STEP' ");
        sb.append(" 	 WHEN HE.PLAN_STATUS = 'PRINT_REJECT' THEN 'PRINT_REJECT' ");
        sb.append(" 	 ELSE CASE WHEN (SELECT COUNT(*) FROM (SELECT (PURCHASE_ORG_AMT_ORDER - PURCHASE_ORG_AMT) AS CHECK_AMT FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID = HE.PLAN_ID) WHERE CHECK_AMT >= 0) > 0 THEN 'PRINT_ORDER' ELSE 'PRINT_THINK' END END");
        sb.append(" AS PLAN_STATUS,");
        sb.append(" HE.INV_PLAN_NAME, ");
        sb.append(" NVL(HE.VALID_FLAG, 'Y') AS VALID_FLAG, ");
        sb.append(" INFO.BRANCH_NBR, ");
        sb.append(" INFO.BRANCH_NAME, ");
        sb.append(" INFO.EMP_NAME, ");
        sb.append(" HE.CREATOR, ");
        sb.append(" HE.SPP_TYPE, ");
        sb.append(" HE.LASTUPDATE, ");
        sb.append(" HE.CREATETIME, ");
        sb.append(" HE.PLAN_ID, ");
        sb.append(" HE.RISK_ATTR, ");
        sb.append(" HE.VOL_RISK_ATTR, ");
        sb.append(" HE.STEP_STAUS, ");
        sb.append(" HE.CUST_ID, ");
        sb.append(" CASE ");
        sb.append(" 	WHEN TMP.COUNTSPP > 0 THEN '有，' || TMP.COUNTSPP || '筆' ");
        sb.append(" 	ELSE '無' ");
        sb.append(" END COUNTSPP, TMP.COUNTSPP as TRADECOUNT, ");
        sb.append("   NVL((");
        sb.append("     SELECT LOG.EXEC_CNT");
        sb.append("     FROM LOG");
        sb.append("     WHERE HE.PLAN_ID = LOG.PLAN_ID");
        sb.append("     AND LOG.EXEC_TYPE = 'E'), 0) IS_EMAIL,");
        sb.append("   NVL((");
        sb.append("     SELECT LOG.EXEC_CNT");
        sb.append("     FROM LOG");
        sb.append("     WHERE HE.PLAN_ID = LOG.PLAN_ID");
        sb.append("     AND LOG.EXEC_TYPE = 'P'), 0) IS_PRINT");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE ");
        sb.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON ");
        sb.append(" INFO.EMP_ID = HE.CREATOR ");
        sb.append(" LEFT JOIN ( ");
        sb.append(" SELECT COUNT(*) COUNTSPP, PLAN_ID FROM ( ");
        sb.append(" SELECT HE.PLAN_ID, HE.CUST_ID, DE.PRD_ID ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE ");
        sb.append(" LEFT JOIN TBFPS_PORTFOLIO_PLAN_SPP DE ON HE.PLAN_ID = DE.PLAN_ID ");
        sb.append(" INNER JOIN ( ");
        sb.append(" SELECT COUNT(*) COUNTINV, PLAN_ID FROM TBFPS_PORTFOLIO_PLAN_SPP ");
        sb.append(" WHERE NVL(PURCHASE_TWD_AMT_ORDER,0) > 0 ");
        sb.append(" AND (NVL(PURCHASE_TWD_AMT,0) - NVL(PURCHASE_TWD_AMT_ORDER,0) = 0) ");
        sb.append(" GROUP BY PLAN_ID) TMP ");
        sb.append(" ON TMP.PLAN_ID = HE.PLAN_ID ");
        sb.append(" WHERE HE.CUST_ID = :custID ");
        sb.append(" AND SUBSTR(NVL(HE.VALID_FLAG, 'Y'), 0, 1) <> 'N' ");
        sb.append(" AND (NVL(PURCHASE_TWD_AMT,0) - NVL(PURCHASE_TWD_AMT_ORDER,0) > 0) ");
        sb.append(" AND TRUNC(DE.LASTUPDATE)  + 14 - TRUNC(SYSDATE) > 0) ");
        sb.append(" GROUP BY PLAN_ID ");
//        sb.append("     SELECT COUNT(*) COUNTSPP, PLAN_ID FROM TBFPS_PORTFOLIO_PLAN_SPP ");
//        sb.append(" 	WHERE ORDER_STATUS = null ");
//        sb.append(" 	GROUP BY PLAN_ID ");
        sb.append(" ) TMP ON TMP.PLAN_ID = HE.PLAN_ID ");
        sb.append(" WHERE HE.CUST_ID = :custID ");
        
        qc.setObject("custID", inputVO.getCustID());
        if (StringUtils.isNotBlank(inputVO.getPlanStatus())) {
            sb.append(" AND HE.PLAN_STATUS = :status ");
            qc.setObject("status", inputVO.getPlanStatus());
        }
        
        if ("N".equals(inputVO.getIsDisable())) {
            sb.append(" AND substr(HE.VALID_FLAG, 0, 1) = :isDisable ");
            qc.setObject("isDisable", inputVO.getIsDisable());
        } else if ("Y".equals(inputVO.getIsDisable())) {
        	sb.append(" AND (substr(HE.VALID_FLAG, 0, 1) = :isDisable or HE.VALID_FLAG is null) ");
            qc.setObject("isDisable", inputVO.getIsDisable());
        }
        sb.append(dateQueryStr("HE.LASTUPDATE", qc, inputVO.getSD(), inputVO.getED()));
        sb.append(") ");
        sb.append("ORDER BY ");
        sb.append("DECODE(PLAN_STATUS, 'ACTIVE', 1), ");
        sb.append("DECODE(VALID_FLAG,'Y', 1,'N', 2), ");
        sb.append("TRUNC(LASTUPDATE) DESC, ");
        sb.append("DECODE(PLAN_STATUS, 'PRINT_ORDER', 1, 'PRINT_THINK', 2, 'PLAN_STEP', 3, 'PRINT_REJECT', 4) ");
        System.out.println(sb.toString());
        
        return sb;
    }
    public void inquire(Object body, IPrimitiveMap header) throws Exception {
        FPS350InputVO inputVO = (FPS350InputVO) body;
        FPS350OutputVO outputVO = new FPS350OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = inquire_sql(inputVO, qc);
        rtnQuery(dam, qc, sb, outputVO);
        
        //rtnPaginQuery(dam, qc, sb, outputVO, inputVO);
    }
    public List<Map<String, Object>> api_inquire(FPS350InputVO inputVO) throws Exception {
    	DataAccessManager dam = this.getDataAccessManager();
    	
    	QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        qc.setQueryString(inquire_sql(inputVO, qc).toString());
        
        return dam.exeQuery(qc);
    }
    
    /**
     * 取得計劃書查詢結果
     * @param body
     * @param header
     * @throws JBranchException
     */
    private StringBuffer inquireProposal_sql(FPS350InputVO inputVO, QueryConditionIF qc) throws Exception {
    	// old code
    	StringBuffer sb = new StringBuffer();
    	sb.append(" WITH TMP AS ( ");
        sb.append("   SELECT ");
        sb.append("   COUNT (*) EXEC_CNT,");
        sb.append("   TO_CHAR(MAX(FILE_LOG.CREATETIME),'YYYY/MM/DD') EXEC_TIME, ");
        sb.append("   FILE_LOG.EXEC_TYPE, ");
        sb.append("   FILE_LOG.SEQ_NO, ");
        sb.append("   FILE_LOG.PLAN_TYPE, ");
        sb.append("   FILE_LOG.PLAN_ID ");
        sb.append("   FROM　TBFPS_PORTFOLIO_PLAN_FILE_LOG　FILE_LOG ");
        sb.append("   WHERE FILE_LOG.PLAN_TYPE = 'SPP' ");
        sb.append("   GROUP BY FILE_LOG.PLAN_ID, FILE_LOG.PLAN_TYPE, FILE_LOG.SEQ_NO, FILE_LOG.EXEC_TYPE ");
        sb.append(" ) ");
        sb.append(" SELECT ");
        sb.append(" PLAN_FILE.PLAN_ID, ");
        sb.append(" PLAN_FILE.PLAN_TYPE, ");
        sb.append(" PLAN_FILE.SEQ_NO, ");
        sb.append(" PLAN_FILE.CREATETIME BUILDTIME, ");
        sb.append(" HE.CUST_ID, ");
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
        sb.append(" FROM　TBFPS_PORTFOLIO_PLAN_FILE PLAN_FILE ");
        sb.append(" LEFT JOIN TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE ");
        sb.append(" ON HE.PLAN_ID = PLAN_FILE.PLAN_ID ");
        sb.append(" WHERE PLAN_FILE.ENCRYPT = 'Y' ");
        sb.append(" AND PLAN_FILE.PLAN_TYPE = 'SPP' ");
        sb.append(" AND PLAN_FILE.PLAN_ID = :planId ");
        qc.setObject("planId", inputVO.getPlanID());
        sb.append(" ORDER BY PLAN_FILE.CREATETIME DESC");
        
        return sb;
    }
    public void inquireProposal(Object body, IPrimitiveMap header) throws Exception {
        FPS350InputVO inputVO = (FPS350InputVO) body;
        FPS350OutputVO outputVO = new FPS350OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = inquireProposal_sql(inputVO, qc);
        rtnQuery(dam, qc, sb, outputVO);
        //rtnPaginQuery(dam, qc, sb, outputVO, inputVO);
    }
    public List<Map<String, Object>> api_inquireProposal(FPS350InputVO inputVO) throws Exception {
    	DataAccessManager dam = this.getDataAccessManager();
    	
    	QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        qc.setQueryString(inquireProposal_sql(inputVO, qc).toString());
        
        return dam.exeQuery(qc);
    }
    
    /**
     * 刪除
     * @param body
     * @param header
     * @throws DAOException 
     * @throws JBranchException
     */
    public void deletePlan(Object body, IPrimitiveMap header) throws JBranchException {
      FPS350InputVO inputVO = (FPS350InputVO) body;
      String planID = inputVO.getPlanID();
      DataAccessManager dam = this.getDataAccessManager();
      QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
      StringBuffer sb = new StringBuffer();
      sb.append(" DELETE FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD ");
      sb.append(" WHERE PLAN_ID = :planID ");
      qc.setObject("planID", planID);
      qc.setQueryString(sb.toString());
      dam.exeUpdate(qc);
      sb = new StringBuffer();
      sb.append(" DELETE FROM TBFPS_PORTFOLIO_PLAN_SPP ");
      sb.append(" WHERE PLAN_ID = :planID ");
      qc.setObject("planID", planID);
      qc.setQueryString(sb.toString());
      dam.exeUpdate(qc);
      
      this.sendRtnObject(true);
    }
    
    /**
     * 使用者行為紀錄
     * @param body
     * @param inputVO, execType E:email, P:print
     * @throws JBranchException
     */
    private void execLog(FPS350InputVO inputVO, String execType) throws JBranchException {
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO TBFPS_PORTFOLIO_PLAN_FILE_LOG ");
        sb.append(" (PLAN_ID, PLAN_TYPE, SEQ_NO, LOG_SEQ, EXEC_TYPE, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
        sb.append(" VALUES(:planId, 'SPP', :seqNo, :logSeq, :execType, :creator, SYSDATE, :moifier, SYSDATE) ");
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
     * @param body
     * @param inputVO
     * @throws JBranchException
     */
    private BigDecimal getSeq(FPS350InputVO inputVO) throws JBranchException {
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT MAX(LOG_SEQ) LOG_SEQ FROM TBFPS_PORTFOLIO_PLAN_FILE_LOG ");
        sb.append(" WHERE PLAN_ID = :planId ");
        sb.append(" AND SEQ_NO = :seqNo ");
        sb.append(" AND PLAN_TYPE = 'SPP' ");
        sb.append(" GROUP BY PLAN_ID,PLAN_TYPE,SEQ_NO ");
        qc.setQueryString(sb.toString());
        qc.setObject("planId", inputVO.getPlanID());
        qc.setObject("seqNo", inputVO.getSEQNO());
        List<Map<String, Object>> result = dam.exeQuery(qc);
        if(result.isEmpty()) return new BigDecimal(1);
        BigDecimal num = (BigDecimal)result.get(0).get("LOG_SEQ");
        return num.add(new BigDecimal(1));
    }
    /**
     * 轉寄
     * @param body
     * @param header
     * @throws Exception 
     */
    public void sendMail(Object body, IPrimitiveMap header) throws Exception {
    	FPS350InputVO inputVO = (FPS350InputVO) body;
        FPS350OutputVO outputVO = new FPS350OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT FILE_NAME, PLAN_PDF_FILE ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE ");
        sb.append(" WHERE PLAN_ID = :planId 			");
        sb.append(" AND PLAN_TYPE = 'SPP' 			");
        sb.append(" AND ENCRYPT = 'Y' 				");
        sb.append(" AND SEQ_NO = :seqNo 				");
        qc.setObject("planId", inputVO.getPlanID());
        qc.setObject("seqNo", inputVO.getSEQNO());
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> result = dam.exeQuery(qc);
        
        // 特定目的
        boolean isSuccess = FPSUtils.sendMain(dam, result, getCustEmail(inputVO.getCustID()), FPSUtils.getMailContent(1));
  	  
  	  	if(isSuccess) {
  	  		execLog(inputVO, "E");
  	  	} else {
  	  		throw new APException("E-Mail 發送失敗!");
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
        List<Map<String,Object>> result = dam.exeQuery(qc);
        if(result.isEmpty()) return "";
    	return (String)result.get(0).get("EMAIL");
    }
    
    //信箱Email格式檢查
  	private boolean isEmail(String email) {
  		Pattern emailPattern = Pattern
  				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
  		Matcher matcher = emailPattern.matcher(email);
  		if (matcher.find()) {
  			return true;
  		}
  		return false;
  	}
    
    /**
     * 取得查詢結果 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void inquirePrint(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
        FPS350InputVO inputVO = (FPS350InputVO) body;
        FPS350OutputVO outputVO = new FPS350OutputVO();
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
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE ");
        sb.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON ");
        sb.append(" INFO.EMP_ID=HE.CREATOR ");
        sb.append(" WHERE HE.CUST_ID = :custID ");
        sb.append(" AND HE.PLAN_ID = :planID ");
        sb.append(" AND HE.PLAN_TYPE = 'SPP' ");
        qc.setObject("custID", inputVO.getCustID());
        qc.setObject("planID", inputVO.getPlanID());

        qc.setQueryString(sb.toString());

        outputVO.setChecker(check(inputVO, dam));

        rtnPaginQuery(dam, qc, sb, outputVO, inputVO);
    }

    /**
     * 列印
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void print(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
        FPS350InputVO inputVO = (FPS350InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT A.PLAN_PDF_FILE, A.FILE_NAME, A.SEQ_NO ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE A 	");
        sb.append(" WHERE A.PLAN_ID = :planId 			");
        sb.append(" AND A.ENCRYPT = 'Y'					");
        sb.append(" AND A.PLAN_TYPE = 'SPP' 			");
        sb.append(" AND A.SEQ_NO = ( 					");
        sb.append(" 	SELECT Max(B.SEQ_NO) FROM TBFPS_PORTFOLIO_PLAN_FILE B ");
        sb.append(" 	WHERE B.PLAN_ID = A.PLAN_ID 	");
        sb.append(" 	AND B.ENCRYPT = 'Y' 			");
        sb.append(" 	AND B.PLAN_TYPE = 'SPP' 		");
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
                    //IGNORE
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date now = new Date();
            notifyClientToDownloadFile(temp_path_relative + "//" + filename, sdf.format(now) + "特定目的理財規劃");
        } else {
            throw new APException("無可下載的檔案!"); // 無可下載的檔案
        }
    }
    
    /**
     * 預覽圖
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void preview(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
        FPS350InputVO inputVO = (FPS350InputVO) body;
        FPS350OutputVO outputVO = new FPS350OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT PLAN_PDF_FILE, FILE_NAME ");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE  	");
        sb.append(" WHERE PLAN_ID = :planId 			");
        sb.append(" AND ENCRYPT = 'N'					");
        sb.append(" AND PLAN_TYPE = 'SPP' 			");
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
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     */
    public void addShelf(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
        FPS350InputVO inputVO = (FPS350InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        WorkStation ws = DataManager.getWorkStation(uuid);
        String userID = ws.getUser().getUserID();
//        TBFPS_DOC_FILEVO vo = (TBFPS_DOC_FILEVO) dam.findByPKey(TBFPS_DOC_FILEVO.TABLE_UID,
//                new BigDecimal(inputVO.getSEQNO()));
//
//        vo.setMAO_DOC_FLAG("Y");
//        vo.setModifier(userID);
//        vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
//        dam.update(vo);
        this.sendRtnObject(null);
    }

    // 檢查 for 轉入行動書櫃
    @SuppressWarnings("unchecked")
    private int check(FPS350InputVO inputVO, DataAccessManager dam) throws JBranchException, ParseException {
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT COUNT(*) CHECKER FROM TBSFA_MAO_APL_MAIN A ");
        sb.append(" JOIN TBSFA_MAO_APL_CLIST B ON A.APL_SEQNO = B.APL_SEQNO ");
        sb.append(" WHERE A.STATUS = 'A' AND B.CUST_ID = :custID AND TRUNC(A.USE_DATE) >= TRUNC(SYSDATE) ");
        qc.setObject("custID", inputVO.getCustID());
        qc.setQueryString(sb.toString());
        return Integer.parseInt(((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CHECKER").toString());
    }

    // 執行查詢回傳
    private void rtnQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS350OutputVO outputVO)
            throws DAOException, JBranchException {
        qc.setQueryString(sb.toString());
        outputVO.setOutputList(dam.exeQuery(qc));// 回傳資料
        this.sendRtnObject(outputVO);
    }

    // 執行查詢分頁回傳
    private void rtnPaginQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS350OutputVO outputVO,
            FPS350InputVO inputVO) throws DAOException, JBranchException {
        qc.setQueryString(sb.toString());
        ResultIF pageList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
        outputVO.setOutputList(pageList);// 回傳資料
        outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
        outputVO.setTotalPage(pageList.getTotalPage());// 總頁次
        outputVO.setTotalRecord(pageList.getTotalRecord());// 總筆數
        this.sendRtnObject(outputVO);
    }

    // 日期->字串
    private String dateQueryStr(String title, QueryConditionIF qc, Date sd, Date ed) {
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