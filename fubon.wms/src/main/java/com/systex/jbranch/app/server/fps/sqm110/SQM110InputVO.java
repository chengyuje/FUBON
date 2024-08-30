package com.systex.jbranch.app.server.fps.sqm110;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM110InputVO extends PagingInputVO{	
	
	private Date eCreDate;
	/***** 可試範圍專用START *****/
	private Date sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String cust_id;
	private String qtn_type; //問券別
	private String case_no;  // 案件編號
	private String satisfaction_o; //滿意度
	private String case_status; //處理狀態
	
	
	/***** 可試範圍專用END *****/
	
	private List<Map<String, Object>> checkList;
	private List<Map<String, Object>> list2;
	public Date geteCreDate() {
		return eCreDate;
	}
	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}
	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getQtn_type() {
		return qtn_type;
	}
	public void setQtn_type(String qtn_type) {
		this.qtn_type = qtn_type;
	}
	public String getCase_no() {
		return case_no;
	}
	public void setCase_no(String case_no) {
		this.case_no = case_no;
	}
	
	public List<Map<String, Object>> getCheckList() {
		return checkList;
	}
	public void setCheckList(List<Map<String, Object>> checkList) {
		this.checkList = checkList;
	}
	public List<Map<String, Object>> getList2() {
		return list2;
	}
	public void setList2(List<Map<String, Object>> list2) {
		this.list2 = list2;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getSatisfaction_o() {
		return satisfaction_o;
	}
	public void setSatisfaction_o(String satisfaction_o) {
		this.satisfaction_o = satisfaction_o;
	}
	public String getCase_status() {
		return case_status;
	}
	public void setCase_status(String case_status) {
		this.case_status = case_status;
	}
	
	
}
