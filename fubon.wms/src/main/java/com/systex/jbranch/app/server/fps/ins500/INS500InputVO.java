package com.systex.jbranch.app.server.fps.ins500;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class INS500InputVO {
	
	private String custId;
	
	private String gender;
	
	private String age;
	
	private String prdID;
	
	private String annual;
	
	private String currCD;
	
	private Date birth;
	
	private List<Map<String, Object>> suggestList;
	
	private List<List<Object>> selectPrdDatas;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}

	public String getAnnual() {
		return annual;
	}

	public void setAnnual(String annual) {
		this.annual = annual;
	}

	public String getCurrCD() {
		return currCD;
	}

	public void setCurrCD(String currCD) {
		this.currCD = currCD;
	}
	
	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public List<Map<String, Object>> getSuggestList() {
		return suggestList;
	}

	public void setSuggestList(List<Map<String, Object>> suggestList) {
		this.suggestList = suggestList;
	}

	public List<List<Object>> getSelectPrdDatas() {
		return selectPrdDatas;
	}

	public void setSelectPrdDatas(List<List<Object>> selectPrdDatas) {
		this.selectPrdDatas = selectPrdDatas;
	}
}
