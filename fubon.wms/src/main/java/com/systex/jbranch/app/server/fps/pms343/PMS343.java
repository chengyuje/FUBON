package com.systex.jbranch.app.server.fps.pms343;

import java.text.DecimalFormat;
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
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :基金/ETF/股票贖回金流報表<br>
 * Comments Name : PMS343.java<br>
 * Author : Frank<br>
 * Date :2016/05/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms343")
@Scope("request")
public class PMS343 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS343.class);

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)throws JBranchException,ParseException {
		PMS343InputVO inputVO = (PMS343InputVO) body;
		PMS343OutputVO outputVO = new PMS343OutputVO();
		
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
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		// rptType = inputVO.getRptType();
		
		/**RptType 
		 * 1 = AO
		 * 2 = 分行
		 * 3 = 營運區
		 * 4 = 業務處
		 * **/
		StringBuffer sql = new StringBuffer("select ROWNUM, t.* from ( ");
		sql.append("select CF.REGION_CENTER_NAME, ");
		if (inputVO.getRptType() < 4) // 營運區以下
			sql.append("CF.BRANCH_AREA_NAME, ");
		if (inputVO.getRptType() < 3) // 分行以下
			sql.append("CF.BRANCH_NBR, CF.BRANCH_NAME, GROUP_NAME, ");
		if (inputVO.getRptType() < 2) // AO 以下
			sql.append("EMP_RANK_NAME, AO_CODE, EMP_NAME, ");
		sql.append("DAY_VALU, MTD_VALU, DAY_COST, MTD_COST,DATA_DATE ");
		if (inputVO.getRptType() == 1)
			sql.append("from TBPMS_DAY_RDMP_CF_AO CF ");
		if (inputVO.getRptType() == 2)
			sql.append("from TBPMS_DAY_RDMP_CF_BR CF ");
		if (inputVO.getRptType() == 3)
			sql.append("from TBPMS_DAY_RDMP_CF_OP CF ");
		if (inputVO.getRptType() == 4)
			sql.append("from TBPMS_DAY_RDMP_CF_RC CF ");
		// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
		if (3 > inputVO.getRptType()){
		sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
		sql.append("ON ORG.BRANCH_NBR=CF.BRANCH_NBR ");
		}

		sql.append("where 1=1 ");
		// 資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("and to_char(DATA_DATE,'yyyymmdd') = :data_date ");
			condition.setObject("data_date", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		}
		// 業務處
		if (inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")) {
			if (4 == inputVO.getRptType()||3 == inputVO.getRptType()){
				sql.append("and REGION_CENTER_ID = :region_center_id ");		
			}else{
				sql.append("and ORG.REGION_CENTER_ID = :region_center_id ");
			}
			condition.setObject("region_center_id", inputVO.getRegion_center_id());
		}else{
			//登入非總行人員強制加區域中心
			if(!headmgrMap.containsKey(roleID)) {
				if (4 == inputVO.getRptType()||3 == inputVO.getRptType()){
					sql.append("and REGION_CENTER_ID IN (:region_center_id) ");	
				}else{
					sql.append("and ORG.REGION_CENTER_ID IN (:region_center_id) ");
				}
				condition.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}
		
		// 營運區
		if(inputVO.getRptType() < 4){
			if (inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")) {
				if (4 == inputVO.getRptType()||3 == inputVO.getRptType()){
					sql.append("and BRANCH_AREA_ID = :branch_area_id ");
				}else{
					sql.append("and ORG.BRANCH_AREA_ID = :branch_area_id ");
				}
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					if (4 == inputVO.getRptType()||3 == inputVO.getRptType()){
						sql.append("  and BRANCH_AREA_ID IN (:branch_area_id) ");
					}else{
						sql.append("  and ORG.BRANCH_AREA_ID IN (:branch_area_id) ");
					}
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
		}
		
		// 分行別
		if(inputVO.getRptType() < 3){
			if (inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("") ) {
				sql.append("and ORG.BRANCH_NBR = :branch_nbr ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {		
					sql.append("  and ORG.BRANCH_NBR IN (:branch_nbr) ");
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
		}
		
		// 理專 AO_CODE
		if(inputVO.getRptType() < 2){
			if (inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")) {
				sql.append("and AO_CODE = :ao_code ");
				condition.setObject("ao_code", inputVO.getAo_code());
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID) || fchMap.containsKey(roleID)) {
					sql.append(" and AO_CODE IN (:ao_code) ");
					condition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
		}
		//排序
		sql.append(" order by CF.REGION_CENTER_ID");
		if (inputVO.getRptType() < 4)
			sql.append(", CF.BRANCH_AREA_ID");
		if (inputVO.getRptType() < 3)
			sql.append(", CF.BRANCH_NBR");
		if (inputVO.getRptType() < 2)
			sql.append(", CF.AO_CODE");
		sql.append(") t ");
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list_test = dam.exeQuery(condition);
		outputVO.setTotalList(dam.exeQuery(condition));
		outputVO.setTotalList(list_test);
		sendRtnObject(outputVO);
//		ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
//		if (list.size() > 0) {
//			int totalPage = list.getTotalPage();
//			outputVO.setTotalPage(totalPage);
//			outputVO.setResultList(list);	//分頁查詢
//			outputVO.setTotalRecord(list.getTotalRecord()); //csv專用 list
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
//		} else {
//			throw new APException("ehl_01_common_009");
//		}
	}

	/* === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS343OutputVO outputVO = (PMS343OutputVO) body;
		String strTitle = "";
		int rptType = outputVO.getRt();
		if (rptType == 1)
			strTitle = "AO ";
		if (rptType == 2)
			strTitle = "分行";
		if (rptType == 3)
			strTitle = "營運區";
		if (rptType == 4)
			strTitle = "業務處";
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "基金/ETF/股票贖回金流報表_" + strTitle + "贖回量_"
				+ sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			String[] records = new String[12];
			int i = 0;
//			records[i] = checkIsNull(map, "ROWNUM"); // 項次
			records[i] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處";
			if (rptType < 4)
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區";
			if (rptType < 3) {
				records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代號";
				records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱";
			}
			if (rptType < 2) {
				records[++i] = checkIsNull(map, "GROUP_NAME"); // 組別";
				records[++i] = checkIsNull(map, "EMP_RANK_NAME"); // 理專職級";
				records[++i] = checkIsNull(map, "AO_CODE") + "-"
						+ checkIsNull(map, "EMP_NAME"); // 理專";
			}
			records[++i] = currencyFormat(map, "DAY_VALU"); // 當日贖回參考現值";
			records[++i] = currencyFormat(map, "MTD_VALU"); // MTD累積贖回值";
			records[++i] = currencyFormat(map, "DAY_COST"); // 當日贖回參考成本";
			records[++i] = currencyFormat(map, "MTD_COST"); // MTD累積贖回成本";

			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[12];
		int j = 0;
		csvHeader[j] = "項次";
		csvHeader[++j] = "業務處";
		if (rptType < 4)
			csvHeader[++j] = "營運區";
		if (rptType < 3) {
			csvHeader[++j] = "分行代號";
			csvHeader[++j] = "分行名稱";
		}
		if (rptType < 2) {
			csvHeader[++j] = "組別";
			csvHeader[++j] = "理專職級";
			csvHeader[++j] = "理專";
		}
		csvHeader[++j] = "當日贖回參考現值";
		csvHeader[++j] = "MTD累積贖回值";
		csvHeader[++j] = "當日贖回參考成本";
		csvHeader[++j] = "MTD累積贖回成本";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);

		this.sendRtnObject(null);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
}