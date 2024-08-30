package com.systex.jbranch.fubon.commons.esb.vo.nfbrnh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 動態鎖利贖回
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRNHOutputVO {
	@XmlElement
	private String ErrorCode;			//錯誤碼
    @XmlElement
	private String ErrorMsg;			//錯誤訊息
    @XmlElement
	private String EviNum;				//憑證編號
    @XmlElement
	private String Short;				//短線交易 1:真短線 2:假短線 空白:無短線
    
    
	public String getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}
	public String getErrorMsg() {
		return ErrorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		ErrorMsg = errorMsg;
	}
	public String getEviNum() {
		return EviNum;
	}
	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}
	public String getShort() {
		return Short;
	}
	public void setShort(String s) {
		Short = s;
	}
	
}
