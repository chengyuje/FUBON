package com.systex.jbranch.app.server.fps.pms411;

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
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :風險部位警示報表<br>
 * Comments Name : PMS411.java<br>
 * Author : Frank<br>
 * Date :2016/05/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms411")
@Scope("request")
public class PMS411 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS411.class);
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS411InputVO inputVO = (PMS411InputVO) body;
		PMS411OutputVO outputVO = new PMS411OutputVO();		
		
		String roleType = "";
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

		try{
			// 取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(inputVO.getsCreDate());
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		
			DataAccessManager dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			ArrayList<String> sql_list = new ArrayList<String>();
			StringBuffer sql = new StringBuffer("select ROWNUM, t.* from ( ");
			sql.append("select TO_CHAR(WARN.DATA_DATE, 'YYYY/MM/DD') AS DATA_DATE, ");
			sql.append("WARN.BRANCH_NBR, WARN.AO_CODE, ");
			sql.append("WARN.CUST_ID, WARN.CUST_NAME, ");
			sql.append("WARN.INVEST_AMT, WARN.NOMATCH_AMT, ");
			sql.append(" WARN.AST_AMT, WARN.NOMATCH_PERCENT,WARN.BRANCH_NAME,WARN.LASTUPDATE ");				
			sql.append(" from TBPMS_RISK_WARN_RPT WARN ");
		
			sql.append(" where 1=1 ");	
			if (inputVO.getsCreDate() != null && !"".equals(inputVO.getsCreDate())){
				sql.append(" and WARN.DATA_YEARMON like :YEARMONN ");
				condition.setObject("YEARMONN", inputVO.getsCreDate() );
			}		
//			//區域中心
//			if(inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")){
//				sql.append(" and WARN.REGION_CENTER_ID = :REGION_CENTER_ID ");
//				condition.setObject("REGION_CENTER_ID" , inputVO.getRegion_center_id() );
//			} else {
//				// 登入非總行人員強制加區域中心
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append(" and WARN.REGION_CENTER_ID IN (:REGION_CENTER_ID) ");
//					condition.setObject("REGION_CENTER_ID",pms000outputVO.getV_regionList());
//				}
//			}
//			//營運區
//			if(inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")){
//				sql.append(" and WARN.BRANCH_AREA_ID = :BRANCH_AREA_ID ");
//				condition.setObject("BRANCH_AREA_ID", inputVO.getBranch_area_id() );
//			}else {
//				// 登入非總行人員強制加營運區
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append(" and WARN.BRANCH_AREA_ID IN (:BRANCH_AREA_ID) ");
//					condition.setObject("BRANCH_AREA_ID",pms000outputVO.getV_areaList());
//				}
//			}
//			//分行
//			if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
//				sql.append("and WARN.BRANCH_NBR = :BRANCH_NBR ");
//				condition.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
//			}	else {
//				// 登入非總行人員強制加分行
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append(" and WARN.BRANCH_NBR IN (:BRANCH_NBR) ");
//					condition.setObject("BRANCH_NBR",pms000outputVO.getV_branchList());
//				}
//			}	
			
			// by Willis 20171024 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("and WARN.BRANCH_NBR = :BRNCH_NBRR ");
				condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
			// 營運區	
			}else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())){
				sql.append("and WARN.BRANCH_NBR in ( ");
				sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :BRANCH_AREA_IDD ) ");
				condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
		    // 區域中心	
			}else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())){
				sql.append("and WARN.BRANCH_NBR in ( ");
				sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :REGION_CENTER_IDD ) ");
				condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}
			
			//理專AO_CODE
			if(inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")){
				sql.append("and WARN.AO_CODE = :AO_CODE ");
					condition.setObject("AO_CODE", inputVO.getAo_code());
			}else {	// 登入非總行人員強制加分行
				if (!headmgrMap.containsKey(roleID)) {
						sql.append(" and WARN.AO_CODE IN (:AO_CODE) ");
						condition.setObject("AO_CODE",pms000outputVO.getV_aoList());
				}
			}
			
			sql.append(" order by WARN.DATA_DATE,WARN.BRANCH_NBR,WARN.AO_CODE) t ");	
			condition.setQueryString(sql.toString());
//			for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
//				condition.setString(sql_i + 1, sql_list.get(sql_i));
//			}
			//#0002303 : 不要有2000筆資料的限制
			ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			int totalPage = list.getTotalPage();
			
			outputVO.setTotalList(dam.exeQuery(condition));
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			outputVO.setCsvList(list);
			
			sendRtnObject(outputVO);			
		}catch(Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");

		}

	}			
	
	/*  === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS411OutputVO outputVO = (PMS411OutputVO) body;		
		/*0002251 */
		List<Map<String, Object>> list = outputVO.getCsvList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "風險部位警示報表_" + sdf.format(new Date()) 
				+ "-"+ getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv"; 
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : list){
			String[] records = new String[10];
			int i = 0;
			records[i] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
			records[++i] = checkIsNull(map, "DATA_DATE"); //資料基準日
			records[++i] = checkIsNull(map, "BRANCH_NBR"); //分行代碼
			records[++i] = checkIsNull(map, "AO_CODE");  //AO Code
			records[++i] = checkIsNull(map, "CUST_ID"); //客戶ID
			records[++i] = checkIsNull(map, "CUST_NAME"); //客戶姓名
			records[++i] = currencyFormat(map, "INVEST_AMT");  //投資總金額
			records[++i] = currencyFormat(map, "NOMATCH_AMT");  //不適配投資金額
			records[++i] = currencyFormat(map, "AST_AMT");  //資產總金額
			records[++i] = percentFormat(map, "NOMATCH_PERCENT")+"%";  //風險不適配投資%
			
			listCSV.add(records);
		}
		//header
		String [] csvHeader = new String[10];
		int j = 0;
		csvHeader[j] = "序號";
		csvHeader[++j] = "資料基準日";						
		csvHeader[++j] = "分行代碼";
		csvHeader[++j] = "AO Code";
		csvHeader[++j] = "客戶ID";
		csvHeader[++j] = "客戶姓名";
		csvHeader[++j] = "投資總金額";
		csvHeader[++j] = "不適配投資金額";
		csvHeader[++j] = "資產總金額";
		csvHeader[++j] = "風險不適配投資%";			
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(outputVO);
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
	//處理貨幣/數字格式
	private String currencyFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));										
		}else
			return "0.00";		
	}
	//處理投資%
	private String percentFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			DecimalFormat df = new DecimalFormat("#.0000");			
			return df.format(map.get(key));										
		}else
			return "0.0000";		
	}	
	
}