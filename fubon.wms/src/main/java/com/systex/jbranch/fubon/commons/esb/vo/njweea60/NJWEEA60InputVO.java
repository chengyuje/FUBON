package com.systex.jbranch.fubon.commons.esb.vo.njweea60;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.math.BigDecimal;

/**
 * 客戶債券手續費率查詢(網銀快速申購)
 * @author 1800036
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJWEEA60InputVO {
	@XmlElement
	private String CurAcctName;  //戶名代號
	@XmlElement
	private String CustId;  //身份証ID
	@XmlElement
	private String BondNo;  //債券代號
    @XmlElement
	private String PriceType;    //限價方式 1限價；2市價
    @XmlElement
	private BigDecimal EntrustAmt;  //委託價格
    @XmlElement
	private BigDecimal PurchaseAmt; //申購面額
    @XmlElement
	private String TxFeeType;    //手續費議價 1 無議價 2 給予折扣  固定放1
    @XmlElement
	private String TxFeeRate;    //手續費費率
    @XmlElement
	private String TxAcct;   	//扣款帳號
    @XmlElement
   	private String Channel;   	//交易來源 WEB,CTI,IVR,MOB

    
    public String getCurAcctName() {
		return CurAcctName;
	}

	public void setCurAcctName(String curAcctName) {
		CurAcctName = curAcctName;
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
    
    public String getTxFeeRate() {
		return TxFeeRate;
	}

	public void setTxFeeRate(String txFeeRate) {
		TxFeeRate = txFeeRate;
	}

	public String getTxAcct() {
		return TxAcct;
	}

	public void setTxAcct(String txAcct) {
		TxAcct = txAcct;
	}

	public String getChannel() {
		return Channel;
	}

	public void setChannel(String channel) {
		Channel = channel;
	}

	@Override
    public String toString() {
        return "NJWEEA60InputVO {" +
                "BondNo='" + BondNo + '\'' +
                ", PriceType=" + PriceType +
                ", EntrustAmt='" + EntrustAmt + '\'' +
                ", PurchaseAmt='" + PurchaseAmt + '\'' +
                ", TxFeeType=" + TxFeeType + '\'' +
                ", TxFeeRate=" + TxFeeRate + '\'' +
                ", TxAcct='" + TxAcct + '\'' +
                ", Channel='" + Channel + '\'' +
                '}';
    }
}
