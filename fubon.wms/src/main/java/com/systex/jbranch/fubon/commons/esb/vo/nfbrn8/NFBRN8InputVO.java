package com.systex.jbranch.fubon.commons.esb.vo.nfbrn8;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN8InputVO {
	@XmlElement
	private String CONFIRM;		//交易項目
	@XmlElement
	private String CUST_ID;		//客戶ID 
	@XmlElement
	private String BRG_BEGIN_DATE;//優惠起日
	@XmlElement
	private String BRG_END_DATE;	//優惠迄日
	@XmlElement
	private String AUTH_EMP_ID;	//鍵機/覆核人員
	@XmlElement
	private String AUTH_DATE;		//鍵機/覆核日期
	@XmlElement
	private String AUTH_TIME;		//鍵機/覆核時間
	@XmlElement
	private BigDecimal DMT_STOCK;	//國內股票折數
	@XmlElement
	private BigDecimal FRN_STOCK;	//國外股票折數
	@XmlElement
	private BigDecimal DMT_BOND;	//國內債券折數
	@XmlElement
	private BigDecimal FRN_BOND;	//國外債券折數
	@XmlElement
	private BigDecimal DMT_BALANCED;//國內平衡折數
	@XmlElement
	private BigDecimal FRN_BALANCED;//國外平衡折數
	@XmlElement
	private String BRG_REASON;	//備註
	
	
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

	public String getAUTH_EMP_ID() {
		return AUTH_EMP_ID;
	}
	public void setAUTH_EMP_ID(String aUTH_EMP_ID) {
		AUTH_EMP_ID = aUTH_EMP_ID;
	}


	public String getBRG_BEGIN_DATE() {
		return BRG_BEGIN_DATE;
	}
	public String getBRG_END_DATE() {
		return BRG_END_DATE;
	}
	public String getAUTH_DATE() {
		return AUTH_DATE;
	}
	public String getAUTH_TIME() {
		return AUTH_TIME;
	}
	public void setBRG_BEGIN_DATE(String bRG_BEGIN_DATE) {
		BRG_BEGIN_DATE = bRG_BEGIN_DATE;
	}
	public void setBRG_END_DATE(String bRG_END_DATE) {
		BRG_END_DATE = bRG_END_DATE;
	}
	public void setAUTH_DATE(String aUTH_DATE) {
		AUTH_DATE = aUTH_DATE;
	}
	public void setAUTH_TIME(String aUTH_TIME) {
		AUTH_TIME = aUTH_TIME;
	}
	public BigDecimal getDMT_STOCK() {
		return DMT_STOCK;
	}
	public BigDecimal getFRN_STOCK() {
		return FRN_STOCK;
	}
	public BigDecimal getDMT_BOND() {
		return DMT_BOND;
	}
	public BigDecimal getFRN_BOND() {
		return FRN_BOND;
	}
	public BigDecimal getDMT_BALANCED() {
		return DMT_BALANCED;
	}
	public BigDecimal getFRN_BALANCED() {
		return FRN_BALANCED;
	}
	public void setDMT_STOCK(BigDecimal dMT_STOCK) {
		DMT_STOCK = dMT_STOCK;
	}
	public void setFRN_STOCK(BigDecimal fRN_STOCK) {
		FRN_STOCK = fRN_STOCK;
	}
	public void setDMT_BOND(BigDecimal dMT_BOND) {
		DMT_BOND = dMT_BOND;
	}
	public void setFRN_BOND(BigDecimal fRN_BOND) {
		FRN_BOND = fRN_BOND;
	}
	public void setDMT_BALANCED(BigDecimal dMT_BALANCED) {
		DMT_BALANCED = dMT_BALANCED;
	}
	public void setFRN_BALANCED(BigDecimal fRN_BALANCED) {
		FRN_BALANCED = fRN_BALANCED;
	}
	public String getBRG_REASON() {
		return BRG_REASON;
	}
	public void setBRG_REASON(String bRG_REASON) {
		BRG_REASON = bRG_REASON;
	}

	
	

	
	
	
	
}
