package com.systex.jbranch.platform.server.info.fubonbranch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.BranchFactoryIF;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class WmsBranchFactory implements BranchFactoryIF {

	/**
	 * 
	 * 
	 */
	@Override
	public List<Branch> getBranchList() throws JBranchException {
		DataAccessManager dam = new DataAccessManager();
		//  若用cirtia,需自行new transaction
		List<Branch> branhList = new ArrayList<Branch>();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("SELECT BRCHID, NAME FROM TBSYSBRANCH ORDER BY BRCHID");
		List<Map<String, String>> result = dam.exeQuery(qc);
		return branhList;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public Branch getBranch(String branchNbr) throws JBranchException {
		Branch brch = new Branch();
		DataAccessManager dam = new DataAccessManager();
		//  若用cirtia,需自行new transaction
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("select BRANCH_NBR, BRANCH_NAME from VWORG_DEFN_INFO where BRANCH_NBR=:branchNbr");
		qc.setObject("branchNbr", branchNbr);

		List<Map<String, String>> result = dam.exeQuery(qc);
		for (int i = 0; i < result.size(); i++) {

			Map<String, String> recordMap = (Map<String, String>) result.get(i);

			brch.setBrchID((String) recordMap.get("BRANCH_NBR"));
			brch.setName((String) recordMap.get("BRANCH_NAME"));
			brch.setOpDate("00000000");
			brch.setTxnFlag("0");
		}
		return brch;
	}

	/**
	 * 
	 * @param empId
	 * @return
	 * @throws JBranchException
	 */
	public WmsBranch getWmsBranch(Map<String, String> loginInfoMap) throws JBranchException {
		WmsBranch brch = new WmsBranch();

		DataAccessManager dam = new DataAccessManager();
		//  若用cirtia,需自行new transaction
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		if ("FA".equals(loginInfoMap.get("roleId")) || "IA".equals(loginInfoMap.get("roleId"))) {
			sb.append("SELECT d.BRANCH_NBR, d.BRANCH_NAME, d.BRANCH_AREA_ID, ");
			sb.append("       d.BRANCH_AREA_NAME, d.REGION_CENTER_ID, d.REGION_CENTER_NAME ");
			sb.append("FROM TBORG_FAIA f ");
			sb.append("LEFT JOIN VWORG_DEFN_INFO d ON f.BRANCH_NBR = d.BRANCH_NBR ");
			sb.append("WHERE EMP_ID = :empId ");
			sb.append("ORDER BY d.BRANCH_NBR ");

			qc.setQueryString(sb.toString());
			qc.setObject("empId", loginInfoMap.get("userId"));

			List<Map<String, String>> result = dam.exeQuery(qc);

			for (int i = 0; i < result.size(); i++) {
				Map<String, String> recordMap = (Map<String, String>) result.get(i);
				brch.setRegion_center_id(recordMap.get("REGION_CENTER_ID"));
				brch.setRegion_center_name(recordMap.get("REGION_CENTER_NAME"));
				brch.setBranch_area_id(recordMap.get("BRANCH_AREA_ID"));
				brch.setBranch_area_name(recordMap.get("BRANCH_AREA_NAME"));
				// 預設分行0000
				brch.setBrchID((recordMap.get("BRANCH_NBR") == null) ? "000" : recordMap.get("BRANCH_NBR"));
				brch.setName(recordMap.get("BRANCH_NAME"));

				brch.setOpDate("00000000");
				brch.setTxnFlag("0");
			}
		} else if ("PAO".equals(loginInfoMap.get("roleId"))) {
			sb.append("SELECT d.BRANCH_NBR, d.BRANCH_NAME, d.BRANCH_AREA_ID, ");
			sb.append("       d.BRANCH_AREA_NAME, d.REGION_CENTER_ID, d.REGION_CENTER_NAME ");
			sb.append("FROM TBORG_PAO f ");
			sb.append("LEFT JOIN VWORG_DEFN_INFO d ON f.BRANCH_NBR = d.BRANCH_NBR ");
			sb.append("WHERE EMP_ID = :empId ");
			sb.append("ORDER BY d.BRANCH_NBR ");

			qc.setQueryString(sb.toString());
			qc.setObject("empId", loginInfoMap.get("userId"));

			List<Map<String, String>> result = dam.exeQuery(qc);

			for (int i = 0; i < result.size(); i++) {
				Map<String, String> recordMap = (Map<String, String>) result.get(i);
				brch.setRegion_center_id(recordMap.get("REGION_CENTER_ID"));
				brch.setRegion_center_name(recordMap.get("REGION_CENTER_NAME"));
				brch.setBranch_area_id(recordMap.get("BRANCH_AREA_ID"));
				brch.setBranch_area_name(recordMap.get("BRANCH_AREA_NAME"));
				// 預設分行0000
				brch.setBrchID((recordMap.get("BRANCH_NBR") == null) ? "000" : recordMap.get("BRANCH_NBR"));
				brch.setName(recordMap.get("BRANCH_NAME"));

				brch.setOpDate("00000000");
				brch.setTxnFlag("0");
			}
		} else {
			brch.setRegion_center_id(loginInfoMap.get("REGION_CENTER_ID"));
			brch.setRegion_center_name(loginInfoMap.get("REGION_CENTER_NAME"));
			brch.setBranch_area_id(loginInfoMap.get("BRANCH_AREA_ID"));
			brch.setBranch_area_name(loginInfoMap.get("BRANCH_AREA_NAME"));
			// 預設分行0000
			brch.setBrchID((StringUtils.isBlank(loginInfoMap.get("BRANCH_NBR"))) ? "000" : loginInfoMap.get("BRANCH_NBR"));
			brch.setName(loginInfoMap.get("BRANCH_NAME"));

			brch.setOpDate("00000000");
			brch.setTxnFlag("0");
		}

		return brch;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public List<Map<String, String>> getBranchLabelList() throws JBranchException {
		return new ArrayList<Map<String, String>>();
	}

	/**
	 * 取得可視營運中心清單
	 * 
	 * @param loginID
	 * @param loginRole
	 * @return
	 * @throws JBranchException
	 */
	public List<Map<String, String>> getAvailRegionList(Map<String, String> loginInfoMap, String sysRole) throws JBranchException {
		DataAccessManager dam = new DataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		if (StringUtils.equals("N", loginInfoMap.get("isPrimaryRole")) || StringUtils.equals("AN", loginInfoMap.get("isPrimaryRole"))) {
			sb.append("select distinct REGION_CENTER_ID, REGION_CENTER_NAME from table(FN_GET_PT_AVAIL_BRANCH_LIST(:loginSysRole, :loginID, :loginDeptID, :memLoginFlag))");

		} else {
			sb.append("select distinct REGION_CENTER_ID, REGION_CENTER_NAME from table(FN_GET_AVAIL_BRANCH_LIST(:loginSysRole, :loginID, :loginDeptID, :memLoginFlag))");
		}

		sb.append(" order by REGION_CENTER_ID");
		
		cond.setObject("loginSysRole", sysRole);
		cond.setObject("loginID", loginInfoMap.get("userId"));
		cond.setObject("loginDeptID", loginInfoMap.get("deptID"));
		cond.setObject("memLoginFlag", loginInfoMap.get("memLoginFlag"));

		cond.setQueryString(sb.toString());
		return dam.exeQuery(cond);
	}

	/**
	 * 取得可視區域清單
	 * 
	 * @param loginID
	 * @param loginRole
	 * @return
	 * @throws JBranchException
	 */
	public List<Map<String, String>> getAvailAreaList(Map<String, String> loginInfoMap, String sysRole) throws JBranchException {
		DataAccessManager dam = new DataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		if (StringUtils.equals("N", loginInfoMap.get("isPrimaryRole")) || StringUtils.equals("AN", loginInfoMap.get("isPrimaryRole"))) {
			sb.append("select distinct BRANCH_AREA_ID, BRANCH_AREA_NAME, REGION_CENTER_ID from table(FN_GET_PT_AVAIL_BRANCH_LIST(:loginSysRole, :loginID, :loginDeptID, :memLoginFlag))");

			cond.setObject("loginDeptID", loginInfoMap.get("deptID"));
		} else {
			sb.append("select distinct BRANCH_AREA_ID, BRANCH_AREA_NAME, REGION_CENTER_ID from table(FN_GET_AVAIL_BRANCH_LIST(:loginSysRole, :loginID, :loginDeptID, :memLoginFlag))");
		}

		sb.append(" order by BRANCH_AREA_ID");

		cond.setObject("loginSysRole", sysRole);
		cond.setObject("loginID", loginInfoMap.get("userId"));
		cond.setObject("loginDeptID", loginInfoMap.get("deptID"));
		cond.setObject("memLoginFlag", loginInfoMap.get("memLoginFlag"));
		
		cond.setQueryString(sb.toString());

		return dam.exeQuery(cond);
	}

	/**
	 * 取得可視分行清單
	 * 
	 * @param loginID
	 * @param loginRole
	 * @return
	 * @throws JBranchException
	 */
	public List<Map<String, String>> getAvailBranchList(Map<String, String> loginInfoMap, String sysRole) throws JBranchException {
		
		DataAccessManager dam = new DataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		if (StringUtils.equals("N", loginInfoMap.get("isPrimaryRole")) || StringUtils.equals("AN", loginInfoMap.get("isPrimaryRole"))) {
			sb.append("select BRANCH_NBR, BRANCH_NAME, BRANCH_AREA_ID from table(FN_GET_PT_AVAIL_BRANCH_LIST(:loginSysRole, :loginID, :loginDeptID, :memLoginFlag))");

			cond.setObject("loginDeptID", loginInfoMap.get("deptID"));
		} else {
			sb.append("select BRANCH_NBR, BRANCH_NAME, BRANCH_AREA_ID from table(FN_GET_AVAIL_BRANCH_LIST(:loginSysRole, :loginID, :loginDeptID, :memLoginFlag))");
		}

		sb.append(" order by BRANCH_NBR");

		cond.setObject("loginSysRole", sysRole);
		cond.setObject("loginID", loginInfoMap.get("userId"));
		cond.setObject("loginDeptID", loginInfoMap.get("deptID"));
		cond.setObject("memLoginFlag", loginInfoMap.get("memLoginFlag"));

		cond.setQueryString(sb.toString());
		
		List<Map<String, String>> list = dam.exeQuery(cond);
		
		if (list.size() > 0) {
			return list;
		} else {
			throw new APException("該人員角色為：" + loginInfoMap.get("roleName") + "，查無管轄行。");
		}
	}

		
}
