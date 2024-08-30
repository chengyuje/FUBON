package com.systex.jbranch.app.server.fps.appvo.fp;


public class FPSUB000OutputVO {

	public FPSUB000OutputVO() {
		
	}
	
	public String custID;//客戶ID
	public String custName;//客戶姓名
	public String errorMsg;//錯誤訊息
	public String specialCust;//客戶是否為特殊客戶Y=是N=不是
	//2013/11/26 add by Avia
	public String specialString;	 //成為特定客戶的條件內容
	
	public String getSpecialCust() {
		return specialCust;
	}
	public void setSpecialCust(String specialCust) {
		this.specialCust = specialCust;
	}
	public String getSpecialString() {
		return specialString;
	}
	public void setSpecialString(String specialString) {
		this.specialString = specialString;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
}