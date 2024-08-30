package com.systex.jbranch.fubon.commons.esb.vo.sdprc09a;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by JackyWu on 2016/12/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDPRC09AOutputDetailVO {
	@XmlElement
    private String BUSINESS_CD;   //業務別編號
	@XmlElement
    private String CURRENCY;      //幣別
	@XmlElement
    private String BRH;           //分行代碼
	@XmlElement
    private String IVAMT2;   //庫存餘額
	public String getBUSINESS_CD() {
		return BUSINESS_CD;
	}
	public void setBUSINESS_CD(String bUSINESS_CD) {
		BUSINESS_CD = bUSINESS_CD;
	}
	public String getCURRENCY() {
		return CURRENCY;
	}
	public void setCURRENCY(String cURRENCY) {
		CURRENCY = cURRENCY;
	}
	public String getBRH() {
		return BRH;
	}
	public void setBRH(String bRH) {
		BRH = bRH;
	}
	public String getIVAMT2() {
		return IVAMT2;
	}
	public void setIVAMT2(String iVAMT2) {
		IVAMT2 = iVAMT2;
	}
	
	
}
