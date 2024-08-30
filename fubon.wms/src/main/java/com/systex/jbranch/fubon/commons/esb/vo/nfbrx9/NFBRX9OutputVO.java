package com.systex.jbranch.fubon.commons.esb.vo.nfbrx9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRX9OutputVO {
	
	@XmlElement
	private String ErrorCode;
	
    @XmlElement
	private String ErrorMsg;
    
    @XmlElement(name="TxRepeat")
    private List<NFBRX9OutputVODetailsVO> details;

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

	public List<NFBRX9OutputVODetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NFBRX9OutputVODetailsVO> details) {
		this.details = details;
	}

}