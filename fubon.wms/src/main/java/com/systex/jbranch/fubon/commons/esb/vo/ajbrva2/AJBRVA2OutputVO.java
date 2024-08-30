package com.systex.jbranch.fubon.commons.esb.vo.ajbrva2;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/07/26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVA2OutputVO {
    @XmlElement
	private String SPRefId; 	// 傳送序號
    @XmlElement
	private String CustId;  	// 身份証ID
    @XmlElement
	private String BondNo;  	// 債券代號
    @XmlElement
    private String PriceType;	// 限價方式
    @XmlElement
    private String EntrustAmt;	// 委託價格
    @XmlElement
    private String PurchaseAmt; // 申購面額
    @XmlElement
	private String TxFeeType;   // 手續費議價
    @XmlElement
	private String TrustAcct;   // 信託帳號
    @XmlElement
	private String Occur;		// 筆數

    @XmlElement(name = "TxRepeat")
	private List<AJBRVA2OutputVODetails> details;

    public String getSPRefId() {
        return SPRefId;
    }

    public void setSPRefId(String SPRefId) {
        this.SPRefId = SPRefId;
    }

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

    public String getOccur() {
        return Occur;
    }

    public void setOccur(String occur) {
        Occur = occur;
    }

    public List<AJBRVA2OutputVODetails> getDetails() {
        return details;
    }

    public void setDetails(List<AJBRVA2OutputVODetails> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "AJBRVA2OutputVO{" +
                "SPRefId='" + SPRefId + '\'' +
                ", CustId='" + CustId + '\'' +
                ", BondNo='" + BondNo + '\'' +
                ", PriceType=" + PriceType +
                ", EntrustAmt=" + EntrustAmt +
                ", PurchaseAmt=" + PurchaseAmt +
                ", TxFeeType='" + TxFeeType + '\'' +
                ", TrustAcct='" + TrustAcct + '\'' +
                ", Occur='" + Occur + '\'' +
                ", details=" + details +
                '}';
    }
}
