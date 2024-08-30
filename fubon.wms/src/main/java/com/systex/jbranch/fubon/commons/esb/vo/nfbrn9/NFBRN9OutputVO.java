package com.systex.jbranch.fubon.commons.esb.vo.nfbrn9;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN9OutputVO {
	@XmlElement
	private String ErrorCode;
    @XmlElement
	private String ErrorMsg;
    @XmlElement(name="TxRepeat")
    private List<NFBRN9OutputVODetailsVO> details;
    
    
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
	public List<NFBRN9OutputVODetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<NFBRN9OutputVODetailsVO> details) {
		this.details = details;
	}
}
