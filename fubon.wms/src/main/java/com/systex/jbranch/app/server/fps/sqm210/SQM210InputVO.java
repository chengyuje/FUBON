package com.systex.jbranch.app.server.fps.sqm210;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM210InputVO extends PagingInputVO {
	
	private String yearMon;
	private String branchNbr;
	private String empId;
	private String jobTitleName;
	private String qtnType;
	private String satisfactionO;
	private String empName;
	private String respNo;
	private String deductionFinal;
	private String custId;
	private String curJob;
	private List<Map<String,String>> paramList;
	public List<Map<String, String>> getParamList() {
		return paramList;
	}
	public void setParamList(List<Map<String, String>> paramList) {
		this.paramList = paramList;
	}
	public String getCurJob() {
		return curJob;
	}
	public void setCurJob(String curJob) {
		this.curJob = curJob;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getRespNo() {
		return respNo;
	}
	public void setRespNo(String respNo) {
		this.respNo = respNo;
	}
	public String getDeductionFinal() {
		return deductionFinal;
	}
	public void setDeductionFinal(String deductionFinal) {
		this.deductionFinal = deductionFinal;
	}
	private List<Map<String, Object>> list;
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
	public String getJobTitleName() {
		return jobTitleName;
	}
	public void setJobTitleName(String jobTitleName) {
		this.jobTitleName = jobTitleName;
	}
	public String getQtnType() {
		return qtnType;
	}
	public void setQtnType(String qtnType) {
		this.qtnType = qtnType;
	}
	public String getSatisfactionO() {
		return satisfactionO;
	}
	public void setSatisfactionO(String satisfactionO) {
		this.satisfactionO = satisfactionO;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}