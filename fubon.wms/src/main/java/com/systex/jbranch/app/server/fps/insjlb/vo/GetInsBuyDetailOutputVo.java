package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;
import java.util.Map;

public class GetInsBuyDetailOutputVo {
	private List errorMsg;
	private Map<String , List<Map<String , Object>>> tempArrayInsseq1;
	private Map<String , List<Map<String , Object>>> tempArrayInsseq2;
	public List getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(List errorMsg) {
		this.errorMsg = errorMsg;
	}
	public Map<String, List<Map<String, Object>>> getTempArrayInsseq1() {
		return tempArrayInsseq1;
	}
	public void setTempArrayInsseq1(
			Map<String, List<Map<String, Object>>> tempArrayInsseq1) {
		this.tempArrayInsseq1 = tempArrayInsseq1;
	}
	public Map<String, List<Map<String, Object>>> getTempArrayInsseq2() {
		return tempArrayInsseq2;
	}
	public void setTempArrayInsseq2(
			Map<String, List<Map<String, Object>>> tempArrayInsseq2) {
		this.tempArrayInsseq2 = tempArrayInsseq2;
	}
}
