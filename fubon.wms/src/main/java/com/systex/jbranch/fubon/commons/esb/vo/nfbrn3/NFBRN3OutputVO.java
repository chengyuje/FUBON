package com.systex.jbranch.fubon.commons.esb.vo.nfbrn3;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN3OutputVO {
    @XmlElement
	private String ErrorCode;       //錯誤碼
    @XmlElement
	private String ErrorMsg;        //錯誤訊息
    @XmlElement
	private String ErrorCode_1;     //錯誤碼1
    @XmlElement
	private String ErrorMsg_1;      //錯誤訊息1
    @XmlElement
	private String ErrorCode_2;     //錯誤碼2
    @XmlElement
	private String ErrorMsg_2;      //錯誤訊息2
    @XmlElement
	private String ErrorCode_3;     //錯誤碼3
    @XmlElement
	private String ErrorMsg_3;      //錯誤訊息3
    @XmlElement
	private String Short_1;         //是否為短線交易1
    @XmlElement
	private String FeeRate_1;   //手續費率1
    @XmlElement
	private String GroupOfa_1;      //團體代碼1
    @XmlElement
	private String RedeemAmt_1; //贖回金額1
    @XmlElement
	private String Short_2;         //是否為短線交易2
    @XmlElement
	private String FeeRate_2;       //手續費率2
    @XmlElement
	private String GroupOfa_2;      //團體代碼2
    @XmlElement
	private String RedeemAmt_2;     //贖回金額2
    @XmlElement
	private String Short_3;         //是否為短線交易3
    @XmlElement
	private String FeeRate_3;       //手續費率3
    @XmlElement
	private String GroupOfa_3;      //團體代碼3
    @XmlElement
	private String RedeemAmt_3;     //贖回金額3
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
	public String getErrorCode_1() {
		return ErrorCode_1;
	}
	public void setErrorCode_1(String errorCode_1) {
		ErrorCode_1 = errorCode_1;
	}
	public String getErrorMsg_1() {
		return ErrorMsg_1;
	}
	public void setErrorMsg_1(String errorMsg_1) {
		ErrorMsg_1 = errorMsg_1;
	}
	public String getErrorCode_2() {
		return ErrorCode_2;
	}
	public void setErrorCode_2(String errorCode_2) {
		ErrorCode_2 = errorCode_2;
	}
	public String getErrorMsg_2() {
		return ErrorMsg_2;
	}
	public void setErrorMsg_2(String errorMsg_2) {
		ErrorMsg_2 = errorMsg_2;
	}
	public String getErrorCode_3() {
		return ErrorCode_3;
	}
	public void setErrorCode_3(String errorCode_3) {
		ErrorCode_3 = errorCode_3;
	}
	public String getErrorMsg_3() {
		return ErrorMsg_3;
	}
	public void setErrorMsg_3(String errorMsg_3) {
		ErrorMsg_3 = errorMsg_3;
	}
	public String getShort_1() {
		return Short_1;
	}
	public void setShort_1(String short_1) {
		Short_1 = short_1;
	}
	public String getFeeRate_1() {
		return FeeRate_1;
	}
	public void setFeeRate_1(String feeRate_1) {
		FeeRate_1 = feeRate_1;
	}
	public String getGroupOfa_1() {
		return GroupOfa_1;
	}
	public void setGroupOfa_1(String groupOfa_1) {
		GroupOfa_1 = groupOfa_1;
	}
	public String getRedeemAmt_1() {
		return RedeemAmt_1;
	}
	public void setRedeemAmt_1(String redeemAmt_1) {
		RedeemAmt_1 = redeemAmt_1;
	}
	public String getShort_2() {
		return Short_2;
	}
	public void setShort_2(String short_2) {
		Short_2 = short_2;
	}
	public String getFeeRate_2() {
		return FeeRate_2;
	}
	public void setFeeRate_2(String feeRate_2) {
		FeeRate_2 = feeRate_2;
	}
	public String getGroupOfa_2() {
		return GroupOfa_2;
	}
	public void setGroupOfa_2(String groupOfa_2) {
		GroupOfa_2 = groupOfa_2;
	}
	public String getRedeemAmt_2() {
		return RedeemAmt_2;
	}
	public void setRedeemAmt_2(String redeemAmt_2) {
		RedeemAmt_2 = redeemAmt_2;
	}
	public String getShort_3() {
		return Short_3;
	}
	public void setShort_3(String short_3) {
		Short_3 = short_3;
	}
	public String getFeeRate_3() {
		return FeeRate_3;
	}
	public void setFeeRate_3(String feeRate_3) {
		FeeRate_3 = feeRate_3;
	}
	public String getGroupOfa_3() {
		return GroupOfa_3;
	}
	public void setGroupOfa_3(String groupOfa_3) {
		GroupOfa_3 = groupOfa_3;
	}
	public String getRedeemAmt_3() {
		return RedeemAmt_3;
	}
	public void setRedeemAmt_3(String redeemAmt_3) {
		RedeemAmt_3 = redeemAmt_3;
	}
    
}
