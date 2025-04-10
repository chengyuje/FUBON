package com.systex.jbranch.fubon.commons.esb.vo.ajbrvc9;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 長效單申購電文
 * 
 * 日期格式改為文字格式：上行電文欄位格式 9(08) 要用民國年,如今日: 2016/10/18 => 01051018
 * 					  電文欄位格式 9(07) 要用民國年,如今日: 2016/10/18 => 1051018
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVC9InputVO {
    @XmlElement
    private String CheckCode;   	// 確認碼：1.檢核	2.確認
    @XmlElement
    private String ApplyDate;   	// 申請日期
    @XmlElement
    private String KeyinNo; 		// 理專登錄編號
    @XmlElement
    private String KeyinNo1;    	// 理專登錄序號
    @XmlElement
    private String Type;    		// 初級或次級
    @XmlElement
    private String CustNo;  		// 身份証號
    @XmlElement
    private String TrustType;   	// 信託業務別：Y.外幣	N.台幣
    @XmlElement
    private String TrustAcct;   	// 信託帳號
    @XmlElement
    private String BondNo;  		// 債券代號
    @XmlElement
    private BigDecimal BondVal; 	// 票面價值
    @XmlElement
    private String TxAcct;  		// 扣款帳號
    @XmlElement
    private String RcvAcct; 		// 收益帳號
    @XmlElement
    private String BranchNo;    	// 受理分行
    @XmlElement
    private String KeyinId; 		// 鍵機櫃員
    @XmlElement
    private String TxCur;   		// 委託面額幣別
    @XmlElement
    private BigDecimal TxVal;   	// 委託面額
    @XmlElement
    private String TapeNo;  		// 錄音序號
    @XmlElement
    private String TxExTeller;  	// 解說專員
    @XmlElement
    private String TxBoss;  		// 主管
    @XmlElement
    private String TxType;  		// 限價方式：固定放2市價
    @XmlElement
    private String GtcStartDate; 	// 長效單起日
    @XmlElement
    private String GtcEndDate; 		// 長效單迄日
    @XmlElement
    private BigDecimal TxPrice; 	// 委買單價
    @XmlElement
    private String TxFeeType;   	// 手續費議價
    @XmlElement
    private BigDecimal TxFeeRate;	// 手續費費率
    @XmlElement
    private String AuthID; 			// 被授權人ID
    @XmlElement
    private String Filler;  		// 預留欄位：
    								// 第1位：客戶申貸紀錄(Y/N/空白)
    								// 第2位：長效單/預約單(空白：長效單 1：預約單)
								    // 第3-10位：預約單/長效單生效起日9(08)

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

    public BigDecimal getBondVal() {
        return BondVal;
    }

    public void setBondVal(BigDecimal bondVal) {
        BondVal = bondVal;
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

    public BigDecimal getTxPrice() {
        return TxPrice;
    }

    public void setTxPrice(BigDecimal txPrice) {
        TxPrice = txPrice;
    }

    public String getTxFeeType() {
        return TxFeeType;
    }

    public void setTxFeeType(String txFeeType) {
        TxFeeType = txFeeType;
    }

    public BigDecimal getTxFeeRate() {
        return TxFeeRate;
    }

    public void setTxFeeRate(BigDecimal txFeeRate) {
        TxFeeRate = txFeeRate;
    }

    public String getGtcStartDate() {
		return GtcStartDate;
	}

	public void setGtcStartDate(String gtcStartDate) {
		GtcStartDate = gtcStartDate;
	}

	public String getGtcEndDate() {
		return GtcEndDate;
	}

	public void setGtcEndDate(String gtcEndDate) {
		GtcEndDate = gtcEndDate;
	}

	public String getAuthID() {
		return AuthID;
	}

	public void setAuthID(String authID) {
		AuthID = authID;
	}

	public String getFiller() {
        return Filler;
    }

    public void setFiller(String filler) {
        Filler = filler;
    }
}
