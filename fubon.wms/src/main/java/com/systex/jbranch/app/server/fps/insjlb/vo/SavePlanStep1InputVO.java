package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.math.BigDecimal;
import java.util.List;


public class SavePlanStep1InputVO {

	public SavePlanStep1InputVO() {
		super();
	}

	private String insSeq;			// 序號
	private String insCustID;		// 客戶編號
	private String insPlanId;		// 保險規劃編號
	private String insPlanType;		// 規劃類型
	private BigDecimal flowID;		// 流程編號
	private Object insPlanMain;		// 保險規劃主檔資料(MainDataVO)
	private List insPlanOldItem;	// 保險規劃既有保障加總(OldItemDtVO)

	public String getInsSeq() {
		return insSeq;
	}
	public void setInsSeq(String insSeq) {
		this.insSeq = insSeq;
	}
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
	public String getInsPlanType() {
		return insPlanType;
	}
	public void setInsPlanType(String insPlanType) {
		this.insPlanType = insPlanType;
	}
	public BigDecimal getFlowID() {
		return flowID;
	}
	public void setFlowID(BigDecimal flowID) {
		this.flowID = flowID;
	}
	public Object getInsPlanMain() {
		return insPlanMain;
	}
	public void setInsPlanMain(Object insPlanMain) {
		this.insPlanMain = insPlanMain;
	}
	public List getInsPlanOldItem() {
		return insPlanOldItem;
	}
	public void setInsPlanOldItem(List insPlanOldItem) {
		this.insPlanOldItem = insPlanOldItem;
	}
}
