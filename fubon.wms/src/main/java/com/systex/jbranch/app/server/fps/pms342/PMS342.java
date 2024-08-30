package com.systex.jbranch.app.server.fps.pms342;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
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
 * Description :大額異動報表<br>
 * Comments Name : PMS342.java<br>
 * Author : Frank<br>
 * Date :2016/05/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms342")
@Scope("request")
public class PMS342 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS342.class);
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS342InputVO inputVO = (PMS342InputVO) body;
		PMS342OutputVO outputVO = new PMS342OutputVO();		
		
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
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer("  SELECT TO_CHAR(D.TRADE_DATE, 'YYYY/MM/DD') AS TRADE_DATE ,  ");	
		sql.append(" D.CUST_ID, D.CUST_NAME, D.ACCOUNT, D.DEBIT_AMT,  D.CREDIT_AMT, D.BALANCE_AMT, D.SUMMARY, D.CREATETIME,   ");	
		sql.append(" D.EMP_ID, D.EMP_NAME , A.AO_CODE ,D.REGION_CENTER_ID ,D.BRANCH_NBR ,D.CO_ACCT_YN  FROM TBPMS_DAILY_LARGE_CHANGE D  ");	
		//sql.append(" LEFT JOIN (SELECT AO_CODE,BRA_NBR,EMP_ID FROM VWORG_AO_INFO where type='1') A ");	
		sql.append(" LEFT JOIN VWORG_AO_INFO  A ");
		sql.append(" ON D.BRANCH_NBR=A.BRA_NBR AND D.EMP_ID=A.EMP_ID AND A.type='1'  ");	
		sql.append(" WHERE 1=1 ");		
		//資料統計日期
		if (inputVO.getsCreDate() != null){
			sql.append("and TRUNC(D.TRADE_DATE) >= TO_DATE( :scredate , 'YYYY-MM-DD') ");
			condition.setObject("scredate", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.getsCreDate()));
		}
		if (inputVO.geteCreDate() != null){
			sql.append("and TRUNC(D.TRADE_DATE) <= TO_DATE( :ecredate , 'YYYY-MM-DD') ");
			condition.setObject("ecredate", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.geteCreDate()));
		}
		//區域中心
		if(inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")){
			//sql.append("and D.REGION_CENTER_ID = :region_center_id ");
			sql.append("  and D.BRANCH_NBR IN ( ");
			sql.append("    SELECT BRANCH_NBR ");
			sql.append("    FROM VWORG_DEFN_BRH ");
			sql.append("    WHERE DEPT_ID = :region_center_id ");
			sql.append("  ) ");
			condition.setObject("region_center_id", inputVO.getRegion_center_id());
		}else{
			//登入非總行人員強制加區域中心
			if(!headmgrMap.containsKey(roleID)) {
				//sql.append("and REGION_CENTER_ID IN (:region_center_id) ");
				sql.append("  and D.BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID IN (:region_center_id) ");
				sql.append("  ) ");
				condition.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}
		//營運區
		if(inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")){
			//sql.append("and D.OP_AREA_ID = :branch_area_id ");
			sql.append("  and D.BRANCH_NBR IN ( ");
			sql.append("    SELECT BRANCH_NBR ");
			sql.append("    FROM VWORG_DEFN_BRH ");
			sql.append("    WHERE DEPT_ID = :branch_area_id ");
			sql.append("  ) ");
			condition.setObject("branch_area_id", inputVO.getBranch_area_id());
		}else{
			//登入非總行人員強制加營運區
			if(!headmgrMap.containsKey(roleID)) {
				//sql.append("  and D.OP_AREA_ID IN (:branch_area_id) ");
				sql.append("  and D.BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID IN (:branch_area_id) ");
				sql.append("  ) ");
				condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
			}
		}
		//分行
		if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
			sql.append("and D.BRANCH_NBR = :branch_nbr ");
			condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}else{
			//登入非總行人員強制加分行
			if(!headmgrMap.containsKey(roleID)) {	
				sql.append("  and D.BRANCH_NBR IN (:branch_nbr) ");
				condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
			}
		}
		//AO_CODE
		if(!StringUtils.isBlank(inputVO.getAo_code()) && !"".equals(inputVO.getAo_code())){
			sql.append(" and A.AO_CODE = :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());
		}else if(!StringUtils.isBlank(inputVO.getEmp_id()) && !"".equals(inputVO.getEmp_id())){
			sql.append(" and D.EMP_ID = :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id());
		}
		sql.append(" group by TRADE_DATE,D.CUST_ID, D.CUST_NAME, D.ACCOUNT,  ");
		sql.append(" D.DEBIT_AMT,  D.CREDIT_AMT, D.BALANCE_AMT, D.SUMMARY, D.CREATETIME, ");  
		sql.append(" D.EMP_ID, D.EMP_NAME , A.AO_CODE ,D.REGION_CENTER_ID ,D.BRANCH_NBR ,D.CO_ACCT_YN ");
		sql.append(" order by TRADE_DATE, CUST_ID, ACCOUNT  ");				
		condition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			outputVO.setTotalList(dam.executeQuery(condition));
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}			
	
	/*  === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS342OutputVO outputVO = (PMS342OutputVO) body;		
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "大額異動報表_" + sdf.format(new Date()) + "_"+ (String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv"; 
		List listCSV =  new ArrayList();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> conYN = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
		for(Map<String, Object> map : list){
			String[] records = new String[11];
			int i = 0;
			StringBuffer sql3 = new StringBuffer();
//			int c=Integer.parseInt(checkIsNull(map, "ROWNUM").toString());
			records[i] = checkIsNull(map, "TRADE_DATE"); //交易日期
			records[++i] = DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, "CUST_ID"));  //客戶ID
			records[++i] = checkIsNull(map, "CUST_NAME"); //客戶姓名
			records[++i] = checkIsAoNull(map, "ACCOUNT"); //帳號
			records[++i] = currencyFormat(map, "DEBIT_AMT");  //借方金額
			records[++i] = currencyFormat(map, "CREDIT_AMT");  //貸方金額
			records[++i] = currencyFormat(map, "BALANCE_AMT");  //資料日餘額
			records[++i] = checkIsNull(map, "SUMMARY");  //摘要
			records[++i] = checkIsAoNull(map, "EMP_ID");  //理專員編
			records[++i] = checkIsNull(map, "EMP_NAME");  //理專姓名	
			records[++i] = conYN.get(checkIsNull(map, "CO_ACCT_YN"));  //是否為法金戶	
			
			listCSV.add(records);
		}
		//header
		String [] csvHeader = new String[11];
		int j = 0;
		csvHeader[j] = "交易日期";			
		csvHeader[++j] = "客戶ID";
		csvHeader[++j] = "客戶姓名";
		csvHeader[++j] = "帳號";
		csvHeader[++j] = "借方金額";
		csvHeader[++j] = "貸方金額";
		csvHeader[++j] = "資料日餘額";
		csvHeader[++j] = "摘要";
		csvHeader[++j] = "理專員編";
		csvHeader[++j] = "理專姓名";
		csvHeader[++j] = "是否為法金戶";
		
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
	//處理貨幣格式
	private String currencyFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			NumberFormat nf = NumberFormat.getInstance();
			return nf.format(map.get(key));	
		}else
			return "0.00";		
	}
}