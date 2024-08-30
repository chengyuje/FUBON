package com.systex.jbranch.fubon.commons.esb.vo.nrbrvc3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NRBRVC3OutputDetailsVO {
	
	@XmlElement
	private String AUTH_EMP_ID;
	
	@XmlElement
	private String AUTH_DATE;
	
	@XmlElement
	private String AUTH_TIME;
	
	@XmlElement
	private String BRG_BEGIN_DATE;
	
	@XmlElement
	private String BRG_END_DATE;
	
	@XmlElement
	private String BRG_REASON;
	
	@XmlElement
	private String BUY_HK_MRK;
	
	@XmlElement
	private String BUY_US_MRK;
	
	@XmlElement
	private String SELL_HK_MRK;
	
	@XmlElement
	private String SELL_US_MRK;
	
	@XmlElement
	private String APPLY_DATE;
	
	@XmlElement
	private String APPLY_TIME;
	
	/* 2017.11.22 add by Carley */
	@XmlElement
	private String END_DATE;	//終止日期

	/* 2018.10.15 add by Mimi */	
	@XmlElement
	private String BUY_UK_MRK;  	
	
	@XmlElement
	private String SELL_UK_MRK;  	
	
	@XmlElement
	private String BUY_JP_MRK;

	@XmlElement
	private String SELL_JP_MRK;  
	
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

	public String getBRG_REASON() {
		return BRG_REASON;
	}

	public void setBRG_REASON(String bRG_REASON) {
		BRG_REASON = bRG_REASON;
	}

	public String getBUY_HK_MRK() {
		return BUY_HK_MRK;
	}

	public void setBUY_HK_MRK(String bUY_HK_MRK) {
		BUY_HK_MRK = bUY_HK_MRK;
	}

	public String getBUY_US_MRK() {
		return BUY_US_MRK;
	}

	public void setBUY_US_MRK(String bUY_US_MRK) {
		BUY_US_MRK = bUY_US_MRK;
	}
	
	public String getBUY_UK_MRK() {
		return BUY_UK_MRK;
	}

	public void setBUY_UK_MRK(String bUY_UK_MRK) {
		BUY_US_MRK = bUY_UK_MRK;
	}	

	public String getSELL_HK_MRK() {
		return SELL_HK_MRK;
	}

	public void setSELL_HK_MRK(String sELL_HK_MRK) {
		SELL_HK_MRK = sELL_HK_MRK;
	}

	public String getSELL_US_MRK() {
		return SELL_US_MRK;
	}

	public void setSELL_US_MRK(String sELL_US_MRK) {
		SELL_US_MRK = sELL_US_MRK;
	}
	
	public String getSELL_UK_MRK() {
		return SELL_UK_MRK;
	}

	public void setSELL_UK_MRK(String sELL_UK_MRK) {
		SELL_UK_MRK = sELL_UK_MRK;
	}	

	public String getBUY_JP_MRK() {
		return BUY_JP_MRK;
	}

	public void setBUY_JP_MRK(String bUY_JP_MRK) {
		BUY_JP_MRK = bUY_JP_MRK;
	}

	public String getSELL_JP_MRK() {
		return SELL_JP_MRK;
	}

	public void setSELL_JP_MRK(String sELL_JP_MRK) {
		SELL_JP_MRK = sELL_JP_MRK;
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

	public String getEND_DATE() {
		return END_DATE;
	}

	public void setEND_DATE(String eND_DATE) {
		END_DATE = eND_DATE;
	}

}