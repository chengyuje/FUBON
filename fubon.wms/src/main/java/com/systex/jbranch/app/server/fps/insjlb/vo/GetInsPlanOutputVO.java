package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

public class GetInsPlanOutputVO {

	public GetInsPlanOutputVO() {
		super();
	}

	private Object outMain;			// 保險規劃主檔資料(MainDataVO)
	private List outOldItemList;	// 保險規劃既有保障加總(OldItemDtVO)
	private List outNewProdList;	// 保險規劃購買產品(NewProdDataVO)
	private List outBussParmList;	// 保險規劃業務參數(BussParmVO)
	private String flow_id;//2012/11/29,賴禮強,ADD
	
	public Object getOutMain() {
		return outMain;
	}
	public void setOutMain(Object outMain) {
		this.outMain = outMain;
	}
	public List getOutOldItemList() {
		return outOldItemList;
	}
	public void setOutOldItemList(List outOldItemList) {
		this.outOldItemList = outOldItemList;
	}
	public List getOutNewProdList() {
		return outNewProdList;
	}
	public void setOutNewProdList(List outNewProdList) {
		this.outNewProdList = outNewProdList;
	}
	public List getOutBussParmList() {
		return outBussParmList;
	}
	public void setOutBussParmList(List outBussParmList) {
		this.outBussParmList = outBussParmList;
	}
	public String getFlow_id() {
		return flow_id;
	}
	public void setFlow_id(String flow_id) {
		this.flow_id = flow_id;
	}
}
