package com.systex.jbranch.app.server.fps.pms120;

import java.math.BigDecimal;
import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS120InputVO extends PagingInputVO {
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String dataMonth;
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
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getDataMonth() {
		return dataMonth;
	}
	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}
	
    
	
}
