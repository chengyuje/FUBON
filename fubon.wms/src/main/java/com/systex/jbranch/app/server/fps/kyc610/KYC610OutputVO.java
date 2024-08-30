package com.systex.jbranch.app.server.fps.kyc610;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC610OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> questionnaireList;
	private String custName;
	private Map<String,Object> resultMap;
	private String examVersion;
	
	public List<Map<String, Object>> getQuestionnaireList() {
		return questionnaireList;
	}

	public void setQuestionnaireList(List<Map<String, Object>> questionnaireList) {
		this.questionnaireList = questionnaireList;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}

	public String getExamVersion() {
		return examVersion;
	}

	public void setExamVersion(String examVersion) {
		this.examVersion = examVersion;
	}
	
	

}
