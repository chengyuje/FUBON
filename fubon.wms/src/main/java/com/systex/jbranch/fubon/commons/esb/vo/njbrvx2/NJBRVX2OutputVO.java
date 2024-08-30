package com.systex.jbranch.fubon.commons.esb.vo.njbrvx2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVX2OutputVO {
	
	@XmlElement
	private String ErrorCode;

	@XmlElement
	private String ErrorMsg;

	@XmlElement
	private String BackRate;

	@XmlElement
	private String TxPrice;

	@XmlElement
	private String TxFeeRate;

	@XmlElement
	private String TxFee1;

	@XmlElement
	private String CusType;

	@XmlElement
	private String TrustNo;

	@XmlElement
	private String TxAmt1;

	@XmlElement
	private String TxAmt2;

	@XmlElement
	private String MatureMark;

	@XmlElement
	private String TxFee2;

	@XmlElement
	private String TxMsgCode;

	@XmlElement
	private String Filler;

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

	public String getErrorMsg() {
		return ErrorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		ErrorMsg = errorMsg;
	}

	public String getBackRate() {
		return BackRate;
	}

	public void setBackRate(String backRate) {
		BackRate = backRate;
	}

	public String getTxPrice() {
		return TxPrice;
	}

	public void setTxPrice(String txPrice) {
		TxPrice = txPrice;
	}

	public String getTxFeeRate() {
		return TxFeeRate;
	}

	public void setTxFeeRate(String txFeeRate) {
		TxFeeRate = txFeeRate;
	}

	public String getTxFee1() {
		return TxFee1;
	}

	public void setTxFee1(String txFee1) {
		TxFee1 = txFee1;
	}

	public String getCusType() {
		return CusType;
	}

	public void setCusType(String cusType) {
		CusType = cusType;
	}

	public String getTrustNo() {
		return TrustNo;
	}

	public void setTrustNo(String trustNo) {
		TrustNo = trustNo;
	}

	public String getTxAmt1() {
		return TxAmt1;
	}

	public void setTxAmt1(String txAmt1) {
		TxAmt1 = txAmt1;
	}

	public String getTxAmt2() {
		return TxAmt2;
	}

	public void setTxAmt2(String txAmt2) {
		TxAmt2 = txAmt2;
	}

	public String getMatureMark() {
		return MatureMark;
	}

	public void setMatureMark(String matureMark) {
		MatureMark = matureMark;
	}

	public String getTxFee2() {
		return TxFee2;
	}

	public void setTxFee2(String txFee2) {
		TxFee2 = txFee2;
	}

	public String getTxMsgCode() {
		return TxMsgCode;
	}

	public void setTxMsgCode(String txMsgCode) {
		TxMsgCode = txMsgCode;
	}

	public String getFiller() {
		return Filler;
	}

	public void setFiller(String filler) {
		Filler = filler;
	}

}