package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;


public class SavePlanStep2InputVO {

	public SavePlanStep2InputVO() {
		super();
	}
	
	private String insCustID;			//客戶編號
	private String insPlanId;			//保險規劃編號
	private Object insPlanMain;			//保險規劃主檔資料
	private List insPlanNewProd;		//保險規劃購買產品
	
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
	public Object getInsPlanMain() {
		return insPlanMain;
	}
	public void setInsPlanMain(Object insPlanMain) {
		this.insPlanMain = insPlanMain;
	}
	public List getInsPlanNewProd() {
		return insPlanNewProd;
	}
	public void setInsPlanNewProd(List insPlanNewProd) {
		this.insPlanNewProd = insPlanNewProd;
	}
}
