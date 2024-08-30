package com.systex.jbranch.app.server.fps.pms310;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS310InputVO extends PagingInputVO {
	private String tarType;
	private String branch_nbr;
	private String emp_id;
	private String fileName;
	private String reportDate;
	private String types;
	private String NBR_state;
	private String PS_state;
	private String PSEmpId;

	public String getPS_state() {
		return PS_state;
	}

	public void setPS_state(String pS_state) {
		PS_state = pS_state;
	}

	public String getPSEmpId() {
		return PSEmpId;
	}

	public void setPSEmpId(String pSEmpId) {
		PSEmpId = pSEmpId;
	}

	public String getNBR_state() {
		return NBR_state;
	}

	public void setNBR_state(String nBR_state) {
		NBR_state = nBR_state;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getTarType() {
		return tarType;
	}

	public void setTarType(String tarType) {
		this.tarType = tarType;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

}
