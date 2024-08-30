package com.systex.jbranch.fubon.commons.esb.vo.nfbrx7;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRX7OutputVO {
	
	@XmlElement
	private String TRUST_CURR;

	@XmlElement
	private String FEE;

	@XmlElement
	private String FEE_RATE;

	@XmlElement
	private String GROUP_OFA;

	@XmlElement
	private String FEE_M;

	@XmlElement
	private String FEE_RATE_M;

	@XmlElement
	private String FEE_H;

	@XmlElement
	private String FEE_RATE_H;

	@XmlElement
	private String DEFAULT_FEE_RATE;

	@XmlElement
	private String DEFAULT_FEE_RATE_M;

	@XmlElement
	private String DEFAULT_FEE_RATE_H;

	public String getTRUST_CURR() {
		return TRUST_CURR;
	}

	public void setTRUST_CURR(String tRUST_CURR) {
		TRUST_CURR = tRUST_CURR;
	}

	public String getFEE() {
		return FEE;
	}

	public void setFEE(String fEE) {
		FEE = fEE;
	}

	public String getFEE_RATE() {
		return FEE_RATE;
	}

	public void setFEE_RATE(String fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}

	public String getGROUP_OFA() {
		return GROUP_OFA;
	}

	public void setGROUP_OFA(String gROUP_OFA) {
		GROUP_OFA = gROUP_OFA;
	}

	public String getFEE_M() {
		return FEE_M;
	}

	public void setFEE_M(String fEE_M) {
		FEE_M = fEE_M;
	}

	public String getFEE_RATE_M() {
		return FEE_RATE_M;
	}

	public void setFEE_RATE_M(String fEE_RATE_M) {
		FEE_RATE_M = fEE_RATE_M;
	}

	public String getFEE_H() {
		return FEE_H;
	}

	public void setFEE_H(String fEE_H) {
		FEE_H = fEE_H;
	}

	public String getFEE_RATE_H() {
		return FEE_RATE_H;
	}

	public void setFEE_RATE_H(String fEE_RATE_H) {
		FEE_RATE_H = fEE_RATE_H;
	}

	public String getDEFAULT_FEE_RATE() {
		return DEFAULT_FEE_RATE;
	}

	public void setDEFAULT_FEE_RATE(String dEFAULT_FEE_RATE) {
		DEFAULT_FEE_RATE = dEFAULT_FEE_RATE;
	}

	public String getDEFAULT_FEE_RATE_M() {
		return DEFAULT_FEE_RATE_M;
	}

	public void setDEFAULT_FEE_RATE_M(String dEFAULT_FEE_RATE_M) {
		DEFAULT_FEE_RATE_M = dEFAULT_FEE_RATE_M;
	}

	public String getDEFAULT_FEE_RATE_H() {
		return DEFAULT_FEE_RATE_H;
	}

	public void setDEFAULT_FEE_RATE_H(String dEFAULT_FEE_RATE_H) {
		DEFAULT_FEE_RATE_H = dEFAULT_FEE_RATE_H;
	}

}