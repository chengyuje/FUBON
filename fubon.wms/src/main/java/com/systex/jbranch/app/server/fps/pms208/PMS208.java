package com.systex.jbranch.app.server.fps.pms208;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms208.PMS208InputVO;
import com.systex.jbranch.app.server.fps.pms208.PMS208OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
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
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :落點排名(含晉級目標達成率)<br>
 * Comments Name : PMS208OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version :1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms208")
@Scope("request")
public class PMS208 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS208.class);

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)throws JBranchException, ParseException{
		//輸入VO
		PMS208InputVO inputVO = (PMS208InputVO) body;
		//輸出VO
		PMS208OutputVO outputVO = new PMS208OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		try{
			this.queryData(condition, inputVO);
			//分頁查詢結果
			ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	public void queryData(QueryConditionIF condition,PMS208InputVO inputVO)
		throws JBranchException, ParseException{
		String roleType = "";
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);//個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //業務處主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		StringBuffer sql = new StringBuffer();
		
		try {
            sql.append(" WITH RTO AS ( ");
            sql.append("     SELECT TO_NUMBER(INV_RATE) AS p_INV_RATE ");
            sql.append("          , TO_NUMBER(INS_RATE) AS p_INS_RATE ");
            sql.append("          , TO_NUMBER(EXG_RATE) AS p_EXG_RATE ");
            sql.append("     FROM ( ");
            sql.append("       SELECT PARAM_CODE, PARAM_DESC ");
            sql.append("       FROM TBSYSPARAMETER ");
            sql.append("       WHERE PARAM_TYPE='PMS.TARGET_RATE' ) ");
            sql.append("     PIVOT (SUM(PARAM_DESC) FOR PARAM_CODE IN ( ");
            sql.append("       'INV' AS INV_RATE, ");
            sql.append("       'INS' AS INS_RATE, ");
            sql.append("       'EXG' AS EXG_RATE ) ) ) , ");
            sql.append(" AO_DTL AS ( ");
            sql.append("     SELECT AO.DATA_DATE ");
            sql.append("          , AO.REGION_CENTER_ID , AO.REGION_CENTER_NAME, AO.BRANCH_AREA_ID, AO.BRANCH_AREA_NAME  ");
            sql.append("          , AO.BRANCH_NBR, AO.BRANCH_NAME, AO.GROUP_TYPE ");
            sql.append("          , AO.EMP_ID, AO.EMP_NAME, AO.AO_CODE, AO.AO_JOB_TITLE ");
            sql.append("          , AO.MTD_INV_FEE, NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INV_RATE*AO.MTD_INV_RATE_MM  AS MTD_INV_GOAL ");
            sql.append("          , DECODE(NVL(AOTAR.TAR_AMOUNT,0),0,0,(AO.MTD_INV_FEE/(NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INV_RATE*AO.MTD_INV_RATE_MM)*100)) AS MTD_INV_RATE ");
            sql.append("          , AO.MTD_INS_FEE, NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INS_RATE*AO.MTD_INS_RATE_MM  AS MTD_INS_GOAL ");
            sql.append("          , DECODE(NVL(AOTAR.TAR_AMOUNT,0),0,0,(AO.MTD_INS_FEE/(NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INS_RATE*AO.MTD_INS_RATE_MM)*100)) AS MTD_INS_RATE ");
            sql.append("          , AO.MTD_EXG_FEE, NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_EXG_RATE*AO.MTD_INV_RATE_MM  AS MTD_EXG_GOAL ");
            sql.append("          , DECODE(NVL(AOTAR.TAR_AMOUNT,0),0,0,(AO.MTD_EXG_FEE/(NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_EXG_RATE*AO.MTD_INV_RATE_MM)*100)) AS MTD_EXG_RATE ");
            sql.append("          , AO.MTD_SUM_FEE ");
            sql.append("          , ( NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INV_RATE*AO.MTD_INV_RATE_MM + ");
            sql.append("              NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INS_RATE*AO.MTD_INS_RATE_MM + ");
            sql.append("              NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_EXG_RATE*AO.MTD_INV_RATE_MM ) AS MTD_SUM_GOAL ");
            sql.append("          , DECODE(NVL(AOTAR.TAR_AMOUNT,0),0,0,(AO.MTD_SUM_FEE / ");
            sql.append("            ( NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INV_RATE*AO.MTD_INV_RATE_MM + ");
            sql.append("              NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_INS_RATE*AO.MTD_INS_RATE_MM + ");
            sql.append("              NVL(AOTAR.TAR_AMOUNT,0)*RTO.p_EXG_RATE*AO.MTD_INV_RATE_MM )*100)) AS MTD_SUM_RATE ");
            sql.append("          , AO.MTD_SUM_RATE_MM, AO.YTD_SUM_FEE, AO.YTD_SUM_RATE_YY ");
            sql.append("      FROM TBPMS_AO_DAY_PROFIT_MYTD AO ");
            sql.append("      LEFT JOIN RTO ON 1 = 1 ");
            sql.append("      LEFT JOIN TBPMS_EMP_PRD_TAR_M AOTAR ");
            sql.append("        ON AO.EMP_ID = AOTAR.EMP_ID ");
            sql.append("       AND AOTAR.DATA_YEARMON = SUBSTR(AO.DATA_DATE, 1, 6) ");
            sql.append("      WHERE AO.DATA_DATE <= :data_date ");
            sql.append("        AND AO.DATA_DATE IN ( ");
            sql.append("           SELECT MAX(DATA_DATE) ");
            sql.append("           FROM TBPMS_AO_DAY_PROFIT_MYTD ");
            sql.append("           WHERE SUBSTR(DATA_DATE,1,4) = SUBSTR(:data_date, 1, 4) ");
            sql.append("           GROUP BY SUBSTR(DATA_DATE,1,6) ) ) ");
            sql.append(" SELECT RTN.DATA_DATE ");
            sql.append("      , RTN.REGION_CENTER_ID, RTN.REGION_CENTER_NAME, RTN.BRANCH_AREA_ID, RTN.BRANCH_AREA_NAME  ");
            sql.append("      , RTN.BRANCH_NBR, RTN.BRANCH_NAME, RTN.AO_CODE, RTN.EMP_ID, RTN.EMP_NAME, RTN.AO_JOB_TITLE  ");
            sql.append("      , RTN.MTD_SUM_GOAL, RTN.MTD_SUM_FEE, ROUND(RTN.MTD_SUM_RATE,2) AS MTD_SUM_RATE ");
            sql.append("      , RANK() OVER(ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_ALL_BY_ALL ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY NULL) AS CNT_MTD_ALL_BY_ALL ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.AO_JOB_TITLE ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_ALL_BY_JOB ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.AO_JOB_TITLE) AS CNT_MTD_ALL_BY_JOB ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_CEN_BY_ALL ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID) AS CNT_MTD_CEN_BY_ALL ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.AO_JOB_TITLE ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_CEN_BY_JOB ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.AO_JOB_TITLE) AS CNT_MTD_CEN_BY_JOB ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_OPT_BY_ALL ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID) AS CNT_MTD_OPT_BY_ALL ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.AO_JOB_TITLE ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_OPT_BY_JOB ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.AO_JOB_TITLE) AS CNT_MTD_OPT_BY_JOB ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_BRA_BY_ALL ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR) AS CNT_MTD_BRA_BY_ALL ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR, RTN.AO_JOB_TITLE ORDER BY RTN.MTD_SUM_RATE DESC ) AS RANK_MTD_BRA_BY_JOB ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR, RTN.AO_JOB_TITLE) AS CNT_MTD_BRA_BY_JOB ");
            sql.append("      , YRS.YTD_SUM_GOAL, RTN.YTD_SUM_FEE, DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)*100) AS YTD_SUM_RATE ");
            sql.append("      , RANK() OVER(ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_ALL_BY_ALL ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY NULL) AS CNT_YTD_ALL_BY_ALL ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.AO_JOB_TITLE ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_ALL_BY_JOB ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.AO_JOB_TITLE) AS CNT_YTD_ALL_BY_JOB ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_CEN_BY_ALL ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID) AS CNT_YTD_CEN_BY_ALL ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.AO_JOB_TITLE ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_CEN_BY_JOB ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.AO_JOB_TITLE) AS CNT_YTD_CEN_BY_JOB ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_OPT_BY_ALL ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID) AS CNT_YTD_OPT_BY_ALL ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.AO_JOB_TITLE ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_OPT_BY_JOB ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.AO_JOB_TITLE) AS CNT_YTD_OPT_BY_JOB ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_BRA_BY_ALL ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR) AS CNT_YTD_BRA_BY_ALL ");
            sql.append("      , RANK() OVER(PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR, RTN.AO_JOB_TITLE ORDER BY DECODE(YRS.YTD_SUM_GOAL,0,0,ROUND(RTN.YTD_SUM_FEE/YRS.YTD_SUM_GOAL,2)) DESC ) AS RANK_YTD_BRA_BY_JOB ");
            sql.append("      , COUNT(RTN.EMP_ID) OVER (PARTITION BY RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR, RTN.AO_JOB_TITLE) AS CNT_YTD_BRA_BY_JOB ");
            sql.append(" FROM AO_DTL RTN ");
            sql.append(" LEFT JOIN ( ");
            sql.append("     SELECT EMP_ID, SUM(MTD_SUM_GOAL) AS YTD_SUM_GOAL ");
            sql.append("     FROM AO_DTL ");
            sql.append("     GROUP BY EMP_ID ) YRS ");
            sql.append("   ON RTN.EMP_ID = YRS.EMP_ID ");
            sql.append(" WHERE RTN.DATA_DATE = :data_date ");
			
			//==主查詢條件==
			condition.setObject("data_date", inputVO.getReportDate());
			
			// AO_COCE
			if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				sql.append("  and RTN.EMP_ID = :EMP_ID ");
				condition.setObject("EMP_ID", inputVO.getEmp_id());
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append("  and RTN.AO_CODE IN (:ao_code) ");
					condition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("  and RTN.BRANCH_NBR = :branch_nbr ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {
					System.err.println("登入非總行人員強制加分行" + pms000outputVO.getBranchList());
					sql.append("  and RTN.BRANCH_NBR IN (:branch_nbr) ");
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("  and RTN.BRANCH_AREA_ID = :branch_area_id ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("  and RTN.BRANCH_AREA_ID IN (:branch_area_id) ");
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			// 業務處
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("and RTN.REGION_CENTER_ID = :region_center_id ");
				condition.setObject("region_center_id", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加業務處
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and RTN.REGION_CENTER_ID IN (:region_center_id) ");
					condition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			
			sql.append(" order by RTN.REGION_CENTER_ID, RTN.BRANCH_AREA_ID, RTN.BRANCH_NBR, EMP_ID ");
			condition.setQueryString(sql.toString());
		}catch (Exception e) {
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
		
	public void export(Object body, IPrimitiveMap header) throws JBranchException,ParseException {
		// 取得畫面資料
		PMS208OutputVO outputVO = (PMS208OutputVO) body;
				
		List<Map<String, Object>> list = outputVO.getResultList();
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "落點排名(含晉級目標達成率)" + sdf.format(new Date())+ "-"+getUserVariable(FubonSystemVariableConsts.LOGINID)
						+ "_員工編號.csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[31];
					int i = 0;
					records[i++] = checkIsNull(map, "DATA_DATE"); // 資料日期
					records[i++] = checkIsNull(map, "REGION_CENTER_ID"); // 業務處ID
					records[i++] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
					records[i++] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
					records[i++] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
					records[i++] = checkIsNull(map, "BRANCH_NBR"); // 分行代碼
					records[i++] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[i++] = checkIsNull(map, "AO_CODE"); // AO Code
					records[i++] = checkIsNull(map, "EMP_ID"); // 理專員編
					records[i++] = checkIsNull(map, "EMP_NAME"); // 理專姓名
					records[i++] = checkIsNull(map, "AO_JOB_TITLE"); // 職級
					
					if("MTD".equals(outputVO.getType())){
						records[i++] = checkIsNull(map, "MTD_SUM_GOAL"); // 目標金額
						records[i++] = checkIsNull(map, "MTD_SUM_FEE"); // 實際手收
						records[i++] = checkIsNullRate(map, "MTD_SUM_RATE"); // 達成率
						records[i++] = checkIsNull(map, "RANK_MTD_ALL_BY_ALL"); // 全行理專排名
						records[i++] = checkIsNull(map, "CNT_MTD_ALL_BY_ALL");
						records[i++] = checkIsNull(map, "RANK_MTD_ALL_BY_JOB"); // 全行相同職務別理專排名
						records[i++] = checkIsNull(map, "CNT_MTD_ALL_BY_JOB");
						records[i++] = checkIsNull(map, "RANK_MTD_CEN_BY_ALL"); // 所屬業務處理專排名
						records[i++] = checkIsNull(map, "CNT_MTD_CEN_BY_ALL");
						records[i++] = checkIsNull(map, "RANK_MTD_CEN_BY_JOB"); // 所屬業務處相同職務別理專排名
						records[i++] = checkIsNull(map, "CNT_MTD_CEN_BY_JOB");
						records[i++] = checkIsNull(map, "RANK_MTD_OPT_BY_ALL"); // 所屬營運區理專排名
						records[i++] = checkIsNull(map, "CNT_MTD_OPT_BY_ALL");
						records[i++] = checkIsNull(map, "RANK_MTD_OPT_BY_JOB"); // 所屬營運區相同職務別理專排名
						records[i++] = checkIsNull(map, "CNT_MTD_OPT_BY_JOB"); 
						records[i++] = checkIsNull(map, "RANK_MTD_BRA_BY_ALL"); // 所屬分行理專排名
						records[i++] = checkIsNull(map, "CNT_MTD_BRA_BY_ALL");
						records[i++] = checkIsNull(map, "RANK_MTD_BRA_BY_JOB"); // 所屬分行相同職務別理專排名
						records[i++] = checkIsNull(map, "CNT_MTD_BRA_BY_JOB");
					}else{
						records[i++] = checkIsNull(map, "YTD_SUM_GOAL"); // 目標金額
						records[i++] = checkIsNull(map, "YTD_SUM_FEE"); // 實際手收
						records[i++] = checkIsNullRate(map, "YTD_SUM_RATE"); // 達成率
						records[i++] = checkIsNull(map, "RANK_YTD_ALL_BY_ALL"); // 全行理專排名
						records[i++] = checkIsNull(map, "CNT_YTD_ALL_BY_ALL");
						records[i++] = checkIsNull(map, "RANK_YTD_ALL_BY_JOB"); // 全行相同職務別理專排名
						records[i++] = checkIsNull(map, "CNT_YTD_ALL_BY_JOB");
						records[i++] = checkIsNull(map, "RANK_YTD_CEN_BY_ALL"); // 所屬業務處理專排名
						records[i++] = checkIsNull(map, "CNT_YTD_CEN_BY_ALL");
						records[i++] = checkIsNull(map, "RANK_YTD_CEN_BY_JOB"); // 所屬業務處相同職務別理專排名
						records[i++] = checkIsNull(map, "CNT_YTD_CEN_BY_JOB");
						records[i++] = checkIsNull(map, "RANK_YTD_OPT_BY_ALL"); // 所屬營運區理專排名
						records[i++] = checkIsNull(map, "CNT_YTD_OPT_BY_ALL");
						records[i++] = checkIsNull(map, "RANK_YTD_OPT_BY_JOB"); // 所屬營運區相同職務別理專排名
						records[i++] = checkIsNull(map, "CNT_YTD_OPT_BY_JOB"); 
						records[i++] = checkIsNull(map, "RANK_YTD_BRA_BY_ALL"); // 所屬分行理專排名
						records[i++] = checkIsNull(map, "CNT_YTD_BRA_BY_ALL");
						records[i++] = checkIsNull(map, "RANK_YTD_BRA_BY_JOB"); // 所屬分行相同職務別理專排名
						records[i++] = checkIsNull(map, "CNT_YTD_BRA_BY_JOB");
					}
					
					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[31];
				int j = 0;
				csvHeader[j++] = "資料日期";
				csvHeader[j++] = "業務處ID";
				csvHeader[j++] = "業務處名稱";
				csvHeader[j++] = "營運區ID";
				csvHeader[j++] = "營運區名稱";
				csvHeader[j++] = "分行代碼";
				csvHeader[j++] = "分行名稱";
				csvHeader[j++] = "AO Code";
				csvHeader[j++] = "理專員編";
				csvHeader[j++] = "理專姓名";
				csvHeader[j++] = "職級";
				csvHeader[j++] = "目標金額";
				csvHeader[j++] = outputVO.getType()+" 實際手收";
				csvHeader[j++] = outputVO.getType()+" 達成率";
				csvHeader[j++] = outputVO.getType()+" 全行-全排名";
				csvHeader[j++] = "全行理專總人數";
				csvHeader[j++] = outputVO.getType()+" 全行-職務別排名";
				csvHeader[j++] = "全行相同職務別理專總人數";
				csvHeader[j++] = outputVO.getType()+" 所屬業務處-全排名";
				csvHeader[j++] = "所屬業務處理專總人數";
				csvHeader[j++] = outputVO.getType()+" 所屬業務處-職務別排名";
				csvHeader[j++] = "所屬業務處相同職務別理專總人數";
				csvHeader[j++] = outputVO.getType()+" 所屬營運區-全排名";
				csvHeader[j++] = "所屬營運區理專總人數";
				csvHeader[j++] = outputVO.getType()+" 所屬營運區-職務別排名";
				csvHeader[j++] = "所屬營運區相同職務別理專總人數";
				csvHeader[j++] = outputVO.getType()+" 所屬分行-全排名";
				csvHeader[j++] = "所屬分行理專總人數";
				csvHeader[j++] = outputVO.getType()+" 所屬分行-職務別排名";
				csvHeader[j++] = "所屬分行相同職務別理專總人數";			

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			}
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/** ==判斷MAP值傳會字串== **/
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotEmpty(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	/** 達成率格式 **/
	private String checkIsNullRate(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))&& map.get(key) != null) {
			return map.get(key) + "%";
		} else
			return "0%";
	}
}
