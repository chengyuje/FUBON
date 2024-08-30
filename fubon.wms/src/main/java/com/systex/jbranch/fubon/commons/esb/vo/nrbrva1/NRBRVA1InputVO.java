package com.systex.jbranch.fubon.commons.esb.vo.nrbrva1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVA1InputVO {
	
	@XmlElement
	private String TRUST_ACCT_BRANCH;
	
	@XmlElement
	private String PROD_ID;
	
	@XmlElement
	private String ACCT_NO1;
	
	@XmlElement
	private String TRUST_CURR_TYPE;

	public String getPROD_ID() {
		return PROD_ID;
	}

	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}

	public String getACCT_NO1() {
		return ACCT_NO1;
	}

	public void setACCT_NO1(String aCCT_NO1) {
		ACCT_NO1 = aCCT_NO1;
	}

	public String getTRUST_CURR_TYPE() {
		return TRUST_CURR_TYPE;
	}

	public void setTRUST_CURR_TYPE(String tRUST_CURR_TYPE) {
		TRUST_CURR_TYPE = tRUST_CURR_TYPE;
	}

	public String getTRUST_ACCT_BRANCH() {
		return TRUST_ACCT_BRANCH;
	}

	public void setTRUST_ACCT_BRANCH(String tRUST_ACCT_BRANCH) {
		TRUST_ACCT_BRANCH = tRUST_ACCT_BRANCH;
	}

	
}