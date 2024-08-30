package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;


public class SavePlanStep3InputVO {

	public SavePlanStep3InputVO() {
		super();
	}
	
	private String insCustID;			//客户ID
	private String insPlanId;			//客戶編號
	private String insSeq;				//序號
	private Object insPlanMain;			//保險規劃主檔資料

	public String getInsCustID() {
		return insCustID;
	}
	public void setInsCustID(String insCustID) {
		this.insCustID = insCustID;
	}
	public String getInsPlanId() {
		return insPlanId;
	}
	public void setInsPlanId(String insPlanId) {
		this.insPlanId = insPlanId;
	}
	public String getInsSeq() {
		return insSeq;
	}
	public void setInsSeq(String insSeq) {
		this.insSeq = insSeq;
	}
	public Object getInsPlanMain() {
		return insPlanMain;
	}
	public void setInsPlanMain(Object insPlanMain) {
		this.insPlanMain = insPlanMain;
	}
}
