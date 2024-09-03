package com.systex.jbranch.fubon.commons.esb.vo.nfbrn6;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN6InputVO {
	@XmlElement
	private String CONFIRM;		//交易項目
	@XmlElement
	private String APPLY_SEQ;	//議價編號
	@XmlElement
	private String TRADE_DATE;	//優惠日
	@XmlElement
	private String BRANCH_NBR;	//交易分行
	@XmlElement
	private String PROD_ID;		//基金代碼
	@XmlElement
	private String CUST_ID;		//身分證號
	@XmlElement
	private BigDecimal AMT;			//申購金額
	@XmlElement
	private String CURRENCY;	//申購幣別
	@XmlElement
	private BigDecimal FEE_RATE;	//手續費率
	@XmlElement
	private String AUTH_EMP_ID;	//鍵機/覆核人員
	@XmlElement
	private String AUTH_DATE;		//鍵機/覆核日期
	@XmlElement
	private String AUTH_TIME;		//鍵機/覆核時間
	@XmlElement
	private String REMARKS;		//備註
	@XmlElement
	private String DYNAMIC_YN;		//Y:動態鎖利	空白:一般基金單次議價
	
	
	public String getCONFIRM() {
		return CONFIRM;
	}
	public void setCONFIRM(String cONFIRM) {
		CONFIRM = cONFIRM;
	}
	public String getAPPLY_SEQ() {
		return APPLY_SEQ;
	}
	public void setAPPLY_SEQ(String aPPLY_SEQ) {
		APPLY_SEQ = aPPLY_SEQ;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	public String getPROD_ID() {
		return PROD_ID;
	}
	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getCURRENCY() {
		return CURRENCY;
	}
	public void setCURRENCY(String cURRENCY) {
		CURRENCY = cURRENCY;
	}
	public String getAUTH_EMP_ID() {
		return AUTH_EMP_ID;
	}
	public void setAUTH_EMP_ID(String aUTH_EMP_ID) {
		AUTH_EMP_ID = aUTH_EMP_ID;
	}
	public String getREMARKS() {
		return REMARKS;
	}
	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}
	public BigDecimal getAMT() {
		return AMT;
	}
	public BigDecimal getFEE_RATE() {
		return FEE_RATE;
	}
	public void setAMT(BigDecimal aMT) {
		AMT = aMT;
	}
	public void setFEE_RATE(BigDecimal fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}
	public String getTRADE_DATE() {
		return TRADE_DATE;
	}
	public String getAUTH_DATE() {
		return AUTH_DATE;
	}
	public String getAUTH_TIME() {
		return AUTH_TIME;
	}
	public void setTRADE_DATE(String tRADE_DATE) {
		TRADE_DATE = tRADE_DATE;
	}
	public void setAUTH_DATE(String aUTH_DATE) {
		AUTH_DATE = aUTH_DATE;
	}
	public void setAUTH_TIME(String aUTH_TIME) {
		AUTH_TIME = aUTH_TIME;
	}
	public String getDYNAMIC_YN() {
		return DYNAMIC_YN;
	}
	public void setDYNAMIC_YN(String dYNAMIC_YN) {
		DYNAMIC_YN = dYNAMIC_YN;
	}
	
}
