package com.systex.jbranch.fubon.commons.esb.vo.nfee012;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/9.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFEE012OutputVO {
    @XmlElement
    private String SPRefId;     //傳送序號
    @XmlElement
    private String AcctId16;    //帳號
    @XmlElement
    private String Name;        //姓名
    @XmlElement
    private String SEX;         //性別 0:女 1:男 2:公司
    @XmlElement
    private String FstTrade;    //是否為首購 Y:是 N:否
    @XmlElement
    private String DayFstTrade; //是否為當日首購 Y:是 N:否

    public String getSPRefId() {
        return SPRefId;
    }

    public void setSPRefId(String SPRefId) {
        this.SPRefId = SPRefId;
    }

    public String getAcctId16() {
        return AcctId16;
    }

    public void setAcctId16(String acctId16) {
        AcctId16 = acctId16;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSEX() {
        return SEX;
    }

    public void setSEX(String SEX) {
        this.SEX = SEX;
    }

    public String getFstTrade() {
        return FstTrade;
    }

    public void setFstTrade(String fstTrade) {
        FstTrade = fstTrade;
    }

    public String getDayFstTrade() {
        return DayFstTrade;
    }

    public void setDayFstTrade(String dayFstTrade) {
        DayFstTrade = dayFstTrade;
    }
}
