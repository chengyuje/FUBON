package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

public class HVSUB101InputVO implements Serializable {
	
	private String dataYM;			//資料年月
	private List mktIdxList; 		//基金商品清單
	private boolean success;		//交易是否成功Flag	True: 成功	False: 失敗
	private String errorMsg;		//錯誤訊息
	private String periodStartDate;		//資料計算起日	YYYY/MM
	private String periodEndDate;		//資料計算迄日	YYYY/MM
	private String advisorID;		//研究員員編
	
	public String getAdvisorID() {
		return advisorID;
	}
	public void setAdvisorID(String advisorID) {
		this.advisorID = advisorID;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getPeriodStartDate() {
		return periodStartDate;
	}
	public void setPeriodStartDate(String periodStartDate) {
		this.periodStartDate = periodStartDate;
	}
	public String getPeriodEndDate() {
		return periodEndDate;
	}
	public void setPeriodEndDate(String periodEndDate) {
		this.periodEndDate = periodEndDate;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getDataYM() {
		return dataYM;
	}
	public void setDataYM(String dataYM) {
		this.dataYM = dataYM;
	}
	public List getMktIdxList() {
		return mktIdxList;
	}
	public void setMktIdxList(List mktIdxList) {
		this.mktIdxList = mktIdxList;
	}	
}
