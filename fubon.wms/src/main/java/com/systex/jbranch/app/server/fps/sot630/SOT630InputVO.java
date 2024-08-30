package com.systex.jbranch.app.server.fps.sot630;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT630InputVO extends PagingInputVO {

	private String custID;
	private String authType;
	private String prodType;
	private String tradeType;
	private String trustTradeType;
	private Date sDate;
	private Date eDate;

	private String CUST_ID;
	private String CUST_NAME;
	private String AUTH_TYPE;
	private String PROD_TYPE;
	private String TRADE_TYPE;
	private Date EVALUATE_VALID_DATE;
	private String ABILITY_RESULT;
	private String TRUST_TRADE_TYPE;
	private String AUTH_BRANCH_NBR;
	private String AUTH_DIRECTOR_EMP_ID;
	private Date AUTH_DATE;
	private String FINACIAL_COGNITION_RESULT;
	
	public String getFINACIAL_COGNITION_RESULT() {
		return FINACIAL_COGNITION_RESULT;
	}

	public void setFINACIAL_COGNITION_RESULT(String fINACIAL_COGNITION_RESULT) {
		FINACIAL_COGNITION_RESULT = fINACIAL_COGNITION_RESULT;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public String getAUTH_TYPE() {
		return AUTH_TYPE;
	}

	public void setAUTH_TYPE(String aUTH_TYPE) {
		AUTH_TYPE = aUTH_TYPE;
	}

	public String getPROD_TYPE() {
		return PROD_TYPE;
	}

	public void setPROD_TYPE(String pROD_TYPE) {
		PROD_TYPE = pROD_TYPE;
	}

	public String getTRADE_TYPE() {
		return TRADE_TYPE;
	}

	public void setTRADE_TYPE(String tRADE_TYPE) {
		TRADE_TYPE = tRADE_TYPE;
	}

	public Date getEVALUATE_VALID_DATE() {
		return EVALUATE_VALID_DATE;
	}

	public void setEVALUATE_VALID_DATE(Date eVALUATE_VALID_DATE) {
		EVALUATE_VALID_DATE = eVALUATE_VALID_DATE;
	}

	public String getABILITY_RESULT() {
		return ABILITY_RESULT;
	}

	public void setABILITY_RESULT(String aBILITY_RESULT) {
		ABILITY_RESULT = aBILITY_RESULT;
	}

	public String getTRUST_TRADE_TYPE() {
		return TRUST_TRADE_TYPE;
	}

	public void setTRUST_TRADE_TYPE(String tRUST_TRADE_TYPE) {
		TRUST_TRADE_TYPE = tRUST_TRADE_TYPE;
	}

	public String getAUTH_BRANCH_NBR() {
		return AUTH_BRANCH_NBR;
	}

	public void setAUTH_BRANCH_NBR(String aUTH_BRANCH_NBR) {
		AUTH_BRANCH_NBR = aUTH_BRANCH_NBR;
	}

	public String getAUTH_DIRECTOR_EMP_ID() {
		return AUTH_DIRECTOR_EMP_ID;
	}

	public void setAUTH_DIRECTOR_EMP_ID(String aUTH_DIRECTOR_EMP_ID) {
		AUTH_DIRECTOR_EMP_ID = aUTH_DIRECTOR_EMP_ID;
	}

	public Date getAUTH_DATE() {
		return AUTH_DATE;
	}

	public void setAUTH_DATE(Date aUTH_DATE) {
		AUTH_DATE = aUTH_DATE;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getTrustTradeType() {
		return trustTradeType;
	}

	public void setTrustTradeType(String trustTradeType) {
		this.trustTradeType = trustTradeType;
	}

	public Date getsDate() {
		return sDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

}
