package com.systex.jbranch.fubon.commons.esb.vo.cew012r;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* Created by Stella on 2017/01/17.
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType

public class CEW012ROutputDetailsVO {
	@XmlElement
	private String CUS_ID;                  //歸戶ID
	@XmlElement
	private String REF_NO;                  //REF.NO
	@XmlElement
	private String SYS_NO;                  //SYS.NO
	@XmlElement
	private String CRD_NAME;                //申請卡種
	@XmlElement
	private String CRD_TYPE;                //申請卡別
	@XmlElement
	private String APPLY_CODE;              //申請階段代碼
	@XmlElement
	private String APPLY_MSG;               //申請進度訊息
	@XmlElement
	private String RES_CODE;                //回應訊息代碼
	@XmlElement
	private String RES_MSG;                 //回應訊息
	@XmlElement
	private String FURTHER_DOC;             //補件訊息
	
	
	public String getCUS_ID() {
		return CUS_ID;
	}
	public void setCUS_ID(String cUS_ID) {
		CUS_ID = cUS_ID;
	}
	public String getREF_NO() {
		return REF_NO;
	}
	public void setREF_NO(String rEF_NO) {
		REF_NO = rEF_NO;
	}
	public String getSYS_NO() {
		return SYS_NO;
	}
	public void setSYS_NO(String sYS_NO) {
		SYS_NO = sYS_NO;
	}
	public String getCRD_NAME() {
		return CRD_NAME;
	}
	public void setCRD(String cRD_NAME) {
		CRD_NAME = cRD_NAME;
	}
	public String getCRD_TYPE() {
		return CRD_TYPE;
	}
	public void setCRD_TYPE(String cRD_TYPE) {
		CRD_TYPE = cRD_TYPE;
	}
	public String getAPPLY_CODE() {
		return APPLY_CODE;
	}
	public void setAPPLY_CODE(String aPPLY_CODE) {
		APPLY_CODE = aPPLY_CODE;
	}
	public String getAPPLY_MSG() {
		return APPLY_MSG;
	}
	public void setAPPLY_MSG(String aPPLY_MSG) {
		APPLY_MSG = aPPLY_MSG;
	}
	public String getRES_CODE() {
		return RES_CODE;
	}
	public void setRES_CODE(String rES_CODE) {
		RES_CODE = rES_CODE;
	}
	public String getRES_MSG() {
		return RES_MSG;
	}
	public void setRES_MSG(String rES_MSG) {
		RES_MSG = rES_MSG;
	}
	public String getFURTHER_DOC() {
		return FURTHER_DOC;
	}
	public void setFURTHER_DOC(String fURTHER_DOC) {
		FURTHER_DOC = fURTHER_DOC;
	}
	
	
	
}
