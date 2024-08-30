package com.systex.jbranch.app.server.fps.fps818;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS818InputVO extends PagingInputVO {
	private String plan_type;
	private List<String> chkPlan;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String vip_degree;
	private String ao_job_rank;
	private String plan_category;
	private String memo;
	private String is_valid;
	private String plan_status;
	private Date plan_sDate;
	private Date plan_eDate;
	private Date plan_update_sDate;
	private Date plan_update_eDate;
	private List<Map<String, Object>> totalList;
	
	
	public String getPlan_type() {
		return plan_type;
	}
	public void setPlan_type(String plan_type) {
		this.plan_type = plan_type;
	}
	public List<String> getChkPlan() {
		return chkPlan;
	}
	public void setChkPlan(List<String> chkPlan) {
		this.chkPlan = chkPlan;
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
	public String getVip_degree() {
		return vip_degree;
	}
	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}
	public String getAo_job_rank() {
		return ao_job_rank;
	}
	public void setAo_job_rank(String ao_job_rank) {
		this.ao_job_rank = ao_job_rank;
	}
	public String getPlan_category() {
		return plan_category;
	}
	public void setPlan_category(String plan_category) {
		this.plan_category = plan_category;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getIs_valid() {
		return is_valid;
	}
	public void setIs_valid(String is_valid) {
		this.is_valid = is_valid;
	}
	public String getPlan_status() {
		return plan_status;
	}
	public void setPlan_status(String plan_status) {
		this.plan_status = plan_status;
	}
	public Date getPlan_sDate() {
		return plan_sDate;
	}
	public void setPlan_sDate(Date plan_sDate) {
		this.plan_sDate = plan_sDate;
	}
	public Date getPlan_eDate() {
		return plan_eDate;
	}
	public void setPlan_eDate(Date plan_eDate) {
		this.plan_eDate = plan_eDate;
	}
	public Date getPlan_update_sDate() {
		return plan_update_sDate;
	}
	public void setPlan_update_sDate(Date plan_update_sDate) {
		this.plan_update_sDate = plan_update_sDate;
	}
	public Date getPlan_update_eDate() {
		return plan_update_eDate;
	}
	public void setPlan_update_eDate(Date plan_update_eDate) {
		this.plan_update_eDate = plan_update_eDate;
	}
	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}
	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}
}