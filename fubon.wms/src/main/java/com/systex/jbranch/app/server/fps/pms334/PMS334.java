package com.systex.jbranch.app.server.fps.pms334;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * Description :分行最適經營客戶管理報表Controller <br>
 * Comments Name : PMS334.java<br>
 * Author :Kevin<br>
 * Date :2016年06月06日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
@Component("pms334")
@Scope("request")
public class PMS334 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS334.class);

	/**
	 * 匯出NULL判斷
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
				return String.valueOf(map.get(key));
			
		} else {
			return "";
		}
	}
	private String checkIsNull2(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
				return String.valueOf(map.get(key));
			
		} else {
			return "0";
		}
	}
	

	/**
	 * 匯出
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 輸入VO
		PMS334InputVO inputVO = (PMS334InputVO) body;
		
//		inputVO.setAsc(asc);
//		inputVO.setBr_id(br_id);
//		inputVO.setCheckin(checkin);
//		inputVO.setColumn(column);
//		inputVO.setCurrentPageIndex(currentPageIndex);
//		inputVO.setCUST_DEGREE(cUST_DEGREE);
//		inputVO.setCUST_ID(cUST_ID);
//		inputVO.setDataMonth(dataMonth);
//		inputVO.setEmp_id(emp_id);
//		inputVO.setIND(iND);
//		inputVO.setOp_id(op_id);
//		inputVO.setPageCount(pageCount);
//		inputVO.setRc_id(rc_id);
//		inputVO.setVIP_DEGREE(vIP_DEGREE);
		
		// 輸出VO
		PMS334OutputVO outputVO = new PMS334OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
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
			pms000InputVO.setReportDate(inputVO.getReportDate());
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			// ==主查詢==
			sql.append("SELECT RPT.* FROM TBPMS_OP_CUST_MGMT_RPT RPT ");
			// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
			sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
			sql.append("ON ORG.BRANCH_NBR=RPT.BRANCH_NBR ");
			sql.append("WHERE 1=1  ");
			// ==主查詢條件==
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"".equals(inputVO.getRegion_center_id())) {
				sql.append(" and ORG.REGION_CENTER_ID = :region_center_id ");
				condition.setObject("region_center_id", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and ORG.REGION_CENTER_ID IN (:region_center_id) ");
					condition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
				sql.append(" and ORG.BRANCH_AREA_ID = :branch_area_id ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("  and ORG.BRANCH_AREA_ID IN (:branch_area_id) ");
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
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
			// 員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and RPT.AO_CODE = :ao_codee");
				condition.setObject("ao_codee", inputVO.getAo_code());
			}else if(!StringUtils.isBlank(inputVO.getEmp_id()) && !"".equals(inputVO.getEmp_id())){
				sql.append(" and RPT.AO_CODE = :ao_codee ");
				condition.setObject("ao_codee", inputVO.getEmp_id());
			}
			// 年月
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				sql.append(" and YEARMON LIKE :YEARMONN");
				condition.setObject("YEARMONN", "%" + inputVO.getsCreDate() + "%");
			}
			// 排序
			sql.append(" ORDER BY RPT.REGION_CENTER_ID, RPT.BRANCH_AREA_ID , RPT.BRANCH_NBR , RPT.AO_CODE ");
			condition.setQueryString(sql.toString());
			// 匯出CSV
			List<Map<String, Object>> list = dam.exeQuery(condition);

			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "最適經營客戶" + sdf.format(new Date())
						+ "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) +  ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[31];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON"); // 資料統計月份
//					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心名稱
//					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
					records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); // 分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[++i] = checkIsNullAndTrans(map, "AO_JOB_RANK"); // 理專等級
					records[++i] = checkIsNullAndTrans(map, "AO_CODE"); // AO CODE
					records[++i] = checkIsNullAndTrans(map, "EMP_ID"); // 員編
					records[++i] = currencyFormat(map, "MAX_CUST_CNT_TOT"); // 客戶數(+10%)
					records[++i] = currencyFormat(map, "MAX_AUM"); // AUM
					records[++i] = currencyFormat(map, "TOT_CUST_CNT"); // 總戶數
					records[++i] = currencyFormat(map, "TOT_CUST_PCTG"); // 戶數佔比
					records[++i] = currencyFormat(map, "TOT_NOT_IN_CTRL"); // 不計入總量控管戶數
					records[++i] = currencyFormat(map, "TOT_AUM"); // AUM
					records[++i] = currencyFormat(map, "TOT_CUST_DIFF"); // 與最適客戶數差異
					records[++i] = currencyFormat(map, "V_CUST_CNT"); // 戶數
					records[++i] = currencyFormat(map, "V_NOT_IN_CTRL"); // 計入總量控管戶數
					records[++i] = currencyFormat(map, "V_AUM"); // AUM
					records[++i] = currencyFormat(map, "V_CUST_DIFF"); // 與最適客戶數差異
					records[++i] = currencyFormat(map, "A_CUST_CNT"); // 戶數
					records[++i] = currencyFormat(map, "A_NOT_IN_CTRL"); // 不計入總量控管戶數
					records[++i] = currencyFormat(map, "A_AUM"); // AUM
					records[++i] = currencyFormat(map, "A_CUST_DIFF"); // 與最適客戶數差異
					records[++i] = currencyFormat(map, "B_CUST_CNT"); // 戶數
					records[++i] = currencyFormat(map, "B_NOT_IN_CTRL"); // 不計入總量控管戶數
					records[++i] = currencyFormat(map, "B_AUM"); // AUM
					records[++i] = currencyFormat(map, "B_CUST_DIFF"); // 與最適客戶數差異
					records[++i] = currencyFormat(map, "M_CUST_CNT"); //
					records[++i] = currencyFormat(map, "M_NOT_IN_CTRL"); // 與最適客戶數差異
					records[++i] = currencyFormat(map, "M_AUM"); // AUM
					records[++i] = currencyFormat(map, "M_CUST_DIFF"); // 不計入總量控管戶數
					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[31];
				int j = 0;
				csvHeader[j] = "資料統計月份";
//				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
//				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "理專等級";
				csvHeader[++j] = "AO CODE";
				csvHeader[++j] = "員編";
				csvHeader[++j] = "上限-客戶數(+10%)";
				csvHeader[++j] = "上限-AUM";

				csvHeader[++j] = "轄下-總戶數";
				csvHeader[++j] = "轄下-戶數佔比";
				csvHeader[++j] = "轄下-不計入總量控管戶數";
				csvHeader[++j] = "轄下-AUM";
				csvHeader[++j] = "轄下-與最適客戶數差異";

				csvHeader[++j] = "私人-戶數";
				csvHeader[++j] = "私人-計入總量控管戶數";
				csvHeader[++j] = "私人-AUM";
				csvHeader[++j] = "私人-與最適客戶數差異";

				csvHeader[++j] = "白金-戶數";
				csvHeader[++j] = "白金-不計入總量控管戶數";
				csvHeader[++j] = "白金-AUM";
				csvHeader[++j] = "白金-與最適客戶數差異";

				csvHeader[++j] = "個人-戶數";
				csvHeader[++j] = "個人-不計入總量控管戶數";
				csvHeader[++j] = "個人-AUM";
				csvHeader[++j] = "個人-與最適客戶數差異";

				csvHeader[++j] = "潛力-戶數";
				csvHeader[++j] = "潛力-不計入總量控管";
				csvHeader[++j] = "潛力-AUM";
				csvHeader[++j] = "潛力-與最適客戶數差";

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else
				this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		// 輸入VO
		PMS334InputVO inputVO = (PMS334InputVO) body;
		// 輸出VO
		PMS334OutputVO outputVO = new PMS334OutputVO();
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
		
		
		if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				pms000InputVO.setReportDate(inputVO.getsCreDate());
		}
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
	
	
		
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			// ==預先兜資料，主查詢(前段)==
			sql.append("WITH ");
			sql.append("ORIGINAL_VIEW AS ( ");
			sql.append("  select ");
			sql.append("  tbpms.REGION_CENTER_NAME,        ");     
			sql.append("  tbpms.BRANCH_AREA_NAME   , ");
			sql.append("  tbpms.BRANCH_NAME , ");
			sql.append("''   AS EMP_NAME,");
			sql.append("  tbpms.REGION_CENTER_ID     , ");
			sql.append("  tbpms.BRANCH_AREA_ID   , ");
			sql.append("  tbpms.BRANCH_NBR         , ");
			sql.append("  tbpms.AO_CODE        , ");
			sql.append("  tbpms.YEARMON        , ");
			sql.append("  tbpms.AO_JOB_RANK            , ");
			sql.append("  tbpms.EMP_ID             , ");
			sql.append("  tbpms.MAX_CUST_CNT_TOT   , ");
			sql.append("  tbpms.MAX_CUST_CNT_V     , ");
			sql.append("  tbpms.MAX_CUST_CNT_A     , ");
			sql.append("  tbpms.MAX_CUST_CNT_B     , ");
			sql.append("  tbpms.MAX_CUST_CNT_M     , ");
			sql.append("  tbpms.MAX_AUM            , ");
			sql.append("  tbpms.TOT_CUST_CNT       , ");
			sql.append("  tbpms.TOT_CUST_PCTG      , ");
			sql.append("  tbpms.TOT_NOT_IN_CTRL    , ");
			sql.append("  tbpms.TOT_AUM            , ");
			sql.append("  tbpms.TOT_CUST_DIFF      , ");
			sql.append("  tbpms.V_CUST_CNT         , ");
			sql.append("  tbpms.V_NOT_IN_CTRL      , ");
			sql.append("  tbpms.V_AUM              , ");
			sql.append("  tbpms.V_CUST_DIFF        , ");
			sql.append("  tbpms.A_CUST_CNT         , ");
			sql.append("  tbpms.A_NOT_IN_CTRL      , ");
			sql.append("  tbpms.A_AUM              , ");
			sql.append("  tbpms.A_CUST_DIFF        , ");
			sql.append("  tbpms.B_CUST_CNT         , ");
			sql.append("  tbpms.B_NOT_IN_CTRL      , ");
			sql.append("  tbpms.B_AUM              , ");
			sql.append("  tbpms.B_CUST_DIFF        , ");
			sql.append("  tbpms.M_CUST_CNT         , ");
			sql.append("  tbpms.M_NOT_IN_CTRL      , ");
			sql.append("  tbpms.M_AUM              , ");
			sql.append("  tbpms.M_CUST_DIFF          ");
			sql.append("  from  TBPMS_OP_CUST_MGMT_RPT tbpms ");
			// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
			sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
			sql.append("ON ORG.BRANCH_NBR=tbpms.BRANCH_NBR ");
			sql.append("  where 1=1  ");
			// ==主查詢條件==
			// 日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				sql.append(" and tbpms.YEARMON =:STARTTIME ");
				condition.setObject("STARTTIME", inputVO.getsCreDate());	
			}
		
		
			// AO_COCE
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append("and tbpms.AO_CODE = :ao_code ");
				condition.setObject("ao_code", inputVO.getAo_code());
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append("and tbpms.AO_CODE IN (:ao_code) ");
					condition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("and ORG.BRANCH_NBR = :branch_nbr ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and ORG.BRANCH_NBR IN (:branch_nbr) ");
					//pms000outputVO.getBranchList
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("and ORG.BRANCH_AREA_ID = :branch_area_id ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and ORG.BRANCH_AREA_ID IN (:branch_area_id) ");
					//pms000outputVO.getArea_branchList
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("and ORG.REGION_CENTER_ID = :region_center_id ");
				condition.setObject("region_center_id", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and ORG.REGION_CENTER_ID IN (:region_center_id) ");
					//pms000outputVO.getRegionList
					condition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			sql.append("), ");

			// ==預先兜資料(後段)==
			sql.append("BRANCH AS ( ");
			sql.append("  select REGION_CENTER_NAME,BRANCH_AREA_NAME,BRANCH_NAME,'BRANCH' as EMP_NAME,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR,'ZZZZZBRANCH' as AO_CODE,'BRANCH' as YEARMON,'BRANCH' as AO_JOB_RANK,'BRANCH' as EMP_ID, ");
			sql.append("   SUM(MAX_CUST_CNT_TOT) AS MAX_CUST_CNT_TOT, ");  //(A)
			sql.append("   SUM(MAX_CUST_CNT_V) AS MAX_CUST_CNT_V,     ");
			sql.append("   SUM(MAX_CUST_CNT_A) AS MAX_CUST_CNT_A,     ");
			sql.append("   SUM(MAX_CUST_CNT_B) AS MAX_CUST_CNT_B,     ");
			sql.append("   SUM(MAX_CUST_CNT_M) AS MAX_CUST_CNT_M,     ");
			sql.append("   SUM(MAX_AUM ) AS MAX_AUM ,                 ");
			sql.append("   SUM(TOT_CUST_CNT) AS TOT_CUST_CNT,         ");   //(B)
			sql.append("   ROUND((SUM(TOT_CUST_CNT)-SUM(TOT_NOT_IN_CTRL))/SUM(MAX_CUST_CNT_TOT),2)*100 AS TOT_CUST_PCTG,     ");   //((B)-(C))/(A)
			sql.append("   SUM(TOT_NOT_IN_CTRL ) AS TOT_NOT_IN_CTRL , ");   //(C)
			sql.append("   SUM(TOT_AUM ) AS TOT_AUM ,                 ");   
			sql.append("   SUM(TOT_CUST_CNT)-SUM(TOT_NOT_IN_CTRL)-SUM(MAX_CUST_CNT_TOT) AS TOT_CUST_DIFF,     ");   //(B)-(C)-(A) TOT_CUST_DIFF
			sql.append("   SUM(V_CUST_CNT) AS V_CUST_CNT,             ");
			sql.append("   SUM(V_NOT_IN_CTRL ) AS V_NOT_IN_CTRL ,     ");
			sql.append("   SUM(V_AUM ) AS V_AUM ,                     ");
			sql.append("   SUM(V_CUST_DIFF ) AS V_CUST_DIFF ,         ");
			sql.append("   SUM(A_CUST_CNT) AS A_CUST_CNT,             ");
			sql.append("   SUM(A_NOT_IN_CTRL ) AS A_NOT_IN_CTRL ,     ");
			sql.append("   SUM(A_AUM ) AS A_AUM ,                     ");
			sql.append("   SUM(A_CUST_DIFF ) AS A_CUST_DIFF ,         ");
			sql.append("   SUM(B_CUST_CNT) AS B_CUST_CNT,             ");
			sql.append("   SUM(B_NOT_IN_CTRL ) AS B_NOT_IN_CTRL ,     ");
			sql.append("   SUM(B_AUM ) AS B_AUM ,                     ");
			sql.append("   SUM(B_CUST_DIFF ) AS B_CUST_DIFF ,         ");
			sql.append("   SUM(M_CUST_CNT) AS M_CUST_CNT,             ");
			sql.append("   SUM(M_NOT_IN_CTRL ) AS M_NOT_IN_CTRL ,     ");
			sql.append("   SUM(M_AUM ) AS M_AUM ,                     ");
			sql.append("   SUM(M_CUST_DIFF ) AS M_CUST_DIFF          ");
			sql.append("  from ORIGINAL_VIEW ");
			sql.append("  group by REGION_CENTER_NAME,BRANCH_AREA_NAME,BRANCH_NAME,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR ");
			sql.append("), ");
			sql.append("AREA AS ( ");
			sql.append("  select REGION_CENTER_NAME,BRANCH_AREA_NAME,'AREA' as BRANCH_NAME,'AREA' as EMP_NAME,REGION_CENTER_ID,BRANCH_AREA_ID,'AREA' as BRANCH_NBR,'ZZZZZAREA' as AO_CODE,'AREA' as YEARMON,'AREA' as AO_JOB_RANK,'AREA' as EMP_ID,		");
			sql.append("   SUM(max_cust_cnt_tot) AS MAX_CUST_CNT_TOT,SUM(MAX_CUST_CNT_V) AS MAX_CUST_CNT_V,     ");
			sql.append("   SUM(MAX_CUST_CNT_A) AS MAX_CUST_CNT_A,     ");
			sql.append("   SUM(MAX_CUST_CNT_B) AS MAX_CUST_CNT_B,     ");
			sql.append("   SUM(MAX_CUST_CNT_M) AS MAX_CUST_CNT_M,     ");
			sql.append("   SUM(MAX_AUM ) AS MAX_AUM ,                 ");
			sql.append("   SUM(TOT_CUST_CNT) AS TOT_CUST_CNT,         ");
			sql.append("   ROUND((SUM(TOT_CUST_CNT)-SUM(TOT_NOT_IN_CTRL))/SUM(MAX_CUST_CNT_TOT),2)*100 AS TOT_CUST_PCTG,     ");   //((B)-(C))/(A)
			sql.append("   SUM(TOT_NOT_IN_CTRL ) AS TOT_NOT_IN_CTRL , ");
			sql.append("   SUM(TOT_AUM ) AS TOT_AUM ,                 ");   
			sql.append("   SUM(TOT_CUST_CNT)-SUM(TOT_NOT_IN_CTRL)-SUM(MAX_CUST_CNT_TOT) AS TOT_CUST_DIFF,     ");   //(B)-(C)-(A) TOT_CUST_DIFF
			sql.append("   SUM(V_CUST_CNT) AS V_CUST_CNT,             ");
			sql.append("   SUM(V_NOT_IN_CTRL ) AS V_NOT_IN_CTRL ,     ");
			sql.append("   SUM(V_AUM ) AS V_AUM ,                     ");
			sql.append("   SUM(V_CUST_DIFF ) AS V_CUST_DIFF ,         ");
			sql.append("   SUM(A_CUST_CNT) AS A_CUST_CNT,             ");
			sql.append("   SUM(A_NOT_IN_CTRL ) AS A_NOT_IN_CTRL ,     ");
			sql.append("   SUM(A_AUM ) AS A_AUM ,                     ");
			sql.append("   SUM(A_CUST_DIFF ) AS A_CUST_DIFF ,         ");
			sql.append("   SUM(B_CUST_CNT) AS B_CUST_CNT,             ");
			sql.append("   SUM(B_NOT_IN_CTRL ) AS B_NOT_IN_CTRL ,     ");
			sql.append("   SUM(B_AUM ) AS B_AUM ,                     ");
			sql.append("   SUM(B_CUST_DIFF ) AS B_CUST_DIFF ,         ");
			sql.append("   SUM(M_CUST_CNT) AS M_CUST_CNT,             ");
			sql.append("   SUM(M_NOT_IN_CTRL ) AS M_NOT_IN_CTRL ,     ");
			sql.append("   SUM(M_AUM ) AS M_AUM ,                     ");
			sql.append("   SUM(M_CUST_DIFF ) AS M_CUST_DIFF          ");
			sql.append("  from BRANCH ");
			sql.append("  group by REGION_CENTER_NAME,BRANCH_AREA_NAME,REGION_CENTER_ID,BRANCH_AREA_ID ");
			sql.append("), ");
			sql.append("REGION AS ( ");
			sql.append("  select REGION_CENTER_NAME,'REGION' as BRANCH_AREA_NAME,'REGION' as BRANCH_NAME,'REGION' as EMP_NAME,REGION_CENTER_ID,'REGION' as BRANCH_AREA_ID,'REGION' as BRANCH_NBR,'ZZZZZREGION' as AO_CODE,'REGION' as YEARMON,'REGION' as AO_JOB_RANK,'REGION' as EMP_ID,     ");
			sql.append("   SUM(max_cust_cnt_tot) AS MAX_CUST_CNT_TOT,SUM(MAX_CUST_CNT_V) AS MAX_CUST_CNT_V,SUM(MAX_CUST_CNT_A) AS MAX_CUST_CNT_A,     ");
			sql.append("   SUM(MAX_CUST_CNT_B) AS MAX_CUST_CNT_B,     ");
			sql.append("   SUM(MAX_CUST_CNT_M) AS MAX_CUST_CNT_M,     ");
			sql.append("   SUM(MAX_AUM ) AS MAX_AUM ,                 ");
			sql.append("   SUM(TOT_CUST_CNT) AS TOT_CUST_CNT,         ");
			sql.append("   ROUND((SUM(TOT_CUST_CNT)-SUM(TOT_NOT_IN_CTRL))/SUM(MAX_CUST_CNT_TOT),2)*100 AS TOT_CUST_PCTG,     ");   //((B)-(C))/(A)
			sql.append("   SUM(TOT_NOT_IN_CTRL ) AS TOT_NOT_IN_CTRL , ");
			sql.append("   SUM(TOT_AUM ) AS TOT_AUM ,                 ");   
			sql.append("   SUM(TOT_CUST_CNT)-SUM(TOT_NOT_IN_CTRL)-SUM(MAX_CUST_CNT_TOT) AS TOT_CUST_DIFF,     ");   //(B)-(C)-(A) TOT_CUST_DIFF
			sql.append("   SUM(V_CUST_CNT) AS V_CUST_CNT,             ");
			sql.append("   SUM(V_NOT_IN_CTRL ) AS V_NOT_IN_CTRL ,     ");
			sql.append("   SUM(V_AUM ) AS V_AUM ,                     ");
			sql.append("   SUM(V_CUST_DIFF ) AS V_CUST_DIFF ,         ");
			sql.append("   SUM(A_CUST_CNT) AS A_CUST_CNT,             ");
			sql.append("   SUM(A_NOT_IN_CTRL ) AS A_NOT_IN_CTRL ,     ");
			sql.append("   SUM(A_AUM ) AS A_AUM ,                     ");
			sql.append("   SUM(A_CUST_DIFF ) AS A_CUST_DIFF ,         ");
			sql.append("   SUM(B_CUST_CNT) AS B_CUST_CNT,             ");
			sql.append("   SUM(B_NOT_IN_CTRL ) AS B_NOT_IN_CTRL ,     ");
			sql.append("   SUM(B_AUM ) AS B_AUM ,                     ");
			sql.append("   SUM(B_CUST_DIFF ) AS B_CUST_DIFF ,         ");
			sql.append("   SUM(M_CUST_CNT) AS M_CUST_CNT,             ");
			sql.append("   SUM(M_NOT_IN_CTRL ) AS M_NOT_IN_CTRL ,     ");
			sql.append("   SUM(M_AUM ) AS M_AUM ,                     ");
			sql.append("   SUM(M_CUST_DIFF ) AS M_CUST_DIFF          ");
			sql.append("  from AREA ");
			sql.append("  group by REGION_CENTER_NAME,REGION_CENTER_ID ");
			sql.append("), ");
			sql.append("SUM_BRANCH AS ( ");
			sql.append("  SELECT * ");
			sql.append("  FROM ( ");
			sql.append("    SELECT *  ");
			sql.append("    FROM ORIGINAL_VIEW ");
			sql.append("    UNION ALL ");
			sql.append("    SELECT *  ");
			sql.append("    FROM BRANCH   ");
			sql.append("  ) ");
			sql.append("),  ");
			sql.append("SUM_BRANCH_AREA AS ( ");
			sql.append("  SELECT * ");
			sql.append("  FROM SUM_BRANCH ");
			sql.append("  UNION ALL ");
			sql.append("  SELECT * ");
			sql.append("  FROM AREA ");
			sql.append("), ");
			sql.append("SUM_BRANCH_AREA_REGION AS ( ");
			sql.append("  SELECT *  ");
			sql.append("  FROM SUM_BRANCH_AREA ");
			sql.append("  UNION ALL ");
			sql.append("  SELECT * ");
			sql.append("  FROM REGION ");
			sql.append(") ");
			
			// 依據條件顯示適當的合計
			if (StringUtils.isNotBlank(inputVO.getAo_code()) &&
				StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
				StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
				StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("SELECT * from ORIGINAL_VIEW ");
			} else if (!StringUtils.isNotBlank(inputVO.getAo_code()) &&
					StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
					StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("SELECT * from SUM_BRANCH ");
			} else if (!StringUtils.isNotBlank(inputVO.getAo_code()) &&
					!StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
					StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("SELECT * from SUM_BRANCH_AREA ");
			} else if (!StringUtils.isNotBlank(inputVO.getAo_code()) &&
					!StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					!StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("SELECT * from SUM_BRANCH_AREA_REGION ");
			}
			
			sql.append("ORDER BY ");
			sql.append("REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, AO_CODE, YEARMON "); 
			condition.setQueryString(sql.toString());
			// 分頁查詢
//			inputVO.setPageCount(50);
			ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			// CSV全部查詢資訊
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCsvList(csvList); // csv匯出用
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
	
	/**
	 * 處理貨幣格式
	 * @param map
	 * @param key
	 * @return
	 */
	private String currencyFormat(Map map, String key)
	{		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{
			NumberFormat nf = NumberFormat.getInstance();
			return nf.format(map.get(key));										
		}else
			return "0";		
	}
	
	
	
	/**
	* 檢查Map取出欄位是否為Null
	* @param map
	* @return String
	*/
	private String checkIsNullAndTrans(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf("=\""+map.get(key)+"\"");
		}else{
			return "";
		}
	}
	
	

}