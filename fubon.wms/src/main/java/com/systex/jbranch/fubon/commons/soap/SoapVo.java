package com.systex.jbranch.fubon.commons.soap;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class SoapVo {	
	private Charset requestCharSet;//請求編碼
	private Charset reponseCharSet;//回應編碼
	private Map<String , String> headers = new HashMap();//soap request data handler
	private String url;//soap request url
	private Map<String , String> nameSpace;//request tag's namespace
	private String soapRequestData;//soap request xml content
	private String soapResponseData;//soap response xml content
	private String tlsVersion;

	public Charset getRequestCharSet() {
		return requestCharSet;
	}
	public void setRequestCharSet(Charset requestCharSet) {
		this.requestCharSet = requestCharSet;
	}
	public Charset getReponseCharSet() {
		return reponseCharSet;
	}
	public void setReponseCharSet(Charset reponseCharSet) {
		this.reponseCharSet = reponseCharSet;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Map<String , String>  getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(Map<String , String>  nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getSoapRequestData() {
		return soapRequestData;
	}
	public void setSoapRequestData(String soapRequestData) {
		this.soapRequestData = soapRequestData;
	}
	public String getSoapResponseData() {
		return soapResponseData;
	}
	public void setSoapResponseData(String soapResponseData) {
		this.soapResponseData = soapResponseData;
	}
	public String getTlsVersion() {
		return tlsVersion;
	}
	public void setTlsVersion(String tlsVersion) {
		this.tlsVersion = tlsVersion;
	}
}