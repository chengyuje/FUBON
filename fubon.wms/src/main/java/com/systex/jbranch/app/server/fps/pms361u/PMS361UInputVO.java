package com.systex.jbranch.app.server.fps.pms361u;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS361UInputVO extends PagingInputVO {
	private Date sDate;
	private Date eDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String needConfirmYN;
	private String showNoData;
	
	private List<Map<String, Object>> totalList;
	
	
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
	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}
	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}
	public String getNeedConfirmYN() {
		return needConfirmYN;
	}
	public void setNeedConfirmYN(String needConfirmYN) {
		this.needConfirmYN = needConfirmYN;
	}
	public String getShowNoData() {
		return showNoData;
	}
	public void setShowNoData(String showNoData) {
		this.showNoData = showNoData;
	}
	
}