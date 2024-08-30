package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.math.BigDecimal;

public class GetNextPlanInfoOutputVO {

	private String insNewUpd; 			// 固定值，新增或修改
	private BigDecimal insSeq; 			// 規劃序號
	private String insPlanType; 		// 規劃類型
	private String insCustID; 			// 客戶編號
	private String insPlanID; 			// 保險規劃ID
	private String insPlanCurrentStep; 	// 目前規劃步驟
	
	public GetNextPlanInfoOutputVO() {
		super();
	}
	public String getInsNewUpd() {
		return insNewUpd;
	}
	public void setInsNewUpd(String insNewUpd) {
		this.insNewUpd = insNewUpd;
	}
	public BigDecimal getInsSeq() {
		return insSeq;
	}
	public void setInsSeq(BigDecimal insSeq) {
		this.insSeq = insSeq;
	}
	public String getInsPlanType() {
		return insPlanType;
	}
	public void setInsPlanType(String insPlanType) {
		this.insPlanType = insPlanType;
	}
	public String getInsCustID() {
		return insCustID;
	}
	public void setInsCustID(String insCustID) {
		this.insCustID = insCustID;
	}
	public String getInsPlanID() {
		return insPlanID;
	}
	public void setInsPlanID(String insPlanID) {
		this.insPlanID = insPlanID;
	}
	public String getInsPlanCurrentStep() {
		return insPlanCurrentStep;
	}
	public void setInsPlanCurrentStep(String insPlanCurrentStep) {
		this.insPlanCurrentStep = insPlanCurrentStep;
	}
	
}
