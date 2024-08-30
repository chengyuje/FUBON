package com.systex.jbranch.fubon.commons.esb.vo.nfee002;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/9.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NFEE002OutputVODetailsVO {
 
    @XmlElement
    private String APPLY_DATE;		//申請日期
    @XmlElement
    private String BRG_BEGIN_DATE;	//優惠期間(起)
    @XmlElement
    private String BRG_END_DATE;	//優惠期間(迄)
    @XmlElement
    private String DMT_STOCK;		//國內股票型
    @XmlElement
    private String DMT_BOND;		//國內債券型
    @XmlElement
    private String DMT_BALANCED;	//國內平衡型
    @XmlElement
    private String FRN_STOCK;		//國外股票型
    @XmlElement
    private String FRN_BOND;		//國外債券型
    @XmlElement
    private String FRN_BALANCED;	//國外平衡型
    @XmlElement
    private String BRG_REASON;		//分行備註
    @XmlElement
    private String AUTH_DATE;		//覆核日期
    @XmlElement
    private String TERMINATE_DATE;	//終止日期
	public String getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public void setAPPLY_DATE(String aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
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
	public String getDMT_STOCK() {
		return DMT_STOCK;
	}
	public void setDMT_STOCK(String dMT_STOCK) {
		DMT_STOCK = dMT_STOCK;
	}
	public String getDMT_BOND() {
		return DMT_BOND;
	}
	public void setDMT_BOND(String dMT_BOND) {
		DMT_BOND = dMT_BOND;
	}
	public String getDMT_BALANCED() {
		return DMT_BALANCED;
	}
	public void setDMT_BALANCED(String dMT_BALANCED) {
		DMT_BALANCED = dMT_BALANCED;
	}
	public String getFRN_STOCK() {
		return FRN_STOCK;
	}
	public void setFRN_STOCK(String fRN_STOCK) {
		FRN_STOCK = fRN_STOCK;
	}
	public String getFRN_BOND() {
		return FRN_BOND;
	}
	public void setFRN_BOND(String fRN_BOND) {
		FRN_BOND = fRN_BOND;
	}
	public String getFRN_BALANCED() {
		return FRN_BALANCED;
	}
	public void setFRN_BALANCED(String fRN_BALANCED) {
		FRN_BALANCED = fRN_BALANCED;
	}
	public String getBRG_REASON() {
		return BRG_REASON;
	}
	public void setBRG_REASON(String bRG_REASON) {
		BRG_REASON = bRG_REASON;
	}
	public String getAUTH_DATE() {
		return AUTH_DATE;
	}
	public void setAUTH_DATE(String aUTH_DATE) {
		AUTH_DATE = aUTH_DATE;
	}
	public String getTERMINATE_DATE() {
		return TERMINATE_DATE;
	}
	public void setTERMINATE_DATE(String tERMINATE_DATE) {
		TERMINATE_DATE = tERMINATE_DATE;
	}
	
}
