package com.systex.jbranch.fubon.commons.esb.vo.ebpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType; 

/**
 * AI BANK快速下單 – 發送電文至AI BANK, 行事曆表單上可以出現快速申購之訊息
 * Created by Jeff on 2024/06/27.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EBPMN2OutputVO {
	@XmlElement
	private String errorCode; // 錯誤代碼
	@XmlElement
	private String errorMsg; // 錯誤說明

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
