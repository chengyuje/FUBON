package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

public class GetOldItemDetailInputVO {

	private String custId;
	private List planType;
	private List subPlanType;
	private List excludePlanType;
	private List excludeSubPlanType;
	private String inOut;
	
	public GetOldItemDetailInputVO() {
		super();
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public List getPlanType() {
		return planType;
	}
	public void setPlanType(List planType) {
		this.planType = planType;
	}
	public List getSubPlanType() {
		return subPlanType;
	}
	public void setSubPlanType(List subPlanType) {
		this.subPlanType = subPlanType;
	}
	public List getExcludePlanType() {
		return excludePlanType;
	}
	public void setExcludePlanType(List excludePlanType) {
		this.excludePlanType = excludePlanType;
	}
	public List getExcludeSubPlanType() {
		return excludeSubPlanType;
	}
	public void setExcludeSubPlanType(List excludeSubPlanType) {
		this.excludeSubPlanType = excludeSubPlanType;
	}
	public String getInOut() {
		return inOut;
	}
	public void setInOut(String inOut) {
		this.inOut = inOut;
	}
}
