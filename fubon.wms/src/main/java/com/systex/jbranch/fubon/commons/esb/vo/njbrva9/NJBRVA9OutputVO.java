package com.systex.jbranch.fubon.commons.esb.vo.njbrva9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/26.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVA9OutputVO {
    @XmlElement
	private String ErrorCode;   //錯誤碼
    @XmlElement
	private String ErrorMsg;    //錯誤訊息
    @XmlElement
	private String BackRate;    //匯率
    @XmlElement
	private String TxPrice; //委買單價
    @XmlElement
	private String TxFeeRate;   //手續費費率
    @XmlElement
	private String TxFee1;  //手續費金額
    @XmlElement
	private String CusType; //客戶性質
    @XmlElement
	private String TrustNo; //憑証號碼
    @XmlElement
	private String TxAmt1;  //預估成交金額
    @XmlElement
	private String TxAmt2;  //預估收付金額
    @XmlElement
	private String MatureMark;  //到期狀態註記
    @XmlElement
	private String TxFee2;  //預估應付前手息
    @XmlElement
	private String TxMsgCode;   //專業投資人之提示訊息

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

    public String getTxPrice() {
        return TxPrice;
    }

    public void setTxPrice(String txPrice) {
        TxPrice = txPrice;
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

    public String getTrustNo() {
        return TrustNo;
    }

    public void setTrustNo(String trustNo) {
        TrustNo = trustNo;
    }

    public String getTxAmt1() {
        return TxAmt1;
    }

    public void setTxAmt1(String txAmt1) {
        TxAmt1 = txAmt1;
    }

    public String getTxAmt2() {
        return TxAmt2;
    }

    public void setTxAmt2(String txAmt2) {
        TxAmt2 = txAmt2;
    }

    public String getMatureMark() {
        return MatureMark;
    }

    public void setMatureMark(String matureMark) {
        MatureMark = matureMark;
    }

    public String getTxFee2() {
        return TxFee2;
    }

    public void setTxFee2(String txFee2) {
        TxFee2 = txFee2;
    }

    public String getTxMsgCode() {
        return TxMsgCode;
    }

    public void setTxMsgCode(String txMsgCode) {
        TxMsgCode = txMsgCode;
    }
}
