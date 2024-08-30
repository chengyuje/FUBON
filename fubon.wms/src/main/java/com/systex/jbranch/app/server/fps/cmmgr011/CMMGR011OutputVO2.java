package com.systex.jbranch.app.server.fps.cmmgr011;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;


public class CMMGR011OutputVO2 extends PagingOutputVO {
	private String roleid;
	private String rolename;
	private String priID;
	private String priName;
	private String itemid;
	private boolean maintenance;
	private boolean query;
	private boolean print;
	private boolean exports;
	private boolean watermark;
	private boolean security;
	private boolean confirm;
	private boolean mobile;
	private boolean screen;
	private boolean allowMaintenance=false;
	private boolean allowQuery=false;
	private boolean allowExport=false;
	private boolean allowPrint=false;
	private boolean allowWatermark=false;
	private boolean allowSecurity=false;
	private boolean allowConfirm=false;
	private boolean allowMobile=false;
	private boolean allowScreen=false;
	
	
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getPriID() {
		return priID;
	}
	public void setPriID(String priID) {
		this.priID = priID;
	}
	public String getPriName() {
		return priName;
	}
	public void setPriName(String priName) {
		this.priName = priName;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public boolean isMaintenance() {
		return maintenance;
	}
	public void setMaintenance(boolean maintenance) {
		this.maintenance = maintenance;
	}
	public boolean isQuery() {
		return query;
	}
	public void setQuery(boolean query) {
		this.query = query;
	}
	public boolean isPrint() {
		return print;
	}
	public void setPrint(boolean print) {
		this.print = print;
	}
	public boolean isExports() {
		return exports;
	}
	public void setExports(boolean exports) {
		this.exports = exports;
	}
	public boolean isWatermark() {
		return watermark;
	}
	public void setWatermark(boolean watermark) {
		this.watermark = watermark;
	}
	public boolean isSecurity() {
		return security;
	}
	public void setSecurity(boolean security) {
		this.security = security;
	}
	public boolean isConfirm() {
		return confirm;
	}
	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}
	public boolean isMobile() {
		return mobile;
	}
	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}
	public boolean isScreen() {
		return screen;
	}
	public void setScreen(boolean screen) {
		this.screen = screen;
	}
	public boolean isAllowMaintenance() {
		return allowMaintenance;
	}
	public void setAllowMaintenance(boolean allowMaintenance) {
		this.allowMaintenance = allowMaintenance;
	}
	public boolean isAllowQuery() {
		return allowQuery;
	}
	public void setAllowQuery(boolean allowQuery) {
		this.allowQuery = allowQuery;
	}
	public boolean isAllowExport() {
		return allowExport;
	}
	public void setAllowExport(boolean allowExport) {
		this.allowExport = allowExport;
	}
	public boolean isAllowPrint() {
		return allowPrint;
	}
	public void setAllowPrint(boolean allowPrint) {
		this.allowPrint = allowPrint;
	}
	public boolean isAllowWatermark() {
		return allowWatermark;
	}
	public void setAllowWatermark(boolean allowWatermark) {
		this.allowWatermark = allowWatermark;
	}
	public boolean isAllowSecurity() {
		return allowSecurity;
	}
	public void setAllowSecurity(boolean allowSecurity) {
		this.allowSecurity = allowSecurity;
	}
	public boolean isAllowConfirm() {
		return allowConfirm;
	}
	public void setAllowConfirm(boolean allowConfirm) {
		this.allowConfirm = allowConfirm;
	}
	public boolean isAllowMobile() {
		return allowMobile;
	}
	public void setAllowMobile(boolean allowMobile) {
		this.allowMobile = allowMobile;
	}
	public boolean isAllowScreen() {
		return allowScreen;
	}
	public void setAllowScreen(boolean allowScreen) {
		this.allowScreen = allowScreen;
	}
}