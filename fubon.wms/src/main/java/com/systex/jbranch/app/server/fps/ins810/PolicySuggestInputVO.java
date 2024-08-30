package com.systex.jbranch.app.server.fps.ins810;

import org.apache.commons.lang.ObjectUtils;

public class PolicySuggestInputVO {
	
	// 幣別
	private String currCD;

	// 參數編號
	private String paraNo;

	// 參數類型 (透過類型找 paraNo)
	private String paraType;
	
	// 性別
	private String gender;
	
	// 年齡
	private int age;
	private String insAge;
	
	// 險種代號
	private String insPrdId;
	
	// 繳費年期
	private String annual;
	
	//資產傳承
	private String estate;
	
	public PolicySuggestInputVO(){
		
	}
	
	public PolicySuggestInputVO(String paraType, String paraNo, String currCD, String insAge) {
		this.paraType = paraType;
		this.paraNo = paraNo;
		this.currCD = currCD;
		this.insAge = insAge;
	}
	
	public String getCurrCD() {
		return currCD;
	}

	public void setCurrCD(String currCD) {
		this.currCD = currCD;
	}

	public String getParaNo() {
		return paraNo;
	}

	public void setParaNo(String paraNo) {
		this.paraNo = paraNo;
	}

	public String getParaType() {
		return paraType;
	}

	public void setParaType(String paraType) {
		this.paraType = paraType;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getInsPrdId() {
		return insPrdId;
	}

	public void setInsPrdId(String insPrdId) {
		this.insPrdId = insPrdId;
	}

	public String getAnnual() {
		return annual;
	}

	public void setAnnual(String annual) {
		this.annual = annual;
	}

	public String getInsAge() {
		return insAge;
	}

	public void setInsAge(String insAge) {
		this.insAge = insAge;
	}

	public String getEstate() {
		return estate;
	}

	public void setEstate(String estate) {
		this.estate = estate;
	}
	
	
}
