package com.systex.jbranch.fubon.commons.esb.vo.eb032282;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created on 2019/08/08.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB032282InputVO {
	@XmlElement
	private String FUNC;	//功能
	@XmlElement
	private String CUST_NO;	//客戶ID
	@XmlElement
	private String BUS_TYP;	
	
	public String getFUNC() {
		return FUNC;
	}
	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}
	public String getCUST_NO() {
		return CUST_NO;
	}
	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}
	public String getBUS_TYP() {
		return BUS_TYP;
	}
	public void setBUS_TYP(String bUS_TYP) {
		BUS_TYP = bUS_TYP;
	}
	
}