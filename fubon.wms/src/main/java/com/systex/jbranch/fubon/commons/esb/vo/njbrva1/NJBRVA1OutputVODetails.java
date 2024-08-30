package com.systex.jbranch.fubon.commons.esb.vo.njbrva1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVA1OutputVODetails {
    @XmlElement
	private String BondName;        //債券名稱
    @XmlElement
	private String ProdRiskLv;      //商品風險等級
    @XmlElement
    private String ProdMinBuyAmt;   //最低申購面額
    @XmlElement
    private String ProdMinGrdAmt;   //累進申購面額
    @XmlElement
	private String ProdCurr;        //計價幣別
    @XmlElement
	private String RefVal;      //參考報價
    @XmlElement
	private String RefValDate;        //報價日期

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

    public String getProdMinBuyAmt() {
		return ProdMinBuyAmt;
	}

	public void setProdMinBuyAmt(String prodMinBuyAmt) {
		ProdMinBuyAmt = prodMinBuyAmt;
	}

	public void setProdMinGrdAmt(String prodMinGrdAmt) {
		ProdMinGrdAmt = prodMinGrdAmt;
	}

    public String getProdCurr() {
        return ProdCurr;
    }

    public String getProdMinGrdAmt() {
		return ProdMinGrdAmt;
	}

	public void setProdCurr(String prodCurr) {
        ProdCurr = prodCurr;
    }

    public String getRefVal() {
        return RefVal;
    }

    public void setRefVal(String refVal) {
        RefVal = refVal;
    }

    public String getRefValDate() {
        return RefValDate;
    }

    public void setRefValDate(String refValDate) {
        RefValDate = refValDate;
    }

    @Override
    public String toString() {
        return "NJBRVA1OutputVODetails{" +
                "BondName='" + BondName + '\'' +
                ", ProdRiskLv='" + ProdRiskLv + '\'' +
                ", ProdMinBuyAmt='" + ProdMinBuyAmt + '\'' +
                ", ProdMinGrdAmt='" + ProdMinGrdAmt + '\'' +
                ", ProdCurr='" + ProdCurr + '\'' +
                ", RefVal=" + RefVal +
                ", RefValDate=" + RefValDate +
                '}';
    }
}
