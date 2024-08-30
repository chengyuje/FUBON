package com.systex.jbranch.fubon.commons.esb.vo.ebpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType; 

/**
 * 網路快速下單 – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息
 * Created by Valentino on 2017/03/23.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EBPMNOutputVO {  
	@XmlElement
	private String errorCode; // 錯誤代碼
	@XmlElement
	private String errorMsg;  // 錯誤說明
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
