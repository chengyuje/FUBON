package com.systex.jbranch.app.server.fps.sot703;

import java.util.Date;

public class SOT703InputVO {
    private String custId;      //客戶ID
    private String tradeSeq;    //下單交易序號
    private String seqNo;       //明細檔交易流水號
    private String isOBU;
    private String certificateId;
    private String prodId;
    private String amt1;
    private String amt2;
    private String amt3;
    private String currency;
    private Date tradeDate; //交易日期
    private String debitAcct; //扣款帳號
    private String trustTS;				//M:金錢信託 S:特金交易
    private String notifyType;//電文EBPMN通知類型
    private String confirm; //確認碼 1.檢核 , 空白：確認
    private String returnNumZeroYN; //動態鎖利母基金庫存為0仍要顯示
    
    
	public String getNotifyType() {
		return notifyType;
	}
	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getTradeSeq() {
		return tradeSeq;
	}
	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getIsOBU() {
		return isOBU;
	}
	public void setIsOBU(String isOBU) {
		this.isOBU = isOBU;
	}
	public String getCertificateId() {
		return certificateId;
	}
	public void setCertificateId(String certificateId) {
		this.certificateId = certificateId;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getAmt1() {
		return amt1;
	}
	public void setAmt1(String amt1) {
		this.amt1 = amt1;
	}
	public String getAmt2() {
		return amt2;
	}
	public void setAmt2(String amt2) {
		this.amt2 = amt2;
	}
	public String getAmt3() {
		return amt3;
	}
	public void setAmt3(String amt3) {
		this.amt3 = amt3;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getDebitAcct() {
		return debitAcct;
	}
	public void setDebitAcct(String debitAcct) {
		this.debitAcct = debitAcct;
	}
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	public String getReturnNumZeroYN() {
		return returnNumZeroYN;
	}
	public void setReturnNumZeroYN(String returnNumZeroYN) {
		this.returnNumZeroYN = returnNumZeroYN;
	}
	@Override
	public String toString() {
		return "SOT703InputVO [custId=" + custId + ", tradeSeq=" + tradeSeq
				+ ", seqNo=" + seqNo + ", isOBU=" + isOBU + ", certificateId="
				+ certificateId + ", prodId=" + prodId + ", amt1=" + amt1
				+ ", amt2=" + amt2 + ", amt3=" + amt3 + ", currency="
				+ currency + ",tradeDate=" + tradeDate + ",debitAcct=" + debitAcct + "]";
	}
}
