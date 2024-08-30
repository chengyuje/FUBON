package com.systex.jbranch.app.server.fps.fps323;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS323InputVO extends PagingInputVO{
	// Juan
	private double beginAmount;
	private List<Map<String, Object>> ret;
	private Date endDate;
	private int year;
	private int eachYear;

	public double getBeginAmount() {
		return beginAmount;
	}
	public void setBeginAmount(double beginAmount) {
		this.beginAmount = beginAmount;
	}
	public List<Map<String, Object>> getRet() {
		return ret;
	}
	public void setRet(List<Map<String, Object>> ret) {
		this.ret = ret;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getEachYear() {
		return eachYear;
	}
	public void setEachYear(int eachYear) {
		this.eachYear = eachYear;
	}


	// G
	private double sumN1;
	private double sumN2;
	private List<Map<String,String>> mapArray1;
	private List<Map<String,String>> mapArray2;
	private int invPeriod;
	private int expect;
	
	public double getSumN1() {
		return sumN1;
	}
	public void setSumN1(double sumN1) {
		this.sumN1 = sumN1;
	}
	public double getSumN2() {
		return sumN2;
	}
	public void setSumN2(double sumN2) {
		this.sumN2 = sumN2;
	}
	public List<Map<String, String>> getMapArray1() {
		return mapArray1;
	}
	public void setMapArray1(List<Map<String, String>> mapArray1) {
		this.mapArray1 = mapArray1;
	}
	public List<Map<String, String>> getMapArray2() {
		return mapArray2;
	}
	public void setMapArray2(List<Map<String, String>> mapArray2) {
		this.mapArray2 = mapArray2;
	}
	public int getInvPeriod() {
		return invPeriod;
	}
	public void setInvPeriod(int invPeriod) {
		this.invPeriod = invPeriod;
	}
	public int getExpect() {
		return expect;
	}
	public void setExpect(int expect) {
		this.expect = expect;
	}
	
	private String custID;
	private String planID;

	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getPlanID() {
		return planID;
	}
	public void setPlanID(String planID) {
		this.planID = planID;
	}

	
	
}
