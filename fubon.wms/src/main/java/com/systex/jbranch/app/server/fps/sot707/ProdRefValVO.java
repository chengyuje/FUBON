package com.systex.jbranch.app.server.fps.sot707;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/29.
 */
public class ProdRefValVO {
    String      CustId;         //身份証ID
    String      BondNo;         //債券代號
    String      BondName;       //債券名稱
    String      ProdRiskLv;     //商品風險等級
    BigDecimal  ProdMinBuyAmt;  //最低申購面額
    BigDecimal  ProdMinGrdAmt;  //累進申購面額
    String      ProdCurr;       //計價幣別
    BigDecimal  RefVal;         //參考報價
    Date        RefValDate;     //報價日期

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

    public String getBondName() {
        return BondName;
    }

    public void setBondName(String bondName) {
        BondName = bondName;
    }

    public String getProdRiskLv() {
        return ProdRiskLv;
    }

    public void setProdRiskLv(String prodRiskLv) {
        ProdRiskLv = prodRiskLv;
    }

    public BigDecimal getProdMinBuyAmt() {
        return ProdMinBuyAmt;
    }

    public void setProdMinBuyAmt(BigDecimal prodMinBuyAmt) {
        ProdMinBuyAmt = prodMinBuyAmt;
    }

    public BigDecimal getProdMinGrdAmt() {
        return ProdMinGrdAmt;
    }

    public void setProdMinGrdAmt(BigDecimal prodMinGrdAmt) {
        ProdMinGrdAmt = prodMinGrdAmt;
    }

    public String getProdCurr() {
        return ProdCurr;
    }

    public void setProdCurr(String prodCurr) {
        ProdCurr = prodCurr;
    }

    public BigDecimal getRefVal() {
        return RefVal;
    }

    public void setRefVal(BigDecimal refVal) {
        RefVal = refVal;
    }

    public Date getRefValDate() {
        return RefValDate;
    }

    public void setRefValDate(Date refValDate) {
        RefValDate = refValDate;
    }

    @Override
    public String toString() {
        return "ProdRefValVO{" +
                "CustId='" + CustId + '\'' +
                ", BondNo='" + BondNo + '\'' +
                ", BondName='" + BondName + '\'' +
                ", ProdRiskLv='" + ProdRiskLv + '\'' +
                ", ProdMinBuyAmt=" + ProdMinBuyAmt +
                ", ProdMinGrdAmt=" + ProdMinGrdAmt +
                ", ProdCurr='" + ProdCurr + '\'' +
                ", RefVal=" + RefVal +
                ", RefValDate=" + RefValDate +
                '}';
    }
}
