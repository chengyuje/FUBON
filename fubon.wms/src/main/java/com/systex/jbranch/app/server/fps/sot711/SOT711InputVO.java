package com.systex.jbranch.app.server.fps.sot711;

import java.math.BigDecimal;

/**
 * Created by SebastianWu on 2016/9/29.
 */
public class SOT711InputVO {
    private String      custId;         //身份証ID
    private String      bondNo;         //債券代號
    private String      priceType;      //限價方式 1：限價 2：市價
    private BigDecimal  entrustAmt;     //委託價格
    private BigDecimal  purchaseAmt;    //申購面額
    private String      txFeeType;      //手續費議價 1：不議價 2：議價
    private String      trustAcct;      //信託帳號
    private String      debitAcct;      //扣款帳號
    private String 		isOBU;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getBondNo() {
        return bondNo;
    }

    public void setBondNo(String bondNo) {
        this.bondNo = bondNo;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public BigDecimal getPurchaseAmt() {
        return purchaseAmt;
    }

    public void setPurchaseAmt(BigDecimal purchaseAmt) {
        this.purchaseAmt = purchaseAmt;
    }

    public String getTxFeeType() {
        return txFeeType;
    }

    public void setTxFeeType(String txFeeType) {
        this.txFeeType = txFeeType;
    }

    public String getTrustAcct() {
        return trustAcct;
    }

    public void setTrustAcct(String trustAcct) {
        this.trustAcct = trustAcct;
    }

    public String getDebitAcct() {
		return debitAcct;
	}

	public void setDebitAcct(String debitAcct) {
		this.debitAcct = debitAcct;
	}

	public String getIsOBU() {
		return isOBU;
	}

	public void setIsOBU(String isOBU) {
		this.isOBU = isOBU;
	}

	@Override
    public String toString() {
        return "SOT711InputVO{" +
                "custId='" + custId + '\'' +
                ", bondNo='" + bondNo + '\'' +
                ", priceType='" + priceType + '\'' +
                ", entrustAmt=" + entrustAmt +
                ", purchaseAmt=" + purchaseAmt +
                ", txFeeType='" + txFeeType + '\'' +
                ", trustAcct='" + trustAcct + '\'' +
                ", debitAcct='" + debitAcct + '\'' +
                ", isOBU='" + isOBU + '\'' +
                '}';
    }
}
