package com.systex.jbranch.app.server.fps.sqm140;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM140InputVO extends PagingInputVO{	
	
	private Date eCreDate;
	/***** 可試範圍專用START *****/
	private Date sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String cust_id;
	private String qtn_type; //問券別
	private String deduction_initial; //處長簽核意見
	private String deduction_final; // 扣分
	private String ho_check; //放行
	private String case_no;  // 案件編號
	
	private List<Map<String, Object>> delList;  // 需刪除的SEQ
	
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
	public String getDeduction_initial() {
		return deduction_initial;
	}
	public void setDeduction_initial(String deduction_initial) {
		this.deduction_initial = deduction_initial;
	}
	public String getDeduction_final() {
		return deduction_final;
	}
	public void setDeduction_final(String deduction_final) {
		this.deduction_final = deduction_final;
	}
	public String getHo_check() {
		return ho_check;
	}
	public void setHo_check(String ho_check) {
		this.ho_check = ho_check;
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
	public List<Map<String, Object>> getDelList() {
		return delList;
	}
	public void setDelList(List<Map<String, Object>> delList) {
		this.delList = delList;
	}
	
	
}
