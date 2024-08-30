package com.systex.jbranch.fubon.commons.esb.vo.nrbrvc1;

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
public class NRBRVC1InputVO {
	
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
	private String BRG_BEGIN_DATE;
	
	@XmlElement
	private String BRG_END_DATE;
	
	@XmlElement
	private String BRG_REASON;
	
	@XmlElement
	private BigDecimal BUY_HK_MRK;
	
	@XmlElement
	private BigDecimal BUY_US_MRK; 	
	
	@XmlElement
	private BigDecimal SELL_HK_MRK;
	
	@XmlElement
	private BigDecimal SELL_US_MRK;
	
	@XmlElement
	private String APPLY_DATE;
	
	@XmlElement
	private String APPLY_TIME;
	
	@XmlElement
	private String AUTH_TIME;
	
	/* 2018.10.15 add by Mimi */	
	@XmlElement
	private BigDecimal BUY_UK_MRK;

	@XmlElement
	private BigDecimal SELL_UK_MRK;  

	@XmlElement
	private BigDecimal BUY_JP_MRK;

	@XmlElement
	private BigDecimal SELL_JP_MRK;  
	
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

	public String getBRG_REASON() {
		return BRG_REASON;
	}

	public void setBRG_REASON(String bRG_REASON) {
		BRG_REASON = bRG_REASON;
	}

	public BigDecimal getBUY_HK_MRK() {
		return BUY_HK_MRK;
	}

	public void setBUY_HK_MRK(BigDecimal bUY_HK_MRK) {
		BUY_HK_MRK = bUY_HK_MRK;
	}

	public BigDecimal getBUY_US_MRK() {
		return BUY_US_MRK;
	}

	public void setBUY_US_MRK(BigDecimal bUY_US_MRK) {
		BUY_US_MRK = bUY_US_MRK;
	}
	
	public BigDecimal getBUY_UK_MRK() {
		return BUY_UK_MRK;
	}

	public void setBUY_UK_MRK(BigDecimal bUY_UK_MRK) {
		BUY_UK_MRK = bUY_UK_MRK;
	}
	
	public BigDecimal getSELL_HK_MRK() {
		return SELL_HK_MRK;
	}

	public void setSELL_HK_MRK(BigDecimal sELL_HK_MRK) {
		SELL_HK_MRK = sELL_HK_MRK;
	}

	public BigDecimal getSELL_US_MRK() {
		return SELL_US_MRK;
	}

	public void setSELL_US_MRK(BigDecimal sELL_US_MRK) {
		SELL_US_MRK = sELL_US_MRK;
	}

	public BigDecimal getSELL_UK_MRK() {
		return SELL_UK_MRK;
	}

	public void setSELL_UK_MRK(BigDecimal sELL_UK_MRK) {
		SELL_UK_MRK = sELL_UK_MRK;
	}	
	
	public BigDecimal getBUY_JP_MRK() {
		return BUY_JP_MRK;
	}

	public void setBUY_JP_MRK(BigDecimal bUY_JP_MRK) {
		BUY_JP_MRK = bUY_JP_MRK;
	}

	public BigDecimal getSELL_JP_MRK() {
		return SELL_JP_MRK;
	}

	public void setSELL_JP_MRK(BigDecimal sELL_JP_MRK) {
		SELL_JP_MRK = sELL_JP_MRK;
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

	public String getAUTH_DATE() {
		return AUTH_DATE;
	}

	public void setAUTH_DATE(String aUTH_DATE) {
		AUTH_DATE = aUTH_DATE;
	}

	public String getBRG_BEGIN_DATE() {
		return BRG_BEGIN_DATE;
	}

	public void setBRG_BEGIN_DATE(String bRG_BEGIN_DATE) {
		BRG_BEGIN_DATE = bRG_BEGIN_DATE;
	}

	public String getBRG_END_DATE() {
		return BRG_END_DATE;
	}

	public void setBRG_END_DATE(String bRG_END_DATE) {
		BRG_END_DATE = bRG_END_DATE;
	}

	public String getAPPLY_DATE() {
		return APPLY_DATE;
	}

	public void setAPPLY_DATE(String aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}

}