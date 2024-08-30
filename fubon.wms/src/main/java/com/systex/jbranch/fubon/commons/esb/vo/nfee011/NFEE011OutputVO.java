package com.systex.jbranch.fubon.commons.esb.vo.nfee011;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/9.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFEE011OutputVO {
    @XmlElement
    private String SPRefId;         //傳送序號
    @XmlElement
    private String AcctId16;        //帳號
    @XmlElement
    private String Name;            //姓名
    @XmlElement
    private String SEX;             //性別
    @XmlElement
    private String Notice;          //停損停利通知
    @XmlElement
    private String AutoNotice;      //主動停損停利通知
    @XmlElement
    private String AmtNotice;       //到價通知

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

    public String getNotice() {
        return Notice;
    }

    public void setNotice(String notice) {
        Notice = notice;
    }

    public String getAutoNotice() {
        return AutoNotice;
    }

    public void setAutoNotice(String autoNotice) {
        AutoNotice = autoNotice;
    }

    public String getAmtNotice() {
        return AmtNotice;
    }

    public void setAmtNotice(String amtNotice) {
        AmtNotice = amtNotice;
    }
}
