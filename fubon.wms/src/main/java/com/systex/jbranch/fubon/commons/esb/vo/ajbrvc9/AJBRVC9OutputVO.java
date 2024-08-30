package com.systex.jbranch.fubon.commons.esb.vo.ajbrvc9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 長效單申購電文
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVC9OutputVO {
    @XmlElement
	private String ErrorCode;   // 錯誤碼
    @XmlElement
	private String ErrorMsg;    // 錯誤訊息
    @XmlElement
	private String BackRate;    // 匯率
    @XmlElement
	private String TxFeeRate;   // 手續費費率
    @XmlElement
	private String TxFee1;		// 手續費金額
    @XmlElement
	private String CusType;		// 客戶性質
    @XmlElement
	private String RefVal; 		// 參考報價
    @XmlElement
	private String RefValDate;	// 參考報價日期
    @XmlElement
	private String TxAmt1;  	// 預估成交金額
    @XmlElement
	private String TxMsgCode;   // 專業投資人之提示訊息
    @XmlElement
	private String TxMsgCode1;	// 委買單價不符合最新報價之控管
    @XmlElement
    private String Filler;		// 預留欄位
    
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

    public String getBackRate() {
        return BackRate;
    }

    public void setBackRate(String backRate) {
        BackRate = backRate;
    }

    public String getTxFeeRate() {
        return TxFeeRate;
    }

    public void setTxFeeRate(String txFeeRate) {
        TxFeeRate = txFeeRate;
    }

    public String getTxFee1() {
        return TxFee1;
    }

    public void setTxFee1(String txFee1) {
        TxFee1 = txFee1;
    }

    public String getCusType() {
        return CusType;
    }

    public void setCusType(String cusType) {
        CusType = cusType;
    }

    public String getTxAmt1() {
        return TxAmt1;
    }

    public void setTxAmt1(String txAmt1) {
        TxAmt1 = txAmt1;
    }

    public String getTxMsgCode() {
        return TxMsgCode;
    }

    public void setTxMsgCode(String txMsgCode) {
        TxMsgCode = txMsgCode;
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

	public String getTxMsgCode1() {
		return TxMsgCode1;
	}

	public void setTxMsgCode1(String txMsgCode1) {
		TxMsgCode1 = txMsgCode1;
	}

	public String getFiller() {
		return Filler;
	}

	public void setFiller(String filler) {
		Filler = filler;
	}
    
}
