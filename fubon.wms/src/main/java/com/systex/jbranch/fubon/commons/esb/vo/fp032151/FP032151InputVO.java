package com.systex.jbranch.fubon.commons.esb.vo.fp032151;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SamTu on 2018/02/26.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FP032151InputVO {
	@XmlElement
	private String FUNC;	
	@XmlElement
	private String CUST_NO;	//客戶ID
	@XmlElement
	private String IDTYPE;
	
	
	
	
	public String getIDTYPE() {
		return IDTYPE;
	}
	public void setIDTYPE(String iDTYPE) {
		IDTYPE = iDTYPE;
	}
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