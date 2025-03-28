package com.systex.jbranch.app.server.fps.pms408;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Description :短期多次承作KYC日報
 * Author : 2016/05/17 Frank
 * Editor : 2017/01/30 Kevin
 */

@Component("pms408")
@Scope("prototype")
public class PMS408 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	
	public String getSQL (String type, Boolean condition) {

		StringBuffer sql = new StringBuffer();
		
		switch (type) {
			case "BASE" :
				sql.append("  SELECT COMPARE.DATA_DATE, ");
				sql.append("         COMPARE.CUST_ID, ");
				sql.append("         COMPARE.TEST_SEQ_BEF, ");
				sql.append("         COMPARE.QST_NO_BEF, ");
				sql.append("         COMPARE.QUESTION_DESC_BEF, ");
				sql.append("         REPLACE(COMPARE.ANSWER_BEF, '|', ',') AS ANSWER_BEF, ");
				sql.append("         COMPARE.SIDE_BEF, ");
				sql.append("         COMPARE.TEST_SEQ_AFR, ");
				sql.append("         COMPARE.QST_NO_AFR, ");
				sql.append("         CASE WHEN COMPARE.QUESTION_DESC_AFR = QUESTION_DESC_BEF THEN '同前次題目' ELSE QUESTION_DESC_AFR END AS QUESTION_DESC_AFR, ");
				sql.append("         REPLACE(COMPARE.ANSWER_AFR, '|', ',') AS ANSWER_AFR, ");
				sql.append("         COMPARE.SIDE_AFR ");
				sql.append("  FROM TBKYC_INVESTOREXAM_M_COMPARE COMPARE ");
				sql.append("  WHERE 1 = 1 ");
				
				if (condition) {
					sql.append("  AND DATA_DATE = :dataDate ");
					sql.append("  AND CUST_ID = :custID ");
					sql.append("  AND DATA_TYPE = :dataType ");
				}
				
				break;
			case "QA":
				sql.append("  SELECT DISTINCT EXAM.EXAM_VERSION, ");
				sql.append("         EXAM.QUESTION_VERSION, ");
				sql.append("         EXAM.QST_NO, ");
				sql.append("         QM.QUESTION_DESC, ");
				sql.append("         QD.ANSWER_SEQ, ");
				sql.append("         QD.ANSWER_DESC ");
				sql.append("  FROM TBSYS_QUESTIONNAIRE EXAM ");
				sql.append("  LEFT JOIN TBSYS_QST_QUESTION QM ON EXAM.QUESTION_VERSION = QM.QUESTION_VERSION ");
				sql.append("  LEFT JOIN TBSYS_QST_ANSWER QD ON QM.QUESTION_VERSION = QD.QUESTION_VERSION ");
				
				break;
			case "BEF":
				sql.append("  SELECT BEF.TEST_SEQ_BEF, ");
				sql.append("         I.EXAMID AS EXAM_VERSION, ");
				sql.append("         I.QID AS QUESTION_VERSION, ");
				sql.append("         BEF.QST_NO_BEF AS QST_NO, ");
				sql.append("         QA.QUESTION_DESC, ");
				sql.append("         LISTAGG(BEF.ANS_LIST, '|') WITHIN GROUP (ORDER BY BEF.QST_NO_BEF, BEF.ANS_LIST) AS ANSWER_BEF, ");
				sql.append("         LISTAGG(QA.ANSWER_DESC, '|') WITHIN GROUP (ORDER BY BEF.QST_NO_BEF, BEF.ANS_LIST) AS ANSWER_DESC_BEF ");
				sql.append("  FROM ( ");
				sql.append("    SELECT DISTINCT TEST_SEQ_BEF, QST_NO_BEF, ANS_LIST ");
				sql.append("    FROM ( ");
				sql.append("      SELECT TEST_SEQ_BEF, QST_NO_BEF, REGEXP_SUBSTR(PAR.ANSWER_BEF, '[^,]+', 1, TEMP_T.LEV) ANS_LIST ");
				sql.append("      FROM PAR ");
				sql.append("      OUTER APPLY ( ");
				sql.append("        SELECT LEVEL AS LEV ");
				sql.append("        FROM DUAL ");
				sql.append("        CONNECT BY LEVEL <= REGEXP_COUNT(PAR.ANSWER_BEF, ',') + 1 ");
				sql.append("      ) TEMP_T ");
				sql.append("    ) ");
				sql.append("  ) BEF ");
				sql.append("  INNER JOIN PEOPSOFT.PS_FP_INVESTOREXAMRECORD I ON BEF.TEST_SEQ_BEF = I.PROFILE_TEST_ID AND BEF.QST_NO_BEF = I.QSEQUENCE ");
				sql.append("  INNER JOIN QA ON I.EXAMID = QA.EXAM_VERSION AND I.QID = QA.QUESTION_VERSION AND I.QSEQUENCE = QA.QST_NO AND BEF.ANS_LIST = ANSWER_SEQ ");
				sql.append("  GROUP BY BEF.TEST_SEQ_BEF, I.EXAMID, I.QID, BEF.QST_NO_BEF, QA.QUESTION_DESC ");
				sql.append("  UNION ALL ");
				sql.append("  SELECT BEF.TEST_SEQ_BEF, ");
				sql.append("         I.EXAMID AS EXAM_VERSION, ");
				sql.append("         I.QID AS QUESTION_VERSION, ");
				sql.append("         BEF.QST_NO_BEF AS QST_NO, ");
				sql.append("         QA.QUESTION_DESC, ");
				sql.append("         LISTAGG(BEF.ANS_LIST, '|') WITHIN GROUP (ORDER BY BEF.QST_NO_BEF, BEF.ANS_LIST) AS ANSWER_BEF, ");
				sql.append("         LISTAGG(QA.ANSWER_DESC, '|') WITHIN GROUP (ORDER BY BEF.QST_NO_BEF, BEF.ANS_LIST) AS ANSWER_DESC_BEF ");
				sql.append("  FROM ( ");
				sql.append("    SELECT DISTINCT TEST_SEQ_BEF, QST_NO_BEF, ANS_LIST ");
				sql.append("    FROM ( ");
				sql.append("      SELECT TEST_SEQ_BEF, QST_NO_BEF, REGEXP_SUBSTR(PAR.ANSWER_BEF, '[^,]+', 1, TEMP_T.LEV) ANS_LIST ");
				sql.append("      FROM PAR ");
				sql.append("      OUTER APPLY ( ");
				sql.append("        SELECT LEVEL AS LEV ");
				sql.append("        FROM DUAL ");
				sql.append("        CONNECT BY LEVEL <= REGEXP_COUNT(PAR.ANSWER_BEF, ',') + 1 ");
				sql.append("      ) TEMP_T ");
				sql.append("    ) ");
				sql.append("  ) BEF ");
				sql.append("  INNER JOIN PEOPSOFT.PS_FP_INVESTOREXAMRECORD I ON BEF.TEST_SEQ_BEF = I.PROFILE_TEST_ID AND BEF.QST_NO_BEF = I.QSEQUENCE ");
				sql.append("  INNER JOIN QA ON 'KYC10000101' || I.EXAMID = QA.EXAM_VERSION AND '20170826' || I.QID = QA.QUESTION_VERSION AND I.QSEQUENCE = QA.QST_NO AND BEF.ANS_LIST = ANSWER_SEQ ");
				sql.append("  GROUP BY BEF.TEST_SEQ_BEF, I.EXAMID, I.QID, BEF.QST_NO_BEF, QA.QUESTION_DESC ");
				
				break;
			case "AFR":
				sql.append("  SELECT AFR.TEST_SEQ_AFR, ");
				sql.append("         I.EXAMID AS EXAM_VERSION, ");
				sql.append("         I.QID AS QUESTION_VERSION, ");
				sql.append("         AFR.QST_NO_AFR AS QST_NO, ");
				sql.append("         QA.QUESTION_DESC, ");
				sql.append("         LISTAGG(AFR.ANS_LIST, '|') WITHIN GROUP (ORDER BY AFR.QST_NO_AFR, AFR.ANS_LIST) AS ANSWER_AFR, ");
				sql.append("         LISTAGG(QA.ANSWER_DESC, '|') WITHIN GROUP (ORDER BY AFR.QST_NO_AFR, AFR.ANS_LIST) AS ANSWER_DESC_AFR ");
				sql.append("  FROM ( ");
				sql.append("    SELECT DISTINCT TEST_SEQ_AFR, QST_NO_AFR, ANS_LIST ");
				sql.append("    FROM ( ");
				sql.append("      SELECT TEST_SEQ_AFR, QST_NO_AFR, REGEXP_SUBSTR(PAR.ANSWER_AFR, '[^,]+', 1, TEMP_T.LEV) ANS_LIST ");
				sql.append("      FROM PAR ");
				sql.append("      OUTER APPLY ( ");
				sql.append("        SELECT LEVEL AS LEV ");
				sql.append("        FROM DUAL ");
				sql.append("        CONNECT BY LEVEL <= REGEXP_COUNT(PAR.ANSWER_AFR, ',') + 1 ");
				sql.append("      ) TEMP_T ");
				sql.append("    ) ");
				sql.append("  ) AFR ");
				sql.append("  INNER JOIN PEOPSOFT.PS_FP_INVESTOREXAMRECORD I ON AFR.TEST_SEQ_AFR = I.PROFILE_TEST_ID AND AFR.QST_NO_AFR = I.QSEQUENCE ");
				sql.append("  INNER JOIN QA ON I.EXAMID = QA.EXAM_VERSION AND I.QID = QA.QUESTION_VERSION AND I.QSEQUENCE = QA.QST_NO AND AFR.ANS_LIST = ANSWER_SEQ ");
				sql.append("  GROUP BY AFR.TEST_SEQ_AFR, I.EXAMID, I.QID, AFR.QST_NO_AFR, QA.QUESTION_DESC ");
				sql.append("  UNION ALL ");
				sql.append("  SELECT AFR.TEST_SEQ_AFR, ");
				sql.append("         I.EXAMID AS EXAM_VERSION, ");
				sql.append("         I.QID AS QUESTION_VERSION, ");
				sql.append("         AFR.QST_NO_AFR AS QST_NO, ");
				sql.append("         QA.QUESTION_DESC, ");
				sql.append("         LISTAGG(AFR.ANS_LIST, '|') WITHIN GROUP (ORDER BY AFR.QST_NO_AFR, AFR.ANS_LIST) AS ANSWER_AFR, ");
				sql.append("         LISTAGG(QA.ANSWER_DESC, '|') WITHIN GROUP (ORDER BY AFR.QST_NO_AFR, AFR.ANS_LIST) AS ANSWER_DESC_AFR ");
				sql.append("  FROM ( ");
				sql.append("    SELECT DISTINCT TEST_SEQ_AFR, QST_NO_AFR, ANS_LIST ");
				sql.append("    FROM ( ");
				sql.append("      SELECT TEST_SEQ_AFR, QST_NO_AFR, REGEXP_SUBSTR(PAR.ANSWER_AFR, '[^,]+', 1, TEMP_T.LEV) ANS_LIST ");
				sql.append("      FROM PAR ");
				sql.append("      OUTER APPLY ( ");
				sql.append("        SELECT LEVEL AS LEV ");
				sql.append("        FROM DUAL ");
				sql.append("        CONNECT BY LEVEL <= REGEXP_COUNT(PAR.ANSWER_AFR, ',') + 1 ");
				sql.append("      ) TEMP_T ");
				sql.append("    ) ");
				sql.append("  ) AFR ");
				sql.append("  INNER JOIN PEOPSOFT.PS_FP_INVESTOREXAMRECORD I ON AFR.TEST_SEQ_AFR = I.PROFILE_TEST_ID AND AFR.QST_NO_AFR = I.QSEQUENCE ");
				sql.append("  INNER JOIN QA ON 'KYC10000101' || I.EXAMID = QA.EXAM_VERSION AND '20170826' || I.QID = QA.QUESTION_VERSION AND I.QSEQUENCE = QA.QST_NO AND AFR.ANS_LIST = ANSWER_SEQ ");
				sql.append("  GROUP BY AFR.TEST_SEQ_AFR, I.EXAMID, I.QID, AFR.QST_NO_AFR, QA.QUESTION_DESC ");
				
				break;
			case "FINAL":
				sql.append("SELECT PAR.DATA_DATE, ");
				sql.append("       PAR.CUST_ID, ");
				sql.append("       PAR.TEST_SEQ_BEF, ");
				sql.append("       PAR.QST_NO_BEF, ");
				sql.append("       PAR.QUESTION_DESC_BEF, ");
				sql.append("       REPLACE(PAR.ANSWER_BEF, ',', '|') AS ANSWER_BEF, ");
				sql.append("       BEF.ANSWER_DESC_BEF, ");
				sql.append("       PAR.SIDE_BEF, ");
				sql.append("       PAR.TEST_SEQ_AFR, ");
				sql.append("       PAR.QST_NO_AFR, ");
				sql.append("       PAR.QUESTION_DESC_AFR, ");
				sql.append("       REPLACE(PAR.ANSWER_AFR, ',', '|') AS ANSWER_AFR, ");
				sql.append("       AFR.ANSWER_DESC_AFR, ");
				sql.append("       PAR.SIDE_AFR ");
				sql.append("FROM PAR ");
				sql.append("LEFT JOIN BEF ON PAR.TEST_SEQ_BEF = BEF.TEST_SEQ_BEF AND PAR.QST_NO_BEF = BEF.QST_NO ");
				sql.append("LEFT JOIN AFR ON PAR.TEST_SEQ_AFR = AFR.TEST_SEQ_AFR AND PAR.QST_NO_AFR = AFR.QST_NO ");
				sql.append("ORDER BY TO_NUMBER(PAR.QST_NO_BEF) ");
				
				break;
		}
		
		return sql.toString();
	}
	
	public void queryCompare(Object body, IPrimitiveMap header) throws Exception {
		
		PMS408InputVO inputVO = (PMS408InputVO) body;
		PMS408OutputVO outputVO = new PMS408OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("WITH PAR AS ( ");
		sb.append(getSQL("BASE", true));
		sb.append(") ");
		sb.append(", QA AS ( ");
		sb.append(getSQL("QA", true));
		sb.append(") ");
		sb.append(", BEF AS ( ");
		sb.append(getSQL("BEF", true));
		sb.append(") ");
		sb.append(", AFR AS ( ");
		sb.append(getSQL("AFR", true));
		sb.append(") ");
		
		sb.append(getSQL("FINAL", true));
		
		queryCondition.setObject("dataDate", inputVO.getCompareDataDate());
		queryCondition.setObject("custID", inputVO.getCompareCustID());
		queryCondition.setObject("dataType", inputVO.getCompareDataType());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setCompareDtlList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}

	public void queryData(Object body, IPrimitiveMap header) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("WITH PAR AS ( ");
		sb.append(getSQL("BASE", true));
		sb.append(") ");
		sb.append(", QA AS ( ");
		sb.append(getSQL("QA", true));
		sb.append(") ");
		sb.append(", BEF AS ( ");
		sb.append(getSQL("BEF", true));
		sb.append(") ");
		sb.append(", AFR AS ( ");
		sb.append(getSQL("AFR", true));
		sb.append(") ");
		
		sb.append(getSQL("FINAL", true));
		
		PMS408OutputVO outputVO = new PMS408OutputVO();
		outputVO = this.queryData(body);

		if (CollectionUtils.isNotEmpty(outputVO.getResultList())) {
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/** 查詢資料 CRM181呼叫 **/
	public PMS408OutputVO queryData(Object body) throws Exception {
		
		initUUID();

		PMS408InputVO inputVO = (PMS408InputVO) body;
		PMS408OutputVO outputVO = new PMS408OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		
		String roleID = "";
		if (StringUtils.isNotEmpty(inputVO.getRoleID()))
			roleID = inputVO.getRoleID();
		else
			roleID = loginRoleID;
		
		this.queryData(condition, inputVO, "query");
		
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());



		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); 				// 總行人員
		Map<String, String> specialHeadMgrMap = xmlInfo.doGetVariable("FUBONSYS.SPECIAL_HEADMGR_ROLE", FormatHelper.FORMAT_2);  //內控管理科人員/財管績效管理科人員 
		
		// 判斷是否為總行人員
		if (headmgrMap.containsKey(roleID)) {
			outputVO.setIsHeadMgr("Y");
		} else {
			outputVO.setIsHeadMgr("N");
		}
		
		//判斷是否為內控管理科 or 財管績效管理科人員
		if(specialHeadMgrMap.containsKey(roleID)){
			outputVO.setIsSpecialHeadMgr("Y");
		}else {
			outputVO.setIsSpecialHeadMgr("N");
		}
		
		outputVO.setTotalList(dam.exeQuery(condition));
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		return outputVO;
	}

	public void queryData(QueryConditionIF condition, PMS408InputVO inputVO, String actionType) throws JBranchException, ParseException {

		initUUID();
		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		XmlInfo xmlInfo = new XmlInfo();
		boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);
		boolean isPSOP = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);
		boolean isARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);

		//20170918問題單3719:若為本日無異動時, 不判斷NOTE不為空而是改判斷SEQ=N
		StringBuffer sql = new StringBuffer();

		sql.append("WITH ARLIST (REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NAME, BRANCH_NBR) AS ( ");
		sql.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NAME, BRANCH_NBR ");
		sql.append("  FROM TBPMS_ORG_REC_N ");
		sql.append("  WHERE LAST_DAY(TO_DATE(:yearmonand,'YYYYMM')) BETWEEN START_TIME AND END_TIME ");
		sql.append("  AND ORG_TYPE = '50' ");
		sql.append("  AND DEPT_ID >= '200' ");
		sql.append("  AND DEPT_ID <= '900' ");
		sql.append("  AND LENGTH(DEPT_ID) = 3 ");
		sql.append(") ");
		
		switch (actionType) {
			case "V2" :
				sql.append(", PAR AS ( ");
				sql.append(getSQL("BASE", false));
				sql.append(") ");
				
				sql.append(", QA AS ( ");
				sql.append(getSQL("QA", false));
				sql.append(") ");
				
				sql.append(", BEF AS ( ");
				sql.append(getSQL("BEF", false));
				sql.append(") ");
				
				sql.append(", AFR AS ( ");
				sql.append(getSQL("AFR", false));
				sql.append(") ");
				break;
		}
			
		sql.append("SELECT ");
		switch (actionType) {
			case "V2" :
				sql.append("       EXP_D.TEST_SEQ_BEF, ");
				sql.append("       EXP_D.QST_NO_BEF, ");
				sql.append("       EXP_D.QUESTION_DESC_BEF, ");
				sql.append("       EXP_D.ANSWER_BEF, ");
			    sql.append("       EXP_D.ANSWER_DESC_BEF, ");
				sql.append("       EXP_D.SIDE_BEF, ");
				sql.append("       EXP_D.TEST_SEQ_AFR, ");
				sql.append("       EXP_D.QST_NO_AFR, ");
				sql.append("       CASE WHEN EXP_D.QUESTION_DESC_AFR = EXP_D.QUESTION_DESC_BEF THEN '同前次題目' ELSE EXP_D.QUESTION_DESC_AFR END AS QUESTION_DESC_AFR, ");
				sql.append("       EXP_D.ANSWER_AFR, ");
			    sql.append("       EXP_D.ANSWER_DESC_AFR, ");
				sql.append("       EXP_D.SIDE_AFR ");  
				
				break;
			default:
				sql.append("       CASE WHEN A.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
				sql.append("       A.SEQ, ");
				sql.append("       A.DATA_DATE, ");
				sql.append("       A.BRANCH_NBR, ");
				sql.append("       TO_CHAR(A.CREATETIME, 'YYYY-MM-DD') AS CREATETIME, ");
				sql.append("       CASE WHEN TRUNC(a.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
				sql.append("       A.NOTE_TYPE, ");
				sql.append("       A.NOTE, ");
				sql.append("       A.NOTE2, ");
				sql.append("       A.SUPERVISOR_FLAG, ");
				sql.append("       A.HR_ATTR, ");
				sql.append("       A.CREATETIME AS CDATE, ");
				sql.append("       A.MODIFIER, ");
				sql.append("       A.LASTUPDATE, ");
				sql.append("       A.FIRSTUPDATE, ");
				sql.append("       A.DATA_TYPE AS DATA_TYPE_O, ");
				sql.append("       A.REDO_KYC_TIMES, ");
				sql.append("       CASE WHEN B.INVEST_BRANCH_NBR = '999' THEN '網銀' ELSE '分行' END AS INVEST_SOURCE, ");
				sql.append("       CASE WHEN C.IPADDRESS IS NOT NULL THEN C.IPADDRESS ELSE ' ' END AS INVEST_IP, ");
				sql.append("       B.CUST_ID, ");
				sql.append("       B.CUST_NAME, ");
				sql.append("       B.AO_CODE, ");
				sql.append("       COALESCE(COOL.CUST_RISK_BEF, B.CUST_RISK_BEF) AS CUST_RISK_BEF, ");
				sql.append("       CASE WHEN B.CREATOR = '999996' THEN 'Fubon+' ELSE B.CREATOR END AS CREATOR, ");
				sql.append("       COALESCE(COOL.CUST_RISK_AFR, B.CUST_RISK_AFR) AS CUST_RISK_AFR, ");
				sql.append("       TO_CHAR(B.SIGNOFF_DATE, 'YYYY-MM-DD') AS SIGNOFF_DATE, ");
				sql.append("       EMP.EMP_NAME, ");
				sql.append("       ARLIST.BRANCH_NAME, ");
				sql.append("       ARLIST.BRANCH_AREA_ID, ");
				sql.append("       ARLIST.REGION_CENTER_ID, ");
				sql.append("       PAR.PARAM_NAME AS DATA_TYPE, ");
				sql.append("       A.CUST_AGE, ");
//				sql.append("       A.RECORD_SEQ, ");
				sql.append("       (SELECT COUNT(*) FROM TBKYC_INVESTOREXAM_M_COMPARE IMC WHERE IMC.DATA_DATE = A.DATA_DATE AND IMC.CUST_ID = B.CUST_ID AND IMC.TEST_SEQ_BEF IS NULL) AS JS_IMC_COUNTS, ");
				sql.append("       (SELECT COUNT(*) FROM TBKYC_INVESTOREXAM_M_COMPARE IMC WHERE IMC.DATA_DATE = A.DATA_DATE AND IMC.CUST_ID = B.CUST_ID) AS IMC_COUNTS ");
				
				break;
		}
			
		sql.append("FROM TBPMS_MONTHLY_KYC_RPT A ");
		
		sql.append("LEFT JOIN TBKYC_INVESTOREXAM_M_HIS B ON A.SEQ = B.SEQ ");
		sql.append("LEFT JOIN TBPMS_SALES_AOCODE_REC AO ON B.AO_CODE = AO.AO_CODE AND A.CREATETIME BETWEEN AO.START_TIME AND AO.END_TIME ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP ON AO.EMP_ID = EMP.EMP_ID AND A.BRANCH_NBR = EMP.DEPT_ID AND A.CREATETIME BETWEEN EMP.START_TIME AND EMP.END_TIME ");

		sql.append("LEFT JOIN ARLIST ON A.BRANCH_NBR = ARLIST.BRANCH_NBR ");

		switch (actionType) {
			case "V2" :
				sql.append("LEFT JOIN ( ");
				sql.append(getSQL("FINAL", true));
				sql.append(") EXP_D ON EXP_D.DATA_DATE = A.DATA_DATE AND EXP_D.CUST_ID = B.CUST_ID ");
				
				break;

			default :
				sql.append("LEFT JOIN TBKYC_COOLING_PERIOD COOL ON A.SEQ = COOL.SEQ ");
				sql.append("LEFT JOIN TBPMS_BRANCH_IP C ON B.INVEST_IP = C.IPADDRESS  AND C.COM_TYPE = '1' ");
				sql.append("LEFT JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'PMS.KYC_TYPE' AND PAR.PARAM_CODE = A.DATA_TYPE ");
				
				break;
		}
		
		sql.append("WHERE 1 = 1 ");
		sql.append("AND A.DATA_DATE >= :sCreDate ");
		sql.append("AND A.DATA_DATE <= :eCreDate ");
		sql.append("AND (B.CUST_ID IS NOT NULL OR A.SEQ = 'N') ");
		
		//設定的月份
		condition.setObject("yearmonand", new SimpleDateFormat("yyyyMM").format(inputVO.getsCreDate()));
		condition.setObject("sCreDate", new SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		condition.setObject("eCreDate", new SimpleDateFormat("yyyyMMdd").format(inputVO.getEndDate()));
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			// AO_COCE
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append("AND B.AO_CODE LIKE :ao_code ");
				condition.setObject("ao_code", inputVO.getAo_code());
			} else {
				// 登入為銷售人員強制加AO_CODE
				if (!"A157".equals(loginRoleID)) {
					if (isFC || isPSOP) {

						// 取得查詢資料可視範圍
						PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
						PMS000InputVO pms000InputVO = new PMS000InputVO();
						pms000InputVO.setReportDate(new SimpleDateFormat("yyyyMM").format(inputVO.getsCreDate()));
						PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

						sql.append("AND B.AO_CODE IN (:ao_code) ");
						condition.setObject("ao_code", pms000outputVO.getV_aoList());
					}
				}
			}

			// by Willis 20171024 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別

			if (StringUtils.isNumeric(inputVO.getBranch_nbr()) && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("AND ARLIST.BRANCH_NBR = :BRNCH_NBRR ");
				condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sql.append("AND ( ");
				sql.append("  (A.RM_FLAG = 'B' AND ARLIST.BRANCH_AREA_ID = :BRANCH_AREA_IDD) ");
				
				if (isHANDMGR || isARMGR) {
					sql.append("  OR (A.RM_FLAG = 'U' AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE EMP.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :BRANCH_AREA_IDD )) ");
				}
			
				sql.append(") ");
				condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("AND ARLIST.REGION_CENTER_ID = :REGION_CENTER_IDD ");
				condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}
			
			if (!isHANDMGR && !isARMGR) {
				sql.append("AND A.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("AND (");
				sql.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE EMP.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("  OR EMP.E_DEPT_ID = :uhrmOP ");
				sql.append(")");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("AND A.RM_FLAG = 'U' ");

			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append("AND B.AO_CODE LIKE :ao_code ");
				condition.setObject("ao_code", inputVO.getAo_code());
			}
		}

		//由工作首頁CRM181過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sql.append("AND A.FIRSTUPDATE IS NULL ");
		}
		
		// #0001400_WMS-CR-20221221-01_擬優化各項內控報表相關功能_P1 : 依「首次建立時間」是否為空值判斷，以利填寫檢核說明時可篩選。
 		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
 			switch (inputVO.getNoteStatus()) {
 				case "01":
 					sql.append("AND A.FIRSTUPDATE IS NOT NULL ");
 					break;
 				case "02":
 					sql.append("AND A.FIRSTUPDATE IS NULL ");
 					break;
 			}
 		}
 		
 		// #0001400_WMS-CR-20221221-01_擬優化各項內控報表相關功能_P1 : 資料來源
 		if (StringUtils.isNotEmpty(inputVO.getKycType())) {
 			switch (inputVO.getKycType()) {
 				case "1":
 					sql.append("AND A.DATA_TYPE = '1' ");
 					break;
 				case "2":
 					sql.append("AND A.DATA_TYPE = '2' ");
 					break;
 			}
 		}

		sql.append("ORDER BY A.DATA_DATE DESC, A.BRANCH_NBR, B.CUST_ID, B.SIGNOFF_DATE ");
		switch (actionType) {
			case "V2" :
				sql.append(", TO_NUMBER(EXP_D.QST_NO_BEF) ");  
				break;
		}

		condition.setQueryString(sql.toString());
	}

	/** 更新資料 **/
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS408InputVO inputVO = (PMS408InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		try {
			for (Map<String, Object> map : inputVO.getList()) {
				map.put("NOTE_TYPE", map.get("NOTE_TYPE") == null ? "" : map.get("NOTE_TYPE"));
				map.put("NOTE", map.get("NOTE") == null ? "" : map.get("NOTE"));
				map.put("NOTE2", map.get("NOTE2") == null ? "" : map.get("NOTE2"));
				map.put("HR_ATTR", map.get("HR_ATTR") == null ? "" : map.get("HR_ATTR"));
//				map.put("RECORD_SEQ", map.get("RECORD_SEQ") == null ? "" : map.get("RECORD_SEQ"));

				for (Map<String, Object> map2 : inputVO.getList2()) { // 資料修改前
					map2.put("NOTE_TYPE", map2.get("NOTE_TYPE") == null ? "" : map2.get("NOTE_TYPE"));
					map2.put("NOTE", map2.get("NOTE") == null ? "" : map2.get("NOTE"));
					map2.put("NOTE2", map2.get("NOTE2") == null ? "" : map2.get("NOTE2"));
					map2.put("HR_ATTR", map2.get("HR_ATTR") == null ? "" : map2.get("HR_ATTR"));
//					map2.put("RECORD_SEQ", map2.get("RECORD_SEQ") == null ? "" : map2.get("RECORD_SEQ"));
					
					if (map.get("DATA_DATE").equals(map2.get("DATA_DATE")) && 
						map.get("BRANCH_NBR").equals(map2.get("BRANCH_NBR")) && 
						map.get("DATA_TYPE_O").equals(map2.get("DATA_TYPE_O")) && 
						map.get("SEQ").equals(map2.get("SEQ")) &&
						(!map.get("NOTE_TYPE").equals(map2.get("NOTE_TYPE")) || 
						 !map.get("NOTE").equals(map2.get("NOTE")) || 
						 !map.get("NOTE2").equals(map2.get("NOTE2")) || 
						 !map.get("HR_ATTR").equals(map2.get("HR_ATTR")) 
//						 || 
//						 !map.get("RECORD_SEQ").equals(map2.get("RECORD_SEQ"))
						)) {

						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuffer();
						
						sb.append("UPDATE TBPMS_MONTHLY_KYC_RPT ");
						sb.append("SET HR_ATTR = :hrAttr, ");
						sb.append("    NOTE_TYPE = :noteType, ");
						sb.append("    NOTE = :note, ");
						sb.append("    NOTE2 = :note2, ");
//						sb.append("    RECORD_SEQ = :recordSEQ, ");
						sb.append("    MODIFIER = :modifier, ");
						sb.append("    LASTUPDATE = sysdate ");
						
						if (null == map.get("FIRSTUPDATE")) {
							sb.append("    , FIRSTUPDATE = sysdate ");
						}
						
						sb.append("WHERE DATA_DATE = :dataDate ");
						sb.append("AND BRANCH_NBR = :branch ");
						sb.append("AND SEQ = :seq ");
						sb.append("AND DATA_TYPE = :data_type ");
						
						// KEY
						queryCondition.setObject("dataDate", map.get("DATA_DATE"));
						queryCondition.setObject("branch", map.get("BRANCH_NBR"));
						queryCondition.setObject("seq", map.get("SEQ"));
						queryCondition.setObject("data_type", map.get("DATA_TYPE_O"));
						
						// CONTENT
						queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
						queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
						queryCondition.setObject("note", map.get("NOTE"));
						queryCondition.setObject("note2", map.get("NOTE2"));
//						queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
						queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
						
						queryCondition.setQueryString(sb.toString());
						
						dam.exeUpdate(queryCondition);
					}
				}
			}

			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/** 產出Excel **/
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {
		
		SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

		XmlInfo xmlInfo = new XmlInfo();
		
		PMS408InputVO inputVO = (PMS408InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		switch (inputVO.getRptVersion()) {
			case "V1":
				this.queryData(condition, inputVO, inputVO.getRptVersion());
				list = dam.exeQuery(condition);
				break;
			case "V2":
				list = inputVO.getCompareDtlList();
				break;
		}
		
		String reportName = "短期多次承作KYC日報";
		String fileName = reportName + "_" + (StringUtils.equals(inputVO.getRptVersion(), "V2") ? "差異表_" : "") + sdfYYYYMMDD.format(new Date()) + "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(reportName);
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		// 表頭 CELL型式
		XSSFCellStyle headingStyle = workbook.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		
		// 資料 CELL型式
		XSSFCellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		titleStyle.setBorderBottom((short) 1);
		titleStyle.setBorderTop((short) 1);
		titleStyle.setBorderLeft((short) 1);
		titleStyle.setBorderRight((short) 1);
		
		Integer index = 0;

		XSSFRow row;
		
		String[] headerLine = new String[]{};
		String[] mainLine   = new String[]{};
		switch (inputVO.getRptVersion()) {
			case "V1":
				row = sheet.createRow(index);
				
				for (int i = 0; i < 1; i++) {
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(titleStyle);
					cell.setCellValue(reportName);
				}
				
				index++;
				
				headerLine  = new String[] { "序號", "私銀註記", "資料日期", "分行代碼", "分行名稱", "公用電腦IP位置", "身分證字號", "客戶姓名", "高齡客戶", "AO Code", "風險承受度前", "風險承受度後", "測試/簽置日期", "資料來源", "短期多次承作KYC次數", "建立人", "專員沒有勸誘客戶提高風險屬性", "查證方式", "檢核說明", "首次建立時間", "最新異動人員", "最新異動日期"};
				mainLine 	= new String[] { "SEQ", "RM_FLAG", "DATA_DATE", "BRANCH_NBR", "BRANCH_NAME", "INVEST_IP", "CUST_ID", "CUST_NAME", "CUST_AGE", "AO_CODE", "CUST_RISK_BEF", "CUST_RISK_AFR", "SIGNOFF_DATE", "DATA_TYPE", "REDO_KYC_TIMES", "CREATOR", "HR_ATTR", "NOTE", "NOTE2", "FIRSTUPDATE", "MODIFIER", "LASTUPDATE"};

				break;
			case "V2":
				headerLine  = new String[] {"前次測試/聲明代號", "前次題號", "前次題目", "前次答案", "本次測試/聲明代號", "本次題號", "本次題目", "本次答案"};
				mainLine 	= new String[] {"TEST_SEQ_BEF", "QST_NO_BEF", "QUESTION_DESC_BEF", "ANSWER_DESC_BEF", "TEST_SEQ_AFR", "QST_NO_AFR", "QUESTION_DESC_AFR", "ANSWER_DESC_AFR"};

				break;
		}
		
		row = sheet.createRow(index);
		
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
		}

		index++;
		
		int a = 1;
		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);
			
			for (int i = 0; i < mainLine.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(mainStyle);

				switch (mainLine[i]) {
					case "SEQ":
						cell.setCellValue(String.valueOf(a));
						break;
					case "CUST_ID":
						cell.setCellValue(DataFormat.getCustIdMaskForHighRisk(StringUtils.defaultString((String) map.get(mainLine[i]))));
						break;
					case "FIRSTUPDATE":
					case "LASTUPDATE":
						cell.setCellValue(dateFormat(map, mainLine[i]));
						break;
					case "CUST_AGE":
						cell.setCellValue(StringUtils.equals("", checkIsNull(map, mainLine[i])) ? "" : (new BigDecimal(checkIsNull(map, mainLine[i])).compareTo(new BigDecimal("65")) >= 0 ? checkIsNull(map, mainLine[i]) : ""));
						break;
					case "NOTE":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");
					
					
						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(mainLine[i]));
						}
						
						cell.setCellValue(note);
						break;
					case "TEST_SEQ_BEF": 
					case "QST_NO_BEF":
					case "QUESTION_DESC_BEF":
					case "ANSWER_DESC_BEF":
					case "TEST_SEQ_AFR":
					case "QST_NO_AFR":
					case "QUESTION_DESC_AFR":
					case "ANSWER_DESC_AFR":
						cell.setCellValue(checkIsNull(map, mainLine[i]));
						break;
					case "RECORD_SEQ":
						cell.setCellValue(checkIsNull(map, mainLine[i]) + "");
						break;
					default :
						cell.setCellValue(checkIsNull(map, mainLine[i]));
						break;
				}
			}
			
			a++;
			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));

		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		sendRtnObject(null);
	}

	/** 檢查Map取出欄位是否為Null **/
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			if ("CUST_ID".equals(key)) {
				return DataFormat.getCustIdMaskForHighRisk(String.valueOf(map.get(key)));
			} else {
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}

	/** 日期格式 **/
	private String dateFormat(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(map.get(key));
		} else
			return "";
	}
}