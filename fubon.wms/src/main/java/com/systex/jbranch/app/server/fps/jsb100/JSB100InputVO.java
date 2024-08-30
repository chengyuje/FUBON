package com.systex.jbranch.app.server.fps.jsb100;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class JSB100InputVO extends PagingInputVO{
	private String cust_id;
	private String ins_id;
	private String seq;
	private String policy_nbr;
	private String policy_simp_name;	
	private String policy_full_name;	
	private String acceptid;
	private String update_status;
	private String contract_status;
	private String seq_no;
	private String appl_name;
	private String ins_name;
	private Date proposer_birth;
	private Date insured_birth;
	private String project_code;
	private String pay_type;
	private String policy_assure_amt;
	private String unit_nbr;
	private String service_emp_id;
	private Date data_date;
	private String update_reason;
	private String status;
	private Date user_update_date;
	private Date user_update_date_s;
	private Date user_update_date_e;
	
	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getIns_id() {
		return ins_id;
	}

	public void setIns_id(String ins_id) {
		this.ins_id = ins_id;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getPolicy_nbr() {
		return policy_nbr;
	}

	public void setPolicy_nbr(String policy_nbr) {
		this.policy_nbr = policy_nbr;
	}

	public String getPolicy_simp_name() {
		return policy_simp_name;
	}

	public void setPolicy_simp_name(String policy_simp_name) {
		this.policy_simp_name = policy_simp_name;
	}

	public String getPolicy_full_name() {
		return policy_full_name;
	}

	public void setPolicy_full_name(String policy_full_name) {
		this.policy_full_name = policy_full_name;
	}

	public String getAcceptid() {
		return acceptid;
	}

	public void setAcceptid(String acceptid) {
		this.acceptid = acceptid;
	}

	public String getUpdate_status() {
		return update_status;
	}

	public void setUpdate_status(String update_status) {
		this.update_status = update_status;
	}

	public String getContract_status() {
		return contract_status;
	}

	public void setContract_status(String contract_status) {
		this.contract_status = contract_status;
	}
	
	public String getSeq_no() {
		return seq_no;
	}

	public void setSeq_no(String seq_no) {
		this.seq_no = seq_no;
	}

	public String getAppl_name() {
		return appl_name;
	}

	public void setAppl_name(String appl_name) {
		this.appl_name = appl_name;
	}

	public String getIns_name() {
		return ins_name;
	}

	public void setIns_name(String ins_name) {
		this.ins_name = ins_name;
	}

	public Date getProposer_birth() {
		return proposer_birth;
	}

	public void setProposer_birth(Date proposer_birth) {
		this.proposer_birth = proposer_birth;
	}

	public Date getInsured_birth() {
		return insured_birth;
	}

	public void setInsured_birth(Date insured_birth) {
		this.insured_birth = insured_birth;
	}

	public String getProject_code() {
		return project_code;
	}

	public void setProject_code(String project_code) {
		this.project_code = project_code;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getPolicy_assure_amt() {
		return policy_assure_amt;
	}

	public void setPolicy_assure_amt(String policy_assure_amt) {
		this.policy_assure_amt = policy_assure_amt;
	}

	public String getUnit_nbr() {
		return unit_nbr;
	}

	public void setUnit_nbr(String unit_nbr) {
		this.unit_nbr = unit_nbr;
	}

	public String getService_emp_id() {
		return service_emp_id;
	}

	public void setService_emp_id(String service_emp_id) {
		this.service_emp_id = service_emp_id;
	}

	public Date getData_date() {
		return data_date;
	}

	public void setData_date(Date data_date) {
		this.data_date = data_date;
	}

	public String getUpdate_reason() {
		return update_reason;
	}

	public void setUpdate_reason(String update_reason) {
		this.update_reason = update_reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getUser_update_date() {
		return user_update_date;
	}

	public void setUser_update_date(Date user_update_date) {
		this.user_update_date = user_update_date;
	}

	public Date getUser_update_date_s() {
		return user_update_date_s;
	}

	public void setUser_update_date_s(Date user_update_date_s) {
		this.user_update_date_s = user_update_date_s;
	}

	public Date getUser_update_date_e() {
		return user_update_date_e;
	}

	public void setUser_update_date_e(Date user_update_date_e) {
		this.user_update_date_e = user_update_date_e;
	}
	
}
