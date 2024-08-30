package com.systex.jbranch.app.server.fps.fps817;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS817InputVO extends PagingInputVO {
	private Boolean isManger;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_job_rank;
	private String planning;
	private String plan_category;
	private String vip_degree;
	private String memo;
	private Date sDate;
	private Date eDate;
	
	
	public Boolean getIsManger() {
		return isManger;
	}
	public void setIsManger(Boolean isManger) {
		this.isManger = isManger;
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
	public String getAo_job_rank() {
		return ao_job_rank;
	}
	public void setAo_job_rank(String ao_job_rank) {
		this.ao_job_rank = ao_job_rank;
	}
	public String getPlanning() {
		return planning;
	}
	public void setPlanning(String planning) {
		this.planning = planning;
	}
	public String getPlan_category() {
		return plan_category;
	}
	public void setPlan_category(String plan_category) {
		this.plan_category = plan_category;
	}
	public String getVip_degree() {
		return vip_degree;
	}
	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
}