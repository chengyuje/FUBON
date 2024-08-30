package com.systex.jbranch.app.server.fps.ins500;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class INS500OutputVO {
	
	private List<Map<String, Object>> savingsList; // 既有儲蓄清單
	private List<Map<String, Object>> reimbursementList; // 主推還本型清單
	private List<Map<String, Object>> protectionList; // 推薦保障型清單
	private List<Map<String, Object>> lstLogTableList;// 錯誤訊息
	
	private BigDecimal insuredFee;
	
	public List<Map<String, Object>> getSavingsList() {
		return savingsList;
	}
	public void setSavingsList(List<Map<String, Object>> savingsList) {
		this.savingsList = savingsList;
	}
	public List<Map<String, Object>> getReimbursementList() {
		return reimbursementList;
	}
	public void setReimbursementList(List<Map<String, Object>> reimbursementList) {
		this.reimbursementList = reimbursementList;
	}
	public List<Map<String, Object>> getProtectionList() {
		return protectionList;
	}
	public void setProtectionList(List<Map<String, Object>> protectionList) {
		this.protectionList = protectionList;
	}
	public BigDecimal getInsuredFee() {
		return insuredFee;
	}
	public void setInsuredFee(BigDecimal insuredFee) {
		this.insuredFee = insuredFee;
	}
	public List<Map<String, Object>> getLstLogTableList() {
		return lstLogTableList;
	}
	public void setLstLogTableList(List<Map<String, Object>> lstLogTableList) {
		this.lstLogTableList = lstLogTableList;
	}
	
	
	
}
