package com.systex.jbranch.app.server.fps.insjlb.vo;

public class GetNewPlanOldItemSumInputVO {

	private String custId; 		// 客戶ID(被保人ID)
	private String planType; 	// 計劃型態
	private String subPlanType; // 00:癌症一次給付/醫療實支實付; 01:癌症日額/醫療住院日額
	private String inOut;		// 行內或行外
	public GetNewPlanOldItemSumInputVO() {
		super();
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getSubPlanType() {
		return subPlanType;
	}
	public void setSubPlanType(String subPlanType) {
		this.subPlanType = subPlanType;
	}
	public String getInOut() {
		return inOut;
	}
	public void setInOut(String inOut) {
		this.inOut = inOut;
	}
}
