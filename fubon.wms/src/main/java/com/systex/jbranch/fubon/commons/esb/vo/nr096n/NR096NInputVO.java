package com.systex.jbranch.fubon.commons.esb.vo.nr096n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/23.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NR096NInputVO {
    @XmlElement
	private String AcctId16; //帳號
    @XmlElement
	private String CustPswd32; //密碼
    @XmlElement
	private String CustId; //客戶ID
    @XmlElement
	private String CurAcctId; //使用者代號
    @XmlElement
	private String CurAcctName; //戶名代號
    @XmlElement
	private String ExchangeNo; //交易所代號
    @XmlElement
	private String DayType; //查詢天數

    public String getAcctId16() {
        return AcctId16;
    }

    public void setAcctId16(String acctId16) {
        AcctId16 = acctId16;
    }

    public String getCustPswd32() {
        return CustPswd32;
    }

    public void setCustPswd32(String custPswd32) {
        CustPswd32 = custPswd32;
    }

    public String getCustId() {
        return CustId;
    }

    public void setCustId(String custId) {
        CustId = custId;
    }

    public String getCurAcctId() {
        return CurAcctId;
    }

    public void setCurAcctId(String curAcctId) {
        CurAcctId = curAcctId;
    }

    public String getCurAcctName() {
        return CurAcctName;
    }

    public void setCurAcctName(String curAcctName) {
        CurAcctName = curAcctName;
    }

    public String getExchangeNo() {
        return ExchangeNo;
    }

    public void setExchangeNo(String exchangeNo) {
        ExchangeNo = exchangeNo;
    }

    public String getDayType() {
        return DayType;
    }

    public void setDayType(String dayType) {
        DayType = dayType;
    }

    @Override
    public String toString() {
        return "NR096NInputVO{" +
                "AcctId16='" + AcctId16 + '\'' +
                ", CustPswd32='" + CustPswd32 + '\'' +
                ", CustId='" + CustId + '\'' +
                ", CurAcctId='" + CurAcctId + '\'' +
                ", CurAcctName='" + CurAcctName + '\'' +
                ", ExchangeNo='" + ExchangeNo + '\'' +
                ", DayType='" + DayType + '\'' +
                '}';
    }
}
