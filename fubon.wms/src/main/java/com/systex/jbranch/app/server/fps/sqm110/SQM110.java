package com.systex.jbranch.app.server.fps.sqm110;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Description : 滿意度問卷查詢
 * Author : Willis
 * Date : 2018/05/02
 * Modifier : 2021/03/04 BY OCEAN => #0535: WMS-CR-20210115-01_擬新增即時滿意度淨推薦值問項
 */

@Component("sqm110")
@Scope("request")
public class SQM110 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

	/**
	 * 查詢資料
	 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM110InputVO inputVO = (SQM110InputVO) body;
		SQM110OutputVO outputVO = new SQM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		String roleID = (String) getCommonVariable(SystemVariableConsts.LOGINROLE); //被代理人ROLE
		String ID = (String) getCommonVariable(SystemVariableConsts.LOGINID); //被代理人id
		
		// 取得查詢資料可視範圍
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));

		// 『整體滿意度』OR『問題7』任一不滿意 ==> 需要出滿意度報告
		sql.append(" WITH Q7 AS ( ");
		sql.append(" SELECT NULL AS SEQ, DATA_DATE, A.QTN_TYPE, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID, AO_CODE, QUESTION_DESC, CUST_NAME, CUST_ID, ");
		sql.append("        MOBILE_NO, ANSWER, RESP_NOTE, TRADE_DATE, RESP_DATE, SEND_DATE, CAMPAIGN_ID, STEP_ID, SATISFACTION_O, SATISFACTION_W, ");
		sql.append("        NULL AS CASE_NO, NULL AS OWNER_EMP_ID, NULL AS HO_CHECK, NULL AS DEDUCTION_FINAL,  NULL AS DELETE_FLAG,  NULL AS CASE_STATUS,  NULL AS QST_VERSION ");
		sql.append(" FROM TBSQM_CSM_MAIN A ");
		sql.append(" LEFT JOIN TBSQM_CSM_ANSWER ANS ON A.PAPER_UUID = ANS.PAPER_UUID ");
		sql.append(" LEFT JOIN TBSQM_CSM_QUESTION Q ON ANS.QUESTION_MAPPING_ID = Q.QUESTION_MAPPING_ID ");
		sql.append(" LEFT JOIN TBSQM_CSM_OPTIONS O ON ANS.ITEM_MAPPING_ID = O.ITEM_MAPPING_ID ");
		sql.append(" WHERE 1 = 1 AND A.QTN_TYPE = 'WMS03' AND Q.PRIORITY = 7 AND O.OPTION_TYPE = 'S' AND O.SCORE IN ('1', '2', '3') AND A.SATISFACTION_O IN ('1', '2', '3') ");
		sql.append(" ) ");
		// 以上為取得臨櫃問券中『問題7』+『整體滿意度』無不滿意的問券
		
		sql.append("SELECT DISTINCT * FROM ( ");
		sql.append("  SELECT A.*, ");
		sql.append("         ORG.REGION_CENTER_ID AS REGION_CENTER_ID_ORG, ");
		sql.append("         ORG.REGION_CENTER_NAME, ");
		sql.append("         ORG.BRANCH_AREA_ID AS BRANCH_AREA_ID_ORG, ");
		sql.append("         ORG.BRANCH_AREA_NAME, ");
		sql.append("         ORG.BRANCH_NAME, ");
		sql.append("         EMP.EMP_NAME, ");
		sql.append("         TO_CHAR(PABTH_UTIL.FC_GETBUSIDAY(TO_DATE(DATA_DATE, 'yyyyMMdd'), 'TWD', 8), 'yyyyMMdd') END_DATE, ");
		sql.append("         CUST.CON_DEGREE, ");
		sql.append("         FRQ.FRQ_DAY, ");
		sql.append("         TO_CHAR(VISIT.CREATETIME, 'yyyyMMdd') AS LAST_VISIT_DATE, ");
		sql.append("         CASE WHEN R.ROLE_ID IS NULL THEN NULL ELSE R.ROLE_ID END AS ROLE_FLAG, ");
		sql.append("         UH.DEPT_ID AS UHRM_DEPT, ");
		sql.append("         UHDEPT.DEPT_NAME AS UHRM_DEPT_NAME ");
		sql.append("  FROM ( ");
		sql.append("  	SELECT SEQ, DATA_DATE, QTN_TYPE, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID, AO_CODE, QUESTION_DESC, CUST_NAME, CUST_ID, ");
		sql.append("           MOBILE_NO, ANSWER, RESP_NOTE, TRADE_DATE, RESP_DATE, SEND_DATE, CAMPAIGN_ID, STEP_ID, SATISFACTION_O, SATISFACTION_W, ");
		sql.append("           CASE_NO, OWNER_EMP_ID, HO_CHECK, DEDUCTION_FINAL, DELETE_FLAG, CASE WHEN CASE_STATUS = 'N' THEN NULL ELSE COALESCE(CASE_STATUS, '0') END  AS  CASE_STATUS,QST_VERSION ");                                          
		sql.append("  	FROM TBSQM_CSM_IMPROVE_MAST WHERE 1 = 1 AND DELETE_FLAG IS NULL AND HO_CHECK = 'Y' ");
//		sql.append("  	FROM TBSQM_CSM_IMPROVE_MAST WHERE SATISFACTION_W IN('4', '5', '6') AND DELETE_FLAG IS NULL  AND HO_CHECK = 'Y' ");
		sql.append("  	UNION ");
		sql.append("  	SELECT NULL AS SEQ, DATA_DATE, QTN_TYPE, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID, AO_CODE, QUESTION_DESC, CUST_NAME, CUST_ID, ");
		sql.append("           MOBILE_NO, ANSWER, RESP_NOTE, TRADE_DATE, RESP_DATE, SEND_DATE, CAMPAIGN_ID, STEP_ID, SATISFACTION_O, SATISFACTION_W, ");
		sql.append("           NULL AS CASE_NO, NULL AS OWNER_EMP_ID, NULL AS HO_CHECK, NULL AS DEDUCTION_FINAL,  NULL AS DELETE_FLAG,  NULL AS CASE_STATUS, NULL AS QST_VERSION ");
		sql.append("  	FROM TBSQM_CSM_MAIN WHERE 1 = 1 AND SATISFACTION_W IN('1', '2', '3') AND QTN_TYPE <> 'WMS03' ");
//		sql.append("  	FROM TBSQM_CSM_MAIN WHERE SATISFACTION_W IN('1', '2', '3') ");
		sql.append("  	UNION ");
		sql.append("	SELECT * FROM Q7 ");
		sql.append("  	UNION ");
		// 因為問題7非必填，所以要另外取得『整體滿意度』無不滿意的問券，然後排除『問題7』+『整體滿意度』無不滿意的問券
		sql.append("  	SELECT NULL AS SEQ, DATA_DATE, QTN_TYPE, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID, AO_CODE, QUESTION_DESC, CUST_NAME, CUST_ID, ");
		sql.append("           MOBILE_NO, ANSWER, RESP_NOTE, TRADE_DATE, RESP_DATE, SEND_DATE, CAMPAIGN_ID, STEP_ID, SATISFACTION_O, SATISFACTION_W, ");
		sql.append("           NULL AS CASE_NO, NULL AS OWNER_EMP_ID, NULL AS HO_CHECK, NULL AS DEDUCTION_FINAL,  NULL AS DELETE_FLAG,  NULL AS CASE_STATUS, NULL AS QST_VERSION ");
		sql.append("  	FROM TBSQM_CSM_MAIN A ");
		sql.append("  	WHERE 1 = 1 AND A.QTN_TYPE = 'WMS03' AND A.SATISFACTION_O IN ('1', '2', '3') ");
		sql.append("  	AND NOT EXISTS (SELECT 'X' FROM Q7 WHERE A.DATA_DATE = Q7.DATA_DATE AND A.QTN_TYPE = Q7.QTN_TYPE AND A.CUST_ID = Q7.QTN_TYPE) ");
		sql.append("  )  A ");
		sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON ORG.DEPT_ID = A.BRANCH_NBR AND TO_DATE(A.TRADE_DATE, 'yyyyMMdd') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP ON EMP.EMP_ID = A.EMP_ID AND TO_DATE(A.TRADE_DATE, 'yyyyMMdd') BETWEEN EMP.START_TIME AND EMP.END_TIME ");
		sql.append("  LEFT JOIN TBPMS_CUST_REC_N CUST ON CUST.CUST_ID = A.CUST_ID AND TO_DATE(A.SEND_DATE, 'yyyyMMdd') BETWEEN CUST.START_TIME AND CUST.END_TIME ");
		sql.append("  LEFT JOIN VWCRM_CUST_MGMT_FRQ_MAP FRQ ON FRQ.CON_DEGREE = CUST.CON_DEGREE AND FRQ.VIP_DEGREE = NVL(CUST.VIP_DEGREE, 'M') ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT A.CUST_ID,MAX(A.CREATETIME) AS CREATETIME ");
		sql.append("    FROM TBCRM_CUST_VISIT_RECORD A ");
		sql.append("    INNER JOIN TBSQM_CSM_IMPROVE_MAST B ON A.CUST_ID = B.CUST_ID AND A.CREATETIME <= TO_DATE(B.SEND_DATE, 'yyyyMMdd') ");
		sql.append("    GROUP BY A.CUST_ID ");
		sql.append("  ) VISIT ON VISIT.CREATETIME <= TO_DATE(A.SEND_DATE, 'yyyyMMdd') AND VISIT.CUST_ID = A.CUST_ID ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT ROLE_ID FROM TBORG_ROLE WHERE SYS_ROLE IN('HEADMGR_ROLE', 'ARMGR_ROLE', 'MBRMGR_ROLE', 'BMMGR_ROLE', 'UHRMMGR_ROLE', 'UHRMBMMGR_ROLE') ORDER BY SYS_ROLE ");
		sql.append("  ) R ON R.ROLE_ID = :ROLE_ID ");
		sql.append("  LEFT JOIN (SELECT DISTINCT EMP_ID, DEPT_ID FROM VWORG_EMP_UHRM_INFO WHERE PRIVILEGEID IN ('UHRM001', 'UHRM002')) UH ON UH.EMP_ID = A.EMP_ID ");
		sql.append("  LEFT JOIN TBORG_DEFN UHDEPT ON UHDEPT.DEPT_ID = UH.DEPT_ID ");
		sql.append("  WHERE 1 = 1 ");
		condition.setObject("ROLE_ID", roleID);
		
		// 資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("  AND A.TRADE_DATE >= :times "); // sql.append("and A.DATA_DATE >= :times ");
			condition.setObject("times", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		}
		
		if (inputVO.geteCreDate() != null) {
			sql.append("  AND A.TRADE_DATE <= :timee "); // sql.append("and A.DATA_DATE <= :timee ");
			condition.setObject("timee", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.geteCreDate()));
		}

		switch (getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG).toString()) {
			//私銀角色
			case "uhrmMGR":
			case "uhrmBMMGR":
				// 若ID等於自己，可以查詢自己所有資訊，不加其他組織查詢條件
				if (ID.equals(inputVO.getEmp_id())) {
					sql.append(" AND A.EMP_ID = :emp_id ");
					condition.setObject("emp_id", inputVO.getEmp_id());
				} else {
					//有UHRM權限人員只能查詢UHRM人員鍵機或UHRM為招攬人員的案件
					sql.append(" AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE MT.EMP_ID = A.EMP_ID AND MT.DEPT_ID = :loginArea) ");
					condition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
					break;
				}
				break;	
			//一般非私銀角色
			default:
				// 若ID等於自己，可以查詢自己所有資訊，不加其他組織查詢條件
				if (ID.equals(inputVO.getEmp_id())) {
					sql.append("  AND A.EMP_ID = :emp_id ");
					condition.setObject("emp_id", inputVO.getEmp_id());
				} else if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) { //有輸入分行
					sql.append("  AND ORG.BRANCH_NBR = :BRNCH_NBRR ");
					sql.append(" AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE MT.EMP_ID = A.EMP_ID) ");
					condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
				} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {  //有輸入營運區
					if(StringUtils.equals("Y", inputVO.getMgrUHRMAreaYN())) {
						//總行或業務處長選私銀區
						sql.append(" AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE MT.EMP_ID = A.EMP_ID AND MT.DEPT_ID = :areaId) ");
					} else {
						sql.append(" AND ORG.BRANCH_AREA_ID = :areaId ");
						sql.append(" AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE MT.EMP_ID = A.EMP_ID AND MT.DEPT_ID = :areaId) ");
					}
					condition.setObject("areaId", inputVO.getBranch_area_id());
				} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) { //有輸入業務處
					sql.append("  AND ORG.REGION_CENTER_ID = :REGION_CENTER_IDD ");
					condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
				}
				break;
		}

		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append("  AND A.EMP_ID = :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id());
		}

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("  AND A.CUST_ID LIKE :cust_id ");
			condition.setObject("cust_id", inputVO.getCust_id() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getQtn_type())) {
			sql.append("  AND A.QTN_TYPE = :qtn_type ");
			condition.setObject("qtn_type", inputVO.getQtn_type());
		}

		if (StringUtils.isNotBlank(inputVO.getSatisfaction_o())) {
			sql.append("  AND A.SATISFACTION_O = :satisfaction ");
			condition.setObject("satisfaction", inputVO.getSatisfaction_o());
		}

		if (StringUtils.isNotBlank(inputVO.getCase_status())) {
			if ("0".equals(inputVO.getCase_status())) {
				sql.append("  AND A.CASE_STATUS = '0' ");
			} else {
				sql.append("  AND A.CASE_STATUS = :case_status ");
				condition.setObject("case_status", inputVO.getCase_status());
			}
		}

		sql.append(") A ");
		sql.append("ORDER BY TRADE_DATE, BRANCH_NBR, EMP_ID, AO_CODE, CUST_ID, QTN_TYPE ");
		
		condition.setQueryString(sql.toString());

		outputVO.setTotalList(dam.exeQuery(condition));

		sendRtnObject(outputVO);
	}

	/**
	 * 查詢問題答案資料
	 * 
	 * @throws ParseException
	 **/
	public void getQuestion(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM110DetailInputVO inputVO = (SQM110DetailInputVO) body;
		SQM110OutputVO outputVO = new SQM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT Q.PRIORITY AS QST_ORDER, ");
		sql.append("       Q.DESCRIPTION AS QST_DESC, ");
		sql.append("       LISTAGG(CASE WHEN Q.PRIORITY = '1' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS01' AND Q.PRIORITY = '12' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS02' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS03' AND Q.PRIORITY = '18' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS04' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN OP.DESCRIPTION IS NULL THEN NULL ");
		sql.append("               ELSE DECODE(OP.OPTION_TYPE, 'S', NVL(TO_CHAR(OP.SCORE), OP.DESCRIPTION) ,'M', OP.DESCRIPTION ) END, ';') ");
		sql.append("       WITHIN GROUP(ORDER BY Q.PRIORITY ASC, OP.PRIORITY ASC, Q.DESCRIPTION ASC) AS ANSWER ");
		sql.append("FROM TBSQM_CSM_QUESTION Q ");
		sql.append("LEFT JOIN TBSQM_CSM_ANSWER ANS ON Q.QUESTION_MAPPING_ID = ANS.QUESTION_MAPPING_ID AND ANS.PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE) ");                                          
		sql.append("LEFT JOIN TBSQM_CSM_OPTIONS OP ON (ANS.ITEM_MAPPING_ID = OP.ITEM_MAPPING_ID AND Q.QUESTION_MAPPING_ID = OP.QUESTION_MAPPING_ID) ");
		sql.append("WHERE Q.QTN_TYPE = ( ");
		sql.append("  SELECT DISTINCT QTN_TYPE ");
		sql.append("  FROM TBSQM_CSM_QUESTION ");
		sql.append("  WHERE QUESTION_MAPPING_ID IN ( ");
		sql.append("    SELECT QUESTION_MAPPING_ID ");
		sql.append("    FROM TBSQM_CSM_ANSWER ");
		sql.append("    WHERE PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE) ");
		sql.append("  ) ");
		sql.append(") ");
		sql.append("AND NVL(OP.OPTION_TYPE, ' ') <> 'O' ");
		sql.append("AND ANS.QUESTION_MAPPING_ID IS NOT NULL ");
		sql.append("GROUP BY Q.PRIORITY, Q.DESCRIPTION ");
		sql.append("ORDER BY LENGTH(Q.PRIORITY), Q.PRIORITY ");
		
		condition.setObject("CUST_ID", inputVO.getCust_id());
		condition.setObject("DATA_DATE", inputVO.getData_date());
		condition.setObject("QTN_TYPE", inputVO.getQtnType());

		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);

		outputVO.setTotalList(list);
		outputVO.setResultList(list);
		
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢問題答案資料
	 * 
	 * @throws ParseException
	 **/
	public List<Map<String, String>> getQuestionList(String qtn_type, String data_date, String cust_id) throws JBranchException, ParseException {

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT ROWNUM AS QST_ORDER, ");
		sql.append("       PRIORITY, ");
		sql.append("       QST_DESC, ");
		sql.append("       ANSWER, ");
		sql.append("       NULL AS BRANCH_NBR, ");
		sql.append("       NULL AS TRADE_DATE, ");
		sql.append("       NULL AS RESP_DATE ");
		sql.append("FROM ( ");
		sql.append("  SELECT Q.PRIORITY, ");
		sql.append("         Q.DESCRIPTION AS QST_DESC, ");
		sql.append("         LISTAGG(CASE WHEN Q.PRIORITY = '1' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");
		sql.append("                      WHEN Q.QTN_TYPE = 'WMS01' AND Q.PRIORITY = '12' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                      WHEN Q.QTN_TYPE = 'WMS02' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                      WHEN Q.QTN_TYPE = 'WMS03' AND Q.PRIORITY = '18' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                      WHEN Q.QTN_TYPE = 'WMS04' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");   
		sql.append("                      WHEN OP.DESCRIPTION IS NULL THEN NULL ");
		sql.append("                 ELSE CASE WHEN OP.DESCRIPTION = 'multi' THEN ANS.USER_INPUT ELSE OP.DESCRIPTION END END, ';') ");
		sql.append("        WITHIN GROUP(ORDER BY Q.PRIORITY ASC, OP.PRIORITY ASC, Q.DESCRIPTION ASC) AS ANSWER ");
		sql.append("  FROM TBSQM_CSM_QUESTION Q ");
		sql.append("  LEFT JOIN TBSQM_CSM_ANSWER ANS ON Q.QUESTION_MAPPING_ID = ANS.QUESTION_MAPPING_ID AND ANS.PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE) ");                                
		sql.append("  LEFT JOIN TBSQM_CSM_OPTIONS OP ON (ANS.ITEM_MAPPING_ID = OP.ITEM_MAPPING_ID AND Q.QUESTION_MAPPING_ID = OP.QUESTION_MAPPING_ID) ");
		sql.append("  WHERE Q.QTN_TYPE = ( ");
		sql.append("    SELECT DISTINCT QTN_TYPE ");
		sql.append("    FROM TBSQM_CSM_QUESTION ");
		sql.append("    WHERE QUESTION_MAPPING_ID IN( ");
		sql.append("      SELECT QUESTION_MAPPING_ID ");
		sql.append("      FROM TBSQM_CSM_ANSWER ");
		sql.append("      WHERE PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE) ");
		sql.append("    ) ");
		sql.append("  ) ");
		sql.append("  AND ANS.QUESTION_MAPPING_ID IS NOT NULL ");
		sql.append("  GROUP BY Q.PRIORITY, Q.DESCRIPTION ");
		sql.append("  ORDER BY LENGTH(Q.PRIORITY), Q.PRIORITY ");
		sql.append(") ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT NULL AS BRANCH_NBR, NULL AS TRADE_DATE, NULL AS RESP_DATE ");
		sql.append("  FROM DUAL ");
		sql.append(") A ON 1 = 1 ");
		sql.append("ORDER BY 1, 2 ");
		
		condition.setObject("CUST_ID", cust_id);
		condition.setObject("DATA_DATE", data_date);
		condition.setObject("QTN_TYPE", qtn_type);
		
		condition.setQueryString(sql.toString());
		
		return dam.exeQuery(condition);
	}

	/**
	 * 查詢明細資料
	 * 
	 * @throws ParseException
	 **/
	public void getDtlData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM110DetailInputVO inputVO = (SQM110DetailInputVO) body;
		SQM110OutputVO outputVO = new SQM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.*, CASE WHEN B.CNT > 0 THEN 'Y' ELSE 'N' END CASE_FLOW ");
		sql.append("FROM TBSQM_CSM_IMPROVE_DTL A ");
		sql.append("LEFT JOIN (SELECT COUNT(1) AS CNT, CASE_NO FROM TBSQM_CSM_FLOW_DTL WHERE CASE_NO = :CASE_NO GROUP BY CASE_NO) B ON B.CASE_NO = A.CASE_NO ");
		sql.append("WHERE A.CASE_NO = :CASE_NO ");
		
		condition.setObject("CASE_NO", inputVO.getCase_no());

		condition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢處理狀態軌跡明細資料
	 * 
	 * @throws ParseException
	 **/
	public void queryFlowDetail(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM110DetailInputVO inputVO = (SQM110DetailInputVO) body;
		SQM110OutputVO outputVO = new SQM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT SIGNOFF_ID, ");
		sql.append("       CASE WHEN SIGNOFF_ID <> CREATOR THEN SIGNOFF_NAME || '-代' ELSE SIGNOFF_NAME END AS SIGNOFF_NAME, ");
		sql.append("       NEXT_SIGNOFF_ID, ");
		sql.append("       NEXT_SIGNOFF_NAME, ");
		sql.append("       CASE WHEN ROWNUM > 1 THEN '5' ELSE CASE_STATUS END CASE_STATUS, ");
		sql.append("       CREATETIME, ");
		sql.append("       REMARK ");
		sql.append("FROM ( ");
		sql.append("  SELECT * ");
		sql.append("  FROM TBSQM_CSM_FLOW_DTL ");
		sql.append("  WHERE CASE_NO = :CASE_NO ");
		sql.append("  ORDER BY TO_NUMBER(SIGNOFF_NUM) DESC ");
		sql.append(") ");
		
		condition.setObject("CASE_NO", inputVO.getCase_no());

		condition.setQueryString(sql.toString());

		outputVO.setTotalList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢員工職級與職務資料
	 * 
	 * @throws ParseException
	 **/
	public void getJob_title_name(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		SQM110DetailInputVO inputVO = (SQM110DetailInputVO) body;
		SQM110OutputVO outputVO = new SQM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// 主查詢 sql 修正 20170120
		sql.append("SELECT EMP_ID,JOB_TITLE_NAME AS CUR_JOB, EMP_NAME ");
		sql.append("FROM TBPMS_EMPLOYEE_REC_N  ");
		sql.append("WHERE IS_PRIMARY_ROLE ='Y' ");
		sql.append("AND EMP_ID = :EMP_ID ");
		sql.append("AND TO_DATE(:TRADE_DATE, 'yyyyMMdd') BETWEEN START_TIME AND END_TIME ");
		
		condition.setObject("EMP_ID", inputVO.getEmp_id());
		condition.setObject("TRADE_DATE", inputVO.getTrade_date());
		
		condition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/** 匯出EXCEL檔 **/
	public void exportRPT(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		SQM110InputVO inputVO = (SQM110InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> ansMap = xmlInfo.doGetVariable("SQM.ANS_TYPE", FormatHelper.FORMAT_3);

		List<Map<String, Object>> list = inputVO.getCheckList();
		List<Map<String, Object>> list_01 = new ArrayList<Map<String, Object>>(); // 投資保險
		List<Map<String, Object>> list_02 = new ArrayList<Map<String, Object>>(); // RM/SRM
		List<Map<String, Object>> list_03 = new ArrayList<Map<String, Object>>(); // 開戶	
		List<Map<String, Object>> list_04 = new ArrayList<Map<String, Object>>(); // 櫃檯
		List<Map<String, Object>> list_05 = new ArrayList<Map<String, Object>>(); // 簡訊	

		DataFormat dfObj = new DataFormat();

		for (Map<String, Object> data : list) {
			//			if(StringUtils.isNotBlank(ObjectUtils.toString(data.get("DEDUCTION_FINAL")))){//調查結果已出來，才需要被匯出來。
			switch (ObjectUtils.toString(data.get("QTN_TYPE"))) {
				case "WMS01": // RM/SRM
					list_01.add(data);
					break;
				case "WMS02": // 投資保險
					list_02.add(data);
					break;
				case "WMS03": // 櫃檯
					list_03.add(data);
					break;
				case "WMS04": // 開戶
					list_04.add(data);
					break;
				case "WMS05": // 簡訊
					list_05.add(data);
					break;
			}
			//			}	
		}

		String fileName = "滿意度問卷報告_" + sdfYYYYMMDD.format(new Date()) + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");

		XSSFWorkbook wb = new XSSFWorkbook();
		
		// 資料 CELL型式
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);

		// 表頭 CELL型式
		XSSFCellStyle headingStyle = wb.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		if (!list_01.isEmpty()) {
			//設定SHEET名稱
			XSSFSheet sheet_01 = wb.createSheet("RM_SRM滿意度問卷");
			sheet_01.setDefaultColumnWidth(20);
			sheet_01.setDefaultRowHeightInPoints(20);

			Integer index = 0; // 行數

			List<String> heading = new ArrayList<String>();

			heading.add("(一)RM/SRM滿意度問卷(非常滿意/滿意/普通/不滿意/非常不滿意)");
			heading.add("問題");

			List<String> headerLineTop = new ArrayList<String>();

			headerLineTop.add("業務處");
			headerLineTop.add("營運區");
			headerLineTop.add("私銀區");
			headerLineTop.add("分行別");
			headerLineTop.add("AO_CODE");
			headerLineTop.add("RM/SRM姓名");
			headerLineTop.add("RM/SRM員工編號");
			headerLineTop.add("客戶ID");
			headerLineTop.add("交易日期");
			headerLineTop.add("填寫日期");

			// Heading
			XSSFRow row = sheet_01.createRow(index);

			for (int i = 0; i < heading.size(); i++) {
				if (i == 0) {
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(heading.get(i));
					sheet_01.addMergedRegion(new CellRangeAddress(0, 0, 0, headerLineTop.size() - 1));
				} else {
					XSSFCell cell = row.createCell(headerLineTop.size());
					cell.setCellStyle(headingStyle);
					cell.setCellValue(heading.get(i));
				}

			}

			index++;
			row = sheet_01.createRow(index);
			row.setHeightInPoints(25);
			int final_index = 0;
			int headerLineTop_Cnt = 0;

			for (headerLineTop_Cnt = 0; headerLineTop_Cnt < headerLineTop.size(); headerLineTop_Cnt++) {
				XSSFCell cell = row.createCell(headerLineTop_Cnt);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(headerLineTop_Cnt));
			}

			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT DESCRIPTION || '(問題' || PRIORITY || ')' AS QST_DESC ");
			sql.append("FROM TBSQM_CSM_QUESTION Q ");
			sql.append("INNER JOIN ( ");
			sql.append("  SELECT DISTINCT QUESTIONNAIRE_ID ");
			sql.append("  FROM TBSQM_CSM_QUESTION ");
			sql.append("  WHERE UPDATE_DATE = (SELECT MAX(UPDATE_DATE) FROM TBSQM_CSM_QUESTION WHERE QTN_TYPE = 'WMS01') ");
			sql.append(") NEW ON NEW.QUESTIONNAIRE_ID = Q.QUESTIONNAIRE_ID ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT NULL AS BRANCH_NBR, NULL AS TRADE_DATE, NULL AS RESP_DATE ");
			sql.append("  FROM DUAL ");
			sql.append(") A ON 1 = 1 ");
			sql.append("WHERE QTN_TYPE = 'WMS01' ");	
			sql.append("ORDER BY PRIORITY ");
			
			condition.setQueryString(sql.toString());

			List<Map<String, Object>> qst_list = dam.exeQuery(condition);

			if (!qst_list.isEmpty()) {
				for (Map<String, Object> dataMap : qst_list) {
					XSSFCell cell = row.createCell(headerLineTop_Cnt++);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(ObjectUtils.toString(dataMap.get("QST_DESC")));
					sheet_01.autoSizeColumn(headerLineTop_Cnt);
				}

				//				resp_index = headerLineTop_Cnt++;
				//				XSSFCell cell = row.createCell(resp_index);
				//				cell.setCellStyle(headingStyle);
				//				cell.setCellValue("滿意質化原因");

				final_index = headerLineTop_Cnt++;
				
				XSSFCell cell = row.createCell(final_index);
				cell.setCellStyle(headingStyle);
				cell.setCellValue("調查結果");
				
				//合併問題動態欄位
				sheet_01.addMergedRegion(new CellRangeAddress(0, 0, headerLineTop.size(), headerLineTop_Cnt - 1));
			}

			//固定欄位
			List<Map<Integer, Object>> detailList = new ArrayList<Map<Integer, Object>>();
			Map map = new HashMap();
			int detailColumn = 0;
			map.put(detailColumn++, "REGION_CENTER_NAME");
			map.put(detailColumn++, "BRANCH_AREA_NAME");
			map.put(detailColumn++, "UHRM_DEPT_NAME");
			map.put(detailColumn++, "BRANCH_NAME");
			map.put(detailColumn++, "AO_CODE");
			map.put(detailColumn++, "EMP_NAME");
			map.put(detailColumn++, "EMP_ID");
			map.put(detailColumn++, "CUST_ID");
			map.put(detailColumn++, "TRADE_DATE");
			map.put(detailColumn++, "RESP_DATE");
			detailList.add(map);
			
			//資料開始
			index++;

			int detail = index;

			for (Map<String, Object> dataMap : list_01) {
				row = sheet_01.createRow(detail++);
				
				//前8固定欄位 
				for (int j = 0; j < detailList.get(0).size(); j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(style);
					
					if (detailList.get(0).get(j).equals("BRANCH_NAME")) {
						String bra_name = ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)));
						String bra_nbr = ObjectUtils.toString(dataMap.get("BRANCH_NBR"));
						cell.setCellValue(bra_nbr.equals("") ? "" : bra_nbr + "-" + bra_name);
					} else if (detailList.get(0).get(j).equals("CUST_ID")) {
						cell.setCellValue(dfObj.maskID(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)))));
					} else {
						cell.setCellValue(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j))));
					}
				}
				
				//此問題動態欄位
				List<Map<String, String>> ansList = getQuestionList(ObjectUtils.toString(dataMap.get("QTN_TYPE")), ObjectUtils.toString(dataMap.get("DATA_DATE")), ObjectUtils.toString(dataMap.get("CUST_ID")));
				int i = detailList.get(0).size(); //從前面固定欄位開始往後增加

				if (!qst_list.isEmpty()) {
					for (int a = 0; a < qst_list.size(); a++) {
						String ans = "";//答案
						int order = a + 1;//題號

						for (Map<String, String> answerMap : ansList) {
							Object o = answerMap.get("PRIORITY");
							
							//答案應該對應放置在第幾題
							int qst_order = Integer.parseInt(o.toString());
							
							if (order == qst_order) {
								//客戶要求將資料裡有{{}}部分濾掉，這裡找尋第一個{的index，若找不到則擷取全部資料
								if (StringUtils.isNotBlank(answerMap.get("ANSWER"))) {
									int find_index = answerMap.get("ANSWER").indexOf("{") == -1 ? answerMap.get("ANSWER").length() : answerMap.get("ANSWER").indexOf("{");
									ans = ObjectUtils.toString(answerMap.get("ANSWER")).substring(0, find_index);
								}
							}
						}

						XSSFCell cell = row.createCell(i + a);
						cell.setCellStyle(style);
						cell.setCellValue(ans.replace("multi", ""));
					}

					XSSFCell cell = row.createCell(final_index);
					cell.setCellStyle(style);
					
					String Final_Desc = "";
					if ("Y".equals(ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")))) {
						Final_Desc = "扣分";
					} else if ("N".equals(ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")))) {
						Final_Desc = "不扣分";
					}
					
					cell.setCellValue(Final_Desc);
				}
			}
		}

		if (!list_02.isEmpty()) {
			//設定SHEET名稱
			XSSFSheet sheet_02 = wb.createSheet("投資保險滿意度問卷");
			sheet_02.setDefaultColumnWidth(20);
			sheet_02.setDefaultRowHeightInPoints(20);

			Integer index = 0; // 行數

			List<String> heading = new ArrayList<String>();

			heading.add("(二)投資/保險滿意度問卷(非常滿意/滿意/普通/不滿意/非常不滿意)");
			heading.add("問題");

			List<String> headerLineTop = new ArrayList<String>();

			headerLineTop.add("業務處");
			headerLineTop.add("營運區");
			headerLineTop.add("私銀區");
			headerLineTop.add("分行別");
			headerLineTop.add("AO_CODE");
			headerLineTop.add("RM/SRM姓名");
			headerLineTop.add("RM/SRM員工編號");
			headerLineTop.add("客戶ID");
			headerLineTop.add("交易日期");
			headerLineTop.add("填寫日期");

			// Heading
			XSSFRow row = sheet_02.createRow(index);

			for (int i = 0; i < heading.size(); i++) {
				if (i == 0) {
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(heading.get(i));
					sheet_02.addMergedRegion(new CellRangeAddress(0, 0, 0, headerLineTop.size() - 1));
				} else {
					XSSFCell cell = row.createCell(headerLineTop.size());
					cell.setCellStyle(headingStyle);
					cell.setCellValue(heading.get(i));
				}

			}

			index++;
			row = sheet_02.createRow(index);
			row.setHeightInPoints(25);
			int resp_index = 0;
			int final_index = 0;
			int headerLineTop_Cnt = 0;
			for (headerLineTop_Cnt = 0; headerLineTop_Cnt < headerLineTop.size(); headerLineTop_Cnt++) {
				XSSFCell cell = row.createCell(headerLineTop_Cnt);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(headerLineTop_Cnt));
			}

			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT DESCRIPTION || '(問題' || PRIORITY || ')' AS QST_DESC ");
			sql.append("FROM TBSQM_CSM_QUESTION Q ");
			sql.append("INNER JOIN ( ");
			sql.append("  SELECT DISTINCT QUESTIONNAIRE_ID ");
			sql.append("  FROM TBSQM_CSM_QUESTION ");
			sql.append("  WHERE UPDATE_DATE = (SELECT MAX(UPDATE_DATE) FROM TBSQM_CSM_QUESTION WHERE QTN_TYPE = 'WMS02') ");
			sql.append(") NEW ON NEW.QUESTIONNAIRE_ID = Q.QUESTIONNAIRE_ID ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT NULL AS BRANCH_NBR, NULL AS TRADE_DATE, NULL AS RESP_DATE ");
			sql.append("  FROM DUAL ");
			sql.append(") A ON 1 = 1 ");
			sql.append("WHERE QTN_TYPE = 'WMS02' ");
			sql.append("ORDER BY PRIORITY ");
			
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> qst_list = dam.exeQuery(condition);

			if (!qst_list.isEmpty()) {
				for (Map<String, Object> dataMap : qst_list) {
					XSSFCell cell = row.createCell(headerLineTop_Cnt++);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(ObjectUtils.toString(dataMap.get("QST_DESC")));
					sheet_02.autoSizeColumn(headerLineTop_Cnt);
				}

				//				resp_index = headerLineTop_Cnt++;
				//				XSSFCell cell = row.createCell(resp_index);
				//				cell.setCellStyle(headingStyle);
				//				cell.setCellValue("滿意質化原因");

				final_index = headerLineTop_Cnt++;
				
				XSSFCell cell = row.createCell(final_index);
				cell.setCellStyle(headingStyle);
				cell.setCellValue("調查結果");

				//合併問題動態欄位
				sheet_02.addMergedRegion(new CellRangeAddress(0, 0, headerLineTop.size(), headerLineTop_Cnt - 1));
			}

			//固定欄位
			List<Map<Integer, Object>> detailList = new ArrayList<Map<Integer, Object>>();
			Map map = new HashMap();
			int detailColumn = 0;
			map.put(detailColumn++, "REGION_CENTER_NAME");
			map.put(detailColumn++, "BRANCH_AREA_NAME");
			map.put(detailColumn++, "UHRM_DEPT_NAME");
			map.put(detailColumn++, "BRANCH_NAME");
			map.put(detailColumn++, "AO_CODE");
			map.put(detailColumn++, "EMP_NAME");
			map.put(detailColumn++, "EMP_ID");
			map.put(detailColumn++, "CUST_ID");
			map.put(detailColumn++, "TRADE_DATE");
			map.put(detailColumn++, "RESP_DATE");
			detailList.add(map); //E
			//資料開始
			index++;

			int detail = index;

			for (Map<String, Object> dataMap : list_02) {
				row = sheet_02.createRow(detail++);
				
				//前8固定欄位 
				for (int j = 0; j < detailList.get(0).size(); j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(style);
					
					if (detailList.get(0).get(j).equals("BRANCH_NAME")) {
						String bra_name = ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)));
						String bra_nbr = ObjectUtils.toString(dataMap.get("BRANCH_NBR"));
						cell.setCellValue(bra_nbr.equals("") ? "" : bra_nbr + "-" + bra_name);
					} else if (detailList.get(0).get(j).equals("CUST_ID")) {
						cell.setCellValue(dfObj.maskID(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)))));
					} else {
						cell.setCellValue(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j))));
					}
				}
				
				//後面為動態欄位
				List<Map<String, String>> ansList = getQuestionList(ObjectUtils.toString(dataMap.get("QTN_TYPE")), ObjectUtils.toString(dataMap.get("DATA_DATE")), ObjectUtils.toString(dataMap.get("CUST_ID")));
				int i = detailList.get(0).size(); //從前面固定欄位開始往後增加

				if (!qst_list.isEmpty()) {
					for (int a = 0; a < qst_list.size(); a++) {

						String ans = "";//答案
						int order = a + 1;//題號

						for (Map<String, String> answerMap : ansList) {
							Object o = answerMap.get("PRIORITY");
							
							//答案應該對應放置在第幾題
							int qst_order = Integer.parseInt(o.toString());
							
							if (order == qst_order) {
								//客戶要求將資料裡有{{}}部分濾掉，這裡找尋第一個{的index，若找不到則擷取全部資料
								if (StringUtils.isNotBlank(answerMap.get("ANSWER"))) {
									int find_index = answerMap.get("ANSWER").indexOf("{") == -1 ? answerMap.get("ANSWER").length() : answerMap.get("ANSWER").indexOf("{");
									ans = ObjectUtils.toString(answerMap.get("ANSWER")).substring(0, find_index);
								}
							}
						}

						XSSFCell cell = row.createCell(i + a);
						cell.setCellStyle(style);
						cell.setCellValue(ans.replace("multi", ""));
					}

					//					XSSFCell cell = row.createCell(resp_index);
					//					cell.setCellStyle(style);
					//					cell.setCellValue(ObjectUtils.toString(dataMap.get("RESP_NOTE")));

					XSSFCell cell = row.createCell(final_index);
					cell.setCellStyle(style);
					
					String Final_Desc = "";
					if ("Y".equals(ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")))) {
						Final_Desc = "扣分";
					} else if ("N".equals(ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")))) {
						Final_Desc = "不扣分";
					}
					
					cell.setCellValue(Final_Desc);
				}
			}
		}

		if (!list_03.isEmpty()) {
			//設定SHEET名稱
			XSSFSheet sheet_03 = wb.createSheet("櫃檯滿意度問卷");
			sheet_03.setDefaultColumnWidth(20);
			sheet_03.setDefaultRowHeightInPoints(20);

			Integer index = 0; // 行數

			List<String> heading = new ArrayList<String>();

			heading.add("(三)櫃檯滿意度問卷(非常滿意/滿意/普通/不滿意/非常不滿意)");
			heading.add("問題");

			List<String> headerLineTop = new ArrayList<String>();

			headerLineTop.add("業務處");
			headerLineTop.add("營運區");
			headerLineTop.add("私銀區");
			headerLineTop.add("分行別");
			headerLineTop.add("員工編號");
			headerLineTop.add("員工姓名");
			headerLineTop.add("客戶ID");
			headerLineTop.add("交易日期");
			headerLineTop.add("填寫日期");

			// Heading
			XSSFRow row = sheet_03.createRow(index);

			for (int i = 0; i < heading.size(); i++) {
				if (i == 0) {
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(heading.get(i));
					sheet_03.addMergedRegion(new CellRangeAddress(0, 0, 0, headerLineTop.size() - 1));
				} else {
					XSSFCell cell = row.createCell(headerLineTop.size());
					cell.setCellStyle(headingStyle);
					cell.setCellValue(heading.get(i));
				}

			}

			index++;
			row = sheet_03.createRow(index);
			row.setHeightInPoints(25);
			int resp_index = 0;
			int final_index = 0;
			int headerLineTop_Cnt = 0;
			for (headerLineTop_Cnt = 0; headerLineTop_Cnt < headerLineTop.size(); headerLineTop_Cnt++) {
				XSSFCell cell = row.createCell(headerLineTop_Cnt);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(headerLineTop_Cnt));
			}

			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			
			sql.append("SELECT DESCRIPTION || '(問題' || PRIORITY || ')' AS QST_DESC ");
			sql.append("FROM TBSQM_CSM_QUESTION Q ");
			sql.append("INNER JOIN ( ");
			sql.append("  SELECT DISTINCT QUESTIONNAIRE_ID ");
			sql.append("  FROM TBSQM_CSM_QUESTION ");
			sql.append("  WHERE UPDATE_DATE = (SELECT MAX(UPDATE_DATE) FROM TBSQM_CSM_QUESTION WHERE QTN_TYPE = 'WMS03') ");
			sql.append(") NEW ON NEW.QUESTIONNAIRE_ID = Q.QUESTIONNAIRE_ID ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT NULL AS BRANCH_NBR, NULL AS TRADE_DATE, NULL AS RESP_DATE ");
			sql.append("  FROM DUAL ");
			sql.append(") A ON 1 = 1 ");
			sql.append("WHERE QTN_TYPE = 'WMS03' ");
			sql.append("ORDER BY PRIORITY ");

			condition.setQueryString(sql.toString());
			List<Map<String, Object>> qst_list = dam.exeQuery(condition);

			if (!qst_list.isEmpty()) {
				for (Map<String, Object> dataMap : qst_list) {
					XSSFCell cell = row.createCell(headerLineTop_Cnt++);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(ObjectUtils.toString(dataMap.get("QST_DESC")));
					sheet_03.autoSizeColumn(headerLineTop_Cnt);
				}

				//				resp_index = headerLineTop_Cnt++;
				//				XSSFCell cell = row.createCell(resp_index);
				//				cell.setCellStyle(headingStyle);
				//				cell.setCellValue("滿意質化原因");

				final_index = headerLineTop_Cnt++;
				
				XSSFCell cell = row.createCell(final_index);
				cell.setCellStyle(headingStyle);
				cell.setCellValue("調查結果");

				//合併問題動態欄位
				sheet_03.addMergedRegion(new CellRangeAddress(0, 0, headerLineTop.size(), headerLineTop_Cnt - 1));
			}

			List<Map<Integer, Object>> detailList = new ArrayList<Map<Integer, Object>>();
			Map map = new HashMap();
			int detailColumn = 0;
			map.put(detailColumn++, "REGION_CENTER_NAME");
			map.put(detailColumn++, "BRANCH_AREA_NAME");
			map.put(detailColumn++, "UHRM_DEPT_NAME");
			map.put(detailColumn++, "BRANCH_NAME");
			map.put(detailColumn++, "EMP_ID");
			map.put(detailColumn++, "EMP_NAME");
			map.put(detailColumn++, "CUST_ID");
			map.put(detailColumn++, "TRADE_DATE");
			map.put(detailColumn++, "RESP_DATE");
			detailList.add(map); //E
			
			//資料開始
			index++;

			int detail = index;

			for (Map<String, Object> dataMap : list_03) {
				row = sheet_03.createRow(detail++);
				//固定欄位 
				for (int j = 0; j < detailList.get(0).size(); j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(style);
					
					if (detailList.get(0).get(j).equals("BRANCH_NAME")) {
						String bra_name = ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)));
						String bra_nbr = ObjectUtils.toString(dataMap.get("BRANCH_NBR"));
						cell.setCellValue(bra_nbr.equals("") ? "" : bra_nbr + "-" + bra_name);
					} else if (detailList.get(0).get(j).equals("CUST_ID")) {
						cell.setCellValue(dfObj.maskID(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)))));
					} else {
						cell.setCellValue(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j))));
					}
				}
				//後面為動態欄位
				List<Map<String, String>> ansList = getQuestionList(ObjectUtils.toString(dataMap.get("QTN_TYPE")), ObjectUtils.toString(dataMap.get("DATA_DATE")), ObjectUtils.toString(dataMap.get("CUST_ID")));
				//				String ansList [] = ObjectUtils.toString(dataMap.get("ANSWER")).split(";");
				int i = detailList.get(0).size(); //從前面固定欄位開始往後增加

				if (!qst_list.isEmpty()) {
					for (int a = 0; a < qst_list.size(); a++) {
						String ans = "";//答案
						int order = a + 1;//題號

						for (Map<String, String> answerMap : ansList) {
							Object o = answerMap.get("PRIORITY");
							
							//答案應該對應放置在第幾題
							int qst_order = Integer.parseInt(o.toString());
							
							if (order == qst_order) {
								//客戶要求將資料裡有{{}}部分濾掉，這裡找尋第一個{的index，若找不到則擷取全部資料
								if (StringUtils.isNotBlank(answerMap.get("ANSWER"))) {
									int find_index = answerMap.get("ANSWER").indexOf("{") == -1 ? answerMap.get("ANSWER").length() : answerMap.get("ANSWER").indexOf("{");
									ans = ObjectUtils.toString(answerMap.get("ANSWER")).substring(0, find_index);
								}
							}
						}

						XSSFCell cell = row.createCell(i + a);
						cell.setCellStyle(style);
						cell.setCellValue(ans.replace("multi", ""));
					}

					//					XSSFCell cell = row.createCell(resp_index);
					//					cell.setCellStyle(style);
					//					cell.setCellValue(ObjectUtils.toString(dataMap.get("RESP_NOTE")));

					XSSFCell cell = row.createCell(final_index);
					cell.setCellStyle(style);
					
					String Final_Desc = "";
					if ("Y".equals(ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")))) {
						Final_Desc = "扣分";
					} else if ("N".equals(ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")))) {
						Final_Desc = "不扣分";
					}
					
					cell.setCellValue(Final_Desc);
				}
			}
		}

		if (!list_04.isEmpty()) {
			//設定SHEET名稱
			XSSFSheet sheet_04 = wb.createSheet("開戶滿意度問卷");
			sheet_04.setDefaultColumnWidth(20);
			sheet_04.setDefaultRowHeightInPoints(20);

			Integer index = 0; // 行數

			List<String> heading = new ArrayList<String>();

			heading.add("(四)開戶滿意度問卷(非常滿意/滿意/普通/不滿意/非常不滿意)");
			heading.add("問題");

			List<String> headerLineTop = new ArrayList<String>();

			headerLineTop.add("業務處");
			headerLineTop.add("營運區");
			headerLineTop.add("私銀區");
			headerLineTop.add("分行別");
			headerLineTop.add("員工編號");
			headerLineTop.add("員工姓名");
			headerLineTop.add("客戶ID");
			headerLineTop.add("交易日期");
			headerLineTop.add("填寫日期");

			// Heading
			XSSFRow row = sheet_04.createRow(index);

			for (int i = 0; i < heading.size(); i++) {
				if (i == 0) {
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(heading.get(i));
					sheet_04.addMergedRegion(new CellRangeAddress(0, 0, 0, headerLineTop.size() - 1));
				} else {
					XSSFCell cell = row.createCell(headerLineTop.size());
					cell.setCellStyle(headingStyle);
					cell.setCellValue(heading.get(i));
				}

			}

			index++;
			row = sheet_04.createRow(index);
			row.setHeightInPoints(25);
			int resp_index = 0;
			int final_index = 0;
			int headerLineTop_Cnt = 0;
			for (headerLineTop_Cnt = 0; headerLineTop_Cnt < headerLineTop.size(); headerLineTop_Cnt++) {
				XSSFCell cell = row.createCell(headerLineTop_Cnt);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(headerLineTop_Cnt));
			}

			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT DESCRIPTION || '(問題' || PRIORITY || ')' AS QST_DESC ");
			sql.append("FROM TBSQM_CSM_QUESTION Q ");
			sql.append("INNER JOIN ( ");
			sql.append("  SELECT DISTINCT QUESTIONNAIRE_ID ");
			sql.append("  FROM TBSQM_CSM_QUESTION ");
			sql.append("  WHERE UPDATE_DATE = (SELECT MAX(UPDATE_DATE) FROM TBSQM_CSM_QUESTION WHERE QTN_TYPE = 'WMS04') ");
			sql.append(") NEW ON NEW.QUESTIONNAIRE_ID = Q.QUESTIONNAIRE_ID ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT NULL AS BRANCH_NBR, NULL AS TRADE_DATE, NULL AS RESP_DATE ");
			sql.append("  FROM DUAL ");
			sql.append(") A ON 1 = 1 ");
			sql.append("WHERE QTN_TYPE = 'WMS04' ");
			sql.append("ORDER BY PRIORITY ");
			
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> qst_list = dam.exeQuery(condition);

			if (!qst_list.isEmpty()) {
				for (Map<String, Object> dataMap : qst_list) {
					XSSFCell cell = row.createCell(headerLineTop_Cnt++);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(ObjectUtils.toString(dataMap.get("QST_DESC")));
					sheet_04.autoSizeColumn(headerLineTop_Cnt);
				}

				//				resp_index = headerLineTop_Cnt++;
				//				XSSFCell cell = row.createCell(resp_index);
				//				cell.setCellStyle(headingStyle);
				//				cell.setCellValue("滿意質化原因");

				final_index = headerLineTop_Cnt++;
				XSSFCell cell = row.createCell(final_index);
				cell.setCellStyle(headingStyle);
				cell.setCellValue("調查結果");

				//合併問題動態欄位
				sheet_04.addMergedRegion(new CellRangeAddress(0, 0, headerLineTop.size(), headerLineTop_Cnt - 1));
			}

			List<Map<Integer, Object>> detailList = new ArrayList<Map<Integer, Object>>();
			Map map = new HashMap();
			int detailColumn = 0;
			map.put(detailColumn++, "REGION_CENTER_NAME");
			map.put(detailColumn++, "BRANCH_AREA_NAME");
			map.put(detailColumn++, "UHRM_DEPT_NAME");
			map.put(detailColumn++, "BRANCH_NAME");
			map.put(detailColumn++, "EMP_ID");
			map.put(detailColumn++, "EMP_NAME");
			map.put(detailColumn++, "CUST_ID");
			map.put(detailColumn++, "TRADE_DATE");
			map.put(detailColumn++, "RESP_DATE");
			detailList.add(map); //E
			//資料開始
			index++;

			int detail = index;

			for (Map<String, Object> dataMap : list_04) {
				row = sheet_04.createRow(detail++);
				
				//前8固定欄位 
				for (int j = 0; j < detailList.get(0).size(); j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(style);
					
					if (detailList.get(0).get(j).equals("BRANCH_NAME")) {
						String bra_name = ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)));
						String bra_nbr = ObjectUtils.toString(dataMap.get("BRANCH_NBR"));
						cell.setCellValue(bra_nbr.equals("") ? "" : bra_nbr + "-" + bra_name);
					} else if (detailList.get(0).get(j).equals("CUST_ID")) {
						cell.setCellValue(dfObj.maskID(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)))));
					} else {
						cell.setCellValue(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j))));
					}
				}
				
				//後面為動態欄位
				List<Map<String, String>> ansList = getQuestionList(ObjectUtils.toString(dataMap.get("QTN_TYPE")), ObjectUtils.toString(dataMap.get("DATA_DATE")), ObjectUtils.toString(dataMap.get("CUST_ID")));
				int i = detailList.get(0).size(); //從前面固定欄位開始往後增加

				if (!qst_list.isEmpty()) {
					for (int a = 0; a < qst_list.size(); a++) {
						String ans = "";//答案
						int order = a + 1;//題號

						for (Map<String, String> answerMap : ansList) {
							Object o = answerMap.get("PRIORITY");
							
							//答案應該對應放置在第幾題
							int qst_order = Integer.parseInt(o.toString());
							
							if (order == qst_order) {
								//客戶要求將資料裡有{{}}部分濾掉，這裡找尋第一個{的index，若找不到則擷取全部資料
								if (StringUtils.isNotBlank(answerMap.get("ANSWER"))) {
									int find_index = answerMap.get("ANSWER").indexOf("{") == -1 ? answerMap.get("ANSWER").length() : answerMap.get("ANSWER").indexOf("{");
									ans = ObjectUtils.toString(answerMap.get("ANSWER")).substring(0, find_index);
								}
							}
						}

						XSSFCell cell = row.createCell(i + a);
						cell.setCellStyle(style);
						cell.setCellValue(ans.replace("multi", ""));
					}

					//					XSSFCell cell = row.createCell(resp_index);
					//					cell.setCellStyle(style);
					//					cell.setCellValue(ObjectUtils.toString(dataMap.get("RESP_NOTE")));

					XSSFCell cell = row.createCell(final_index);
					cell.setCellStyle(style);
					
					String Final_Desc = "";
					if ("Y".equals(ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")))) {
						Final_Desc = "扣分";
					} else if ("N".equals(ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")))) {
						Final_Desc = "不扣分";
					}
					
					cell.setCellValue(Final_Desc);
				}
			}
		}

		if (!list_05.isEmpty()) {
			//設定SHEET名稱
			XSSFSheet sheet_05 = wb.createSheet("簡訊滿意度問卷");
			sheet_05.setDefaultColumnWidth(20);
			sheet_05.setDefaultRowHeightInPoints(20);

			Integer index = 0; // 行數

			List<String> heading = new ArrayList<String>();

			heading.add("(五)簡訊滿意度問卷(非常滿意/滿意/普通/不滿意/非常不滿意/未聯繫)");

			List<String> headerLineTop = new ArrayList<String>();

			headerLineTop.add("業務處");
			headerLineTop.add("營運區");
			headerLineTop.add("私銀區");
			headerLineTop.add("分行別");
			headerLineTop.add("員工編號");
			headerLineTop.add("員工姓名");
			headerLineTop.add("AO_CODE");
			headerLineTop.add("題目(簡訊內文)");
			headerLineTop.add("客戶姓名");
			headerLineTop.add("客戶ID");
			headerLineTop.add("手機號碼");
			headerLineTop.add("客戶回應");
			headerLineTop.add("客戶回應日");
			headerLineTop.add("問卷發送日");
			headerLineTop.add("活動代碼");
			headerLineTop.add("調查結果");

			// Heading
			XSSFRow row = sheet_05.createRow(index);

			for (int i = 0; i < heading.size(); i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(heading.get(i));
				sheet_05.addMergedRegion(new CellRangeAddress(0, 0, 0, headerLineTop.size() - 1));
			}

			index++;
			row = sheet_05.createRow(index);
			row.setHeightInPoints(25);
			int resp_index = 0;
			int final_index = 0;
			int headerLineTop_Cnt = 0;
			for (headerLineTop_Cnt = 0; headerLineTop_Cnt < headerLineTop.size(); headerLineTop_Cnt++) {
				XSSFCell cell = row.createCell(headerLineTop_Cnt);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(headerLineTop_Cnt));
			}

			List<Map<Integer, Object>> detailList = new ArrayList<Map<Integer, Object>>();
			Map map = new HashMap();
			int detailColumn = 0;
			map.put(detailColumn++, "REGION_CENTER_NAME");
			map.put(detailColumn++, "BRANCH_AREA_NAME");
			map.put(detailColumn++, "UHRM_DEPT_NAME");
			map.put(detailColumn++, "BRANCH_NAME");
			map.put(detailColumn++, "EMP_ID");
			map.put(detailColumn++, "EMP_NAME");
			map.put(detailColumn++, "AO_CODE");
			map.put(detailColumn++, "QUESTION_DESC");
			map.put(detailColumn++, "CUST_NAME");
			map.put(detailColumn++, "CUST_ID");
			map.put(detailColumn++, "MOBILE_NO");
			map.put(detailColumn++, "ANSWER");
			map.put(detailColumn++, "RESP_DATE");
			map.put(detailColumn++, "SEND_DATE");
			map.put(detailColumn++, "CAMPAIGN_ID");
			map.put(detailColumn++, "DEDUCTION_FINAL");
			detailList.add(map);

			//資料開始
			index++;

			int detail = index;

			for (Map<String, Object> dataMap : list_05) {

				row = sheet_05.createRow(detail++);
				//固定欄位 
				for (int j = 0; j < detailList.get(0).size(); j++) {
					if (j < detailList.get(0).size() - 1) {
						XSSFCell cell = row.createCell(j);
						cell.setCellStyle(style);
						
						if (detailList.get(0).get(j).equals("ANSWER")) {//答案需要轉成對應的中文
							cell.setCellValue(ansMap.get(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)))));
						} else if (detailList.get(0).get(j).equals("BRANCH_NAME")) {
							String bra_name = ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)));
							String bra_nbr = ObjectUtils.toString(dataMap.get("BRANCH_NBR"));
							cell.setCellValue(bra_nbr.equals("") ? "" : bra_nbr + "-" + bra_name);
						} else if (detailList.get(0).get(j).equals("CUST_ID")) {
							cell.setCellValue(dfObj.maskID(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)))));
						} else if (detailList.get(0).get(j).equals("CUST_NAME")) {
							cell.setCellValue(dfObj.maskName(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)))));
						} else if (detailList.get(0).get(j).equals("MOBILE_NO")) {
							cell.setCellValue(dfObj.maskCellPhone(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j)))));
						} else {
							cell.setCellValue(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j))));
						}

					} else {//最後一欄需轉換
						XSSFCell cell = row.createCell(j);
						cell.setCellStyle(style);
						String Final_Desc = "";
						if ("Y".equals(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j))))) {
							Final_Desc = "扣分";
						} else if ("N".equals(ObjectUtils.toString(dataMap.get(detailList.get(0).get(j))))) {
							Final_Desc = "不扣分";
						}
						cell.setCellValue(Final_Desc);
					}
				}
				sheet_05.autoSizeColumn(5);

			}

		}

		String tempName = UUID.randomUUID().toString();
		
		// 路徑結合
		File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
		
		// 絕對路徑建檔
		wb.write(new FileOutputStream(f));

		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName);

		this.sendRtnObject(null);
	}

	//下載	
	public void download(Object body, IPrimitiveMap header) throws Exception {
		
		SQM110InputVO inputVO = (SQM110InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String longinID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> ansMap = xmlInfo.doGetVariable("SQM.ANS_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> qtnList = xmlInfo.doGetVariable("SQM.QTN_TYPE", FormatHelper.FORMAT_3); // 問卷類型
		
		String str_case = "";
		int runCnt = 0;
		for (Map<String, Object> map : inputVO.getCheckList()) {

			if (inputVO.getCheckList().size() > 1 && (runCnt + 1) != inputVO.getCheckList().size()) {
				str_case = str_case + "'" + ObjectUtils.toString(map.get("CASE_NO")) + "',";
			} else {
				str_case = str_case + "'" + ObjectUtils.toString(map.get("CASE_NO")) + "'";
			}
			runCnt++;
		}

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select D.* , ");
		sql.append("       A.SEQ, ");
		sql.append("       A.DATA_DATE, ");
		sql.append("       A.QTN_TYPE, ");
		sql.append("       A.BRANCH_NBR, ");
		sql.append("       ORG.BRANCH_NAME, ");
		sql.append("       A.TRADE_DATE, ");
		sql.append("       A.CUST_ID, ");
		sql.append("       A.CUST_NAME, ");
		sql.append("       A.RESP_NOTE, ");
		sql.append("       A.QUESTION_DESC, ");
		sql.append("       A.MOBILE_NO, ");
		sql.append("       A.ANSWER, ");
		sql.append("       A.DEDUCTION_FINAL ");
		sql.append("from TBSQM_CSM_IMPROVE_MAST A ");
		sql.append("INNER JOIN TBSQM_CSM_IMPROVE_DTL D ON A.CASE_NO = D.CASE_NO ");
		sql.append("LEFT JOIN TBPMS_ORG_REC_N ORG ON ORG.dept_id = A.BRANCH_NBR AND to_date(A.TRADE_DATE,'yyyymmdd') between ORG.START_TIME and ORG.END_TIME ");
		sql.append("where D.CASE_NO in (" + str_case + ") ");
		sql.append("and A.SATISFACTION_W IN ('4','5','6') ");
		sql.append("AND A.DELETE_FLAG is null ");
		sql.append("and A.HO_CHECK = 'Y' ");

		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);

		String url = "";
		String txnCode = "SQM110";
		String reportID = "R1";
		ReportIF report = null;

		ArrayList<String> url_list = new ArrayList<String>();
		DataFormat dfObj = new DataFormat();

		try {
			for (Map<String, Object> dataMap : list) {
				ReportFactory factory = new ReportFactory();
				ReportDataIF data = new ReportData();//取得傳輸資料給report模組的instance
				ReportGeneratorIF gen = factory.getGenerator(); //取得產生PDF檔的instance
				String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				
				//將值帶入報表欄位中
				data.addParameter("QTN_DESC", qtnList.get(ObjectUtils.toString(dataMap.get("QTN_TYPE"))));
				data.addParameter("BRANCH_NBR", ObjectUtils.toString(dataMap.get("BRANCH_NBR")).equals("") ? "" : ObjectUtils.toString(dataMap.get("BRANCH_NBR")) + "-" + ObjectUtils.toString(dataMap.get("BRANCH_NAME")));
				data.addParameter("TRADE_DATE", ObjectUtils.toString(dataMap.get("TRADE_DATE")));
				data.addParameter("CUST_ID", dfObj.maskID(ObjectUtils.toString(dataMap.get("CUST_ID"))));
				data.addParameter("CUST_NAME", dfObj.maskName(ObjectUtils.toString(dataMap.get("CUST_NAME"))));
				data.addParameter("RESP_NOTE", ObjectUtils.toString(dataMap.get("RESP_NOTE")) == "" ? "無質化原因" : ObjectUtils.toString(dataMap.get("RESP_NOTE")));
				data.addParameter("BRH_DESC", ObjectUtils.toString(dataMap.get("BRH_DESC")));
				data.addParameter("EMP_NAME", ObjectUtils.toString(dataMap.get("EMP_NAME")));
				data.addParameter("EMP_ID", ObjectUtils.toString(dataMap.get("EMP_ID")));
				data.addParameter("CUR_JOB", ObjectUtils.toString(dataMap.get("CUR_JOB")));
				data.addParameter("CUR_JOB_Y", ObjectUtils.toString(dataMap.get("CUR_JOB_Y")));
				data.addParameter("CUR_JOB_M", ObjectUtils.toString(dataMap.get("CUR_JOB_M")));
				data.addParameter("SUP_EMP_ID", ObjectUtils.toString(dataMap.get("SUP_EMP_ID")));
				data.addParameter("SUP_EMP_NAME", ObjectUtils.toString(dataMap.get("SUP_EMP_NAME")));
				data.addParameter("SUP_CUR_JOB", ObjectUtils.toString(dataMap.get("SUP_CUR_JOB")));

				data.addParameter("IMPROVE_DESC", ObjectUtils.toString(dataMap.get("IMPROVE_DESC")));
				data.addParameter("OP_SUP_REMARK", ObjectUtils.toString(dataMap.get("OP_SUP_REMARK")));
				data.addParameter("RC_VICE_SUP_REMARK", ObjectUtils.toString(dataMap.get("RC_VICE_SUP_REMARK")));
				data.addParameter("LAST_VISIT_DATE", ObjectUtils.toString(dataMap.get("LAST_VISIT_DATE")));
				data.addParameter("CON_DEGREE", ObjectUtils.toString(dataMap.get("CON_DEGREE")));
				data.addParameter("FRQ_DAY", ObjectUtils.toString(dataMap.get("FRQ_DAY")));
				
				String deduction_inital_desc = ObjectUtils.toString(dataMap.get("DEDUCTION_INITIAL")).equals("Y") ? "扣分" : ObjectUtils.toString(dataMap.get("DEDUCTION_INITIAL")).equals("N") ? "不扣分" : "";
				data.addParameter("DEDUCTION_INITIAL", deduction_inital_desc);
				
				data.addParameter("DEDUCTION_FINAL", ObjectUtils.toString(dataMap.get("DEDUCTION_FINAL")));
				
				String rc_vice_sup_remark_desc = "";
				String rc_sup_remark_desc = "";
				if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get("RC_VICE_SUP_REMARK")))) {
					rc_vice_sup_remark_desc = "處副主管: " + ObjectUtils.toString(dataMap.get("RC_VICE_SUP_REMARK"));
				}
				
				if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get("RC_SUP_REMARK")))) {
					if (StringUtils.isNotBlank(rc_vice_sup_remark_desc)) {
						rc_sup_remark_desc = "\n處主管: " + ObjectUtils.toString(dataMap.get("RC_SUP_REMARK"));
					} else {
						rc_sup_remark_desc = "處主管: " + ObjectUtils.toString(dataMap.get("RC_SUP_REMARK"));
					}
				}
				data.addParameter("RC_SUP_REMARK", rc_vice_sup_remark_desc + rc_sup_remark_desc);
				
				data.addParameter("HEADMGR_REMARK", ObjectUtils.toString(dataMap.get("HEADMGR_REMARK")));
				data.addParameter("WAITING_TIME", ObjectUtils.toString(dataMap.get("WAITING_TIME")));
				data.addParameter("WORKING_TIME", ObjectUtils.toString(dataMap.get("WORKING_TIME")));
				data.addParameter("qtn_type", ObjectUtils.toString(dataMap.get("QTN_TYPE")));

				List<Map<String, String>> QA_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> allList = new ArrayList<Map<String, String>>();

				//簡訊問卷
				if ("WMS05".equals(ObjectUtils.toString(dataMap.get("QTN_TYPE")))) {
					Map<String, String> q1_map = new HashMap<String, String>();
					q1_map.put("QST_DESC", "1.客戶手機");
					q1_map.put("ANSWER", dfObj.maskCellPhone(ObjectUtils.toString(dataMap.get("MOBILE_NO"))));
					allList.add(q1_map);
					
					Map<String, String> q2_map = new HashMap<String, String>();
					q2_map.put("QST_DESC", "2.簡訊內容");
					q2_map.put("ANSWER", ObjectUtils.toString(dataMap.get("QUESTION_DESC")));
					allList.add(q2_map);
					
					Map<String, String> q3_map = new HashMap<String, String>();
					q3_map.put("QST_DESC", "3.客戶回覆");
					q3_map.put("ANSWER", ansMap.get(ObjectUtils.toString(dataMap.get("ANSWER"))));
					allList.add(q3_map);
				} else {
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append("select ROWNUM as QST_ORDER, ");
					sql.append("       A.QST_DESC, ");
					sql.append("       A.ANSWER, ");
					sql.append("       A.BRANCH_NBR, ");
					sql.append("       A.TRADE_DATE, ");
					sql.append("       A.RESP_DATE ");
					sql.append("from ( ");
					sql.append("  SELECT Q.PRIORITY AS QST_ORDER, ");
					sql.append("         M.BRANCH_NBR, ");
					sql.append("         M.TRADE_DATE, ");
					sql.append("         M.RESP_DATE, ");
					sql.append("         Q.DESCRIPTION as QST_DESC, ");
					sql.append("         LISTAGG(CASE WHEN Q.PRIORITY = '1' THEN OP.DESCRIPTION ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS01' AND Q.PRIORITY = '11' THEN (SELECT PARAM_NAME || '{{' || OP.SCORE || '}}' FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SQM.ANS_TYPE_PUSH' AND PARAM_CODE = OP.SCORE) ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS01' AND Q.PRIORITY = '12' THEN OP.DESCRIPTION ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS02' AND Q.PRIORITY = '13' THEN (SELECT PARAM_NAME || '{{' || OP.SCORE || '}}' FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SQM.ANS_TYPE_PUSH' AND PARAM_CODE = OP.SCORE) ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS02' AND Q.PRIORITY = '14' THEN OP.DESCRIPTION ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS03' AND Q.PRIORITY = '17' THEN (SELECT PARAM_NAME || '{{' || OP.SCORE || '}}' FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SQM.ANS_TYPE_PUSH' AND PARAM_CODE = OP.SCORE) ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS03' AND Q.PRIORITY = '18' THEN OP.DESCRIPTION ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS04' AND Q.PRIORITY = '13' THEN (SELECT PARAM_NAME || '{{' || OP.SCORE || '}}' FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SQM.ANS_TYPE_PUSH' AND PARAM_CODE = OP.SCORE) ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS04' AND Q.PRIORITY = '14' THEN OP.DESCRIPTION ");
					sql.append("                 ELSE OP.DESCRIPTION END, ';') ");
					sql.append("         WITHIN GROUP(ORDER BY Q.PRIORITY ASC, OP.PRIORITY ASC, Q.DESCRIPTION ASC) AS ANSWER ");
					sql.append("  FROM TBSQM_CSM_ANSWER ANS ");
					sql.append("  INNER JOIN TBSQM_CSM_MAIN M ON M.PAPER_UUID = ANS.PAPER_UUID ");
					sql.append("  LEFT JOIN TBSQM_CSM_OPTIONS OP ON ANS.ITEM_MAPPING_ID = OP.ITEM_MAPPING_ID ");
					sql.append("  LEFT JOIN TBSQM_CSM_QUESTION Q ON ANS.QUESTION_MAPPING_ID = Q.QUESTION_MAPPING_ID ");
					sql.append("  WHERE M.CUST_ID = :CUST_ID ");
					sql.append("  AND M.DATA_DATE = :DATA_DATE ");
					sql.append("  AND M.QTN_TYPE = :QTN_TYPE ");
					sql.append("  AND OP.OPTION_TYPE <> 'O' ");
					sql.append("  AND ANS.QUESTION_MAPPING_ID IS NOT NULL ");
					sql.append("  GROUP BY Q.PRIORITY, M.BRANCH_NBR, M.TRADE_DATE, M.RESP_DATE, Q.DESCRIPTION ");
					sql.append(") A ");
					sql.append("ORDER BY A.QST_ORDER ");
					
					condition.setObject("CUST_ID", ObjectUtils.toString(dataMap.get("CUST_ID")));
					condition.setObject("DATA_DATE", ObjectUtils.toString(dataMap.get("DATA_DATE")));
					condition.setObject("QTN_TYPE", ObjectUtils.toString(dataMap.get("QTN_TYPE")));

					condition.setQueryString(sql.toString());
					
					QA_list = dam.exeQuery(condition);
					
					//取得題號、題目、答案
					for (Map<String, String> map : QA_list) {
						map.put("QST_DESC", ObjectUtils.toString(map.get("QST_DESC")));
						
						//將資料裡面{{數字}}濾掉 
						int index = map.get("ANSWER").indexOf("{") == -1 ? map.get("ANSWER").length() : map.get("ANSWER").indexOf("{");

						map.put("ANSWER", ObjectUtils.toString(map.get("ANSWER")).substring(0, index));

						allList.add(map);
					}
				}
				data.addRecordList("allList", allList);

				report = gen.generateReport(txnCode, reportID, data);

				//多筆PDF做合併的動作
				if (list.size() > 1) {
					url = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports" + System.getProperties().getProperty("file.separator") + new File(report.getLocation()).getName();
					url_list.add(url);
				} else {
					url = report.getLocation();

					String fileName = "滿意度問卷查詢_" + ObjectUtils.toString(dataMap.get("CASE_NO")) + "_" + longinID + ".pdf";
					notifyClientToDownloadFile(url, fileName);
				}
			}

			//多筆將檔名統一命名	
			if (list.size() > 1) {
				String uuid = UUID.randomUUID().toString();
				mergePdfFiles(url_list, uuid);
				String fileName = "滿意度問卷查詢_" + longinID + ".pdf";
				logger.info("mulit:" + (String) getCommonVariable(SystemVariableConsts.TEMP_PATH_RELATIVE) + "\\reports\\" + uuid + ".pdf");
				notifyClientToDownloadFile((String) getCommonVariable(SystemVariableConsts.TEMP_PATH_RELATIVE) + "\\reports\\" + uuid + ".pdf", fileName);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 合併 PDF 檔案
	 * 
	 * @param files
	 * @param savepath
	 * @throws APException
	 */
	public void mergePdfFiles(ArrayList<String> files, String uuid) throws DocumentException, APException {
		
		try {
			String savaPath = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports" + System.getProperties().getProperty("file.separator") + uuid + ".pdf";

			Document document = new Document(new PdfReader(files.get(0)).getPageSize(1));
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(savaPath));
			document.open();
			for (int i = 0; i < files.size(); i++) {
				PdfReader reader = new PdfReader(files.get(i));
				int n = reader.getNumberOfPages();
				for (int j = 1; j <= n; j++) {
					document.newPage();
					PdfImportedPage page = copy.getImportedPage(reader, j);
					copy.addPage(page);
				}
			}
			
			document.close();
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void getUHRMList(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(getUHRMList(body));
	}
	
	public SQM110OutputVO getUHRMList(Object body) throws Exception {
		SQM110InputVO inputVO = (SQM110InputVO) body;
		SQM110OutputVO outputVO = new SQM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT MEM.EMP_NAME AS LABEL, MEM.EMP_ID AS DATA, MEM.BRANCH_NBR, MEM.BRANCH_NAME ");
		sb.append("FROM VWORG_EMP_UHRM_INFO MEM ");
		sb.append("WHERE MEM.PRIVILEGEID = 'UHRM002' ");
		sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE MT.DEPT_ID = MEM.DEPT_ID AND MT.DEPT_ID = :areaId) ");
		if(StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			queryCondition.setObject("areaId", inputVO.getBranch_area_id());
		} else {
			queryCondition.setObject("areaId", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
		}
		sb.append("ORDER BY MEM.EMP_ID ");
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setUhrmList(dam.exeQuery(queryCondition));
		
		return outputVO;
	}
}