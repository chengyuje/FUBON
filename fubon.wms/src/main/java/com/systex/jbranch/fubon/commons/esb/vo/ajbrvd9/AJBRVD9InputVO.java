package com.systex.jbranch.fubon.commons.esb.vo.ajbrvd9;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 長效單贖回電文
 * 
 * 日期格式改為文字格式：上行電文欄位格式 9(08) 要用民國年,如今日: 2016/10/18 => 01051018
 * 					  電文欄位格式 9(07) 要用民國年,如今日: 2016/10/18 => 1051018
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVD9InputVO {
	@XmlElement
    private String CheckCode;		// 確認碼：1.檢核	2.確認
    @XmlElement
    private String ApplyDate;		// 申請日期
    @XmlElement
    private String KeyinNo;			// 理專登錄編號
    @XmlElement
    private String KeyinNo1;		// 理專登錄序號
    @XmlElement
    private String CustNo;			// 身份証號
    @XmlElement
    private String TrustType;		// 信託業務別
    @XmlElement
    private String TrustAcct;		// 信託帳號
    @XmlElement
    private String BondNo;			// 債券代號
    @XmlElement
    private String TrustNo;			// 憑証號碼
    @XmlElement
    private BigDecimal TrustVal;	// 庫存餘額
    @XmlElement
    private BigDecimal BondVal;		// 票面價值
    @XmlElement
    private BigDecimal Unit;		// 庫存張數
    @XmlElement
    private String RcvAcct;			// 入帳帳號
    @XmlElement
    private String BranchNo;		// 受理分行
    @XmlElement
    private String KeyinId;			// 鍵機櫃員
    @XmlElement
    private String TxCur;			// 委託面額幣別
    @XmlElement
    private BigDecimal TxVal;		// 委託面額
    @XmlElement
    private BigDecimal TxPrice;		// 委賣單價
    @XmlElement
    private String TxType;			//限價方式
    @XmlElement
    private String TxExTeller;		//解說專員
    @XmlElement
    private String GtcEndDate;		//長效單迄日
    @XmlElement
    private String Filler;			//是否暫停交易

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

    public String getTrustNo() {
        return TrustNo;
    }

    public void setTrustNo(String trustNo) {
        TrustNo = trustNo;
    }

    public BigDecimal getTrustVal() {
        return TrustVal;
    }

    public void setTrustVal(BigDecimal trustVal) {
        TrustVal = trustVal;
    }

    public BigDecimal getBondVal() {
        return BondVal;
    }

    public void setBondVal(BigDecimal bondVal) {
        BondVal = bondVal;
    }

    public BigDecimal getUnit() {
        return Unit;
    }

    public void setUnit(BigDecimal unit) {
        Unit = unit;
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

    public BigDecimal getTxVal() {
        return TxVal;
    }

    public void setTxVal(BigDecimal txVal) {
        TxVal = txVal;
    }

    public BigDecimal getTxPrice() {
        return TxPrice;
    }

    public void setTxPrice(BigDecimal txPrice) {
        TxPrice = txPrice;
    }

    public String getTxType() {
        return TxType;
    }

    public void setTxType(String txType) {
        TxType = txType;
    }

    public String getTxExTeller() {
        return TxExTeller;
    }

    public void setTxExTeller(String txExTeller) {
        TxExTeller = txExTeller;
    }

	public String getGtcEndDate() {
		return GtcEndDate;
	}

	public void setGtcEndDate(String gtcEndDate) {
		GtcEndDate = gtcEndDate;
	}

	public String getFiller() {
        return Filler;
    }

    public void setFiller(String filler) {
        Filler = filler;
    }

}
