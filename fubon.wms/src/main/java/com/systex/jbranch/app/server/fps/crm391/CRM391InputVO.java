package com.systex.jbranch.app.server.fps.crm391;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM391InputVO extends PagingInputVO {
	
	private String cust_id;
	private Date apl_sDate;
	private Date apl_eDate;
	private String process;
	private Date apl_mgr_sDate;
	private Date apl_mgr_eDate;
	private String org_ao_brh;
	private String new_ao_brh;
	private Date op_mgr_sDate;
	private Date op_mgr_eDate;
	private String org_ao_code;
	private Date dc_mgr_sDate;
	private Date dc_mgr_eDate;
	private String new_ao_code;
	private Date hq_mgr_sDate;
	private Date hq_mgr_eDate;
	private String trs_type; //新增移轉類別
	private String org_uEmpID;
	private String new_uEmpID;
	
	public String getOrg_uEmpID() {
		return org_uEmpID;
	}

	public void setOrg_uEmpID(String org_uEmpID) {
		this.org_uEmpID = org_uEmpID;
	}

	public String getNew_uEmpID() {
		return new_uEmpID;
	}

	public void setNew_uEmpID(String new_uEmpID) {
		this.new_uEmpID = new_uEmpID;
	}

	public String getTrs_type() {
		return trs_type;
	}

	public void setTrs_type(String trs_type) {
		this.trs_type = trs_type;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public Date getApl_sDate() {
		return apl_sDate;
	}

	public void setApl_sDate(Date apl_sDate) {
		this.apl_sDate = apl_sDate;
	}

	public Date getApl_eDate() {
		return apl_eDate;
	}

	public void setApl_eDate(Date apl_eDate) {
		this.apl_eDate = apl_eDate;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public Date getApl_mgr_sDate() {
		return apl_mgr_sDate;
	}

	public void setApl_mgr_sDate(Date apl_mgr_sDate) {
		this.apl_mgr_sDate = apl_mgr_sDate;
	}

	public Date getApl_mgr_eDate() {
		return apl_mgr_eDate;
	}

	public void setApl_mgr_eDate(Date apl_mgr_eDate) {
		this.apl_mgr_eDate = apl_mgr_eDate;
	}

	public String getOrg_ao_brh() {
		return org_ao_brh;
	}

	public void setOrg_ao_brh(String org_ao_brh) {
		this.org_ao_brh = org_ao_brh;
	}

	public String getNew_ao_brh() {
		return new_ao_brh;
	}

	public void setNew_ao_brh(String new_ao_brh) {
		this.new_ao_brh = new_ao_brh;
	}

	public Date getOp_mgr_sDate() {
		return op_mgr_sDate;
	}

	public void setOp_mgr_sDate(Date op_mgr_sDate) {
		this.op_mgr_sDate = op_mgr_sDate;
	}

	public Date getOp_mgr_eDate() {
		return op_mgr_eDate;
	}

	public void setOp_mgr_eDate(Date op_mgr_eDate) {
		this.op_mgr_eDate = op_mgr_eDate;
	}

	public String getOrg_ao_code() {
		return org_ao_code;
	}

	public void setOrg_ao_code(String org_ao_code) {
		this.org_ao_code = org_ao_code;
	}

	public Date getDc_mgr_sDate() {
		return dc_mgr_sDate;
	}

	public void setDc_mgr_sDate(Date dc_mgr_sDate) {
		this.dc_mgr_sDate = dc_mgr_sDate;
	}

	public Date getDc_mgr_eDate() {
		return dc_mgr_eDate;
	}

	public void setDc_mgr_eDate(Date dc_mgr_eDate) {
		this.dc_mgr_eDate = dc_mgr_eDate;
	}

	public String getNew_ao_code() {
		return new_ao_code;
	}

	public void setNew_ao_code(String new_ao_code) {
		this.new_ao_code = new_ao_code;
	}

	public Date getHq_mgr_sDate() {
		return hq_mgr_sDate;
	}

	public void setHq_mgr_sDate(Date hq_mgr_sDate) {
		this.hq_mgr_sDate = hq_mgr_sDate;
	}

	public Date getHq_mgr_eDate() {
		return hq_mgr_eDate;
	}

	public void setHq_mgr_eDate(Date hq_mgr_eDate) {
		this.hq_mgr_eDate = hq_mgr_eDate;
	}
}