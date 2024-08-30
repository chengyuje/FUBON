package com.systex.jbranch.app.server.fps.pms206;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.util.Calendar;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
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
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :AMC活動量<br>
 * Comments Name : PMS206.java<br>
 * Author : Kevin Hsu<br>
 * Date :2016年05月28日 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月12日<br>
 */

@Component("pms206")
@Scope("request")
public class PMS206 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS206.class);
	
	/**
	 *  取得本週/上週,本月/上月日期
	 */
	public void getReportDate(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS206OutputVO outputVO = new PMS206OutputVO();
		dam = this.getDataAccessManager();
		try {
			QueryConditionIF queryCondition = dam
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			
			sql.append("SELECT DISTINCT WEEK_START_DATE,WEEK_END_DATE,WEEK_START_DATE AS DATA"
					+ ",WEEK_START_DATE || ' 第'||SUBSTRB(MTH_WEEK,4,1)||'週' AS LABEL FROM TBPMS_DATE_REC "
					+ "WHERE TO_DATE(DATA_DATE,'YYYYMMDD') BETWEEN ADD_MONTHS(SYSDATE,-6) AND SYSDATE "
					+ "ORDER BY WEEK_START_DATE DESC");
			
			queryCondition.setQueryString(sql.toString());
			
			outputVO.setWeekList(dam.exeQuery(queryCondition));
			sql = new StringBuffer();
			
			sql.append("SELECT DISTINCT MON_YEAR,TO_DATE(MON_YEAR,'YYYYMM') AS THIS_MON_START_DATE,"
					+ "LAST_DAY(TO_DATE(MON_YEAR,'YYYYMM')) AS LAST_MON_START_DATE ,MON_YEAR AS DATA,"
					+ "MON_YEAR AS LABEL FROM TBPMS_DATE_REC "
					+ "WHERE TO_DATE(DATA_DATE,'YYYYMMDD') BETWEEN ADD_MONTHS(SYSDATE,-6) AND SYSDATE "
					+ "ORDER BY MON_YEAR DESC ");
			
			queryCondition.setQueryString(sql.toString());
			
			outputVO.setMeonthList(dam.exeQuery(queryCondition));
//			
//			sql.append("SELECT TO_DATE(MON_YEAR||'01','YYYYMMDD') as THIS_MON_START_DATE,"
//					+ "SYSDATE as THIS_MON_END_DATE,"
//					+ "ADD_MONTHS(TO_DATE(MON_YEAR||'01','YYYYMMDD'),-1) as LAST_MON_START_DATE, "
//					+ "TO_DATE(MON_YEAR||'01','YYYYMMDD')-1 as LAST_MON_END_DATE, "
//					+ "TO_DATE(WEEK_START_DATE,'YYYYMMDD') as WEEK_START_DATE, "
//					+ "TO_DATE(WEEK_END_DATE,'YYYYMMDD') as WEEK_END_DATE "
//					+ "FROM TBPMS_DATE_REC WHERE DATA_DATE = TO_CHAR(SYSDATE,'YYYYMMDD')");
//			queryCondition.setQueryString(sql.toString());
//			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
//			if(!list.isEmpty()){
//				//本週本月上月起訖日
//				List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
//				Map<String,Object> dataMap = new HashMap<String,Object>();
//				dataMap.put("THIS_WEEK_START_DATE",list.get(0).get("WEEK_START_DATE"));
//				dataMap.put("THIS_WEEK_END_DATE",list.get(0).get("WEEK_END_DATE"));
//				dataMap.put("THIS_MON_START_DATE",list.get(0).get("THIS_MON_START_DATE"));
//				dataMap.put("THIS_MON_END_DATE",list.get(0).get("THIS_MON_END_DATE"));
//				dataMap.put("LAST_MON_START_DATE",list.get(0).get("LAST_MON_START_DATE"));
//				dataMap.put("LAST_MON_END_DATE",list.get(0).get("LAST_MON_END_DATE"));
//				
//				//上週起訖日
//				sql = new StringBuffer();
//				sql.append("SELECT TO_DATE(WEEK_START_DATE,'YYYYMMDD') as WEEK_START_DATE, "
//					+ "TO_DATE(WEEK_END_DATE,'YYYYMMDD') as WEEK_END_DATE "
//					+ "FROM TBPMS_DATE_REC WHERE DATA_DATE = TO_CHAR(:DATA_DATE,'YYYYMMDD')");
//				Calendar cal = Calendar.getInstance();  
//				cal.setTime((Date)list.get(0).get("WEEK_START_DATE"));
//				cal.add(Calendar.DATE, -1);
//				queryCondition.setObject("DATA_DATE", cal.getTime());
//				queryCondition.setQueryString(sql.toString());
//				list = dam.exeQuery(queryCondition);
//				if(!list.isEmpty()){
//					dataMap.put("LAST_WEEK_START_DATE",list.get(0).get("WEEK_START_DATE"));
//					dataMap.put("LAST_WEEK_END_DATE",list.get(0).get("WEEK_END_DATE"));
//				}
//				dataList.add(dataMap);
//				outputVO.setDateList(dataList);
//			}
		}catch(Exception e){
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		sendRtnObject(outputVO);
	}
	/**
	 * 查詢(主表)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS206InputVO inputVO = (PMS206InputVO) body;
		PMS206OutputVO outputVO = new PMS206OutputVO();
		dam = this.getDataAccessManager();
		try {

			// 周一時間
			Calendar mCal = Calendar.getInstance();
			mCal.setFirstDayOfWeek(Calendar.SUNDAY);
			mCal.setTimeInMillis(System.currentTimeMillis());// 當前時間
			mCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			QueryConditionIF queryCondition = dam
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			String roleType = "";
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
			Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
			Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
			Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
			Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
				
			//取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(getYesterday()); //此交易固定放昨日日期
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			
			//==主查詢==
			StringBuffer sql1 = new StringBuffer();
//			sql1.append(" SELECT  * FROM TBPMS_WEEK_AMC_ACT A   ");
//			sql1.append(" , TBPMS_WEEK_AMC_ACT_AVG B ");
//			sql1.append(" WHERE 1=1  AND  A.AO_JOB_RANK = B.AO_JOB_RANK  ");
//			sql1.append("   AND A.CON_DEGREE = B.CON_DEGREE AND TO_DATE(A.WEEK_START_DATE,'YYYYMMDD') = TO_DATE(:NOWSUNDAY,'YYYY-MM-DD') ");

			sql1.append("SELECT CASE WHEN A.CON_DEGREE = 'Z' THEN '總計' ELSE A.CON_DEGREE END AS CON_DEGREE");
			sql1.append(",COALESCE(A.CUST_CNT,0) AS CUST_CNT");
			sql1.append(",COALESCE(A.A_EST_CNT,0) AS A_EST_CNT");
			sql1.append(",COALESCE(A.A_ACT_CNT,0) AS A_ACT_CNT");
			sql1.append(",COALESCE(A.M_EST_CNT,0) AS M_EST_CNT");
			sql1.append(",COALESCE(A.M_ACT_CNT,0) AS M_ACT_CNT");
			sql1.append(",COALESCE(A.C_EST_CNT,0) AS C_EST_CNT");
			sql1.append(",COALESCE(A.C_ACT_CNT,0) AS C_ACT_CNT");
			sql1.append(",A.A_RATE,A.M_RATE,A.C_RATE,A.AO_JOB_RANK,B.A_RATE AS AV_A_RATE,B.M_RATE AS AV_M_RATE,B.C_RATE AS AV_C_RATE");
			sql1.append(",LA.A_RATE AS LA_A_RATE,LA.M_RATE AS LA_M_RATE,LA.C_RATE AS LA_C_RATE");
			sql1.append(",LA.WEEK_START_DATE AS LA_WEEK_START_DATE,LA.WEEK_END_DATE AS LA_WEEK_END_DATE ");
			sql1.append(",A.WEEK_START_DATE ,A.WEEK_END_DATE ");
			sql1.append(" from TBPMS_WEEK_AMC_ACT A left join TBPMS_WEEK_AMC_ACT_AVG B ON A.AO_JOB_RANK = B.AO_JOB_RANK AND  A.WEEK_START_DATE = B.WEEK_START_DATE AND A.CON_DEGREE = B.CON_DEGREE ");
			sql1.append(" left join TBPMS_WEEK_AMC_ACT LA on LA.WEEK_START_DATE = (SELECT WEEK_START_DATE FROM TBPMS_DATE_REC WHERE DATA_DATE = TO_CHAR(TO_DATE(A.WEEK_START_DATE,'YYYYMMDD')- 1,'YYYYMMDD')) ");
			sql1.append(" and LA.AO_CODE = A.AO_CODE and LA.EMP_ID = A.EMP_ID AND LA.CON_DEGREE = A.CON_DEGREE ");
			sql1.append("WHERE  1=1 ");
			sql1.append(" AND A.WEEK_START_DATE = :NOWSUNDAY ");
			//原來的A.WEEK_START_DATE是長這樣--->TO_DATE(A.WEEK_START_DATE,'YYYYMMDD') = TO_DATE(:NOWSUNDAY,'YYYY-MM-DD')
			sql1.append(" ");
			//==主查詢條件設定==
			// AO_COCE
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql1.append("and A.AO_CODE = :ao_code ");
				queryCondition.setObject("ao_code", inputVO.getAo_code());
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql1.append("and A.AO_CODE IN (:ao_code) ");
					queryCondition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql1.append("and A.BRANCH_NBR = :branch_nbr ");
				queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {
					sql1.append("and A.BRANCH_NBR IN (:branch_nbr) ");
					queryCondition.setObject("branch_nbr", pms000outputVO.getBranchList());
				}
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql1.append("and A.BRANCH_AREA_ID = :branch_area_id ");
				queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql1.append("and A.BRANCH_AREA_ID IN (:branch_area_id) ");
					queryCondition.setObject("branch_area_id", pms000outputVO.getArea_branchList());
				}
			}
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql1.append("and A.REGION_CENTER_ID = :region_center_id ");
				queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql1.append("and A.REGION_CENTER_ID IN (:region_center_id) ");
					queryCondition.setObject("region_center_id", pms000outputVO.getRegionList());
				}
			}

			
			//排序
			sql1.append(" ORDER BY DECODE(A.CON_DEGREE,'E',1,'I',2,'P',3,'O',4,'S',5,'Z',6) 　");
			queryCondition.setQueryString(sql1.toString());
			
			//設定日期

//			queryCondition.setObject("NOWSUNDAY", StringUtils.left(inputVO.getTHIS_WEEK_START_DATE(),10)); // 本週日第一天 星期天
			queryCondition.setObject("NOWSUNDAY", inputVO.getWEEK()); // 原本是從reportdate查出來的，現在改要求，變從js拿來

//			queryCondition.setObject("LASTSUNDAY", StringUtils.left(inputVO.getLAST_WEEK_START_DATE(),10)); // 上週日第一天 星期天
			//分頁查詢結果
			List<Map<String,Object>> list = dam.executeQuery(queryCondition);
			
			if (!list.isEmpty()) {
		
				outputVO.setResultList(list); // data
			}else{
				outputVO.setResultList(new ArrayList<Map<String,Object>>()); // data
			}
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	/**
	 * 查詢2
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData2(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS206InputVO inputVO = (PMS206InputVO) body;
		PMS206OutputVO outputVO = new PMS206OutputVO();
		dam = this.getDataAccessManager();
		try {
			// 周一時間
			Calendar mCal = Calendar.getInstance();
			mCal.setFirstDayOfWeek(Calendar.SUNDAY);
			mCal.setTimeInMillis(System.currentTimeMillis());// 當前時間
			mCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			QueryConditionIF queryCondition = dam
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			String roleType = "";
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
			Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
			Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
			Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
			Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
				
			//取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(getYesterday()); //此交易固定放昨日日期
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			//==主查詢==
			StringBuffer sql2 = new StringBuffer();
			String nmon = sdf.format(mCal.getTime()).toString();
			sql2.append("SELECT CASE WHEN A.CON_DEGREE = 'Z' THEN '總計' ELSE A.CON_DEGREE END AS CON_DEGREE");
			sql2.append(",COALESCE(A.CUST_CNT,0) AS CUST_CNT");
			sql2.append(",COALESCE(A.A_EST_CNT,0) AS A_EST_CNT");
			sql2.append(",COALESCE(A.A_ACT_CNT,0) AS A_ACT_CNT");
			sql2.append(",COALESCE(A.M_EST_CNT,0) AS M_EST_CNT");
			sql2.append(",COALESCE(A.M_ACT_CNT,0) AS M_ACT_CNT");
			sql2.append(",COALESCE(A.C_EST_CNT,0) AS C_EST_CNT");
			sql2.append(",COALESCE(A.C_ACT_CNT,0) AS C_ACT_CNT");
			sql2.append(",A.A_RATE,A.M_RATE,A.C_RATE,A.AO_JOB_RANK,B.A_RATE AS AV_A_RATE,B.M_RATE AS AV_M_RATE,B.C_RATE AS AV_C_RATE");
			sql2.append(",LA.A_RATE AS LA_A_RATE,LA.M_RATE AS LA_M_RATE,LA.C_RATE AS LA_C_RATE");
			sql2.append(",TO_CHAR(TO_DATE(LA.YEARMON,'YYYYMM'),'YYYYMMDD') AS LA_MON_START_DATE,TO_CHAR(LAST_DAY(TO_DATE(LA.YEARMON,'YYYYMM')),'YYYYMMDD') AS LA_MON_END_DATE ");
			sql2.append(",TO_CHAR(TO_DATE(A.YEARMON,'YYYYMM'),'YYYYMMDD') AS MON_START_DATE ,A.END_DATE AS MON_END_DATE ");
			sql2.append(" FROM TBPMS_MON_AMC_ACT A ");
			sql2.append(" LEFT JOIN TBPMS_MON_AMC_ACT_AVG B ON A.CON_DEGREE=B.CON_DEGREE AND A.AO_JOB_RANK=B.AO_JOB_RANK AND A.YEARMON=B.YEARMON");
			sql2.append(" left join TBPMS_MON_AMC_ACT_AVG LA on TO_DATE(LA.YEARMON,'YYYYMM') = ADD_MONTHS(TO_DATE(A.YEARMON,'YYYYMM'),-1) ");
			sql2.append(" AND A.AO_JOB_RANK = LA.AO_JOB_RANK AND A.CON_DEGREE = LA.CON_DEGREE ");
//			sql2.append(" WHERE 1=1 AND A.YEARMON = TO_CHAR(SYSDATE,'YYYYMM') ");//原來的日期審核是這樣
			sql2.append(" WHERE 1=1 AND A.YEARMON = :NOWSUNDAY ");//這個是新的，從js拿來
			//==主查詢條件設定==
			// AO_COCE
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql2.append("and A.AO_CODE = :ao_code ");
				queryCondition.setObject("ao_code", inputVO.getAo_code());
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql2.append("and A.AO_CODE IN (:ao_code) ");
					queryCondition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql2.append("and A.BRANCH_NBR = :branch_nbr ");
				queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {
					sql2.append("and A.BRANCH_NBR IN (:branch_nbr) ");
					queryCondition.setObject("branch_nbr", pms000outputVO.getBranchList());
				}
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql2.append("and A.BRANCH_AREA_ID = :branch_area_id ");
				queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql2.append("and A.BRANCH_AREA_ID IN (:branch_area_id) ");
					queryCondition.setObject("branch_area_id", pms000outputVO.getArea_branchList());
				}
			}
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql2.append("and A.REGION_CENTER_ID = :region_center_id ");
				queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql2.append("and A.REGION_CENTER_ID IN (:region_center_id) ");
					queryCondition.setObject("region_center_id", pms000outputVO.getRegionList());
				}
			}
			
			//排序
			sql2.append(" ORDER BY DECODE(A.CON_DEGREE,'E',1,'I',2,'P',3,'O',4,'S',5,'Z',6) 　");
			queryCondition.setQueryString(sql2.toString());
			
			
			queryCondition.setObject("NOWSUNDAY", inputVO.getMONTH()); // 原本是sysdate，現在改要求，變從js拿來
			
			//分頁查詢結果
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			
			if (!list.isEmpty()) {
		
				outputVO.setResultList(list); // data
			}else{
				outputVO.setResultList(new ArrayList<Map<String,Object>>()); // data
			}
			
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	private String getYesterday(){
		Calendar cal = Calendar.getInstance();  
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(cal.getTime());
	}
}
