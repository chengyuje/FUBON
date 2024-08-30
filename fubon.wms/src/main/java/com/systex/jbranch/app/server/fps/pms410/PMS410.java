package com.systex.jbranch.app.server.fps.pms410;

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
import com.systex.jbranch.app.server.fps.pms408.PMS408InputVO;
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
 * Description :客戶投資風險承受度測試統計報表<br>
 * Comments Name : PMS410.java<br>
 * Author : Frank<br>
 * Date :2016/05/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms410")
@Scope("request")
public class PMS410 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS410.class);
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException , ParseException {
		PMS410InputVO inputVO = (PMS410InputVO) body;
		PMS410OutputVO outputVO = new PMS410OutputVO();				
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
		this.queryData(condition, inputVO);
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List<Map<String,Object>> list1=dam.exeQuery(condition);
		if (list.size() > 0) {			
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setResultList2(list1);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
           Boolean x=false;
			if(list.getTotalPage()==inputVO.getCurrentPageIndex()+1)
            {
            	x=true;
				outputVO.setSHOW(x);
            	
            }else
            {
            	outputVO.setSHOW(x);	
            }
			
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}			
	
	public void queryData(QueryConditionIF condition,PMS410InputVO inputVO) throws JBranchException , ParseException{
		
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
		pms000InputVO.setReportDate(inputVO.getDataMonth());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		StringBuffer sql = new StringBuffer("");
		sql.append("select ROWNUM ,SUBSTRB(STC.DATA_YEARMON,0,4)||'/'||SUBSTRB(STC.DATA_YEARMON,-2,2) AS DATA_YEARMON, ");
		sql.append("STC.BRANCH_AREA_NAME, STC.BRANCH_NBR, STC.BRANCH_NAME,  ");
		sql.append("STC.C1_PCTG, STC.C1_NOP, STC.C2_PCTG, STC.C2_NOP, ");
		sql.append("STC.C3_PCTG, STC.C3_NOP, STC.C4_PCTG, STC.C4_NOP, ");
		sql.append("STC.C5_PCTG, STC.C5_NOP,STC.CREATETIME, ");
		sql.append("STC.TOTAL_KYC, STC.TOTAL_168, ");
		sql.append("sum(STC.C1_PCTG) TOTAL_STC_C1_PCTG,sum(STC.C1_NOP) TOTAL_STC_C1_NOP,sum(STC.C2_PCTG) TOTAL_STC_C2_PCTG, ");
		sql.append("sum(STC.C2_NOP) TOTAL_STC_C2_NOP,sum(STC.C3_PCTG) TOTAL_STC_C3_PCTG,sum(STC.C3_NOP) TOTAL_STC_C3_NOP, sum(STC.C4_PCTG) TOTAL_STC_C4_PCTG, ");
		sql.append(" sum(STC.C4_NOP) TOTAL_STC_C4_NOP,sum(STC.C5_PCTG) TOTAL_STC_C5_PCTG,sum(STC.C5_NOP) TOTAL_STC_C5_NOP,  ");
		sql.append(" sum(STC.TOTAL_KYC) TOTAL_STC_TOTAL_KYC,sum(STC.TOTAL_168) TOTAL_STC_TOTAL_168 ");
		sql.append("from TBPMS_RISK_TLRC_STTSTC STC ");		
		sql.append("where 1=1 ");		
		sql.append("and STC.DATA_YEARMON = :yearmon ");
		
		// 分行
//				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
//					sql.append("and STC.BRANCH_NBR = :branch_nbr ");
//					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
//				}else{
//					//登入非總行人員強制加分行
//					if(!headmgrMap.containsKey(roleID)) {
//						sql.append("and STC.BRANCH_NBR IN (:branch_nbr) ");
//						condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
//					}
//				}
//				// 營運區
//				if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
//					sql.append("and STC.BRANCH_AREA_ID = :branch_area_id ");
//					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
//				}else{
//					//登入非總行人員強制加營運區
//					if(!headmgrMap.containsKey(roleID)) {
//						sql.append("and STC.BRANCH_AREA_ID IN (:branch_area_id) ");
//						condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
//					}
//				}
//				// 區域中心
//				if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
//					sql.append("and STC.REGION_CENTER_ID = :region_center_id ");
//					condition.setObject("region_center_id", inputVO.getRegion_center_id());
//				}else{
//					//登入非總行人員強制加區域中心
//					if(!headmgrMap.containsKey(roleID)) {
//						sql.append("and STC.REGION_CENTER_ID IN (:region_center_id) ");
//						condition.setObject("region_center_id", pms000outputVO.getV_regionList());
//					}
//				}
		
		// by Willis 20171024 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append("and STC.BRANCH_NBR = :BRNCH_NBRR ");
					condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
				// 營運區	
				}else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())){
					sql.append("and STC.BRANCH_NBR in ( ");
					sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :BRANCH_AREA_IDD ) ");
					condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			    // 區域中心	
				}else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())){
					sql.append("and STC.BRANCH_NBR in ( ");
					sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :REGION_CENTER_IDD ) ");
					condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
				}
		
		sql.append(" group by ROWNUM,DATA_YEARMON,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,CREATETIME,C1_PCTG,C1_NOP,C2_PCTG,C2_NOP,C3_PCTG,C3_NOP,C4_PCTG,C4_NOP,C5_PCTG,C5_NOP,C5_NOP,TOTAL_KYC,TOTAL_168  "); 
		
		condition.setObject("yearmon", inputVO.getDataMonth());
		sql.append(" order by  ROWNUM ,DATA_YEARMON,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME");		
		condition.setQueryString(sql.toString());
		
	}
	/*  === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException , ParseException{
		
		PMS410InputVO inputVO = (PMS410InputVO) body;
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
		this.queryData(condition, inputVO);
		
		List<Map<String, Object>> list = dam.executeQuery(condition);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "客戶投資風險承受度測試統計報表_" + sdf.format(new Date())+"_"+(String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv"; 
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : list){
			String[] records = new String[15];
			int i = 0;
			records[i] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
			records[++i] = checkIsNull(map, "DATA_YEARMON").replace("/", "_") ; //資料年月
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");  //分區
			records[++i] = checkIsNull(map, "BRANCH_NBR"); //分行代碼
			records[++i] = checkIsNull(map, "BRANCH_NAME"); //分行名稱
			records[++i] = checkIsNull(map, "C1_PCTG");  //C1百分比 
			records[++i] = checkIsNull(map, "C1_NOP");  // C1人數
			records[++i] = checkIsNull(map, "C2_PCTG"); //C2百分比
			records[++i] = checkIsNull(map, "C2_NOP");  // C2人數
			records[++i] = checkIsNull(map, "C3_PCTG");  //C3百分比 
			records[++i] = checkIsNull(map, "C3_NOP");  //C3人數
			records[++i] = checkIsNull(map, "C4_PCTG");  //C4百分比 
			records[++i] = checkIsNull(map, "C4_NOP");  // C4人數
			records[++i] = checkIsNull(map, "TOTAL_KYC");  //KYC合計數				
			records[++i] = checkIsNull(map, "TOTAL_168");  //168新開戶合計數
			
			listCSV.add(records);
		}
		//header
		String [] csvHeader = new String[15];
		int j = 0;
		csvHeader[j] = "序號";
		csvHeader[++j] = "資料年月";			
		csvHeader[++j] = "分區";
		csvHeader[++j] = "分行代碼";
		csvHeader[++j] = "分行名稱";
		csvHeader[++j] = "C1百分比";
		csvHeader[++j] = "C1人數";
		csvHeader[++j] = "C2百分比";
		csvHeader[++j] = "C2人數";
		csvHeader[++j] = "C3百分比";
		csvHeader[++j] = "C3人數";
		csvHeader[++j] = "C4百分比";
		csvHeader[++j] = "C4人數";
		csvHeader[++j] = "KYC合計數";
		csvHeader[++j] = "168新開戶合計數";
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);

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
	//處理貨幣格式
	private String currencyFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));										
		}else
			return "$0.00";
	}
}