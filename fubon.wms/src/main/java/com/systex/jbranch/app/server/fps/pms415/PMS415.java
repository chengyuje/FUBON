package com.systex.jbranch.app.server.fps.pms415;

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
import com.systex.jbranch.fubon.commons.ValidUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :保險單分行遞送管理報表<br>
 * Comments Name : PMS415.java<br>
 * Author : Allen    <br>
 * Date : 2018/03/26 <br>
 * Version : 1.0 <br>
 * Editor : <br>
 * Editor Date : <br>
 */

@Component("pms415")
@Scope("request")
public class PMS415 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS415.class);
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS415InputVO inputVO = (PMS415InputVO) body;
		PMS415OutputVO outputVO = new PMS415OutputVO();		
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" select ROWNUM, TO_CHAR(IOT.APPLY_DATE, 'YYYY/MM/DD') as APPLY_DATE, IOT.BRANCH_NBR, IOT.BRANCH_NAME, ");
		sql.append(" 	IOT.INS_ID, IOT.CUST_ID, IOT.PROPOSER_NAME ");	
		sql.append(" from VWIOT_MAIN IOT ");
		sql.append("	inner join TBIOT_FEEDBACK FB on IOT.POLICY_NO1 = FB.POLICY_NO1 ");
		sql.append("		and IOT.POLICY_NO2 = FB.POLICY_NO2 ");
		sql.append("		and IOT.POLICY_NO3 = FB.POLICY_NO3 ");
		
		if(inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")){
			sql.append(" left outer join VWORG_DEFN_INFO ORG ");
			sql.append(" 	ON ORG.BRANCH_NBR = IOT.BRANCH_NBR ");
		}
		if(!StringUtils.isBlank(inputVO.getAo_code()) && !"".equals(inputVO.getAo_code())){
			sql.append(" left outer join VWORG_AO_INFO AO ");
			sql.append(" 	ON AO.EMP_ID = IOT.RECRUIT_ID  ");
		}
		sql.append(" where 1=1 ");
		sql.append("	AND FB.SEND_TYPE = '1' ");
		
		/*====== 組SQL語法  ====== */
		
		//要保申請區間
		if (inputVO.getApplyDateS() != null){
			sql.append("and TRUNC(IOT.KEYIN_DATE) >= TO_DATE( :applyDateS , 'YYYY-MM-DD') ");
			condition.setObject("applyDateS", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.getApplyDateS()));
		}
		if (inputVO.getApplyDateE() != null){
			sql.append("and TRUNC(IOT.KEYIN_DATE) <= TO_DATE( :applyDateE , 'YYYY-MM-DD') ");
			condition.setObject("applyDateE", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.getApplyDateE()));
		}
		//區域中心
		if(inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")){
			sql.append("and ORG.REGION_CENTER_ID = :region_center_id ");
			condition.setObject("region_center_id", inputVO.getRegion_center_id());
		}else{
			//登入非總行人員強制加區域中心
			if(!headmgrMap.containsKey(roleID)) {
				sql.append("and ORG.REGION_CENTER_ID IN (:region_center_id) ");
				condition.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}
		//營運區
		if(inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")){
			sql.append("and IOT.BRANCH_AREA_ID = :branch_area_id ");
			condition.setObject("branch_area_id", inputVO.getBranch_area_id());
		}else{
			//登入非總行人員強制加營運區
			if(!headmgrMap.containsKey(roleID)) {
				sql.append("  and IOT.BRANCH_AREA_ID IN (:branch_area_id) ");
				condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
			}
		}
		//分行
		if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
			sql.append("and IOT.BRANCH_NBR = :branch_nbr ");
			condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}else{
			//登入非總行人員強制加分行
			if(!headmgrMap.containsKey(roleID)) {
				sql.append("  and IOT.BRANCH_NBR IN (:branch_nbr) ");
				condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
			}
		}
		//AO_CODE
		if(!StringUtils.isBlank(inputVO.getAo_code()) && !"".equals(inputVO.getAo_code())){
			sql.append(" and AO.AO_CODE = :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());
		}

		
		//要保人ID
		if(inputVO.getCust_id() != null && !inputVO.getCust_id().equals("")){
			if(ValidUtil.isValidIDorRCNumber(inputVO.getCust_id())){
				sql.append(" and IOT.CUST_ID = :cust_id ");
				condition.setObject("cust_id", inputVO.getCust_id().toUpperCase());
			}else{
				throw new JBranchException("ehl_01_common_030");
			}
		}
		
		//保險文件編號
		if(inputVO.getIns_id() != null && !inputVO.getIns_id().equals("")){
			sql.append(" and IOT.INS_ID = :ins_id ");
			condition.setObject("ins_id", inputVO.getIns_id());
		}
		
		sql.append(" order by IOT.APPLY_DATE  ");				
		condition.setQueryString(sql.toString());
		
		/*====== 組SQL語法  ====== */
		
		/*====== 執行SQL語法 && 製作分頁  ====== */
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			
			outputVO.setTotalList(dam.exeQuery(condition));
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}		
		/*====== 執行SQL語法 && 製作分頁  ====== */
	}			
	
	/* === 產出Excel === */
	public void export(Object body, IPrimitiveMap header)throws JBranchException {
		PMS415OutputVO outputVO = (PMS415OutputVO) body;		
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "保險單分行遞送管理報表_" + sdf.format(new Date()) + "_"+ (String)getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : list){
			String[] records = new String[6];
			int i = 0;
			records[i++] = checkIsNull(map, "APPLY_DATE");    //要保書申請日			
			records[i++] = checkIsNull(map, "BRANCH_NBR");    //分行代碼			
			records[i++] = checkIsNull(map, "BRANCH_NAME");   //分行名稱
			records[i++] = checkIsNull(map, "INS_ID");        //保險文件編號
			records[i++] = maskID(map, "CUST_ID");            //要保人
			records[i++] = checkIsNull(map, "PROPOSER_NAME"); //要保人姓名
			
			listCSV.add(records);
		}
		
		/*=== 組表頭 ===*/
		String [] csvHeader = new String[10];
		int j = 0;
		csvHeader[j++] = "要保書申請日";			
		csvHeader[j++] = "分行代碼";
		csvHeader[j++] = "分行名稱";
		csvHeader[j++] = "保險文件編號";
		csvHeader[j++] = "要保人ID";
		csvHeader[j++] = "要保人姓名";

		/*=== 組表頭 ===*/
		
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
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
	/**
	* 從Map取出對CUST_ID欄位加密
	* 
	* @param map
	* @return String
	*/
	private String maskID(Map map, String key){
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key)).substring(0,4) + "****" + String.valueOf(map.get(key)).substring(8);
		}else{
			return "";
		}
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsAoNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf("=\""+map.get(key)+"\"");
		}else{
			return "";
		}
	}
}