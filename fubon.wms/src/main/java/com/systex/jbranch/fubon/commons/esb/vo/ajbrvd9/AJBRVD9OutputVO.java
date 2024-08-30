package com.systex.jbranch.fubon.commons.esb.vo.ajbrvd9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 長效單申購電文
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVD9OutputVO {
	@XmlElement
    private String ErrorCode;  // 錯誤碼
    @XmlElement
    private String ErrorMsg;   // 錯誤訊息
    @XmlElement
    private String CusType;    // 客戶性質
    @XmlElement
    private String BackUnit;   // 委賣張數
    @XmlElement
    private String RefVal;     // 參考報價
    @XmlElement
    private String RefValDate; // 參考報價日期
    @XmlElement
    private String Type1;      // 信託帳號是否暫停交易
    @XmlElement
    private String TxMsgCode;  // 委賣單價不符合最新報價之控管
    @XmlElement
    private String Filler;     // 預留欄位

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

	public String getTxMsgCode() {
		return TxMsgCode;
	}

	public void setTxMsgCode(String txMsgCode) {
		TxMsgCode = txMsgCode;
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

}
