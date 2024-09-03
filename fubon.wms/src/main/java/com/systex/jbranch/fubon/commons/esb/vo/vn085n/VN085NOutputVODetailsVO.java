package com.systex.jbranch.fubon.commons.esb.vo.vn085n;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/9.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class VN085NOutputVODetailsVO {
 
	@XmlElement
	private String Occur;//資料筆數
	@XmlElement
	private String BeneDate;//優惠日期
	@XmlElement
	private String BeneCode;//優惠碼
	@XmlElement
	private String EviNum;//成交憑證號碼
	@XmlElement
	private String TrustBranch;//信託行
	@XmlElement
	private String FundNo;//基金代碼
	@XmlElement
	private String InvestCur;//申購幣別
	@XmlElement
	private String InvestAmt;//申購金額
	@XmlElement
	private String FundRate;//原費率
	@XmlElement
	private String BrgType;//議價方式
	@XmlElement
	private String FeeRate;//手續費率
	@XmlElement
	private String FixFee;//固定收費
	@XmlElement
	private String CrtBrh;//鍵機分行
	@XmlElement
	private String TellerId;//經辦
	@XmlElement
	private String AppMgr;//授權主管
	@XmlElement
	private String CrtTime;//鍵機時間
	@XmlElement
    private String DynamicYN; //Y:動態鎖利 空白:一般基金單次議價
	
	public String getOccur() {
		return Occur;
	}
	public void setOccur(String occur) {
		Occur = occur;
	}
	public String getBeneDate() {
		return BeneDate;
	}
	public void setBeneDate(String beneDate) {
		BeneDate = beneDate;
	}
	public String getBeneCode() {
		return BeneCode;
	}
	public void setBeneCode(String beneCode) {
		BeneCode = beneCode;
	}
	public String getEviNum() {
		return EviNum;
	}
	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}
	public String getTrustBranch() {
		return TrustBranch;
	}
	public void setTrustBranch(String trustBranch) {
		TrustBranch = trustBranch;
	}
	public String getFundNo() {
		return FundNo;
	}
	public void setFundNo(String fundNo) {
		FundNo = fundNo;
	}
	public String getInvestCur() {
		return InvestCur;
	}
	public void setInvestCur(String investCur) {
		InvestCur = investCur;
	}
	public String getInvestAmt() {
		return InvestAmt;
	}
	public void setInvestAmt(String investAmt) {
		InvestAmt = investAmt;
	}
	public String getFundRate() {
		return FundRate;
	}
	public void setFundRate(String fundRate) {
		FundRate = fundRate;
	}
	public String getBrgType() {
		return BrgType;
	}
	public void setBrgType(String brgType) {
		BrgType = brgType;
	}
	public String getFeeRate() {
		return FeeRate;
	}
	public void setFeeRate(String feeRate) {
		FeeRate = feeRate;
	}
	public String getFixFee() {
		return FixFee;
	}
	public void setFixFee(String fixFee) {
		FixFee = fixFee;
	}
	public String getCrtBrh() {
		return CrtBrh;
	}
	public void setCrtBrh(String crtBrh) {
		CrtBrh = crtBrh;
	}
	public String getTellerId() {
		return TellerId;
	}
	public void setTellerId(String tellerId) {
		TellerId = tellerId;
	}
	public String getAppMgr() {
		return AppMgr;
	}
	public void setAppMgr(String appMgr) {
		AppMgr = appMgr;
	}
	public String getCrtTime() {
		return CrtTime;
	}
	public void setCrtTime(String crtTime) {
		CrtTime = crtTime;
	}
	public String getDynamicYN() {
		return DynamicYN;
	}
	public void setDynamicYN(String dynamicYN) {
		DynamicYN = dynamicYN;
	}
	
}
