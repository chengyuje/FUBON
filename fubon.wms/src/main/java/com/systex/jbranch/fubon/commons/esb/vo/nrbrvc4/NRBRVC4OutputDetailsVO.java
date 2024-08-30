package com.systex.jbranch.fubon.commons.esb.vo.nrbrvc4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NRBRVC4OutputDetailsVO {
	
	@XmlElement
	private String APPLY_SEQ;
	
	@XmlElement
	private String AUTH_EMP_ID;
	
	@XmlElement
	private String AUTH_DATE;
	
	@XmlElement
	private String AUTH_TIME;
	
	@XmlElement
	private String PROD_ID;
	
	@XmlElement
	private String UNIT;
	
	@XmlElement
	private String PRICE;
	
	@XmlElement
	private String FEE_RATE;
	
	@XmlElement
	private String FEE_DISCOUNT;
	
	@XmlElement
	private String FEE;
	
	@XmlElement
	private String TRUST_CURR_TYPE;
	
	@XmlElement
	private String SEQ_USECODE;
	
	@XmlElement
	private String APPLY_DATE;
	
	@XmlElement
	private String APPLY_TIME;
	
	@XmlElement
	private String DISCOUNT_TYPE;

	public String getAPPLY_SEQ() {
		return APPLY_SEQ;
	}

	public void setAPPLY_SEQ(String aPPLY_SEQ) {
		APPLY_SEQ = aPPLY_SEQ;
	}

	public String getAUTH_EMP_ID() {
		return AUTH_EMP_ID;
	}

	public void setAUTH_EMP_ID(String aUTH_EMP_ID) {
		AUTH_EMP_ID = aUTH_EMP_ID;
	}

	public String getAUTH_DATE() {
		return AUTH_DATE;
	}

	public void setAUTH_DATE(String aUTH_DATE) {
		AUTH_DATE = aUTH_DATE;
	}

	public String getAUTH_TIME() {
		return AUTH_TIME;
	}

	public void setAUTH_TIME(String aUTH_TIME) {
		AUTH_TIME = aUTH_TIME;
	}

	public String getPROD_ID() {
		return PROD_ID;
	}

	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}

	public String getUNIT() {
		return UNIT;
	}

	public void setUNIT(String uNIT) {
		UNIT = uNIT;
	}

	public String getPRICE() {
		return PRICE;
	}

	public void setPRICE(String pRICE) {
		PRICE = pRICE;
	}

	public String getFEE_RATE() {
		return FEE_RATE;
	}

	public void setFEE_RATE(String fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}

	public String getFEE_DISCOUNT() {
		return FEE_DISCOUNT;
	}

	public void setFEE_DISCOUNT(String fEE_DISCOUNT) {
		FEE_DISCOUNT = fEE_DISCOUNT;
	}

	public String getFEE() {
		return FEE;
	}

	public void setFEE(String fEE) {
		FEE = fEE;
	}

	public String getTRUST_CURR_TYPE() {
		return TRUST_CURR_TYPE;
	}

	public void setTRUST_CURR_TYPE(String tRUST_CURR_TYPE) {
		TRUST_CURR_TYPE = tRUST_CURR_TYPE;
	}

	public String getSEQ_USECODE() {
		return SEQ_USECODE;
	}

	public void setSEQ_USECODE(String sEQ_USECODE) {
		SEQ_USECODE = sEQ_USECODE;
	}

	public String getAPPLY_DATE() {
		return APPLY_DATE;
	}

	public void setAPPLY_DATE(String aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}

	public String getAPPLY_TIME() {
		return APPLY_TIME;
	}

	public void setAPPLY_TIME(String aPPLY_TIME) {
		APPLY_TIME = aPPLY_TIME;
	}

	public String getDISCOUNT_TYPE() {
		return DISCOUNT_TYPE;
	}

	public void setDISCOUNT_TYPE(String dISCOUNT_TYPE) {
		DISCOUNT_TYPE = dISCOUNT_TYPE;
	}

}