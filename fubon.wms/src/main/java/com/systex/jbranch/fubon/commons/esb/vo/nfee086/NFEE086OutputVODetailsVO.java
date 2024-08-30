package com.systex.jbranch.fubon.commons.esb.vo.nfee086;

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
public class NFEE086OutputVODetailsVO {
	
    @XmlElement
	private String Occur;		//資料筆數
    @XmlElement
    private String BeneDate;	//交易日期
    @XmlElement
    private String BeneCode;	//議價編號
    @XmlElement
    private String EviNum;		//成交憑證號碼
    @XmlElement
    private String TrustBranch;	//分行
    @XmlElement
    private String FundNo;		//基金代碼
    @XmlElement
    private String InvestCur;	//申購幣別
    @XmlElement
    private String InvestAmt;	//申購金額
    @XmlElement
    private String ProCode;		//團體代碼
    @XmlElement
    private String CrtBrh;		//鍵機分行
    @XmlElement
    private String TellerId;	//經辦
    @XmlElement
    private String AppMgr;		//授權主管
    @XmlElement
    private String CrtTime;		//鍵機時間
    
    
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
	public String getProCode() {
		return ProCode;
	}
	public void setProCode(String proCode) {
		ProCode = proCode;
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
    
    
	
}
