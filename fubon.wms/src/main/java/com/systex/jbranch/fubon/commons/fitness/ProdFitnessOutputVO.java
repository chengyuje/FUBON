package com.systex.jbranch.fubon.commons.fitness;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class ProdFitnessOutputVO {
	private Boolean isError;
	private String errorID;
	private StringBuilder warningMsg = new StringBuilder();
	
	/**
	 * Constructor
	 */
	public ProdFitnessOutputVO() {
		this.setIsError(Boolean.FALSE);
	}
	
	/**
	 * Constructor
	 * @param type：訊息類別
	 * @param id：訊息代碼
	 */
//	public ProdFitnessOutputVO(String type, String id) {
//		this.setMessage(type, id);
//	}
	

	
	public Boolean getIsError() {
		return isError;
	}

	public void setIsError(Boolean isError) {
		this.isError = isError;
	}

	public String getErrorID() {
		return errorID;
	}

	public void setErrorID(String errorID) {
		if(StringUtils.isNotBlank(errorID)) 
			this.setIsError(Boolean.TRUE);
		else
			this.setIsError(Boolean.FALSE);
		
		this.errorID = errorID;
	}

	public StringBuilder getWarningMsg() {
		return warningMsg;
	}

	public void setWarningMsg(String warningMsg) {
		if (StringUtils.isBlank(this.warningMsg.toString())) {
			this.warningMsg = this.warningMsg.append(warningMsg);
		} else {
			this.warningMsg = this.warningMsg.append("; ").append(warningMsg);
		}		
	}


//	/**
//	 * 是否為空VO
//	 * @return
//	 */
//	public Boolean isEmpty() {
//		return (StringUtils.isBlank(this.getMsgType()) && StringUtils.isBlank(this.getMsgID()) 
//				? Boolean.TRUE : Boolean.FALSE);
//	}
//	
//	/**
//	 * 是否為非空VO
//	 * @return
//	 */
//	public Boolean isNotEmpty() {
//		return (!StringUtils.isBlank(this.getMsgType()) || !StringUtils.isBlank(this.getMsgID()) 
//				? Boolean.TRUE : Boolean.FALSE);
//	}
}