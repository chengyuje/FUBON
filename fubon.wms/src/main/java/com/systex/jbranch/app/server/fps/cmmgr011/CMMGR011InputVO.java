package com.systex.jbranch.app.server.fps.cmmgr011;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CMMGR011InputVO extends PagingInputVO {
	private String txnCode;
	private String txnName;
	private String sysType;
	private String jrnType;
	private String secApply;
	private Boolean chkMaintenance;
	private Boolean chkQuery;
	private Boolean chkPrint;
	private Boolean chkExport;
	private Boolean chkWatermark;
	private Boolean chkSecurity;
	private Boolean chkConfirm;
	private Boolean chkMobile;
	private Boolean chkScreen;
	private String moduleId;

	private String operType;

	private String queryType;
	
	private String priID;
	private List<?> itemPriFuns;

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
	public String getSysType() {
		return sysType;
	}
	public void setSysType(String sysType) {
		this.sysType = sysType;
	}
	public String getJrnType() {
		return jrnType;
	}
	public void setJrnType(String jrnType) {
		this.jrnType = jrnType;
	}
	public String getSecApply() {
		return secApply;
	}
	public void setSecApply(String secApply) {
		this.secApply = secApply;
	}
	public Boolean getChkMaintenance() {
		return chkMaintenance;
	}
	public void setChkMaintenance(Boolean chkMaintenance) {
		this.chkMaintenance = chkMaintenance;
	}
	public Boolean getChkQuery() {
		return chkQuery;
	}
	public void setChkQuery(Boolean chkQuery) {
		this.chkQuery = chkQuery;
	}
	public Boolean getChkPrint() {
		return chkPrint;
	}
	public void setChkPrint(Boolean chkPrint) {
		this.chkPrint = chkPrint;
	}
	public Boolean getChkExport() {
		return chkExport;
	}
	public void setChkExport(Boolean chkExport) {
		this.chkExport = chkExport;
	}
	public Boolean getChkWatermark() {
		return chkWatermark;
	}
	public void setChkWatermark(Boolean chkWatermark) {
		this.chkWatermark = chkWatermark;
	}
	public Boolean getChkSecurity() {
		return chkSecurity;
	}
	public void setChkSecurity(Boolean chkSecurity) {
		this.chkSecurity = chkSecurity;
	}
	public Boolean getChkConfirm() {
		return chkConfirm;
	}
	public void setChkConfirm(Boolean chkConfirm) {
		this.chkConfirm = chkConfirm;
	}
	public Boolean getChkMobile() {
		return chkMobile;
	}
	public void setChkMobile(Boolean chkMobile) {
		this.chkMobile = chkMobile;
	}
	public Boolean getChkScreen() {
		return chkScreen;
	}
	public void setChkScreen(Boolean chkScreen) {
		this.chkScreen = chkScreen;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getPriID() {
		return priID;
	}
	public void setPriID(String priID) {
		this.priID = priID;
	}
	public List<?> getItemPriFuns() {
		return itemPriFuns;
	}
	public void setItemPriFuns(List<?> itemPriFuns) {
		this.itemPriFuns = itemPriFuns;
	}
}
