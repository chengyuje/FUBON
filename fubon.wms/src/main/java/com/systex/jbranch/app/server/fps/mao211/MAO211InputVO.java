package com.systex.jbranch.app.server.fps.mao211;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MAO211InputVO extends PagingInputVO{
	
	private String branchNbr;
	private Date use_date;
	private Date apl_yn;
	private String cust_id;
	private String cust_name;
	private String ao_list;
	private String ao_code;
	private String emp_id;
	private String dev_nbr;
	private String use_period;
	private String custString;
	private List<String> custList;
	private List<String> custList2;
	private String dev_site_type;
	private List<Integer> useTimeList;
	private List<Map<String,Object>> relationshipList;
	
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	public Date getApl_yn() {
		return apl_yn;
	}
	public void setApl_yn(Date apl_yn) {
		this.apl_yn = apl_yn;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getAo_list() {
		return ao_list;
	}
	public void setAo_list(String ao_list) {
		this.ao_list = ao_list;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getDev_nbr() {
		return dev_nbr;
	}
	public void setDev_nbr(String dev_nbr) {
		this.dev_nbr = dev_nbr;
	}
	public Date getUse_date() {
		return use_date;
	}
	public void setUse_date(Date use_date) {
		this.use_date = use_date;
	}
	public String getUse_period() {
		return use_period;
	}
	public void setUse_period(String use_period) {
		this.use_period = use_period;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getCustString() {
		return custString;
	}
	public void setCustString(String custString) {
		this.custString = custString;
	}
	public List<String> getCustList() {
		return custList;
	}
	public void setCustList(List<String> custList) {
		this.custList = custList;
	}
	public List<String> getCustList2() {
		return custList2;
	}
	public void setCustList2(List<String> custList2) {
		this.custList2 = custList2;
	}
	public String getDev_site_type() {
		return dev_site_type;
	}
	public void setDev_site_type(String dev_site_type) {
		this.dev_site_type = dev_site_type;
	}
	public List<Integer> getUseTimeList() {
		return useTimeList;
	}
	public void setUseTimeList(List<Integer> useTimeList) {
		this.useTimeList = useTimeList;
	}
	public List<Map<String, Object>> getRelationshipList() {
		return relationshipList;
	}
	public void setRelationshipList(List<Map<String, Object>> relationshipList) {
		this.relationshipList = relationshipList;
	}
	
}