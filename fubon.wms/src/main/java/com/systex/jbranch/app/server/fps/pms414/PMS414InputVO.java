package com.systex.jbranch.app.server.fps.pms414;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS414InputVO extends PagingInputVO {

	private Date sCreDate;
	private Date eCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String needConfirmYN;

	private List<Map<String, Object>> list;
	private List<Map<String, Object>> list2;

	private String reason;
	private String selectRoleID;
	
	private String bakFlag;

	public String getBakFlag() {
		return bakFlag;
	}

	public void setBakFlag(String bakFlag) {
		this.bakFlag = bakFlag;
	}

	public String getSelectRoleID() {
		return selectRoleID;
	}

	public void setSelectRoleID(String selectRoleID) {
		this.selectRoleID = selectRoleID;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public List<Map<String, Object>> getList2() {
		return list2;
	}

	public void setList2(List<Map<String, Object>> list2) {
		this.list2 = list2;
	}

	public String getNeedConfirmYN() {
		return needConfirmYN;
	}

	public void setNeedConfirmYN(String needConfirmYN) {
		this.needConfirmYN = needConfirmYN;
	}

}
