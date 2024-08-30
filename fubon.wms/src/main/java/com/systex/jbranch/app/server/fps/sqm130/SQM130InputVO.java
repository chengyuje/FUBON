package com.systex.jbranch.app.server.fps.sqm130;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM130InputVO extends PagingInputVO {
	
	private String trade_date_ys;
	private String trade_date_ms;
	private String trade_date_ye;
	private String trade_date_me;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String report_type;
	private String emp_id;
	private List<Map<String,String>> paramList;
	public List<Map<String, String>> getParamList() {
		return paramList;
	}
	public void setParamList(List<Map<String, String>> paramList) {
		this.paramList = paramList;
	}
	public String getTradeDateYS() {
		return trade_date_ys;
	}
	public void setTradeDateYS(String tradeDateYS) {
		this.trade_date_ys = tradeDateYS;
	}
	public String getTradeDateMS() {
		return trade_date_ms;
	}
	public void setTradeDateMS(String tradeDateMS) {
		this.trade_date_ms = tradeDateMS;
	}
	public String getTradeDateYE() {
		return trade_date_ye;
	}
	public void setTradeDateYE(String tradeDateYE) {
		this.trade_date_ye = tradeDateYE;
	}
	public String getTradeDateME() {
		return trade_date_me;
	}
	public void setTradeDateME(String tradeDateME) {
		this.trade_date_me = tradeDateME;
	}
	public String getRegionCenterId() {
		return region_center_id;
	}
	public void setRegionCenterId(String regionCenterId) {
		this.region_center_id = regionCenterId;
	}
	public String getBranchAreaId() {
		return branch_area_id;
	}
	public void setBranchAreaId(String branchAreaId) {
		this.branch_area_id = branchAreaId;
	}
	public String getBranchNbr() {
		return branch_nbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branch_nbr = branchNbr;
	}
	public String getEmpId() {
		return emp_id;
	}
	public void setEmpId(String empId) {
		this.emp_id = empId;
	}
	public String getReportType() {
		return report_type;
	}
	public void setReportType(String reportType) {
		this.report_type = reportType;
	}
}