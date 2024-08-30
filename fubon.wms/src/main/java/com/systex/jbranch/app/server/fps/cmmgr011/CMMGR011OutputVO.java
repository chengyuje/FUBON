package com.systex.jbranch.app.server.fps.cmmgr011;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR011OutputVO extends PagingOutputVO {
	private List<Map<String,Object>> priList;
	private List<Map<String,Object>> txnList;
	private List<CMMGR011OutputVO2> itemPriFuns;
	private List<String> allowFuns;
	//功能代號對應功能明細
	private String apply;
	private List<Map<String,Object>> functionList;

	public List<Map<String, Object>> getPriList() {
		return priList;
	}
	public void setPriList(List<Map<String, Object>> priList) {
		this.priList = priList;
	}
	public List<Map<String, Object>> getTxnList() {
		return txnList;
	}
	public void setTxnList(List<Map<String, Object>> txnList) {
		this.txnList = txnList;
	}
	public List<CMMGR011OutputVO2> getItemPriFuns() {
		return itemPriFuns;
	}
	public void setItemPriFuns(List<CMMGR011OutputVO2> itemPriFuns) {
		this.itemPriFuns = itemPriFuns;
	}
	public List<String> getAllowFuns() {
		return allowFuns;
	}
	public void setAllowFuns(List<String> allowFuns) {
		this.allowFuns = allowFuns;
	}
	public String getApply() {
		return apply;
	}
	public void setApply(String apply) {
		this.apply = apply;
	}
	public List<Map<String, Object>> getFunctionList() {
		return functionList;
	}
	public void setFunctionList(List<Map<String, Object>> functionList) {
		this.functionList = functionList;
	}
}
