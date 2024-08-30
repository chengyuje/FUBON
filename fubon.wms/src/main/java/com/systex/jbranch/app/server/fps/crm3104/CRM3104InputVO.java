package com.systex.jbranch.app.server.fps.crm3104;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM3104InputVO extends PagingInputVO{
	private String PRJ_ID;                   //專案代號
	private String region_center_id;
	private String branch_area_id;
	private String bra_nbr;
	private String emp_id;
	private List<Map<String, Object>> printList;
	private Date PRJ_EXE_DATE;
	
	public String getPRJ_ID() {
		return PRJ_ID;
	}
	public void setPRJ_ID(String pRJ_ID) {
		PRJ_ID = pRJ_ID;
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
	public String getBra_nbr() {
		return bra_nbr;
	}
	public void setBra_nbr(String bra_nbr) {
		this.bra_nbr = bra_nbr;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public List<Map<String, Object>> getPrintList() {
		return printList;
	}
	public void setPrintList(List<Map<String, Object>> printList) {
		this.printList = printList;
	}
	public Date getPRJ_EXE_DATE() {
		return PRJ_EXE_DATE;
	}
	public void setPRJ_EXE_DATE(Date pRJ_EXE_DATE) {
		PRJ_EXE_DATE = pRJ_EXE_DATE;
	}
	
}
