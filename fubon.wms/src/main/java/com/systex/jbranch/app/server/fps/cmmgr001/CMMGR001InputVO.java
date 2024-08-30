package com.systex.jbranch.app.server.fps.cmmgr001;

import java.util.List;

import com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO;
import com.systex.jbranch.platform.common.security.module.vo.ItemFunctionVO;

public class CMMGR001InputVO {
	
	public CMMGR001InputVO()
	{
		ItemFunctionVO vo =null;
		TBSYSSECUROLEVO vo2=null;
		
	}
	private String roleID;
	private String roleName;
	private String extend3;
	private String approvalLevel;
	private String groupID;
	private String mainPage;
	private String queryType;
	private String txnCode;
	private String txnName;
	private List functionList;
	public String getRoleID() {
		return roleID;
	}
	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getApprovalLevel() {
		return approvalLevel;
	}
	public void setApprovalLevel(String approvalLevel) {
		this.approvalLevel = approvalLevel;
	}
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	public String getGroupID() {
		return groupID;
	}
	public List getFunctionList() {
		return functionList;
	}
	public void setFunctionList(List functionList) {
		this.functionList = functionList;
	}
	public void setMainPage(String extend2) {
		this.mainPage = extend2;
	}
	public String getMainPage() {
		return mainPage;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getQueryType() {
		return queryType;
	}
	public String getTxnCode() {
		return txnCode;
	}
	public void setTxnCode(String txnCode) {
		this.txnCode = txnCode;
	}
	public String getTxnName() {
		return txnName;
	}
	public void setTxnName(String txnName) {
		this.txnName = txnName;
	}
	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}
	public String getExtend3() {
		return extend3;
	}
	
	
	


}
