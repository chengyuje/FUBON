package com.systex.jbranch.fubon.commons.esb.vo.nrbrva2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVA2InputVO {
	
	@XmlElement
	private String CUST_ID;
	
	@XmlElement
	private String TRUST_ACCT_BRANCH;
	
	@XmlElement
	private String ACCT_NO1;
	
	@XmlElement
	private String BUYSELL;
	
	@XmlElement
	private String PROD_ID;
	
	@XmlElement
	private String BUY_UNIT;
	
	@XmlElement
	private String ENTRUST_TYPE;
	
	@XmlElement
	private String DUE_DATE;
	
	@XmlElement
	private String DEFAULT_FEE_RATE;
	
	@XmlElement
	private String TRUST_CURR_TYPE;

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getTRUST_ACCT_BRANCH() {
		return TRUST_ACCT_BRANCH;
	}

	public void setTRUST_ACCT_BRANCH(String tRUST_ACCT_BRANCH) {
		TRUST_ACCT_BRANCH = tRUST_ACCT_BRANCH;
	}

	public String getACCT_NO1() {
		return ACCT_NO1;
	}

	public void setACCT_NO1(String aCCT_NO1) {
		ACCT_NO1 = aCCT_NO1;
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

	public String getBUY_UNIT() {
		return BUY_UNIT;
	}

	public void setBUY_UNIT(String bUY_UNIT) {
		BUY_UNIT = bUY_UNIT;
	}

	public String getENTRUST_TYPE() {
		return ENTRUST_TYPE;
	}

	public void setENTRUST_TYPE(String eNTRUST_TYPE) {
		ENTRUST_TYPE = eNTRUST_TYPE;
	}

	public String getDUE_DATE() {
		return DUE_DATE;
	}

	public void setDUE_DATE(String dUE_DATE) {
		DUE_DATE = dUE_DATE;
	}

	public String getDEFAULT_FEE_RATE() {
		return DEFAULT_FEE_RATE;
	}

	public void setDEFAULT_FEE_RATE(String dEFAULT_FEE_RATE) {
		DEFAULT_FEE_RATE = dEFAULT_FEE_RATE;
	}

	public String getTRUST_CURR_TYPE() {
		return TRUST_CURR_TYPE;
	}

	public void setTRUST_CURR_TYPE(String tRUST_CURR_TYPE) {
		TRUST_CURR_TYPE = tRUST_CURR_TYPE;
	}

}