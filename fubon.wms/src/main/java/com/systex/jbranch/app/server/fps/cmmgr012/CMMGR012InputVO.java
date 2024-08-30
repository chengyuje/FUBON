package com.systex.jbranch.app.server.fps.cmmgr012;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;


public class CMMGR012InputVO extends PagingInputVO{
	
	private String tipBranchId;
	private String tipWorkStationId;
	private String tipTeller;
	private String tipTxnCode;
	private String cmbFunctionId;
	private String cmbRoleId;
	private Date dtfStartDate;
	private Date dtfLastUpdate;
	public String getTipBranchId() {
		return tipBranchId;
	}
	public void setTipBranchId(String tipBranchId) {
		this.tipBranchId = tipBranchId;
	}
	public String getTipWorkStationId() {
		return tipWorkStationId;
	}
	public void setTipWorkStationId(String tipWorkStationId) {
		this.tipWorkStationId = tipWorkStationId;
	}
	public String getTipTeller() {
		return tipTeller;
	}
	public void setTipTeller(String tipTeller) {
		this.tipTeller = tipTeller;
	}
	public String getTipTxnCode() {
		return tipTxnCode;
	}
	public void setTipTxnCode(String tipTxnCode) {
		this.tipTxnCode = tipTxnCode;
	}
	public String getCmbFunctionId() {
		return cmbFunctionId;
	}
	public void setCmbFunctionId(String cmbFunctionId) {
		this.cmbFunctionId = cmbFunctionId;
	}
	public String getCmbRoleId() {
		return cmbRoleId;
	}
	public void setCmbRoleId(String cmbRoleId) {
		this.cmbRoleId = cmbRoleId;
	}
	public Date getDtfStartDate() {
		return dtfStartDate;
	}
	public void setDtfStartDate(Date dtfStartDate) {
		this.dtfStartDate = dtfStartDate;
	}
	public Date getDtfLastUpdate() {
		return dtfLastUpdate;
	}
	public void setDtfLastUpdate(Date dtfLastUpdate) {
		this.dtfLastUpdate = dtfLastUpdate;
	}
	
}
