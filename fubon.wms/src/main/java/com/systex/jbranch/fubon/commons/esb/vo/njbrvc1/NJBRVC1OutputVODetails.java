package com.systex.jbranch.fubon.commons.esb.vo.njbrvc1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NJBRVC1OutputVODetails {
	@XmlElement
	private String TxDate;		 	// 交易日期
	@XmlElement
	private String CustId;			// 身分證ID
	@XmlElement
	private String TxnType; 		// 交易種類
	@XmlElement
	private String BondNo; 			// 債券代號
	@XmlElement
	private String BondName; 		// 債券名稱
	@XmlElement
	private String TxPrice; 		// 委託價格
	@XmlElement
	private String TxEndDate; 		// 長效單委託迄日
	@XmlElement
	private String GtcNo; 			// 長效單號
	@XmlElement
	private String EntrustDate; 	// 成功委託日
	@XmlElement
	private String TrustNo; 		// 憑證編號
	@XmlElement
	private String EntrustStatus;	// 委託狀態 有效單/失效單
	@XmlElement
	private String EXPStartDt;		// 指定交易起日 民國年 / 長效單委託起日
	@XmlElement
	private String TxCurr1; 		// 交易幣別
	@XmlElement
	private String TxVal; 			// 委託面額
	@XmlElement
	private String TxType2; 		// 委託方式 1=預約單 2=長效單
	@XmlElement
	private String PriceType;      // 市價或限價:
								   //     申購 => 1:限價  
								   //             2:市價/限價(自行輸入價格)
	   							   //     贖回 => 2:發行機構或券商回覆之實際成交價格/自行輸入價格
								   //			  4:參考贖回報價減1％（含）以內
	   							   //			  5:參考贖回報價減3％（含）以內
	   							   //			  6:參考贖回報價減5％（含）以內

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

	public String getTxnType() {
		return TxnType;
	}

	public void setTxnType(String txnType) {
		TxnType = txnType;
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

	public String getTxPrice() {
		return TxPrice;
	}

	public void setTxPrice(String txPrice) {
		TxPrice = txPrice;
	}

	public String getTxEndDate() {
		return TxEndDate;
	}

	public void setTxEndDate(String txEndDate) {
		TxEndDate = txEndDate;
	}

	public String getGtcNo() {
		return GtcNo;
	}

	public void setGtcNo(String gtcNo) {
		GtcNo = gtcNo;
	}

	public String getEntrustDate() {
		return EntrustDate;
	}

	public void setEntrustDate(String entrustDate) {
		EntrustDate = entrustDate;
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

	public String getEXPStartDt() {
		return EXPStartDt;
	}

	public void setEXPStartDt(String eXPStartDt) {
		EXPStartDt = eXPStartDt;
	}

	public String getTxCurr1() {
		return TxCurr1;
	}

	public void setTxCurr1(String txCurr1) {
		TxCurr1 = txCurr1;
	}

	public String getTxVal() {
		return TxVal;
	}

	public void setTxVal(String txVal) {
		TxVal = txVal;
	}

	public String getTxType2() {
		return TxType2;
	}

	public void setTxType2(String txType2) {
		TxType2 = txType2;
	}

	public String getPriceType() {
		return PriceType;
	}

	public void setPriceType(String priceType) {
		PriceType = priceType;
	}
}
