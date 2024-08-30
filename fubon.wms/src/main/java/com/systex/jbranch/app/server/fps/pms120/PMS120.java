package com.systex.jbranch.app.server.fps.pms120;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_PS_PIPELINEPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_PS_PIPELINEVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Scope("request")
@Component("pms120")
public class PMS120 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;


	public void query(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS120InputVO inputVO =  (PMS120InputVO)body;
		PMS120OutputVO outputVO = new PMS120OutputVO();
		
		String roleType = "";
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String loginRole = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getDataMonth());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		Date report_date = pms000.getMonthLastDate(inputVO.getDataMonth());
		
		String yyyy = (report_date.getYear()+1900)+"";
		String mm = (report_date.getMonth()+1) < 10 ? "0" + (report_date.getMonth()+1) : (report_date.getMonth()+1)+"";
		String dd = report_date.getDate() < 10 ? "0" + report_date.getDate() : report_date.getDate() + "";
		String report_date_str = yyyy+mm+dd;
		
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append(" WITH ACH_GOAL(EMP_ID,ACH_GOAL,CREATETIME) AS ( ");
		sql.append(" SELECT PS_EMP_ID,CREDIT_FND+HB_FND AS ACH_GOAL,CREATETIME FROM TBPMS_CFP_TARGET_SET_PS WHERE YEARMON = :datamonth ");
		sql.append(" ),PIPELINE(EMP_ID,EST_AMT_TOTAL) AS( ");
		sql.append(" SELECT EMP_ID,SUM(EST_AMT) FROM TBPMS_PS_PIPELINE WHERE PLAN_YEARMON = :datamonth GROUP BY EMP_ID ");
		sql.append(" ),MRTG(EMP_ID,ACT_AMT_TOTAL,DATA_DATE) AS ( ");
		sql.append(" SELECT EMP_ID,MRTG_MTD_OUT_AMT,DATA_DATE FROM TBPMS_MRTG_PS_MTD WHERE DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBPMS_MRTG_PS_MTD) ");
		sql.append(" ),CREDIT(EMP_ID,ACT_AMT_TOTAL,DATA_DATE) AS ( ");
		sql.append(" SELECT EMP_ID,CT_MTD_OA,DATA_DATE FROM TBPMS_CREDIT_PS_MTD WHERE DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBPMS_CREDIT_PS_MTD) ");
		sql.append(" ) ");
		sql.append(" SELECT V_STRING1 as BRANCH_NBR,V_STRING2 as BRANCH_NAME,V_STRING3 as EMP_ID,V_STRING4 as EMP_NAME ");
		sql.append(" ,ACH_GOAL,EST_AMT_TOTAL,COALESCE(MRTG.ACT_AMT_TOTAL,0)+COALESCE(CREDIT.ACT_AMT_TOTAL,0) AS ACT_AMT_TOTAL ");
		sql.append(" ,MRTG.DATA_DATE,ACH_GOAL.CREATETIME AS CREATETIME");
		sql.append(" from table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','4')) EMP ");
		sql.append(" LEFT JOIN ACH_GOAL ON EMP.V_STRING3 = ACH_GOAL.EMP_ID ");
		sql.append(" LEFT JOIN PIPELINE ON EMP.V_STRING3 = PIPELINE.EMP_ID ");
		sql.append(" LEFT JOIN MRTG ON EMP.V_STRING3 = MRTG.EMP_ID ");
		sql.append(" LEFT JOIN CREDIT ON EMP.V_STRING3 = CREDIT.EMP_ID ");
		sql.append(" WHERE 1=1  ");
		condition.setObject("datamonth", inputVO.getDataMonth());
		condition.setObject("report_date", report_date_str);
		condition.setObject("loginID", loginID);
		condition.setObject("roleID", loginRole);
		// AO_COCE
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append(" and EMP.V_STRING3 = :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id());
		}
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append(" and EMP.V_STRING1 = :branch_nbr ");
			condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		// 營運區
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
			sql.append(" and EMP.V_STRING1 IN (SELECT V_STRING3 FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','3')) WHERE V_STRING1 = :branch_area_id) ");
			condition.setObject("branch_area_id", inputVO.getBranch_area_id());
		}
		sql.append(" ORDER BY EMP.V_STRING3 ");		
		condition.setQueryString(sql.toString());
		
		outputVO.setDataList(dam.exeQuery(condition));
		
		
		sql = new StringBuffer();
		sql.append("WITH PIPELINE_CUST(EST_MEETING,ACT_MEETING,ESC_INCOM,ACT_INCOM,EST_DEBIT,ACT_DEBIT) AS ");
		sql.append("( ");
		sql.append("SELECT ");
		sql.append(" CASE WHEN SUBSTR(EST_MEETING_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS EST_MEETING ");
		sql.append(",CASE WHEN SUBSTR(ACT_MEETING_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS ACT_MEETING ");
		sql.append(",CASE WHEN SUBSTR(EST_INCOM_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS ESC_INCOM ");
		sql.append(",CASE WHEN SUBSTR(ACT_INCOM_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS ACT_INCOM ");
		sql.append(",CASE WHEN SUBSTR(EX_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS EST_DEBIT ");
		sql.append(",CASE WHEN SUBSTR(ACTUAL_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS ACT_DEBIT ");
		sql.append("FROM TBPMS_PS_PIPELINE a WHERE EST_PRD = '1' and exists( ");
		sql.append("SELECT 'Y' FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','4')) EMP ");
		sql.append(" WHERE a.BRANCH_NBR = EMP.V_STRING1 AND A.EMP_ID = EMP.V_STRING3 ");
		condition.setObject("datamonth", inputVO.getDataMonth());
				// AO_COCE
				if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
					sql.append(" and EMP.V_STRING3 = :emp_id ");
					condition.setObject("emp_id", inputVO.getEmp_id());
				}
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and EMP.V_STRING1 = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}

				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
					sql.append(" and EMP.V_STRING1 IN (SELECT V_STRING3 FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','3')) WHERE V_STRING1 = :branch_area_id) ");
					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
				}
		sql.append(" ) ");
		sql.append(") ");
		sql.append(" SELECT COUNT(DISTINCT EST_MEETING) AS EST_MEETING,COUNT(DISTINCT ACT_MEETING) AS ACT_MEETING ");
		sql.append(",COUNT(DISTINCT ESC_INCOM) AS ESC_INCOM,COUNT(DISTINCT ACT_INCOM) AS ACT_INCOM ");
		sql.append(",COUNT(DISTINCT EST_DEBIT) AS EST_DEBIT,COUNT(DISTINCT ACT_DEBIT) AS ACT_DEBIT ");
		sql.append(" FROM PIPELINE_CUST");
		
		condition.setQueryString(sql.toString());
		
		outputVO.setDataList2(dam.exeQuery(condition));		
		
		sql = new StringBuffer();
		sql.append("WITH ITEM(ITEM_CODE,ITEM_NAME) AS ");
		sql.append("( ");
		sql.append("SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS_EST_PROD' ");
		sql.append("),PIPELINE(EST_PRD,EST_AMT,APPLY_AMT,NEXT_MON_APPLY_AMT,CURR_MON_APPLY_AMT,CURR_MON_DEBIT_AMT) AS ");
		sql.append("( ");
		sql.append("SELECT EST_PRD,SUM(EST_AMT),SUM(CASE WHEN CASE_NUM IS NOT NULL AND EX_DATE IS NULL THEN APPLY_AMT ELSE 0 END) AS APPLY_AMT ");
		sql.append(",SUM(CASE WHEN CASE_NUM IS NOT NULL AND EX_DATE > PLAN_YEARMON THEN APPLY_AMT ELSE 0 END) AS NEXT_MON_APPLY_AMT ");
		sql.append(",SUM(CASE WHEN CASE_NUM IS NOT NULL AND EX_DATE = PLAN_YEARMON THEN APPLY_AMT ELSE 0 END) AS CURR_MON_APPLY_AMT ");
		sql.append(",SUM(CASE WHEN CASE_NUM IS NOT NULL AND ACTUAL_DATE IS NOT NULL AND SUBSTRB(ACTUAL_DATE,1,6) = PLAN_YEARMON THEN DEBT_AMT ELSE 0 END) AS CURR_MON_DEBIT_AMT ");
		sql.append(" FROM TBPMS_PS_PIPELINE a WHERE PLAN_YEARMON = :datamonth and exists( ");
		sql.append("SELECT 'Y' FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','4')) EMP ");
		sql.append("WHERE a.BRANCH_NBR = EMP.V_STRING1 AND A.EMP_ID = EMP.V_STRING3 ");
				// AO_COCE
				if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
					sql.append(" and EMP.V_STRING3 = :emp_id ");
					condition.setObject("emp_id", inputVO.getEmp_id());
				}
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and EMP.V_STRING1 = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}
		
				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
					sql.append(" and EMP.V_STRING1 IN (SELECT V_STRING3 FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','3')) WHERE V_STRING1 = :branch_area_id) ");
					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
				}
		sql.append(" ) ");
		sql.append(" GROUP BY EST_PRD ");
		sql.append(") ");
		sql.append("SELECT * FROM ITEM LEFT JOIN PIPELINE ON ITEM.ITEM_CODE = PIPELINE.EST_PRD ");
		sql.append(" order by ITEM_CODE ");
		condition.setQueryString(sql.toString());		
		outputVO.setDataList3(dam.exeQuery(condition));
		
		sql = new StringBuffer();
		sql.append("WITH PIPELINE_CUST AS ( ");
		sql.append("  SELECT ");
		sql.append("    CASE WHEN SUBSTR(a.EST_MEETING_DATE,1,6) = :datamonth THEN a.EST_AMT ELSE 0 END AS EST_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.ACT_MEETING_DATE,1,6) = :datamonth THEN a.APPLY_AMT ELSE 0 END AS APPLY_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.EST_INCOM_DATE,1,6) = :datamonth THEN a.EST_AMT ELSE 0 END AS DEBT_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.ACT_INCOM_DATE,1,6) = :datamonth THEN a.APPLY_AMT ELSE 0 END AS AAPPLY_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.EX_DATE,1,6) = :datamonth THEN a.EST_AMT ELSE 0 END AS APRV_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.ACTUAL_DATE,1,6) = :datamonth THEN a.DEBT_AMT ELSE 0 END AS DRAW_AMT ");
		sql.append("  FROM TBPMS_PS_PIPELINE a ");
		sql.append("  WHERE a.EST_PRD = '1' and exists ( ");
		sql.append("      SELECT 'Y' "); 
		sql.append("      FROM table ( ");
		sql.append("        FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','4') ");
		sql.append("      ) EMP ");
		sql.append("      WHERE a.BRANCH_NBR = EMP.V_STRING1 "); 
		sql.append("        AND A.EMP_ID = EMP.V_STRING3 ");  
				// AO_COCE
				if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
					sql.append(" and EMP.V_STRING3 = :emp_id ");
					condition.setObject("emp_id", inputVO.getEmp_id());
				}
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and EMP.V_STRING1 = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}

				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
					sql.append(" and EMP.V_STRING1 IN (SELECT V_STRING3 FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','3')) WHERE V_STRING1 = :branch_area_id) ");
					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
				}
		sql.append("    ) ");
		sql.append("  UNION ALL SELECT 0,0,0,0,0,0 FROM DUAL ");
		sql.append(") ");
		sql.append("SELECT ");
		sql.append("  SUM(EST_AMT) AS EST_AMT, ");
		sql.append("  SUM(APPLY_AMT) AS APPLY_AMT, ");
		sql.append("  SUM(DEBT_AMT) AS DEBT_AMT, ");
		sql.append("  SUM(AAPPLY_AMT) AS AAPPLY_AMT, ");
		sql.append("  SUM(APRV_AMT) AS APRV_AMT, ");
		sql.append("  SUM(DRAW_AMT) AS DRAW_AMT ");
		sql.append("FROM PIPELINE_CUST ");
		
		condition.setQueryString(sql.toString());
		
		outputVO.setDataList4(dam.exeQuery(condition));
		
		sql = new StringBuffer();
		sql.append("WITH PIPELINE_CUST(EST_MEETING,ACT_MEETING,ESC_INCOM,ACT_INCOM,EST_DEBIT,ACT_DEBIT) AS ");
		sql.append("( ");
		sql.append("SELECT ");
		sql.append(" CASE WHEN SUBSTR(EST_MEETING_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS EST_MEETING ");
		sql.append(",CASE WHEN SUBSTR(ACT_MEETING_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS ACT_MEETING ");
		sql.append(",CASE WHEN SUBSTR(EST_INCOM_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS ESC_INCOM ");
		sql.append(",CASE WHEN SUBSTR(ACT_INCOM_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS ACT_INCOM ");
		sql.append(",CASE WHEN SUBSTR(EX_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS EST_DEBIT ");
		sql.append(",CASE WHEN SUBSTR(ACTUAL_DATE,1,6) = :datamonth AND SUBSTR(a.EX_DATE,1,6) = :datamonth THEN CASE_NUM ELSE NULL END AS ACT_DEBIT ");
		sql.append("FROM TBPMS_PS_PIPELINE a WHERE EST_PRD = '2' and exists( ");
		sql.append("SELECT 'Y' FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','4')) EMP ");
		sql.append(" WHERE a.BRANCH_NBR = EMP.V_STRING1 AND A.EMP_ID = EMP.V_STRING3 ");
		condition.setObject("datamonth", inputVO.getDataMonth());
				// AO_COCE
				if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
					sql.append(" and EMP.V_STRING3 = :emp_id ");
					condition.setObject("emp_id", inputVO.getEmp_id());
				}
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and EMP.V_STRING1 = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}

				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
					sql.append(" and EMP.V_STRING1 IN (SELECT V_STRING3 FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','3')) WHERE V_STRING1 = :branch_area_id) ");
					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
				}
		sql.append(" ) ");
		sql.append(") ");
		sql.append(" SELECT COUNT(DISTINCT EST_MEETING) AS EST_MEETING,COUNT(DISTINCT ACT_MEETING) AS ACT_MEETING ");
		sql.append(",COUNT(DISTINCT ESC_INCOM) AS ESC_INCOM,COUNT(DISTINCT ACT_INCOM) AS ACT_INCOM ");
		sql.append(",COUNT(DISTINCT EST_DEBIT) AS EST_DEBIT,COUNT(DISTINCT ACT_DEBIT) AS ACT_DEBIT ");
		sql.append(" FROM PIPELINE_CUST");
		
		condition.setQueryString(sql.toString());
		
		outputVO.setDataList5(dam.exeQuery(condition));
		
		sql = new StringBuffer();
		sql.append("WITH PIPELINE_CUST AS ( ");
		sql.append("  SELECT ");
		sql.append("    CASE WHEN SUBSTR(a.EST_MEETING_DATE,1,6) = :datamonth THEN a.EST_AMT ELSE 0 END AS EST_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.ACT_MEETING_DATE,1,6) = :datamonth THEN a.APPLY_AMT ELSE 0 END AS APPLY_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.EST_INCOM_DATE,1,6) = :datamonth THEN a.EST_AMT ELSE 0 END AS DEBT_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.ACT_INCOM_DATE,1,6) = :datamonth THEN a.APPLY_AMT ELSE 0 END AS AAPPLY_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.EX_DATE,1,6) = :datamonth THEN a.EST_AMT ELSE 0 END AS APRV_AMT, ");
		sql.append("    CASE WHEN SUBSTR(a.ACTUAL_DATE,1,6) = :datamonth THEN a.DEBT_AMT ELSE 0 END AS DRAW_AMT ");
		sql.append("  FROM TBPMS_PS_PIPELINE a ");
		sql.append("  WHERE a.EST_PRD = '2' and exists ( ");
		sql.append("      SELECT 'Y' "); 
		sql.append("      FROM table ( ");
		sql.append("        FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','4') ");
		sql.append("      ) EMP ");
		sql.append("      WHERE a.BRANCH_NBR = EMP.V_STRING1 "); 
		sql.append("        AND A.EMP_ID = EMP.V_STRING3 ");  
				// AO_COCE
				if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
					sql.append(" and EMP.V_STRING3 = :emp_id ");
					condition.setObject("emp_id", inputVO.getEmp_id());
				}
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and EMP.V_STRING1 = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}

				// 營運區
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
					sql.append(" and EMP.V_STRING1 IN (SELECT V_STRING3 FROM table(FN_GET_VRR_D('', to_date(:report_date,'YYYYMMDD'), :loginID, :roleID, '','Y','3')) WHERE V_STRING1 = :branch_area_id) ");
					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
				}
		sql.append("    ) ");
		sql.append("  UNION ALL SELECT 0,0,0,0,0,0 FROM DUAL ");
		sql.append(") ");  
		sql.append("SELECT "); 
		sql.append("  SUM(EST_AMT) AS EST_AMT, ");
		sql.append("  SUM(APPLY_AMT) AS APPLY_AMT, ");
		sql.append("  SUM(DEBT_AMT) AS DEBT_AMT, ");
		sql.append("  SUM(AAPPLY_AMT) AS AAPPLY_AMT, ");
		sql.append("  SUM(APRV_AMT) AS APRV_AMT, ");
		sql.append("  SUM(DRAW_AMT) AS DRAW_AMT ");
		sql.append("FROM PIPELINE_CUST ");
		
		condition.setQueryString(sql.toString());
		
		outputVO.setDataList6(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}
}
