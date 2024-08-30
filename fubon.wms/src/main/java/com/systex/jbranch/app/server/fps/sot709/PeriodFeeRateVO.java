package com.systex.jbranch.app.server.fps.sot709;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PeriodFeeRateVO {
	
	private Date applyDate;//申請日期
	private Date brgBeginDate;//優惠期間(起)
	private Date brgEndDate;//優惠期間(迄)
	private BigDecimal dmtStock;//國內股票型
	private BigDecimal dmtBond;//國內債券型
	private BigDecimal dmtBalanced;//國內平衡型
	private BigDecimal frnStock;//國外股票型
	private BigDecimal frnBond;//國外債券型
	private BigDecimal frnBalanced;//國外平衡型
	private String brgReason;//分行備註
	private Date authDate;//覆核日期
	private Date terminateDate;//終止日期
	private List<Map<String, Object>> periodFeeRateList;
	
	
	
	public Date getApplyDate() {
		return applyDate;
	}
	public Date getBrgBeginDate() {
		return brgBeginDate;
	}
	public Date getBrgEndDate() {
		return brgEndDate;
	}
	public BigDecimal getDmtStock() {
		return dmtStock;
	}
	public BigDecimal getDmtBond() {
		return dmtBond;
	}
	public BigDecimal getDmtBalanced() {
		return dmtBalanced;
	}
	public BigDecimal getFrnStock() {
		return frnStock;
	}
	public BigDecimal getFrnBond() {
		return frnBond;
	}
	public BigDecimal getFrnBalanced() {
		return frnBalanced;
	}
	public String getBrgReason() {
		return brgReason;
	}
	public Date getAuthDate() {
		return authDate;
	}
	public Date getTerminateDate() {
		return terminateDate;
	}
	public List<Map<String, Object>> getPeriodFeeRateList() {
		return periodFeeRateList;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public void setBrgBeginDate(Date brgBeginDate) {
		this.brgBeginDate = brgBeginDate;
	}
	public void setBrgEndDate(Date brgEndDate) {
		this.brgEndDate = brgEndDate;
	}
	public void setDmtStock(BigDecimal dmtStock) {
		this.dmtStock = dmtStock;
	}
	public void setDmtBond(BigDecimal dmtBond) {
		this.dmtBond = dmtBond;
	}
	public void setDmtBalanced(BigDecimal dmtBalanced) {
		this.dmtBalanced = dmtBalanced;
	}
	public void setFrnStock(BigDecimal frnStock) {
		this.frnStock = frnStock;
	}
	public void setFrnBond(BigDecimal frnBond) {
		this.frnBond = frnBond;
	}
	public void setFrnBalanced(BigDecimal frnBalanced) {
		this.frnBalanced = frnBalanced;
	}
	public void setBrgReason(String brgReason) {
		this.brgReason = brgReason;
	}
	public void setAuthDate(Date authDate) {
		this.authDate = authDate;
	}
	public void setTerminateDate(Date terminateDate) {
		this.terminateDate = terminateDate;
	}
	public void setPeriodFeeRateList(List<Map<String, Object>> periodFeeRateList) {
		this.periodFeeRateList = periodFeeRateList;
	}
	
	
	

	
	
}
