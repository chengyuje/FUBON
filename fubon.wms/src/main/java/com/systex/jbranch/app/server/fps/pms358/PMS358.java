package com.systex.jbranch.app.server.fps.pms358;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms348.PMS348InputVO;
import com.systex.jbranch.app.server.fps.pms348.PMS348OutputVO;
import com.systex.jbranch.app.server.fps.pms359.PMS359OutputVO;
import com.systex.jbranch.app.server.fps.sot814.SOT814;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :台幣定存增減報表<br>
 * Comments Name : PMS358java<br>
 * Author : Frank<br>
 * Date :2016/07/15 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms358")
@Scope("request")
public class PMS358 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS358.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public void date_query (Object body, IPrimitiveMap header)
			throws JBranchException{
		PMS358OutputVO outputVO = new PMS358OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			StringBuilder sql=new StringBuilder();
			sql.append("SELECT distinct DATA_DATE from TBPMS_NTD_CD_DATE order by DATA_DATE DESC");
			condition.setQueryString(sql.toString());
			List<Map<String,Object>> list=dam.exeQuery(condition);
			outputVO.setResultList(list);;
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		} 
	
	
	}
	
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS358InputVO inputVO = (PMS358InputVO) body;
		PMS358OutputVO outputVO = new PMS358OutputVO();

		String roleType = "";
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> fchMap = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2);         //FCH理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
			
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
//		outputVO.setResultDlist(this.queryCDate(body,header));
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);


//		StringBuffer sql = new StringBuffer("SELECT ROWNUM, T.* FROM ( ");
		StringBuffer sql = new StringBuffer();
		//20170520   週報/月報
		//TBPMS_NTD_CD      //周報
		//TBPMS_NTD_CD_MON  //月報
		String dataBase=(inputVO.getReportType().equals("week"))?"TBPMS_NTD_CD":"TBPMS_NTD_CD_MON";
		////20170520   週報/月報
		//WEEK_BAL      //周統計
		//MON_BAL  //月統計
		String dataBase_co=(inputVO.getReportType().equals("week"))?"CD.WEEK_BAL":"CD.MON_BAL";
		//總行人員、區域中心主管、營運督導統計至分行
		if(headmgrMap.containsKey(roleID) || armgrMap.containsKey(roleID) || mbrmgrMap.containsKey(roleID)){
			sql.append(" SELECT ");
			sql.append(" ROW_NUMBER() OVER (ORDER BY CD.region_center_id, CD.branch_area_id, CD.branch_nbr DESC) RN, ");
			//週報要GROUP BY DATA_DATE 月報不用
			if(inputVO.getReportType().equals("week")){
				sql.append("  CD.DATA_DATE, ");
				sql.append("  'week' AS RTYPE, ");
			
			}else{
				sql.append("  'month' AS RTYPE, ");
			}
			
			sql.append(" CD.REGION_CENTER_ID, CD.REGION_CENTER_NAME, CD.BRANCH_AREA_ID, ");
			sql.append(" CD.BRANCH_AREA_NAME, CD.BRANCH_NBR, CD.BRANCH_NAME, ");
//			sql.append(" CASE WHEN (AO_CODE = '000' OR AO_CODE IS NULL OR AO_CODE IN (SELECT AO_CODE FROM TBPMS_SALES_AOCODE_REC WHERE TYPE = '3')) THEN 'MASS戶' ELSE '理財戶' END as CUST_TYPE, ");
			sql.append(" CASE WHEN NVL(CD.ao_code,'000') <> '000' AND AO.ao_code IS NULL THEN '理財戶'  ELSE 'MASS戶' END   AS CUST_TYPE , ");
			sql.append(" SUM("+dataBase_co+") as WEEK_BAL, ");
			sql.append(" SUM(CD.LMON_BAL) as LMON_BAL, ");
			sql.append(" SUM(CD.CD_DIFF) as CD_DIFF,TRUNC(CD.CREATETIME) AS CREATDATE ");
			sql.append(" FROM "+dataBase +"  CD ");
			
			// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG ");
			sql.append(" ON ORG.BRANCH_NBR=CD.BRANCH_NBR ");
			
			sql.append(" LEFT JOIN  tbpms_sales_aocode_rec AO ");
			if(inputVO.getReportType().equals("week")){
				sql.append(" ON CD.ao_code = AO.ao_code AND AO.TYPE = '3' AND TO_DATE(CD.DATA_DATE,'YYYYMMDD') BETWEEN AO.START_TIME AND AO.END_TIME  ");
			
			}else{
				sql.append(" ON CD.ao_code = AO.ao_code AND AO.TYPE = '3' AND TO_DATE(CD.yearmon || '01','YYYYMMDD') BETWEEN AO.START_TIME AND AO.END_TIME ");
			}	
			
			sql.append(" WHERE 1=1  AND CD.BRANCH_NBR BETWEEN '200' AND '900' ");
			sql.append(" AND TO_NUMBER(CD.BRANCH_NBR) <> 806 AND TO_NUMBER(CD.BRANCH_NBR) <> 810 ");
			if(inputVO.getReportType().equals("week")){
				sql.append(" and  ( CD.week_bal > 0 OR CD.lmon_bal > 0 ) ");
//				sql.append(" AND CASE WHEN CD.week_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN CD.lmon_bal = 0 THEN 1 ELSE 3 END ");
			
			}else{
//				sql.append(" and  ( CD.MON_BAL > 0 or CD.LMON_BAL > 0 ) ");
				sql.append(" AND CASE WHEN CD.mon_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN CD.lmon_bal = 0 THEN 1 ELSE 3 END ");
			}	
			outputVO.setRoleType("RC");
		}
		//分行個金主管統計至理專
		if(bmmgrMap.containsKey(roleID)){
			sql.append("SELECT ");
			sql.append(" ROW_NUMBER() OVER (ORDER BY CD.region_center_id, CD.branch_area_id, CD.branch_nbr DESC) RN, ");
			//週報要GROUP BY DATA_DATE 月報不用
			if(inputVO.getReportType().equals("week")){
				sql.append("  CD.DATA_DATE, ");
				sql.append("  'week' AS RTYPE, ");
			}else{
				sql.append("  'month' AS RTYPE, ");
			}
			
			sql.append(" CD.REGION_CENTER_ID, CD.REGION_CENTER_NAME, CD.BRANCH_AREA_ID, ");
			sql.append(" CD.BRANCH_AREA_NAME, CD.BRANCH_NBR,CD.BRANCH_NAME,CD.AO_CODE,CD.EMP_NAME ");
//			sql.append(" ,CASE WHEN (AO_CODE = '000' OR AO_CODE IS NULL OR AO_CODE IN (SELECT AO_CODE FROM TBPMS_SALES_AOCODE_REC WHERE TYPE = '3')) THEN 'MASS戶' ELSE '理財戶' END as CUST_TYPE, ");
			sql.append(" ,CASE WHEN NVL(CD.ao_code,'000') <> '000' AND AO.ao_code IS NULL THEN '理財戶'  ELSE 'MASS戶' END   AS CUST_TYPE , ");
			sql.append(" SUM("+dataBase_co+") as WEEK_BAL, ");
			sql.append(" SUM(CD.LMON_BAL) as LMON_BAL, ");
			sql.append(" SUM(CD.CD_DIFF) as CD_DIFF ,TRUNC(CD.CREATETIME) AS CREATDATE");
			sql.append(" FROM "+dataBase +"  CD ");
			
			// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG ");
			sql.append(" ON ORG.BRANCH_NBR=CD.BRANCH_NBR ");
			
			sql.append(" LEFT JOIN  tbpms_sales_aocode_rec AO ");
			if(inputVO.getReportType().equals("week")){
				sql.append(" ON CD.ao_code = AO.ao_code AND AO.TYPE = '3' AND TO_DATE(CD.DATA_DATE,'YYYYMMDD') BETWEEN AO.START_TIME AND AO.END_TIME  ");
			
			}else{
				sql.append(" ON CD.ao_code = AO.ao_code AND AO.TYPE = '3' AND TO_DATE(CD.yearmon || '01','YYYYMMDD') BETWEEN AO.START_TIME AND AO.END_TIME ");
			}	
			
			sql.append(" WHERE 1=1  AND CD.BRANCH_NBR BETWEEN '200' AND '900' ");
			sql.append(" AND TO_NUMBER(CD.BRANCH_NBR) <> 806 AND TO_NUMBER(CD.BRANCH_NBR) <> 810 ");
			if(inputVO.getReportType().equals("week")){
				sql.append(" and  ( CD.week_bal > 0 OR CD.lmon_bal > 0 )  ");
//				sql.append(" AND CASE WHEN CD.week_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN CD.lmon_bal = 0 THEN 1 ELSE 3 END ");
			
			}else{
//				sql.append(" and  (CD.MON_BAL > 0 or CD.LMON_BAL > 0 ) ");
				sql.append(" AND CASE WHEN CD.mon_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN CD.lmon_bal = 0 THEN 1 ELSE 3 END ");
			}	
			outputVO.setRoleType("BR");
		}
		//分行理專/OP 直接顯示客戶明細
		if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID) || fchMap.containsKey(roleID)){
			sql.append("SELECT ");
			sql.append(" ROW_NUMBER() OVER (ORDER BY CD.region_center_id, CD.branch_area_id, CD.branch_nbr DESC) RN, ");
			//週報要GROUP BY DATA_DATE 月報不用
			if(inputVO.getReportType().equals("week")){
				sql.append("  CD.DATA_DATE, ");
				sql.append("  'week' AS RTYPE, ");
			}else{
				sql.append("  'month' AS RTYPE, ");
			}
			
			sql.append(" CD.REGION_CENTER_ID,CD.REGION_CENTER_NAME,CD.BRANCH_AREA_ID,CD.BRANCH_AREA_NAME,CD.BRANCH_NBR, ");
			sql.append(" CD.BRANCH_NAME,CD.AO_CODE,CD.EMP_ID,CD.EMP_NAME,CD.CUST_ID,CD.CUST_NAME,"+dataBase_co+" as WEEK_BAL,CD.LMON_BAL,CD.CD_DIFF ");
//			sql.append(" ,CASE WHEN (AO_CODE = '000' OR AO_CODE IS NULL OR AO_CODE IN (SELECT AO_CODE FROM TBPMS_SALES_AOCODE_REC WHERE TYPE = '3')) THEN 'MASS戶' ELSE '理財戶' END as CUST_TYPE ");
			sql.append(" ,CASE WHEN NVL(CD.ao_code,'000') <> '000' AND AO.ao_code IS NULL THEN '理財戶'  ELSE 'MASS戶' END   AS CUST_TYPE   ,TRUNC(CD.CREATETIME) AS CREATDATE");
			sql.append(" FROM "+dataBase +"  CD ");
			
			// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG ");
			sql.append(" ON ORG.BRANCH_NBR=CD.BRANCH_NBR ");
			
			sql.append(" LEFT JOIN  tbpms_sales_aocode_rec AO ");
			if(inputVO.getReportType().equals("week")){
				sql.append(" ON CD.ao_code = AO.ao_code AND AO.TYPE = '3' AND TO_DATE(CD.DATA_DATE,'YYYYMMDD') BETWEEN AO.START_TIME AND AO.END_TIME  ");
			
			}else{
				sql.append(" ON CD.ao_code = AO.ao_code AND AO.TYPE = '3' AND TO_DATE(CD.yearmon || '01','YYYYMMDD') BETWEEN AO.START_TIME AND AO.END_TIME ");
			}	
			
			sql.append(" WHERE 1=1  AND CD.BRANCH_NBR BETWEEN '200' AND '900' ");	
			sql.append(" AND TO_NUMBER(CD.BRANCH_NBR) <> 806 AND TO_NUMBER(CD.BRANCH_NBR) <> 810 ");
			if(inputVO.getReportType().equals("week")){
				sql.append(" and  ( CD.week_bal > 0 OR CD.lmon_bal > 0 )  ");
//				sql.append(" AND CASE WHEN CD.week_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN CD.lmon_bal = 0 THEN 1 ELSE 3 END ");
			
			}else{
//				sql.append(" and  (CD.MON_BAL > 0 or CD.LMON_BAL > 0 ) ");
				sql.append(" AND CASE WHEN CD.mon_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN CD.lmon_bal = 0 THEN 1 ELSE 3 END ");
			}	
			outputVO.setRoleType("AO");
		}
				
		// 資料日期
		if (StringUtils.isNotBlank(inputVO.getReportDate())) {
			//週報
			if(inputVO.getReportType().equals("week")){
				sql.append("and CD.DATA_DATE = :data_date ");
				condition.setObject("data_date", inputVO.getReportDate());
			}
			//月報
			if(inputVO.getReportType().equals("month")){
				sql.append("and CD.YEARMON = :data_date ");
				condition.setObject("data_date", inputVO.getReportDate());
			}
		}
		
		// AO_COCE
		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			sql.append(" and CD.AO_CODE = :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());
		}else{
			//登入為銷售人員強制加AO_CODE
			if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID) || fchMap.containsKey(roleID)) {
				sql.append(" and CD.AO_CODE IN (:ao_code) ");
				condition.setObject("ao_code", pms000outputVO.getV_aoList());
			}
		}
				
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append(" and ORG.BRANCH_NBR = :branch_nbr ");
			condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}else{
			//登入非總行人員強制加分行
			if(!headmgrMap.containsKey(roleID)) {		
				sql.append("  and ORG.BRANCH_NBR IN (:branch_nbr) ");
				condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
			}
		}

		// 營運區
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
			//sql.append(" and ORG.BRANCH_AREA_ID = :branch_area_id ");
			sql.append("  and ORG.BRANCH_NBR IN ( ");
			sql.append("    SELECT BRANCH_NBR ");
			sql.append("    FROM VWORG_DEFN_BRH ");
			sql.append("    WHERE DEPT_ID = :branch_area_id ");
			sql.append("  ) ");
			condition.setObject("branch_area_id", inputVO.getBranch_area_id());
		}else{
			//登入非總行人員強制加營運區
			if(!headmgrMap.containsKey(roleID)) {
				//sql.append("  and ORG.BRANCH_AREA_ID IN (:branch_area_id) ");
				sql.append("  and ORG.BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID IN (:branch_area_id) ");
				sql.append("  ) ");
				condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
			}
		}
				
		// 區域中心
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"".equals(inputVO.getRegion_center_id())) {
			//sql.append(" and ORG.REGION_CENTER_ID = :region_center_id ");
			sql.append("  and ORG.BRANCH_NBR IN ( ");
			sql.append("    SELECT BRANCH_NBR ");
			sql.append("    FROM VWORG_DEFN_BRH ");
			sql.append("    WHERE DEPT_ID = :region_center_id ");
			sql.append("  ) ");
			condition.setObject("region_center_id", inputVO.getRegion_center_id());
		}else{
			//登入非總行人員強制加區域中心
			if(!headmgrMap.containsKey(roleID)) {
				//sql.append("and ORG.REGION_CENTER_ID IN (:region_center_id) ");
				sql.append("  and ORG.BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID IN (:region_center_id) ");
				sql.append("  ) ");
				condition.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}
		
		//總行人員、區域中心主管、營運督導統計至分行
		if(headmgrMap.containsKey(roleID) || armgrMap.containsKey(roleID) || mbrmgrMap.containsKey(roleID)){		
			sql.append(" GROUP BY ");
			
			//週報要GROUP BY DATA_DATE 月報不用
			if(inputVO.getReportType().equals("week")){
				sql.append("  CD.DATA_DATE, ");
			}
			
			sql.append(" CD.REGION_CENTER_ID, ");
			sql.append(" CD.REGION_CENTER_NAME, CD.BRANCH_AREA_ID, ");
			sql.append(" CD.BRANCH_AREA_NAME, CD.BRANCH_NBR,CD.BRANCH_NAME  ");
//			sql.append(" ,CASE WHEN (AO_CODE = '000' OR AO_CODE IS NULL OR AO_CODE IN (SELECT AO_CODE FROM TBPMS_SALES_AOCODE_REC WHERE TYPE = '3')) THEN 'MASS戶' ELSE '理財戶' END ");
			sql.append(" ,CASE WHEN NVL(CD.ao_code,'000') <> '000' AND AO.ao_code IS NULL THEN '理財戶'  ELSE 'MASS戶' END,TRUNC(CD.CREATETIME) ");
		}
		//分行個金主管統計至理專
		if(bmmgrMap.containsKey(roleID)){
			sql.append(" GROUP BY ");
			
			//週報要GROUP BY DATA_DATE 月報不用
			if(inputVO.getReportType().equals("week")){
				sql.append("  CD.DATA_DATE, ");
			}
			
			sql.append(" CD.REGION_CENTER_ID, ");
			sql.append(" CD.REGION_CENTER_NAME, CD.BRANCH_AREA_ID, ");
			sql.append(" CD.BRANCH_AREA_NAME, CD.BRANCH_NBR,CD.BRANCH_NAME,CD.AO_CODE,CD.EMP_NAME ");
//			sql.append(" ,CASE WHEN (AO_CODE = '000' OR AO_CODE IS NULL OR AO_CODE IN (SELECT AO_CODE FROM TBPMS_SALES_AOCODE_REC WHERE TYPE = '3')) THEN 'MASS戶' ELSE '理財戶' END ");
			sql.append(" ,CASE WHEN NVL(CD.ao_code,'000') <> '000' AND AO.ao_code IS NULL THEN '理財戶'  ELSE 'MASS戶' END ,TRUNC(CD.CREATETIME)  ");
		}

		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
//		ResultIF list = dam.executePaging(condition,
//				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
//			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setResultList(list);
//			outputVO.setTotalRecord(list.getTotalRecord());
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			outputVO.setResultList(list);
			sendRtnObject(outputVO);
			
		}
	}

	
	/* ==== 【查詢】建表日期 ======== */
	public List<Map<String, Object>> queryCDate(Object body, IPrimitiveMap header) throws JBranchException {
		PMS358InputVO inputVO = (PMS358InputVO) body;
		PMS358OutputVO outputVO = new PMS358OutputVO();
		//20170520   週報/月報
		//TBPMS_NTD_CD      //周報
		//TBPMS_NTD_CD_MON  //月報		
		String dataBase=(inputVO.getReportType().equals("week"))?"TBPMS_NTD_CD":"TBPMS_NTD_CD_MON";
		////20170520   週報/月報
		//DATA_DATE      //周統計
		//YEARMONN  //月統計
		String date_co=(inputVO.getReportType().equals("week"))?"DATA_DATE":"YEARMON";
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT MAX(CREATETIME) AS CREATDATE FROM "+dataBase+" WHERE 1=1 ");
		sql.append(" AND "+date_co+" = :YEARMONN ");
		
		// 設定時間
		String CDate = "";
		
		CDate =(inputVO.getReportType().equals("week"))? inputVO.getReportDate().toString(): inputVO.getsCreDate().toString();
		CDate = CDate.replace("-", "");
		condition.setObject("YEARMONN",CDate);
		
		condition.setQueryString(sql.toString());
//		List<Map<String, Object>> list = dam.exeQuery(condition);		
//		outputVO.setResultList(list);
//		this.sendRtnObject(outputVO);
		return dam.exeQuery(condition);
	}
	
	/*** 分行轄下資料 ***/
	public void queryBRDetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS358InputVO inputVO = (PMS358InputVO) body;
		PMS358OutputVO outputVO = new PMS358OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("SELECT ROWNUM, T.* FROM ( ");
		//20170520   週報/月報
		//TBPMS_NTD_CD      //周報
		//TBPMS_NTD_CD_MON  //月報
		String dataBase=(inputVO.getReportType().equals("week"))?"TBPMS_NTD_CD":"TBPMS_NTD_CD_MON";
		//20170520   週報/月報
		//WEEK_BAL      //周統計
		//MON_BAL  //月統計
		String dataBase_co=(inputVO.getReportType().equals("week"))?"WEEK_BAL":"MON_BAL";
		sql.append("SELECT ");
		
		//週報要GROUP BY DATA_DATE 月報不用
		if(inputVO.getReportType().equals("week")){
			sql.append(" DATA_DATE, ");
		}
		
		sql.append(" REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, ");
		sql.append(" BRANCH_AREA_NAME, BRANCH_NBR,BRANCH_NAME,AO_CODE,EMP_NAME ");
		sql.append(" ,CASE WHEN (AO_CODE = '000' OR AO_CODE IS NULL OR AO_CODE IN (SELECT AO_CODE FROM TBPMS_SALES_AOCODE_REC WHERE TYPE = '3')) THEN 'MASS戶' ELSE '理財戶' END as CUST_TYPE, ");
		sql.append(" SUM("+dataBase_co+") as WEEK_BAL, ");
		sql.append(" SUM(LMON_BAL) as LMON_BAL, ");
		sql.append(" SUM(CD_DIFF) as CD_DIFF ");
		sql.append(" FROM   "+dataBase+ "  ");
		sql.append(" WHERE 1=1  AND BRANCH_NBR BETWEEN '200' AND  '900' ");
		sql.append(" AND TO_NUMBER(BRANCH_NBR) <> 806 AND TO_NUMBER(BRANCH_NBR) <> 810 ");
		if(inputVO.getReportType().equals("week")){
			sql.append(" and  (WEEK_BAL > 0 or LMON_BAL > 0 ) ");
//			sql.append(" AND CASE WHEN week_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN lmon_bal = 0 THEN 1 ELSE 3 END ");
		}else{
//			sql.append(" and  (MON_BAL > 0 or LMON_BAL > 0 ) ");
			sql.append(" AND CASE WHEN mon_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN lmon_bal = 0 THEN 1 ELSE 3 END ");
		}	
		
		//日期格式
		String DDate = inputVO.getStrDate().toString();
		DDate = DDate.replace("/", "");	
		//週報
		if(inputVO.getReportType().equals("week")){
			sql.append("and DATA_DATE = :data_date ");
			condition.setObject("data_date", DDate);
		}
		//月報
		if(inputVO.getReportType().equals("month")){
			sql.append("and YEARMON = :data_date ");
			condition.setObject("data_date", DDate );
		}
		if(StringUtils.isNotBlank(inputVO.getAo_code())){
			sql.append(" and AO_CODE = :ao_codee ");
			condition.setObject("ao_codee", inputVO.getAo_code() );
		}
		
		// 分行
		sql.append(" and BRANCH_NBR = :branch_nbr ");
		condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		
		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			sql.append(" and AO_CODE = :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());
		}
		
				
		sql.append(" GROUP BY ");
		
		//週報要GROUP BY DATA_DATE 月報不用
		if(inputVO.getReportType().equals("week")){
			sql.append(" DATA_DATE, ");
		}
		
		sql.append(" REGION_CENTER_ID, ");
		sql.append(" REGION_CENTER_NAME, BRANCH_AREA_ID, ");
		sql.append(" BRANCH_AREA_NAME, BRANCH_NBR, ");
		sql.append(" BRANCH_NAME, AO_CODE, EMP_NAME ");
		sql.append(" ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, ");
		sql.append(" BRANCH_NBR, AO_CODE) T ");
		
		// CUST_TYPE(理財戶/MASS戶)
		if (StringUtils.isNotBlank(inputVO.getCust_type())) {
			sql.append("WHERE CUST_TYPE = :cust_type ");
			condition.setObject("cust_type", inputVO.getCust_type());
		}
				
		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);

	
//		ResultIF list = dam.executePaging(condition,
//				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
//			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setBrDetail(list);
//			outputVO.setTotalRecord(list.getTotalRecord());
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/*** 理專轄下資料 ***/
	public void queryAODetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS358InputVO inputVO = (PMS358InputVO) body;
		PMS358OutputVO outputVO = new PMS358OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("SELECT ROWNUM, T.* FROM ( ");

		//20170520   週報/月報
		//TBPMS_NTD_CD      //周報
		//TBPMS_NTD_CD_MON  //月報
		String dataBase= "",dataBase_co="";
		if(StringUtils.equals(inputVO.getReportType(), "week")){
			dataBase = "TBPMS_NTD_CD";
			dataBase_co = "WEEK_BAL";
		}else{
			dataBase = "TBPMS_NTD_CD_MON";
			dataBase_co = "MON_BAL";
		}
		
		//20170520   週報/月報
		//WEEK_BAL      //周統計
		//MON_BAL  //月統計
		
		sql.append(" SELECT ");
		
		//週報要GROUP BY DATA_DATE 月報不用
		if(inputVO.getReportType().equals("week")){
			sql.append(" DATA_DATE, ");
		}
		
		sql.append(" REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, ");
		sql.append(" BRANCH_NAME, AO_CODE,EMP_ID, EMP_NAME,CUST_ID, CUST_NAME,");
//		sql.append(" WEEK_BAL, LMON_BAL, CD_DIFF, ");
		
		sql.append(" SUM("+dataBase_co+") as WEEK_BAL, ");
		sql.append(" SUM(LMON_BAL) as LMON_BAL, ");
		sql.append(" SUM(CD_DIFF) as CD_DIFF, ");
		
		sql.append(" CASE WHEN (AO_CODE = '000' OR AO_CODE IS NULL OR AO_CODE IN (SELECT AO_CODE FROM TBPMS_SALES_AOCODE_REC WHERE TYPE = '3')) THEN 'MASS戶' ELSE '理財戶' END as CUST_TYPE ");
		sql.append(" FROM  "+dataBase+ "  ");
		sql.append(" WHERE 1=1  AND BRANCH_NBR BETWEEN '200' AND  '900'  ");
		sql.append(" AND TO_NUMBER(BRANCH_NBR) <> 806 AND TO_NUMBER(BRANCH_NBR) <> 810 ");

		if(inputVO.getReportType().equals("week")){
			sql.append(" and  (WEEK_BAL > 0 or LMON_BAL > 0 ) ");
//			sql.append(" AND CASE WHEN week_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN lmon_bal = 0 THEN 1 ELSE 3 END ");
		}else{
//			sql.append(" and  (MON_BAL > 0 or LMON_BAL > 0 ) ");
			sql.append(" AND CASE WHEN mon_bal = 0 THEN 1 ELSE 2 END <> CASE WHEN lmon_bal = 0 THEN 1 ELSE 3 END ");
		}	
		
		//日期格式
		String DDate = inputVO.getStrDate().toString();
		DDate = DDate.replace("/", "");
		//週報
		if(inputVO.getReportType().equals("week")){
			sql.append("and DATA_DATE = :data_date ");
			condition.setObject("data_date", DDate);
		}
		//月報
		if(inputVO.getReportType().equals("month")){
			sql.append("and YEARMON = :data_date ");
			condition.setObject("data_date", DDate);
		}
		
		// 理專員編
		sql.append("and AO_CODE = :ao_code ");
		condition.setObject("ao_code", inputVO.getAo_code());	
		
		//MASS戶需要分行做篩選
		if(("000").equals(inputVO.getAo_code())){
			sql.append("and BRANCH_NBR = :branch_nbr ");
			condition.setObject("branch_nbr", inputVO.getBranch_nbr());	
		}
		sql.append(" GROUP BY ");
		
		//週報要GROUP BY DATA_DATE 月報不用
		if(inputVO.getReportType().equals("week")){
			sql.append(" DATA_DATE, ");
		}
		
		sql.append(" REGION_CENTER_ID, ");
		sql.append(" REGION_CENTER_NAME, BRANCH_AREA_ID, ");
		sql.append(" BRANCH_AREA_NAME, BRANCH_NBR, ");
		sql.append(" BRANCH_NAME, AO_CODE, EMP_ID, EMP_NAME, CUST_ID, CUST_NAME ");
		
		sql.append(" ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, ");
		sql.append(" BRANCH_NBR, AO_CODE, CUST_ID ) T ");

		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);

//		ResultIF list = dam.executePaging(condition,
//				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
//			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setAoDetail(list);
//			outputVO.setTotalRecord(list.getTotalRecord());
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

}