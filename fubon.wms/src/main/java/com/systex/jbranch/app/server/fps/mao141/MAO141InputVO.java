package com.systex.jbranch.app.server.fps.mao141;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MAO141InputVO extends PagingInputVO{
	private String regionCenterId;
	private String bra_areaID;
	private String branchNbr;
	private String dev_nbr;
	private String dev_status;
	private List<Map<String, Object>> chkSEQ;
	private String dev_site_type;
	private String dev_take_emp;
	
	public String getRegionCenterId() {
		return regionCenterId;
	}
	public void setRegionCenterId(String regionCenterId) {
		this.regionCenterId = regionCenterId;
	}
	public String getBra_areaID() {
		return bra_areaID;
	}
	public void setBra_areaID(String bra_areaID) {
		this.bra_areaID = bra_areaID;
	}
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	public String getDev_nbr() {
		return dev_nbr;
	}
	public void setDev_nbr(String dev_nbr) {
		this.dev_nbr = dev_nbr;
	}
	public String getDev_status() {
		return dev_status;
	}
	public void setDev_status(String dev_status) {
		this.dev_status = dev_status;
	}
	public List<Map<String, Object>> getChkSEQ() {
		return chkSEQ;
	}
	public void setChkSEQ(List<Map<String, Object>> chkSEQ) {
		this.chkSEQ = chkSEQ;
	}
	public String getDev_site_type() {
		return dev_site_type;
	}
	public void setDev_site_type(String dev_site_type) {
		this.dev_site_type = dev_site_type;
	}
	public String getDev_take_emp() {
		return dev_take_emp;
	}
	public void setDev_take_emp(String dev_take_emp) {
		this.dev_take_emp = dev_take_emp;
	}
}
