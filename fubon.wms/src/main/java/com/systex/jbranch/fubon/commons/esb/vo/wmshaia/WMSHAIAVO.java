package com.systex.jbranch.fubon.commons.esb.vo.wmshaia;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created 2023/11/07
 * 高資產客戶投組適配承作，取得風險檢核資訊
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class WMSHAIAVO {
	@XmlElement
	private String CUST_ID;
	@XmlElement
	private String CUST_KYC;
	@XmlElement
	private String SP_YN;
	@XmlElement
	private String PROD_RISK;
	@XmlElement
	private String AMT_BUY_1;
	@XmlElement
	private String AMT_SELL_1;
	@XmlElement
	private String AMT_BUY_2;
	@XmlElement
	private String AMT_SELL_2;
	@XmlElement
	private String AMT_BUY_3;
	@XmlElement
	private String AMT_SELL_3;
	@XmlElement
	private String AMT_BUY_4;
	@XmlElement
	private String AMT_SELL_4;
	@XmlElement
	private String SEQ;
	@XmlElement
	private String DENO_AMT;
	@XmlElement
	private String BASE_RISK_1;
	@XmlElement
	private String AMT_1;
	@XmlElement
	private String BASE_RISK_2;
	@XmlElement
	private String AMT_2;
	@XmlElement
	private String AMT_LEFT_2;
	@XmlElement
	private String BASE_RISK_3;
	@XmlElement
	private String AMT_3;
	@XmlElement
	private String AMT_LEFT_3;
	@XmlElement
	private String BASE_RISK_4;
	@XmlElement
	private String AMT_4;
	@XmlElement
	private String AMT_LEFT_4;
	@XmlElement
	private String RISK_1;
	@XmlElement
	private String RISK_2;
	@XmlElement
	private String RISK_3;
	@XmlElement
	private String RISK_SUM;
	@XmlElement
	private String VALIDATE_YN;
	@XmlElement
	private String EMSGID; //錯誤代碼
	@XmlElement
	private String EMSGTXT; //錯誤訊息
	
	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getCUST_KYC() {
		return CUST_KYC;
	}

	public void setCUST_KYC(String cUST_KYC) {
		CUST_KYC = cUST_KYC;
	}

	public String getSP_YN() {
		return SP_YN;
	}

	public void setSP_YN(String sP_YN) {
		SP_YN = sP_YN;
	}

	public String getPROD_RISK() {
		return PROD_RISK;
	}

	public void setPROD_RISK(String pROD_RISK) {
		PROD_RISK = pROD_RISK;
	}

	public String getAMT_BUY_1() {
		return AMT_BUY_1;
	}

	public void setAMT_BUY_1(String aMT_BUY_1) {
		AMT_BUY_1 = aMT_BUY_1;
	}

	public String getAMT_SELL_1() {
		return AMT_SELL_1;
	}

	public void setAMT_SELL_1(String aMT_SELL_1) {
		AMT_SELL_1 = aMT_SELL_1;
	}

	public String getAMT_BUY_2() {
		return AMT_BUY_2;
	}

	public void setAMT_BUY_2(String aMT_BUY_2) {
		AMT_BUY_2 = aMT_BUY_2;
	}

	public String getAMT_SELL_2() {
		return AMT_SELL_2;
	}

	public void setAMT_SELL_2(String aMT_SELL_2) {
		AMT_SELL_2 = aMT_SELL_2;
	}

	public String getAMT_BUY_3() {
		return AMT_BUY_3;
	}

	public void setAMT_BUY_3(String aMT_BUY_3) {
		AMT_BUY_3 = aMT_BUY_3;
	}

	public String getAMT_SELL_3() {
		return AMT_SELL_3;
	}

	public void setAMT_SELL_3(String aMT_SELL_3) {
		AMT_SELL_3 = aMT_SELL_3;
	}

	public String getAMT_BUY_4() {
		return AMT_BUY_4;
	}

	public void setAMT_BUY_4(String aMT_BUY_4) {
		AMT_BUY_4 = aMT_BUY_4;
	}

	public String getAMT_SELL_4() {
		return AMT_SELL_4;
	}

	public void setAMT_SELL_4(String aMT_SELL_4) {
		AMT_SELL_4 = aMT_SELL_4;
	}

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}

	public String getDENO_AMT() {
		return DENO_AMT;
	}

	public void setDENO_AMT(String dENO_AMT) {
		DENO_AMT = dENO_AMT;
	}

	public String getBASE_RISK_1() {
		return BASE_RISK_1;
	}

	public void setBASE_RISK_1(String bASE_RISK_1) {
		BASE_RISK_1 = bASE_RISK_1;
	}

	public String getAMT_1() {
		return AMT_1;
	}

	public void setAMT_1(String aMT_1) {
		AMT_1 = aMT_1;
	}

	public String getBASE_RISK_2() {
		return BASE_RISK_2;
	}

	public void setBASE_RISK_2(String bASE_RISK_2) {
		BASE_RISK_2 = bASE_RISK_2;
	}

	public String getAMT_2() {
		return AMT_2;
	}

	public void setAMT_2(String aMT_2) {
		AMT_2 = aMT_2;
	}

	public String getAMT_LEFT_2() {
		return AMT_LEFT_2;
	}

	public void setAMT_LEFT_2(String aMT_LEFT_2) {
		AMT_LEFT_2 = aMT_LEFT_2;
	}

	public String getBASE_RISK_3() {
		return BASE_RISK_3;
	}

	public void setBASE_RISK_3(String bASE_RISK_3) {
		BASE_RISK_3 = bASE_RISK_3;
	}

	public String getAMT_3() {
		return AMT_3;
	}

	public void setAMT_3(String aMT_3) {
		AMT_3 = aMT_3;
	}

	public String getAMT_LEFT_3() {
		return AMT_LEFT_3;
	}

	public void setAMT_LEFT_3(String aMT_LEFT_3) {
		AMT_LEFT_3 = aMT_LEFT_3;
	}

	public String getBASE_RISK_4() {
		return BASE_RISK_4;
	}

	public void setBASE_RISK_4(String bASE_RISK_4) {
		BASE_RISK_4 = bASE_RISK_4;
	}

	public String getAMT_4() {
		return AMT_4;
	}

	public void setAMT_4(String aMT_4) {
		AMT_4 = aMT_4;
	}

	public String getAMT_LEFT_4() {
		return AMT_LEFT_4;
	}

	public void setAMT_LEFT_4(String aMT_LEFT_4) {
		AMT_LEFT_4 = aMT_LEFT_4;
	}

	public String getRISK_1() {
		return RISK_1;
	}

	public void setRISK_1(String rISK_1) {
		RISK_1 = rISK_1;
	}

	public String getRISK_2() {
		return RISK_2;
	}

	public void setRISK_2(String rISK_2) {
		RISK_2 = rISK_2;
	}

	public String getRISK_3() {
		return RISK_3;
	}

	public void setRISK_3(String rISK_3) {
		RISK_3 = rISK_3;
	}

	public String getRISK_SUM() {
		return RISK_SUM;
	}

	public void setRISK_SUM(String rISK_SUM) {
		RISK_SUM = rISK_SUM;
	}

	public String getVALIDATE_YN() {
		return VALIDATE_YN;
	}

	public void setVALIDATE_YN(String vALIDATE_YN) {
		VALIDATE_YN = vALIDATE_YN;
	}

	public String getEMSGID() {
		return EMSGID;
	}

	public void setEMSGID(String eMSGID) {
		EMSGID = eMSGID;
	}

	public String getEMSGTXT() {
		return EMSGTXT;
	}

	public void setEMSGTXT(String eMSGTXT) {
		EMSGTXT = eMSGTXT;
	}
	
}