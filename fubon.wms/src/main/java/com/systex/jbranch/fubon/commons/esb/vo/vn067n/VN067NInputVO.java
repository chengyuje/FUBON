package com.systex.jbranch.fubon.commons.esb.vo.vn067n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType; 

/**
 * 網路快速下單 - 基金申購檢核
 * Created by Valentino on 2017/03/23.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class VN067NInputVO {
	
	@XmlElement
	private String CurAcctName;  //戶名代號 
	@XmlElement
	private String AcctId16;     // 扣款帳號
	@XmlElement
	private String CustId;       // 客戶ID
	@XmlElement
	private String Amt13;        // 金額
	@XmlElement
	private String TrustKind;    // 信託業務別
	@XmlElement
	private String SubCode;      // 交易類別
	@XmlElement
	private String FundNO;       // 基金代號
	@XmlElement
	private String ConfirmCode;  // 確認碼
	@XmlElement
	private String PayDate01;    // 扣款日期1
	@XmlElement
	private String PayDate02;    // 扣款日期2
	@XmlElement
	private String PayDate03;    // 扣款日期3
	@XmlElement
	private String PayDate04;    // 扣款日期4
	@XmlElement
	private String PayDate05;    // 扣款日期5
	@XmlElement
	private String PayDate06;    // 扣款日期6
	@XmlElement
	private String ChSource;     // 通路來源
	@XmlElement
	private String PayAmt_M;     // 扣款金額中
	@XmlElement
	private String PayAmt_H;     // 扣款金額高
	@XmlElement
	private String Mark;         // 定期不定額
	@XmlElement
	private String ContNum;      // 議價編號
	@XmlElement
	private String TxType;       // 交易類別
	/*
	 @XmlElement 
	private String 		待確認;         //	團體優惠代碼
    */
	public String getAcctId16() {
		return AcctId16;
	}
	public void setAcctId16(String acctId16) {
		AcctId16 = acctId16;
	}
	public String getCustId() {
		return CustId;
	}
	public void setCustId(String custId) {
		CustId = custId;
	}
	public String getAmt13() {
		return Amt13;
	}
	public void setAmt13(String amt13) {
		Amt13 = amt13;
	}
	public String getTrustKind() {
		return TrustKind;
	}
	public void setTrustKind(String trustKind) {
		TrustKind = trustKind;
	}
	public String getSubCode() {
		return SubCode;
	}
	public void setSubCode(String subCode) {
		SubCode = subCode;
	}
	public String getFundNO() {
		return FundNO;
	}
	public void setFundNO(String fundNO) {
		FundNO = fundNO;
	}
	public String getConfirmCode() {
		return ConfirmCode;
	}
	public void setConfirmCode(String confirmCode) {
		ConfirmCode = confirmCode;
	}
	public String getPayDate01() {
		return PayDate01;
	}
	public void setPayDate01(String payDate01) {
		PayDate01 = payDate01;
	}
	public String getPayDate02() {
		return PayDate02;
	}
	public void setPayDate02(String payDate02) {
		PayDate02 = payDate02;
	}
	public String getPayDate03() {
		return PayDate03;
	}
	public void setPayDate03(String payDate03) {
		PayDate03 = payDate03;
	}
	public String getPayDate04() {
		return PayDate04;
	}
	public void setPayDate04(String payDate04) {
		PayDate04 = payDate04;
	}
	public String getPayDate05() {
		return PayDate05;
	}
	public void setPayDate05(String payDate05) {
		PayDate05 = payDate05;
	}
	public String getPayDate06() {
		return PayDate06;
	}
	public void setPayDate06(String payDate06) {
		PayDate06 = payDate06;
	}
	public String getChSource() {
		return ChSource;
	}
	public void setChSource(String chSource) {
		ChSource = chSource;
	}
	public String getPayAmt_M() {
		return PayAmt_M;
	}
	public void setPayAmt_M(String payAmt_M) {
		PayAmt_M = payAmt_M;
	}
	public String getPayAmt_H() {
		return PayAmt_H;
	}
	public void setPayAmt_H(String payAmt_H) {
		PayAmt_H = payAmt_H;
	}
	public String getMark() {
		return Mark;
	}
	public void setMark(String mark) {
		Mark = mark;
	}
	public String getContNum() {
		return ContNum;
	}
	public void setContNum(String contNum) {
		ContNum = contNum;
	}
	public String getTxType() {
		return TxType;
	}
	public void setTxType(String txType) {
		TxType = txType;
	}
	public String getCurAcctName() {
		return CurAcctName;
	}
	public void setCurAcctName(String curAcctName) {
		CurAcctName = curAcctName;
	}
	
	
}
