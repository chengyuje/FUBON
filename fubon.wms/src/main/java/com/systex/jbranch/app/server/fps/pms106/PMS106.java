package com.systex.jbranch.app.server.fps.pms106;

import java.text.ParseException;
import java.util.ArrayList;
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
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :<br>
 * Comments Name : PMS106.java<br>
 * Author : frank<br>
 * Date :2016年07月05日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年01月12日<br>
 */

@Component("pms106")
@Scope("request")
public class PMS106 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS106.class);
	
	/*** 查詢主檔 
	 * @throws ParseException ***/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {		
		PMS106InputVO inputVO = (PMS106InputVO) body;
		PMS106OutputVO outputVO = new PMS106OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		
		// 筆數限制
		Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
		
		
		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getsCreDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		
		sql.append("SELECT T.YEARMON, ");
		sql.append("T.REGION_CENTER_ID, T.REGION_CENTER_NAME, T.BRANCH_AREA_ID, ");
		sql.append("T.BRANCH_AREA_NAME, T.BRANCH_NBR, T.BRANCH_NAME, ");
		if(inputVO.getSrchType().equals("1")){  //分行統計查詢
			sql.append("SUM(FCD_AMT) as FCD_AMT, SUM(BOND_AMT) as BOND_AMT, ");
			sql.append("SUM(NEW_INS_AMT) as NEW_INS_AMT, SUM(ACUM_INS_AMT) as ACUM_INS_AMT,  ");
			sql.append("SUM(TOTAL_AMT) as TOTAL_AMT,SUM(OTHER_AMT) as OTHER_AMT ,MAX(T.CREATETIME) as CREATETIME ");									
		}else{	//理專統計查詢
			sql.append("T.AO_CODE, T.EMP_NAME, FCD_AMT, BOND_AMT, ");
//			sql.append("NEW_INS_AMT, ACUM_INS_AMT,  ");
			sql.append("NEW_INS_AMT, ACUM_INS_AMT, TOTAL_AMT,OTHER_AMT ,T.CREATETIME ");
		}
		sql.append("FROM TBPMS_DUE_CF_MAST T ");
		// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
		sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
		sql.append("ON ORG.BRANCH_NBR=T.BRANCH_NBR ");
		sql.append("WHERE 1=1 ");
			
	
		// ==查詢條件==
		// 統計日期
		if (!StringUtils.isBlank(inputVO.getsCreDate())){
			sql.append(" AND YEARMON = :DATA_DATEE ");
			condition.setObject("DATA_DATEE",inputVO.getsCreDate());
		}
		// 區域中心
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND ORG.REGION_CENTER_ID like :REGION_CENTER_ID ");
			condition.setObject("REGION_CENTER_ID","%" + inputVO.getRegion_center_id()+ "%");
		}else {
		// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append(" and ORG.REGION_CENTER_ID IN (:REGION_CENTER_ID) ");
				condition.setObject("REGION_CENTER_ID",pms000outputVO.getV_regionList());
			}
		}
		// 營運區代號
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND ORG.BRANCH_AREA_ID like :BRANCH_AREA_IDD ");
			condition.setObject("BRANCH_AREA_IDD","%" + inputVO.getBranch_area_id()+ "%");
		}else {
			// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append(" and ORG.BRANCH_AREA_ID IN (:BRANCH_AREA_IDD) ");
				condition.setObject("BRANCH_AREA_IDD",pms000outputVO.getV_areaList());
			}
		}
		// 分行代號
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND ORG.BRANCH_NBR like :BRANCH_NBRR ");
			condition.setObject("BRANCH_NBRR","%" + inputVO.getBranch_nbr()+ "%");
		}else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append(" and ORG.BRANCH_NBR IN (:BRANCH_NBRR) ");
				condition.setObject("BRANCH_NBRR",pms000outputVO.getV_branchList());
			}
		}
		// 理專ao_code
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			if(!inputVO.getAo_code().equals("000")){
				sql.append(" AND T.AO_CODE like :AO_CODEE ");
				condition.setObject("AO_CODEE","%" + inputVO.getAo_code()+ "%");
			}else{
				sql.append(" AND T.BRANCH_NBR IS NULL ");
			}
				
		}else {
			// 登入為銷售人員強制加AO_CODE
			if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
				sql.append(" and T.AO_CODE IN (:AO_CODEE) ");
				condition.setObject("AO_CODEE", pms000outputVO.getV_aoList());
			}
		}
	
	
	
		if(inputVO.getSrchType().equals("1")){
			sql.append("GROUP BY YEARMON, T.REGION_CENTER_ID, T.REGION_CENTER_NAME, ");
			sql.append("T.BRANCH_AREA_ID, T.BRANCH_AREA_NAME, T.BRANCH_NBR, T.BRANCH_NAME ");			
			sql.append("ORDER BY T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR ");
		}else
			sql.append("ORDER BY T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR, T.AO_CODE ");
		
		condition.setQueryString(sql.toString());
//		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
//			condition.setString(sql_i + 1, sql_list.get(sql_i));
//		}
		
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		outputVO.setTotalList(dam.exeQuery(condition));
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
	
	/*** 查詢FCD ***/
	public void queryFCD(Object body, IPrimitiveMap header) throws JBranchException {		
		PMS106InputVO inputVO = (PMS106InputVO) body;
		PMS106OutputVO outputVO = new PMS106OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.YEARMON, ");
		sql.append("a.AO_CODE, a.EMP_NAME, a.CUST_ID, a.CUST_NAME, ");
		//帳號欄位目前拔除  無此欄位
//		sql.append("a.ACCOUNT, a.CRCY_TYPE, a.DEP_AMT, a.REF_EXCH_RATE, ");
//		sql.append("a.PROD_KEY, a.CRCY_TYPE, a.DEP_AMT, a.REF_EXCH_RATE, ");
		sql.append("a.CRCY_TYPE, a.DEP_AMT, a.REF_EXCH_RATE, ");
		sql.append("TO_CHAR(a.DUE_DATE, 'YYYY/MM/DD') as DUE_DATE ");
		sql.append("FROM TBPMS_DUE_CF_DTL_FCD a ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO b ");
		sql.append("ON b.EMP_ID = a.EMP_ID ");
		sql.append("WHERE 1=1 ");
		//資料統計日期
		if (inputVO.getDataMonth() != null && !inputVO.getDataMonth().equals("")){
			sql.append("and a.YEARMON = ? ");
			sql_list.add(inputVO.getDataMonth());
		}				
		//分行
		if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
			sql.append("and b.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBranch_nbr());
		}
		//理專
		if(inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")){
			sql.append("and a.AO_CODE = ? ");
			sql_list.add(inputVO.getAo_code());
		}		
		sql.append("ORDER BY a.AO_CODE ");
		
		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
	
	/*** 查詢主檔 
	 * @throws ParseException ***/
	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException, ParseException {		
		PMS106InputVO inputVO = (PMS106InputVO) body;
		PMS106OutputVO outputVO = new PMS106OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
		StringBuffer sql = new StringBuffer();
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		
		// 筆數限制
		Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
		
		
		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getsCreDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		
		sql.append("SELECT  ");		
		if(inputVO.getSrchType().equals("1")){  //分行統計查詢
			sql.append("SUM(FCD_AMT) as FCD_AMT, SUM(BOND_AMT) as BOND_AMT, ");
			sql.append("SUM(NEW_INS_AMT) as NEW_INS_AMT, SUM(ACUM_INS_AMT) as ACUM_INS_AMT,  ");
			sql.append("SUM(TOTAL_AMT) as TOTAL_AMT,SUM(OTHER_AMT) as OTHER_AMT  ");									
		}else{	//理專統計查詢
			sql.append("SUM(FCD_AMT) as FCD_AMT, SUM(BOND_AMT) as BOND_AMT, ");
			sql.append("SUM(NEW_INS_AMT) as NEW_INS_AMT, SUM(ACUM_INS_AMT) as ACUM_INS_AMT,  ");
			sql.append(" SUM(TOTAL_AMT) as TOTAL_AMT,SUM(OTHER_AMT) as OTHER_AMT  ");
		}
		sql.append("FROM TBPMS_DUE_CF_MAST T ");
		// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
		sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
		sql.append("ON ORG.BRANCH_NBR=T.BRANCH_NBR ");
		sql.append("WHERE 1=1 ");
		// ==查詢條件==
		// 統計日期
		if (!StringUtils.isBlank(inputVO.getsCreDate())){
			sql.append(" AND YEARMON = :DATA_DATEE ");
			condition.setObject("DATA_DATEE",inputVO.getsCreDate());
		}
		// 區域中心
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND ORG.REGION_CENTER_ID like :REGION_CENTER_ID ");
			condition.setObject("REGION_CENTER_ID","%" + inputVO.getRegion_center_id()+ "%");
		}else {
		// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append(" and ORG.REGION_CENTER_ID IN (:REGION_CENTER_ID) ");
				condition.setObject("REGION_CENTER_ID",pms000outputVO.getV_regionList());
			}
		}
		// 營運區代號
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND ORG.BRANCH_AREA_ID like :BRANCH_AREA_IDD ");
			condition.setObject("BRANCH_AREA_IDD","%" + inputVO.getBranch_area_id()+ "%");
		}else {
			// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append(" and ORG.BRANCH_AREA_ID IN (:BRANCH_AREA_IDD) ");
				condition.setObject("BRANCH_AREA_IDD",pms000outputVO.getV_areaList());
			}
		}
		// 分行代號
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND ORG.BRANCH_NBR like :BRANCH_NBRR ");
			condition.setObject("BRANCH_NBRR","%" + inputVO.getBranch_nbr()+ "%");
		}else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append(" and ORG.BRANCH_NBR IN (:BRANCH_NBRR) ");
				condition.setObject("BRANCH_NBRR",pms000outputVO.getV_branchList());
			}
		}
		// 理專ao_code
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sql.append(" AND T.AO_CODE like :AO_CODEE ");
			condition.setObject("AO_CODEE","%" + inputVO.getAo_code()+ "%");
		}else {
			// 登入為銷售人員強制加AO_CODE
			if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
				sql.append(" and T.AO_CODE IN (:AO_CODEE) ");
				condition.setObject("AO_CODEE", pms000outputVO.getV_aoList());
			}
		}
		if(inputVO.getSrchType().equals("1")){
		
			sql.append("ORDER BY T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR ");
		}else
			sql.append("ORDER BY T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR, T.AO_CODE ");
		
		condition.setQueryString(sql.toString());
//		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
//			condition.setString(sql_i + 1, sql_list.get(sql_i));
//		}
//		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList2(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
	
	
	/*** 查詢BOND ***/
	public void queryBOND(Object body, IPrimitiveMap header) throws JBranchException {		
		PMS106InputVO inputVO = (PMS106InputVO) body;
		PMS106OutputVO outputVO = new PMS106OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.YEARMON, ");
		sql.append("a.AO_CODE, a.EMP_NAME, a.CUST_ID, a.CUST_NAME, ");
		sql.append("a.PRD_TYPE, a.PRD_NBR, a.PRD_NAME, ");
		sql.append("a.CRCY_TYPE, a.AMT, a.REF_EXCH_RATE, ");	
		sql.append("TO_CHAR(a.DUE_DATE, 'YYYY/MM/DD') as DUE_DATE ");
		sql.append("FROM TBPMS_DUE_CF_DTL_BOND a ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO b ");
		sql.append("ON b.EMP_ID = a.EMP_ID ");
		sql.append("WHERE 1=1 ");
		//資料統計日期
		if (inputVO.getDataMonth() != null && !inputVO.getDataMonth().equals("")){
			sql.append("and a.YEARMON = ? ");
			sql_list.add(inputVO.getDataMonth());
		}				
		//分行
		if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
			sql.append("and b.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBranch_nbr());
		}
		//理專
		if(inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")){
			sql.append("and a.AO_CODE = ? ");
			sql_list.add(inputVO.getAo_code());
		}		
		sql.append("ORDER BY a.AO_CODE ");
		
		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.executeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
	
	/*** 查詢NEW_INS ***/
	public void queryNEWINS(Object body, IPrimitiveMap header) throws JBranchException {		
		PMS106InputVO inputVO = (PMS106InputVO) body;
		PMS106OutputVO outputVO = new PMS106OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.YEARMON, ");
		sql.append("a.AO_CODE, a.EMP_NAME, a.CUST_ID, a.CUST_NAME, ");
		sql.append("a.CF_TYPE, a.POLICY_NO, a.INS_NBR, ");
		sql.append("a.PRD_NAME, a.SEQ, a.PAY_YEAR, a.CRCY_TYPE, ");
		sql.append("a.DEP_AMT, a.REF_EXCH_RATE, ");
		sql.append("TO_CHAR(a.EFFECT_DATE, 'YYYY/MM/DD') as EFFECT_DATE ");
		sql.append("FROM TBPMS_DUE_CF_DTL_NEW_INS a ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO b ");
		sql.append("ON b.EMP_ID = a.EMP_ID ");
		sql.append("WHERE 1=1 ");
		//資料統計日期
		if (inputVO.getDataMonth() != null && !inputVO.getDataMonth().equals("")){
			sql.append("and a.YEARMON = ? ");
			sql_list.add(inputVO.getDataMonth());
		}				
		//分行
		if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
			sql.append("and b.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBranch_nbr());
		}
		//理專
		if(inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")){
			sql.append("and a.AO_CODE = ? ");
			sql_list.add(inputVO.getAo_code());
		}		
		sql.append("ORDER BY a.AO_CODE ");
		
		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
	
	/*** 查詢ACUM_INS ***/
	public void queryACUMINS(Object body, IPrimitiveMap header) throws JBranchException {		
		PMS106InputVO inputVO = (PMS106InputVO) body;
		PMS106OutputVO outputVO = new PMS106OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.YEARMON, ");
		sql.append("a.AO_CODE, a.EMP_NAME, a.CUST_ID, a.CUST_NAME, ");
		sql.append("a.CF_TYPE, a.POLICY_NO, a.INS_NBR, ");
		sql.append("a.PRD_NAME, a.SEQ, a.PAY_YEAR, a.CRCY_TYPE, ");
		sql.append("a.DEP_AMT, a.REF_EXCH_RATE, ");
		sql.append("TO_CHAR(a.EFFECT_DATE, 'YYYY/MM/DD') as EFFECT_DATE ");
		sql.append("FROM TBPMS_DUE_CF_DTL_ACUM_INS a ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO b ");
		sql.append("ON b.EMP_ID = a.EMP_ID ");
		sql.append("WHERE 1=1 ");
		//資料統計日期
		if (inputVO.getDataMonth() != null && !inputVO.getDataMonth().equals("")){
			sql.append("and a.YEARMON = ? ");
			sql_list.add(inputVO.getDataMonth());
		}				
		//分行
		if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
			sql.append("and b.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBranch_nbr());
		}
		//理專
		if(inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")){
			sql.append("and a.AO_CODE = ? ");
			sql_list.add(inputVO.getAo_code());
		}		
		sql.append("ORDER BY a.AO_CODE ");
		
		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
	
	
	/*** 查詢 ***/
	public void queryGI(Object body, IPrimitiveMap header) throws JBranchException {		
		PMS106InputVO inputVO = (PMS106InputVO) body;
		PMS106OutputVO outputVO = new PMS106OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.YEARMON, ");
		sql.append("a.AO_CODE, a.EMP_NAME, a.CUST_ID, a.CUST_NAME, ");
		sql.append("a.PRD_TYPE, ");
		sql.append("a.PRD_NAME,a.AMT,A.DUE_DATE,A.REF_EXCH_RATE,A.PRD_NBR, ");
		sql.append("A.CRCY_TYPE ");
		sql.append(" ");
		sql.append("FROM TBPMS_DUE_CF_DTL_OTHER a ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO b ");
		sql.append("ON b.EMP_ID = a.EMP_ID ");
		sql.append("WHERE 1=1 ");
		//資料統計日期
		if (inputVO.getDataMonth() != null && !inputVO.getDataMonth().equals("")){
			sql.append("and a.YEARMON = ? ");
			sql_list.add(inputVO.getDataMonth());
		}				
		//分行
		if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
			sql.append("and b.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBranch_nbr());
		}
		//理專
		if(inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")){
			sql.append("and a.AO_CODE = ? ");
			sql_list.add(inputVO.getAo_code());
		}		
		sql.append("ORDER BY a.AO_CODE ");
		
		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	
}