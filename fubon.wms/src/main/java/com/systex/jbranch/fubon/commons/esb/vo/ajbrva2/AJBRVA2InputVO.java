package com.systex.jbranch.fubon.commons.esb.vo.ajbrva2;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/07/26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVA2InputVO {
    @XmlElement
	private String CustId;  		// 身份証ID
    @XmlElement
	private String BondNo;  		// 債券代號
    @XmlElement
	private String PriceType;    	// 限價方式：1限價；2市價
    @XmlElement
	private BigDecimal EntrustAmt;  // 委託價格
    @XmlElement
	private BigDecimal PurchaseAmt; // 申購面額
    @XmlElement
	private String TxFeeType;    	// 手續費議價：1不議價；2議價
    @XmlElement
	private String TrustAcct;   	// 信託帳號

    public String getCustId() {
        return CustId;
    }

    public void setCustId(String custId) {
        CustId = custId;
    }

    public String getBondNo() {
        return BondNo;
    }

    public void setBondNo(String bondNo) {
        BondNo = bondNo;
    }

    public String getPriceType() {
        return PriceType;
    }

    public void setPriceType(String priceType) {
        PriceType = priceType;
    }

    public BigDecimal getEntrustAmt() {
        return EntrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        EntrustAmt = entrustAmt;
    }

    public BigDecimal getPurchaseAmt() {
        return PurchaseAmt;
    }

    public void setPurchaseAmt(BigDecimal purchaseAmt) {
        PurchaseAmt = purchaseAmt;
    }

    public String getTxFeeType() {
        return TxFeeType;
    }

    public void setTxFeeType(String txFeeType) {
        TxFeeType = txFeeType;
    }

    public String getTrustAcct() {
        return TrustAcct;
    }

    public void setTrustAcct(String trustAcct) {
        TrustAcct = trustAcct;
    }

    @Override
    public String toString() {
        return "AJBRVA2InputVO{" +
                "CustId='" + CustId + '\'' +
                ", BondNo='" + BondNo + '\'' +
                ", PriceType=" + PriceType +
                ", EntrustAmt='" + EntrustAmt + '\'' +
                ", PurchaseAmt='" + PurchaseAmt + '\'' +
                ", TxFeeType=" + TxFeeType +
                ", TrustAcct='" + TrustAcct + '\'' +
                '}';
    }
}
