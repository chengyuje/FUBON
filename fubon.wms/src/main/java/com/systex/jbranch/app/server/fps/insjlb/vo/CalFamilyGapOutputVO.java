package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.math.BigDecimal;
import java.util.List;

public class CalFamilyGapOutputVO {
	private String insCustId;			// 客戶ID
	private String insCustName;			// 客戶姓名
	private BigDecimal insFamilyGap;	// 家庭財務安全缺口
	private BigDecimal insFamilyFee;	// 家庭費用缺口
	private BigDecimal insFamilyDebt;	// 家庭負債缺口
	private BigDecimal oldItemLife;		// 既有壽險保障金額(元)
	private BigDecimal oldItemAccident;	// 既有意外保障金額(元)
	private BigDecimal oldItemDread;	// 既有重大疾病保障金額(元)
	private BigDecimal oldItemHealth;	// 既有住院日額保障金額(元)
	private List lstCashFlow;			// 費用不足明細
	private List lstLogTable;			// 錯誤資訊
	private BigDecimal tempMaxGap;		//家庭財務安全區口
	
	public String getInsCustId() {
		return insCustId;
	}
	public void setInsCustId(String insCustId) {
		this.insCustId = insCustId;
	}
	public BigDecimal getTempMaxGap() {
		return tempMaxGap;
	}
	public void setTempMaxGap(BigDecimal tempMaxGap) {
		this.tempMaxGap = tempMaxGap;
	}
	public String getInsCustName() {
		return insCustName;
	}
	public void setInsCustName(String insCustName) {
		this.insCustName = insCustName;
	}
	public BigDecimal getInsFamilyGap() {
		return insFamilyGap;
	}
	public void setInsFamilyGap(BigDecimal insFamilyGap) {
		this.insFamilyGap = insFamilyGap;
	}
	public BigDecimal getInsFamilyFee() {
		return insFamilyFee;
	}
	public void setInsFamilyFee(BigDecimal insFamilyFee) {
		this.insFamilyFee = insFamilyFee;
	}
	public BigDecimal getInsFamilyDebt() {
		return insFamilyDebt;
	}
	public void setInsFamilyDebt(BigDecimal insFamilyDebt) {
		this.insFamilyDebt = insFamilyDebt;
	}
	public List getLstCashFlow() {
		return lstCashFlow;
	}
	public void setLstCashFlow(List lstCashFlow) {
		this.lstCashFlow = lstCashFlow;
	}
	public List getLstLogTable() {
		return lstLogTable;
	}
	public void setLstLogTable(List lstLogTable) {
		this.lstLogTable = lstLogTable;
	}
	public BigDecimal getOldItemLife() {
		return oldItemLife;
	}
	public void setOldItemLife(BigDecimal oldItemLife) {
		this.oldItemLife = oldItemLife;
	}
	public BigDecimal getOldItemAccident() {
		return oldItemAccident;
	}
	public void setOldItemAccident(BigDecimal oldItemAccident) {
		this.oldItemAccident = oldItemAccident;
	}
	public BigDecimal getOldItemDread() {
		return oldItemDread;
	}
	public void setOldItemDread(BigDecimal oldItemDread) {
		this.oldItemDread = oldItemDread;
	}
	public BigDecimal getOldItemHealth() {
		return oldItemHealth;
	}
	public void setOldItemHealth(BigDecimal oldItemHealth) {
		this.oldItemHealth = oldItemHealth;
	}
}
