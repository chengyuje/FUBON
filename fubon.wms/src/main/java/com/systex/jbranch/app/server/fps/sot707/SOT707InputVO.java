package com.systex.jbranch.app.server.fps.sot707;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/26.
 */
public class SOT707InputVO {
    private String  custId; //客戶ID
    private String  prodId; //商品代號
    private String  prodName;   //商品名稱
    private Date    startDate;  //申購起日
    private Date    endDate;    //申購迄日
    private String  prodType;   //產品類別  1：SN 2：海外債
    private String  tradeSeq;   //下單交易序號
    private String  checkType;  //電文確認碼 1:檢核    2:確認
    private BigDecimal purchaseAmt;		//申購金額/庫存金額
    private String isOBU;
    private String gtcNo;		//長效單號

    private String debitAcct; 	//扣款帳號

    public String getDebitAcct() {
		return debitAcct;
	}

	public void setDebitAcct(String debitAcct) {
		this.debitAcct = debitAcct;
	}

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getProdType() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType = prodType;
    }

    public String getTradeSeq() {
        return tradeSeq;
    }

    public void setTradeSeq(String tradeSeq) {
        this.tradeSeq = tradeSeq;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public BigDecimal getPurchaseAmt() {
		return purchaseAmt;
	}

	public void setPurchaseAmt(BigDecimal purchaseAmt) {
		this.purchaseAmt = purchaseAmt;
	}
	
	public String getIsOBU() {
		return isOBU;
	}

	public void setIsOBU(String isOBU) {
		this.isOBU = isOBU;
	}

	public String getGtcNo() {
		return gtcNo;
	}

	public void setGtcNo(String gtcNo) {
		this.gtcNo = gtcNo;
	}

	@Override
    public String toString() {
        return "SOT707InputVO{" +
                "custId='" + custId + '\'' +
                ", prodId='" + prodId + '\'' +
                ", prodName='" + prodName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", prodType='" + prodType + '\'' +
                ", tradeSeq='" + tradeSeq + '\'' +
                ", checkType='" + checkType + '\'' +
                ", purchaseAmt='" + purchaseAmt + '\'' +
                ", isOBU='" + isOBU + '\'' +
                ", gtcNo='" + gtcNo + '\'' +
                '}';
    }
}