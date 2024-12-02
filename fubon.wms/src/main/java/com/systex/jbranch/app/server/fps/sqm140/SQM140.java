package com.systex.jbranch.app.server.fps.sqm140;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_FLOW_DTLPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_FLOW_DTLVO;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_IMPROVE_MASTPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_IMPROVE_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_MAINPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_MAINVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Description : 總行滿意度管理
 * Author : Willis
 * Date : 2018/03/08
 * Modifier : 2021/03/04 BY OCEAN => #0535: WMS-CR-20210115-01_擬新增即時滿意度淨推薦值問項
 */

@Component("sqm140")
@Scope("request")
public class SQM140 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SQM140.class);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	
	/**
	 * 查詢資料
	 * 
	 * @throws ParseException
	 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM140InputVO inputVO = (SQM140InputVO) body;
		SQM140OutputVO outputVO = new SQM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		// 主查詢 sql 修正 20170120
		sql.append("SELECT * ");
		sql.append("FROM ( ");
		sql.append("  SELECT A.*, ");
		sql.append("         DTL.DEDUCTION_INITIAL, ");
		sql.append("         DTL.BRH_DESC, ");
		sql.append("         DTL.IMPROVE_DESC, ");
		sql.append("         DTL.RC_SUP_REMARK, ");
		sql.append("         DTL.RC_VICE_SUP_REMARK, ");
		sql.append("         DTL.HEADMGR_REMARK, ");
		sql.append("         DTL.CUR_JOB, ");
		sql.append("         DTL.SUP_EMP_NAME, ");
		sql.append("         DTL.SUP_EMP_ID, ");
		sql.append("         DTL.SUP_CUR_JOB, ");
		sql.append("         ORG.REGION_CENTER_ID AS REGION_CENTER_ID_ORG, ");
		sql.append("         ORG.REGION_CENTER_NAME, ");
		sql.append("         ORG.BRANCH_AREA_ID AS BRANCH_AREA_ID_ORG, ");
		sql.append("         ORG.BRANCH_AREA_NAME, ");
		sql.append("         ORG.BRANCH_NAME, ");
		sql.append("         EMP.EMP_NAME, ");
		sql.append("         TO_CHAR(PABTH_UTIL.FC_GETBUSIDAY(TO_DATE(DATA_DATE, 'YYYYMMDD'), 'TWD', 8), 'YYYY/MM/DD') END_DATE, ");
		sql.append("         FLOW.BRMGR, ");
		sql.append("         FLOW.MBRMGR, ");
		sql.append("         FLOW.CRMGR_DEPUTY, ");
		sql.append("         FLOW.CRMGR, ");
		sql.append("         FLOW.HEADMGR, ");
		sql.append("         OWNER_EMP.EMP_NAME AS OWNER_EMP_NAME, ");
		sql.append("         STATUS_MAPP.PARAM_NAME AS CASE_STATUS_NAME ");
		sql.append("  FROM  TBSQM_CSM_IMPROVE_MAST A ");
		sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON ORG.DEPT_ID = A.BRANCH_NBR AND TO_DATE(A.TRADE_DATE, 'YYYYMMDD') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP ON EMP.EMP_ID = A.EMP_ID AND TO_DATE(A.TRADE_DATE, 'YYYYMMDD') BETWEEN EMP.START_TIME AND EMP.END_TIME ");
		sql.append("  LEFT JOIN TBSQM_CSM_IMPROVE_DTL DTL ON DTL.CASE_NO IS NOT NULL AND DTL.CASE_NO =  A.CASE_NO ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT CASE_NO, ");
		sql.append("           LISTAGG(BRMGR, ';') WITHIN GROUP(ORDER BY BRMGR ASC ) BRMGR, ");
		sql.append("           LISTAGG(MBRMGR, ';') WITHIN GROUP(ORDER BY MBRMGR ASC ) MBRMGR, ");
		sql.append("           LISTAGG(CRMGR_DEPUTY, ';') WITHIN GROUP(ORDER BY CRMGR_DEPUTY ASC ) CRMGR_DEPUTY, ");
		sql.append("           LISTAGG(CRMGR, ';') WITHIN GROUP(ORDER BY CRMGR ASC ) CRMGR, ");
		sql.append("           LISTAGG(HEADMGR, ';') WITHIN GROUP(ORDER BY HEADMGR ASC ) HEADMGR ");
		sql.append("    FROM ( ");
		sql.append("      SELECT DISTINCT ");
		sql.append("             CASE_NO, ");
		sql.append("             CASE WHEN EMP.ROLE_ID = 'A161' THEN EMP_ID || '-' || EMP_NAME ELSE '' END BRMGR, ");
		sql.append("             CASE WHEN EMP.ROLE_ID = 'A146' THEN EMP_ID || '-' || EMP_NAME ELSE '' END MBRMGR, ");
		sql.append("             CASE WHEN EMP.ROLE_ID = 'A164' AND JOB_TITLE_NAME = '處副主管' THEN EMP_ID || '-' || EMP_NAME ELSE '' END CRMGR_DEPUTY, ");
		sql.append("             CASE WHEN EMP.ROLE_ID = 'A164' AND JOB_TITLE_NAME = '處主管' THEN EMP_ID || '-' || EMP_NAME ELSE '' END CRMGR, ");
		sql.append("             CASE WHEN EMP.ROLE_ID IN (SELECT PARAM_CODE AS ROLE_ID FROM TBSYSPARAMETER WHERE PARAM_TYPE='FUBONSYS.HEADMGR_ROLE') THEN EMP_ID || '-' || EMP_NAME ELSE '' END HEADMGR ");
		sql.append("      FROM TBSQM_CSM_FLOW_DTL ");
		sql.append("      LEFT JOIN ( ");
		sql.append("        SELECT DISTINCT NVL(INFO.EMP_ID, PLU.EMP_ID) AS EMP_ID, NVL(INFO.ROLE_ID, PLU.ROLE_ID) AS ROLE_ID, NVL(INFO.EMP_NAME, PLU.EMP_NAME) AS EMP_NAME, NVL(INFO.JOB_TITLE_NAME, PLU.JOB_TITLE_NAME) AS JOB_TITLE_NAME ");
		sql.append("        FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
		sql.append("        LEFT JOIN VWORG_EMP_PLURALISM_INFO PLU ON INFO.EMP_ID = PLU.EMP_ID ");
		sql.append("      ) EMP ON EMP.EMP_ID = NEXT_SIGNOFF_ID ");
		sql.append("      WHERE CASE_STATUS <> 4 AND NEXT_SIGNOFF_ID <> 'CLOSE' ");
		sql.append("    ) ");
		sql.append("    GROUP BY CASE_NO ");
		sql.append("  ) FLOW ON A.CASE_NO = FLOW.CASE_NO ");
		sql.append("  LEFT JOIN TBORG_MEMBER OWNER_EMP ON A.OWNER_EMP_ID = OWNER_EMP.EMP_ID ");
		sql.append("  LEFT JOIN TBSYSPARAMETER STATUS_MAPP ON A.CASE_STATUS = STATUS_MAPP.PARAM_CODE AND STATUS_MAPP.PARAM_TYPE = 'SQM.STATUS_LIST' ");
		sql.append("  WHERE 1= 1 ");
		sql.append("  AND A.DELETE_FLAG IS NULL ");

		// 資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("  AND A.TRADE_DATE >= :times ");	// sql.append("and A.DATA_DATE >= :times ");
			condition.setObject("times", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		}
		
		if (inputVO.geteCreDate() != null) {
			sql.append("  AND A.TRADE_DATE <= :timee "); // sql.append("and A.DATA_DATE <= :timee ");
			condition.setObject("timee", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.geteCreDate()));
		}

		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("  AND ORG.BRANCH_NBR = :BRNCH_NBRR ");
			condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
		} else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("  AND ORG.BRANCH_NBR IN (:BRNCH_NBRR) ");
				condition.setObject("BRNCH_NBRR", pms000outputVO.getV_branchList());
			}
		}

		// 營運區
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append("  AND (ORG.BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
			sql.append("  		OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = A.EMP_ID AND UHRM.DEPT_ID = :BRANCH_AREA_IDD)) ");
			condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
		} else {
			// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("  AND ORG.BRANCH_AREA_ID IN (:BRANCH_AREA_IDD) ");
				condition.setObject("BRANCH_AREA_IDD", pms000outputVO.getV_areaList());
			}
		}

		// 區域中心
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append("  AND ORG.REGION_CENTER_ID = :REGION_CENTER_IDD ");
			condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
		} else {
			// 登入非總行人員強制加區域中心
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("  AND ORG.REGION_CENTER_ID IN (:REGION_CENTER_IDD) ");
				condition.setObject("REGION_CENTER_IDD", pms000outputVO.getV_regionList());
			}
		}

		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append("  AND A.EMP_ID like :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("  AND A.CUST_ID like :cust_id ");
			condition.setObject("cust_id", inputVO.getCust_id() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getQtn_type())) {
			sql.append("  AND A.QTN_TYPE = :qtn_type ");
			condition.setObject("qtn_type", inputVO.getQtn_type());
		}

		if (StringUtils.isNotBlank(inputVO.getDeduction_initial())) {
			if ("null".equals(inputVO.getDeduction_initial())) {
				sql.append("  AND DTL.DEDUCTION_INITIAL is null ");
			} else {
				sql.append("  AND DTL.DEDUCTION_INITIAL = :deduction_initial ");
				condition.setObject("deduction_initial", inputVO.getDeduction_initial());
			}
		}

		if (StringUtils.isNotBlank(inputVO.getDeduction_final())) {

			if ("null".equals(inputVO.getDeduction_final())) {
				sql.append("  AND A.DEDUCTION_FINAL is null ");
			} else {
				sql.append("  AND A.DEDUCTION_FINAL = :deduction_final ");
				condition.setObject("deduction_final", inputVO.getDeduction_final());
			}
		}

		if (StringUtils.isNotBlank(inputVO.getHo_check()) && "Y".equals(inputVO.getHo_check())) {
			sql.append("  AND A.HO_CHECK = :ho_check ");
			condition.setObject("ho_check", inputVO.getHo_check());
		} else {
			sql.append("  AND A.HO_CHECK is null ");
		}

		if (StringUtils.isNotBlank(inputVO.getCase_no())) {
			sql.append("  AND A.CASE_NO like :case_no ");
			condition.setObject("case_no", inputVO.getCase_no() + "%");
		}

		sql.append("  ORDER BY A.TRADE_DATE, ORG.BRANCH_NBR, A.EMP_ID, A.AO_CODE, A.CUST_ID, A.QTN_TYPE ");
		sql.append(") ");
		sql.append("ORDER BY LASTUPDATE DESC ");
		
		condition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalList(dam.exeQuery(condition));
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢匯入資料
	 * 
	 * @throws ParseException
	 **/
	public void queryImportData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM140ImportInputVO inputVO = (SQM140ImportInputVO) body;
		SQM140OutputVO outputVO = new SQM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		// 主查詢 sql 修正 20170120
		sql.append(" SELECT DISTINCT * FROM ( ");
		sql.append(" SELECT A.*, ");
		sql.append("       ORG.REGION_CENTER_NAME, ");
		sql.append("       ORG.BRANCH_AREA_NAME, ");
		sql.append("       ORG.BRANCH_NAME, ");
		sql.append("       EMP.EMP_NAME ");
		sql.append(" FROM TBSQM_CSM_MAIN A ");
		sql.append(" LEFT JOIN (SELECT * FROM TBPMS_ORG_REC WHERE ACTIVE_FLAG = 'Y') ORG ON ORG.DEPT_ID = A.BRANCH_NBR AND TO_DATE(A.TRADE_DATE, 'yyyyMMdd') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sql.append(" LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP ON EMP.EMP_ID = A.EMP_ID AND TO_DATE(A.TRADE_DATE, 'yyyyMMdd') BETWEEN EMP.START_TIME AND EMP.END_TIME ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND SATISFACTION_W in ('4', '5', '6')  "); //不滿意, 非常不滿意, 未聯繫
		sql.append(" AND QTN_TYPE <> 'WMS03' ");
		// 臨櫃：依據整體不滿意(問題13)或問題7(不滿意/非常不滿意)任一不滿意，才需撰寫報告
		sql.append(" UNION ");
		sql.append(" SELECT A.*, ORG.REGION_CENTER_NAME, ORG.BRANCH_AREA_NAME, ORG.BRANCH_NAME, EMP.EMP_NAME ");
		sql.append(" FROM TBSQM_CSM_MAIN A ");
		sql.append(" LEFT JOIN (SELECT * FROM TBPMS_ORG_REC WHERE ACTIVE_FLAG = 'Y') ORG ON ORG.DEPT_ID = A.BRANCH_NBR AND TO_DATE(A.TRADE_DATE, 'yyyyMMdd') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sql.append(" LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP ON EMP.EMP_ID = A.EMP_ID AND TO_DATE(A.TRADE_DATE, 'yyyyMMdd') BETWEEN EMP.START_TIME AND EMP.END_TIME ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND SATISFACTION_O in ('4', '5', '6') ");
		sql.append(" AND QTN_TYPE = 'WMS03' ");
		sql.append(" UNION ");
		sql.append(" SELECT A.*, ORG.REGION_CENTER_NAME, ORG.BRANCH_AREA_NAME, ORG.BRANCH_NAME, EMP.EMP_NAME ");
		sql.append(" FROM TBSQM_CSM_MAIN A ");
		sql.append(" LEFT JOIN (SELECT * FROM TBPMS_ORG_REC WHERE ACTIVE_FLAG = 'Y') ORG ON ORG.DEPT_ID = A.BRANCH_NBR AND TO_DATE(A.TRADE_DATE, 'yyyyMMdd') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sql.append(" LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP ON EMP.EMP_ID = A.EMP_ID AND TO_DATE(A.TRADE_DATE, 'yyyyMMdd') BETWEEN EMP.START_TIME AND EMP.END_TIME ");
		sql.append(" LEFT JOIN TBSQM_CSM_ANSWER ANS ON A.PAPER_UUID = ANS.PAPER_UUID ");
		sql.append(" LEFT JOIN TBSQM_CSM_QUESTION Q ON ANS.QUESTION_MAPPING_ID = Q.QUESTION_MAPPING_ID ");
		sql.append(" LEFT JOIN TBSQM_CSM_OPTIONS O ON ANS.ITEM_MAPPING_ID = O.ITEM_MAPPING_ID ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND A.QTN_TYPE = 'WMS03' ");
		sql.append(" AND Q.PRIORITY = 7 ");
		sql.append(" AND O.OPTION_TYPE = 'S' ");
		sql.append(" AND O.SCORE IN ('4', '5', '6') ");
		sql.append(" ) WHERE 1 = 1 ");
		
		// 資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("AND TRADE_DATE >= :times "); // sql.append("and DATA_DATE >= :times ");
			condition.setObject("times", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		}
		if (inputVO.geteCreDate() != null) {
			sql.append("AND TRADE_DATE <= :timee "); // sql.append("and DATA_DATE <= :timee ");
			condition.setObject("timee", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.geteCreDate()));
		}

		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("AND BRANCH_NBR = :BRNCH_NBRR ");
			condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
		} else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("AND BRANCH_NBR IN (:BRNCH_NBRR) ");
				condition.setObject("BRNCH_NBRR", pms000outputVO.getV_branchList());
			}
		}

		if (StringUtils.isNotBlank(inputVO.getQtn_type())) {
			sql.append("AND QTN_TYPE = :QTN_TYPE ");
			condition.setObject("QTN_TYPE", inputVO.getQtn_type());
		}

		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append("AND EMP_ID = :EMP_ID ");
			condition.setObject("EMP_ID", inputVO.getEmp_id());
		}

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("AND CUST_ID = :CUST_ID ");
			condition.setObject("CUST_ID", inputVO.getCust_id());
		}

		if ("Y".equals(inputVO.getImport_check())) {
			sql.append("AND IMPORTED = :IMPORTED ");
			condition.setObject("IMPORTED", inputVO.getImport_check());
		} else {
			sql.append("AND IMPORTED IS NULL ");
		}

		sql.append("ORDER BY TRADE_DATE, BRANCH_NBR, EMP_ID, CUST_ID ");
		
		condition.setQueryString(sql.toString());
		
		outputVO.setTotalList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢匯入資料
	 * 
	 * @throws ParseException
	 **/
	public void getQuestion(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		SQM140EditInputVO inputVO = (SQM140EditInputVO) body;
		SQM140OutputVO outputVO = new SQM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// 主查詢 sql 修正 20180803
		sql.append("SELECT Q.PRIORITY AS QST_ORDER, ");
		sql.append("       Q.DESCRIPTION AS QST_DESC, ");
		sql.append("       LISTAGG(CASE WHEN Q.PRIORITY = '1' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS01' AND Q.PRIORITY = '12' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS02' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS03' AND Q.PRIORITY = '18' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS04' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");    
		sql.append("                    WHEN OP.DESCRIPTION IS NULL THEN NULL ");
		sql.append("               ELSE DECODE(OP.OPTION_TYPE, 'S', NVL(TO_CHAR(OP.SCORE), OP.DESCRIPTION), 'M', OP.DESCRIPTION ) END, ';') ");
		sql.append("       WITHIN GROUP(ORDER BY Q.PRIORITY ASC, OP.PRIORITY ASC, Q.DESCRIPTION ASC)  AS ANSWER ");
		sql.append("FROM TBSQM_CSM_QUESTION Q ");
		sql.append("LEFT JOIN TBSQM_CSM_ANSWER ANS ON Q.QUESTION_MAPPING_ID = ANS.QUESTION_MAPPING_ID AND ANS.PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE) ");
		sql.append("LEFT JOIN TBSQM_CSM_OPTIONS OP ON ( ANS.ITEM_MAPPING_ID = OP.ITEM_MAPPING_ID AND Q.QUESTION_MAPPING_ID = OP.QUESTION_MAPPING_ID ) ");
		sql.append("WHERE Q.QTN_TYPE = ( ");
		sql.append("  SELECT DISTINCT QTN_TYPE FROM TBSQM_CSM_QUESTION WHERE QUESTION_MAPPING_ID IN (SELECT QUESTION_MAPPING_ID FROM TBSQM_CSM_ANSWER WHERE PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE))");
		sql.append(") ");
		sql.append("AND NVL(OP.OPTION_TYPE,' ') <> 'O' ");
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

	/* ==== 【匯入】匯入資料 ======== */
	public void importData(Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM140ImportInputVO inputVO = (SQM140ImportInputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		try {
			for (Map<String, Object> map : inputVO.getImportList()) { // 資料修改後

				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				
				sql.append("SELECT TBSQM_CSM_IMPROVE_MAST_SEQ.nextval AS SEQ FROM DUAL ");
				
				condition.setQueryString(sql.toString());
				
				List<Map<String, Object>> SEQLIST = dam.exeQuery(condition);
				
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ"); // BigDecimal qst_version = new BigDecimal(map.get("QST_VERSION").toString());

				//將資料匯入滿意度改善主檔	
				TBSQM_CSM_IMPROVE_MASTPK pk = new TBSQM_CSM_IMPROVE_MASTPK();
				TBSQM_CSM_IMPROVE_MASTVO paramVO = new TBSQM_CSM_IMPROVE_MASTVO();
				pk.setSEQ(seqNo);
				pk.setCUST_ID(map.get("CUST_ID").toString());
				pk.setDATA_DATE(map.get("DATA_DATE").toString());
				pk.setQTN_TYPE(map.get("QTN_TYPE").toString());
				paramVO.setcomp_id(pk);
				paramVO.setREGION_CENTER_ID(ObjectUtils.toString(map.get("REGION_CENTER_ID")));
				paramVO.setBRANCH_AREA_ID(ObjectUtils.toString(map.get("BRANCH_AREA_ID")));
				paramVO.setBRANCH_NBR(ObjectUtils.toString(map.get("BRANCH_NBR")));
				paramVO.setAO_CODE(ObjectUtils.toString(map.get("AO_CODE")));
				paramVO.setEMP_ID(ObjectUtils.toString(map.get("EMP_ID")));
				paramVO.setQUESTION_DESC(ObjectUtils.toString(map.get("QUESTION_DESC")));
				paramVO.setCUST_NAME(ObjectUtils.toString(map.get("CUST_NAME")));
				paramVO.setMOBILE_NO(ObjectUtils.toString(map.get("MOBILE_NO")));
				paramVO.setANSWER(ObjectUtils.toString(map.get("ANSWER")));
				paramVO.setRESP_NOTE(ObjectUtils.toString(map.get("RESP_NOTE")));
				paramVO.setTRADE_DATE(ObjectUtils.toString(map.get("TRADE_DATE")));
				paramVO.setRESP_DATE(ObjectUtils.toString(map.get("RESP_DATE")));
				paramVO.setSEND_DATE(ObjectUtils.toString(map.get("SEND_DATE")));
				paramVO.setCAMPAIGN_ID(ObjectUtils.toString(map.get("CAMPAIGN_ID")));
				paramVO.setSTEP_ID(ObjectUtils.toString(map.get("STEP_ID")));
				paramVO.setSATISFACTION_O(ObjectUtils.toString(map.get("SATISFACTION_O")));
				paramVO.setSATISFACTION_W(ObjectUtils.toString(map.get("SATISFACTION_W")));
				paramVO.setUHRM_YN(ObjectUtils.toString(map.get("UHRM_YN")));
				// paramVO.setQST_VERSION(qst_version);
				dam.create(paramVO);

				//將滿意度問卷主檔的註記欄位壓Y
				TBSQM_CSM_MAINPK mainPk = new TBSQM_CSM_MAINPK();
				mainPk.setCUST_ID(map.get("CUST_ID").toString());
				mainPk.setDATA_DATE(map.get("DATA_DATE").toString());
				mainPk.setQTN_TYPE(map.get("QTN_TYPE").toString());
				TBSQM_CSM_MAINVO mainVO = (TBSQM_CSM_MAINVO) dam.findByPKey(TBSQM_CSM_MAINVO.TABLE_UID, mainPk);
				mainVO.setIMPORTED("Y");

				dam.update(mainVO);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		sendRtnObject(null);
	}

	/* ==== 更新刪除註記 ======== */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {

		SQM140EditInputVO inputVO = (SQM140EditInputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		try {
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT REGION_CENTER_ID,BRANCH_AREA_ID ");
			sql.append("FROM VWORG_DEFN_INFO ");
			sql.append("WHERE BRANCH_NBR = :BRANCH_NBR ");
			condition.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> ORGLIST = dam.exeQuery(condition);
			
			//取得私銀註記
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("SELECT 1 FROM VWORG_EMP_UHRM_INFO WHERE EMP_ID = :empId ");
			condition.setObject("empId", inputVO.getEmp_id());
			List<Map<String, Object>> uhrmlist = dam.exeQuery(condition);

			BigDecimal seqNo = new BigDecimal(inputVO.getSeq());
			//將資料刪除註記壓Y	
			TBSQM_CSM_IMPROVE_MASTPK pk = new TBSQM_CSM_IMPROVE_MASTPK();
			pk.setSEQ(seqNo);
			pk.setCUST_ID(inputVO.getCust_id());
			pk.setDATA_DATE(inputVO.getData_date());
			pk.setQTN_TYPE(inputVO.getQtnType());
			
			TBSQM_CSM_IMPROVE_MASTVO paramVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, pk);
			if (paramVO != null) {
				paramVO.setREGION_CENTER_ID(ObjectUtils.toString(ORGLIST.get(0).get("REGION_CENTER_ID")));
				paramVO.setBRANCH_AREA_ID(ObjectUtils.toString(ORGLIST.get(0).get("BRANCH_AREA_ID")));
				paramVO.setBRANCH_NBR(inputVO.getBranch_nbr());
				paramVO.setEMP_ID(inputVO.getEmp_id());
				paramVO.setRESP_NOTE(inputVO.getResp_note());
				paramVO.setUHRM_YN(CollectionUtils.isNotEmpty(uhrmlist) ? "Y" : "N"); //私銀註記
				dam.update(paramVO);
				sendRtnObject(null);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* ==== 更新刪除註記 ======== */
	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM140InputVO inputVO = (SQM140InputVO) body;
		dam = this.getDataAccessManager();

		try {
			Map<String, Object> map = inputVO.getDelList().get(0);

			BigDecimal seqNo = new BigDecimal(map.get("SEQ").toString());
			
			//將資料刪除註記壓Y	
			TBSQM_CSM_IMPROVE_MASTPK pk = new TBSQM_CSM_IMPROVE_MASTPK();
			pk.setSEQ(seqNo);
			pk.setCUST_ID(map.get("CUST_ID").toString());
			pk.setDATA_DATE(map.get("DATA_DATE").toString());
			pk.setQTN_TYPE(map.get("QTN_TYPE").toString());
			
			TBSQM_CSM_IMPROVE_MASTVO paramVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, pk);
			paramVO.setDELETE_FLAG("Y");

			if (paramVO != null) {
				dam.update(paramVO);
				sendRtnObject(null);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* ==== 【放行】放行資料 ======== */
	public void ho_check(Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM140InputVO inputVO = (SQM140InputVO) body;
		dam = this.getDataAccessManager();
		
		try {
			for (Map<String, Object> map : inputVO.getCheckList()) { // 資料修改後
				BigDecimal seqNo = new BigDecimal(map.get("SEQ").toString()); // BigDecimal seqNo = (BigDecimal) map.get("SEQ");

				TBSQM_CSM_IMPROVE_MASTPK pk = new TBSQM_CSM_IMPROVE_MASTPK();
				pk.setSEQ(seqNo);
				pk.setCUST_ID(map.get("CUST_ID").toString());
				pk.setDATA_DATE(map.get("DATA_DATE").toString());
				pk.setQTN_TYPE(map.get("QTN_TYPE").toString());
				
				TBSQM_CSM_IMPROVE_MASTVO paramVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, pk);
				paramVO.setHO_CHECK("Y");

				if (paramVO != null) {
					dam.update(paramVO);
					sendRtnObject(null);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* ==== 【扣分】扣分資料 ======== */
	public void deduction(Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM140InputVO inputVO = (SQM140InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		try {
			for (Map<String, Object> map : inputVO.getCheckList()) { // 資料修改後
				BigDecimal seqNo = new BigDecimal(map.get("SEQ").toString());

				TBSQM_CSM_IMPROVE_MASTPK pk = new TBSQM_CSM_IMPROVE_MASTPK();
				pk.setSEQ(seqNo);
				pk.setCUST_ID(map.get("CUST_ID").toString());
				pk.setDATA_DATE(map.get("DATA_DATE").toString());
				pk.setQTN_TYPE(map.get("QTN_TYPE").toString());
				
				TBSQM_CSM_IMPROVE_MASTVO paramVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, pk);
				paramVO.setDEDUCTION_FINAL("Y");
				paramVO.setCASE_STATUS("1");
				
				if (paramVO != null) {
					dam.update(paramVO);

					//執行扣分時，建立軌跡。
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					
					sql.append("select (COUNT(*)+1) AS SIGNOFF_NUM  ");
					sql.append("from TBSQM_CSM_FLOW_DTL ");
					sql.append("WHERE CASE_NO = :CASE_NO ");
					
					condition.setObject("CASE_NO", ObjectUtils.toString(map.get("CASE_NO")));
					
					condition.setQueryString(sql.toString());
					
					List<Map<String, Object>> NUMLIST = dam.exeQuery(condition);

					TBSQM_CSM_FLOW_DTLPK flowPk = new TBSQM_CSM_FLOW_DTLPK();
					flowPk.setCASE_NO(ObjectUtils.toString(map.get("CASE_NO")));
					flowPk.setSIGNOFF_NUM((NUMLIST.get(0).get("SIGNOFF_NUM").toString()));

					TBSQM_CSM_FLOW_DTLVO flowVO = new TBSQM_CSM_FLOW_DTLVO();
					flowVO.setcomp_id(flowPk);
					flowVO.setSIGNOFF_ID(loginID);
					flowVO.setSIGNOFF_NAME(getEmp_Name(loginID));
					flowVO.setCASE_STATUS("1"); //扣分

					dam.create(flowVO);

					sendRtnObject(null);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* ==== 【不扣分】不扣分資料 ======== */
	public void no_deduction(Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM140InputVO inputVO = (SQM140InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		try {
			for (Map<String, Object> map : inputVO.getCheckList()) { // 資料修改後
				BigDecimal seqNo = new BigDecimal(map.get("SEQ").toString());

				TBSQM_CSM_IMPROVE_MASTPK pk = new TBSQM_CSM_IMPROVE_MASTPK();
				pk.setSEQ(seqNo);
				pk.setCUST_ID(map.get("CUST_ID").toString());
				pk.setDATA_DATE(map.get("DATA_DATE").toString());
				pk.setQTN_TYPE(map.get("QTN_TYPE").toString());
				
				TBSQM_CSM_IMPROVE_MASTVO paramVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, pk);
				paramVO.setDEDUCTION_FINAL("N");
				paramVO.setCASE_STATUS("2");
				
				if (paramVO != null) {
					dam.update(paramVO);

					//執行不扣分時，建立軌跡。
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					
					sql.append("select (COUNT(*)+1) AS SIGNOFF_NUM  ");
					sql.append("from TBSQM_CSM_FLOW_DTL ");
					sql.append("WHERE CASE_NO = :CASE_NO ");
					
					condition.setObject("CASE_NO", ObjectUtils.toString(map.get("CASE_NO")));
					
					condition.setQueryString(sql.toString());
					
					List<Map<String, Object>> NUMLIST = dam.exeQuery(condition);

					TBSQM_CSM_FLOW_DTLPK flowPk = new TBSQM_CSM_FLOW_DTLPK();
					flowPk.setCASE_NO(ObjectUtils.toString(map.get("CASE_NO")));
					flowPk.setSIGNOFF_NUM((NUMLIST.get(0).get("SIGNOFF_NUM").toString()));

					TBSQM_CSM_FLOW_DTLVO flowVO = new TBSQM_CSM_FLOW_DTLVO();
					flowVO.setcomp_id(flowPk);
					flowVO.setSIGNOFF_ID(loginID);
					flowVO.setSIGNOFF_NAME(getEmp_Name(loginID));
					flowVO.setCASE_STATUS("2"); //不扣分

					dam.create(flowVO);
					sendRtnObject(null);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public String getEmp_Name(String emp_id) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("select distinct EMP_NAME  ");
			sql.append("from TBPMS_EMPLOYEE_REC_N ");
			sql.append("WHERE EMP_ID = :EMP_ID ");
			sql.append("and sysdate BETWEEN START_TIME AND END_TIME  ");
			
			condition.setObject("EMP_ID", emp_id);
			
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> EMPLIST = dam.exeQuery(condition);
			
			String emp_name = "";
			if (EMPLIST.size() > 0) {
				emp_name = EMPLIST.get(0).get("EMP_NAME").toString();
			}
			
			return emp_name;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* === 產出彙整報告==== */
	public void collectionExportRPT(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		SQM140OutputVO outputVO = (SQM140OutputVO) body;
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> qtnMap = xmlInfo.doGetVariable("SQM.QTN_TYPE", FormatHelper.FORMAT_3); // 問卷類型
		
		List<Map<String, Object>> list = outputVO.getTotalList();
		
		// 20210319 ADD MARK BY OCEAN => #0535: WMS-CR-20210115-01_擬新增即時滿意度淨推薦值問項 => 佩珊：調整彙整報告功能(將扣分也納入EXCEL表中，另請新增分行改善報告中的分行說明/問題釐清內容)
//		//只做不扣分資料
//		for (int i = list.size() - 1; i >= 0; i--) {
//			if (!"N".equals(ObjectUtils.toString(list.get(i).get("DEDUCTION_INITIAL")))) {
//				list.remove(i);
//			}
//		}

		if (list.isEmpty()) {
			this.sendRtnObject(null);
			return;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "滿意度彙整報告_" + sdf.format(new Date()) + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String filePath = Path + fileName;
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
		style.setWrapText(true);

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
		//		headingStyle.setWrapText(true);

		//設定SHEET名稱
		XSSFSheet sheet = wb.createSheet("滿意度彙整報告");
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		Integer index = 0; // 行數

		List<String> headerLineTop = new ArrayList<String>();

		headerLineTop.add("編號");
		headerLineTop.add("類別");
		headerLineTop.add("客戶不滿意的原因");
		headerLineTop.add("新增分行說明/問題釐清");
		headerLineTop.add("分行改善作法");
		headerLineTop.add("權責單位-扣分對象");
		headerLineTop.add("處/副主管批示不扣分原因");
		headerLineTop.add("總行意見");
		headerLineTop.add("執副裁示");

		// Heading
		XSSFRow row = sheet.createRow(index);
		row.setHeightInPoints(25);

		for (int i = 0; i < headerLineTop.size(); i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLineTop.get(i));
		}

		//固定欄位
		String[] detailList = {	"CASE_NO",					// 編號
								"QTN_TYPE", 				// 類別
								"RESP_NOTE", 				// 客戶不滿意的原因
								"BRH_DESC", 				// 新增分行說明/問題釐清
								"IMPROVE_DESC", 			// 分行改善作法
								"BRANCH_NBR", 				// 權責單位-扣分對象
								"RC_SUP_REMARK", 			// 處/副主管批示不扣分原因
								"HEADMGR_REMARK", 			// 總行意見
								"BOSS_REMARK"};				// 執副裁示
//		List<Map<Integer, Object>> detailList = new ArrayList<Map<Integer, Object>>();
//		Map map = new HashMap();
//		int detailColumn = 0;
//		map.put(detailColumn++, "CASE_NO");
//		map.put(detailColumn++, "QTN_TYPE");
//		map.put(detailColumn++, "RESP_NOTE");
//		map.put(detailColumn++, "");
//		map.put(detailColumn++, "IMPROVE_DESC");
//		map.put(detailColumn++, "BRANCH_NBR");
//		map.put(detailColumn++, "RC_SUP_REMARK");
//		map.put(detailColumn++, "HEADMGR_REMARK");
//		map.put(detailColumn++, "");
//		detailList.add(map);
		//資料開始
		index++;

		int detail = index;

		for (Map<String, Object> dataMap : list) {
			row = sheet.createRow(detail++);
			//固定欄位 
			for (int j = 0; j < detailList.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				
				switch (detailList[j]) {
					case "BRANCH_NBR" : // 1.權責單位(請以上下文顯示)扣分對象。
						String branch_name = ObjectUtils.toString(dataMap.get("BRANCH_NAME"));
						String emp = "";
						String sup_emp = "";
						if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get("EMP_ID")))) {
							String curJob = ObjectUtils.toString(dataMap.get("CUR_JOB")).equals("") ? "" : ObjectUtils.toString(dataMap.get("CUR_JOB")) + "：";
							emp = "\n" + curJob + ObjectUtils.toString(dataMap.get("EMP_ID"));
						}
						if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get("EMP_NAME")))) {
							emp = emp + "-" + ObjectUtils.toString(dataMap.get("EMP_NAME"));
						}
	
						if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get("SUP_EMP_ID")))) {
							String sup_cur_job = ObjectUtils.toString(dataMap.get("SUP_CUR_JOB")).equals("") ? "" : ObjectUtils.toString(dataMap.get("SUP_CUR_JOB")) + "：";
							sup_emp = "\n" + sup_cur_job + ObjectUtils.toString(dataMap.get("SUP_EMP_ID"));
						}
						if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get("SUP_EMP_NAME")))) {
							sup_emp = sup_emp + "-" + ObjectUtils.toString(dataMap.get("SUP_EMP_NAME"));
						}
	
						cell.setCellValue(ObjectUtils.toString(dataMap.get(detailList[j])) + "-" + branch_name + emp + sup_emp);
						sheet.setColumnWidth(4, 40 * 256); //設定欄位寬度    
						
						break;
					case "QTN_TYPE" : 
						String qtn_type = "";
						qtn_type = ObjectUtils.toString(dataMap.get(detailList[j]));
						cell.setCellValue(ObjectUtils.toString(qtnMap.get(qtn_type)));
						
						break;
					case "BOSS_REMARK" :
						String s = "□ 扣分\r\r\r\n□ 不扣分 ";
						cell.setCellValue(s);
						
						break;
					case "RC_SUP_REMARK" :
						// 處副主管批示不扣分原因也需要加入
						String remark = "";
						if (dataMap.get("RC_VICE_SUP_REMARK") != null) {
							remark += "處副主管：" + ObjectUtils.toString(dataMap.get("RC_VICE_SUP_REMARK"));
						}
						if (dataMap.get(detailList[j]) != null) {
							if (remark.length() > 0) {
								remark += "\n";
							}
							remark += "處主管：" + ObjectUtils.toString(dataMap.get(detailList[j]));
						}
						cell.setCellValue(remark);
						
						break;
					default:
						cell.setCellValue(ObjectUtils.toString(dataMap.get(detailList[j])));
						break;
				}
			}
		}

		String tempName = UUID.randomUUID().toString();
		
		// 路徑結合
		File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
		
		// 絕對路徑建檔
		wb.write(new FileOutputStream(f));

		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName);

		this.sendRtnObject(outputVO);
	}

	/* === 產出備查報告==== */
	public void exportRPT(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		SQM140OutputVO outputVO = (SQM140OutputVO) body;
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> ansMap = xmlInfo.doGetVariable("SQM.ANS_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> qtnMap = xmlInfo.doGetVariable("SQM.QTN_TYPE", FormatHelper.FORMAT_3); // 問卷類型
		
		List<Map<String, Object>> list = outputVO.getTotalList();

		//滿意度
		//		Map qtnMap = new HashMap<String , String>();
		//		qtnMap.put("WMS01", "投資/保險");
		//		qtnMap.put("WMS02", "理專");
		//		qtnMap.put("WMS03", "開戶");
		//		qtnMap.put("WMS04", "櫃檯");
		//		qtnMap.put("WMS05", "簡訊");

		//只做已有處理結果資料
		//		for (int i = list.size() - 1; i >= 0; i--) {
		//			if(StringUtils.isBlank((ObjectUtils.toString(list.get(i).get("DEDUCTION_FINAL"))))){
		//				list.remove(i);
		//			}
		//		}

		if (list.isEmpty()) {
			this.sendRtnObject(null);
			return;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "滿意度備查_" + sdf.format(new Date()) + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String filePath = Path + fileName;
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
		style.setWrapText(true);

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
		//		headingStyle.setWrapText(true);

		//設定SHEET名稱
		XSSFSheet sheet = wb.createSheet("滿意度備查");
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		Integer index = 0; // 行數

		List<String> headerLineTop = new ArrayList<String>();

		headerLineTop.add("案件編號");
		headerLineTop.add("問卷別");
		headerLineTop.add("交易日期");
		headerLineTop.add("業務處");
		headerLineTop.add("營運區");
		headerLineTop.add("分行別");
		headerLineTop.add("員工編號");
		headerLineTop.add("AO_CODE");
		headerLineTop.add("主管姓名");
		headerLineTop.add("主管員編");
		headerLineTop.add("客戶姓名");
		headerLineTop.add("客戶ID");
		headerLineTop.add("回覆日期");
		headerLineTop.add("調查結果(整體滿意度)");
		headerLineTop.add("代碼");
		headerLineTop.add("處理結果");
		headerLineTop.add("處/副主管簽核意見");
		headerLineTop.add("主管");
		headerLineTop.add("個金主管");
		headerLineTop.add("督導");
		headerLineTop.add("副處主管");
		headerLineTop.add("處長主管");
		headerLineTop.add("總行經辦");
		headerLineTop.add("目前應簽核人員");
		headerLineTop.add("目前處理狀態");

		// Heading
		XSSFRow row = sheet.createRow(index);
		row.setHeightInPoints(25);

		for (int i = 0; i < headerLineTop.size(); i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLineTop.get(i));

		}

		//固定欄位
		String[] mainLine    = {"CASE_NO", 
								"QTN_TYPE", 
								"TRADE_DATE", 
								"REGION_CENTER_NAME", 
								"BRANCH_AREA_NAME", 
								"BRANCH_NAME", 
								"EMP_ID", 
								"AO_CODE", 
								"SUP_EMP_NAME", 
								"SUP_EMP_ID", 
								"CUST_NAME", 
								"CUST_ID", 
								"RESP_DATE", 
								"SATISFACTION_O", 
								"SATISFACTION_O", 
								"DEDUCTION_FINAL", 
								"DEDUCTION_INITIAL", 
								"SUP_EMP_ID", 
								"BRMGR", 
								"MBRMGR", 
								"CRMGR_DEPUTY", 
								"CRMGR", 
								"HEADMGR", 
								"OWNER_EMP_ID", 
								"CASE_STATUS"};
		//資料開始
		index++;

		int detail = index;

		for (Map<String, Object> dataMap : list) {
			row = sheet.createRow(detail++);
			//固定欄位 
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				
				switch (mainLine[j]) {
					case "CASE_NO" : // 案件編號
						String case_no = ObjectUtils.toString(dataMap.get(mainLine[j]));
						if (StringUtils.isNotBlank(case_no)) {
							cell.setCellValue(ObjectUtils.toString(case_no));
						} else {
							cell.setCellValue("未建立");
						}
						
						break;
					case "QTN_TYPE" : // 轉換問卷別
						String qtn_type = "";
						qtn_type = ObjectUtils.toString(dataMap.get(mainLine[j]));
						cell.setCellValue(ObjectUtils.toString(qtnMap.get(qtn_type)));
						
						break;
					case "BRANCH_NAME": // 分行別
						String branch_nbr = ObjectUtils.toString(dataMap.get("BRANCH_NBR"));
						String branch_name = ObjectUtils.toString(dataMap.get(mainLine[j]));
						if (StringUtils.isNotBlank(branch_nbr)) {
							cell.setCellValue(branch_nbr + "-" + branch_name);
						} else {
							cell.setCellValue("");
						}
						
						break;
					case "SATISFACTION_O": // 調查結果(整體滿意度)
						if (j == 13) {
							String ans = ObjectUtils.toString(dataMap.get(mainLine[j]));
							cell.setCellValue(ansMap.get(ans));
						} else {
							cell.setCellValue(ObjectUtils.toString(dataMap.get(mainLine[j])));
						}
						
						break;
					case "DEDUCTION_FINAL": // 處理結果
						String final_Desc = "";
						final_Desc = ObjectUtils.toString(dataMap.get(mainLine[j]));
						if ("Y".equals(final_Desc)) {
							final_Desc = "扣分";
						} else if ("N".equals(final_Desc)) {
							final_Desc = "不扣分";
						}
						cell.setCellValue(final_Desc);
						
						break;
					case "DEDUCTION_INITIAL": // 處長簽核意見
						String deduction_initial_Desc = "";
						deduction_initial_Desc = ObjectUtils.toString(dataMap.get(mainLine[j]));
						if (StringUtils.isNotBlank(deduction_initial_Desc)) {
							if (StringUtils.equals(deduction_initial_Desc, "Y")) {
								cell.setCellValue("扣分");
							} else {
								cell.setCellValue("不扣分");
							}
						} else {
							cell.setCellValue("未裁示");
						}
						
						break;
					case "SUP_EMP_ID": // 主管
						String sup = "";
						if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get(mainLine[j])))) {
							if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get("SUP_EMP_NAME")))) {
								sup = ObjectUtils.toString(dataMap.get(mainLine[j])) + "-" + ObjectUtils.toString(dataMap.get("SUP_EMP_NAME"));
							} else {
								sup = ObjectUtils.toString(dataMap.get(mainLine[j]));
							}
						}
						cell.setCellValue(sup);
						
						break;
					case "OWNER_EMP_ID": // 目前應簽核人員
						String ownerEmpID = "";
						if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get(mainLine[j])))) {
							if (StringUtils.isNotBlank(ObjectUtils.toString(dataMap.get("OWNER_EMP_NAME")))) {
								ownerEmpID = ObjectUtils.toString(dataMap.get(mainLine[j])) + "-" + ObjectUtils.toString(dataMap.get("OWNER_EMP_NAME"));
							} else {
								ownerEmpID = ObjectUtils.toString(dataMap.get(mainLine[j]));
							}
						}
						cell.setCellValue(ownerEmpID);
						
						break;
					case "CASE_STATUS": // 目前處理狀態
						cell.setCellValue(ObjectUtils.toString(dataMap.get("CASE_STATUS_NAME")));
						break;
					default:
						cell.setCellValue(ObjectUtils.toString(dataMap.get(mainLine[j])));
						
						break;
				}
			}
		}

		String tempName = UUID.randomUUID().toString();
		
		// 路徑結合
		File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
		
		// 絕對路徑建檔
		wb.write(new FileOutputStream(f));

		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName);

		this.sendRtnObject(outputVO);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
}