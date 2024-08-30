package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;



public class RolePermissionsVO implements Serializable {
	
	private String txnid;				//交易代號
	private String role;				//角色
	private List functionid; 			//角色可執行權限
	
	public String getTxnid() {
		return txnid;
	}
	public List getFunctionid() {
		return functionid;
	}
	public void setFunctionid(List functionid) {
		this.functionid = functionid;
	}
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
	
}
