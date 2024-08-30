package com.systex.jbranch.fubon.commons.esb.vo.njbrvx1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVX1InputVO {
	
    @XmlElement
	private String CustId;  //身份証ID
    
    @XmlElement
	private String BondNo;  //債券代號
    
    @XmlElement
	private String PriceType;    //限價方式
    
    @XmlElement
	private String EntrustAmt;  //委託價格
    
    @XmlElement
	private String PurchaseAmt; //申購面額
    
    @XmlElement
	private String TxFeeType;    //手續費議價
    
    @XmlElement
	private String TrustAcct;   //信託帳號

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

    public String getEntrustAmt() {
		return EntrustAmt;
	}

	public void setEntrustAmt(String entrustAmt) {
		EntrustAmt = entrustAmt;
	}

	public String getPurchaseAmt() {
		return PurchaseAmt;
	}

	public void setPurchaseAmt(String purchaseAmt) {
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
        return "NJBRVA2InputVO{" +
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
