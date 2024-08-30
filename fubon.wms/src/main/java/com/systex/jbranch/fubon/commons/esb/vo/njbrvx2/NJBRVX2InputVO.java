package com.systex.jbranch.fubon.commons.esb.vo.njbrvx2;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVX2InputVO {
	
	@XmlElement
	private String CheckCode;

	@XmlElement
	private String ApplyDate;

	@XmlElement
	private String KeyinNo;

	@XmlElement
	private String KeyinNo1;

	@XmlElement
	private String Type;

	@XmlElement
	private String CustNo;

	@XmlElement
	private String TrustType;

	@XmlElement
	private String TrustAcct;

	@XmlElement
	private String BondNo;

	@XmlElement
	private BigDecimal BondVal;

	@XmlElement
	private String TxAcct;

	@XmlElement
	private String RcvAcct;

	@XmlElement
	private String BranchNo;

	@XmlElement
	private String KeyinId;

	@XmlElement
	private String TxCur;

	@XmlElement
	private BigDecimal TxVal;

	@XmlElement
	private String TapeNo;

	@XmlElement
	private String TxExTeller;

	@XmlElement
	private String TxBoss;

	@XmlElement
	private String TxType;

	@XmlElement
	private String ComDate;

	@XmlElement
	private BigDecimal TxPrice;

	@XmlElement
	private String TxFeeType;

	@XmlElement
	private BigDecimal TxFeeRate;

	@XmlElement
	private String MatureState;

	@XmlElement
	private String Filler;

	@XmlElement
	private String AuthID;

	public String getCheckCode() {
		return CheckCode;
	}

	public void setCheckCode(String checkCode) {
		CheckCode = checkCode;
	}

	public String getApplyDate() {
		return ApplyDate;
	}

	public void setApplyDate(String applyDate) {
		ApplyDate = applyDate;
	}

	public String getKeyinNo() {
		return KeyinNo;
	}

	public void setKeyinNo(String keyinNo) {
		KeyinNo = keyinNo;
	}

	public String getKeyinNo1() {
		return KeyinNo1;
	}

	public void setKeyinNo1(String keyinNo1) {
		KeyinNo1 = keyinNo1;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getCustNo() {
		return CustNo;
	}

	public void setCustNo(String custNo) {
		CustNo = custNo;
	}

	public String getTrustType() {
		return TrustType;
	}

	public void setTrustType(String trustType) {
		TrustType = trustType;
	}

	public String getTrustAcct() {
		return TrustAcct;
	}

	public void setTrustAcct(String trustAcct) {
		TrustAcct = trustAcct;
	}

	public String getBondNo() {
		return BondNo;
	}

	public void setBondNo(String bondNo) {
		BondNo = bondNo;
	}

	public String getTxAcct() {
		return TxAcct;
	}

	public void setTxAcct(String txAcct) {
		TxAcct = txAcct;
	}

	public String getRcvAcct() {
		return RcvAcct;
	}

	public void setRcvAcct(String rcvAcct) {
		RcvAcct = rcvAcct;
	}

	public String getBranchNo() {
		return BranchNo;
	}

	public void setBranchNo(String branchNo) {
		BranchNo = branchNo;
	}

	public String getKeyinId() {
		return KeyinId;
	}

	public void setKeyinId(String keyinId) {
		KeyinId = keyinId;
	}

	public String getTxCur() {
		return TxCur;
	}

	public void setTxCur(String txCur) {
		TxCur = txCur;
	}

	public BigDecimal getBondVal() {
		return BondVal;
	}

	public void setBondVal(BigDecimal bondVal) {
		BondVal = bondVal;
	}

	public BigDecimal getTxVal() {
		return TxVal;
	}

	public void setTxVal(BigDecimal txVal) {
		TxVal = txVal;
	}

	public String getTapeNo() {
		return TapeNo;
	}

	public void setTapeNo(String tapeNo) {
		TapeNo = tapeNo;
	}

	public String getTxExTeller() {
		return TxExTeller;
	}

	public void setTxExTeller(String txExTeller) {
		TxExTeller = txExTeller;
	}

	public String getTxBoss() {
		return TxBoss;
	}

	public void setTxBoss(String txBoss) {
		TxBoss = txBoss;
	}

	public String getTxType() {
		return TxType;
	}

	public void setTxType(String txType) {
		TxType = txType;
	}

	public String getComDate() {
		return ComDate;
	}

	public void setComDate(String comDate) {
		ComDate = comDate;
	}

	public String getTxFeeType() {
		return TxFeeType;
	}

	public void setTxFeeType(String txFeeType) {
		TxFeeType = txFeeType;
	}

	public BigDecimal getTxPrice() {
		return TxPrice;
	}

	public void setTxPrice(BigDecimal txPrice) {
		TxPrice = txPrice;
	}

	public BigDecimal getTxFeeRate() {
		return TxFeeRate;
	}

	public void setTxFeeRate(BigDecimal txFeeRate) {
		TxFeeRate = txFeeRate;
	}

	public String getMatureState() {
		return MatureState;
	}

	public void setMatureState(String matureState) {
		MatureState = matureState;
	}

	public String getFiller() {
		return Filler;
	}

	public void setFiller(String filler) {
		Filler = filler;
	}

	public String getAuthID() {
		return AuthID;
	}

	public void setAuthID(String authID) {
		AuthID = authID;
	}
 	
}