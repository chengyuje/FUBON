package com.systex.jbranch.app.server.fps.sot710;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PeriodFeeRateVO {
	
	private String authEmpId;		//覆核主管員編
	private Date authDate;			//覆核完成日期
	private Date authTime;			//覆核完成時間
	private Date brgBeginDate;		//適用優惠期間起日
	private Date brgEndDate;		//適用優惠期間迄日
	private String brgReason;		//議價原因
	private BigDecimal buyHKMrk;	//買入香港市場X折
	private BigDecimal buyUSMrk;	//買入美國市場X折
	private BigDecimal sellHKMrk;	//賣出香港市場X折
	private BigDecimal sellUSMrk;	//賣出美國市場X折
	private Date applyDate;			//申請日期
	private Date applyTime;			//申請時間
	/* 2017.11.22 add by Carley */
	private Date endDate;			//終止日期
	/* 2018.10.15 add by Mimi */
	private BigDecimal buyUKMrk;    //買入倫敦市場X折
	private BigDecimal sellUKMrk;   //賣出倫敦市場X折
	private BigDecimal buyJPMrk;    //買入日本市場X折
	private BigDecimal sellJPMrk;   //賣出日本市場X折
	
	public String getAuthEmpId() {
		return authEmpId;
	}
	public void setAuthEmpId(String authEmpId) {
		this.authEmpId = authEmpId;
	}
	public Date getAuthDate() {
		return authDate;
	}
	public void setAuthDate(Date authDate) {
		this.authDate = authDate;
	}
	public Date getAuthTime() {
		return authTime;
	}
	public void setAuthTime(Date authTime) {
		this.authTime = authTime;
	}
	public Date getBrgBeginDate() {
		return brgBeginDate;
	}
	public void setBrgBeginDate(Date brgBeginDate) {
		this.brgBeginDate = brgBeginDate;
	}
	public Date getBrgEndDate() {
		return brgEndDate;
	}
	public void setBrgEndDate(Date brgEndDate) {
		this.brgEndDate = brgEndDate;
	}
	public String getBrgReason() {
		return brgReason;
	}
	public void setBrgReason(String brgReason) {
		this.brgReason = brgReason;
	}
	public BigDecimal getBuyHKMrk() {
		return buyHKMrk;
	}
	public void setBuyHKMrk(BigDecimal buyHKMrk) {
		this.buyHKMrk = buyHKMrk;
	}
	public BigDecimal getBuyUSMrk() {
		return buyUSMrk;
	}
	public void setBuyUSMrk(BigDecimal buyUSMrk) {
		this.buyUSMrk = buyUSMrk;
	}
	public BigDecimal getSellHKMrk() {
		return sellHKMrk;
	}
	public void setSellHKMrk(BigDecimal sellHKMrk) {
		this.sellHKMrk = sellHKMrk;
	}
	public BigDecimal getSellUSMrk() {
		return sellUSMrk;
	}
	public void setSellUSMrk(BigDecimal sellUSMrk) {
		this.sellUSMrk = sellUSMrk;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public BigDecimal getBuyUKMrk() {
		return buyUKMrk;
	}
	public void setBuyUKMrk(BigDecimal buyUKMrk) {
		this.buyUKMrk = buyUKMrk;
	}
	public BigDecimal getSellUKMrk() {
		return sellUKMrk;
	}
	public void setSellUKMrk(BigDecimal sellUKMrk) {
		this.sellUKMrk = sellUKMrk;
	}
	public BigDecimal getBuyJPMrk() {
		return buyJPMrk;
	}
	public void setBuyJPMrk(BigDecimal buyJPMrk) {
		this.buyJPMrk = buyJPMrk;
	}
	public BigDecimal getSellJPMrk() {
		return sellJPMrk;
	}
	public void setSellJPMrk(BigDecimal sellJPMrk) {
		this.sellJPMrk = sellJPMrk;
	}	
	
}
