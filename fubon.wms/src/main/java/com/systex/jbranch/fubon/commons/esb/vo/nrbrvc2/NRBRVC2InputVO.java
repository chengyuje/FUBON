package com.systex.jbranch.fubon.commons.esb.vo.nrbrvc2;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVC2InputVO {
	
	@XmlElement
	private String APPLY_SEQ;
	
	@XmlElement
	private String EMP_ID;
	
	@XmlElement
	private String CONFIRM;
	
	@XmlElement
	private String CUST_ID;
	
	@XmlElement
	private String BRANCH_NBR;
	
	@XmlElement
	private String AUTH_EMP_ID;
	
	@XmlElement
	private String AUTH_DATE;
	
	@XmlElement
	private String BUYSELL;
	
	@XmlElement
	private String PROD_ID;
	
	@XmlElement
	private String TRADE_DATE;
	
	@XmlElement
	private BigDecimal UNIT;
	
	@XmlElement
	private BigDecimal PRICE;
	
	@XmlElement
	private BigDecimal FEE_RATE;
	
	@XmlElement
	private BigDecimal FEE_DISCOUNT;
	
	@XmlElement
	private BigDecimal FEE;
	
	@XmlElement
	private String TRUST_CURR_TYPE;
	
	@XmlElement
	private String DISCOUNT_TYPE;
	
	@XmlElement
	private String APPLY_DATE;
	
	@XmlElement
	private String APPLY_TIME;
	
	@XmlElement
	private String AUTH_TIME;

	public String getAUTH_DATE() {
		return AUTH_DATE;
	}

	public void setAUTH_DATE(String aUTH_DATE) {
		AUTH_DATE = aUTH_DATE;
	}

	public String getTRADE_DATE() {
		return TRADE_DATE;
	}

	public void setTRADE_DATE(String tRADE_DATE) {
		TRADE_DATE = tRADE_DATE;
	}

	public String getAPPLY_DATE() {
		return APPLY_DATE;
	}

	public void setAPPLY_DATE(String aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}

	public String getAPPLY_SEQ() {
		return APPLY_SEQ;
	}

	public void setAPPLY_SEQ(String aPPLY_SEQ) {
		APPLY_SEQ = aPPLY_SEQ;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public String getCONFIRM() {
		return CONFIRM;
	}

	public void setCONFIRM(String cONFIRM) {
		CONFIRM = cONFIRM;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

	public String getAUTH_EMP_ID() {
		return AUTH_EMP_ID;
	}

	public void setAUTH_EMP_ID(String aUTH_EMP_ID) {
		AUTH_EMP_ID = aUTH_EMP_ID;
	}

	public String getBUYSELL() {
		return BUYSELL;
	}

	public void setBUYSELL(String bUYSELL) {
		BUYSELL = bUYSELL;
	}

	public String getPROD_ID() {
		return PROD_ID;
	}

	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}

	public BigDecimal getUNIT() {
		return UNIT;
	}

	public void setUNIT(BigDecimal uNIT) {
		UNIT = uNIT;
	}

	public BigDecimal getPRICE() {
		return PRICE;
	}

	public void setPRICE(BigDecimal pRICE) {
		PRICE = pRICE;
	}

	public BigDecimal getFEE_RATE() {
		return FEE_RATE;
	}

	public void setFEE_RATE(BigDecimal fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}

	public BigDecimal getFEE_DISCOUNT() {
		return FEE_DISCOUNT;
	}

	public void setFEE_DISCOUNT(BigDecimal fEE_DISCOUNT) {
		FEE_DISCOUNT = fEE_DISCOUNT;
	}

	public BigDecimal getFEE() {
		return FEE;
	}

	public void setFEE(BigDecimal fEE) {
		FEE = fEE;
	}

	public String getTRUST_CURR_TYPE() {
		return TRUST_CURR_TYPE;
	}

	public void setTRUST_CURR_TYPE(String tRUST_CURR_TYPE) {
		TRUST_CURR_TYPE = tRUST_CURR_TYPE;
	}

	public String getDISCOUNT_TYPE() {
		return DISCOUNT_TYPE;
	}

	public void setDISCOUNT_TYPE(String dISCOUNT_TYPE) {
		DISCOUNT_TYPE = dISCOUNT_TYPE;
	}

	public String getAPPLY_TIME() {
		return APPLY_TIME;
	}

	public void setAPPLY_TIME(String aPPLY_TIME) {
		APPLY_TIME = aPPLY_TIME;
	}

	public String getAUTH_TIME() {
		return AUTH_TIME;
	}

	public void setAUTH_TIME(String aUTH_TIME) {
		AUTH_TIME = aUTH_TIME;
	}

	
}