package com.systex.jbranch.fubon.commons.esb.vo.ccm002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2017/01/04.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CCM002InputVO {
	@XmlElement
	private String FUNCTION;	//功能
	@XmlElement
	private String RANGE;	//範圍
	@XmlElement
	private String CUSID;	//身分證字號
	
	public String getFUNCTION() {
		return FUNCTION;
	}
	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}
	public String getRANGE() {
		return RANGE;
	}
	public void setRANGE(String rANGE) {
		RANGE = rANGE;
	}
	public String getCUSID() {
		return CUSID;
	}
	public void setCUSID(String cUSID) {
		CUSID = cUSID;
	}
	
}