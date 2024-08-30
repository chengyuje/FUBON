package com.systex.jbranch.app.server.fps.crm512;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM512InputVO extends PagingInputVO {

	private String custID;
	
	private List<Map<String, Object>> qusBankListBefore;
	private List<Map<String, Object>> qusBankListAfter;
	
	private String examVersion;
	private String questionVersion;
	
	private String bossEmpID;
	private String bossEmpPWD;
	private String checkBossFlag;
	
	public String getCheckBossFlag() {
		return checkBossFlag;
	}

	public void setCheckBossFlag(String checkBossFlag) {
		this.checkBossFlag = checkBossFlag;
	}

	public String getBossEmpID() {
		return bossEmpID;
	}

	public void setBossEmpID(String bossEmpID) {
		this.bossEmpID = bossEmpID;
	}

	public String getBossEmpPWD() {
		return bossEmpPWD;
	}

	public void setBossEmpPWD(String bossEmpPWD) {
		this.bossEmpPWD = bossEmpPWD;
	}

	public String getExamVersion() {
		return examVersion;
	}

	public void setExamVersion(String examVersion) {
		this.examVersion = examVersion;
	}

	public String getQuestionVersion() {
		return questionVersion;
	}

	public void setQuestionVersion(String questionVersion) {
		this.questionVersion = questionVersion;
	}

	public List<Map<String, Object>> getQusBankListBefore() {
		return qusBankListBefore;
	}

	public void setQusBankListBefore(List<Map<String, Object>> qusBankListBefore) {
		this.qusBankListBefore = qusBankListBefore;
	}

	public List<Map<String, Object>> getQusBankListAfter() {
		return qusBankListAfter;
	}

	public void setQusBankListAfter(List<Map<String, Object>> qusBankListAfter) {
		this.qusBankListAfter = qusBankListAfter;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

}
