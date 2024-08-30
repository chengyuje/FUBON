package com.systex.jbranch.app.server.fps.crm321;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM321InputVO extends PagingInputVO{
	private String regionCenterId;
	private String branchAreaId;
	private String branchNbr;
	
	private String fch_regionCenterId;
	private String fch_branchAreaId;
	private String fch_branchNbr;
	
	private String crm_trs_total_fch_cust_no;
	private String fch_mast_brh;
	private String orifch_mast_brh;
	private String fch_mast_brh_row;
	private String ass_brh;
	private String oriass_brh;
	private String priority_order;
	private String oripriority_order;
	private List<Map<String, Object>> list;
	public String getRegionCenterId() {
		return regionCenterId;
	}
	public void setRegionCenterId(String regionCenterId) {
		this.regionCenterId = regionCenterId;
	}
	public String getBranchAreaId() {
		return branchAreaId;
	}
	public void setBranchAreaId(String branchAreaId) {
		this.branchAreaId = branchAreaId;
	}
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	public String getFch_regionCenterId() {
		return fch_regionCenterId;
	}
	public void setFch_regionCenterId(String fch_regionCenterId) {
		this.fch_regionCenterId = fch_regionCenterId;
	}
	public String getFch_branchAreaId() {
		return fch_branchAreaId;
	}
	public void setFch_branchAreaId(String fch_branchAreaId) {
		this.fch_branchAreaId = fch_branchAreaId;
	}
	public String getFch_branchNbr() {
		return fch_branchNbr;
	}
	public void setFch_branchNbr(String fch_branchNbr) {
		this.fch_branchNbr = fch_branchNbr;
	}
	public String getCrm_trs_total_fch_cust_no() {
		return crm_trs_total_fch_cust_no;
	}
	public void setCrm_trs_total_fch_cust_no(String crm_trs_total_fch_cust_no) {
		this.crm_trs_total_fch_cust_no = crm_trs_total_fch_cust_no;
	}
	public String getFch_mast_brh() {
		return fch_mast_brh;
	}
	public void setFch_mast_brh(String fch_mast_brh) {
		this.fch_mast_brh = fch_mast_brh;
	}
	public String getOrifch_mast_brh() {
		return orifch_mast_brh;
	}
	public void setOrifch_mast_brh(String orifch_mast_brh) {
		this.orifch_mast_brh = orifch_mast_brh;
	}
	public String getFch_mast_brh_row() {
		return fch_mast_brh_row;
	}
	public void setFch_mast_brh_row(String fch_mast_brh_row) {
		this.fch_mast_brh_row = fch_mast_brh_row;
	}
	public String getAss_brh() {
		return ass_brh;
	}
	public void setAss_brh(String ass_brh) {
		this.ass_brh = ass_brh;
	}
	public String getOriass_brh() {
		return oriass_brh;
	}
	public void setOriass_brh(String oriass_brh) {
		this.oriass_brh = oriass_brh;
	}
	public String getPriority_order() {
		return priority_order;
	}
	public void setPriority_order(String priority_order) {
		this.priority_order = priority_order;
	}
	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	public String getOripriority_order() {
		return oripriority_order;
	}
	public void setOripriority_order(String oripriority_order) {
		this.oripriority_order = oripriority_order;
	}	
}
