package com.systex.jbranch.app.server.fps.sqm230;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM230InputVO extends PagingInputVO {
	private String yearMon;
	private String branchNbr;
	private String empId;
	private String caseNo;
	private String deductionInitial;
	private String deductionFinal;
	private List<Map<String,String>> resultList;
	private String emp_dp_4;
	private String im_sup_dp_4;
	private String so_sup_dp_4;
	private String emp_dp_5;
	private String im_sup_dp_5;
	private String so_sup_dp_5;
	
	public String getYearMon() {
		return yearMon;
	}
	
	public void setYearMon(String yearMon) {
		this.yearMon = yearMon;
	}
	
	public String getBranchNbr() {
		return branchNbr;
	}
	
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	
	public String getEmpId() {
		return empId;
	}
	
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
	public String getCaseNo() {
		return caseNo;
	}
	
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	
	public String getDeductionInitial() {
		return deductionInitial;
	}
	
	public void setDeductionInitial(String deductionInitial) {
		this.deductionInitial = deductionInitial;
	}
	
	public String getDeductionFinal() {
		return deductionFinal;
	}
	
	public void setDeductionFinal(String deductionFinal) {
		this.deductionFinal = deductionFinal;
	}
	
	public List<Map<String, String>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, String>> resultList) {
		this.resultList = resultList;
	}

	public String getEmp_dp_4() {
		return emp_dp_4;
	}

	public void setEmp_dp_4(String emp_dp_4) {
		this.emp_dp_4 = emp_dp_4;
	}

	public String getIm_sup_dp_4() {
		return im_sup_dp_4;
	}

	public void setIm_sup_dp_4(String im_sup_dp_4) {
		this.im_sup_dp_4 = im_sup_dp_4;
	}

	public String getSo_sup_dp_4() {
		return so_sup_dp_4;
	}

	public void setSo_sup_dp_4(String so_sup_dp_4) {
		this.so_sup_dp_4 = so_sup_dp_4;
	}

	public String getEmp_dp_5() {
		return emp_dp_5;
	}

	public void setEmp_dp_5(String emp_dp_5) {
		this.emp_dp_5 = emp_dp_5;
	}

	public String getIm_sup_dp_5() {
		return im_sup_dp_5;
	}

	public void setIm_sup_dp_5(String im_sup_dp_5) {
		this.im_sup_dp_5 = im_sup_dp_5;
	}

	public String getSo_sup_dp_5() {
		return so_sup_dp_5;
	}

	public void setSo_sup_dp_5(String so_sup_dp_5) {
		this.so_sup_dp_5 = so_sup_dp_5;
	}
	
}