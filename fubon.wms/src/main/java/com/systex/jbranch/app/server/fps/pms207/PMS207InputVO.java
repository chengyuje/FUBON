package com.systex.jbranch.app.server.fps.pms207;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS207InputVO extends PagingInputVO {
	private String dataMonth;
	private String sttType;
	private String emp_id;       //員編
	private String work_dt;
	private String branch_nbr;
	private String ao_code;

	public String getDataMonth() {
		return dataMonth;
	}

	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}

	public String getSttType() {
		return sttType;
	}

	public void setSttType(String sttType) {
		this.sttType = sttType;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getWork_dt() {
		return work_dt;
	}

	public void setWork_dt(String work_dt) {
		this.work_dt = work_dt;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	

}
