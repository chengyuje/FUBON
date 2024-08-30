package com.systex.jbranch.fubon.commons.esb.vo.njbrvc3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * SOT330 海外債成交結果查詢
 * 
 * @author SamTu
 * @date 2020.11.05
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVC3OutputDetailsVO {

	@XmlElement
	private String TxDate; // 交易日期
	@XmlElement
	private String CustId; // 身份証ID
	@XmlElement
	private String TxnType2; // 交易種類 B:申購 S:贖回
	@XmlElement
	private String BondNo; // 債券代號
	@XmlElement
	private String BondName; // 債券名稱
	@XmlElement
	private String TxVal; // 成交面額
	@XmlElement
	private String TxAmt; // 成交價格
	@XmlElement
	private String ChanCharge; // 通路服務費
	@XmlElement
	private String TxFee; // 手續費
	@XmlElement
	private String TxCur; // 交易幣別
	@XmlElement
	private String AcAMT; // 總扣款金額
	@XmlElement
	private String TrustNo; // 憑證號碼
	@XmlElement
	private String EntrustStatus; // 成交狀態 01已成交 02未成交 03部分成交
	@XmlElement
	private String SellAmtDate; // 贖回款預計入帳日
	
	
	
	public String getSellAmtDate() {
		return SellAmtDate;
	}
	public void setSellAmtDate(String sellAmtDate) {
		SellAmtDate = sellAmtDate;
	}
	public String getTxDate() {
		return TxDate;
	}
	public void setTxDate(String txDate) {
		TxDate = txDate;
	}
	public String getCustId() {
		return CustId;
	}
	public void setCustId(String custId) {
		CustId = custId;
	}
	public String getTxnType2() {
		return TxnType2;
	}
	public void setTxnType2(String txnType2) {
		TxnType2 = txnType2;
	}
	public String getBondNo() {
		return BondNo;
	}
	public void setBondNo(String bondNo) {
		BondNo = bondNo;
	}
	public String getBondName() {
		return BondName;
	}
	public void setBondName(String bondName) {
		BondName = bondName;
	}
	public String getTxVal() {
		return TxVal;
	}
	public void setTxVal(String txVal) {
		TxVal = txVal;
	}
	public String getTxAmt() {
		return TxAmt;
	}
	public void setTxAmt(String txAmt) {
		TxAmt = txAmt;
	}
	public String getChanCharge() {
		return ChanCharge;
	}
	public void setChanCharge(String chanCharge) {
		ChanCharge = chanCharge;
	}
	public String getTxFee() {
		return TxFee;
	}
	public void setTxFee(String txFee) {
		TxFee = txFee;
	}
	public String getTxCur() {
		return TxCur;
	}
	public void setTxCur(String txCur) {
		TxCur = txCur;
	}
	public String getAcAMT() {
		return AcAMT;
	}
	public void setAcAMT(String acAMT) {
		AcAMT = acAMT;
	}
	public String getTrustNo() {
		return TrustNo;
	}
	public void setTrustNo(String trustNo) {
		TrustNo = trustNo;
	}
	public String getEntrustStatus() {
		return EntrustStatus;
	}
	public void setEntrustStatus(String entrustStatus) {
		EntrustStatus = entrustStatus;
	}
	
	

}
