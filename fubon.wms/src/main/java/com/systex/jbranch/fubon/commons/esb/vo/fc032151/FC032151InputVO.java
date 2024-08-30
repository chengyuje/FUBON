package com.systex.jbranch.fubon.commons.esb.vo.fc032151;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/09/09.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032151InputVO {
	@XmlElement
	private String FUNC;	
	@XmlElement
	private String CUST_NO;	//客戶ID
	
	
	public String getFUNC() {
		return FUNC;
	}
	public String getCUST_NO() {
		return CUST_NO;
	}
	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}
	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}



}