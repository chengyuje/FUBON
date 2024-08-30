package com.systex.jbranch.fubon.commons.esb.vo.nfbrx7;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRX7InputVO {
	
	@XmlElement
	private String ERR_COD;

	@XmlElement
	private String ERR_TXT;

	@XmlElement
	private String CUST_ID;

	@XmlElement
	private String BRANCH_NBR;

	@XmlElement
	private String TRUST_CURR_TYPE;

	@XmlElement
	private String TRADE_SUB_TYPE;

	@XmlElement
	private String TRADE_DATE;

	@XmlElement
	private String EFF_DATE;

	@XmlElement
	private String PROD_ID;

	@XmlElement
	private String PURCHASE_AMT_L;

	@XmlElement
	private String PURCHASE_AMT_M;

	@XmlElement
	private String PURCHASE_AMT_H;

	@XmlElement
	private String AUTO_CX;

	public String getERR_COD() {
		return ERR_COD;
	}

	public void setERR_COD(String eRR_COD) {
		ERR_COD = eRR_COD;
	}

	public String getERR_TXT() {
		return ERR_TXT;
	}

	public void setERR_TXT(String eRR_TXT) {
		ERR_TXT = eRR_TXT;
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

	public String getTRUST_CURR_TYPE() {
		return TRUST_CURR_TYPE;
	}

	public void setTRUST_CURR_TYPE(String tRUST_CURR_TYPE) {
		TRUST_CURR_TYPE = tRUST_CURR_TYPE;
	}

	public String getTRADE_SUB_TYPE() {
		return TRADE_SUB_TYPE;
	}

	public void setTRADE_SUB_TYPE(String tRADE_SUB_TYPE) {
		TRADE_SUB_TYPE = tRADE_SUB_TYPE;
	}

	public String getTRADE_DATE() {
		return TRADE_DATE;
	}

	public void setTRADE_DATE(String tRADE_DATE) {
		TRADE_DATE = tRADE_DATE;
	}

	public String getEFF_DATE() {
		return EFF_DATE;
	}

	public void setEFF_DATE(String eFF_DATE) {
		EFF_DATE = eFF_DATE;
	}

	public String getPROD_ID() {
		return PROD_ID;
	}

	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}

	public String getPURCHASE_AMT_L() {
		return PURCHASE_AMT_L;
	}

	public void setPURCHASE_AMT_L(String pURCHASE_AMT_L) {
		PURCHASE_AMT_L = pURCHASE_AMT_L;
	}

	public String getPURCHASE_AMT_M() {
		return PURCHASE_AMT_M;
	}

	public void setPURCHASE_AMT_M(String pURCHASE_AMT_M) {
		PURCHASE_AMT_M = pURCHASE_AMT_M;
	}

	public String getPURCHASE_AMT_H() {
		return PURCHASE_AMT_H;
	}

	public void setPURCHASE_AMT_H(String pURCHASE_AMT_H) {
		PURCHASE_AMT_H = pURCHASE_AMT_H;
	}

	public String getAUTO_CX() {
		return AUTO_CX;
	}

	public void setAUTO_CX(String aUTO_CX) {
		AUTO_CX = aUTO_CX;
	}

}