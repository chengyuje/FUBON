package com.systex.jbranch.ws.external.service;

public class ExternalErrorMsg{
	private String rtnCode;
	private String rtnMsg;
	private Object content;
	
	private ExternalErrorMsg(String rtnCode , String rtnMsg){
		this.rtnCode = rtnCode;
		this.rtnMsg = rtnMsg;
	}
	
	private ExternalErrorMsg(String rtnCode , String rtnMsg , Object content){
		this(rtnCode , rtnMsg);
		this.content = content;
	}
	
	public static ExternalErrorMsg getInstance(String rtnCode , String rtnMsg){
		return new ExternalErrorMsg(rtnCode , rtnMsg);
	}
	
	public static ExternalErrorMsg getInstance(String rtnCode , String rtnMsg , Object content){
		return new ExternalErrorMsg(rtnCode , rtnMsg , content);
	}
	
	public String getRtnCode() {
		return rtnCode;
	}
	public void setRtnCode(String rtnCode) {
		this.rtnCode = rtnCode;
	}
	public String getRtnMsg() {
		return rtnMsg;
	}
	public void setRtnMsg(String rtnMsg) {
		this.rtnMsg = rtnMsg;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
}
