package com.systex.jbranch.app.server.fps.pms208;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 落點排名(含晉級目標達成率)<br>
 * Comments Name : pms208InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS208InputVO extends PagingInputVO {
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String reportDate;
	private String type; // QTD/MTD/YTD
	private String emp_id;
	
	
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

	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	
}
