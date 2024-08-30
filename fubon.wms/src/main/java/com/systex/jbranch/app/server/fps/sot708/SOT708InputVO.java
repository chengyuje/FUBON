package com.systex.jbranch.app.server.fps.sot708;

import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/30.
 */
public class SOT708InputVO {
    private String  custId;     //客戶ID
    private String  prodId;     //商品代號
    private String  prodName;   //商品名稱
    private Date    startDate;  //申購起日
    private Date    endDate;    //申購迄日
    private String  tradeSeq;   //下單交易序號
    private String  checkType;  //電文確認碼 1:檢核 2:確認

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

    @Override
    public String toString() {
        return "SOT708InputVO{" +
                "custId='" + custId + '\'' +
                ", prodId='" + prodId + '\'' +
                ", prodName='" + prodName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", tradeSeq='" + tradeSeq + '\'' +
                ", checkType='" + checkType + '\'' +
                '}';
    }
}
