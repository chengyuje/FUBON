package com.systex.jbranch.fubon.commons.esb.vo.ajbrvb9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/07/26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVB9OutputVO {
    @XmlElement
    private String ErrorCode;  // 錯誤碼
    @XmlElement
    private String ErrorMsg;   // 錯誤訊息
    @XmlElement
    private String CusType;    // 客戶性質
    @XmlElement
    private String BackUnit;   // 委賣張數
    @XmlElement
    private String BackFee;    // 預估前手息
    @XmlElement
    private String Type1;      // 信託帳號是否暫停交易：Y.暫停
    @XmlElement
    private String Filler;     // 預留欄位：1-7:參考報價(整數3小數4)、8-15:參考報價日期

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }

    public String getCusType() {
        return CusType;
    }

    public void setCusType(String cusType) {
        CusType = cusType;
    }

    public String getBackUnit() {
        return BackUnit;
    }

    public void setBackUnit(String backUnit) {
        BackUnit = backUnit;
    }

    public String getBackFee() {
        return BackFee;
    }

    public void setBackFee(String backFee) {
        BackFee = backFee;
    }

    public String getType1() {
        return Type1;
    }

    public void setType1(String type1) {
        Type1 = type1;
    }

    public String getFiller() {
        return Filler;
    }

    public void setFiller(String filler) {
        Filler = filler;
    }

    @Override
    public String toString() {
        return "AJBRVB9OutputVO{" +
                "ErrorCode='" + ErrorCode + '\'' +
                ", ErrorMsg='" + ErrorMsg + '\'' +
                ", CusType='" + CusType + '\'' +
                ", BackUnit='" + BackUnit + '\'' +
                ", BackFee='" + BackFee + '\'' +
                ", Type1='" + Type1 + '\'' +
                ", Filler='" + Filler + '\'' +
                '}';
    }
}
