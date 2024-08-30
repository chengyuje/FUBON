package com.systex.jbranch.app.server.fps.org130;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_MD_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBORG_EMP_HIRE_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBORG_EMP_HIRE_INFOPK;
import com.systex.jbranch.app.common.fps.table.TBORG_EMP_HIRE_INFOVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * MENU
 * 
 * @author Stella
 * @date 2016/07/04
 * @spec null
 */
@Component("org130")
@Scope("request")

public class ORG130 extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(ORG130.class);
	
	private DataAccessManager dam = null;	
	
	/*
	 * 取得可執行覆核的群組
	 */
	public StringBuffer getReviewList () throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'ORG130' AND FUNCTIONID = 'confirm') "); 
		sb.append("AND ROLEID = :roleID ");
		
		return sb;
	}
	
	/*
	 * 取得可執行編輯的群組
	 */
	public StringBuffer getMaintenanceList () throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'ORG130' AND FUNCTIONID = 'maintenance') "); 
		sb.append("AND ROLEID = :roleID ");
		
		return sb;
	}
	
	//判斷是否為'013'群中的人
	//判斷是否為總行
	//判斷是否為督導
	public void login(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVO3 = (ORG130InputVO) body;
		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT CASE WHEN PRIVILEGEID IN (").append(getReviewList()).append(") THEN 1 ");
		sql.append("            WHEN PRIVILEGEID IN (").append(getMaintenanceList()).append(") THEN 0 ");
		sql.append("       ELSE 2 END AS COUNTS ");
		sql.append("FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE ROLEID = :roleID ");
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("roleID", inputVO3.getRoleID());

		return_VO.setRoleID(dam.exeQuery(queryCondition));
		boolean isHeadMgr	=	this.isHeadMgr();//是否為總行
		boolean isSupervisor	=	this.isSupervisor();//是否為督導
		return_VO.setHeadMgr(isHeadMgr);
		return_VO.setSupervisor(isSupervisor);
		sendRtnObject(return_VO);
	}

	//==登入者之區域中心、營運區、分行、ID、姓名
	public void initial (Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVO3 = (ORG130InputVO) body;
		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID AS INTV_EMP_ID, EMP_NAME AS INTV_EMP_NAME ");
		sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sql.append("WHERE 1=1 ");
		sql.append("AND EMP_ID = :emp_id ");
			
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("emp_id", inputVO3.getLogin_id().trim());
		List list = dam.exeQuery(queryCondition);
		
		return_VO.setResultList(list);
		sendRtnObject(return_VO);

	}

	//==登入者是否為總行
	private boolean isHeadMgr () throws JBranchException {
		ORG130OutputVO return_VO = new ORG130OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行

		String role = (String)getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		if(headmgrMap.containsKey(role))
			return true;
		return false;
	}	
	//查詢
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		logger.info("ORG130 偵錯 LOG...");
		ORG130InputVO inputVO = (ORG130InputVO) body;
		ORG130OutputVO return_VO2 = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		queryCondition.setQueryString(getReviewList().toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List<Map<String, Object>> privilege = dam.exeQuery(queryCondition);
		
		StringBuffer sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sql.append("WITH BASE AS ( ");
		sql.append("SELECT ");
		if(privilege.size() > 0){//登入者為覆核主管
			sql.append(" HOL.SEQNO, ");
		}else{ 
			sql.append(" 0 AS SEQNO, ");
		}
		sql.append("  HOL.EV_DATE ED, ");
		sql.append("  HOL.HONEST, HOL.KINDLY, HOL.PROFESSION, HOL.INNOVATION, HOL.COMMUNICATION COMM, HOL.PROBLEM_SOLVING_SKILL PSS, ");
		sql.append("  HOL.ORGANIZE ORG, HOL.SELF_IMPROVE SI, HOL.RESIGN_REASON RR, HOL.EXPECTATION, HOL.BOOKED_ONBOARD_DATE BOD, HOL.EXP_SALARY, ");
		sql.append("  HOL.INTV_SUP_REMARK ISR, HOL.PRE_MONTHLY_GOAL PMG, HOL.PRE_6M_ACCOMPLISH P6A, HOL.EXP_6M_ABILITY E6A, "); 
		sql.append("  HOL.EXP_1Y_ABILITY E1A, HOL.EXP_PROPERTY_LOAN EPL, HOL.EXP_CREDIT_LOAN ECL, HOL.SUGGEST_JOB SJ, HOL.HIRE_STATUS HS, "); 
		sql.append("  HOL.HIRE_STATUS_TRANS_REMARK HSTR, HOL.PERF_TYPE PT, ");
		sql.append("  HOL.BU_LAYER1 BL1, HOL.BU_LAYER2 BL2, HOL.BU_LAYER3 BL3, HOL.BU_LAYER4 BL4, ");
		sql.append("  HOL.BU_LAYER5 BL5, HOL.BU_LAYER6 BL6, HOL.BU_LAYER7 BL7, HOL.BU_LAYER8 BL8, "); 
		sql.append("  HOL.CROS_BU_LAYER1 CBL1, HOL.CROS_BU_LAYER2 CBL2, HOL.CROS_BU_LAYER3 CBL3, HOL.CROS_BU_LAYER4 CBL4, ");
		sql.append("  HOL.CROS_BU_LAYER5 CBL5, HOL.CROS_BU_LAYER6 CBL6, HOL.CROS_BU_LAYER7 CBL7, HOL.CROS_BU_LAYER8 CBL8, ");
		
		// add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
		sql.append("  HOL.CREDIT_STATUS, HOL.CREDIT_DTL, ");
		
		sql.append("  A.CUST_ID, A.EMP_ID, A.EMP_NAME, A.INTV_EMP_ID, A.INTV_EMP_NAME, DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, "); 
		sql.append("  A.BRCH_RECV_CASE_DATE, A.HO_RECV_CASE_DATE, A.OA_SUP_RT_DATE, A.BRCH_INI_INT_DATE, ");
		sql.append("  A.STATUS, (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.HIRE_STATUS' AND PARAM_CODE = A.STATUS) AS STATUS_NAME, ");
		sql.append("  A.BOOKED_ONBOARD_DATE, A.JOB_RANK, A.JOB_TITLE_NAME, A.AO_JOB_RANK, ");
		sql.append("  A.ADA_TEST_DATE, A.NO_SHOW_REASON, A.RT_RESULT, A.RECRUIT_REMARK, ");
		sql.append("  A.BLACK_LISTED, (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'COMMON.YES_NO' AND PARAM_CODE = A.BLACK_LISTED) AS BLACK_LISTED_NAME, ");
		sql.append("  A.RESIGN_RETURN, A.RETURN_REMARK, A.PREV_JOB_EXP, A.CUR_JOB, A.CUR_JOB_NAME, A.CUR_AUM, A.CUR_MONTHLY_GOAL, A.ACTUAL_ACCOMPLISH, A.CUR_FEE_INCOME, A.CUST_CNT, ");
		sql.append("  A.AO_YEAR_OF_EXP, A.ABLE_ONBOARD_DATE, A.PREV_JOB, A.RC_SUP_EMP_ID, A.OP_SUP_EMP_ID, A.HR, A.TRANS_FROM_BRANCH_ID, A.TRANS_REMARK, A.RESUME_SOURCE, (AA.EMP_ID) AS RECOMMEN_EMP_ID, A.RECOMMENDER_EMP_ID, ");
		sql.append("  A.RECOMMEND_AWARDEE_EMP_ID, A.RECOMMEND_LETTER, A.REQ_CERTIFICATE, A.FINANCIAL_EXP, A.PRE_FIN_INST, A.OTHER_FI, A.OTHER_PRE_FIN_INST, A.RESIGN_REASON, A.CUST_SATISFACTION, ");
		sql.append("  A.ACHIEVEMENT, A.SALES_SKILL, A.ACTIVE, A.PRESSURE_MANAGE, A.COMMUNICATION, A.PROBLEM_SOLVING_SKILL, A.INTV_SUP_REMARK, ");
		sql.append("  A.FEE_6M_ABILITY, A.FEE_1Y_ABILITY, A.SUGGEST_JOB, A.SUGGEST_SALARY, ");
		sql.append("  A.HIRE_STATUS, (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.FINAL_HIRE_STATUS' AND PARAM_CODE = A.HIRE_STATUS) AS HIRE_STATUS_NAME, ");
		sql.append("  A.REVIEW_STATUS, A.FHC_SEQ, ");
		sql.append("  A.CREATETIME, A.CREATOR, A.MODIFIER,  A.LASTUPDATE, A.SEQ ");
		sql.append("FROM TBORG_EMP_HIRE_INFO A ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO B ON A.INTV_EMP_ID = B.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER AA ON A.RECOMMENDER_EMP_ID = AA.EMP_ID ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = A.BRANCH_NBR ");
		sql.append("LEFT JOIN TBORG_EMP_HIRE_REVIEW_HOLDING HOL ON A.CUST_ID = HOL.CUST_ID AND A.BRANCH_NBR = HOL.BRANCH_NBR AND A.FHC_SEQ = HOL.SEQNO ");
		sql.append("WHERE (A.EMP_TYPE='2' OR A.EMP_TYPE IS NULL)	");

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sql.append("AND A.REGION_CENTER_ID = :regionCenterID ");
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			params.put("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sql.append("AND A.REGION_CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			params.put("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sql.append("AND A.BRANCH_AREA_ID = :branchAreaID ");
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			params.put("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sql.append("AND A.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			params.put("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sql.append("AND A.BRANCH_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
			params.put("branchID", inputVO.getBranch_nbr());
		} else {
			sql.append("AND A.BRANCH_NBR IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			params.put("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		if(inputVO.getRecv_case_sdate() != null && inputVO.getRecv_case_edate() != null ){		
			sql.append(" AND A.BRCH_RECV_CASE_DATE BETWEEN :recv_start AND :recv_end ");
			queryCondition.setObject("recv_start", inputVO.getRecv_case_sdate());
			params.put("recv_start", inputVO.getRecv_case_sdate());
			queryCondition.setObject("recv_end", inputVO.getRecv_case_edate());
			params.put("recv_end", inputVO.getRecv_case_edate());
		}
		
		if(inputVO.getBook_onbo_sdate() != null && inputVO.getBook_onbo_edate() != null ){	
			sql.append(" AND A.BOOKED_ONBOARD_DATE BETWEEN :book_start AND :book_end ");
			queryCondition.setObject("book_start", inputVO.getBook_onbo_sdate());
			params.put("book_start", inputVO.getBook_onbo_sdate());
			queryCondition.setObject("book_end", inputVO.getBook_onbo_edate());
			params.put("book_end", inputVO.getBook_onbo_edate());
		}
		
		if(StringUtils.isNotBlank(inputVO.getEmp_name())){
			sql.append(" AND A.EMP_NAME LIKE :emp_name " );
			queryCondition.setObject("emp_name", inputVO.getEmp_name().trim());
			params.put("emp_name", inputVO.getEmp_name().trim());
		}
		
		if(StringUtils.isNotBlank(inputVO.getStatus())){
			sql.append(" AND A.STATUS = :status ");
			queryCondition.setObject("status", inputVO.getStatus());
			params.put("status", inputVO.getStatus());
		}

		sql.append(") ");
		sql.append("SELECT A.CUST_ID, ");
		sql.append("       A.EMP_ID, ");
		sql.append("       A.EMP_NAME, ");
		sql.append("       A.INTV_EMP_ID, ");
		sql.append("       A.INTV_EMP_NAME, ");
		sql.append("       A.REGION_CENTER_ID, ");
		sql.append("       A.REGION_CENTER_NAME, ");
		sql.append("       A.BRANCH_AREA_ID, ");
		sql.append("       A.BRANCH_AREA_NAME, ");
		sql.append("       A.BRANCH_NBR, ");
		sql.append("       A.BRANCH_NAME, ");
		sql.append("       A.BRCH_RECV_CASE_DATE, ");
		sql.append("       A.HO_RECV_CASE_DATE, ");
		sql.append("       A.OA_SUP_RT_DATE, ");
		sql.append("       A.BRCH_INI_INT_DATE, ");
		sql.append("       A.STATUS, ");
		sql.append("       A.STATUS_NAME, ");
		sql.append("       A.BOOKED_ONBOARD_DATE, ");
		sql.append("       A.JOB_RANK, ");
		sql.append("       A.JOB_TITLE_NAME, ");
		sql.append("       A.AO_JOB_RANK, ");
		sql.append("       A.ADA_TEST_DATE, ");
		sql.append("       A.NO_SHOW_REASON, ");
		sql.append("       A.RT_RESULT, ");
		sql.append("       A.RECRUIT_REMARK, ");
		sql.append("       A.BLACK_LISTED, ");
		sql.append("       A.BLACK_LISTED_NAME, ");
		sql.append("       A.RESIGN_RETURN, ");
		sql.append("       A.RETURN_REMARK, ");
		sql.append("       A.PREV_JOB_EXP, ");
		sql.append("       A.CUR_JOB, ");
		sql.append("       A.CUR_JOB_NAME, ");
		sql.append("       A.CUR_AUM, ");
		sql.append("       A.CUR_MONTHLY_GOAL, ");
		sql.append("       A.ACTUAL_ACCOMPLISH, ");
		sql.append("       A.CUR_FEE_INCOME, ");
		sql.append("       A.CUST_CNT, ");
		sql.append("       A.AO_YEAR_OF_EXP, ");
		sql.append("       A.ABLE_ONBOARD_DATE, ");
		sql.append("       A.PREV_JOB, ");
		sql.append("       A.RC_SUP_EMP_ID, ");
		sql.append("       A.OP_SUP_EMP_ID, ");
		sql.append("       A.HR, ");
		sql.append("       A.TRANS_FROM_BRANCH_ID, ");
		sql.append("       A.TRANS_REMARK, ");
		sql.append("       A.RESUME_SOURCE, ");
		sql.append("       A.RECOMMEN_EMP_ID, ");
		sql.append("       A.RECOMMENDER_EMP_ID, ");
		sql.append("       M.RECOMMENDER_EMP_NAME, ");
		sql.append("       A.RECOMMEND_AWARDEE_EMP_ID, ");
		sql.append("       A.RECOMMEND_LETTER, ");
		sql.append("       A.REQ_CERTIFICATE, ");
		sql.append("       A.FINANCIAL_EXP, ");
		sql.append("       A.PRE_FIN_INST, ");
		sql.append("       A.OTHER_FI, ");
		sql.append("       A.OTHER_PRE_FIN_INST, ");
		sql.append("       A.RESIGN_REASON, ");
		sql.append("       A.CUST_SATISFACTION, ");
		sql.append("       A.ACHIEVEMENT, ");
		sql.append("       A.SALES_SKILL, ");
		sql.append("       A.ACTIVE, ");
		sql.append("       A.PRESSURE_MANAGE, ");
		sql.append("       A.COMMUNICATION, ");
		sql.append("       A.PROBLEM_SOLVING_SKILL, ");
		sql.append("       A.INTV_SUP_REMARK, ");
		sql.append("       A.HIRE_STATUS, ");
		sql.append("       A.HIRE_STATUS_NAME, ");
		sql.append("       A.REVIEW_STATUS, ");
		sql.append("       A.FEE_6M_ABILITY, ");
		sql.append("       A.FEE_1Y_ABILITY, ");
		sql.append("       A.SUGGEST_JOB, ");
		sql.append("       A.SUGGEST_SALARY, ");
		sql.append("       ED, HONEST, KINDLY, PROFESSION, INNOVATION, COMM, PSS, ");
		sql.append("       ORG, SI, RR, EXPECTATION, BOD, EXP_SALARY, ");
		sql.append("       ISR, PMG, P6A, E6A, "); 
		sql.append("       E1A, EPL, ECL, SJ, HS, "); 
		sql.append("       HSTR, PT, ");
		sql.append("       BL1, BL2, BL3, BL4, ");
		sql.append("       BL5, BL6, BL7, BL8, "); 
		sql.append("       CBL1, CBL2, CBL3, CBL4, ");
		sql.append("       CBL5, CBL6, CBL7, CBL8, ");
		// add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
		sql.append("       CREDIT_STATUS, ");
		sql.append("       CREDIT_DTL, ");
		
		sql.append("       A.SEQNO, A.FHC_SEQ, ");
		sql.append("       A.CREATETIME, ");
		sql.append("       A.CREATOR, ");
		sql.append("       A.MODIFIER, ");
		sql.append("       A.LASTUPDATE, A.SEQ ");
		
		sql.append("FROM( ");
		sql.append("  SELECT CUST_ID, EMP_ID, EMP_NAME, INTV_EMP_ID, INTV_EMP_NAME, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sql.append("         BRCH_RECV_CASE_DATE, HO_RECV_CASE_DATE, OA_SUP_RT_DATE, BRCH_INI_INT_DATE, ");
		sql.append("         STATUS, STATUS_NAME, ");
		sql.append("         BOOKED_ONBOARD_DATE, JOB_RANK, JOB_TITLE_NAME, AO_JOB_RANK, ");
		sql.append("         ADA_TEST_DATE, NO_SHOW_REASON, RT_RESULT, RECRUIT_REMARK, ");
		sql.append("         BLACK_LISTED, BLACK_LISTED_NAME, ");
		sql.append("         RESIGN_RETURN, RETURN_REMARK, PREV_JOB_EXP, CUR_JOB, CUR_JOB_NAME, CUR_AUM, CUR_MONTHLY_GOAL, ACTUAL_ACCOMPLISH, CUR_FEE_INCOME, CUST_CNT, ");
		sql.append("         AO_YEAR_OF_EXP, ABLE_ONBOARD_DATE, PREV_JOB, RC_SUP_EMP_ID, OP_SUP_EMP_ID, HR, ");
		sql.append("         TRANS_FROM_BRANCH_ID, TRANS_REMARK, RESUME_SOURCE, RECOMMEN_EMP_ID, RECOMMENDER_EMP_ID, ");
		sql.append("         RECOMMEND_AWARDEE_EMP_ID, RECOMMEND_LETTER, REQ_CERTIFICATE, FINANCIAL_EXP, PRE_FIN_INST, OTHER_FI, OTHER_PRE_FIN_INST, RESIGN_REASON, CUST_SATISFACTION, ");
		sql.append("         ACHIEVEMENT, SALES_SKILL, ACTIVE, PRESSURE_MANAGE, COMMUNICATION, PROBLEM_SOLVING_SKILL, INTV_SUP_REMARK, ");
		sql.append("         HIRE_STATUS, HIRE_STATUS_NAME, REVIEW_STATUS, ");
		sql.append("         FEE_6M_ABILITY, FEE_1Y_ABILITY, SUGGEST_JOB, SUGGEST_SALARY, ");
		sql.append("         ED, HONEST, KINDLY, PROFESSION, INNOVATION, COMM, PSS, ");
		sql.append("         ORG, SI, RR, EXPECTATION, BOD, EXP_SALARY, ");
		sql.append("         ISR, PMG, P6A, E6A, "); 
		sql.append("         E1A, EPL, ECL, SJ, HS, "); 
		sql.append("         HSTR, PT, ");
		sql.append("         BL1, BL2, BL3, BL4, ");
		sql.append("         BL5, BL6, BL7, BL8, "); 
		sql.append("         CBL1, CBL2, CBL3, CBL4, ");
		sql.append("         CBL5, CBL6, CBL7, CBL8, ");
		// add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
		sql.append("         CREDIT_STATUS, ");
		sql.append("         CREDIT_DTL, ");
		
		sql.append("         SEQNO, FHC_SEQ, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, SEQ ");
		sql.append("  FROM BASE ");
		sql.append("  UNION ");
		sql.append("  SELECT A.CUST_ID, A.EMP_ID, A.EMP_NAME, A.INTV_EMP_ID, A.INTV_EMP_NAME, DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, "); 
		sql.append("         A.BRCH_RECV_CASE_DATE, A.HO_RECV_CASE_DATE, A.OA_SUP_RT_DATE, A.BRCH_INI_INT_DATE, ");
		sql.append("         A.STATUS, (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.HIRE_STATUS' AND PARAM_CODE = A.STATUS) AS STATUS_NAME, ");
		sql.append("         A.BOOKED_ONBOARD_DATE, A.JOB_RANK, A.JOB_TITLE_NAME, A.AO_JOB_RANK, ");
		sql.append("         A.ADA_TEST_DATE, A.NO_SHOW_REASON, A.RT_RESULT, A.RECRUIT_REMARK, ");
		sql.append("         A.BLACK_LISTED, (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'COMMON.YES_NO' AND PARAM_CODE = A.BLACK_LISTED) AS BLACK_LISTED_NAME, ");
		sql.append("         A.RESIGN_RETURN, A.RETURN_REMARK, A.PREV_JOB_EXP, A.CUR_JOB, A.CUR_JOB_NAME, A.CUR_AUM, A.CUR_MONTHLY_GOAL, A.ACTUAL_ACCOMPLISH, A.CUR_FEE_INCOME, A.CUST_CNT, ");
		sql.append("         A.AO_YEAR_OF_EXP, A.ABLE_ONBOARD_DATE, A.PREV_JOB, A.RC_SUP_EMP_ID, A.OP_SUP_EMP_ID, A.HR, ");
		sql.append("         A.TRANS_FROM_BRANCH_ID, A.TRANS_REMARK, A.RESUME_SOURCE, (AA.EMP_ID) AS RECOMMEN_EMP_ID, A.RECOMMENDER_EMP_ID, ");
		sql.append("         A.RECOMMEND_AWARDEE_EMP_ID, A.RECOMMEND_LETTER, A.REQ_CERTIFICATE, A.FINANCIAL_EXP, A.PRE_FIN_INST, A.OTHER_FI, A.OTHER_PRE_FIN_INST, A.RESIGN_REASON, A.CUST_SATISFACTION, ");
		sql.append("         A.ACHIEVEMENT, A.SALES_SKILL, A.ACTIVE, PRESSURE_MANAGE, A.COMMUNICATION, A.PROBLEM_SOLVING_SKILL, A.INTV_SUP_REMARK, ");
		sql.append("         A.HIRE_STATUS, (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.FINAL_HIRE_STATUS' AND PARAM_CODE = A.HIRE_STATUS) AS HIRE_STATUS_NAME, A.REVIEW_STATUS, ");
		sql.append("         A.FEE_6M_ABILITY, A.FEE_1Y_ABILITY, A.SUGGEST_JOB, A.SUGGEST_SALARY, ");
		sql.append("         NULL ED, NULL HONEST, NULL KINDLY, NULL PROFESSION, NULL INNOVATION, NULL COMM, NULL PSS, ");
		sql.append("         NULL ORG, NULL SI, NULL RR, NULL EXPECTATION, NULL BOD, NULL EXP_SALARY, ");
		sql.append("         NULL ISR, NULL PMG, NULL P6A, NULL E6A, "); 
		sql.append("         NULL E1A, NULL EPL, NULL ECL, NULL SJ, NULL HS, "); 
		sql.append("         NULL HSTR, NULL PT, ");
		sql.append("         NULL BL1, NULL BL2, NULL BL3, NULL BL4, ");
		sql.append("         NULL BL5, NULL BL6, NULL BL7, NULL BL8, "); 
		sql.append("         NULL CBL1, NULL CBL2, NULL CBL3, NULL CBL4, ");
		sql.append("         NULL CBL5, NULL CBL6, NULL CBL7, NULL CBL8, ");
		// add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
		sql.append("         NULL CREDIT_STATUS, ");
		sql.append("         NULL CREDIT_DTL, ");
		
		sql.append("         NULL AS SEQNO, A.FHC_SEQ, A.CREATETIME, A.CREATOR, A.MODIFIER,  A.LASTUPDATE, A.SEQ ");
		sql.append("  FROM TBORG_EMP_HIRE_INFO A ");
		sql.append("  LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO B ON A.INTV_EMP_ID = B.EMP_ID ");
		sql.append("  LEFT JOIN TBORG_MEMBER AA ON A.RECOMMENDER_EMP_ID = AA.EMP_ID ");
		sql.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON DEFN.BRANCH_NBR = A.BRANCH_NBR ");
		sql.append("  WHERE 1 = 1 ");
		sql.append("  AND A.CUST_ID NOT IN (SELECT CUST_ID FROM BASE)");
		sql.append("	AND	(A.EMP_TYPE	=	'2'	OR A.EMP_TYPE IS NULL)	");
		sql.append(") A ");
		sql.append("LEFT JOIN (SELECT EMP_ID AS RECOMMENDER_EMP_ID, EMP_NAME AS RECOMMENDER_EMP_NAME FROM TBORG_MEMBER) M ON A.RECOMMENDER_EMP_ID = M.RECOMMENDER_EMP_ID ");

		sql.append("WHERE 1 = 1 ");		
		
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sql.append("AND (A.REGION_CENTER_ID = :regionCenterID) "); // OR A.STATUS IN ('08','09','10')
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			params.put("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sql.append("AND (A.REGION_CENTER_ID IN (:regionCenterIDList)) "); // OR A.STATUS IN ('08','09','10')
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			params.put("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sql.append("AND (A.BRANCH_AREA_ID = :branchAreaID) "); // OR A.STATUS IN ('08','09','10')
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			params.put("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sql.append("AND (A.BRANCH_AREA_ID IN (:branchAreaIDList)) "); //OR A.STATUS IN ('08','09','10')
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			params.put("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sql.append("AND (A.BRANCH_NBR = :branchID) "); //OR A.STATUS IN ('08','09','10')
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
			params.put("branchID", inputVO.getBranch_nbr());
		} else {
			sql.append("AND (A.BRANCH_NBR IN (:branchIDList)) "); //OR A.STATUS IN ('08','09','10')
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			params.put("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		if(inputVO.getRecv_case_sdate() != null ){		
			sql.append(" AND TO_CHAR(A.BRCH_RECV_CASE_DATE,'YYYYMMDD') >= TO_CHAR(:recv_start,'YYYYMMDD') ");
			queryCondition.setObject("recv_start", inputVO.getRecv_case_sdate());
			params.put("recv_start", inputVO.getRecv_case_sdate());
		}
		
		if( inputVO.getRecv_case_edate() != null){
			sql.append(" AND TO_CHAR(A.BRCH_RECV_CASE_DATE,'YYYYMMDD') <= TO_CHAR(:recv_end,'YYYYMMDD') ");
			queryCondition.setObject("recv_end", inputVO.getRecv_case_edate());
			params.put("recv_end", inputVO.getRecv_case_edate());
		}
		
		if(inputVO.getBook_onbo_sdate() != null   ){	
			sql.append(" AND TO_CHAR(A.BOOKED_ONBOARD_DATE,'YYYYMMDD') >= TO_CHAR(:book_start,'YYYYMMDD') ");
			queryCondition.setObject("book_start", inputVO.getBook_onbo_sdate());
			params.put("book_start", inputVO.getBook_onbo_sdate());
		}
		if(inputVO.getBook_onbo_edate() != null){
			sql.append(" AND TO_CHAR(A.BOOKED_ONBOARD_DATE,'YYYYMMDD') <= TO_CHAR(:book_end,'YYYYMMDD') ");
			queryCondition.setObject("book_end", inputVO.getBook_onbo_edate());
			params.put("book_end", inputVO.getBook_onbo_edate());
		}
		
		if(StringUtils.isNotBlank(inputVO.getEmp_name())){
			sql.append(" AND A.EMP_NAME LIKE :emp_name " );
			queryCondition.setObject("emp_name", inputVO.getEmp_name().trim());
			params.put("emp_name", inputVO.getEmp_name().trim());
		}
		
		if(StringUtils.isNotBlank(inputVO.getStatus())){
			sql.append(" AND A.STATUS = :status ");
			queryCondition.setObject("status", inputVO.getStatus());
			params.put("status", inputVO.getStatus());
		}
		
		sql.append("ORDER BY CASE WHEN A.REVIEW_STATUS = 'W' THEN 0 ELSE 1 END, A.LASTUPDATE DESC, A.REGION_CENTER_ID, A.BRANCH_AREA_ID, A.BRANCH_NBR, A.CREATETIME DESC ");
		
		queryCondition.setQueryString(sql.toString());
		logger.info("ORG130 SQL：" + sql.toString());
		logger.info("ORG130 參數：" + params);

		return_VO2.setResultList2(dam.exeQuery(queryCondition));
		return_VO2.setCsvList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO2);
	}
	
	//== 新增，建立一筆資料
	public void add(Object body, IPrimitiveMap header) throws Exception {
		
		ORG130InputVO inputVO = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		
		StringBuffer sql = new StringBuffer();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sql.append(" SELECT '1' as rewcust ");
		sql.append(" FROM TBORG_EMP_HIRE_INFO ");
		sql.append(" WHERE CUST_ID = :custid ");
		sql.append(" AND BRANCH_NBR = :branchNbr ");
		sql.append(" AND STATUS NOT IN ('08', '09', '10')");
		
		queryCondition.setObject("custid", inputVO.getCust_id());
		queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
		
		queryCondition.setQueryString(sql.toString());
		
		List list = dam.exeQuery(queryCondition);
		
		TBORG_EMP_HIRE_INFOVO vo = new TBORG_EMP_HIRE_INFOVO();
		TBORG_EMP_HIRE_INFOPK pk = new TBORG_EMP_HIRE_INFOPK();
			
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		
		if (list.size() == 0) {
			pk = new TBORG_EMP_HIRE_INFOPK();
			pk.setCUST_ID(inputVO.getCust_id().trim());
			pk.setBRANCH_NBR(inputVO.getBranch_nbr().trim());
			pk.setSEQ(getInfoSeq());
			vo = new TBORG_EMP_HIRE_INFOVO();
			vo.setcomp_id(pk);
			vo.setEMP_NAME(inputVO.getEmp_name().trim());
			vo.setINTV_EMP_ID(inputVO.getIntv_emp_id().trim());
			vo.setINTV_EMP_NAME(inputVO.getIntv_emp_name().trim());
			vo.setSTATUS("01");
			
			if (null != inputVO.getsTime()) {
				long aa = Long.parseLong(inputVO.getsTime());
				Date date = new Timestamp(inputVO.getBrch_ini_int_date().getTime() + aa + 28800000);
				vo.setBRCH_INI_INT_DATE((Timestamp) date);
			} else {
				vo.setBRCH_INI_INT_DATE(new Timestamp(inputVO.getBrch_ini_int_date().getTime()));
			}
			
			vo.setBRCH_RECV_CASE_DATE(currentTM);
			vo.setREGION_CENTER_ID(inputVO.getRegion_center_id().trim());
			vo.setBRANCH_AREA_ID(inputVO.getBranch_area_id().trim());
			vo.setHIRE_STATUS("0");
			vo.setCreatetime(currentTM);
			vo.setCreator(inputVO.getIntv_emp_id());
			vo.setCreatetime(currentTM);
			vo.setModifier(inputVO.getIntv_emp_id());
			vo.setLastupdate(currentTM);
//			vo1.setRC_SUP_EMP_ID(inputVO1.getRc_sup_emp_id().trim());
//			vo1.setOP_SUP_EMP_ID(inputVO1.getOp_sup_emp_id().trim());
			dam.create(vo);

			//EMP_TYPE判斷一般(1)或業務人員(2)
			StringBuilder sql2 = new StringBuilder();
			QueryConditionIF qcIf = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			sql2.append("UPDATE TBORG_EMP_HIRE_INFO SET EMP_TYPE = '2' ");
			sql2.append("	WHERE	CUST_ID	=	:custId	AND	BRANCH_NBR	=	:branchNbr AND	SEQ	=	:seq	");
			qcIf.setObject("custId", inputVO.getCust_id());
			qcIf.setObject("branchNbr", inputVO.getBranch_nbr());
			qcIf.setObject("seq", pk.getSEQ());
			qcIf.setQueryString(sql2.toString());
			dam.exeUpdate(qcIf);
		}
		
		inputVO.setStatus("01");
		
		/*** 取得面試人員資料 start ***/
		inputVO.setEmp_name(vo.getEMP_NAME());
		inputVO.setAo_job_rank(vo.getAO_JOB_RANK());
		/*** 取得面試人員資料 end ***/
		
		// 發送mail所需參數
		ORG130OutputVO outputVO = new ORG130OutputVO();		
							
		List<Map<String, String>> mailList = getMailList(inputVO.getIntv_emp_id(), inputVO, outputVO);
									
		if(mailList.size() > 0) {
//			sendEmail(mailList, inputVO);
		} else {
			outputVO.setErrorMsg("發送失敗");
		}	
		sendRtnObject(outputVO);
			
		//this.sendRtnObject(null);
	}

	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG130InputVO inputVO = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb = new StringBuilder();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("DELETE TBORG_EMP_HIRE_INFO ");
		sb.append("WHERE CUST_ID = :custId	");
		sb.append("AND	BRANCH_NBR = :braNbr ");
		sb.append("AND	SEQ = :seq ");
		queryCondition.setObject("custId", inputVO.getCust_id().trim());
		queryCondition.setObject("braNbr", inputVO.getBranch_nbr().trim());
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		sb = new StringBuilder();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("DELETE TBORG_EMP_HIRE_INFO_REVIEW ");
		sb.append("WHERE CUST_ID = :custId	");
		sb.append("AND	BRANCH_NBR = :braNbr");
		queryCondition.setObject("custId", inputVO.getCust_id().trim());
		queryCondition.setObject("braNbr", inputVO.getBranch_nbr().trim());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);

		sb	= new StringBuilder();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("DELETE TBORG_EMP_HIRE_REVIEW_HOLDING ");
		sb.append("WHERE CUST_ID = :custId ");
		sb.append("AND BRANCH_NBR = :braNbr");
		queryCondition.setObject("custId", inputVO.getCust_id().trim());
		queryCondition.setObject("braNbr", inputVO.getBranch_nbr().trim());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	//==區域中心主管、營運區督導
	public void manager(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG130InputVO inputVO4 = (ORG130InputVO) body;
		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("WITH BASE AS ( ");
		sql.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
		sql.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sql.append("  UNION ");
		sql.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
		sql.append("  FROM VWORG_EMP_PLURALISM_INFO ");
		sql.append(") ");
		sql.append(", EMP_BASE AS ( ");
		sql.append("  SELECT M.EMP_ID, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, 'Y' AS ROLE_TYPE ");
		sql.append("  FROM TBORG_MEMBER M ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sql.append("    FROM TBORG_DEFN ");
		sql.append("    START WITH DEPT_ID IS NOT NULL ");
		sql.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sql.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sql.append("  WHERE M.SERVICE_FLAG = 'A' ");
		sql.append("  AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sql.append("  UNION ");
		sql.append("  SELECT M.EMP_ID, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, 'N' AS ROLE_TYPE ");
		sql.append("  FROM TBORG_MEMBER_PLURALISM M ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sql.append("    FROM TBORG_DEFN ");
		sql.append("    START WITH DEPT_ID IS NOT NULL ");
		sql.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sql.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sql.append("  WHERE (TRUNC(M.TERDTE) >= TRUNC(SYSDATE) OR M.TERDTE IS NULL) ");
		sql.append("  AND M.ACTION <> 'D' ");
		sql.append(") ");
		sql.append(", ROLE_EMP_LIST AS ( ");
		sql.append("  SELECT MR.ROLE_ID, PRI.PRIVILEGEID, EL.EMP_ID, EL.DEPT_ID, EL.ORG_TYPE, EL.DEPT_NAME ");
		sql.append("  FROM EMP_BASE EL ");
		sql.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON EL.ROLE_TYPE = 'Y' AND EL.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sql.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
		sql.append("  UNION ");
		sql.append("  SELECT MR.ROLE_ID, PRI.PRIVILEGEID, EL.EMP_ID, EL.DEPT_ID, EL.ORG_TYPE, EL.DEPT_NAME ");
		sql.append("  FROM EMP_BASE EL ");
		sql.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON EL.ROLE_TYPE = 'N' AND EL.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'N' ");
		sql.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
		sql.append(") ");
		sql.append(", EMP_LIST AS ( ");
		sql.append("  SELECT C.EMP_ID, C.ROLE_ID, C.PRIVILEGEID, C.REGION_CENTER_ID, D.REGION_CENTER_NAME, C.BRANCH_AREA_ID, D.BRANCH_AREA_NAME, C.BRANCH_NBR, D.BRANCH_NAME ");
		sql.append("  FROM ( ");
		sql.append("    SELECT EMP_ID, ROLE_ID, PRIVILEGEID, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ");
		sql.append("    FROM ( ");
		sql.append("      SELECT EMP_ID, ROLE_ID, PRIVILEGEID, ORG_TYPE, DEPT_ID ");
		sql.append("      FROM ROLE_EMP_LIST ");
		sql.append("    ) PIVOT (MAX(DEPT_ID) FOR ORG_TYPE IN ('30' AS REGION_CENTER_ID, '40' AS BRANCH_AREA_ID, '50' AS BRANCH_NBR)) ");
		sql.append("  ) C ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT EMP_ID, ROLE_ID, PRIVILEGEID, REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NAME ");
		sql.append("    FROM ( ");
		sql.append("      SELECT EMP_ID, ROLE_ID, PRIVILEGEID, ORG_TYPE, DEPT_NAME ");
		sql.append("      FROM ROLE_EMP_LIST ");
		sql.append("    ) PIVOT (MAX(DEPT_NAME) FOR ORG_TYPE IN ('30' AS REGION_CENTER_NAME, '40' AS BRANCH_AREA_NAME, '50' AS BRANCH_NAME)) ");
		sql.append("  ) D ON C.EMP_ID = D.EMP_ID AND C.ROLE_ID = D.ROLE_ID AND C.PRIVILEGEID = D.PRIVILEGEID ");
		sql.append(") ");
		
		// 取得業務處處長list
		StringBuffer sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append(sql.toString());
		
		sb.append("SELECT RC.EMP_ID AS RC_SUP_EMP_ID, M.EMP_NAME AS RC_SUP_EMP_NAME ");
		sb.append("FROM EMP_LIST RC ");
		sb.append("LEFT JOIN TBORG_MEMBER M ON RC.EMP_ID = M.EMP_ID ");
		sb.append("WHERE RC.REGION_CENTER_ID = (SELECT REGION_CENTER_ID FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :emp_id) ");
		sb.append("AND RC.PRIVILEGEID = '013' ");
		queryCondition.setObject("emp_id", inputVO4.getLogin_id().trim());
		queryCondition.setQueryString(sb.toString());
		
		return_VO.setRcList(dam.exeQuery(queryCondition));
		
		// 取得營運區督導list
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append(sql.toString());
		
		sb.append("SELECT RC.EMP_ID AS OP_SUP_EMP_ID, M.EMP_NAME AS OP_SUP_EMP_NAME ");
		sb.append("FROM EMP_LIST RC ");
		sb.append("LEFT JOIN TBORG_MEMBER M ON RC.EMP_ID = M.EMP_ID ");
		sb.append("WHERE RC.BRANCH_AREA_ID = (SELECT BRANCH_AREA_ID FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :emp_id) ");
		sb.append("AND RC.PRIVILEGEID = '012' ");
		queryCondition.setObject("emp_id", inputVO4.getLogin_id().trim());
		queryCondition.setQueryString(sb.toString());
		
		return_VO.setOpList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
	
	//推薦人員編
	public void reempid(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVO5 = (ORG130InputVO) body;
		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 2017/12/13 mantis 4057 why Recommender_emp_id first?
		if(StringUtils.isNotBlank(inputVO5.getRecomm_emp_id())) {
			sql.append("SELECT EMP_NAME, EMP_ID FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", inputVO5.getRecomm_emp_id());
		} else {
			sql.append("SELECT A.RECOMMENDER_EMP_ID AS EMP_ID, B.EMP_NAME AS EMP_NAME ");
			sql.append("FROM TBORG_EMP_HIRE_INFO A ");
			sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO B ON A.RECOMMENDER_EMP_ID = B.EMP_ID ");
			sql.append("WHERE A.RECOMMENDER_EMP_ID = :red " );
			queryCondition.setObject("red", inputVO5.getRecommender_emp_id());
		}
		queryCondition.setQueryString(sql.toString());
		
		return_VO.setResultList4(dam.exeQuery(queryCondition));
		sendRtnObject(return_VO);
	}
	
	//推薦人員姓名
	private String recempid(String empId) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer(
				"	SELECT EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :emp_id	"
				);
		queryCondition.setObject("emp_id", empId);
		queryCondition.setQueryString(sql.toString());
		List<Map<String,Object>> list = dam.exeQuery(queryCondition);
		return list.size()>0?(String)list.get(0).get("EMP_NAME"):"";
		
	}
	
	//區域中心
	public void region(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVO4 = (ORG130InputVO) body;
		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME FROM VWORG_DEFN_INFO GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME ");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList7(list);
		sendRtnObject(return_VO);
		
	}
	
	//營運區
	public void op(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVO4 = (ORG130InputVO) body;
		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DEPT_ID as BRANCH_AREA_ID , DEPT_NAME as BRANCH_AREA_NAME, PARENT_DEPT_ID FROM TBORG_DEFN WHERE PARENT_DEPT_ID in (SELECT REGION_CENTER_ID  "
				+ " FROM VWORG_DEFN_INFO GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME)");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList8(list);
		sendRtnObject(return_VO);
	}
	
	//分行
	public void branch(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVO4 = (ORG130InputVO) body;
		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT DEPT_ID AS BRANCH_NBR , DEPT_NAME AS BRANCH_NAME, PARENT_DEPT_ID FROM TBORG_DEFN  "
				+ " WHERE PARENT_DEPT_ID in (SELECT DEPT_ID FROM TBORG_DEFN  WHERE PARENT_DEPT_ID in (SELECT REGION_CENTER_ID FROM VWORG_DEFN_INFO GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME))");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList9(list);
		sendRtnObject(return_VO);
	}
	
	//暫存
	public void tempedit(Object body, IPrimitiveMap header) throws Exception {
		
		ORG130InputVO inputVO2 = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT STATUS, HO_RECV_CASE_DATE, CREATOR ");
		sql.append("FROM TBORG_EMP_HIRE_INFO ");
		sql.append("	WHERE	CUST_ID	=	:custId	AND	BRANCH_NBR	=	:branchNbr AND	SEQ	=	:seq	");
		cond.setObject("custId", inputVO2.getCust_id());
		cond.setObject("branchNbr", inputVO2.getBranch_nbr());
		cond.setObject("seq", inputVO2.getSeq());
		cond.setQueryString(sql.toString());
		List<Map<String,Object>> list = dam.exeQuery(cond);
		String STATUS = 	list.size()>0?(String)list.get(0).get("STATUS"):"";
		Date	HO_RECV_CASE_DATE	=	list.size()>0?(Date)list.get(0).get("HO_RECV_CASE_DATE"):null;
		
		cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuilder();
		sql.append("	UPDATE	TBORG_EMP_HIRE_INFO	");
		sql.append("	SET	EMP_NAME	=	:empName, ");
		if (inputVO2.getBrch_recv_case_date() != null)
			sql.append("			BRCH_RECV_CASE_DATE	=	:brchRecvCaseDate, ");
		if (inputVO2.getsCreDate() != null)
			sql.append("			BOOKED_ONBOARD_DATE	=	:bookedOnboardDate, ");
		sql.append("				JOB_RANK	=	:jobRank, ");
		if (inputVO2.getTestDate() != null)
			sql.append("			ADA_TEST_DATE	=	:adaTestDate, ");
		sql.append("				AO_JOB_RANK	=	:aoJobRank, NO_SHOW_REASON	=	:noShowReason, ");
		sql.append("				RT_RESULT	=	:rtResult, RECRUIT_REMARK	=	:recruitRemark, ");
		sql.append("				RESIGN_RETURN	=	:resignReturn, BLACK_LISTED	=	:blackListed, ");
		if (inputVO2.getAble_onboard_date()	!=	null)
			sql.append("			ABLE_ONBOARD_DATE	=	:ableOnboardDate, ");
		sql.append("				HR	=	:hr, JOB_TITLE_NAME	=	:jobTitleName, RESUME_SOURCE	=	:resumeSource, ");
		sql.append("				RECOMMENDER_EMP_ID	=	:recommEmpId, RECOMMEND_AWARDEE_EMP_ID	=	:recommAward, RECOMMEND_LETTER	=	:recommLetter, EMP_ID	=	:empId, ");
		if (inputVO2.getHo_recv_case_date()	!=	null)
			sql.append("			HO_RECV_CASE_DATE	=	:hoRecvCaseDate, ");
		if(inputVO2.getStatus().equals("02") && null == inputVO2.getHo_recv_case_date())
			sql.append("			HO_RECV_CASE_DATE	=	:hoRecvCaseDate, ");
		sql.append("				INTV_EMP_ID	=	:intvEmpId, INTV_EMP_NAME	=	:intvEmpName, STATUS	=	:status, ");
		if (inputVO2.getBrch_ini_int_date()	!=	null)
			sql.append("			BRCH_INI_INT_DATE	=	:brchIniIntDate, ");
		sql.append("				RC_SUP_EMP_ID	=	:rcSupEmpId, OP_SUP_EMP_ID	=	:opSupEmpId	");
		if (StringUtils.equals("01", STATUS) || 
			StringUtils.equals("02", STATUS) || 
			StringUtils.equals("03", STATUS) || 
			StringUtils.equals("11", 	STATUS)	||
			StringUtils.equals("13", 	STATUS)) {
			sql.append("		, HIRE_STATUS	=	:hireStatus	");
		}
		sql.append("	WHERE	CUST_ID	=	:custId	AND	BRANCH_NBR	=	:branchNbr AND	SEQ	=	:seq	");
		
		cond.setObject("empName",inputVO2.getEmp_name());
		if (inputVO2.getBrch_recv_case_date() != null)
			cond.setObject("brchRecvCaseDate",(inputVO2.getBrch_recv_case_date() != null) ? new Timestamp(inputVO2.getBrch_recv_case_date().getTime()) : new Timestamp((new Date()).getTime()));
		if (inputVO2.getsCreDate() != null)
			cond.setObject("bookedOnboardDate",(inputVO2.getsCreDate() != null) ? new Timestamp(inputVO2.getsCreDate().getTime()) : null);
		cond.setObject("jobRank",(inputVO2.getJob_rank() != null) ? inputVO2.getJob_rank().trim() : null);
		if (inputVO2.getTestDate() != null)
			cond.setObject("adaTestDate",(inputVO2.getTestDate() != null) ? new Timestamp(inputVO2.getTestDate().getTime()) : null);
		cond.setObject("aoJobRank",(StringUtils.isNotBlank(inputVO2.getAo_job_rank())) ? inputVO2.getAo_job_rank().trim() : null);
		cond.setObject("noShowReason",(StringUtils.isNotBlank(inputVO2.getStatus_reason())) ? inputVO2.getStatus_reason().trim() : null);
		cond.setObject("rtResult",(StringUtils.isNotBlank(inputVO2.getResult())) ? inputVO2.getResult().trim() : null);
		cond.setObject("recruitRemark",(StringUtils.isNotBlank(inputVO2.getDesc())) ? inputVO2.getDesc().trim() : null);
		cond.setObject("resignReturn",(StringUtils.isNotBlank(inputVO2.getResign_return())) ? inputVO2.getResign_return().trim() : null);
		cond.setObject("blackListed",(StringUtils.isNotBlank(inputVO2.getBlack_listed())) ? inputVO2.getBlack_listed() : "N");
		if (inputVO2.getAble_onboard_date()	!=	null)
			cond.setObject("ableOnboardDate",null != inputVO2.getAble_onboard_date() ? new Timestamp(inputVO2.getAble_onboard_date().getTime()) : null);
		cond.setObject("hr",(inputVO2.getHr() != null && StringUtils.isNotBlank(inputVO2.getHr())) ? inputVO2.getHr().trim() : null);
		cond.setObject("jobTitleName",(StringUtils.isNotBlank(inputVO2.getJob_title_name())) ? inputVO2.getJob_title_name().trim() : null);
		cond.setObject("resumeSource",(StringUtils.isNotBlank(inputVO2.getResume_source())) ? inputVO2.getResume_source().trim() : null);
		cond.setObject("recommEmpId",(inputVO2.getRecommender_emp_id() != null && StringUtils.isNotBlank(inputVO2.getRecommender_emp_id())) ? inputVO2.getRecommender_emp_id().trim() : null);
		cond.setObject("recommAward",(StringUtils.isNotBlank(inputVO2.getRecommend_awardee_emp_id())) ? inputVO2.getRecommend_awardee_emp_id().trim() : null);
		cond.setObject("recommLetter",(StringUtils.isNotBlank(inputVO2.getRecommend_letter())) ? inputVO2.getRecommend_letter().trim() : null);
		cond.setObject("empId",(StringUtils.isNotBlank(inputVO2.getEmp_id())) ? inputVO2.getEmp_id().trim() : null);
		if (inputVO2.getStatus().equals("02") && null == inputVO2.getHo_recv_case_date())	{
			cond.setObject("hoRecvCaseDate",new Timestamp(System.currentTimeMillis()));
		}	else	{
			if (inputVO2.getHo_recv_case_date()	!=	null)
				cond.setObject("hoRecvCaseDate",HO_RECV_CASE_DATE);
		}
		cond.setObject("intvEmpId",inputVO2.getIntv_emp_id());
		cond.setObject("intvEmpName",inputVO2.getIntv_emp_name().trim());
		cond.setObject("status",inputVO2.getStatus().trim());
		if (inputVO2.getBrch_ini_int_date()	!=	null)
			cond.setObject("brchIniIntDate",new Timestamp(inputVO2.getBrch_ini_int_date().getTime()));
		cond.setObject("rcSupEmpId",inputVO2.getRc_sup_emp_id());
		cond.setObject("opSupEmpId",inputVO2.getOp_sup_emp_id());

		if (StringUtils.equals("01", STATUS) || 
			StringUtils.equals("02", STATUS) || 
			StringUtils.equals("03", STATUS) || 
			StringUtils.equals("11", 	STATUS)	||
			StringUtils.equals("13", 	STATUS)) {
			cond.setObject("hireStatus","4");
		}
		cond.setObject("custId",inputVO2.getCust_id());
		cond.setObject("branchNbr",inputVO2.getBranch_nbr());
		cond.setObject("seq",inputVO2.getSeq());

		cond.setQueryString(sql.toString());
		if (!list.isEmpty())
			dam.exeUpdate(cond);
		
		// 狀態為 "總行已收件" 才寄mail
		if(inputVO2.getStatus().equals("02")){
			// 發送mail所需參數
			ORG130OutputVO outputVO = new ORG130OutputVO();		
									
			List<Map<String, String>> mailList = getMailList((String) list.get(0).get("CREATOR"), inputVO2, outputVO);

			if(mailList.size() > 0) {
				sendEmail(mailList, inputVO2);
			} else {
				outputVO.setErrorMsg("發送失敗");
			}	
			sendRtnObject(outputVO);
		}
		
		this.sendRtnObject(null);
	}
	
	//通過
	public void passedit(Object body, IPrimitiveMap header) throws Exception {
		
		ORG130InputVO inputVO2 = (ORG130InputVO) body;
		
		dam = this.getDataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new	StringBuilder();
		
		sql.append("SELECT * ");
		sql.append("FROM TBORG_EMP_HIRE_INFO ");
		sql.append("	WHERE	CUST_ID	=	:custId	AND	BRANCH_NBR	=	:branchNbr AND	SEQ	=	:seq	");
		cond.setObject("custId", inputVO2.getCust_id());
		cond.setObject("branchNbr", inputVO2.getBranch_nbr());
		cond.setObject("seq", inputVO2.getSeq());
		
		cond.setQueryString(sql.toString());
		List<Map<String,Object>> list = dam.exeQuery(cond);
		
		if(!list.isEmpty()) {
			Map<String,Object> map = list.get(0);

		
			sql	=	new	StringBuilder();
			cond	=	dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		
			sql.append("	UPDATE TBORG_EMP_HIRE_INFO	");
			sql.append("	SET	");
			if (inputVO2.getsCreDate()	!=	inputVO2.getsCreDate())
				sql.append("			BOOKED_ONBOARD_DATE	=	:bookedOnboardDate, ");	 
			sql.append("			JOB_RANK = :jobRank, "); 
			if (inputVO2.getTestDate()	!=	null)
				sql.append("			ADA_TEST_DATE = :adaTestDate, "); 
			sql.append("			JOB_TITLE_NAME = :jobTitleName, "); 
			sql.append("			AO_JOB_RANK = :aoJobRank ,	"); 
			sql.append("			NO_SHOW_REASON = :noShowReason, "); 
			sql.append("			RT_RESULT = :rtResult, "); 
			sql.append("			RECRUIT_REMARK = :recruitRemark, "); 
			sql.append("			RESIGN_RETURN = :resignReturn, "); 
			sql.append("			RETURN_REMARK = :returnRemark, "); 
			sql.append("			BLACK_LISTED = :blackList, "); 
			sql.append("			PREV_JOB_EXP = :prevJobExp, "); 
			sql.append("			CUR_JOB = :curJob, "); 
			sql.append("			CUR_JOB_NAME = :curJobName, "); 
			sql.append("			CUR_AUM = :curAum, "); 
			sql.append("			CUR_MONTHLY_GOAL = :curMontylyGoal, "); 
			if (inputVO2.getAo_year_of_exp()	!=	null)
				sql.append("			AO_YEAR_OF_EXP = :aoYearOfExp, "); 
			if (inputVO2.getAble_onboard_date()	!=	null)
				sql.append("			ABLE_ONBOARD_DATE = :ableOnboardDate, "); 
			sql.append("			PREV_JOB = :prevJob, "); 
			sql.append("			HR = :hr, "); 
			sql.append("			RESUME_SOURCE = :resumeSrc, "); 
			sql.append("			RECOMMENDER_EMP_ID = :recoEmmpid, "); 
			sql.append("			RECOMMEND_AWARDEE_EMP_ID = :recoAwd, "); 
			sql.append("			RECOMMEND_LETTER = :recoLetter, "); 
			sql.append("			EMP_ID = :empId, "); 
			sql.append("			INTV_EMP_ID = :intvEmpId, "); 
			sql.append("			INTV_EMP_NAME = :intvEmpName, "); 
			sql.append("			HIRE_STATUS = '0', "); 
			sql.append("			REVIEW_STATUS = 'S', "); 
			sql.append("			STATUS = '12'		"); //待督導覆核
			sql.append("	WHERE	CUST_ID	=	:custId	AND BRANCH_NBR	=	:braNbr	AND SEQ	=	:seq	");
			cond.setQueryString(sql.toString());
			if (inputVO2.getsCreDate()	!=	inputVO2.getsCreDate())
				cond.setObject("bookedOnboardDate", null != inputVO2.getsCreDate() ? new Timestamp(inputVO2.getsCreDate().getTime()) : null);
			cond.setObject("jobRank", (StringUtils.isNotBlank(inputVO2.getJob_rank())) ? inputVO2.getJob_rank() : null);
			if (inputVO2.getTestDate()	!=	null)
				cond.setObject("adaTestDate", new Timestamp(inputVO2.getTestDate().getTime()));
			cond.setObject("jobTitleName", (StringUtils.isNotBlank(inputVO2.getJob_title_name())) ? inputVO2.getJob_title_name() : null);
			cond.setObject("aoJobRank", (StringUtils.isNotBlank(inputVO2.getJob_title_name())) ? inputVO2.getAo_job_rank().trim() : null);
			cond.setObject("noShowReason", (StringUtils.isNotBlank(inputVO2.getStatus_reason())) ? inputVO2.getStatus_reason().trim() : null);
			cond.setObject("rtResult", (StringUtils.isNotBlank(inputVO2.getResult())) ? inputVO2.getResult().trim() : null);
			cond.setObject("recruitRemark", (StringUtils.isNotBlank(inputVO2.getDesc())) ? inputVO2.getDesc().trim() : null);
			cond.setObject("resignReturn", (StringUtils.isNotBlank(inputVO2.getResign_return())) ? inputVO2.getResign_return() : null);
			cond.setObject("returnRemark", (StringUtils.isNotBlank(inputVO2.getReturn_remark())) ? inputVO2.getReturn_remark().trim() : null);
			cond.setObject("blackList", (StringUtils.isNotBlank(inputVO2.getBlack_listed())) ? inputVO2.getBlack_listed() : "N");
			cond.setObject("prevJobExp", (StringUtils.isNotBlank(inputVO2.getPrev_job_exp())) ? inputVO2.getPrev_job_exp() : null);
			cond.setObject("curJob", (StringUtils.isNotBlank(inputVO2.getCur_job())) ? inputVO2.getCur_job().trim() : null);
			cond.setObject("curJobName", (StringUtils.isNotBlank(inputVO2.getCur_job_name())) ? inputVO2.getCur_job_name().trim() : null);
			cond.setObject("curAum", inputVO2.getCur_aum()==null?"0":inputVO2.getCur_aum().trim());
			cond.setObject("curMontylyGoal", inputVO2.getCur_m_goal()==null?"0":inputVO2.getCur_m_goal().trim());
			if (inputVO2.getAo_year_of_exp()	!=	null)
				cond.setObject("aoYearOfExp", (StringUtils.isNotBlank(inputVO2.getAo_year_of_exp())) ? new BigDecimal(inputVO2.getAo_year_of_exp()==null?"0":inputVO2.getAo_year_of_exp().trim()) : null);
			if (inputVO2.getAble_onboard_date()	!=	null)
				cond.setObject("ableOnboardDate", new Timestamp(inputVO2.getAble_onboard_date().getTime()));
			cond.setObject("prevJob", (StringUtils.isNotBlank(inputVO2.getPrev_job())) ? inputVO2.getPrev_job().trim() : null);
			cond.setObject("hr", (StringUtils.isNotBlank(inputVO2.getHr())) ? inputVO2.getHr().trim() : null);
			cond.setObject("resumeSrc", (StringUtils.isNotBlank(inputVO2.getResume_source())) ? inputVO2.getResume_source().trim() : null);
			cond.setObject("recoEmmpid", (StringUtils.isNotBlank(inputVO2.getRecommender_emp_id())) ? inputVO2.getRecommender_emp_id().trim() : null);
			cond.setObject("recoAwd", (StringUtils.isNotBlank(inputVO2.getRecommender_emp_id())) ? inputVO2.getRecommend_awardee_emp_id().trim() : null);
			cond.setObject("recoLetter",  (StringUtils.isNotBlank(inputVO2.getRecommend_letter())) ? inputVO2.getRecommend_letter() : null);
			cond.setObject("empId",  (StringUtils.isNotBlank(inputVO2.getEmp_id())) ? inputVO2.getEmp_id().trim() : null);
			cond.setObject("intvEmpId",  (StringUtils.isNotBlank(inputVO2.getIntv_emp_id())) ? inputVO2.getIntv_emp_id().trim() : null);
			cond.setObject("intvEmpName",  (StringUtils.isNotBlank(inputVO2.getIntv_emp_name())) ? inputVO2.getIntv_emp_name().trim() : null);
			cond.setObject("custId", inputVO2.getCust_id());
			cond.setObject("braNbr", inputVO2.getBranch_nbr());
			cond.setObject("seq", inputVO2.getSeq());
			dam.exeUpdate(cond);
			
			sql	= new StringBuilder();
			cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			sql.append("INSERT INTO TBORG_EMP_HIRE_INFO_REVIEW ( ");
			sql.append("  SEQNO, ");
			sql.append("  CUST_ID, ");
			sql.append("  EMP_ID, ");
			sql.append("  EMP_NAME, ");
			sql.append("  INTV_EMP_ID, ");
			sql.append("  INTV_EMP_NAME, ");
			sql.append("  REGION_CENTER_ID, ");
			sql.append("  BRANCH_AREA_ID, ");
			sql.append("  BRANCH_NBR, ");

			if(null != map.get("BRCH_RECV_CASE_DATE"))
				sql.append("  BRCH_RECV_CASE_DATE, ");

			if(null != map.get("HO_RECV_CASE_DATE"))
				sql.append("  HO_RECV_CASE_DATE, ");

			if(null != map.get("OA_SUP_RT_DATE"))
				sql.append("  OA_SUP_RT_DATE, ");

			if(null != map.get("BRCH_INI_INT_DATE"))
				sql.append("BRCH_INI_INT_DATE, ");

			if(null != map.get("BOOKED_ONBOARD_DATE"))
				sql.append("BOOKED_ONBOARD_DATE, ");

			sql.append("JOB_RANK, ");
			sql.append("JOB_TITLE_NAME, ");
			sql.append("AO_JOB_RANK, ");

			if(null != map.get("ADA_TEST_DATE"))
				sql.append("ADA_TEST_DATE, ");

			sql.append("NO_SHOW_REASON, ");
			sql.append("RT_RESULT, ");
			sql.append("RECRUIT_REMARK, ");
			sql.append("BLACK_LISTED, ");
			sql.append("RESIGN_RETURN, ");
			sql.append("RETURN_REMARK, ");
			sql.append("PREV_JOB_EXP, ");
			sql.append("CUR_JOB, ");
			sql.append("CUR_JOB_NAME, ");
			sql.append("CUR_AUM, ");
			sql.append("CUR_MONTHLY_GOAL, ");
			sql.append("ACTUAL_ACCOMPLISH, ");

			if(null != map.get("AO_YEAR_OF_EXP"))
				sql.append("AO_YEAR_OF_EXP, ");

			sql.append("CUST_CNT, ");

			if(null != map.get("ABLE_ONBOARD_DATE"))
				sql.append("ABLE_ONBOARD_DATE, ");

			sql.append("PREV_JOB, ");
			sql.append("RC_SUP_EMP_ID, ");
			sql.append("OP_SUP_EMP_ID, ");
			sql.append("HR, ");
			sql.append("TRANS_FROM_BRANCH_ID, ");
			sql.append("TRANS_REMARK, ");
			sql.append("RESUME_SOURCE, ");
			sql.append("RECOMMENDER_EMP_ID, ");
			sql.append("RECOMMEND_AWARDEE_EMP_ID, ");
			sql.append("RECOMMEND_LETTER, ");
			sql.append("REQ_CERTIFICATE, ");
			sql.append("FINANCIAL_EXP, ");
			sql.append("PRE_FIN_INST, ");
			sql.append("OTHER_FI, ");
			sql.append("OTHER_PRE_FIN_INST, ");
			sql.append("RESIGN_REASON, ");
			sql.append("CUST_SATISFACTION, ");
			sql.append("ACHIEVEMENT, ");
			sql.append("SALES_SKILL, ");
			sql.append("ACTIVE, ");
			sql.append("PRESSURE_MANAGE, ");
			sql.append("COMMUNICATION, ");
			sql.append("PROBLEM_SOLVING_SKILL, ");
			sql.append("INTV_SUP_REMARK, ");
			sql.append("FEE_6M_ABILITY, ");
			sql.append("FEE_1Y_ABILITY, ");
			sql.append("SUGGEST_JOB, ");
			sql.append("SUGGEST_SALARY, ");
			sql.append("HIRE_STATUS, ");
			sql.append("REVIEW_STATUS, ");
			sql.append("STATUS, ");
			sql.append("CREATETIME, ");
			sql.append("CREATOR ");
			sql.append(")");
			
			sql.append("VALUES ( ");
			
			sql.append(":SEQNO, ");
			sql.append(":CUST_ID, ");
			sql.append(":EMP_ID, ");
			sql.append(":EMP_NAME, ");
			sql.append(":INTV_EMP_ID, ");
			sql.append(":INTV_EMP_NAME, ");
			sql.append(":REGION_CENTER_ID, ");
			sql.append(":BRANCH_AREA_ID, ");
			sql.append(":BRANCH_NBR, ");

			if(null != map.get("BRCH_RECV_CASE_DATE"))
				sql.append(":BRCH_RECV_CASE_DATE, ");

			if(null != map.get("HO_RECV_CASE_DATE"))
				sql.append(":HO_RECV_CASE_DATE, ");

			if(null != map.get("OA_SUP_RT_DATE"))
				sql.append(":OA_SUP_RT_DATE, ");

			if(null != map.get("BRCH_INI_INT_DATE"))
				sql.append(":BRCH_INI_INT_DATE, ");
			
			if(null != map.get("BOOKED_ONBOARD_DATE"))
				sql.append(":BOOKED_ONBOARD_DATE, ");

			sql.append(":JOB_RANK, ");
			sql.append(":JOB_TITLE_NAME, ");
			sql.append(":AO_JOB_RANK, ");

			if(null != map.get("ADA_TEST_DATE"))
				sql.append(":ADA_TEST_DATE, ");

			sql.append(":NO_SHOW_REASON, ");
			sql.append(":RT_RESULT, ");
			sql.append(":RECRUIT_REMARK, ");
			sql.append(":BLACK_LISTED, ");
			sql.append(":RESIGN_RETURN, ");
			sql.append(":RETURN_REMARK, ");
			sql.append(":PREV_JOB_EXP, ");
			sql.append(":CUR_JOB, ");
			sql.append(":CUR_JOB_NAME, ");
			sql.append(":CUR_AUM, ");
			sql.append(":CUR_MONTHLY_GOAL, ");
			sql.append(":ACTUAL_ACCOMPLISH, ");

			if(null != map.get("AO_YEAR_OF_EXP"))
				sql.append(":AO_YEAR_OF_EXP, ");

			sql.append(":CUST_CNT, ");

			if(null != map.get("ABLE_ONBOARD_DATE"))
				sql.append(":ABLE_ONBOARD_DATE, ");

			sql.append(":PREV_JOB, ");
			sql.append(":RC_SUP_EMP_ID, ");
			sql.append(":OP_SUP_EMP_ID, ");
			sql.append(":HR, ");
			sql.append(":TRANS_FROM_BRANCH_ID, ");
			sql.append(":TRANS_REMARK, ");
			sql.append(":RESUME_SOURCE, ");
			sql.append(":RECOMMENDER_EMP_ID, ");
			sql.append(":RECOMMEND_AWARDEE_EMP_ID, ");
			sql.append(":RECOMMEND_LETTER, ");
			sql.append(":REQ_CERTIFICATE, ");
			sql.append(":FINANCIAL_EXP, ");
			sql.append(":PRE_FIN_INST, ");
			sql.append(":OTHER_FI, ");
			sql.append(":OTHER_PRE_FIN_INST, ");
			sql.append(":RESIGN_REASON, ");
			sql.append(":CUST_SATISFACTION, ");
			sql.append(":ACHIEVEMENT, ");
			sql.append(":SALES_SKILL, ");
			sql.append(":ACTIVE, ");
			sql.append(":PRESSURE_MANAGE, ");
			sql.append(":COMMUNICATION, ");
			sql.append(":PROBLEM_SOLVING_SKILL, ");
			sql.append(":INTV_SUP_REMARK, ");
			sql.append(":FEE_6M_ABILITY, ");
			sql.append(":FEE_1Y_ABILITY, ");
			sql.append(":SUGGEST_JOB, ");
			sql.append(":SUGGEST_SALARY, ");
			sql.append(":HIRE_STATUS, ");
			sql.append("'S', ");
			sql.append("'12', ");
			sql.append("SYSDATE, ");
			sql.append(":CREATOR");
			sql.append(")");
			
			cond.setObject("SEQNO", new BigDecimal(getSEQ()));
			cond.setObject("CUST_ID", map.get("CUST_ID") == null ? "" : (String) map.get("CUST_ID"));
			cond.setObject("EMP_ID", map.get("EMP_ID") == null ? "" : (String) map.get("EMP_ID"));
			cond.setObject("EMP_NAME", map.get("EMP_NAME") == null ? "" : (String) map.get("EMP_NAME"));
			cond.setObject("INTV_EMP_ID", map.get("INTV_EMP_ID"));
			cond.setObject("INTV_EMP_NAME", map.get("INTV_EMP_NAME") == null ? "" : (String)map.get("INTV_EMP_NAME"));
			cond.setObject("REGION_CENTER_ID", map.get("REGION_CENTER_ID") == null ? "" : (String) map.get("REGION_CENTER_ID"));
			cond.setObject("BRANCH_AREA_ID", map.get("BRANCH_AREA_ID") == null ? "" : (String) map.get("BRANCH_AREA_ID"));
			cond.setObject("BRANCH_NBR", map.get("BRANCH_NBR") == null ? "" : (String) map.get("BRANCH_NBR"));
			if (map.get("BRCH_RECV_CASE_DATE") != null)
				cond.setObject("BRCH_RECV_CASE_DATE", (Date) map.get("BRCH_RECV_CASE_DATE"));
			if (map.get("HO_RECV_CASE_DATE") != null)
				cond.setObject("HO_RECV_CASE_DATE", (Date) map.get("HO_RECV_CASE_DATE"));
			if (map.get("OA_SUP_RT_DATE") != null)
				cond.setObject("OA_SUP_RT_DATE", (Date) map.get("OA_SUP_RT_DATE"));
			if(map.get("BRCH_INI_INT_DATE") != null)
				cond.setObject("BRCH_INI_INT_DATE", (Date) map.get("BRCH_INI_INT_DATE"));
			if(map.get("BOOKED_ONBOARD_DATE") != null)
				cond.setObject("BOOKED_ONBOARD_DATE", (Date) map.get("BOOKED_ONBOARD_DATE"));
			cond.setObject("JOB_RANK", map.get("JOB_RANK") == null ? "" : map.get("JOB_RANK"));
			cond.setObject("JOB_TITLE_NAME", map.get("JOB_TITLE_NAME") == null ? "" : (String) map.get("JOB_TITLE_NAME"));
			cond.setObject("AO_JOB_RANK", map.get("AO_JOB_RANK") == null ? "" : (String) map.get("AO_JOB_RANK"));
			if(map.get("ADA_TEST_DATE") != null)
				cond.setObject("ADA_TEST_DATE", (Date) map.get("ADA_TEST_DATE"));
			cond.setObject("NO_SHOW_REASON", map.get("NO_SHOW_REASON") == null ? "" : (String) map.get("NO_SHOW_REASON"));
			cond.setObject("RT_RESULT", map.get("RT_RESULT") == null ? "" : (String) map.get("RT_RESULT"));
			cond.setObject("RECRUIT_REMARK", map.get("RECRUIT_REMARK") == null ? "" : (String) map.get("RECRUIT_REMARK"));
			cond.setObject("BLACK_LISTED", map.get("BLACK_LISTED") == null ? "" : map.get("BLACK_LISTED"));
			cond.setObject("RESIGN_RETURN", map.get("RESIGN_RETURN") == null ? "" : map.get("RESIGN_RETURN"));
			cond.setObject("RETURN_REMARK", map.get("RETURN_REMARK") == null ? "" : (String) map.get("RETURN_REMARK"));
			cond.setObject("PREV_JOB_EXP", map.get("PREV_JOB_EXP") == null ? "" : (String) map.get("PREV_JOB_EXP"));
			cond.setObject("CUR_JOB", map.get("CUR_JOB") == null ? "" : (String) map.get("CUR_JOB"));
			cond.setObject("CUR_JOB_NAME", map.get("CUR_JOB_NAME") == null ? "" : (String) map.get("CUR_JOB_NAME"));
			cond.setObject("CUR_AUM", map.get("CUR_AUM") == null ? "":(String)map.get("CUR_AUM"));
			cond.setObject("CUR_MONTHLY_GOAL", map.get("CUR_MONTHLY_GOAL") == null ? "" : (String) map.get("CUR_MONTHLY_GOAL"));
			cond.setObject("ACTUAL_ACCOMPLISH", map.get("ACTUAL_ACCOMPLISH") == null ? "" : (String) map.get("ACTUAL_ACCOMPLISH"));
			if (map.get("AO_YEAR_OF_EXP") != null)
				cond.setObject("AO_YEAR_OF_EXP", (BigDecimal)map.get("AO_YEAR_OF_EXP"));
			cond.setObject("CUST_CNT", map.get("CUST_CNT") == null ? "" : (String) map.get("CUST_CNT"));
			if (map.get("ABLE_ONBOARD_DATE") != null)
				cond.setObject("ABLE_ONBOARD_DATE", (Date) map.get("ABLE_ONBOARD_DATE"));
			cond.setObject("PREV_JOB", map.get("PREV_JOB") == null ? "" : (String) map.get("PREV_JOB"));
			cond.setObject("RC_SUP_EMP_ID", map.get("RC_SUP_EMP_ID") == null ? "" : (String) map.get("RC_SUP_EMP_ID"));
			cond.setObject("OP_SUP_EMP_ID", map.get("OP_SUP_EMP_ID") == null ? "" : (String) map.get("OP_SUP_EMP_ID"));
			cond.setObject("HR", map.get("HR") == null ? "" : (String) map.get("HR"));
			cond.setObject("TRANS_FROM_BRANCH_ID", map.get("TRANS_FROM_BRANCH_ID") == null ? "" : (String) map.get("TRANS_FROM_BRANCH_ID"));
			cond.setObject("TRANS_REMARK", map.get("TRANS_REMARK") == null ? "" : (String) map.get("TRANS_REMARK"));
			cond.setObject("RESUME_SOURCE", map.get("RESUME_SOURCE") == null ? "" : (String) map.get("RESUME_SOURCE"));
			cond.setObject("RECOMMENDER_EMP_ID", map.get("RECOMMENDER_EMP_ID") == null ? "" : (String) map.get("RECOMMENDER_EMP_ID"));
			cond.setObject("RECOMMEND_AWARDEE_EMP_ID", map.get("RECOMMEND_AWARDEE_EMP_ID") == null ? "" : (String) map.get("RECOMMEND_AWARDEE_EMP_ID"));
			cond.setObject("RECOMMEND_LETTER", map.get("RECOMMEND_LETTER") == null ? "" : map.get("RECOMMEND_LETTER"));
			cond.setObject("REQ_CERTIFICATE", map.get("REQ_CERTIFICATE") == null ? "" : (String) map.get("REQ_CERTIFICATE"));
			cond.setObject("FINANCIAL_EXP", map.get("FINANCIAL_EXP") == null ? "" : map.get("FINANCIAL_EXP"));
			cond.setObject("PRE_FIN_INST", map.get("PRE_FIN_INST") == null ? "" : map.get("PRE_FIN_INST"));
			cond.setObject("OTHER_FI", map.get("OTHER_FI") == null ? "" : (String) map.get("OTHER_FI"));
			cond.setObject("OTHER_PRE_FIN_INST", map.get("OTHER_PRE_FIN_INST") == null ? "" : (String) map.get("OTHER_PRE_FIN_INST"));
			cond.setObject("RESIGN_REASON", map.get("RESIGN_REASON") == null ? "" : (String) map.get("RESIGN_REASON"));
			cond.setObject("CUST_SATISFACTION", map.get("CUST_SATISFACTION") == null ? "" : (String) map.get("CUST_SATISFACTION"));
			cond.setObject("ACHIEVEMENT", map.get("ACHIEVEMENT") == null ? "" : map.get("ACHIEVEMENT"));
			cond.setObject("SALES_SKILL", map.get("SALES_SKILL") == null ? "" : map.get("SALES_SKILL"));
			cond.setObject("ACTIVE", map.get("ACTIVE") == null ? "" : map.get("ACTIVE"));
			cond.setObject("PRESSURE_MANAGE", map.get("PRESSURE_MANAGE") == null ? "" : map.get("PRESSURE_MANAGE"));
			cond.setObject("COMMUNICATION", map.get("COMMUNICATION") == null ? "" : map.get("COMMUNICATION"));
			cond.setObject("PROBLEM_SOLVING_SKILL", map.get("PROBLEM_SOLVING_SKILL") == null ? "" : map.get("PROBLEM_SOLVING_SKILL"));
			cond.setObject("INTV_SUP_REMARK", map.get("INTV_SUP_REMARK") == null ? "" : (String) map.get("INTV_SUP_REMARK"));
			cond.setObject("FEE_6M_ABILITY", map.get("FEE_6M_ABILITY") == null ? "" : (BigDecimal) map.get("FEE_6M_ABILITY"));
			cond.setObject("FEE_1Y_ABILITY", map.get("FEE_1Y_ABILITY") == null ? "" : (BigDecimal) map.get("FEE_1Y_ABILITY"));
			cond.setObject("SUGGEST_JOB", map.get("SUGGEST_JOB") == null ? "" : (String)map.get("SUGGEST_JOB"));
			cond.setObject("SUGGEST_SALARY", map.get("SUGGEST_SALARY") == null ? "" : (BigDecimal)map.get("SUGGEST_SALARY"));
			cond.setObject("HIRE_STATUS", map.get("HIRE_STATUS") == null ? "" : (String)map.get("HIRE_STATUS"));
			cond.setObject("CREATOR", (String) getCommonVariable(SystemVariableConsts.LOGINID));
			
			cond.setQueryString(sql.toString());
			dam.exeUpdate(cond);
		}
		
		// 發送mail所需參數
		ORG130OutputVO outputVO = new ORG130OutputVO();		
		
		TBORG_EMP_HIRE_INFOVO vo = new TBORG_EMP_HIRE_INFOVO();
		TBORG_EMP_HIRE_INFOPK pk = new TBORG_EMP_HIRE_INFOPK();
		pk.setCUST_ID(inputVO2.getCust_id().trim());
		pk.setBRANCH_NBR(inputVO2.getBranch_nbr());
		pk.setSEQ(inputVO2.getSeq());
		
		vo = (TBORG_EMP_HIRE_INFOVO) dam.findByPKey(TBORG_EMP_HIRE_INFOVO.TABLE_UID, pk);
		
		inputVO2.setStatus(vo.getSTATUS());
		
		List<Map<String, String>> mailList = getMailList((String) list.get(0).get("CREATOR"), inputVO2, outputVO);

		if(mailList.size() > 0) {
			sendEmail(mailList, inputVO2);
		} else {
			outputVO.setErrorMsg("發送失敗");
		}
		
		sendRtnObject(outputVO);		
	}	

	//處主管,督導
	//核可，TBORG_EMP_HIRE_INFO_REVIEW，覆核狀態通過		
	public void review(Object body, IPrimitiveMap header) throws Exception {

		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		
		ORG130InputVO inputVO2 = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuilder sql = new StringBuilder();

		//=== 1. 更新主檔
		sql.append("UPDATE TBORG_EMP_HIRE_INFO ");
		sql.append("SET STATUS = :status, ");
		sql.append("    REVIEW_STATUS = :reviewStatus, ");
		sql.append("    HIRE_STATUS = :hireStatus, ");
		sql.append("    MODIFIER = :modifier, ");
		sql.append("    LASTUPDATE = :lstUpdDte ");

		if (!this.isSupervisor()) {
			sql.append(", OA_SUP_RT_Date = :oaSupRtDate ");
		}

		sql.append("WHERE CUST_ID =	:custId	");
		sql.append("AND	BRANCH_NBR = :braNbr ");
		sql.append("AND	SEQ = :seq ");
		
		if (!this.isSupervisor()) {//處長覆核
			cond.setObject("status", "05");
			cond.setObject("reviewStatus", "Y");
			cond.setObject("hireStatus", "1");
			cond.setObject("oaSupRtDate", currentTM);
		} else {//督導覆核
			cond.setObject("status", "03");
			cond.setObject("reviewStatus", "W");
			cond.setObject("hireStatus", "1");
		}
		
		cond.setObject("custId", inputVO2.getCust_id().trim());
		cond.setObject("braNbr", inputVO2.getBranch_nbr());
		cond.setObject("seq", inputVO2.getSeq());
		cond.setObject("modifier", new BigDecimal(inputVO2.getLogin_id()));
		cond.setObject("lstUpdDte", currentTM);
		
		cond.setQueryString(sql.toString());
		
		dam.exeUpdate(cond);

		//=== 2. 更新覆核檔
		cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		sql = new StringBuilder();
		sql.append("UPDATE TBORG_EMP_HIRE_INFO_REVIEW ");
		sql.append("SET	STATUS = :status, ");
		sql.append("    REVIEW_STATUS = :reviewStatus, ");
		sql.append("    HIRE_STATUS = :hireStatus, ");
		sql.append("    MODIFIER = :modifier, ");
		sql.append("    LASTUPDATE = :lstUpdDte ");
		sql.append("WHERE CUST_ID = :custId	");
		sql.append("AND	BRANCH_NBR = :braNbr ");

		if (!this.isSupervisor()) {
			cond.setObject("status", "05");
			cond.setObject("reviewStatus", "Y");//處長覆核
			cond.setObject("hireStatus", "1");
		} else {
			cond.setObject("status", "03");
			cond.setObject("reviewStatus", "W");//督導覆核
			cond.setObject("hireStatus", "");
		}
		
		cond.setObject("modifier", new BigDecimal(inputVO2.getLogin_id()));
		cond.setObject("lstUpdDte", currentTM);
		cond.setObject("custId", inputVO2.getCust_id().trim());
		cond.setObject("braNbr", inputVO2.getBranch_nbr());
		
		cond.setQueryString(sql.toString());
		
		dam.exeUpdate(cond);

		//=== 3. 寄出信件
		if (this.isSupervisor()) {
			inputVO2.setStatus("03");	//督導覆核
		} else {
			inputVO2.setStatus("05");	//處長覆核
		}

		TBORG_EMP_HIRE_INFOVO vo = new TBORG_EMP_HIRE_INFOVO();
		TBORG_EMP_HIRE_INFOPK pk = new TBORG_EMP_HIRE_INFOPK();
		pk.setCUST_ID(inputVO2.getCust_id().trim());
		pk.setBRANCH_NBR(inputVO2.getBranch_nbr());
		pk.setSEQ(inputVO2.getSeq());

		vo = (TBORG_EMP_HIRE_INFOVO) dam.findByPKey(TBORG_EMP_HIRE_INFOVO.TABLE_UID, pk);

		inputVO2.setEmp_name(vo.getEMP_NAME());
		inputVO2.setAo_job_rank(vo.getAO_JOB_RANK());

		// 發送mail所需參數
		ORG130OutputVO outputVO = new ORG130OutputVO();

		List<Map<String, String>> mailList = getMailList(vo.getCreator(), inputVO2, outputVO);

		if (mailList.size() > 0) {
			sendEmail(mailList, inputVO2);
		} else {
			outputVO.setErrorMsg("發送失敗");
		}
		
		sendRtnObject(outputVO);
	}
	
	//退回
	public void reback(Object body, IPrimitiveMap header) throws Exception {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);
		
		ORG130InputVO inputVO2 = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		StringBuffer sb = new StringBuffer();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("UPDATE TBORG_EMP_HIRE_INFO ");
		sb.append("SET	REVIEW_STATUS = :reviewStatus, STATUS = :status, RT_RESULT = :rtResult, OA_SUP_RT_DATE = :opSubRtDte ");
//		if (!this.isSupervisor()) {
//			sb.append(", HIRE_STATUS = :hireStatus ");
//		}
		sb.append("WHERE CUST_ID = :custId AND BRANCH_NBR = :braNbr AND SEQ = :seq ");
		
		queryCondition.setObject("reviewStatus", "N");
		if (this.isSupervisor()) {
			queryCondition.setObject("status", "13");
		} else {
			queryCondition.setObject("status", "11");
//			queryCondition.setObject("hireStatus", "0"); //處主管複試後退回
		}
		
		queryCondition.setObject("rtResult", inputVO2.getResult());	
		queryCondition.setObject("opSubRtDte", new Timestamp(System.currentTimeMillis()));
		queryCondition.setObject("custId", inputVO2.getCust_id());
		queryCondition.setObject("braNbr", inputVO2.getBranch_nbr());
		queryCondition.setObject("seq", inputVO2.getSeq());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		sb	=	new	StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("UPDATE TBORG_EMP_HIRE_INFO_REVIEW ");
		sb.append("SET HIRE_STATUS = '2', REVIEW_STATUS = 'N' ");
		sb.append("WHERE CUST_ID = :custId ");
		sb.append("AND BRANCH_NBR = :braNbr ");
		queryCondition.setObject("custId", inputVO2.getCust_id());
		queryCondition.setObject("braNbr", inputVO2.getBranch_nbr());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);

		sb	=	new	StringBuffer();
		queryCondition	=	dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT INTV_EMP_ID, CREATOR ");
		sb.append("FROM TBORG_EMP_HIRE_INFO ");
		sb.append("WHERE CUST_ID = :custId ");
		sb.append("AND BRANCH_NBR = :braNbr	");
		sb.append("AND SEQ = :seq	");
		queryCondition.setObject("custId", inputVO2.getCust_id());
		queryCondition.setObject("braNbr", inputVO2.getBranch_nbr());
		queryCondition.setObject("seq", inputVO2.getSeq());
		queryCondition.setQueryString(sb.toString());
		List<Map<String,Object>>	list	=	dam.exeQuery(queryCondition);
		String intvEmpId	=	list.isEmpty()?"":(String)list.get(0).get("INTV_EMP_ID");
		
		sb	=	new	StringBuffer();
		queryCondition	=	dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT DISTINCT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'ORG130' AND FUNCTIONID = 'maintenance') ");
		sb.append("AND ROLEID IN ( ");
		sb.append("  SELECT ROLE_ID ");
		sb.append("  FROM TBORG_MEMBER_ROLE ");
		sb.append("  WHERE EMP_ID = :empID ");
		sb.append(") ");
		sb.append("ORDER BY PRIVILEGEID DESC ");
				
		queryCondition.setObject("empID", intvEmpId);
		queryCondition.setQueryString(sb.toString());
			
		list = dam.exeQuery(queryCondition);	
			
		if (list.size() > 0) {
			TBCRM_WKPG_MD_MASTVO msvo = new TBCRM_WKPG_MD_MASTVO();
			msvo.setSEQ(getSN());
			msvo.setPRIVILEGEID((String) list.get(0).get("PRIVILEGEID"));
			msvo.setEMP_ID(intvEmpId);
			msvo.setROLE_LINK_YN("N");
			msvo.setFRQ_TYPE("D");
			msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
			msvo.setDISPLAY_NO("197");
			msvo.setCLICK_YN("N");
			if (!this.isSupervisor())
				msvo.setRPT_NAME("業務人員進用處主管退回通知");
			else
				msvo.setRPT_NAME("業務人員進用處督導退回通知");
			msvo.setRPT_PROG_URL("ORG130");
			msvo.setPASS_PARAMS(null);
			msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));
			dam.create(msvo);
		}
		
		// 判斷是否為督導覆核
		if (this.isSupervisor()){
			inputVO2.setStatus("13");
		} else{
			inputVO2.setStatus("11");
		}
		
		/*** 取得面試人員資料 start ***/
		TBORG_EMP_HIRE_INFOVO vo = new TBORG_EMP_HIRE_INFOVO();
		TBORG_EMP_HIRE_INFOPK pk = new TBORG_EMP_HIRE_INFOPK();
		pk.setCUST_ID(inputVO2.getCust_id().trim());
		pk.setBRANCH_NBR(inputVO2.getBranch_nbr());
		pk.setSEQ(inputVO2.getSeq());
		
		vo = (TBORG_EMP_HIRE_INFOVO) dam.findByPKey(TBORG_EMP_HIRE_INFOVO.TABLE_UID, pk);
		
		inputVO2.setEmp_name(vo.getEMP_NAME());
		inputVO2.setAo_job_rank(vo.getAO_JOB_RANK());
		/*** 取得面試人員資料 end ***/
		
		// 發送mail所需參數
		ORG130OutputVO outputVO = new ORG130OutputVO();		
				
		List<Map<String, String>> mailList = getMailList((String) list.get(0).get("CREATOR"), inputVO2, outputVO);

		if(mailList.size() > 0) {
			sendEmail(mailList, inputVO2);

		} else {
			outputVO.setErrorMsg("發送失敗");
		}	
		sendRtnObject(outputVO);
		
		//this.sendRtnObject(null);
	}
	private boolean isSupervisor() throws JBranchException {
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		String role = (String)getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		//IF 登入者身份為區督導
		 if (mbrmMap.containsKey(role)) {
			 return true;
		 }
		 return false;
	}
	
	//不通過
	public void unpassedit(Object body, IPrimitiveMap header) throws Exception {
		ORG130InputVO inputVO2 = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_EMP_HIRE_INFOVO vo = new TBORG_EMP_HIRE_INFOVO();
		TBORG_EMP_HIRE_INFOPK pk = new TBORG_EMP_HIRE_INFOPK();
		pk.setCUST_ID(inputVO2.getCust_id().trim());
		pk.setBRANCH_NBR(inputVO2.getBranch_nbr().trim());
		pk.setSEQ(inputVO2.getSeq());

		vo = (TBORG_EMP_HIRE_INFOVO) dam.findByPKey(TBORG_EMP_HIRE_INFOVO.TABLE_UID, pk);
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		
		if(null != vo){
			if(inputVO2.getsCreDate()!=null){
				Date sCreDate = new Timestamp(inputVO2.getsCreDate().getTime());
				vo.setBOOKED_ONBOARD_DATE((Timestamp)sCreDate);
			}
			if(inputVO2.getJob_rank()!=null){
				vo.setJOB_RANK(inputVO2.getJob_rank().trim());
			}
			if(inputVO2.getTestDate()!=null){
				Date test = new Timestamp(inputVO2.getTestDate().getTime());
				vo.setADA_TEST_DATE((Timestamp)test);
			}
			if(inputVO2.getJob_title_name()!=null && StringUtils.isNotBlank(inputVO2.getJob_title_name())){
				vo.setJOB_TITLE_NAME(inputVO2.getJob_title_name().trim());
			}
			if(inputVO2.getAo_job_rank()!=null && StringUtils.isNotBlank(inputVO2.getAo_job_rank())){
				vo.setAO_JOB_RANK(inputVO2.getAo_job_rank().trim());
			}
			if(inputVO2.getStatus_reason()!=null && StringUtils.isNotBlank(inputVO2.getStatus_reason())){
				vo.setNO_SHOW_REASON(inputVO2.getStatus_reason().trim());
			}
			if(inputVO2.getResult()!=null && StringUtils.isNotBlank(inputVO2.getResult())){
				vo.setRT_RESULT(inputVO2.getResult().trim());
			}
			if(inputVO2.getDesc()!=null && StringUtils.isNotBlank(inputVO2.getDesc())){
				vo.setRECRUIT_REMARK(inputVO2.getDesc().trim());
			}
			if(inputVO2.getResign_return()!=null && StringUtils.isNotBlank(inputVO2.getResign_return())){
				vo.setRESIGN_RETURN(inputVO2.getResign_return().trim());
			}
			
			if(inputVO2.getReturn_remark()!=null && StringUtils.isNotBlank(inputVO2.getReturn_remark())){
				vo.setRETURN_REMARK(inputVO2.getReturn_remark().trim());
			}	
				
			if(inputVO2.getBlack_listed()!=null && StringUtils.isNotBlank(inputVO2.getBlack_listed())){
				vo.setBLACK_LISTED(inputVO2.getBlack_listed().trim());
			}
			
			if(inputVO2.getPrev_job_exp()!=null && StringUtils.isNotBlank(inputVO2.getPrev_job_exp())){
				vo.setPREV_JOB_EXP(inputVO2.getPrev_job_exp().trim());
			}
			
			if(inputVO2.getCur_job()!=null && StringUtils.isNotBlank(inputVO2.getCur_job())){
				vo.setCUR_JOB(inputVO2.getCur_job().trim());
			}
			
			if(inputVO2.getCur_job_name()!=null && StringUtils.isNotBlank(inputVO2.getCur_job_name())){
				vo.setCUR_JOB_NAME(inputVO2.getCur_job_name().trim());
			}
				
			if(inputVO2.getCur_aum()!=null && StringUtils.isNotBlank(inputVO2.getCur_aum())){
//				BigDecimal cur_aum = new BigDecimal(inputVO2.getCur_aum().trim());
				vo.setCUR_AUM(inputVO2.getCur_aum());
			}
				
			if(inputVO2.getCur_m_goal()!=null && StringUtils.isNotBlank(inputVO2.getCur_m_goal())){
//				BigDecimal cur_m_goal = new BigDecimal(inputVO2.getCur_m_goal().trim());
				vo.setCUR_MONTHLY_GOAL(inputVO2.getCur_m_goal());
			}
			
//			if(inputVO2.getDesc()!=null && StringUtils.isNotBlank(inputVO2.getDesc())){
//				BigDecimal cur_fee_income = new BigDecimal(inputVO2.getCur_fee_income().trim());
//				vo.setCUR_FEE_INCOME(cur_fee_income);
//			}
			
			if(inputVO2.getAo_year_of_exp()!=null){
				BigDecimal ao_year_of_exp = new BigDecimal(inputVO2.getAo_year_of_exp().trim());
				vo.setAO_YEAR_OF_EXP(ao_year_of_exp);
			}
			
			if(inputVO2.getAble_onboard_date()!=null ){
				Date able_onboard_date = new Timestamp(inputVO2.getAble_onboard_date().getTime());
				vo.setABLE_ONBOARD_DATE((Timestamp)able_onboard_date);
			}
			
			if(inputVO2.getPrev_job()!=null && StringUtils.isNotBlank(inputVO2.getPrev_job())){
				vo.setPREV_JOB(inputVO2.getPrev_job().trim());
			}
			
			if(inputVO2.getHr()!=null && StringUtils.isNotBlank(inputVO2.getHr())){
				vo.setHR(inputVO2.getHr().trim());
			}
			
			if(inputVO2.getResume_source()!=null && StringUtils.isNotBlank(inputVO2.getResume_source())){
				vo.setRESUME_SOURCE(inputVO2.getResume_source().trim());
			}
			if(inputVO2.getRecommender_emp_id()!=null && StringUtils.isNotBlank(inputVO2.getRecommender_emp_id())){
				vo.setRECOMMENDER_EMP_ID(inputVO2.getRecommender_emp_id().trim());
			}
			
			if(inputVO2.getRecommend_awardee_emp_id()!=null && StringUtils.isNotBlank(inputVO2.getRecommend_awardee_emp_id())){
				vo.setRECOMMEND_AWARDEE_EMP_ID(inputVO2.getRecommend_awardee_emp_id().trim());
			}
			
			if(inputVO2.getRecommend_letter()!=null && StringUtils.isNotBlank(inputVO2.getRecommend_letter())){
				vo.setRECOMMEND_LETTER(inputVO2.getRecommend_letter().trim());
			}
			
			if(inputVO2.getEmp_id()!=null && StringUtils.isNotBlank(inputVO2.getEmp_id())){
				vo.setEMP_ID(inputVO2.getEmp_id().trim());
			}
			vo.setINTV_EMP_ID(inputVO2.getIntv_emp_id().trim());
			vo.setINTV_EMP_NAME(inputVO2.getIntv_emp_name().trim());
			vo.setHIRE_STATUS("2");
			vo.setSTATUS("08");
			vo.setModifier(inputVO2.getLogin_id());
			vo.setLastupdate(currentTM);
			dam.update(vo);
		}
		
		/*** 取得面試人員資料 start ***/
		inputVO2.setEmp_name(vo.getEMP_NAME());
		inputVO2.setAo_job_rank(vo.getAO_JOB_RANK());
		/*** 取得面試人員資料 end ***/
		
		// 發送mail所需參數
		ORG130OutputVO outputVO = new ORG130OutputVO();		
				
		inputVO2.setStatus("08");
		List<Map<String, String>> mailList = getMailList(vo.getCreator(), inputVO2, outputVO);

		if(mailList.size() > 0) {
			sendEmail(mailList, inputVO2);

		} else {
			outputVO.setErrorMsg("發送失敗");
		}	
		sendRtnObject(outputVO);
		//this.sendRtnObject(null);	
	}
	
	public void transrmk (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG130InputVO inputVO = (ORG130InputVO) body;
		ORG130OutputVO outputVO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBORG_EMP_HIRE_INFOPK pk = new TBORG_EMP_HIRE_INFOPK();
		pk.setCUST_ID(inputVO.getCust_id().trim());
		pk.setBRANCH_NBR(inputVO.getBranch_nbr_back().trim());
		pk.setSEQ(inputVO.getSeq());
		
		TBORG_EMP_HIRE_INFOVO vo = new TBORG_EMP_HIRE_INFOVO();
		vo.setcomp_id(pk);
		vo = (TBORG_EMP_HIRE_INFOVO) dam.findByPKey(TBORG_EMP_HIRE_INFOVO.TABLE_UID, vo.getcomp_id());
		
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		if(null != vo){
			vo.setSTATUS("10");
			vo.setHIRE_STATUS("3");
			
			dam.update(vo);
		}	
		
		TBORG_EMP_HIRE_INFOPK pk2 = new TBORG_EMP_HIRE_INFOPK();
		pk2.setCUST_ID(inputVO.getCust_id().trim());
		pk2.setBRANCH_NBR(inputVO.getBranch_nbr().trim());
		pk2.setSEQ(getInfoSeq());
		TBORG_EMP_HIRE_INFOVO vo2 = new TBORG_EMP_HIRE_INFOVO();
		vo2.setcomp_id(pk2);
		vo2.setHIRE_STATUS("0");										//進用狀態：0：無/1：通過/2：不通過/3：轉介至他行/4：暫存
		vo2.setEMP_NAME(inputVO.getEmp_name().trim());					//應徵者姓名
		vo2.setREGION_CENTER_ID(inputVO.getRegion_center_id().trim());	//區域中心代碼
		vo2.setBRANCH_AREA_ID(inputVO.getBranch_area_id().trim());		//營運區代碼
		vo2.setTRANS_REMARK(inputVO.getTrans_remark().trim());			//轉介單位備註
		vo2.setTRANS_FROM_BRANCH_ID(inputVO.getBranch_nbr_back());		//轉介行
		vo2.setSTATUS("01");											//目前狀態 01：分行(組)主管進件
		vo2.setEMP_TYPE("2");											//進用人員類型 1:作業2:業務
		vo2.setBRCH_RECV_CASE_DATE(new Timestamp(System.currentTimeMillis()));				//分行進件日
		dam.create(vo2);
			
		this.sendRtnObject(outputVO);
	}
	
	//檢核新增輸入CUST_ID與分行，是否重複
	public void rewcust(Object body, IPrimitiveMap header) throws JBranchException {
		logger.info("ORG130 偵錯檢核 LOG...");
		ORG130InputVO inputVO = (ORG130InputVO) body;
		ORG130OutputVO outputVO = new ORG130OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT COUNT(CUST_ID) as rewcust ");
		sql.append(" FROM TBORG_EMP_HIRE_INFO ");
		sql.append(" WHERE CUST_ID = :custid ");
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr_back())) { //轉介
			sql.append(" AND BRANCH_NBR = :branchNbr ");
			queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
		} else {
//			queryCondition.setObject("branchNbr", inputVO.getBranch_nbr_back());
		}
		
		sql.append(" 	AND STATUS NOT IN ('08', '09', '10')");
		
		queryCondition.setObject("custid", inputVO.getCust_id());
		
		queryCondition.setQueryString(sql.toString());
		
		outputVO.setRewcust(dam.exeQuery(queryCondition));

		//===FIXME 偵錯測試
		try {
			if (CollectionUtils.isNotEmpty(outputVO.getRewcust())) {
				Map<String, Object> map = (Map<String, Object>) outputVO.getRewcust().get(0);
				logger.info(map.toString());
				logger.info(ObjectUtils.toString(map.get("REWCUST")));
				logger.info(ObjectUtils.toString(map.get("rewcust")));
			}
		} catch (Exception e) { e.printStackTrace();}
		//===FIXME

		this.sendRtnObject(outputVO);
	}
	
	//檢核日期為工作日
	public void rewdate(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVO3 = (ORG130InputVO) body;
		ORG130OutputVO vo = new ORG130OutputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		List<Map<String, String>> inputLst = new ArrayList<Map<String,String>>();
		HashMap<String, String> dataMap = new HashMap<String, String>();
		if(inputVO3.getsCreDate()!=null && !inputVO3.getsCreDate().equals("")){
			String screDate = sdf.format(inputVO3.getsCreDate());
			dataMap.put("SCREDATE", validateHoliday(screDate));
			inputLst.add(dataMap);
			vo.setReviewdate(inputLst); 
		}
		if(inputVO3.getTestDate()!=null&& !inputVO3.getTestDate().equals("")){
			String testDate = sdf.format(inputVO3.getTestDate());
			dataMap.put("TESTDATE", validateHoliday(testDate));
			inputLst.add(dataMap);
			vo.setReviewdate(inputLst); 
		}
		if(inputVO3.getAble_onboard_date()!=null && !inputVO3.getAble_onboard_date().equals("")){
			String ableOnboardDate = sdf.format(inputVO3.getAble_onboard_date());
			dataMap.put("ABLEONBOARDDATE", validateHoliday(ableOnboardDate));
			inputLst.add(dataMap);
			vo.setReviewdate(inputLst); 
		}
		if(inputVO3.getBrch_ini_int_date()!=null && !inputVO3.getBrch_ini_int_date().equals("") ){
			String brchIniIntDate = sdf.format(inputVO3.getBrch_ini_int_date());
			dataMap.put("BRCHINIINTDATE", validateHoliday(brchIniIntDate));
			inputLst.add(dataMap);
			vo.setReviewdate(inputLst); 
		}
		
		this.sendRtnObject(vo);
	}
	
	public void checkUserId(Object body, IPrimitiveMap header) throws Exception {
		ORG130OutputVO return_VO = new ORG130OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT EMP_ID FROM TBORG_MEMBER_ROLE WHERE ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('043', '044')) AND EMP_ID = :emp_id");
		queryCondition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		
		ORG130InputVO inputVO = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_EMP_HIRE_FILEVO vo = new TBORG_EMP_HIRE_FILEVO();
		vo = (TBORG_EMP_HIRE_FILEVO) dam.findByPKey(TBORG_EMP_HIRE_FILEVO.TABLE_UID, inputVO.getFileData());
		
		try {
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			Blob blob = vo.getHIRE_FILE();
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			
			String fileName = inputVO.getFileLabel() + ".doc";
			String uuid = UUID.randomUUID().toString();
			
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
		    fos.write(blobAsBytes);
		    fos.close();
		    
		    notifyClientToDownloadFile("temp//" + uuid, fileName);
		    
		    this.sendRtnObject(null);
		} catch(Exception e){
			logger.debug(e.getMessage(),e);
		}

//		notifyClientToDownloadFile("resource//" + inputVO.getFileData(), inputVO.getFileLabel());
	}
	
	public void uploadSimple(Object body, IPrimitiveMap header) throws Exception {
		
		ORG130InputVO inputVO = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		
		byte[] data = Files.readAllBytes(Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString()));

		TBORG_EMP_HIRE_FILEVO vo = new TBORG_EMP_HIRE_FILEVO();
		vo = (TBORG_EMP_HIRE_FILEVO) dam.findByPKey(TBORG_EMP_HIRE_FILEVO.TABLE_UID, inputVO.getFileData());
		vo.setHIRE_FILE(ObjectUtil.byteArrToBlob(data));
		dam.update(vo);

//		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
//		File file = new File(DataManager.getRealPath() + "//resource//" + inputVO.getFileData());
//		FileInputStream fin = new FileInputStream(new File(tempPath, inputVO.getFileName()));
//		FileOutputStream fos = new FileOutputStream(file);
//		IOUtils.copy(fin, fos);
//		fin.close();
//		fos.close();
		
		this.sendRtnObject(null);
	}
	
	//面談評估表
	//Can't change table jar, use jdbc sql instead, FK.
	public void interview(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVO3 = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF	condIf	=	dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("	UPDATE	TBORG_EMP_HIRE_INFO	");
		sql.append("	SET	REQ_CERTIFICATE	=	:reqCert	, FINANCIAL_EXP	=	:financialExp, PRE_FIN_INST	=	:preFinInst, ");
		condIf.setObject("reqCert",TextUtils.join(",", inputVO3.getReq_certificate()));
		condIf.setObject("financialExp",inputVO3.getFinancial_exp());
		condIf.setObject("preFinInst",inputVO3.getPre_fin_inst());
		if (StringUtils.isNotBlank(inputVO3.getOther_pre_fin_inst()))	{
			sql.append("				OTHER_PRE_FIN_INST	=	:otherPreFinInst, ");
			condIf.setObject("otherPreFinInst",inputVO3.getOther_pre_fin_inst());
		}
		if (StringUtils.isNotBlank(inputVO3.getOther_fi()))	{
			sql.append("				OTHER_FI	=	:otherFi, ");
			condIf.setObject("otherFi",inputVO3.getOther_fi());
		}
		if (StringUtils.isNotBlank(inputVO3.getCur_m_goal()))	{
					sql.append("				CUR_MONTHLY_GOAL	=	:curMonthlyGoal, ");
					condIf.setObject("curMonthlyGoal",inputVO3.getCur_m_goal());
		}
		sql.append("				ACTUAL_ACCOMPLISH	=	:actualAccomplish, CUR_AUM	=	:curAum, CUST_CNT	=	:custCnt, ");
		condIf.setObject("actualAccomplish",inputVO3.getActual_accomplish());
		condIf.setObject("curAum",inputVO3.getCur_aum());
		condIf.setObject("custCnt",inputVO3.getCust_cnt());
		if (StringUtils.isNotBlank(inputVO3.getResign_reason()))	{
			sql.append("				RESIGN_REASON	=	:resignReason, ");
			condIf.setObject("resignReason",inputVO3.getResign_reason());
		}
		if (StringUtils.isNotBlank(inputVO3.getCust_satisfaction()))	{
			sql.append("				CUST_SATISFACTION	=	:custSatis, ");
			condIf.setObject("custSatis",inputVO3.getCust_satisfaction());
		}
		if (StringUtils.isNotBlank(inputVO3.getAchievement()))	{
			sql.append("				ACHIEVEMENT	=	:achievement, ");
			condIf.setObject("achievement",inputVO3.getAchievement());
		}
		if (StringUtils.isNotBlank(inputVO3.getSales_skill()))	{
			sql.append("				SALES_SKILL	=	:salesSkill, ");
			condIf.setObject("salesSkill",inputVO3.getSales_skill());
		}
		if (StringUtils.isNotBlank(inputVO3.getActive()))	{
			sql.append("				ACTIVE	=	:active, ");
			condIf.setObject("active",inputVO3.getActive());
		}
		if (StringUtils.isNotBlank(inputVO3.getPressure_manage()))	{
			sql.append("				PRESSURE_MANAGE	=	:pressureManage, ");
			condIf.setObject("pressureManage",inputVO3.getPressure_manage());
		}
		if (StringUtils.isNotBlank(inputVO3.getCommunication()))	{
			sql.append("				COMMUNICATION	=	:communication, ");
			condIf.setObject("communication",inputVO3.getCommunication());
		}
		if (StringUtils.isNotBlank(inputVO3.getProblem_solving_skill()))	{
			sql.append("				PROBLEM_SOLVING_SKILL	=	:problemSolvingSkill, ");
			condIf.setObject("problemSolvingSkill",inputVO3.getProblem_solving_skill());
		}
		if (StringUtils.isNotBlank(inputVO3.getIntv_sup_remark()))	{
			sql.append("				INTV_SUP_REMARK	=	:intvSupRemark, ");
			condIf.setObject("intvSupRemark",inputVO3.getIntv_sup_remark());
		}
		if (StringUtils.isNotBlank(inputVO3.getFee_6m_ability()))	{
			sql.append("				FEE_6M_ABILITY	=	:fee6MAbility, ");
			condIf.setObject("fee6MAbility",inputVO3.getFee_6m_ability());
		}
		if (StringUtils.isNotBlank(inputVO3.getFee_1y_ability()))	{
			sql.append("				FEE_1Y_ABILITY	=	:fee1YAbility, ");
			condIf.setObject("fee1YAbility",inputVO3.getFee_1y_ability());
		}
		if (StringUtils.isNotBlank(inputVO3.getSuggest_job()))	{
			sql.append("				SUGGEST_JOB	=	:suggestJob, ");
			condIf.setObject("suggestJob",inputVO3.getSuggest_job());
		}
		if (StringUtils.isNotBlank(inputVO3.getSuggest_salary()))	{
			sql.append("				SUGGEST_SALARY	=	:suggestSalary, ");
			condIf.setObject("suggestSalary",inputVO3.getSuggest_salary());
		}
		sql.append("				MODIFIER	=	:modifier, ");
		sql.append("				LASTUPDATE	=	SYSDATE");
		sql.append("	WHERE CUST_ID	=	:custId	AND BRANCH_NBR	=	:braNbr	AND SEQ	=	:seq	");
		condIf.setObject("modifier",inputVO3.getLogin_id());
		condIf.setObject("custId",inputVO3.getCust_id());
		condIf.setObject("braNbr",inputVO3.getBranch_nbr());
		condIf.setObject("seq",inputVO3.getSeq());
		condIf.setQueryString(sql.toString());
		dam.exeUpdate(condIf);
		//No more Hibernate......
//		TBORG_EMP_HIRE_INFOVO vo = new TBORG_EMP_HIRE_INFOVO();
//		TBORG_EMP_HIRE_INFOPK pk = new TBORG_EMP_HIRE_INFOPK();
//		pk.setCUST_ID(inputVO3.getCust_id());
//		pk.setBRANCH_NBR(inputVO3.getBranch_nbr());
//		vo.setcomp_id(pk);
//		vo = (TBORG_EMP_HIRE_INFOVO) dam.findByPKey(TBORG_EMP_HIRE_INFOVO.TABLE_UID, vo.getcomp_id());
//
//		if(vo != null){
//			vo.setREQ_CERTIFICATE(TextUtils.join(",", inputVO3.getReq_certificate()));
//			vo.setFINANCIAL_EXP(inputVO3.getFinancial_exp());
//			vo.setPRE_FIN_INST(inputVO3.getPre_fin_inst());
//			if(StringUtils.isNotBlank(inputVO3.getOther_pre_fin_inst())){
//				vo.setOTHER_PRE_FIN_INST(inputVO3.getOther_pre_fin_inst().trim());
//			}
//			if(StringUtils.isNotBlank(inputVO3.getOther_fi())){
//				vo.setOTHER_FI(inputVO3.getOther_fi().trim());
//			}
//			//前職業績表現(必填)
//			vo.setCUR_MONTHLY_GOAL(new BigDecimal(StringUtils.isNotBlank(inputVO3.getCur_m_goal())?inputVO3.getCur_m_goal().trim():"0"));
//			vo.setACTUAL_ACCOMPLISH(new BigDecimal(StringUtils.isNotBlank(inputVO3.getActual_accomplish())?inputVO3.getActual_accomplish().trim():"0"));
//			vo.setCUR_AUM(new BigDecimal(StringUtils.isNotBlank(inputVO3.getCur_aum())?inputVO3.getCur_aum().trim():"0"));
//			vo.setCUST_CNT(new BigDecimal(StringUtils.isNotBlank(inputVO3.getCust_cnt())?inputVO3.getCust_cnt().trim():"0"));
//			
//			//離職原因
//			if(StringUtils.isNotBlank(inputVO3.getResign_reason())){
//				vo.setRESIGN_REASON(inputVO3.getResign_reason().trim());
//			}else{
//				vo.setRESIGN_REASON(null);
//			}
//			//客戶紛爭/滿意度
//			if(StringUtils.isNotBlank(inputVO3.getCust_satisfaction())){
//				vo.setCUST_SATISFACTION(inputVO3.getCust_satisfaction().trim());
//			}else{
//				vo.setCUST_SATISFACTION(null);
//			}
//			//評估項目
//			if(StringUtils.isNotBlank(inputVO3.getAchievement())){
//				vo.setACHIEVEMENT(inputVO3.getAchievement());
//			}else{
//				vo.setACHIEVEMENT(null);
//			}
//			
//			if(StringUtils.isNotBlank(inputVO3.getSales_skill())){
//				vo.setSALES_SKILL(inputVO3.getSales_skill());
//			}else{
//				vo.setSALES_SKILL(null);
//			}
//			
//			if(StringUtils.isNotBlank(inputVO3.getActive())){
//				vo.setACTIVE(inputVO3.getActive());
//			}else{
//				vo.setACTIVE(null);
//			}
//			
//			if(StringUtils.isNotBlank(inputVO3.getPressure_manage())){
//				vo.setPRESSURE_MANAGE(inputVO3.getPressure_manage());
//			}else{
//				vo.setPRESSURE_MANAGE(null);
//			}
//			
//			if(StringUtils.isNotBlank(inputVO3.getCommunication())){
//				vo.setCOMMUNICATION(inputVO3.getCommunication());
//			}else{
//				vo.setCOMMUNICATION(null);
//			}
//			
//			if(StringUtils.isNotBlank(inputVO3.getProblem_solving_skill())){
//				vo.setPROBLEM_SOLVING_SKILL(inputVO3.getProblem_solving_skill());
//			}else{
//				vo.setPROBLEM_SOLVING_SKILL(null);
//			}
//			vo.setINTV_SUP_REMARK(StringUtils.isNotBlank(inputVO3.getIntv_sup_remark())?inputVO3.getIntv_sup_remark().trim():null);
//			if (StringUtils.isNotBlank(inputVO3.getFee_6m_ability()))
//				vo.setFEE_6M_ABILITY((StringUtils.isNotBlank(inputVO3.getFee_6m_ability())) ? new BigDecimal(inputVO3.getFee_6m_ability()) : null);
//			if (StringUtils.isNotBlank(inputVO3.getFee_1y_ability()))
//				vo.setFEE_1Y_ABILITY((StringUtils.isNotBlank(inputVO3.getFee_1y_ability())) ? new BigDecimal(inputVO3.getFee_1y_ability()) : null);
//			vo.setSUGGEST_JOB((StringUtils.isNotBlank(inputVO3.getSuggest_job())) ? inputVO3.getSuggest_job() : null);
//			vo.setSUGGEST_SALARY((StringUtils.isNotBlank(inputVO3.getSuggest_salary())) ? new BigDecimal(inputVO3.getSuggest_salary()) : null);
//			vo.setModifier(inputVO3.getLogin_id());
//			vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
//			
//			dam.update(vo);
//		}
		this.sendRtnObject(null);
	}
	
	private List<Map<String,Object>> getInterviewList(String fhcSeq , String custId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder(""
				+ "	SELECT	*	FROM	TBORG_EMP_HIRE_REVIEW_HOLDING	"
				+ "	WHERE	seqno="+fhcSeq+" AND CUST_ID='"+custId+"'");
		qc.setQueryString(sql.toString());
		return dam.exeQuery(qc);
	}
	public void interviewHolding(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130InputVO inputVo = (ORG130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder(""
				+ "	SELECT	*	FROM	TBORG_EMP_HIRE_REVIEW_HOLDING	"
				+ "	WHERE	BRANCH_NBR="+inputVo.getBranch_nbr()+" AND CUST_ID='"+inputVo.getCust_id()+"'");
		qc.setQueryString(sql.toString());
		List list = dam.exeQuery(qc);
		sendRtnObject(list);
	}
	
	public String chkNullrtnObjByStr (String str) {
		
		if (StringUtils.isBlank(str)) {
			return "";
		} else {
			return str;
		}
	}
	
	public Date chkNullrtnObjByDay (Date date) {
		
		if (null == date) {
			return null;
		} else {
			return date;
		}
	}
	
	public BigDecimal chkNullrtnObjByDgl (BigDecimal digital) {
		
		if (null == digital) {
			return BigDecimal.ZERO;
		} else {
			return digital;
		}
	}

	//金控版面談評估表
	public void saveInterviewHolding(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG130InputVO inputVo = (ORG130InputVO) body;
		String     seqNo             = inputVo.getFhcSeq() == null ? this.getSEQ() : inputVo.getFhcSeq();
		String     custId            = chkNullrtnObjByStr(inputVo.getCust_id()); 
		String     empId             = chkNullrtnObjByStr(inputVo.getEmp_id()); 
		String     empName           = chkNullrtnObjByStr(inputVo.getEmp_name()); 
		String     intvEmpId         = chkNullrtnObjByStr(inputVo.getIntv_emp_id()); 
		String     intvEmpName       = chkNullrtnObjByStr(inputVo.getIntv_emp_name()); 
		String     recommEmpId       = chkNullrtnObjByStr(inputVo.getRecomm_emp_id()); 
		String     recEmpName        = this.recempid(inputVo.getRecomm_emp_id());//推薦人姓名
		String     regionCenterId    = chkNullrtnObjByStr(inputVo.getRegion_center_id()); 
		String     branchAreaId      = chkNullrtnObjByStr(inputVo.getBranch_area_id()); 
		String     branchNbr         = chkNullrtnObjByStr(inputVo.getBranch_nbr()); 
		String     status            = chkNullrtnObjByStr(inputVo.getStatus());
		String     jobRank           = chkNullrtnObjByStr(inputVo.getJob_rank());
		String     jobTitleName      = chkNullrtnObjByStr(inputVo.getJob_title_name()); 
		String     aoJobRank         = chkNullrtnObjByStr(inputVo.getAo_job_rank());
		Date       evDate            = chkNullrtnObjByDay(inputVo.getEvDate()); 
		String     honest            = chkNullrtnObjByStr(inputVo.getHonest()); 
		String     kindly            = chkNullrtnObjByStr(inputVo.getKindly()); 
		String     prof              = chkNullrtnObjByStr(inputVo.getProf()); 
		String     innov             = chkNullrtnObjByStr(inputVo.getInnov()); 
		String     communication     = chkNullrtnObjByStr(inputVo.getCommunication()); 
		String     profSlvSkill      = chkNullrtnObjByStr(inputVo.getProblem_solving_skill()); 
		String     organize          = chkNullrtnObjByStr(inputVo.getOrganize()); 
		String     selfImprove       = chkNullrtnObjByStr(inputVo.getSelfImprove()); 
		String     resignReason      = chkNullrtnObjByStr(inputVo.getResign_reason()); 
		String     expectation       = chkNullrtnObjByStr(inputVo.getExpectation()); 
		Date       bookedOnboardDate = chkNullrtnObjByDay(inputVo.getBookedOnboardDate());
		BigDecimal expSalary         = chkNullrtnObjByDgl(inputVo.getExpSalary());
		String     intvSubRemark     = chkNullrtnObjByStr(inputVo.getIntv_sup_remark());
		BigDecimal preMonthGoal      = chkNullrtnObjByDgl(inputVo.getPreMonthGoal()); 
		BigDecimal pre6MAccomplish   = chkNullrtnObjByDgl(inputVo.getPre6MAccomplish()); 
		BigDecimal exp6MAbility      = chkNullrtnObjByDgl(inputVo.getExp6MAbility()); 
		BigDecimal exp1YAbility      = chkNullrtnObjByDgl(inputVo.getExp1YAbility()); 
		BigDecimal expPropertyLoan   = chkNullrtnObjByDgl(inputVo.getExpPropertyLoan());
		BigDecimal expCreditLoan     = chkNullrtnObjByDgl(inputVo.getExpCreditLoan()); 
		String     suggestJob        = chkNullrtnObjByStr(inputVo.getSuggest_job()); 
		String     hireStatus        = chkNullrtnObjByStr(inputVo.getHire_status()); 
		String     statusTransRemark = chkNullrtnObjByStr(inputVo.getHireStatusTransRemark()); 
		String     prefType          = chkNullrtnObjByStr(inputVo.getPrefType());
		String     buLayer1          = chkNullrtnObjByStr(inputVo.getBuLayer1());
		String     buLayer2          = chkNullrtnObjByStr(inputVo.getBuLayer2());
		String     buLayer3          = chkNullrtnObjByStr(inputVo.getBuLayer3());
		String     buLayer4          = chkNullrtnObjByStr(inputVo.getBuLayer4());
		String     buLayer5          = chkNullrtnObjByStr(inputVo.getBuLayer5());
		String     buLayer6          = chkNullrtnObjByStr(inputVo.getBuLayer6());
		String     buLayer7          = chkNullrtnObjByStr(inputVo.getBuLayer7());
		String     buLayer8          = chkNullrtnObjByStr(inputVo.getBuLayer8());
		String     crosBuLayer1      = chkNullrtnObjByStr(inputVo.getCrosBuLayer1());
		String     crosBuLayer2      = chkNullrtnObjByStr(inputVo.getCrosBuLayer2());
		String     crosBuLayer3      = chkNullrtnObjByStr(inputVo.getCrosBuLayer3());
		String     crosBuLayer4      = chkNullrtnObjByStr(inputVo.getCrosBuLayer4());
		String     crosBuLayer5      = chkNullrtnObjByStr(inputVo.getCrosBuLayer5());
		String     crosBuLayer6      = chkNullrtnObjByStr(inputVo.getCrosBuLayer6());
		String     crosBuLayer7      = chkNullrtnObjByStr(inputVo.getCrosBuLayer7());
		String     crosBuLayer8      = chkNullrtnObjByStr(inputVo.getCrosBuLayer8());
		
		// add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
		String     creditStatus      = chkNullrtnObjByStr(inputVo.getCredit_status());
		String     creditDtl         = chkNullrtnObjByStr(inputVo.getCredit_dtl());
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuilder sql = new StringBuilder();
		
		List<Map<String, Object>> interviewList = this.getInterviewList(inputVo.getFhcSeq(), inputVo.getCust_id());
		if (interviewList.isEmpty()) {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			sql = new StringBuilder();
			
			sql.append("INSERT INTO TBORG_EMP_HIRE_REVIEW_HOLDING ( ");
			sql.append("  SEQNO, CUST_ID, EMP_ID, EMP_NAME, INTV_EMP_ID, INTV_EMP_NAME, RECOMMENDER_EMP_ID, RECOMMENDER_EMP_NAME, ");
			sql.append("  REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, STATUS, JOB_RANK, JOB_TITLE_NAME, AO_JOB_RANK, ").append((evDate != null ? "EV_DATE, " : " "));
			sql.append("  HONEST ,");
			sql.append("  KINDLY, PROFESSION, INNOVATION, COMMUNICATION, PROBLEM_SOLVING_SKILL, ORGANIZE, SELF_IMPROVE, RESIGN_REASON, ");
			sql.append("  EXPECTATION, ").append((bookedOnboardDate != null ? "BOOKED_ONBOARD_DATE, " : " "));
			sql.append("  EXP_SALARY, INTV_SUP_REMARK, PRE_MONTHLY_GOAL, PRE_6M_ACCOMPLISH, EXP_6M_ABILITY, ");
			sql.append("  EXP_1Y_ABILITY, EXP_PROPERTY_LOAN, EXP_CREDIT_LOAN, SUGGEST_JOB, HIRE_STATUS, HIRE_STATUS_TRANS_REMARK, PERF_TYPE, ");
			sql.append("  BU_LAYER1, BU_LAYER2, BU_LAYER3, BU_LAYER4, BU_LAYER5, BU_LAYER6, BU_LAYER7, BU_LAYER8, ");
			sql.append("  CROS_BU_LAYER1, CROS_BU_LAYER2, CROS_BU_LAYER3, CROS_BU_LAYER4, CROS_BU_LAYER5, CROS_BU_LAYER6, CROS_BU_LAYER7, CROS_BU_LAYER8, ");
			sql.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, ");
			sql.append("  CREDIT_STATUS, CREDIT_DTL "); // add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
			sql.append(") ");
			sql.append("VALUES ( ");
			sql.append("  :seqNo, :custId, :empId, :empName, :intvEmpId, :intvEmpName, :recEmpId, :recEmpName, ");
			sql.append("  :regCenId, :braAreaId, :braNbr, :status, :jobRank, :jobTitName, :aoJobRank, ").append((evDate != null ? ":evDate, " : " "));
			sql.append("  :honest, ");
			sql.append("  :kindly, :prof, :innov, :comm, :proSlvSkill, :organize, :selfImprove, :resignReason, ");
			sql.append("  :expectation, ").append((bookedOnboardDate != null ? ":bookedOnboardDate, " : " "));
			sql.append("  :expSalary, :intvSupRmk, :preMonthGoal, :pre6MAccomp, :exp6MAbility, ");
			sql.append("  :exp1YAbility, :expPropertyLoan, :expCreditLoan, :suggestJob, :hireStatus, :hireStatusTransRemark, :prefType, ");
			sql.append("  :buLayer1, :buLayer2, :buLayer3, :buLayer4, :buLayer5, :buLayer6, :buLayer7, :buLayer8, ");
			sql.append("  :crosBuLayer1, :crosBuLayer2, :crosBuLayer3, :crosBuLayer4, :crosBuLayer5, :crosBuLayer6, :crosBuLayer7, :crosBuLayer8, ");
			sql.append("  :version, :createTime, :creator, :modifier, :lastUpdate, ");
			sql.append("  :creditStatus, :creditDtl "); // add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
			sql.append(")");

			qc.setObject("seqNo", seqNo);//NUMBER序號
			qc.setObject("custId", custId);//VARCHAR2(20 BYTE)應徵者身份證編號
			qc.setObject("empId", empId);//VARCHAR2(11 BYTE)應徵者員編
			qc.setObject("empName", empName);//VARCHAR2(100 BYTE)應徵者姓名
			qc.setObject("intvEmpId", intvEmpId);//VARCHAR2(11 BYTE)面談者ID
			qc.setObject("intvEmpName", intvEmpName);//VARCHAR2(100 BYTE)面談者姓名
			qc.setObject("recEmpId", recommEmpId);//VARCHAR2(11 BYTE)推薦人員編
			qc.setObject("recEmpName", recEmpName);//VARCHAR2(100 BYTE)推薦人姓名
			qc.setObject("regCenId", regionCenterId);//VARCHAR2(10 BYTE)區域中心代碼
			qc.setObject("braAreaId", branchAreaId);//VARCHAR2(10 BYTE)營運區代碼
			qc.setObject("braNbr", branchNbr);//VARCHAR2(10 BYTE)分行代碼
			qc.setObject("status", status);//CHAR(2 BYTE)目前狀態
			qc.setObject("jobRank", jobRank);//CHAR(1 BYTE)應徵職等ORG.JOB_RANK
			qc.setObject("jobTitName", jobTitleName);//VARCHAR2(50 BYTE)應徵職稱
			qc.setObject("aoJobRank", aoJobRank);//VARCHAR2(60 BYTE)職務ORG.HIRE_JOB_RANK
			if (evDate != null)
				qc.setObject("evDate", evDate);//DATE 面談評估日
			qc.setObject("honest", honest);//CHAR(1 BYTE)誠信
			qc.setObject("kindly", kindly);//CHAR(1 BYTE)親切
			qc.setObject("prof", prof);//CHAR(1 BYTE)    專業
			qc.setObject("innov", innov);//CHAR(1 BYTE) 創新
			qc.setObject("comm", communication);//CHAR(1 BYTE)溝通協調性
			qc.setObject("proSlvSkill", profSlvSkill);//CHAR(1 BYTE)分析決斷
			qc.setObject("organize", organize);//CHAR(1 BYTE)組織能力
			qc.setObject("selfImprove", selfImprove);//CHAR(1 BYTE)自我要求
			qc.setObject("resignReason", resignReason);//VARCHAR2(100 CHAR)更換工作原因
			qc.setObject("expectation", expectation);//varchar2(100 char)對應徵工作的期待與看法
			if (bookedOnboardDate != null)
				qc.setObject("bookedOnboardDate", bookedOnboardDate);//DATE可上班日期
			qc.setObject("expSalary", expSalary == null ? new BigDecimal(0) : expSalary);//NUMBER(11,2)期望待遇
			qc.setObject("intvSupRmk", intvSubRemark);//VARCHAR2(500 CHAR)面談主管評語
			qc.setObject("preMonthGoal", preMonthGoal);//NUMBER(11,2)前職每月業績目標
			qc.setObject("pre6MAccomp", pre6MAccomplish);//NUMBER(11,2)前職6M平均實際達成(萬)
			qc.setObject("exp6MAbility", exp6MAbility);//NUMBER(11,2)評估未來6M手收能力
			qc.setObject("exp1YAbility", exp1YAbility);//NUMBER(11,2)評估未來1Y手收能力
			qc.setObject("expPropertyLoan", expPropertyLoan);//NUMBER(11,2)房貸每月撥款金額(佰萬)
			qc.setObject("expCreditLoan", expCreditLoan);//NUMBER(11,2)信貸每月撥款金額(佰萬)
			qc.setObject("suggestJob", suggestJob);//VARCHAR2(50 CHAR)建議職等職稱
			qc.setObject("hireStatus", hireStatus);//CHAR(1 BYTE)面談結果：0-合格/1-錄用/2-備取/3-不錄用/4-轉介至其他部門/職務/5-暫存
			qc.setObject("hireStatusTransRemark", statusTransRemark);//VARCHAR2(50 CHAR)面談結果：4-轉介至其他部門/職務描述
			qc.setObject("prefType", prefType);//CHAR(1 CHAR)績效評核類別
			qc.setObject("buLayer1", buLayer1);//VARCHAR2(50 CHAR)  BU第一階名稱
			qc.setObject("buLayer2", buLayer2);//VARCHAR2(50 CHAR)  BU第二階名稱
			qc.setObject("buLayer3", buLayer3);//VARCHAR2(50 CHAR)  BU第三階名稱
			qc.setObject("buLayer4", buLayer4);//VARCHAR2(50 CHAR)  BU第四階名稱
			qc.setObject("buLayer5", buLayer5);//VARCHAR2(50 CHAR)  BU第五階名稱
			qc.setObject("buLayer6", buLayer6);//VARCHAR2(50 CHAR)  BU第六階名稱
			qc.setObject("buLayer7", buLayer7);//VARCHAR2(50 CHAR)  BU第七階名稱
			qc.setObject("buLayer8", buLayer8);//VARCHAR2(50 CHAR)  BU第八階名稱
			qc.setObject("crosBuLayer1", crosBuLayer1);//VARCHAR2(50 CHAR)  跨BU第一階名稱
			qc.setObject("crosBuLayer2", crosBuLayer2);//VARCHAR2(50 CHAR)  跨BU第二階名稱
			qc.setObject("crosBuLayer3", crosBuLayer3);//VARCHAR2(50 CHAR)  跨BU第三階名稱
			qc.setObject("crosBuLayer4", crosBuLayer4);//VARCHAR2(50 CHAR)  跨BU第四階名稱
			qc.setObject("crosBuLayer5", crosBuLayer5);//VARCHAR2(50 CHAR)  跨BU第五階名稱
			qc.setObject("crosBuLayer6", crosBuLayer6);//VARCHAR2(50 CHAR)跨BU第六階名稱
			qc.setObject("crosBuLayer7", crosBuLayer7);//VARCHAR2(50 CHAR)跨BU第七階名稱
			qc.setObject("crosBuLayer8", crosBuLayer8);//VARCHAR2(50 CHAR)跨BU第八階名稱
			qc.setObject("version", new BigDecimal(0)); //NUMBER(19,0)版本
			qc.setObject("createTime", new Date());//DATE建立日期
			qc.setObject("creator", intvEmpId);//VARCHAR2(255 BYTE)建立者
			qc.setObject("modifier", intvEmpId);//VARCHAR2(255 BYTE)修改者
			qc.setObject("lastUpdate", new Date());//DATE最後修改日期
			
			// add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
			qc.setObject("creditStatus", creditStatus);
			qc.setObject("creditDtl", creditDtl);

			qc.setQueryString(sql.toString());
			dam.exeUpdate(qc);
			this.updateHireInfo(custId, branchNbr,inputVo.getSeq(), seqNo);
		} else {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			sql = new StringBuilder();
			
			sql.append("UPDATE TBORG_EMP_HIRE_REVIEW_HOLDING ");
			sql.append("SET HONEST = :honest, KINDLY = :kindly, PROFESSION = :prof, INNOVATION = :innov, COMMUNICATION = :comm, ");
			sql.append("    PROBLEM_SOLVING_SKILL = :proSlvSkill, ORGANIZE = :organize, SELF_IMPROVE = :selfImprove, RESIGN_REASON = :resignReason, ");
			sql.append("    EXPECTATION = :expectation, ").append((bookedOnboardDate != null ? "BOOKED_ONBOARD_DATE = :bookedOnboardDate, " : " "));
			sql.append("    EXP_SALARY = :expSalary, INTV_SUP_REMARK = :intvSupRmk, PRE_MONTHLY_GOAL = :preMonthGoal, ");
			sql.append("    PRE_6M_ACCOMPLISH = :pre6MAccomp, EXP_6M_ABILITY = :exp6MAbility, ");
			sql.append("    EXP_1Y_ABILITY = :exp1YAbility, EXP_PROPERTY_LOAN = :expPropertyLoan, EXP_CREDIT_LOAN = :expCreditLoan, ");
			sql.append("    SUGGEST_JOB = :suggestJob, HIRE_STATUS = :hireStatus, HIRE_STATUS_TRANS_REMARK = :statusTransRemark, PERF_TYPE = :prefType, ");
			sql.append("    BU_LAYER1 = :buLayer1, BU_LAYER2 = :buLayer2, BU_LAYER3 = :buLayer3, BU_LAYER4 = :buLayer4, ");
			sql.append("    BU_LAYER5 = :buLayer5, BU_LAYER6 = :buLayer6, BU_LAYER7 = :buLayer7, BU_LAYER8 = :buLayer8, ");
			sql.append("    CROS_BU_LAYER1 = :crosBuLayer1, CROS_BU_LAYER2 = :crosBuLayer2, CROS_BU_LAYER3 = :crosBuLayer3, CROS_BU_LAYER4 = :crosBuLayer4, ");
			sql.append("    CROS_BU_LAYER5 = :crosBuLayer5, CROS_BU_LAYER6 = :crosBuLayer6, CROS_BU_LAYER7 = :crosBuLayer7, CROS_BU_LAYER8 = :crosBuLayer8, ");
			sql.append("    MODIFIER = :modifier, LASTUPDATE = SYSDATE, ");
			sql.append("    CREDIT_STATUS = :creditStatus, CREDIT_DTL = :creditDtl ");
			sql.append("WHERE CUST_ID = :custId AND BRANCH_NBR = :branchNbr ");

			qc.setObject("honest", honest);//CHAR(1 BYTE)誠信
			qc.setObject("kindly", kindly);//CHAR(1 BYTE)親切
			qc.setObject("prof", prof);//CHAR(1 BYTE)    專業
			qc.setObject("innov", innov);//CHAR(1 BYTE) 創新
			qc.setObject("comm", communication);//CHAR(1 BYTE)溝通協調性
			qc.setObject("proSlvSkill", profSlvSkill);//CHAR(1 BYTE)分析決斷
			qc.setObject("organize", organize);//CHAR(1 BYTE)組織能力
			qc.setObject("selfImprove", selfImprove);//CHAR(1 BYTE)自我要求
			qc.setObject("resignReason", resignReason);//VARCHAR2(100 CHAR)更換工作原因
			qc.setObject("expectation", expectation);//varchar2(100 char)對應徵工作的期待與看法
			if (bookedOnboardDate != null)
				qc.setObject("bookedOnboardDate", bookedOnboardDate);//DATE可上班日期
			qc.setObject("expSalary", expSalary == null ? new BigDecimal(0) : expSalary);//NUMBER(11,2)期望待遇
			qc.setObject("intvSupRmk", intvSubRemark);//VARCHAR2(500 CHAR)面談主管評語
			qc.setObject("preMonthGoal", preMonthGoal);//NUMBER(11,2)前職每月業績目標
			qc.setObject("pre6MAccomp", pre6MAccomplish);//NUMBER(11,2)前職6M平均實際達成(萬)
			qc.setObject("exp6MAbility", exp6MAbility);//NUMBER(11,2)評估未來6M手收能力
			qc.setObject("exp1YAbility", exp1YAbility);//NUMBER(11,2)評估未來1Y手收能力
			qc.setObject("expPropertyLoan", expPropertyLoan);//NUMBER(11,2)房貸每月撥款金額(佰萬)
			qc.setObject("expCreditLoan", expCreditLoan);//NUMBER(11,2)信貸每月撥款金額(佰萬)
			qc.setObject("suggestJob", suggestJob);//VARCHAR2(50 CHAR)建議職等職稱
			qc.setObject("hireStatus", hireStatus);//CHAR(1 BYTE)面談結果：0-合格/1-錄用/2-備取/3-不錄用/4-轉介至其他部門/職務/5-暫存
			qc.setObject("statusTransRemark", statusTransRemark);//VARCHAR2(50 CHAR)面談結果：4-轉介至其他部門/職務描述
			qc.setObject("prefType", prefType);//CHAR(1 CHAR)績效評核類別
			qc.setObject("buLayer1", buLayer1);//VARCHAR2(50 CHAR)  BU第一階名稱
			qc.setObject("buLayer2", buLayer2);//VARCHAR2(50 CHAR)  BU第二階名稱
			qc.setObject("buLayer3", buLayer3);//VARCHAR2(50 CHAR)  BU第三階名稱
			qc.setObject("buLayer4", buLayer4);//VARCHAR2(50 CHAR)  BU第四階名稱
			qc.setObject("buLayer5", buLayer5);//VARCHAR2(50 CHAR)  BU第五階名稱
			qc.setObject("buLayer6", buLayer6);//VARCHAR2(50 CHAR)  BU第六階名稱
			qc.setObject("buLayer7", buLayer7);//VARCHAR2(50 CHAR)  BU第七階名稱
			qc.setObject("buLayer8", buLayer8);//VARCHAR2(50 CHAR)  BU第八階名稱
			qc.setObject("crosBuLayer1", crosBuLayer1);//VARCHAR2(50 CHAR)  跨BU第一階名稱
			qc.setObject("crosBuLayer2", crosBuLayer2);//VARCHAR2(50 CHAR)  跨BU第二階名稱
			qc.setObject("crosBuLayer3", crosBuLayer3);//VARCHAR2(50 CHAR)  跨BU第三階名稱
			qc.setObject("crosBuLayer4", crosBuLayer4);//VARCHAR2(50 CHAR)  跨BU第四階名稱
			qc.setObject("crosBuLayer5", crosBuLayer5);//VARCHAR2(50 CHAR)  跨BU第五階名稱
			qc.setObject("crosBuLayer6", crosBuLayer6);//VARCHAR2(50 CHAR)跨BU第六階名稱
			qc.setObject("crosBuLayer7", crosBuLayer7);//VARCHAR2(50 CHAR)跨BU第七階名稱
			qc.setObject("crosBuLayer8", crosBuLayer8);//VARCHAR2(50 CHAR)跨BU第八階名稱
			qc.setObject("modifier", intvEmpId);//VARCHAR2(255 BYTE)修改者
			qc.setObject("custId", custId);
			qc.setObject("branchNbr", branchNbr);
			
			// add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
			qc.setObject("creditStatus", creditStatus);
			qc.setObject("creditDtl", creditDtl);
			
			qc.setQueryString(sql.toString());
			dam.exeUpdate(qc);
		}

		this.sendRtnObject(null);
	}
	private void updateHireInfo(String	custId, String branchNbr, BigDecimal infoSeq, String seqno) throws DAOException, JBranchException	{
		dam	=	this.getDataAccessManager();
		QueryConditionIF	condIf	=	dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuilder	sql	=	new	StringBuilder();
		sql.append("	UPDATE	TBORG_EMP_HIRE_INFO	");
		sql.append("	SET	FHC_SEQ	=	:fchSeq	");
		sql.append("	WHERE	CUST_ID	=	:custId	AND	BRANCH_NBR	=	:branchNbr AND	SEQ	=	:seq	");
		condIf.setObject("fchSeq", seqno);
		condIf.setObject("custId", custId);
		condIf.setObject("branchNbr", branchNbr);
		condIf.setObject("seq", infoSeq);
		condIf.setQueryString(sql.toString());
		dam.exeUpdate(condIf);
	}
	//匯出Excel
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		ORG130OutputVO return_VO2 = (ORG130OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getList();
		if(list.size() > 0){
			String fileName = "業務人員進用流程查詢.csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[22];
				int i = 0;
				records[i] = checkIsNull(map, "REGION_CENTER_NAME");  //區域中心name
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");  //營運區	name
				records[++i] = checkIsNull(map, "BRANCH_NAME");       //分行name
				records[++i] = checkIsNull(map, "EMP_NAME");  
				records[++i] = checkIsNull(map, "AO_JOB_RANK");  
				records[++i] = checkIsNull(map, "STATUS_NAME");  
				records[++i] = checkIsNull(map, "BRCH_RECV_CASE_DATE");  
				records[++i] = checkIsNull(map, "HO_RECV_CASE_DATE");  
				records[++i] = checkIsNull(map, "OA_SUP_RT_DATE");  
				records[++i] = checkIsNull(map, "BOOKED_ONBOARD_DATE");  
				records[++i] = checkIsNull(map, "RETURN_REMARK");  
				records[++i] = checkIsNull(map, "BLACK_LISTED_NAME");  
				records[++i] = checkIsNull(map, "MODIFIER");  
				records[++i] = checkIsNull(map, "LASTUPDATE");  
				listCSV.add(records);
			}
				//header
				String [] csvHeader = new String[15];
				int j = 0;
				csvHeader[j]   = "業務處";
				csvHeader[++j] = "營運區";
				csvHeader[++j] = "分行";
				csvHeader[++j] = "應徵者";
				csvHeader[++j] = "職務	";
				csvHeader[++j] = "狀態";
				csvHeader[++j] = "分行進件日";
				csvHeader[++j] = "總行收件日";
				csvHeader[++j] = "區主管複試日";
				csvHeader[++j] = "預定報到日";
				csvHeader[++j] = "備註/回任";
				csvHeader[++j] = "黑名單";
				csvHeader[++j] = "最後修改人";
				csvHeader[++j] = "最後修改時間";

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				notifyClientToDownloadFile(url, fileName); //download
			} else {
				return_VO2.setResultList6(list);
				this.sendRtnObject(return_VO2);
			   
			}
	
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
	/**產生seq No */
	private String getSEQ() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBORG_EMP_HIRE_INFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	private String getSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCRM_WKPG_MD_MAST.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	private BigDecimal getInfoSeq() throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBORG_EMP_HIRE_INFO.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return new BigDecimal(ObjectUtils.toString(SEQLIST.get(0).get("SEQ")));
	}
	
	private String validateHoliday(String date) throws JBranchException{
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT PABTH_UTIL.FC_IsHoliday(to_date(:endDate,'YYYYMMDD'), 'TWD')AS REWDATE FROM DUAL ");
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("endDate", date);
		List<Map> list =null;
		list = dam.exeQuery(queryCondition);
		date = (String)list.get(0).get("REWDATE").toString();
		
		return  date;
	}
	
	public String getMailSQL(){
		
		StringBuffer sb = new StringBuffer();
		
//		sqlMail.append(" SELECT DISTINCT MEMBER.EMP_EMAIL_ADDRESS ");
//		sqlMail.append(" FROM VWORG_BRANCH_EMP_DETAIL_INFO EMP, ");
//		sqlMail.append(" 	TBORG_MEMBER MEMBER, ");
//		sqlMail.append(" 	VWORG_DEFN_INFO DEFN, ");
//		sqlMail.append(" 	TBORG_MEMBER_ROLE ROLE ");
//		sqlMail.append(" WHERE EMP.EMP_ID = ROLE.EMP_ID ");
//		sqlMail.append(" 	AND EMP.EMP_ID = MEMBER.EMP_ID ");
//		sqlMail.append(" 	AND EMP.REGION_CENTER_ID = DEFN.REGION_CENTER_ID ");
//		sqlMail.append(" 	AND EMP.REGION_CENTER_ID = :region_center_id ");
//		sqlMail.append(" 	AND ROLE.ROLE_ID IN  (select roleid from TBSYSSECUROLPRIASS where privilegeid = :privilegeid) ");
		
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
		sb.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sb.append("  UNION ");
		sb.append("  SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
		sb.append("  FROM VWORG_EMP_PLURALISM_INFO ");
		sb.append("), ");
		sb.append("EMP_BASE AS ( ");
		sb.append("  SELECT M.EMP_ID, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, 'Y' AS ROLE_TYPE ");
		sb.append("  FROM TBORG_MEMBER M ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("    FROM TBORG_DEFN ");
		sb.append("    START WITH DEPT_ID IS NOT NULL ");
		sb.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sb.append("  WHERE M.SERVICE_FLAG = 'A' ");
		sb.append("  AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("  UNION ");
		sb.append("  SELECT M.EMP_ID, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, 'N' AS ROLE_TYPE ");
		sb.append("  FROM TBORG_MEMBER_PLURALISM M ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("    FROM TBORG_DEFN ");
		sb.append("    START WITH DEPT_ID IS NOT NULL ");
		sb.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sb.append("  WHERE (TRUNC(M.TERDTE) >= TRUNC(SYSDATE) OR M.TERDTE IS NULL) ");
		sb.append("  AND M.ACTION <> 'D' ");
		sb.append(") ");
		sb.append(", EMP_LIST AS ( ");
		sb.append("  SELECT C.EMP_ID, C.JOB_TITLE_NAME, C.ROLE_TYPE, C.CODE_LV1, D.NAME_LV1, C.CODE_LV2, D.NAME_LV2, C.CODE_LV3, D.NAME_LV3, C.CODE_LV4, D.NAME_LV4, ");
		sb.append("         C.REGION_CENTER_ID, D.REGION_CENTER_NAME, C.BRANCH_AREA_ID, D.BRANCH_AREA_NAME, C.BRANCH_NBR, D.BRANCH_NAME ");
		sb.append("  FROM ( ");
		sb.append("    SELECT EMP_ID, JOB_TITLE_NAME, ROLE_TYPE, CODE_LV1, CODE_LV2, CODE_LV3, CODE_LV4, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ");
		sb.append("    FROM ( ");
		sb.append("      SELECT EMP_ID, JOB_TITLE_NAME, ORG_TYPE, DEPT_ID, ROLE_TYPE FROM EMP_BASE ");
		sb.append("    ) PIVOT (MAX(DEPT_ID) FOR ORG_TYPE IN ('00' AS CODE_LV1, '05' AS CODE_LV2, '10' AS CODE_LV3, '20' AS CODE_LV4, '30' AS REGION_CENTER_ID, '40' AS BRANCH_AREA_ID, '50' AS BRANCH_NBR)) ");
		sb.append("  ) C ");
		sb.append("  INNER JOIN ( ");
		sb.append("    SELECT EMP_ID, JOB_TITLE_NAME, ROLE_TYPE, NAME_LV1, NAME_LV2, NAME_LV3, NAME_LV4, REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NAME ");
		sb.append("    FROM ( ");
		sb.append("      SELECT EMP_ID, JOB_TITLE_NAME, ORG_TYPE, DEPT_NAME, ROLE_TYPE FROM EMP_BASE ");
		sb.append("    ) PIVOT (MAX(DEPT_NAME) FOR ORG_TYPE IN ('00' AS NAME_LV1, '05' AS NAME_LV2, '10' AS NAME_LV3, '20' AS NAME_LV4, '30' AS REGION_CENTER_NAME, '40' AS BRANCH_AREA_NAME, '50' AS BRANCH_NAME)) ");
		sb.append("  ) D ON C.EMP_ID = D.EMP_ID AND C.ROLE_TYPE = D.ROLE_TYPE ");
		sb.append(") ");
		sb.append(", ROLE_EMP_LIST AS ( ");
		sb.append("  SELECT MR.ROLE_ID, PRI.PRIVILEGEID, EL.EMP_ID, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME ");
		sb.append("  FROM EMP_LIST EL ");
		sb.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON EL.ROLE_TYPE = 'Y' AND EL.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
		sb.append("  UNION ");
		sb.append("  SELECT MR.ROLE_ID, PRI.PRIVILEGEID, EL.EMP_ID, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME ");
		sb.append("  FROM EMP_LIST EL ");
		sb.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON EL.ROLE_TYPE = 'N' AND EL.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'N' ");
		sb.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
		sb.append(") ");
		sb.append(", ADD_HIRE_EMP_LIST AS ( ");
		sb.append("  SELECT MEM.EMP_ID, MEM.REGION_CENTER_ID, MEM.BRANCH_AREA_ID, MEM.BRANCH_NBR, REL.ROLE_ID, ");
		sb.append("         CASE WHEN PRIVILEGEID = '012' THEN 'BRANCH_AREA_EMP' ");
		sb.append("              WHEN ROLE_ID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NOT NULL) THEN 'BRANCH_EMP' ");
		sb.append("              WHEN PRIVILEGEID = '013' THEN 'REGION_CENTER_EMP' ");
		sb.append("              WHEN PRIVILEGEID = '043' THEN 'HEAD_EMP' ");
		sb.append("         ELSE NULL END AS EMP_BOSS ");
		sb.append("  FROM BASE MEM ");
		sb.append("  LEFT JOIN ROLE_EMP_LIST REL ON MEM.EMP_ID = REL.EMP_ID ");
		sb.append("  WHERE REL.PRIVILEGEID = :inPri ");
		sb.append("  AND MEM.BRANCH_NBR = :inBranch ");
		sb.append(") ");
		
		return sb.toString();
	}
	
	public String getBossSQL (String bossType) {
		
		StringBuffer sb = new StringBuffer();
		
		switch (bossType) {
		case "APPLY_BOSS" :
			sb.append("SELECT 'APPLY_BOSS' AS BOSS_TYPE, MEM.EMP_ID AS KEYIN_EMP_ID, MEM.EMP_ID AS BOSS_ID, '011' AS PRIVILEGEID, MEM.EMP_EMAIL_ADDRESS ");
			sb.append("FROM TBORG_MEMBER MEM ");
			sb.append("WHERE EMP_ID = :creator ");
			break;
		case "AREA_BOSS" :
			sb.append("SELECT 'AREA_BOSS' AS BOSS_TYPE, SL.EMP_ID AS KEYIN_EMP_ID, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID, MEM.EMP_EMAIL_ADDRESS ");
			sb.append("FROM ADD_HIRE_EMP_LIST SL ");
			sb.append("LEFT JOIN ROLE_EMP_LIST REL ON REL.PRIVILEGEID = '012' AND SL.BRANCH_AREA_ID = REL.BRANCH_AREA_ID ");
			sb.append("INNER JOIN TBORG_MEMBER MEM ON REL.EMP_ID = MEM.EMP_ID ");
			break;
		case "REGION_BOSS" : 
			sb.append("SELECT 'REGION_BOSS' AS BOSS_TYPE, SL.EMP_ID AS KEYIN_EMP_ID, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID, MEM.EMP_EMAIL_ADDRESS ");
			sb.append("FROM ADD_HIRE_EMP_LIST SL ");
			sb.append("LEFT JOIN ROLE_EMP_LIST REL ON REL.PRIVILEGEID = '013' AND SL.REGION_CENTER_ID = REL.REGION_CENTER_ID ");
			sb.append("INNER JOIN TBORG_MEMBER MEM ON REL.EMP_ID = MEM.EMP_ID ");
			break;
		case "HEAD_BOSS" :
			sb.append("SELECT 'HEAD_BOSS' AS BOSS_TYPE, SL.EMP_ID AS KEYIN_EMP_ID, REL.EMP_ID AS BOSS_ID, '043' AS PRIVILEGEID, REL.EMP_EMAIL_ADDRESS ");
			sb.append("FROM ADD_HIRE_EMP_LIST SL ");
			sb.append("LEFT JOIN (SELECT EMP_ID, EMP_EMAIL_ADDRESS FROM TBORG_MEMBER WHERE EMP_ID IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.HEAD_BOSS')) REL ON 1 = 1 ");
			break;
		}
		
		return sb.toString();
	}
	
	public List<Map<String, String>> getMailList(String applyBoss, ORG130InputVO inputVO, ORG130OutputVO outputVO) throws DAOException, JBranchException{
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);  
		StringBuffer sb = new StringBuffer();
		
		String status = inputVO.getStatus();
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		sb.append(getMailSQL());
		switch (status) {
			case "01" : //01-分行(組)主管進件：督導&人管科(ORG.HEAD_BOSS)
				logger.info("01-分行(組)主管進件：督導&人管科(ORG.HEAD_BOSS)");
				sb.append(getBossSQL("AREA_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("HEAD_BOSS"));
				
				break;
			case "02" : //02-總行已收件：進件主管
				logger.info("02-總行已收件：進件主管");
				sb.append(getBossSQL("APPLY_BOSS"));
				
				queryCondition.setObject("creator", applyBoss); //進件主管
				break;
			case "03" : //03-待處主管複試：處主管&人管科(ORG.HEAD_BOSS)
				logger.info("03-待處主管複試：處主管&人管科(ORG.HEAD_BOSS)");
				sb.append(getBossSQL("REGION_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("HEAD_BOSS"));
				break;
			case "04" : //04-待核薪-缺件/05-待核薪中：進件主管&督導&人管科(ORG.HEAD_BOSS)
			case "05" : 
				logger.info("04-待核薪-缺件/05-待核薪中：進件主管&督導&人管科(ORG.HEAD_BOSS)");
				sb.append(getBossSQL("APPLY_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("AREA_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("HEAD_BOSS"));
				
				queryCondition.setObject("creator", applyBoss); //進件主管
				break;
			case "06" : //06-已核薪缺證照：進件主管&督導
				logger.info("06-已核薪缺證照：進件主管&督導");
				sb.append(getBossSQL("APPLY_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("AREA_BOSS"));
				
				queryCondition.setObject("creator", applyBoss); //進件主管
				break;
			case "07" : //07-待報到：進件主管&督導&人管科(ORG.HEAD_BOSS)
				logger.info("07-待報到：進件主管&督導&人管科(ORG.HEAD_BOSS)");
				sb.append(getBossSQL("APPLY_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("AREA_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("HEAD_BOSS"));
				
				queryCondition.setObject("creator", applyBoss); //進件主管
				break;
			case "08" : //08-結案-不通過/09-結案-已報到 /10-結案-不報到：督導&處主管&人管科(ORG.HEAD_BOSS)
			case "09" : 
			case "10" :
				logger.info("08-結案-不通過/09-結案-已報到 /10-結案-不報到：督導&處主管&人管科(ORG.HEAD_BOSS)");
				sb.append(getBossSQL("AREA_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("REGION_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("HEAD_BOSS"));
				break;
			case "11" : //11-處主管複試後退回：進件主管&督導&人管科(ORG.HEAD_BOSS)
				logger.info("11-處主管複試後退回：進件主管&督導&人管科(ORG.HEAD_BOSS)");
				sb.append(getBossSQL("APPLY_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("AREA_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("HEAD_BOSS"));
				
				queryCondition.setObject("creator", applyBoss); //進件主管
				break;
			case "12" : //12-待督導覆核：督導&人管科(ORG.HEAD_BOSS)
				logger.info("12-待督導覆核：督導&人管科(ORG.HEAD_BOSS)");
				sb.append(getBossSQL("AREA_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("HEAD_BOSS"));
				break;
			case "13" : //13-督導覆核退回：進件主管&人管科(ORG.HEAD_BOSS)
				logger.info("13-督導覆核退回：進件主管&人管科(ORG.HEAD_BOSS)");
				sb.append(getBossSQL("APPLY_BOSS"));
				sb.append("UNION ");
				sb.append(getBossSQL("HEAD_BOSS"));
				
				queryCondition.setObject("creator", applyBoss); //進件主管
				break;
		}

		queryCondition.setObject("inPri", "011"); //鎖定分行主管
		queryCondition.setObject("inBranch", inputVO.getBranch_nbr()); //進件分行
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, String>> mailList = dam.exeQuery(queryCondition);
		
		for (Map<String, String> map : mailList) {
			boolean chkMail = chkMailIsNotNull(map.get("EMP_EMAIL_ADDRESS"));
			if (chkMail) {
				Map<String, String> mailMap = new HashMap<String, String>();
				mailMap.put(FubonSendJavaMail.MAIL, map.get("EMP_EMAIL_ADDRESS"));
				list.add(mailMap);
			} else {
				switch (map.get("BOSS_TYPE")){
				case "APPLY_BOSS" :
					outputVO.setErrorMsg("進件主管Mail有誤 / 進件主管無Mail");
					break;
				case "AREA_BOSS" :
					outputVO.setErrorMsg("直屬營運督導Mail有誤 / 直屬營運督導無Mail");
					break;
				case "REGION_BOSS" :
					outputVO.setErrorMsg("直屬業務處處長Mail有誤 / 直屬業務處處長無Mail");
					break;
				case "HEAD_BOSS" :
					outputVO.setErrorMsg("人員管理科經辦Mail有誤 / 人員管理科經辦無Mail");
					break;
				}
			}
		}

		return list;
	}
	
	public boolean chkMailIsNotNull(String mail){
		
		if(StringUtils.isBlank(mail)){
			return false;
		}else{
			if(isEmail(ObjectUtils.toString(mail)) == false){
				return false;
			}else {
				return true;
			}
		}
	}	
	
	//信箱Email格式檢查
	public static boolean isEmail(String email) {
		Pattern emailPattern = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find()) {
			return true;
		}
		return false;
	}	
	
	public void sendEmail(List<Map<String, String>> mailList, ORG130InputVO inputVO) throws Exception{
		FubonSendJavaMail sendMail = new FubonSendJavaMail();
		FubonMail mail = new FubonMail();
						
		Map<String, Object> map = new HashMap<String, Object>();
						
		// 組出信件內容所需參數
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式			
		XmlInfo xmlInfo = new XmlInfo();
		String currStatus = xmlInfo.getVariable("ORG.HIRE_STATUS", inputVO.getStatus(), "F3");
						
		mail.setLstMailTo(mailList);
						
		//設定信件主旨
		mail.setSubject("{通知}個金業務管理系統「人員進用流程」狀態更新");
						
		//設定信件內容
		mail.setContent("個金業務管理分行人員進用流程管理 「" + (StringUtils.isNotBlank(inputVO.getAo_job_rank()) ? inputVO.getAo_job_rank() : "") + " " + inputVO.getEmp_name() + " 」" 
						+ " 「目前狀態為 " + currStatus + " 」 " + df.format(new Date()));
						
		//寄出信件
		sendMail.sendMail(mail, map);
	}
}