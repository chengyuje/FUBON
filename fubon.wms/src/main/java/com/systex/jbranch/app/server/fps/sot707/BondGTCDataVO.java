package com.systex.jbranch.app.server.fps.sot707;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class BondGTCDataVO {
	private Date  	TxDate;    		//交易日期
	private String  CustId; 		//客戶ID
	private String  TxnType;   		//債券名稱
	private String  BondNo;    		//債券代號
	private String 	BondName;   	//債券名稱
    private BigDecimal  TxPrice;   	//委託價格
    private Date  TxEndDate;  		//長效單委託迄日
    private String GtcNo;			//長效單號
    private Date EntrustDate;		//成功委託日
    private String TrustNo;			//憑證號碼
    private String  EntrustStatus;  //委託狀態
    
	private Date EXPStartDt;       // 指定交易起日 民國年 / 長效單委託起日
	private String TxCurr1;        // 交易幣別
	private String TxVal;          // 委託面額
	private String TxType2;        // 委託方式 1=預約單 2=長效單
	
	private String PriceType;      // 市價或限價:
								   //     申購 => 1:限價  
								   //             2:市價/限價(自行輸入價格)
    							   //     贖回 => 2:發行機構或券商回覆之實際成交價格/自行輸入價格
								   //			  4:參考贖回報價減1％（含）以內
								   //			  5:參考贖回報價減3％（含）以內
								   //			  6:參考贖回報價減5％（含）以內
			
	public Date getTxDate() {
		return TxDate;
	}

	public void setTxDate(Date txDate) {
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

	public BigDecimal getTxPrice() {
		return TxPrice;
	}

	public void setTxPrice(BigDecimal txPrice) {
		TxPrice = txPrice;
	}

	public Date getTxEndDate() {
		return TxEndDate;
	}

	public void setTxEndDate(Date txEndDate) {
		TxEndDate = txEndDate;
	}

	public String getGtcNo() {
		return GtcNo;
	}

	public void setGtcNo(String gtcNo) {
		GtcNo = gtcNo;
	}

	public Date getEntrustDate() {
		return EntrustDate;
	}

	public void setEntrustDate(Date entrustDate) {
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

	public Date getEXPStartDt() {
		return EXPStartDt;
	}

	public void setEXPStartDt(Date eXPStartDt) {
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
