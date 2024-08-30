package com.systex.jbranch.app.server.fps.pms304;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms303.PMS303InputVO;
import com.systex.jbranch.app.server.fps.pms303.PMS303OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :分行保險速報明細Controller <br>
 * Comments Name : PMS304.java<br>
 * Author :Kevin<br>
 * Date :2016年08月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms304")
@Scope("request")
public class PMS304 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		//輸入vo
		PMS304InputVO inputVO = (PMS304InputVO) body;
		//輸出vo
		PMS304OutputVO outputVO = new PMS304OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);//個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		StringBuffer sb = new StringBuffer();
		
		try {
			// ==主查詢==	
			if(inputVO.getCheck()==true)
			{ 
					sb.append(" select rownum as num, t.* ");
					sb.append(" from ( ");
					sb.append(" 	select * from ");
					sb.append(" 	TBPMS_BR_DAY_INS ");
					sb.append(" 	order by region_center_id, branch_area_id, branch_nbr ) t ");
					sb.append(" where 1=1 ");
			}else{
					sb.append(" select rownum as num,t.* ");
					sb.append(" from ( ");
					sb.append("  select X.* , Z.ROLE_NAME AS ROLE_NAME ");
					sb.append("  from TBPMS_AO_DAY_INS X  ");
					sb.append("  LEFT JOIN VWORG_EMP_INFO Y ");
					sb.append("    ON X.EMP_ID = Y.EMP_ID AND X.AO_CODE = Y.AO_CODE ");
					sb.append("  LEFT JOIN TBORG_ROLE Z ");
					sb.append("    ON Y.ROLE_ID = Z.ROLE_ID ");
					sb.append("  order by X.region_center_id, X.branch_area_id, X.branch_nbr ) t ");
					sb.append(" where 1=1  ");
			}
			// ==主查詢條件==
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sb.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
				queryCondition.setObject("REGION_CENTER_IDDD", "%" + inputVO.getRegion_center_id() + "%");
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sb.append("and REGION_CENTER_ID IN (:region_center_id) ");
					queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sb.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
				queryCondition.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sb.append("  and BRANCH_AREA_ID IN (:branch_area_id) ");
					queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sb.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
				queryCondition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr() + "%");
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {		
					sb.append("  and BRANCH_NBR IN (:branch_nbr) ");
					queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			
			//日期
			if (inputVO.getsCreDate() != null) {
				sb.append(" and DATA_DATE  = :YEARMONN");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				// 進行轉換
				String dateString = sdf.format(inputVO.getsCreDate());
				queryCondition.setObject("YEARMONN", dateString);
			}
			//員編
			if(inputVO.getCheck()!=true){
				if (!StringUtils.isBlank(inputVO.getAo_code())) {
					sb.append(" and AO_CODE LIKE :EMP_IDEE");
					queryCondition.setObject("EMP_IDEE", "%" + inputVO.getAo_code() + "%");
				}
			}

			queryCondition.setQueryString(sb.toString());
			
			//分頁查詢
			ResultIF list = dam.executePaging(queryCondition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			//全部 查詢csv
			List<Map<String, Object>> csvList = dam.exeQuery(queryCondition); // 匯出全部用
		
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			// 進行轉換
			String dateString = sdf.format(inputVO.getsCreDate());
			
			// 全行合計
			List<Map<String, Object>> totalList = getTotal(dam, "total", dateString, inputVO);
			
			// 業務處合計
			List<Map<String, Object>> regionCenterList = getTotal(dam, "center", dateString, inputVO);
						
			// 區域合計
			List<Map<String, Object>> branchAreaList = getTotal(dam, "area", dateString, inputVO);
			
			List<Map<String, Object>> branchNbrList = new ArrayList<Map<String, Object>>();
			if(inputVO.getCheck() != true){
				// 分行合計
			    branchNbrList = getTotalBranch(dam, dateString, inputVO);
//				outputVO.setBranchNbrList(branchNbrList);
			}
			
			
			outputVO.setBranchNbrList(branchNbrList);
			outputVO.setBranchAreaList(branchAreaList);
			outputVO.setRegionCenterList(regionCenterList);
			outputVO.setTotalList(totalList);
			outputVO.setCsvList(csvList);
			outputVO.setResultList(list);
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
	
	private List<Map<String, Object>> getTotal(DataAccessManager dam, String type, String DATA_DATEE, PMS304InputVO inputVO) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(DATA_DATEE);
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();	
		
		if ("total".equals(type)){
			sql2.append(" SELECT ");
		} else if ("center".equals(type)) {
			sql2.append(" SELECT REGION_CENTER_ID, ");
		} else if ("area".equals(type)) {
			sql2.append(" SELECT BRANCH_AREA_ID, ");
		}
		
		sql2.append("		 SUM(IV_DAY_CNT) AS IV_DAY_CNT,             SUM(IV_DAY_PREM_FULL) AS IV_DAY_PREM_FULL,");
		sql2.append("        SUM(IV_DAY_FEE_FULL) AS IV_DAY_FEE_FULL ,  SUM(IV_DAY_FEE_DIS) AS IV_DAY_FEE_DIS, "); 
		sql2.append("        SUM(OT_DAY_CNT) AS OT_DAY_CNT,             SUM(OT_DAY_PREM_FULL) AS OT_DAY_PREM_FULL, ");
		sql2.append("        SUM(OT_DAY_FEE_FULL) AS OT_DAY_FEE_FULL,   SUM(OT_DAY_FEE_DIS) AS OT_DAY_FEE_DIS, ");
		sql2.append("        SUM(SY_DAY_CNT) AS SY_DAY_CNT,             SUM(SY_DAY_PREM_FULL) AS SY_DAY_PREM_FULL, ");
		sql2.append("        SUM(SY_DAY_FEE_FULL) AS SY_DAY_FEE_FULL,   SUM(SY_DAY_FEE_DIS) AS SY_DAY_FEE_DIS, ");
		sql2.append("        SUM(LY_DAY_CNT) AS LY_DAY_CNT,             SUM(LY_DAY_PREM_FULL) AS LY_DAY_PREM_FULL, "); 
		sql2.append("        SUM(LY_DAY_FEE_FULL) AS LY_DAY_FEE_FULL,   SUM(LY_DAY_FEE_DIS) AS LY_DAY_FEE_DIS, ");
		sql2.append("        SUM(SUM_DAY_CNT) AS SUM_DAY_CNT,           SUM(SUM_DAY_PREM_FULL) AS SUM_DAY_PREM_FULL,  ");
		sql2.append("        SUM(SUM_DAY_PREM_DIS) AS SUM_DAY_PREM_DIS, SUM(SUM_DAY_FEE_FULL) AS SUM_DAY_FEE_FULL,   SUM(SUM_DAY_FEE_DIS) AS SUM_DAY_FEE_DIS, ");
		sql2.append("        SUM(IV_MON_CNT) AS IV_MON_CNT,             SUM(IV_MON_PREM_FULL) AS IV_MON_PREM_FULL, ");
		sql2.append("        SUM(IV_MON_FEE_FULL) AS IV_MON_FEE_FULL,   SUM(IV_MON_FEE_DIS) AS IV_MON_FEE_DIS, ");
		sql2.append("        SUM(OT_MON_CNT) AS OT_MON_CNT,             SUM(OT_MON_PREM_FULL) AS OT_MON_PREM_FULL, ");
		sql2.append("        SUM(OT_MON_FEE_FULL) AS OT_MON_FEE_FULL,   SUM(OT_MON_FEE_DIS) AS OT_MON_FEE_DIS, ");
		sql2.append("        SUM(SY_MON_CNT) AS SY_MON_CNT,             SUM(SY_MON_PREM_FULL) AS SY_MON_PREM_FULL, ");
		sql2.append("        SUM(SY_MON_FEE_FULL) AS SY_MON_FEE_FULL,   SUM(SY_MON_FEE_DIS) AS SY_MON_FEE_DIS, ");
		sql2.append("        SUM(LY_MON_CNT) AS LY_MON_CNT,             SUM(LY_MON_PREM_FULL) AS LY_MON_PREM_FULL, ");
		sql2.append("        SUM(LY_MON_FEE_FULL) AS LY_MON_FEE_FULL,   SUM(LY_MON_FEE_DIS) AS LY_MON_FEE_DIS, ");
		sql2.append("        SUM(SUM_MON_CNT) AS SUM_MON_CNT,           SUM(SUM_MON_PREM_FULL) AS SUM_MON_PREM_FULL, ");
		sql2.append("        SUM(SUM_MON_PREM_DIS) AS SUM_MON_PREM_DIS, SUM(SUM_MON_FEE_FULL) AS SUM_MON_FEE_FULL,   SUM(SUM_MON_FEE_DIS) AS SUM_MON_FEE_DIS ");
		sql2.append(" FROM TBPMS_BR_DAY_INS ");
		sql2.append(" WHERE DATA_DATE = :DATA_DATEE ");
		
		condition2.setObject("DATA_DATEE", DATA_DATEE);
		
		if ("center".equals(type)) {
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql2.append(" AND REGION_CENTER_ID = :REGION_CENTER_IDDD ");
				condition2.setObject("REGION_CENTER_IDDD", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql2.append("AND REGION_CENTER_ID IN (:region_center_id) ");
					condition2.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			sql2.append(" GROUP BY REGION_CENTER_ID ");	
			sql2.append(" ORDER BY 1 ASC");
			
		} else if ("area".equals(type)) {
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql2.append(" 	 and BRANCH_AREA_ID = :OP_AREA_IDDD ");
				condition2.setObject("OP_AREA_IDDD", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql2.append("  and BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
					condition2.setObject("OP_AREA_IDDD", pms000outputVO.getV_areaList());
				}else if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
					sql2.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD ");
					condition2.setObject("OP_AREA_IDDD", "%" + inputVO.getRegion_center_id() + "%");
				}
			}
			sql2.append(" GROUP BY BRANCH_AREA_ID ");	
			sql2.append(" ORDER BY 1 ASC");
		}
		
		condition2.setQueryString(sql2.toString());
		resultList = dam.exeQuery(condition2);
		
		return resultList;
	}
	
	private List<Map<String, Object>> getTotalBranch(DataAccessManager dam, String DATA_DATEE, PMS304InputVO inputVO) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(DATA_DATEE);
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();	
		
		sql2.append(" SELECT REGION_CENTER_ID, REGION_CENTER_NAME ");
		sql2.append("      , BRANCH_AREA_ID, BRANCH_AREA_NAME AS BRANCH_AREA_NAME ");
		sql2.append("      , BRANCH_NBR, BRANCH_NAME AS BRANCH_NAME ");
		sql2.append("      , SUM(NVL(OT_DAY_CNT, 0)) AS OT_DAY_CNT, SUM(NVL(OT_DAY_PREM_FULL, 0)) AS OT_DAY_PREM_FULL ");
		sql2.append("      , SUM(NVL(OT_DAY_FEE_FULL, 0)) AS OT_DAY_FEE_FULL, SUM(NVL(OT_DAY_FEE_DIS, 0)) AS OT_DAY_FEE_DIS ");
		sql2.append("      , SUM(NVL(IV_DAY_CNT, 0)) AS IV_DAY_CNT, SUM(NVL(IV_DAY_PREM_FULL, 0)) AS IV_DAY_PREM_FULL ");
		sql2.append("      , SUM(NVL(IV_DAY_FEE_FULL, 0)) AS IV_DAY_FEE_FULL, SUM(NVL(IV_DAY_FEE_DIS, 0)) AS IV_DAY_FEE_DIS ");
		sql2.append("      , SUM(NVL(SY_DAY_CNT, 0)) AS SY_DAY_CNT, SUM(NVL(SY_DAY_PREM_FULL, 0)) AS SY_DAY_PREM_FULL ");
		sql2.append("      , SUM(NVL(SY_DAY_FEE_FULL, 0)) AS SY_DAY_FEE_FULL, SUM(NVL(SY_DAY_FEE_DIS, 0)) AS SY_DAY_FEE_DIS ");
		sql2.append("      , SUM(NVL(LY_DAY_CNT, 0)) AS LY_DAY_CNT, SUM(NVL(LY_DAY_PREM_FULL, 0)) AS LY_DAY_PREM_FULL ");
		sql2.append("      , SUM(NVL(LY_DAY_FEE_FULL, 0)) AS LY_DAY_FEE_FULL, SUM(NVL(LY_DAY_FEE_DIS, 0)) AS LY_DAY_FEE_DIS ");
		sql2.append("      , SUM(NVL(SUM_DAY_CNT, 0)) AS SUM_DAY_CNT, SUM(NVL(SUM_DAY_PREM_FULL, 0)) AS SUM_DAY_PREM_FULL ");
		sql2.append("      , SUM(NVL(SUM_DAY_PREM_DIS, 0)) AS SUM_DAY_PREM_DIS, SUM(NVL(SUM_DAY_FEE_FULL, 0)) AS SUM_DAY_FEE_FULL, SUM(NVL(SUM_DAY_FEE_DIS, 0)) AS SUM_DAY_FEE_DIS ");
		sql2.append("      , SUM(NVL(OT_MON_CNT, 0)) AS OT_MON_CNT, SUM(NVL(OT_MON_PREM_FULL, 0)) AS OT_MON_PREM_FULL ");
		sql2.append("      , SUM(NVL(OT_MON_FEE_FULL, 0)) AS OT_MON_FEE_FULL , SUM(NVL(OT_MON_FEE_DIS, 0)) AS OT_MON_FEE_DIS ");
		sql2.append("      , SUM(NVL(IV_MON_CNT, 0)) AS IV_MON_CNT, SUM(NVL(IV_MON_PREM_FULL, 0)) AS IV_MON_PREM_FULL ");
		sql2.append("      , SUM(NVL(IV_MON_FEE_FULL, 0)) AS IV_MON_FEE_FULL, SUM(NVL(IV_MON_FEE_DIS, 0)) AS IV_MON_FEE_DIS ");
		sql2.append("      , SUM(NVL(SY_MON_CNT, 0)) AS SY_MON_CNT, SUM(NVL(SY_MON_PREM_FULL, 0)) AS SY_MON_PREM_FULL ");
		sql2.append("      , SUM(NVL(SY_MON_FEE_FULL, 0)) AS SY_MON_FEE_FULL, SUM(NVL(SY_MON_FEE_DIS, 0)) AS SY_MON_FEE_DIS ");
		sql2.append("      , SUM(NVL(LY_MON_CNT, 0)) AS LY_MON_CNT, SUM(NVL(LY_MON_PREM_FULL, 0)) AS LY_MON_PREM_FULL ");
		sql2.append("      , SUM(NVL(LY_MON_FEE_FULL, 0)) AS LY_MON_FEE_FULL, SUM(NVL(LY_MON_FEE_DIS, 0)) AS LY_MON_FEE_DIS ");
		sql2.append("      , SUM(NVL(SUM_MON_CNT, 0)) AS SUM_MON_CNT ");
		sql2.append("      , SUM(NVL(SUM_MON_PREM_FULL, 0)) AS SUM_MON_PREM_FULL ");
		sql2.append("      , SUM(NVL(SUM_MON_PREM_DIS, 0)) AS SUM_MON_PREM_DIS ");
		sql2.append("      , SUM(NVL(SUM_MON_FEE_FULL, 0)) AS SUM_MON_FEE_FULL ");
		sql2.append("      , SUM(NVL(SUM_MON_FEE_DIS, 0)) AS SUM_MON_FEE_DIS ");
		sql2.append(" FROM ( SELECT X.*, Z.ROLE_NAME AS ROLE_NAME ");
		sql2.append("        FROM TBPMS_AO_DAY_INS X ");
		sql2.append("        LEFT JOIN VWORG_EMP_INFO Y  ");
		sql2.append("          ON X.EMP_ID = Y.EMP_ID AND X.AO_CODE = Y.AO_CODE "); 
		sql2.append("        LEFT JOIN TBORG_ROLE Z  ");
		sql2.append("          ON Y.ROLE_ID = Z.ROLE_ID ");
		sql2.append("       WHERE DATA_DATE = :DATA_DATEE  "); 
		
		condition2.setObject("DATA_DATEE", DATA_DATEE);
		//區域中心
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql2.append("     and X.REGION_CENTER_ID = :REGION_CENTER_IDDD");
			condition2.setObject("REGION_CENTER_IDDD", inputVO.getRegion_center_id());
		}else{
			//登入非總行人員強制加區域中心
			if(!headmgrMap.containsKey(roleID)) {
				sql2.append(" and X.REGION_CENTER_ID IN (:region_center_id) ");
				condition2.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}
		//營運區
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql2.append("     and X.BRANCH_AREA_ID = :OP_AREA_IDDD");
			condition2.setObject("OP_AREA_IDDD", inputVO.getBranch_area_id() );
		}else{
			//登入非總行人員強制加營運區
			if(!headmgrMap.containsKey(roleID)) {
				sql2.append(" and X.BRANCH_AREA_ID IN (:branch_area_id) ");
				condition2.setObject("branch_area_id", pms000outputVO.getV_areaList());
			}
		}
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql2.append("     and X.BRANCH_NBR = :BRANCH_NBRR");
			condition2.setObject("BRANCH_NBRR", inputVO.getBranch_nbr());
		}else{
			//登入非總行人員強制加分行
			if(!headmgrMap.containsKey(roleID)) {		
				sql2.append(" and X.BRANCH_NBR IN (:branch_nbr) ");
				condition2.setObject("branch_nbr", pms000outputVO.getV_branchList());
			}
		}
		sql2.append("   )");
		sql2.append(" GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME   ");
		sql2.append(" ORDER BY REGION_CENTER_ID ASC, BRANCH_AREA_ID  ASC, BRANCH_NBR ASC ");
		
		condition2.setQueryString(sql2.toString());
		resultList = dam.exeQuery(condition2);
		
		return resultList;
	}
	
	/** ==手收折數查詢== 
	 * @throws ParseException **/
	public void queryDiscount(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException{
		PMS304InputVO inputVO = (PMS304InputVO) body;
		PMS304OutputVO outputVO = new PMS304OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT PARAM_NAME_EDIT,PARAM_DESC ");
			sql.append(" FROM TBSYSPARAMETER ");
			sql.append(" WHERE PARAM_TYPE='PMS.INS_DISCOUNT' ");
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(condition);
			outputVO.setDiscountList(list);
			sendRtnObject(outputVO);
						
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}


	/**
	 * 匯出EXCEL
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException {
		PMS304InputVO inputVO = (PMS304InputVO) body;
		PMS304OutputVO outputVO = new PMS304OutputVO();
        List<Map<String, Object>> list = inputVO.getList();
        Boolean isFC = !inputVO.getCheck();
        
        Calendar busiday = Calendar.getInstance();  
        busiday.setTime(new Date());  
        int day = busiday.get(Calendar.DATE);  

  
        List<String> item = new ArrayList<String>();

        item.add("OT_MON_CNT");					//躉繳(當月)-應達成率"+re+"件數
        item.add("OT_MON_PREM_FULL");			//躉繳(當月)-應達成率"+re+"保費
        item.add("OT_MON_FEE_FULL");			//躉繳(當月)-應達成率"+re+"手收100%
        item.add("OT_MON_FEE_DIS");				//躉繳(當月)-應達成率"+re+"手收85%
        					    
        item.add("IV_MON_CNT");					//投資型(當月)-應達成率"+re+"件數
        item.add("IV_MON_PREM_FULL");			//投資型(當月)-應達成率"+re+"保費
        item.add("IV_MON_FEE_FULL");			//投資型(當月)-應達成率"+re+"手收100%
        item.add("IV_MON_FEE_DIS");				//投資型(當月)-應達成率"+re+"手收85%
        					    
        item.add("SY_MON_CNT");					//短年期繳(當月)-應達成率"+re+"件數
        item.add("SY_MON_PREM_FULL");			//短年期繳(當月)-應達成率"+re+"保費
        item.add("SY_MON_FEE_FULL");			//短年期繳(當月)-應達成率"+re+"手收100%
        item.add("SY_MON_FEE_DIS");				//短年期繳(當月)-應達成率"+re+"手收85%
        					    
        item.add("LY_MON_CNT");					//長年期繳(當月)-應達成率"+re+"件數
        item.add("LY_MON_PREM_FULL");			//長年期繳(當月)-應達成率"+re+"保費
        item.add("LY_MON_FEE_FULL");			//長年期繳(當月)-應達成率"+re+"手收100%
        item.add("LY_MON_FEE_DIS");				//長年期繳(當月)-應達成率"+re+"手收85%
        					    
        item.add("SUM_MON_CNT");				//小計(當月)-應達成率"+re+"件數
        item.add("SUM_MON_PREM_FULL");			//小計(當月)-應達成率"+re+"保費
        item.add("SUM_MON_PREM_DIS");			//小計(當月)-應達成率"+re+"保費
        item.add("SUM_MON_FEE_FULL");			//小計(當月)-應達成率" +re+"實際手收100%
        item.add("SUM_MON_FEE_DIS");			//小計(當月)-應達成率" +re+"實際手收85%
        
        item.add("OT_DAY_CNT"); 				//躉繳(當日)件數
        item.add("OT_DAY_PREM_FULL"); 			//躉繳(當日)保費
        item.add("OT_DAY_FEE_FULL");			//躉繳(當日)手收100%
        item.add("OT_DAY_FEE_DIS");				//躉繳(當日)手收85%
        					    
        item.add("IV_DAY_CNT"); 				//投資型(當日)件數
        item.add("IV_DAY_PREM_FULL"); 			//投資型(當日)保費
        item.add("IV_DAY_FEE_FULL");			//投資型(當日)手收100%
        item.add("IV_DAY_FEE_DIS");				//投資型(當日)手收85%
        					    
        item.add("SY_DAY_CNT");					//短年期繳(當日)件數
        item.add("SY_DAY_PREM_FULL");			//短年期繳(當日)保費
        item.add("SY_DAY_FEE_FULL");			//短年期繳(當日)手收100%
        item.add("SY_DAY_FEE_DIS");				//短年期繳(當日)手收85%
        					    
        item.add("LY_DAY_CNT");					//長年期繳(當日)件數
        item.add("LY_DAY_PREM_FULL");			//長年期繳(當日)保費
        item.add("LY_DAY_FEE_FULL");			//長年期繳(當日)手收100%
        item.add("LY_DAY_FEE_DIS");				//長年期繳(當日)手收85%
        					    
        item.add("SUM_DAY_CNT");				//小計(當日)件數
        item.add("SUM_DAY_PREM_FULL");			//小計(當日)保費100%
        item.add("SUM_DAY_PREM_DIS");			//小計(當日)保費85%
        item.add("SUM_DAY_FEE_FULL");			//小計(當日)手收100%
        item.add("SUM_DAY_FEE_DIS");			//小計(當日)手收85%
        					     
	    List<String> headerL = new ArrayList<String>();
         
        headerL.add("業務處");
        headerL.add("營運區"); 
        headerL.add("分行代號"); 
        headerL.add("營業單位");
        headerL.add("組別");
        headerL.add("躉繳(當月)");
        headerL.add("躉繳(當月)");
        headerL.add("躉繳(當月)");
        headerL.add("躉繳(當月)");
        headerL.add("投資型(當月)");
        headerL.add("投資型(當月)");
        headerL.add("投資型(當月)");
        headerL.add("投資型(當月)");
        headerL.add("短年期繳(當月)");
        headerL.add("短年期繳(當月)");
        headerL.add("短年期繳(當月)");
        headerL.add("短年期繳(當月)");
        headerL.add("長年期繳(當月)");
        headerL.add("長年期繳(當月)");
        headerL.add("長年期繳(當月)");
        headerL.add("長年期繳(當月)");
        headerL.add("小計(當月)");
        headerL.add("小計(當月)");
        headerL.add("小計(當月)");
        headerL.add("小計(當月)");
        headerL.add("小計(當月)");
        headerL.add("躉繳(當日)");
        headerL.add("躉繳(當日)"); 
        headerL.add("躉繳(當日)");
        headerL.add("躉繳(當日)");
        headerL.add("投資型(當日)");
        headerL.add("投資型(當日)");
        headerL.add("投資型(當日)");
        headerL.add("投資型(當日)");
        headerL.add("短年期繳(當日)"); 
        headerL.add("短年期繳(當日)"); 
        headerL.add("短年期繳(當日)");
        headerL.add("短年期繳(當日)");
        headerL.add("長年期繳(當日)"); 
        headerL.add("長年期繳(當日)"); 
        headerL.add("長年期繳(當日)"); 
        headerL.add("長年期繳(當日)");
        headerL.add("小計(當日)"); 
        headerL.add("小計(當日)");
        headerL.add("小計(當日)");
        headerL.add("小計(當日)");
        headerL.add("小計(當日)");
		
        int line2Flag = 5;
        if (isFC) {
        	headerL.add(5,"專員別");
        	headerL.add(6,"招攬人員員編");
        	headerL.add(7,"招攬人員姓名");
        	line2Flag = 8;
        }
        
        String[] headerLine1 = headerL.toArray(new String[50]);
        
        dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PARAM_CODE, PARAM_NAME_EDIT from TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.INS_DISCOUNT' ");
		condition.setQueryString(sql.toString());       
		List<Map<String,Object>> disList = dam.exeQuery(condition);
		
		int insDisInt = 0;
		int feeDisInt = 0;
		for (Map<String,Object> map : disList) {
			if (map.get("PARAM_CODE") != null && "1".equals(map.get("PARAM_CODE")) && map.get("PARAM_NAME_EDIT") != null)
				insDisInt = new BigDecimal(map.get("PARAM_NAME_EDIT").toString()).multiply(new BigDecimal(100)).intValue();				
			
			if (map.get("PARAM_CODE") != null && "2".equals(map.get("PARAM_CODE")) && map.get("PARAM_NAME_EDIT") != null)
				feeDisInt = new BigDecimal(map.get("PARAM_NAME_EDIT").toString()).multiply(new BigDecimal(100)).intValue();				
		}
		String insDis = Integer.toString(insDisInt) + "%";
		String feeDis = Integer.toString(feeDisInt) + "%"; 		
      		
        List<String> headerLine2 = new ArrayList<String>();
        
        for (int i = 0; i < line2Flag; i++) {
        	headerLine2.add("");        	
        }
        
        for (int i = 0; i < 10; i++) {
        	if (i == 4 || i == 9) {
        		headerLine2.add("件數");
        		headerLine2.add("保費 100%");
        		headerLine2.add("保費 " + insDis);
        		headerLine2.add("手收 100%");
        		headerLine2.add("手收 " + feeDis);
        	} else {
        		headerLine2.add("件數");
        		headerLine2.add("保費");
        		headerLine2.add("手收 100%");
        		headerLine2.add("手收 " + feeDis);
        	}
        }
		
		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");	
		String fileName = "分行保險速報名細表計_" + sdfYYYYMMDD.format(new Date())+ "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";


			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("分行保險速報名細表計_"+ sdfYYYYMMDD.format(new Date()));
			sheet.setDefaultColumnWidth(30);
			
			XSSFCellStyle style = workbook.createCellStyle();
			XSSFCellStyle STYLE = workbook.createCellStyle();

			style.setAlignment(XSSFCellStyle.ALIGN_CENTER); 				// 水平置中
			style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); 		// 垂直置中

			STYLE.setAlignment(XSSFCellStyle.ALIGN_CENTER); 				// 水平置中
			STYLE.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); 		// 垂直置中
			STYLE.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			STYLE.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			STYLE.setBorderBottom((short) 1);
			STYLE.setBorderTop((short) 1);
			STYLE.setBorderLeft((short) 1);
			STYLE.setBorderRight((short) 1);
			Integer index = 0; // first row
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); // 比對用

			XSSFRow row = sheet.createRow(index);
			for (int i = 0; i < headerLine1.length; i++) {
				String headerLine = headerLine1[i];
				if (tempList.indexOf(headerLine) < 0) {
					tempList.add(headerLine);
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(STYLE);
					cell.setCellValue(headerLine1[i]);

					if (endFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
					}
					startFlag = i;
					endFlag = 0;
				} else {
					endFlag = i;
				}
			}
			if (endFlag != 0) { // 最後的CELL若需要合併儲存格，則在這裡做
				sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
			}

			index++; // next row
			row = sheet.createRow(index);
			for (int i = 0; i < headerLine2.size(); i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(STYLE);
				cell.setCellValue(headerLine2.get(i));

				if ("".equals(headerLine2.get(i))) {
					sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
				}
			}

			index++;

			String centerName = "";
			String areaName = "";
			String branchName = "";
			String centerID = "";
			String areaID = "";
			String branchNbr = "";
			if (list.size() > 0) {
				centerName = list.get(0).get("REGION_CENTER_NAME").toString();
				areaName = list.get(0).get("BRANCH_AREA_NAME").toString();
				branchName = list.get(0).get("BRANCH_NAME").toString();
				centerID = list.get(0).get("REGION_CENTER_ID").toString();
				areaID = list.get(0).get("BRANCH_AREA_ID").toString();
				branchNbr = list.get(0).get("BRANCH_NBR").toString();
			}
			
			List<Map<String, Object>> totalList = inputVO.getTotalList();
			List<Map<String, Object>> centerList = inputVO.getCenterList();
			List<Map<String, Object>> areaList = inputVO.getAreaList();
			List<Map<String, Object>> branchList = inputVO.getBranchList();
			
			int listCnt = 0;
			for (Map<String, Object> map : list) {
				listCnt++;
				
				Boolean changeBranch = false;
				if (isFC) {
					if (map.get("BRANCH_NBR") != null && !branchNbr.equals(map.get("BRANCH_NBR").toString())) {
						changeBranch = true;
					}
				}
				
				Boolean changeArea = false;
				if (map.get("BRANCH_AREA_NAME") != null && !areaName.equals(map.get("BRANCH_AREA_NAME").toString())) {
					changeArea = true;
				}
				
				Boolean changeCenter = false;
				if (map.get("REGION_CENTER_NAME") != null && !centerName.equals(map.get("REGION_CENTER_NAME").toString())) {
					changeCenter = true;
				}
				
				//分行變換
				if (changeBranch) {
					for (Map<String, Object> branchMap : branchList) {
						if (branchMap.get("BRANCH_NBR").toString().equals(branchNbr)){
							countBranchTotal(row, sheet, style, index, centerName, areaName, branchName, branchMap, item);							
						}
					}
					branchName = map.get("BRANCH_NAME").toString();
					branchNbr = map.get("BRANCH_NBR").toString();
					index++;
				}
				
				//營運區變換
				if (changeArea) {
					for (Map<String, Object> areaMap : areaList) {
						if (areaMap.get("BRANCH_AREA_ID").toString().equals(areaID)){
							countAreaTotal(row, sheet, style, index, centerName, areaName, areaMap, item, isFC);							
						}
					}
					areaName = map.get("BRANCH_AREA_NAME").toString();
					areaID = map.get("BRANCH_AREA_ID").toString();
					index++;
				}
				
				//業務處變換
				if (changeCenter) {
					for (Map<String, Object> centerMap : centerList) {
						if (centerMap.get("REGION_CENTER_ID").toString().equals(centerID)){
							countCenterTotal(row, sheet, style, index, centerName, centerMap, item, isFC);							
						}
					}
					centerName = map.get("REGION_CENTER_NAME").toString();
					centerID = map.get("REGION_CENTER_ID").toString();
					index++;
				}
				
				row = sheet.createRow(index);
				int j = 0;
				XSSFCell cell = row.createCell(j);
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "REGION_CENTER_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_NBR"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "GROUP_ID"));
				
				if (isFC) {
					cell = row.createCell(j++);
					cell.setCellStyle(style);
					cell.setCellValue(checkIsNull(map, "ROLE_NAME"));
					cell = row.createCell(j++);
					cell.setCellStyle(style);
					cell.setCellValue(checkIsNull(map, "EMP_ID"));
					cell = row.createCell(j++);
					cell.setCellStyle(style);
					cell.setCellValue(checkIsNull(map, "EMP_NAME"));
				}
				
				for(String key : item) {
					cell = row.createCell(j++);
					cell.setCellStyle(style);
					
					BigDecimal value = new BigDecimal(0);
					if (map.get(key) != null) {
						value = new BigDecimal(map.get(key).toString());
					}
					cell.setCellValue(value.toPlainString());				
				}
				
				index++;
				//最後一筆
				if (listCnt == list.size()) {
					if (isFC) {
						// 分行合計
						for (Map<String, Object> branchMap : branchList) {
							if (branchMap.get("BRANCH_NBR").toString().equals(branchNbr)){
								countBranchTotal(row, sheet, style, index, centerName, areaName, branchName, branchMap, item);							
							}
						}						
						index++;
					}
					
					// 區合計
					for (Map<String, Object> areaMap : areaList) {
						if (areaMap.get("BRANCH_AREA_ID").toString().equals(areaID)){
							countAreaTotal(row, sheet, style, index, centerName, areaName, areaMap, item, isFC);							
						}
					}
					index++;
					
					//處合計
					for (Map<String, Object> centerMap : centerList) {
						if (centerMap.get("REGION_CENTER_ID").toString().equals(centerID)){
							countCenterTotal(row, sheet, style, index, centerName, centerMap, item, isFC);							
						}
					}
					index++;
					
					// 全行合計
					countCenterTotal(row, sheet, style, index, "全行", totalList.get(0), item, isFC);
				}
			}
			
			String tempName = UUID.randomUUID().toString();
			File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
			workbook.write(new FileOutputStream(f)); //絕對路徑建檔
			notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName); //相對路徑取檔

	}
	
	// FOR 分行合計
	private void countBranchTotal(XSSFRow row, XSSFSheet sheet, XSSFCellStyle style, int index, String centerName, String areaName, String branchName, Map<String, Object> branchMap, List<String> item) {
		row = sheet.createRow(index);
		int i = 0;
		XSSFCell cell = row.createCell(i);
		cell = row.createCell(i++);
		cell.setCellStyle(style);
		cell.setCellValue(centerName);
		
		cell = row.createCell(i++);
		cell.setCellStyle(style);
		cell.setCellValue(areaName);
		
		cell = row.createCell(i++);
		cell.setCellStyle(style);
		cell.setCellValue(branchName + " 合計");
		cell = row.createCell(i++);
		cell = row.createCell(i++);
		cell = row.createCell(i++);
		cell = row.createCell(i++);
		cell = row.createCell(i++);
		
		for (String key : item){
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			
			BigDecimal value = new BigDecimal(0);
			if (branchMap.get(key) != null) {
				value = new BigDecimal(branchMap.get(key).toString());
			}
			cell.setCellValue(value.toPlainString());				
		}
		sheet.addMergedRegion(new CellRangeAddress(index, index, 2, 7)); // firstRow, endRow, firstColumn, endColumn			
	}
	
	// FOR 區合計
	private void countAreaTotal(XSSFRow row, XSSFSheet sheet, XSSFCellStyle style, int index, String centerName, String areaName, Map<String, Object> areaMap, List<String> item, Boolean isFC) {
		row = sheet.createRow(index);
		int i = 0;
		XSSFCell areaTalCell = row.createCell(i);
		areaTalCell = row.createCell(i++);
		areaTalCell.setCellStyle(style);
		areaTalCell.setCellValue(centerName);
		
		areaTalCell = row.createCell(i++);
		areaTalCell.setCellStyle(style);
		areaTalCell.setCellValue(areaName + " 合計");
		areaTalCell = row.createCell(i++);
		areaTalCell = row.createCell(i++);
		areaTalCell = row.createCell(i++);
		
		if (isFC) {
			areaTalCell = row.createCell(i++);
			areaTalCell = row.createCell(i++);
			areaTalCell = row.createCell(i++);
		}
		
		for (String key : item){
			areaTalCell = row.createCell(i++);
			areaTalCell.setCellStyle(style);
			
			BigDecimal value = new BigDecimal(0);
			if (areaMap.get(key) != null) {
				value = new BigDecimal(areaMap.get(key).toString());
			}
			areaTalCell.setCellValue(value.toPlainString());				
		}
		
		if (isFC) {
			sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 7)); // firstRow, endRow, firstColumn, endColumn
		} else {
			sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4)); // firstRow, endRow, firstColumn, endColumn			
		}
	}
	
	// FOR 處&全行合計
	private void countCenterTotal(XSSFRow row, XSSFSheet sheet, XSSFCellStyle style, int index, String centerName, Map<String, Object> centerMap, List<String> item, Boolean isFC) {
		row = sheet.createRow(index);
		int i = 0;
		XSSFCell centerTalCell = row.createCell(i);
		centerTalCell = row.createCell(i++);
		centerTalCell.setCellStyle(style);
		centerTalCell.setCellValue(centerName + " 合計");
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);
		
		if (isFC) {
			centerTalCell = row.createCell(i++);
			centerTalCell = row.createCell(i++);
			centerTalCell = row.createCell(i++);
		}
		
		for (String key : item){
			centerTalCell = row.createCell(i++);
			centerTalCell.setCellStyle(style);
			
			BigDecimal value = new BigDecimal(0);
			if (centerMap.get(key) != null) {
				value = new BigDecimal(centerMap.get(key).toString());
			}
			centerTalCell.setCellValue(value.toPlainString());
		}
		
		if (isFC) {
			sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 7)); // firstRow, endRow, firstColumn, endColumn
		} else {
			sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 4)); // firstRow, endRow, firstColumn, endColumn			
		}
	}
	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if(map.get(key)==null)
			return "";
				return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	private String checkIsNu(Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if(map.get(key)==null){
				return "0";
			}
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}

	/**
	 * 取得temp資料夾絕對路徑
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String getTempPath() throws JBranchException {
		String serverPath = (String) getCommonVariable(SystemVariableConsts.SERVER_PATH);
		String seperator = System.getProperties().getProperty("file.separator");
		if (!serverPath.endsWith(seperator)) {
			serverPath += seperator;
		}
		return serverPath + "temp";
	}
}
