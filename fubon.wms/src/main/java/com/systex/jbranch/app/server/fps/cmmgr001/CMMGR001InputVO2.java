package com.systex.jbranch.app.server.fps.cmmgr001;

public class CMMGR001InputVO2 {
	private String roleid;
	private String itemid;
	private String itemname;
	private boolean maintenance;
	private boolean query;
	private boolean print;
	private boolean export;
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
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
	public boolean isExport() {
		return export;
	}
	public void setExport(boolean export) {
		this.export = export;
	}
	

}
