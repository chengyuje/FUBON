package com.systex.jbranch.app.server.fps.sot709;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

public class SingleFeeRateVO {
	
	private BigDecimal beneDate;//優惠日期
	private String beneCode;//優惠碼
	private String eviNum;//成交憑證號碼
	private String trustBranch;//信託行
	private String fundNo;//基金代碼
	private String investCur;//申購幣別
	private BigDecimal investAmt;//申購金額
	private BigDecimal fundRate;//原費率
	private String brgType;//議價方式
	private BigDecimal feeRate;//手續費率
	private BigDecimal fixFee;//固定收費
	private String crtBrh;//鍵機分行
	private String tellerld;//經辦
	private String appMgr;//授權主管
	private BigDecimal crtTime;//鍵機時間
	private String groupCode;//團體代碼
	private String dynamicYN;
	
	public BigDecimal getBeneDate() {
		return beneDate;
	}
	public String getBeneCode() {
		return beneCode;
	}
	public String getEviNum() {
		return eviNum;
	}
	public String getTrustBranch() {
		return trustBranch;
	}
	public String getFundNo() {
		return fundNo;
	}
	public String getInvestCur() {
		return investCur;
	}
	public BigDecimal getInvestAmt() {
		return investAmt;
	}
	public BigDecimal getFundRate() {
		return fundRate;
	}
	public String getBrgType() {
		return brgType;
	}
	public BigDecimal getFeeRate() {
		return feeRate;
	}
	public BigDecimal getFixFee() {
		return fixFee;
	}
	public String getCrtBrh() {
		return crtBrh;
	}
	public String getTellerld() {
		return tellerld;
	}
	public String getAppMgr() {
		return appMgr;
	}
	public BigDecimal getCrtTime() {
		return crtTime;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setBeneDate(BigDecimal beneDate) {
		this.beneDate = beneDate;
	}
	public void setBeneCode(String beneCode) {
		this.beneCode = beneCode;
	}
	public void setEviNum(String eviNum) {
		this.eviNum = eviNum;
	}
	public void setTrustBranch(String trustBranch) {
		this.trustBranch = trustBranch;
	}
	public void setFundNo(String fundNo) {
		this.fundNo = fundNo;
	}
	public void setInvestCur(String investCur) {
		this.investCur = investCur;
	}
	public void setInvestAmt(BigDecimal investAmt) {
		this.investAmt = investAmt;
	}
	public void setFundRate(BigDecimal fundRate) {
		this.fundRate = fundRate;
	}
	public void setBrgType(String brgType) {
		this.brgType = brgType;
	}
	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}
	public void setFixFee(BigDecimal fixFee) {
		this.fixFee = fixFee;
	}
	public void setCrtBrh(String crtBrh) {
		this.crtBrh = crtBrh;
	}
	public void setTellerld(String tellerld) {
		this.tellerld = tellerld;
	}
	public void setAppMgr(String appMgr) {
		this.appMgr = appMgr;
	}
	public void setCrtTime(BigDecimal crtTime) {
		this.crtTime = crtTime;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getDynamicYN() {
		return dynamicYN;
	}
	public void setDynamicYN(String dynamicYN) {
		this.dynamicYN = dynamicYN;
	}
	
}
