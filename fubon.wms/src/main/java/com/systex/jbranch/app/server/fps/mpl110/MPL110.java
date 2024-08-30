package com.systex.jbranch.app.server.fps.mpl110;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * mpl110
 * 
 * @author Carley
 * @date 2019/07/29
 * @spec WMS-CR-20190502-01_M+動態選單新增分行資訊功能
 */
@Component("mpl110")
@Scope("request")
public class MPL110 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {  
		MPL110InputVO inputVO = (MPL110InputVO) body ;
		MPL110OutputVO outputVO = new MPL110OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> availBranchList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		String empID = inputVO.getEmpID();
		outputVO.setIsHEADMGR("N");
		outputVO.setIsARMGR("N");
		outputVO.setIsMBRMGR("N");
		
		// 查詢登入者轄下分行，包含主角色以及兼職角色，也包含其被代理人的主角色及兼職角色
		List<Map<String, String>> loginRoleList = getLoginRole(dam, empID);
		
		for (Map<String, String> map : loginRoleList) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			
			if(StringUtils.equals("Y", ObjectUtils.toString(map.get("ROLE_TYPE")))) {
				//登入者及代理人主角色的轄下分行
				sb.append("select * FROM TABLE(FN_GET_AVAIL_BRANCH_LIST(:loginSysRole, :loginID, :loginDeptID, :memLoginFlag)) ");
			} else {
				//登入者及代理人兼職角色的轄下分行
				sb.append("select * FROM TABLE(FN_GET_PT_AVAIL_BRANCH_LIST(:loginSysRole, :loginID, :loginDeptID, :memLoginFlag)) ");
			}
			queryCondition.setObject("loginSysRole", map.get("LOGIN_ROLE"));
			queryCondition.setObject("loginID", map.get("EMP_ID"));
			queryCondition.setObject("loginDeptID", map.get("DEPT_ID"));
			queryCondition.setObject("memLoginFlag", map.get("MEM_LOGIN_FLAG"));
			
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> tempList = dam.exeQuery(queryCondition);
			
			for (Map<String, Object> tempMap : tempList) {
				availBranchList.add(tempMap);
			}
			
			if(StringUtils.equals("HEADMGR", map.get("LOGIN_ROLE"))) outputVO.setIsHEADMGR("Y");	//有總行權限
			if(StringUtils.equals("ARMGR", map.get("LOGIN_ROLE"))) outputVO.setIsARMGR("Y");		//有業務處長權限
			if(StringUtils.equals("MBRMGR", map.get("LOGIN_ROLE"))) outputVO.setIsMBRMGR("Y");		//有營運督導權限
		}
		
		List<String> list = new ArrayList<String>();
		// 去除重複
		for (Map<String, Object> availBranchMap : availBranchList) {
			try {
				if (!list.contains(availBranchMap.get("BRANCH_NBR").toString())) {
					list.add(availBranchMap.get("BRANCH_NBR").toString());
					resultList.add(availBranchMap);
				}
			} catch(Exception e) {}
		}
				
		for (Map<String, Object> map : resultList) {
			// 將個金分行四個字拿掉
			if (map.get("REGION_CENTER_NAME") != null && String.valueOf(map.get("REGION_CENTER_NAME")).indexOf("個金分行") >= 0) {
				String regionCenterName = String.valueOf(map.get("REGION_CENTER_NAME")).replace("個金分行", "");
				map.put("REGION_CENTER_NAME", regionCenterName);
			}
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	private List<Map<String, String>> getLoginRole(DataAccessManager dam, String empID) throws JBranchException {
		
		// 高端預設組織
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> brhChgMap_BS = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE_BS", FormatHelper.FORMAT_3);
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT BRANCH_NBR ");
		sb.append("FROM TBORG_UHRM_BRH ");
		sb.append("WHERE EMP_ID = :loginID ");

		queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);

		// 依系統角色決定下拉選單可視範圍		
		List<Map<String, String>> rolesList = new ArrayList<Map<String, String>>();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT EMP_ID, ROLE_ID, ROLE_TYPE, ");	//登入者
		sb.append("	      CASE WHEN BRANCH_NBR IS NOT NULL THEN BRANCH_NBR ELSE ( ");
		sb.append("	        CASE WHEN BRANCH_AREA_ID IS NOT NULL THEN BRANCH_AREA_ID ELSE ( ");
		sb.append("	        CASE WHEN REGION_CENTER_ID IS NOT NULL THEN REGION_CENTER_ID ELSE NULL END) END ");
		sb.append("	      ) END AS DEPT_ID, ");
		sb.append("       (SELECT M.DEPT_ID FROM TBORG_MEMBER M WHERE M.EMP_ID = INFO.EMP_ID) AS EMP_DEPT_ID ");
		sb.append("FROM VWORG_EMP_INFO INFO ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("UNION ");
		sb.append("SELECT DISTINCT EMP_ID, ROLE_ID, ROLE_TYPE, ");	//代理人
		sb.append("	      CASE WHEN BRANCH_NBR IS NOT NULL THEN BRANCH_NBR ELSE ( ");
		sb.append("	        CASE WHEN BRANCH_AREA_ID IS NOT NULL THEN BRANCH_AREA_ID ELSE ( ");
		sb.append("	        CASE WHEN REGION_CENTER_ID IS NOT NULL THEN REGION_CENTER_ID ELSE NULL END) END ");
		sb.append("	      ) END AS DEPT_ID, ");
		sb.append("       (SELECT M.DEPT_ID FROM TBORG_MEMBER M WHERE M.EMP_ID = INFO.EMP_ID) AS EMP_DEPT_ID ");
		sb.append("FROM VWORG_EMP_INFO INFO ");
		sb.append("WHERE EXISTS ( ");
		sb.append("  SELECT EMP_ID ");
		sb.append("  FROM TBORG_AGENT AG ");
		sb.append("  WHERE AG.AGENT_ID = :empID ");
		sb.append("  AND AG.AGENT_STATUS = 'S' ");
		sb.append("  AND AG.EMP_ID = INFO.EMP_ID ");
		sb.append(") ");
		queryCondition.setObject("empID", empID);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, String>> resultList = dam.exeQuery(queryCondition);
		
		//                0         1         2         3         4         5            6            7       8          9          10      11             12       13 
		String[] cases = {"FC",     "FCH",    "PAO",    "PSOP",   "BMMGR",  "MBRMGR",    "ARMGR",     "FAIA", "HEADMGR", "UHRMMGR", "UHRM", "UHRMHEADMGR", "BSMGR", "BS"};
		String[] flags = {"brhMem", "brhMem", "brhMem", "brhMem", "brhMem", "brhMemMGR", "brhMemMGR", "ALL",  "ALL",     "uhrmMGR", "UHRM", "uhrmHeadMGR", "bsMGR", "BS"};
		
		String memLoginFlag = flags[0];	// 預設brhMem
		
		for (Map<String, String> map : resultList) {
			Map<String, String> roleMap = new HashMap<String, String>();
			
			int i;
			for (i = 0; i < cases.length; i++) 
				if (!(i == 11 || i == 13)) { // 遇11(UHRMHEADMGR=ARM) & 13 (BS)有額外判斷，所以在此處略過
					if (xmlInfo.doGetVariable("FUBONSYS." + cases[i] + "_ROLE", FormatHelper.FORMAT_2).containsKey(ObjectUtils.toString(map.get("ROLE_ID"))))
						break;
				}
			
			switch (i) {
				case 0:		// FUBONSYS.FC_ROLE
					
					if (new BigDecimal(ObjectUtils.toString(map.get("EMP_DEPT_ID"))).compareTo(new BigDecimal("200")) == 1 &&
							!(new BigDecimal((String) map.get("EMP_DEPT_ID")).compareTo(new BigDecimal("806")) == 0) &&
							!(new BigDecimal((String) map.get("EMP_DEPT_ID")).compareTo(new BigDecimal("810")) == 0) &&
						new BigDecimal(ObjectUtils.toString(map.get("EMP_DEPT_ID"))).compareTo(new BigDecimal("900")) == -1) { 				// 若人員主要組織為分行(200-900)，則判定為分行人員，排除806、810
						memLoginFlag = flags[i];
					} else if (loginBreach.size() > 0) { // 若人員於TBORG_UHRM_BRH中，判定為UHRM
						memLoginFlag = flags[10];
					} else if (StringUtils.equals(brhChgMap_BS.get("BS").toString(), ObjectUtils.toString(map.get("EMP_DEPT_ID")))) { 		// 若人員主要組織非分行 且為175，則判定為銀證人員
						memLoginFlag = flags[13];
					} else {																												// 無法判斷者，則判定為分行人員
						memLoginFlag = flags[i];
					}
					break;
				case 8:		// FUBONSYS.HEADMGR_ROLE
					memLoginFlag = flags[i];
					
					if (StringUtils.equals("R001", ObjectUtils.toString(map.get("ROLE_ID")))) 																 				//   2020/12/16 寫死 R001 ARM		
						memLoginFlag = flags[11];
					
					break;
				case 1:		// FUBONSYS.FCH_ROLE
				case 2: 	// FUBONSYS.PAO_ROLE
				case 3:		// FUBONSYS.PSOP_ROLE
				case 4:		// FUBONSYS.BMMGR_ROLE
				case 5:		// FUBONSYS.MBRMGR_ROLE
				case 6:		// FUBONSYS.ARMGR_ROLE
				case 7:		// FUBONSYS.FAIA_ROLE
				case 9:		// FUBONSYS.UHRMMGR_ROLE
				case 10:	// FUBONSYS.UHRM_ROLE
				case 12:	// FUBONSYS.BSMGR_ROLE
					memLoginFlag = flags[i];
					break;
			}
			
			roleMap.put("MEM_LOGIN_FLAG", memLoginFlag);
			roleMap.put("LOGIN_ROLE", getRoleGroupName(ObjectUtils.toString(map.get("ROLE_ID"))));
			roleMap.put("EMP_ID", ObjectUtils.toString(map.get("EMP_ID")));
			roleMap.put("ROLE_TYPE", ObjectUtils.toString(map.get("ROLE_TYPE")));
			roleMap.put("DEPT_ID", ObjectUtils.toString(map.get("DEPT_ID")));
			rolesList.add(roleMap);
		}
		
		return rolesList;
	}
	
	/***
	 * 取得群組名稱，如：HEADMGR, ARMGR, MBRMGR, BMMGR, FC, PSOP, FAIA, UHRMMGR, UHRM
	 * @param roleId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getRoleGroupName(String roleId) throws DAOException, JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		String loginRole = "";
		
		sb.append("SELECT REPLACE(REPLACE(PARAM_TYPE, 'FUBONSYS.'), '_ROLE') AS LOGINROLED ");
		sb.append(" FROM TBSYSPARAMETER ");
		sb.append(" WHERE PARAM_TYPE LIKE 'FUBONSYS%' AND PARAM_CODE = :roleID AND PARAM_TYPE <> 'FUBONSYS.FPS_BUSINESS_ROLE' ");
		queryCondition.setObject("roleID", roleId);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> rList = dam.exeQuery(queryCondition);
		
		if (rList.size() > 0 && rList.get(0).get("LOGINROLED") != null) {
			loginRole = ObjectUtils.toString(rList.get(0).get("LOGINROLED"));
		}
		
		return loginRole;
	}
	
	// 客戶資產分佈
	public void getAum(Object body, IPrimitiveMap header) throws JBranchException {
		MPL110InputVO inputVO = (MPL110InputVO) body ;
		MPL110OutputVO outputVO = new MPL110OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>(); 
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT AO_WO_YN, TO_CHAR(CREATETIME - 1, 'YYYY/MM/DD') AS DATA_DATE, ");
		sb.append("SUM(NVL(TOTAL_AUM, 0)) AS TOTAL_AUM, ");
		sb.append("SUM(NVL(NATURAL_AUM, 0)) AS NATURAL_AUM, ");
		sb.append("SUM(NVL(LEGAL_AUM, 0)) AS LEGAL_AUM, ");
		sb.append("SUM(NVL(A_SAVING, 0)) AS A_SAVING, ");
		sb.append("SUM(NVL(C_SAVING, 0)) AS C_SAVING, ");
		sb.append("SUM(NVL(INVEST_AMT, 0)) AS INVEST_AMT, ");
		sb.append("SUM(NVL(INSURANCE_AMT, 0)) AS INSURANCE_AMT ");
		sb.append("FROM TBMPL_BRANCH_AST_INFO WHERE 1 = 1 ");
		
		if(StringUtils.isNotBlank(inputVO.getBranchID())) {
			sb.append(" AND BRANCH_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranchID());
		}
		
		if(StringUtils.isNotBlank(inputVO.getAreaID())) {
			sb.append(" AND BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_INFO WHERE BRANCH_AREA_ID = :areaID) ");
			queryCondition.setObject("areaID", inputVO.getAreaID());
		}
		
		if(StringUtils.isNotBlank(inputVO.getRegionID())) {
			sb.append(" AND BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_INFO WHERE REGION_CENTER_ID = :regionID) ");
			queryCondition.setObject("regionID", inputVO.getRegionID());
		}
		
		sb.append(" GROUP BY AO_WO_YN, TO_CHAR(CREATETIME - 1, 'YYYY/MM/DD') ");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String data_date = list.size() > 0 ? (list.get(0).get("DATA_DATE")).toString() : "";
		
		Map<String, Object> aoYMap = new HashMap();
		Map<String, Object> aoNMap = new HashMap();
		for (Map<String, Object> map : list) {
			if ("Y".equals(map.get("AO_WO_YN"))) {
				aoYMap = map;
			} else {
				aoNMap = map;
			}
		}
		List<Map<String, String>> leftTitles = new ArrayList<Map<String,String>>();
		Map<String, String> tempMap = new HashMap();
		tempMap.put("總AUM", "TOTAL_AUM");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("自然人AUM", "NATURAL_AUM");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("法人AUM", "LEGAL_AUM");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("A版存款", "A_SAVING");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("C版存款", "C_SAVING");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("投資", "INVEST_AMT");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("保險", "INSURANCE_AMT");
		leftTitles.add(tempMap);
		
		for (Map<String, String> titleMap : leftTitles) {
			for (String key : titleMap.keySet()) {
				Map<String, Object> map = new HashMap();
				map.put("LEFT_TITLE", key);
				map.put("AO_WO_Y", aoYMap.size() > 0 ? aoYMap.get(titleMap.get(key)).toString() : "0");
				map.put("AO_WO_N", aoNMap.size() > 0 ? aoNMap.get(titleMap.get(key)).toString() : "0");
				map.put("DATA_DATE", data_date);
				resultList.add(map);
			}
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	// 客戶負債分佈
	public void getDebt(Object body, IPrimitiveMap header) throws JBranchException {
		MPL110InputVO inputVO = (MPL110InputVO) body ;
		MPL110OutputVO outputVO = new MPL110OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>(); 
		String branchID = inputVO.getBranchID();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT AO_WO_YN, TO_CHAR(CREATETIME - 1, 'YYYY/MM/DD') AS DATA_DATE, ");
		sb.append("SUM(NVL(MORTGAGE, 0)) AS MORTGAGE, ");
		sb.append("SUM(NVL(CREDIT_LOAN, 0)) AS CREDIT_LOAN, ");
		sb.append("SUM(NVL(SCHOOL_LOAN, 0)) AS SCHOOL_LOAN, ");
		sb.append("SUM(NVL(ABROAD_LOAN, 0)) AS ABROAD_LOAN ");
		sb.append("FROM TBMPL_BRANCH_AST_INFO WHERE 1 = 1 ");
		
		if(StringUtils.isNotBlank(inputVO.getBranchID())) {
			sb.append(" AND BRANCH_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranchID());
		}
		
		if(StringUtils.isNotBlank(inputVO.getAreaID())) {
			sb.append(" AND BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_INFO WHERE BRANCH_AREA_ID = :areaID) ");
			queryCondition.setObject("areaID", inputVO.getAreaID());
		}
		
		if(StringUtils.isNotBlank(inputVO.getRegionID())) {
			sb.append(" AND BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_INFO WHERE REGION_CENTER_ID = :regionID) ");
			queryCondition.setObject("regionID", inputVO.getRegionID());
		}
		
		sb.append(" GROUP BY AO_WO_YN, TO_CHAR(CREATETIME - 1, 'YYYY/MM/DD') ");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String data_date = list.size() > 0 ? (list.get(0).get("DATA_DATE")).toString() : "";
		
		Map<String, Object> aoYMap = new HashMap();
		Map<String, Object> aoNMap = new HashMap();
		for (Map<String, Object> map : list) {
			if ("Y".equals(map.get("AO_WO_YN"))) {
				aoYMap = map;
			} else {
				aoNMap = map;
			}
		}
		List<Map<String, String>> leftTitles = new ArrayList<Map<String,String>>();
		Map<String, String> tempMap = new HashMap();
		tempMap.put("房貸", "MORTGAGE");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("信貸", "CREDIT_LOAN");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("就學貸款", "SCHOOL_LOAN");
		leftTitles.add(tempMap);
		
		tempMap = new HashMap();
		tempMap.put("留學貸款", "ABROAD_LOAN");
		leftTitles.add(tempMap);
		
		for (Map<String, String> titleMap : leftTitles) {
			for (String key : titleMap.keySet()) {
				Map<String, Object> map = new HashMap();
				map.put("LEFT_TITLE", key);
				map.put("AO_WO_Y", aoYMap.size() > 0 ? aoYMap.get(titleMap.get(key)).toString() : "0");
				map.put("AO_WO_N", aoNMap.size() > 0 ? aoNMap.get(titleMap.get(key)).toString() : "0");
				map.put("DATA_DATE", data_date);
				resultList.add(map);
			}
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	// 客戶數分佈
	public void getCustCount(Object body, IPrimitiveMap header) throws JBranchException {
		MPL110InputVO inputVO = (MPL110InputVO) body ;
		MPL110OutputVO outputVO = new MPL110OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>(); 
		String branchID = inputVO.getBranchID();
		dam = this.getDataAccessManager();
		
		// 客群身份
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TO_CHAR(CREATETIME, 'YYYY/MM/DD') AS DATA_DATE, ");
//		sb.append("SUM(NVL(VIP_DEGREE_V, 0)) AS VIP_DEGREE_V, ");
//		sb.append("SUM(NVL(VIP_DEGREE_A, 0)) AS VIP_DEGREE_A, ");
//		sb.append("SUM(NVL(VIP_DEGREE_B, 0)) AS VIP_DEGREE_B, ");
		sb.append("SUM(NVL(VIP_DEGREE_H, 0)) AS VIP_DEGREE_H, ");
		sb.append("SUM(NVL(VIP_DEGREE_T, 0)) AS VIP_DEGREE_T, ");
		sb.append("SUM(NVL(VIP_DEGREE_K, 0)) AS VIP_DEGREE_K, ");
		sb.append("SUM(NVL(VIP_DEGREE_C, 0)) AS VIP_DEGREE_C, ");
		sb.append("SUM(NVL(VIP_DEGREE_M, 0)) AS VIP_DEGREE_M, ");
		sb.append("SUM(NVL(CON_DEGREE_E, 0)) AS CON_DEGREE_E, ");
		sb.append("SUM(NVL(CON_DEGREE_I, 0)) AS CON_DEGREE_I, ");
		sb.append("SUM(NVL(CON_DEGREE_P, 0)) AS CON_DEGREE_P, ");
		sb.append("SUM(NVL(CON_DEGREE_O, 0)) AS CON_DEGREE_O, ");
		sb.append("SUM(NVL(CON_DEGREE_S, 0)) AS CON_DEGREE_S, ");
		sb.append("SUM(NVL(NATURAL_PER, 0)) AS NATURAL_PER, ");
		sb.append("SUM(NVL(LEGAL_PER, 0)) AS LEGAL_PER, ");	
		sb.append("SUM(NVL(A_BLOCK, 0)) AS A_BLOCK, ");
		sb.append("SUM(NVL(C_BLOCK, 0)) AS C_BLOCK ");	
		sb.append("FROM TBMPL_BRANCH_CUST_NUMBER WHERE 1 = 1 ");
		
		if(StringUtils.isNotBlank(inputVO.getBranchID())) {
			sb.append(" AND BRANCH_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranchID());
		}
		
		if(StringUtils.isNotBlank(inputVO.getAreaID())) {
			sb.append(" AND BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_INFO WHERE BRANCH_AREA_ID = :areaID) ");
			queryCondition.setObject("areaID", inputVO.getAreaID());
		}
		
		if(StringUtils.isNotBlank(inputVO.getRegionID())) {
			sb.append(" AND BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_INFO WHERE REGION_CENTER_ID = :regionID) ");
			queryCondition.setObject("regionID", inputVO.getRegionID());
		}
		
		sb.append(" GROUP BY TO_CHAR(CREATETIME, 'YYYY/MM/DD') ");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String data_date = list.size() > 0 ? (list.get(0).get("DATA_DATE")).toString() : "";
		
		List<String> keyList = new ArrayList<>();
		keyList.add("VIP_DEGREE_V");	//	VIP DEGREE等級V 客戶數
		keyList.add("VIP_DEGREE_A");	//	VIP DEGREE等級A 客戶數
		keyList.add("VIP_DEGREE_B");	//	VIP DEGREE等級B 客戶數
		keyList.add("VIP_DEGREE_M");	//	VIP DEGREE等級M 客戶數
		keyList.add("CON_DEGREE_E");	//	貢獻度等級E 客戶數
		keyList.add("CON_DEGREE_I");	//	貢獻度等級I 客戶數
		keyList.add("CON_DEGREE_P");	//	貢獻度等級P 客戶數
		keyList.add("CON_DEGREE_O");	//	貢獻度等級O 客戶數
		keyList.add("CON_DEGREE_S");	//	貢獻度等級S 客戶數
		keyList.add("NATURAL_PER");		//	自然人客戶數
		keyList.add("LEGAL_PER");		//	法人客戶數
		keyList.add("A_BLOCK");			//	A版塊客戶數
		keyList.add("C_BLOCK");			//	C版塊客戶數
		
		if (list.size() > 0) {
			Map<String, Object> map = list.get(0);
			map.put("DATA_DATE", data_date);
			resultList.add(map);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			for (String key : keyList) {
				map.put(key, 0);
			}
			map.put("DATA_DATE", data_date);
			resultList.add(map);
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
}

/*

switch (roleMap.get("LOGIN_ROLE")) {
case "FC":
	if (StringUtils.equals(branchChgMap.get("UHRM").toString(), ObjectUtils.toString(map.get("EMP_DEPT_ID")))){
		roleMap.put("MEM_LOGIN_FLAG", "UHRM");
	} else {
		roleMap.put("MEM_LOGIN_FLAG", "brhMem");
	}
	break;
case "MBRMGR":
	roleMap.put("", "brhMemMGR");
	break;
case "ARMGR":
	roleMap.put("", "brhMemMGR");
	break;
case "HEADMGR":
	if (StringUtils.equals(branchChgMap.get("UHRM").toString(), ObjectUtils.toString(map.get("EMP_DEPT_ID")))){
		roleMap.put("MEM_LOGIN_FLAG", "uhrmHeadMGR");
	} else {
		roleMap.put("MEM_LOGIN_FLAG", "ALL");
	}
	break;
case "FAIA":
	roleMap.put("MEM_LOGIN_FLAG", "ALL");
	break;
case "UHRM":
	roleMap.put("MEM_LOGIN_FLAG", "UHRM");
	break;
case "UHRMMGR":
	roleMap.put("MEM_LOGIN_FLAG", "uhrmMGR");
	break;
case "PSOP":
case "BMMGR":
case "PAO":
	roleMap.put("MEM_LOGIN_FLAG", "brhMem");
	break;
default:
	roleMap.put("MEM_LOGIN_FLAG", "brhMem");
	break;
}
*/