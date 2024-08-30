package com.systex.jbranch.app.server.fps.insjlb.vo;


public class GetInsPlanInputVO {

	public GetInsPlanInputVO() {
		super();
	}

	private String custID;	// 客戶編號
	private String planId;	// 保險規劃編號
	private String planType;// 規劃類型
	private String seq;//規劃序號 //2012/11/27,賴禮強,ADD

	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
}
