package com.systex.jbranch.ws.external.service.domain.insurance_status;

public class ReturnVO {
	private ProcessResult processResult;
	private Object queryResult;     // API Result（可能會多筆陣列型態）
	public ProcessResult getProcessResult() {
		return processResult;
	}
	public void setProcessResult(ProcessResult processResult) {
		this.processResult = processResult;
	}
	public Object getQueryResult() {
		return queryResult;
	}
	public void setQueryResult(Object queryResult) {
		this.queryResult = queryResult;
	}

	
	

}
