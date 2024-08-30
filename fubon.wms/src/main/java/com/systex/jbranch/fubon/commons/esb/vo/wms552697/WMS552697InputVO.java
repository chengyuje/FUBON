package com.systex.jbranch.fubon.commons.esb.vo.wms552697;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SamTu on 2018/03/19.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType

public class WMS552697InputVO {
	
	@XmlElement
	private String CUSID;       //身分證字號

	@XmlElement
	private String FUNCTION;    //功能

	public String getCUSID() {
		return CUSID;
	}

	public void setCUSID(String cUSID) {
		CUSID = cUSID;
	}

	public String getFUNCTION() {
		return FUNCTION;
	}

	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}
	
	
	
}
