package com.systex.jbranch.ws.external.service;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.systex.jbranch.comutil.callBack.CallBackExcute;


public class ExternalConfig {
	private String id;
	private String inName;
	private String outName;
	private String bizCode;
	private String txnCode;
	private String outputType;
	private CallBackExcute beforeFormat;
	private CallBackExcute returnFormat;
	private Map externalVoConfig;
	
	public String getInputVoPath(){
		return ObjectUtils.toString(externalVoConfig.get(getInName()));
	}
	
	public String getOutVoPath(){
		return ObjectUtils.toString(externalVoConfig.get(getOutName()));
	}

	public String getInName() {
		return inName;
	}
	public void setInName(String inName) {
		this.inName = inName;
	}
	public String getOutName() {
		return outName;
	}
	public void setOutName(String outName) {
		this.outName = outName;
	}
	public String getBizCode() {
		return bizCode;
	}
	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}
	public String getTxnCode() {
		return txnCode;
	}
	public void setTxnCode(String txnCode) {
		this.txnCode = txnCode;
	}
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	public CallBackExcute getReturnFormat() {
		return returnFormat;
	}
	public void setReturnFormat(CallBackExcute returnFormat) {
		this.returnFormat = returnFormat;
	}
	public Map getExternalVoConfig() {
		return externalVoConfig;
	}
	public void setExternalVoConfig(Map externalVoConfig) {
		this.externalVoConfig = externalVoConfig;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CallBackExcute getBeforeFormat() {
		return beforeFormat;
	}

	public void setBeforeFormat(CallBackExcute beforeFormat) {
		this.beforeFormat = beforeFormat;
	}


	
}
