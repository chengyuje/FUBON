package com.systex.jbranch.app.server.fps.crm181;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM181InputVO extends PagingInputVO {

	private String empId;
	private String roleId;
	private String priId;
	private String role_link_yn;
	private String frq_type;
	private String frq_mwd;
	private String display_no;
	private String rpt_name;
	private String rpt_prog_url;
	private String call_func_name;
	private String pass_params;
	private BigDecimal display_day;
	private String dataType;
	private String seqNo;
	private String seqM_No;
	private String pri_type;
	
	private String memLoginFlag;

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getPriId() {
		return priId;
	}

	public void setPriId(String priId) {
		this.priId = priId;
	}

	public String getFrq_type() {
		return frq_type;
	}

	public void setFrq_type(String frq_type) {
		this.frq_type = frq_type;
	}

	public String getFrq_mwd() {
		return frq_mwd;
	}

	public void setFrq_mwd(String frq_mwd) {
		this.frq_mwd = frq_mwd;
	}

	public String getDisplay_no() {
		return display_no;
	}

	public void setDisplay_no(String display_no) {
		this.display_no = display_no;
	}

	public String getRpt_name() {
		return rpt_name;
	}

	public void setRpt_name(String rpt_name) {
		this.rpt_name = rpt_name;
	}

	public String getRpt_prog_url() {
		return rpt_prog_url;
	}

	public void setRpt_prog_url(String rpt_prog_url) {
		this.rpt_prog_url = rpt_prog_url;
	}

	public String getCall_func_name() {
		return call_func_name;
	}

	public void setCall_func_name(String call_func_name) {
		this.call_func_name = call_func_name;
	}

	public String getPass_params() {
		return pass_params;
	}

	public void setPass_params(String pass_params) {
		this.pass_params = pass_params;
	}

	public String getRole_link_yn() {
		return role_link_yn;
	}

	public void setRole_link_yn(String role_link_yn) {
		this.role_link_yn = role_link_yn;
	}

	public String getSeqM_No() {
		return seqM_No;
	}

	public void setSeqM_No(String seqM_No) {
		this.seqM_No = seqM_No;
	}

	public BigDecimal getDisplay_day() {
		return display_day;
	}

	public void setDisplay_day(BigDecimal display_day) {
		this.display_day = display_day;
	}

	public String getPri_type() {
		return pri_type;
	}

	public void setPri_type(String pri_type) {
		this.pri_type = pri_type;
	}

}
