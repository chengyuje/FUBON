package com.systex.jbranch.fubon.commons.esb.vo.fp032671;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class FP032671OutputDetailsVO {
    
	@XmlElement
	private String ACNO_CATG_1;
	@XmlElement
	private String ACNO_1;
	@XmlElement
	private String OPN_DATE_1;
	@XmlElement
	private String CUST_ACT_NO_1;
	@XmlElement
	private String ORG_DESC_1;
	@XmlElement
	private String CUST_NAME_1;
	@XmlElement
	private String OVD_DATE_1;
	@XmlElement
	private String TYPE; //判斷活定存 S活存 T定存
	@XmlElement
	private String CURR; //幣別 判斷母子帳號 (母帳號為XXX)
	@XmlElement
	private String BRA; //分行
	@XmlElement
	private String WA_X_ATYPE; //產品大類
	@XmlElement
	private String WA_X_ICAT; //產品子類
	
	
	
	
	public String getBRA() {
		return BRA;
	}
	public void setBRA(String bRA) {
		BRA = bRA;
	}
	public String getCURR() {
		return CURR;
	}
	public void setCURR(String cURR) {
		CURR = cURR;
	}
	public String getACNO_CATG_1() {
		return ACNO_CATG_1;
	}
	public void setACNO_CATG_1(String aCNO_CATG_1) {
		ACNO_CATG_1 = aCNO_CATG_1;
	}
	public String getACNO_1() {
		return ACNO_1;
	}
	public void setACNO_1(String aCNO_1) {
		ACNO_1 = aCNO_1;
	}
	public String getOPN_DATE_1() {
		return OPN_DATE_1;
	}
	public void setOPN_DATE_1(String oPN_DATE_1) {
		OPN_DATE_1 = oPN_DATE_1;
	}
	public String getCUST_ACT_NO_1() {
		return CUST_ACT_NO_1;
	}
	public void setCUST_ACT_NO_1(String cUST_ACT_NO_1) {
		CUST_ACT_NO_1 = cUST_ACT_NO_1;
	}
	public String getORG_DESC_1() {
		return ORG_DESC_1;
	}
	public void setORG_DESC_1(String oRG_DESC_1) {
		ORG_DESC_1 = oRG_DESC_1;
	}
	public String getCUST_NAME_1() {
		return CUST_NAME_1;
	}
	public void setCUST_NAME_1(String cUST_NAME_1) {
		CUST_NAME_1 = cUST_NAME_1;
	}
	public String getOVD_DATE_1() {
		return OVD_DATE_1;
	}
	public void setOVD_DATE_1(String oVD_DATE_1) {
		OVD_DATE_1 = oVD_DATE_1;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getWA_X_ATYPE() {
		return WA_X_ATYPE;
	}
	public void setWA_X_ATYPE(String wA_X_ATYPE) {
		WA_X_ATYPE = wA_X_ATYPE;
	}
	public String getWA_X_ICAT() {
		return WA_X_ICAT;
	}
	public void setWA_X_ICAT(String wA_X_ICAT) {
		WA_X_ICAT = wA_X_ICAT;
	}
	
	
	
}
