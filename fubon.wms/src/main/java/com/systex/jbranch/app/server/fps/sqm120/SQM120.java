package com.systex.jbranch.app.server.fps.sqm120;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_FLOW_DTLPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_FLOW_DTLVO;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_IMPROVE_DTLVO;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_IMPROVE_MASTPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_IMPROVE_MASTVO;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Description : 問卷報告回覆
 * Author : Willis
 * Date : 2018/03/30
 * Modifier : 2021/03/04 BY OCEAN => #0535: WMS-CR-20210115-01_擬新增即時滿意度淨推薦值問項
 */

@Component("sqm120")
@Scope("request")
public class SQM120 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SQM120.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 查詢資料
	 * 
	 * @throws ParseException
	 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM120InputVO inputVO = (SQM120InputVO) body;
		SQM120OutputVO outputVO = new SQM120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		// String ID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String ID = (String) getCommonVariable(SystemVariableConsts.LOGINID); // 被代理人id
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); // 區督導
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2); // 分行人員

		boolean headFlag = headmgrMap.containsKey(roleID);
		boolean mbrFlag = mbrmgrMap.containsKey(roleID);
		boolean bmFlag = bmmgrMap.containsKey(roleID);

		// 取得查詢資料可視範圍
		// PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		// PMS000InputVO pms000InputVO = new PMS000InputVO();
		// pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));
		// PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		// 主查詢 sql 修正 20170120
		sql.append("select A.*, ");
		sql.append("       ORG.REGION_CENTER_ID AS REGION_CENTER_ID_ORG, ");
		sql.append("       ORG.REGION_CENTER_NAME, ");
		sql.append("       ORG.BRANCH_AREA_ID AS BRANCH_AREA_ID_ORG, ");
		sql.append("       ORG.BRANCH_AREA_NAME, ");
		sql.append("       ORG.BRANCH_NAME, ");
		sql.append("       EMP.EMP_NAME, ");
		sql.append("       TO_CHAR(PABTH_UTIL.FC_getBusiDay(to_date(DATA_DATE, 'YYYYMMDD'), 'TWD', 8), 'YYYYMMDD') END_DATE, ");
		sql.append("       CUST.CON_DEGREE, ");
		sql.append("       FRQ.FRQ_DAY, ");
		sql.append("       to_char(VISIT.CREATETIME, 'YYYYMMDD') AS LAST_VISIT_DATE, ");
//		sql.append("       CASE WHEN A.QTN_TYPE in ('WMS03', 'WMS04') and SATISFACTION_O <= 3 and SATISFACTION_W > 3 then 'Y' else 'N' end as NO_IMPROVE_FLAG, ");
		sql.append("       'N' as NO_IMPROVE_FLAG, "); //臨櫃/開戶客戶不滿意案件，調整為客戶任一不滿意案件皆須填寫滿意度回覆
		sql.append("       CASE WHEN A.CASE_STATUS = 'N' then null else COALESCE(A.CASE_STATUS,'0') end AS CASE_STATUS_FLAG ");
		sql.append("from TBSQM_CSM_IMPROVE_MAST A  ");
		sql.append("left join TBCRM_CUST_MAST CM ON CM.CUST_ID = A.CUST_ID ");
		sql.append("left join TBPMS_ORG_REC_N ORG on ORG.dept_id = A.branch_nbr and to_date(A.TRADE_DATE, 'yyyymmdd') between ORG.START_TIME and ORG.END_TIME ");
		sql.append("left join TBPMS_EMPLOYEE_REC_N EMP on EMP.EMP_id = A.EMP_ID and to_date(A.TRADE_DATE, 'yyyymmdd') between EMP.START_TIME and EMP.END_TIME ");
		sql.append("left join TBPMS_CUST_REC_N CUST ON CUST.CUST_ID = A.CUST_ID and to_date(A.SEND_DATE, 'yyyymmdd') between CUST.START_TIME and CUST.END_TIME ");
		sql.append("left join VWCRM_CUST_MGMT_FRQ_MAP FRQ ON FRQ.CON_DEGREE = CUST.CON_DEGREE AND FRQ.VIP_DEGREE = NVL(CUST.VIP_DEGREE, 'M') ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT a.CUST_ID, max(A.CREATETIME) AS CREATETIME ");
		sql.append("  FROM TBCRM_CUST_VISIT_RECORD a ");
		sql.append("  INNER JOIN TBSQM_CSM_IMPROVE_MAST B ON a.CUST_ID = b.CUST_ID AND a.CREATETIME <= to_date(B.SEND_DATE, 'yyyymmdd')");
		sql.append("  GROUP BY A.CUST_ID  ");
		sql.append(") VISIT ON VISIT.CREATETIME <= to_date(A.SEND_DATE, 'yyyymmdd') AND VISIT.CUST_ID = A.CUST_ID ");
		sql.append("where 1 = 1  ");
		sql.append("and A.DELETE_FLAG is null ");
		sql.append("and A.HO_CHECK = 'Y' ");
		sql.append("and NVL(A.CASE_STATUS, ' ') <> 'N' ");
		sql.append("AND CM.UEMP_ID IS NULL "); // 排除高端客戶

		// 總行人員可以查詢已進行簽核中的案件
		if (headFlag) {
			sql.append("and A.CASE_NO IS NOT NULL  ");
			if (StringUtils.isNotBlank(inputVO.getOwner_emp_id())) {
				sql.append("and OWNER_EMP_ID = :owner_emp_id  ");
				condition.setObject("owner_emp_id", inputVO.getOwner_emp_id());
			}
		} else if (!bmFlag) {
			sql.append("and A.CASE_NO IS NOT NULL AND OWNER_EMP_ID = :ID ");
			condition.setObject("ID", ID);
		} else {
			sql.append("and ((A.CASE_NO is null) or (A.CASE_NO IS NOT NULL and OWNER_EMP_ID = :ID) or (A.CASE_NO IS NOT NULL and OWNER_EMP_ID IS NULL)) ");
			condition.setObject("ID", ID);
		}

		// 資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("and A.TRADE_DATE >= :times ");
			// sql.append("and A.DATA_DATE >= :times ");
			condition.setObject("times", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		}
		if (inputVO.geteCreDate() != null) {
			sql.append("and A.TRADE_DATE <= :timee ");
			// sql.append("and A.DATA_DATE <= :timee ");
			condition.setObject("timee", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.geteCreDate()));
		}

		/*********** 歷史組織查詢條件篩選-START by willis ***********/

		// 若ID等於自己，可以查詢自己所有資訊，不加其他組織查詢條件
		if (ID.equals(inputVO.getEmp_id())) {
			sql.append("and A.EMP_ID = :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id());
		}
		// 分行角色、總行
		else if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && (bmFlag || headFlag)) {
			sql.append("AND ORG.BRANCH_NBR = :BRNCH_NBRR ");
			condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
		}
		// 營運區、總行角色
		else if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && (mbrFlag || headFlag)) {
			sql.append("AND ORG.BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
			condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());

			// 區域主管有選分行需加上區域+分行，避免看到歷史其他區的紀錄
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("AND ORG.BRANCH_NBR = :BRNCH_NBRR ");
				condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
			}
		}
		// 業務處 、總行角色
		else {
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("AND ORG.REGION_CENTER_ID = :REGION_CENTER_IDD ");
				condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());

				// 業務處主管有選分行需加上業務處+分行，避免看到歷史其他業務處分行的紀錄
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append("AND ORG.BRANCH_NBR = :BRNCH_NBRR ");
					condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
				}
				// 業務處主管有選區需加上業務處+區，避免看到歷史其他業務處區的紀錄
				else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
					sql.append("AND ORG.BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
					condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
				}
			}
		}

		/*********** 歷史組織查詢條件篩選-END ***********/

		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append("and A.EMP_ID like :emp_id% ");
			condition.setObject("emp_id%", inputVO.getEmp_id() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("and A.CUST_ID like :cust_id ");
			condition.setObject("cust_id", inputVO.getCust_id() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getQtn_type())) {
			sql.append("and A.QTN_TYPE = :qtn_type ");
			condition.setObject("qtn_type", inputVO.getQtn_type());
		}

		sql.append("order by A.TRADE_DATE, ORG.BRANCH_NBR, A.EMP_ID, A.AO_CODE, A.CUST_ID, A.QTN_TYPE  ");
		
		condition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		int totalPage = list.getTotalPage();
		outputVO.setTotalList(dam.exeQuery(condition));
		outputVO.setTotalPage(totalPage);
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢問題答案資料
	 * 
	 * @throws ParseException
	 **/
	public void getQuestion(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM120EditInputVO inputVO = (SQM120EditInputVO) body;
		SQM120OutputVO outputVO = new SQM120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("select Q.PRIORITY AS QST_ORDER, ");
		sql.append("       Q.DESCRIPTION as QST_DESC, ");
		sql.append("       LISTAGG(CASE WHEN Q.PRIORITY = '1' THEN CASE WHEN OP.DESCRIPTION ='其他' then OP.DESCRIPTION||':'||ANS.USER_INPUT ELSE OP.DESCRIPTION END ");
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS01' AND Q.PRIORITY = '12' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS02' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS03' AND Q.PRIORITY = '18' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
		sql.append("                    WHEN Q.QTN_TYPE = 'WMS04' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");    
		sql.append("                    WHEN OP.DESCRIPTION is null THEN null ");
		sql.append("               ELSE DECODE(OP.OPTION_TYPE, 'S', NVL(TO_CHAR(OP.SCORE), OP.DESCRIPTION), 'M', OP.DESCRIPTION ) END , ';') ");
		sql.append("        WITHIN GROUP(ORDER BY Q.PRIORITY ASC, OP.PRIORITY ASC, Q.DESCRIPTION ASC) AS ANSWER ");
		sql.append("from TBSQM_CSM_QUESTION Q ");
		sql.append("left join TBSQM_CSM_ANSWER ANS on Q.QUESTION_MAPPING_ID = ANS.QUESTION_MAPPING_ID and ANS.PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE) ");
		sql.append("left join TBSQM_CSM_OPTIONS OP ON ANS.ITEM_MAPPING_ID = OP.ITEM_MAPPING_ID ");
		sql.append("where Q.QTN_TYPE = ( ");
		sql.append("  select distinct QTN_TYPE ");
		sql.append("  from TBSQM_CSM_QUESTION ");
		sql.append("  where QUESTION_MAPPING_ID in (select QUESTION_MAPPING_ID from TBSQM_CSM_ANSWER where PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE)) ");
		sql.append(") ");
		sql.append("and NVL(OP.OPTION_TYPE,' ') <> 'O' ");
		sql.append("AND ANS.QUESTION_MAPPING_ID IS NOT NULL ");
		sql.append("GROUP BY Q.PRIORITY, Q.DESCRIPTION ");
		sql.append("order by length(Q.PRIORITY), Q.PRIORITY ");
		
		condition.setObject("CUST_ID", inputVO.getCust_id());
		condition.setObject("DATA_DATE", inputVO.getData_date());
		condition.setObject("QTN_TYPE", inputVO.getQtnType());

		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);

		outputVO.setTotalList(list);
		outputVO.setResultList(list);

		if (StringUtils.isNotBlank(inputVO.getIsFrom()) && StringUtils.equals(inputVO.getIsFrom(), "SQM410")) {
			// 前端抓不到xml資訊, 需另外回傳
			XMLInfo xmlInfo = new XMLInfo();
			List<String> pTypeList;
			
			pTypeList = new ArrayList<String>();
			pTypeList.add("SQM.ANS_TYPE");
			outputVO.setAnsTypeList(xmlInfo.getXMLInfo(pTypeList));
			
			pTypeList = new ArrayList<String>();
			pTypeList.add("SQM.ANS_TYPE_PUSH");
			outputVO.setAnsTypePushList(xmlInfo.getXMLInfo(pTypeList));
		}

		sendRtnObject(outputVO);
	}

	/**
	 * 查詢明細資料
	 * 
	 * @throws ParseException
	 **/
	public void getDtlData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM120EditInputVO inputVO = (SQM120EditInputVO) body;
		SQM120OutputVO outputVO = new SQM120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("select A.*,");
		sql.append("       case when B.cnt > 0 then 'Y' else 'N' end case_flow, ");
		sql.append("       C.emp_id as sup_emp_id_new, ");
		sql.append("       C.emp_name as sup_emp_name_new, ");
		sql.append("       C.job_title_name as sup_cur_job_new ");
		sql.append("from TBSQM_CSM_IMPROVE_DTL A ");
		sql.append("left join (select count(1) cnt, CASE_NO from TBSQM_CSM_FLOW_DTL where CASE_NO = :CASE_NO group by CASE_NO) b ON B.CASE_NO = A.CASE_NO ");
		sql.append("left join tborg_member C on A.sup_emp_id = C.emp_id ");
		sql.append("where A.CASE_NO = :CASE_NO ");
		
		condition.setObject("CASE_NO", inputVO.getCase_no());

		condition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/**
	 * 送出角色選單
	 * 
	 * @throws ParseException
	 **/
	public void getSendRole(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM120EditInputVO inputVO = (SQM120EditInputVO) body;
		SQM120OutputVO outputVO = new SQM120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// String ID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID); // 被代理人id
		// 從M+進來的抓不到CommonVariable 2020-2-17 by Jacky
		if (StringUtils.isNotBlank(inputVO.getIsFrom()) && StringUtils.equals(inputVO.getIsFrom(), "SQM410")) {
			loginID = "";
			loginID = inputVO.getLoginID(); // 後前端傳入相關資訊
		}
		
		String branch_nbr = inputVO.getBranch_nbr();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("SELECT * FROM VWORG_DEFN_INFO WHERE BRANCH_NBR = :branch_nbr ");
		
		queryCondition.setObject("branch_nbr", branch_nbr);
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> defnList = dam.exeQuery(queryCondition);

		String region_center_id = "";
		String branch_area_id = "";

		if (defnList.size() > 0) {
			region_center_id = defnList.get(0).get("REGION_CENTER_ID").toString();
			branch_area_id = defnList.get(0).get("BRANCH_AREA_ID").toString();
			// branch_nbr = defnList.get(0).get("BRANCH_NBR").toString();
		}

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		if (StringUtils.isBlank(roleID)) {
			//從M+過來沒有LOGINROLE
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append(" select ROLE_ID FROM VWORG_EMP_INFO WHERE EMP_ID = :EMP_ID ORDER BY PRIVILEGEID DESC "); //取最大權限
			
			queryCondition.setObject("EMP_ID", loginID);
			
			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> role_list = dam.exeQuery(queryCondition);

			roleID = ObjectUtils.toString(role_list.get(0).get("ROLE_ID"));
		}
		
		boolean headFlag = headmgrMap.containsKey(roleID);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		StringBuffer sql1 = new StringBuffer();
		sql1.append("SELECT JOB_TITLE_NAME, EMP_ID, EMP_NAME, CASE WHEN JOB_TITLE_NAME = '處主管' THEN ROLE_ID || '_1' ELSE ROLE_ID END AS ROLE_ID ");
		sql1.append("FROM VWORG_EMP_INFO WHERE 1 = 1 ");

		StringBuffer sql3 = new StringBuffer();
		sql3.append("SELECT ORG.JOB_TITLE_NAME, MAN.EMP_ID, ORG.EMP_NAME, ORG.ROLE_ID FROM ( ");
		sql3.append("SELECT PARAM_CODE AS EMP_ID FROM TBSYSPARAMETER WHERE PARAM_TYPE ='SQM.HEAD_EMP_ID') MAN ");
		sql3.append("LEFT JOIN VWORG_EMP_INFO ORG ON MAN.EMP_ID = ORG.EMP_ID AND ORG.ROLE_ID = 'B038' ");
		// sql3.append("ORDER BY ROLE_ID ");

		StringBuffer sql2 = new StringBuffer();
		if (roleID.equals("A161") || roleID.equals("A149")) {
			// 若登入者為"個金分行主管"，能選擇選項: 區督導、處副主管、處主管、總行。
			sql2.append("AND (BRANCH_AREA_ID = :branch_area_id AND ROLE_ID IN ('A146', 'A301')) ");
			sql2.append("OR (REGION_CENTER_ID = :region_center_id AND ROLE_ID = 'A164') ");
			sql2.append("UNION ");

			sql = sql1.append(sql2).append(sql3);
			queryCondition.setObject("branch_area_id", branch_area_id);
			queryCondition.setObject("region_center_id", region_center_id);

		} else if (roleID.equals("A146") || roleID.equals("A301")) {
			// 若登入者為"區督導"，能選擇選項: 處副主管、處主管、總行。
			sql2.append("AND (REGION_CENTER_ID = :region_center_id AND ROLE_ID = 'A164') ");
			sql2.append("UNION ");

			sql = sql1.append(sql2).append(sql3);
			queryCondition.setObject("region_center_id", region_center_id);

		} else if (roleID.equals("A164")) {
			// 若登入者為"處副主管"，能選擇選項: 處主管、總行。
			// 若登入者為"處主管"，能選擇選項: 總行。
			if (StringUtils.isNotBlank(inputVO.getIsFrom()) && StringUtils.equals(inputVO.getIsFrom(), "SQM410")) {
				// M+ 進來的交易直接排除處長送總行
				// M+處/副主管簽核完成臨櫃/開戶直接給員編913600
				// M+處/副主管簽核完成臨櫃/開戶直接給員編229881
				sql.append("SELECT ORG.JOB_TITLE_NAME, MAN.EMP_ID, ORG.EMP_NAME, ORG.ROLE_ID FROM ( ");
				sql.append("SELECT PARAM_NAME AS EMP_ID FROM TBSYSPARAMETER WHERE PARAM_TYPE ='SQM.HEAD_EMP_ID_M' ");
				sql.append(" AND PARAM_CODE = DECODE(:qtn_type, 'WMS03', 'EMP_ID_1', 'WMS04', 'EMP_ID_1', 'EMP_ID_2')) MAN ");
				sql.append("LEFT JOIN VWORG_EMP_INFO ORG ON MAN.EMP_ID = ORG.EMP_ID AND ORG.ROLE_ID = 'B038' ");

				queryCondition.setObject("qtn_type", inputVO.getQtnType());
			} else {
				sql2.append("AND (REGION_CENTER_ID = :region_center_id AND ROLE_ID = 'A164') ");
				sql2.append("AND JOB_TITLE_NAME <> '處副主管' AND EMP_ID <> :emp_id ");
				sql2.append("UNION ");

				queryCondition.setObject("region_center_id", region_center_id);
				queryCondition.setObject("emp_id", loginID);

				sql = sql1.append(sql2).append(sql3);
			}
		} else if (headFlag) {
			// 新加邏輯總行可以發送給總行
			sql = sql3.append("WHERE MAN.EMP_ID <> :emp_id ");
			queryCondition.setObject("emp_id", loginID);
		} else {
			// 若登入者為"作業主管"，能選擇選項: 個金分行主管、區督導、處副主管、處主管、總行。
			sql2.append("AND (BRANCH_NBR = :branch_nbr AND ROLE_ID IN ('A161', 'A149')) ");
			sql2.append("OR (BRANCH_AREA_ID = :branch_area_id AND ROLE_ID IN ('A146', 'A301')) ");
			sql2.append("OR (REGION_CENTER_ID = :region_center_id AND ROLE_ID = 'A164') ");
			sql2.append("UNION ");

			sql = sql1.append(sql2).append(sql3);
			queryCondition.setObject("branch_nbr", branch_nbr);
			queryCondition.setObject("branch_area_id", branch_area_id);
			queryCondition.setObject("region_center_id", region_center_id);
		}

		sql.append(") ");
		sql.append("ORDER BY DECODE(ROLE_ID, 'A161', 1, 'A149', 1, 'A146', 2, 'A301', 2, 'A164', 3, 'A164_1', 4, 5) ");

		queryCondition.setQueryString("SELECT * FROM ( " + sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		List<Map<String, Object>> titleList = new ArrayList();
		String title = "";
		String role = "";
		for (Map<String, Object> map : list) {
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("JOB_TITLE_NAME")))) {
				if (!ObjectUtils.toString(map.get("JOB_TITLE_NAME")).equals(title)) {
					Map titleMap = new HashMap();
					title = ObjectUtils.toString(map.get("JOB_TITLE_NAME"));
					role = ObjectUtils.toString(map.get("ROLE_ID"));
					titleMap.put("LABEL", title);
					titleMap.put("DATA", role);
					titleList.add(titleMap);
				}
			}
		}
		
		outputVO.setTotalList(list);
		outputVO.setResultList(titleList);
		
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢員工職級與職務資料
	 * 
	 * @throws ParseException
	 **/
	public void getJob_title_name(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM120EditInputVO inputVO = (SQM120EditInputVO) body;
		SQM120OutputVO outputVO = new SQM120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		// 主查詢 sql 修正 20170120
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		
		sql.append("select EMP_ID, JOB_TITLE_NAME as CUR_JOB, EMP_NAME ");
		sql.append("from TBPMS_EMPLOYEE_REC_N ");
		// sql.append("  where IS_PRIMARY_ROLE ='Y'  and emp_id = :EMP_ID and to_date(:TRADE_DATE,'yyyymmdd') between START_TIME and END_TIME ");
		sql.append("where IS_PRIMARY_ROLE ='Y' ");
		sql.append("and emp_id = :EMP_ID ");
		sql.append("and TRUNC(SYSDATE) between START_TIME and END_TIME ");
		
		condition.setObject("EMP_ID", inputVO.getEmp_id());
		// condition.setObject("TRADE_DATE",inputVO.getTrade_date());
		
		condition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/* ==== 更新 ======== */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM120EditInputVO inputVO = (SQM120EditInputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		// String ID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		// 從M+進來的抓不到CommonVariable 2020-2-17 by Jacky
		String ID = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
		if (StringUtils.isNotBlank(inputVO.getIsFrom()) && StringUtils.equals(inputVO.getIsFrom(), "SQM410")) {
			ID = "";
			ID = inputVO.getLoginID(); // 後前端傳入相關資訊
		}

		checkSaveStatus(inputVO, ID); //檢查是否重覆儲存(桌機已送出, M+則不得再送出)

		try {
			// 是否有案件編號
			if (StringUtils.isBlank(inputVO.getCase_no())) {
				// CASE_NO = 西元年-分行代碼-流水號
				String seq = getCaseNoSeq();
				inputVO.setCase_no(StringUtils.substring(inputVO.getData_date(), 0, 4) + "-" + inputVO.getBranch_nbr() + "-" + seq);
			}
			
			TBSQM_CSM_IMPROVE_DTLVO paramVO = (TBSQM_CSM_IMPROVE_DTLVO) dam.findByPKey(TBSQM_CSM_IMPROVE_DTLVO.TABLE_UID, inputVO.getCase_no());

			if (paramVO != null) {
				paramVO.setEMP_ID(inputVO.getEmp_id());
				paramVO.setEND_DATE(inputVO.getEnd_date());
				paramVO.setBRH_DESC(inputVO.getBrh_desc());
				
				if (StringUtils.isNotBlank(inputVO.getWaiting_time())) {
					BigDecimal waitingTime = new BigDecimal(inputVO.getWaiting_time());
					paramVO.setWAITING_TIME(waitingTime);
				}

				if (StringUtils.isNotBlank(inputVO.getWorking_time())) {
					BigDecimal workingTime = new BigDecimal(inputVO.getWorking_time());
					paramVO.setWORKING_TIME(workingTime);
				}
				
				paramVO.setEMP_NAME(inputVO.getEmp_name());
				paramVO.setCUR_JOB(inputVO.getCur_job());
				
				if (StringUtils.isNotBlank(inputVO.getCur_job_y())) {
					BigDecimal cur_job_y = new BigDecimal(inputVO.getCur_job_y());
					paramVO.setCUR_JOB_Y(cur_job_y);
				}
				
				if (StringUtils.isNotBlank(inputVO.getCur_job_m())) {
					BigDecimal cur_job_m = new BigDecimal(inputVO.getCur_job_m());
					paramVO.setCUR_JOB_M(cur_job_m);
				}
				
				paramVO.setAO_CODE(inputVO.getAo_code());
				paramVO.setSUP_EMP_NAME(inputVO.getSup_emp_name());
				paramVO.setSUP_EMP_ID(inputVO.getSup_emp_id());
				paramVO.setSUP_CUR_JOB(inputVO.getSup_cur_job());
				paramVO.setIMPROVE_DESC(inputVO.getImprove_desc());
				paramVO.setOP_SUP_REMARK(inputVO.getOp_sup_remark());

				// 處副主管意見
				if (StringUtils.isNotBlank(inputVO.getRc_vice_sup_remark())) {
					paramVO.setRC_VICE_SUP_REMARK(inputVO.getRc_vice_sup_remark().trim());
				}

				paramVO.setLAST_VISIT_DATE(inputVO.getLast_visit_date());
				paramVO.setCON_DEGREE(inputVO.getCon_degree());
				if (StringUtils.isNotBlank(inputVO.getFrq_day())) {
					BigDecimal frq_day = new BigDecimal(inputVO.getFrq_day());
					paramVO.setFRQ_DAY(frq_day);
				}

				//2020-4-13 by Jacky 從M+來如果是退件不儲存
				if (StringUtils.equals(inputVO.getIsFrom(), "SQM410")) {
					if (!StringUtils.equals(inputVO.getDeduction_initial(), "RC") && !StringUtils.equals(inputVO.getDeduction_initial(), "RL")) {
						paramVO.setDEDUCTION_INITIAL(inputVO.getDeduction_initial());
					}
				} else {
					paramVO.setDEDUCTION_INITIAL(inputVO.getDeduction_initial());
				}

				// 處主管意見
				if (StringUtils.isNotBlank(inputVO.getRc_sup_remark())) {
					paramVO.setRC_SUP_REMARK(inputVO.getRc_sup_remark().trim());
				}

				paramVO.setHEADMGR_REMARK(inputVO.getHeadmgr_remark());
				dam.update(paramVO);
			} else {
				TBSQM_CSM_IMPROVE_DTLVO insertVO = new TBSQM_CSM_IMPROVE_DTLVO();
				insertVO.setCASE_NO(inputVO.getCase_no());
				insertVO.setEMP_ID(inputVO.getEmp_id());
				insertVO.setEND_DATE(inputVO.getEnd_date());
				insertVO.setBRH_DESC(inputVO.getBrh_desc());

				if (StringUtils.isNotBlank(inputVO.getWaiting_time())) {
					BigDecimal waitingTime = new BigDecimal(inputVO.getWaiting_time());
					insertVO.setWAITING_TIME(waitingTime);
				}

				if (StringUtils.isNotBlank(inputVO.getWorking_time())) {
					BigDecimal workingTime = new BigDecimal(inputVO.getWorking_time());
					insertVO.setWORKING_TIME(workingTime);
				}
				
				insertVO.setEMP_NAME(inputVO.getEmp_name());
				insertVO.setCUR_JOB(inputVO.getCur_job());

				if (StringUtils.isNotBlank(inputVO.getCur_job_y())) {
					BigDecimal cur_job_y = new BigDecimal(inputVO.getCur_job_y());
					insertVO.setCUR_JOB_Y(cur_job_y);
				}
				
				if (StringUtils.isNotBlank(inputVO.getCur_job_m())) {
					BigDecimal cur_job_m = new BigDecimal(inputVO.getCur_job_m());
					insertVO.setCUR_JOB_M(cur_job_m);
				}

				insertVO.setAO_CODE(inputVO.getAo_code());
				insertVO.setSUP_EMP_NAME(inputVO.getSup_emp_name());
				insertVO.setSUP_EMP_ID(inputVO.getSup_emp_id());
				insertVO.setSUP_CUR_JOB(inputVO.getSup_cur_job());
				insertVO.setIMPROVE_DESC(inputVO.getImprove_desc());
				insertVO.setOP_SUP_REMARK(inputVO.getOp_sup_remark());

				// 處副主管意見
				if (StringUtils.isNotBlank(inputVO.getRc_vice_sup_remark())) {
					insertVO.setRC_VICE_SUP_REMARK(inputVO.getRc_vice_sup_remark().trim());
				}
				
				insertVO.setLAST_VISIT_DATE(inputVO.getLast_visit_date());
				insertVO.setCON_DEGREE(inputVO.getCon_degree());

				if (StringUtils.isNotBlank(inputVO.getFrq_day())) {
					BigDecimal frq_day = new BigDecimal(inputVO.getFrq_day());
					insertVO.setFRQ_DAY(frq_day);
				}

				insertVO.setDEDUCTION_INITIAL(inputVO.getDeduction_initial());

				// 處主管意見
				if (StringUtils.isNotBlank(inputVO.getRc_sup_remark())) {
					insertVO.setRC_SUP_REMARK(inputVO.getRc_sup_remark().trim());
				}
				
				insertVO.setHEADMGR_REMARK(inputVO.getHeadmgr_remark());

				insertVO.setCreator(ID);// 將此欄位改為建立人，如果是代理人，一樣紀錄被代理人的員編
				dam.create(insertVO);
			}
			
			BigDecimal seq = new BigDecimal(inputVO.getSeq());

			// 將案件送出
			TBSQM_CSM_IMPROVE_MASTPK mastPk = new TBSQM_CSM_IMPROVE_MASTPK();
			mastPk.setSEQ(seq);
			mastPk.setDATA_DATE(inputVO.getData_date());
			mastPk.setCUST_ID(inputVO.getCust_id());
			mastPk.setQTN_TYPE(inputVO.getQtnType());
			
			TBSQM_CSM_IMPROVE_MASTVO mastVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, mastPk);

			mastVO.setCASE_NO(inputVO.getCase_no());
			
			// 判斷員編需不需要修改
			if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				if (!inputVO.getEmp_id().equals(mastVO.getEMP_ID())) {
					mastVO.setEMP_ID(inputVO.getEmp_id());
				}
			}
			
			// 若待處理人ID為空，將資料做暫存，待處理人為登入者
			if (StringUtils.isBlank(inputVO.getOwner_emp_id()) && "close".equals(inputVO.getSave_type())) {
				mastVO.setOWNER_EMP_ID("close");// 總行結案
				mastVO.setCASE_STATUS("3");// 處理中
			} else if (StringUtils.isBlank(inputVO.getOwner_emp_id()) && "save".equals(inputVO.getSave_type())) {
				//修改分行個金主管從桌機暫存因為沒寫入OWNER_EMP_ID而查不到該筆資料
				if (StringUtils.isBlank(inputVO.getIsFrom()) || !StringUtils.equals(inputVO.getIsFrom(), "SQM410")) {
					mastVO.setOWNER_EMP_ID(ID);//暫存不需要變更owner_emp_id,否則M+上送會有問題
				}
			} else {
				if (StringUtils.isNotBlank(inputVO.getOwner_emp_id())) {
					mastVO.setOWNER_EMP_ID(inputVO.getOwner_emp_id());// 儲存並送出
					mastVO.setCASE_STATUS("3");// 處理中
				}
			}
			
			dam.update(mastVO);

			// 待簽核人有值或結案，送出動作，紀錄LOG
			if (StringUtils.isNotBlank(inputVO.getOwner_emp_id()) || "close".equals(inputVO.getSave_type())) {
				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				
				sql.append("select (COUNT(*)+1) AS SIGNOFF_NUM  ");
				sql.append("from TBSQM_CSM_FLOW_DTL ");
				sql.append("WHERE CASE_NO = :CASE_NO ");
				
				condition.setObject("CASE_NO", inputVO.getCase_no());
				
				condition.setQueryString(sql.toString());
				
				List<Map<String, Object>> NUMLIST = dam.exeQuery(condition);

				TBSQM_CSM_FLOW_DTLPK flowPk = new TBSQM_CSM_FLOW_DTLPK();
				flowPk.setCASE_NO(inputVO.getCase_no());
				flowPk.setSIGNOFF_NUM((NUMLIST.get(0).get("SIGNOFF_NUM").toString()));

				TBSQM_CSM_FLOW_DTLVO flowVO = new TBSQM_CSM_FLOW_DTLVO();
				flowVO.setcomp_id(flowPk);
				flowVO.setSIGNOFF_ID(ID);
				flowVO.setSIGNOFF_NAME(getEmp_Name(ID));
				if (!"close".equals(inputVO.getSave_type())) {
					flowVO.setNEXT_SIGNOFF_ID(inputVO.getOwner_emp_id());
					flowVO.setNEXT_SIGNOFF_NAME(getEmp_Name(inputVO.getOwner_emp_id()));
				}

				flowVO.setCASE_STATUS("3"); // 處理中

				dam.create(flowVO);
			}

			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/*
	 * 檢查是否為本人資料
	 */
	private void checkSaveStatus(SQM120EditInputVO inputVO, String ID) throws JBranchException {
		
		dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		
		BigDecimal seq = new BigDecimal(inputVO.getSeq());

		// 將案件送出
		TBSQM_CSM_IMPROVE_MASTPK mastPk = new TBSQM_CSM_IMPROVE_MASTPK();
		mastPk.setSEQ(seq);
		mastPk.setDATA_DATE(inputVO.getData_date());
		mastPk.setCUST_ID(inputVO.getCust_id());
		mastPk.setQTN_TYPE(inputVO.getQtnType());
		
		TBSQM_CSM_IMPROVE_MASTVO mastVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, mastPk);

		boolean isOwner = true;
		if (StringUtils.equals(inputVO.getAgent_type(), "agent")) {
			//檢查是否為代理人資料
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			
			sql.append("SELECT * FROM TBORG_AGENT WHERE EMP_ID = :empID AND AGENT_ID = :agentID  AND SYSDATE BETWEEN START_DATE AND END_DATE ");
			
			condition.setQueryString(sql.toString());
			
			condition.setObject("empID", mastVO.getOWNER_EMP_ID());
			condition.setObject("agentID", ID);
			
			List list = dam.exeQuery(condition);
			if (list.isEmpty()) {
				isOwner = false;
			}
		} else {
			if (StringUtils.isNotBlank(mastVO.getOWNER_EMP_ID()) && !StringUtils.equals(mastVO.getOWNER_EMP_ID(), ID)) {
				isOwner = false;
			}
		}
		
		// 20210319 MODIFY BY OCEAN => #0535: WMS-CR-20210115-01_擬新增即時滿意度淨推薦值問項 => 佩珊：總行功能(併同問題單0475) ，總行經辦可擇案件歷程角色退件/儲存送出等功能，不受限制…
		if (!StringUtils.isNotBlank(inputVO.getIsFrom()) && StringUtils.equals(inputVO.getIsFrom(), "SQM410")) {
			if (!(headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)))) {
				if (!isOwner)
					throw new APException("此筆資料已經更新, 請重新查詢再進行作業~");
			}
		}
	}

	/*
	 * ====省略改善報告只做確認PASS ======== 開戶、櫃檯問卷除整體滿意度外任一題 不滿意/非常不滿意，可以省略改善報告，只確認。
	 */
	public void removeData(Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM120EditInputVO inputVO = (SQM120EditInputVO) body;
		dam = this.getDataAccessManager();
		
		try {
			BigDecimal seq = new BigDecimal(inputVO.getSeq());

			// 將案件送出
			TBSQM_CSM_IMPROVE_MASTPK mastPk = new TBSQM_CSM_IMPROVE_MASTPK();
			mastPk.setSEQ(seq);
			mastPk.setDATA_DATE(inputVO.getData_date());
			mastPk.setCUST_ID(inputVO.getCust_id());
			mastPk.setQTN_TYPE(inputVO.getQtnType());
			
			TBSQM_CSM_IMPROVE_MASTVO mastVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, mastPk);
			mastVO.setOWNER_EMP_ID("close");
			mastVO.setCASE_STATUS("N");

			dam.update(mastVO);
			sendRtnObject(null);
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
			sql.append("and sysdate BETWEEN START_TIME AND END_TIME ");
			
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

	// 退件動作
	public void returns(Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM120EditInputVO inputVO = (SQM120EditInputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// String ID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String ID = (String) getCommonVariable(SystemVariableConsts.LOGINID); // 被代理人id
		if (StringUtils.isNotBlank(inputVO.getIsFrom()) && StringUtils.equals(inputVO.getIsFrom(), "SQM410")) {
			ID = "";
			ID = inputVO.getLoginID(); // 後前端傳入相關資訊
		}
		
		try {
			TBSQM_CSM_IMPROVE_MASTPK mastPk = new TBSQM_CSM_IMPROVE_MASTPK();
			BigDecimal seq = new BigDecimal(inputVO.getSeq());
			mastPk.setSEQ(seq);
			mastPk.setDATA_DATE(inputVO.getData_date());
			mastPk.setCUST_ID(inputVO.getCust_id());
			mastPk.setQTN_TYPE(inputVO.getQtnType());
			TBSQM_CSM_IMPROVE_MASTVO mastVO = (TBSQM_CSM_IMPROVE_MASTVO) dam.findByPKey(TBSQM_CSM_IMPROVE_MASTVO.TABLE_UID, mastPk);

			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			
			sql.append("with SIGNOFF as (  ");
			sql.append("  select SIGNOFF_ID, CASE_NO  ");
			sql.append("  from TBSQM_CSM_FLOW_DTL  ");
			sql.append("  WHERE CASE_NO = :CASE_NO   ");
			// 判斷是否退件給建立人
			if ("returnsToCreator".equals(inputVO.getReturns_type())) {
				sql.append("  and SIGNOFF_NUM = '1'  ");
			} else {
				sql.append("  and SIGNOFF_NUM = ( ");
				sql.append("    select NVL(max(to_number(SIGNOFF_NUM)), 1) ");
				sql.append("    from TBSQM_CSM_FLOW_DTL D ");
				sql.append("    LEFT JOIN TBSQM_CSM_IMPROVE_MAST M ON D.CASE_NO = M.CASE_NO ");
				sql.append("    WHERE D.CASE_NO = :CASE_NO ");
				sql.append("    and D.CASE_STATUS <> '4' ");
				sql.append("    AND NEXT_SIGNOFF_ID = M.OWNER_EMP_ID ) ");
			}

			sql.append(")  ");
			
			sql.append("select (COUNT(*)+1) AS SIGNOFF_NUM, SIGNOFF.SIGNOFF_ID  ");
			sql.append("from TBSQM_CSM_FLOW_DTL A,SIGNOFF ");
			sql.append("WHERE A.CASE_NO = SIGNOFF.CASE_NO ");
			sql.append("group by SIGNOFF.SIGNOFF_ID ");
			
			condition.setObject("CASE_NO", mastVO.getCASE_NO().toString());
			
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> NUMLIST = dam.exeQuery(condition);

			String singoff_id = (NUMLIST.get(0).get("SIGNOFF_ID").toString());// 建立人
			String singoff_num = (NUMLIST.get(0).get("SIGNOFF_NUM").toString());// 下一流程流水碼
			
			// 退件壓回給建立者
			mastVO.setOWNER_EMP_ID(singoff_id);
			mastVO.setCASE_STATUS("4"); // 退件
			dam.update(mastVO);

			// 紀錄LOG
			TBSQM_CSM_FLOW_DTLPK flowPk = new TBSQM_CSM_FLOW_DTLPK();
			flowPk.setCASE_NO(mastVO.getCASE_NO().toString());
			flowPk.setSIGNOFF_NUM(singoff_num);

			TBSQM_CSM_FLOW_DTLVO flowVO = new TBSQM_CSM_FLOW_DTLVO();
			flowVO.setcomp_id(flowPk);
			flowVO.setSIGNOFF_ID(ID);
			flowVO.setSIGNOFF_NAME(getEmp_Name(ID));
			flowVO.setNEXT_SIGNOFF_ID(singoff_id);
			flowVO.setNEXT_SIGNOFF_NAME(getEmp_Name(singoff_id));
			flowVO.setCASE_STATUS("4"); // 退件
			flowVO.setREMARK(inputVO.getReturns_remark());

			dam.create(flowVO);

			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	// 下載
	public void download(Object body, IPrimitiveMap header) throws Exception {
		
		SQM120InputVO inputVO = (SQM120InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String longinID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> ansMap = xmlInfo.doGetVariable("SQM.ANS_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> qtnList = xmlInfo.doGetVariable("SQM.QTN_TYPE", FormatHelper.FORMAT_3); // 問卷類型

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select D.*, ");
		sql.append("       M.SEQ, ");
		sql.append("       M.DATA_DATE, ");
		sql.append("       M.QTN_TYPE, ");
		sql.append("       M.BRANCH_NBR, ");
		sql.append("       ORG.BRANCH_NAME, ");
		sql.append("       M.TRADE_DATE, ");
		sql.append("       M.CUST_ID, ");
		sql.append("       M.CUST_NAME, ");
		sql.append("       M.RESP_NOTE, ");
		sql.append("       M.QUESTION_DESC, ");
		sql.append("       M.MOBILE_NO, ");
		sql.append("       M.ANSWER, ");
		sql.append("       M.DEDUCTION_FINAL ");
		sql.append("from TBSQM_CSM_IMPROVE_MAST M ");
		sql.append("INNER JOIN TBSQM_CSM_IMPROVE_DTL D ");
		sql.append("ON M.CASE_NO = D.CASE_NO ");
		sql.append("LEFT JOIN  TBPMS_ORG_REC_N ORG ");
		sql.append("ON ORG.dept_id = M.BRANCH_NBR ");
		sql.append("AND to_date(M.TRADE_DATE,'yyyymmdd') between ORG.START_TIME and ORG.END_TIME ");
		sql.append("where D.CASE_NO = :case_no ");
		
		condition.setObject("case_no", inputVO.getCase_no());
		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);

		String url = "";
		String txnCode = "SQM120";
		String reportID = "R1";
		ReportIF report = null;

		try {
			DataFormat dfObj = new DataFormat();
			for (Map<String, Object> dataMap : list) {
				ReportFactory factory = new ReportFactory();
				ReportDataIF data = new ReportData();// 取得傳輸資料給report模組的instance
				ReportGeneratorIF gen = factory.getGenerator(); // 取得產生PDF檔的instance

				// 將值帶入報表欄位中
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

				List<Map<String, String>> allList = new ArrayList<Map<String, String>>();
				List<Map<String, String>> QA_list = new ArrayList<Map<String, String>>();

				// 簡訊問卷
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
					
					sql.append("select ROWNUM as QST_ORDER, QST_DESC, ANSWER ");
					sql.append("from ( ");
					sql.append("  SELECT Q.PRIORITY AS QST_ORDER, ");
					sql.append("         Q.DESCRIPTION as QST_DESC, ");
					sql.append("         LISTAGG(CASE WHEN Q.PRIORITY = '1' THEN CASE WHEN OP.DESCRIPTION = '其他' then OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS01' AND Q.PRIORITY = '11' THEN (SELECT PARAM_NAME || '{{' || OP.SCORE || '}}' FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SQM.ANS_TYPE_PUSH' AND PARAM_CODE = OP.SCORE) ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS01' AND Q.PRIORITY = '12' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS02' AND Q.PRIORITY = '13' THEN (SELECT PARAM_NAME || '{{' || OP.SCORE || '}}' FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SQM.ANS_TYPE_PUSH' AND PARAM_CODE = OP.SCORE) ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS02' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS03' AND Q.PRIORITY = '17' THEN (SELECT PARAM_NAME || '{{' || OP.SCORE || '}}' FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SQM.ANS_TYPE_PUSH' AND PARAM_CODE = OP.SCORE) ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS03' AND Q.PRIORITY = '18' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");                 
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS04' AND Q.PRIORITY = '13' THEN (SELECT PARAM_NAME || '{{' || OP.SCORE || '}}' FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SQM.ANS_TYPE_PUSH' AND PARAM_CODE = OP.SCORE) ");
					sql.append("                      WHEN Q.QTN_TYPE = 'WMS04' AND Q.PRIORITY = '14' THEN CASE WHEN OP.DESCRIPTION = '其他' THEN OP.DESCRIPTION || ':' || ANS.USER_INPUT ELSE OP.DESCRIPTION END ");    
					sql.append("                 ELSE OP.DESCRIPTION END, ';') ");
					sql.append("         WITHIN GROUP(ORDER BY Q.PRIORITY ASC, OP.PRIORITY ASC, Q.DESCRIPTION ASC) AS ANSWER ");
					sql.append("  FROM TBSQM_CSM_ANSWER ANS ");
					sql.append("  LEFT JOIN TBSQM_CSM_OPTIONS OP ON ANS.ITEM_MAPPING_ID = OP.ITEM_MAPPING_ID ");
					sql.append("  LEFT JOIN TBSQM_CSM_QUESTION Q ON ANS.QUESTION_MAPPING_ID = Q.QUESTION_MAPPING_ID ");
					sql.append("  WHERE ANS.PAPER_UUID = (SELECT PAPER_UUID FROM TBSQM_CSM_MAIN WHERE CUST_ID = :CUST_ID AND DATA_DATE = :DATA_DATE AND QTN_TYPE = :QTN_TYPE) ");
					sql.append("  AND OP.OPTION_TYPE <>'O' ");
					sql.append("  AND ANS.QUESTION_MAPPING_ID IS NOT NULL ");
					sql.append("  GROUP BY Q.PRIORITY, Q.DESCRIPTION ");
					sql.append("  ORDER BY Q.PRIORITY  ");
					sql.append(") ");

					condition.setObject("CUST_ID", ObjectUtils.toString(dataMap.get("CUST_ID")));
					condition.setObject("DATA_DATE", ObjectUtils.toString(dataMap.get("DATA_DATE")));
					condition.setObject("QTN_TYPE", ObjectUtils.toString(dataMap.get("QTN_TYPE")));

					condition.setQueryString(sql.toString());
					
					QA_list = dam.exeQuery(condition);

					// 取得題號、題目、答案
					for (Map<String, String> map : QA_list) {
						map.put("QST_DESC", ObjectUtils.toString(map.get("QST_DESC")));
						
						// 將資料裡面{{數字}}濾掉
						int index = map.get("ANSWER").indexOf("{") == -1 ? map.get("ANSWER").length() : map.get("ANSWER").indexOf("{");

						map.put("ANSWER", ObjectUtils.toString(map.get("ANSWER")).substring(0, index));

						allList.add(map);
					}
				}
				
				data.addRecordList("allList", allList);

				report = gen.generateReport(txnCode, reportID, data);

				url = report.getLocation();

				String fileName = "滿意度問卷查詢_" + ObjectUtils.toString(dataMap.get("CASE_NO")) + "_" + longinID + ".pdf";

				notifyClientToDownloadFile(url, fileName);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 查詢處理狀態軌跡明細資料
	 * 
	 * @throws ParseException
	 **/
	public void queryFlowDetail(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM120InputVO inputVO = (SQM120InputVO) body;
		SQM120OutputVO outputVO = new SQM120OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("select SIGNOFF_ID, ");
		sql.append("       case when SIGNOFF_ID <> CREATOR then SIGNOFF_NAME || '-代' else SIGNOFF_NAME end as SIGNOFF_NAME, ");
		sql.append("       NEXT_SIGNOFF_ID, ");
		sql.append("       NEXT_SIGNOFF_NAME, ");
		sql.append("       case when rownum > 1 then '5' else CASE_STATUS end CASE_STATUS, ");
		sql.append("       CREATETIME,");
		sql.append("       REMARK ");
		sql.append("from ( ");
		sql.append("  select * ");
		sql.append("  from TBSQM_CSM_FLOW_DTL ");
		sql.append("  where CASE_NO = :CASE_NO ");
		sql.append("  order by TO_NUMBER(SIGNOFF_NUM) desc ");
		sql.append(") ");
		
		condition.setObject("CASE_NO", inputVO.getCase_no());

		condition.setQueryString(sql.toString());

		outputVO.setTotalList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/**
	 * 取得CASE_NO序號
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String getCaseNoSeq() throws JBranchException {
		
		List<Map<String, Object>> seqList = new ArrayList<Map<String, Object>>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select lpad(TBSQM_CSM_IMPROVE_DTL_SEQ.NEXTVAL, 4, '0') as NEXTVAL from dual ");
		
		qc.setQueryString(sb.toString());
		
		seqList = dam.exeQuery(qc);

		return seqList.get(0).get("NEXTVAL").toString();
	}
}