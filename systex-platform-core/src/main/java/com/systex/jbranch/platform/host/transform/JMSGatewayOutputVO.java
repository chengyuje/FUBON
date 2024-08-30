package com.systex.jbranch.platform.host.transform;

import java.io.Serializable;
import java.util.List;

public class JMSGatewayOutputVO implements Serializable{

	private String requestID;
	private long processTime;	//毫秒
	private byte[] cdKey;
	private String code;	//回應代號
	private String desc;	//代號說明
	private List<byte[]> content;	//tota
	private String hostName;	//主機名稱

	/**
	 * @return the requestID
	 */
	public String getRequestID() {
		return requestID;
	}

	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	/**
	 * 本次電文處理時間，單位為毫秒
	 * @return the processTime
	 */
	public long getProcessTime() {
		return processTime;
	}

	/**
	 * @param processTime the processTime to set
	 */
	public void setProcessTime(long processTime) {
		this.processTime = processTime;
	}
	
	/**
	 * @return the cdKey
	 */
	public byte[] getCdKey() {
		return cdKey;
	}

	/**
	 * @param cdKey the cdKey to set
	 */
	public void setCdKey(byte[] cdKey) {
		this.cdKey = cdKey;
	}

	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the content
	 */
	public List<byte[]> getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(List<byte[]> content) {
		this.content = content;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
}
