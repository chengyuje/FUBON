package com.systex.jbranch.fubon.commons.esb.vo.vn067n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * 網路快速下單 - 基金申購檢核
       發送電文VN067N
 * Created by Valentino on 2017/03/23.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class VN067NOutputVO {
	@XmlElement
	private String EMSGID;
	@XmlElement
	private String EMSGTXT; 
	
	@XmlElement
	private String SPRefId;
	@XmlElement
	private String AcctId16;
	@XmlElement
	private String Name;
	@XmlElement
	private String SEX;
	@XmlElement
	private String FundName;
	@XmlElement
	private String EviNum;
	@XmlElement
	private String CurCode;
	@XmlElement
	private String Sign;
	@XmlElement
	private String AcctBal;
	@XmlElement
	private String Ctrl_Code;
	@XmlElement
	private String BeneCode;
	@XmlElement
	private String AvailBalSign;
	@XmlElement
	private String AvailBal;
	
	@XmlElement
	private String FeeRate;
	@XmlElement
	private String Fee;
	@XmlElement
	private String Fee_M;
	@XmlElement
	private String FeeRate_M;
	@XmlElement
	private String FeeRate_H;
	@XmlElement
	private String Fee_H;
	
	
	public String getEMSGID() {
		return EMSGID;
	}
	public void setEMSGID(String eMSGID) {
		EMSGID = eMSGID;
	}
	public String getEMSGTXT() {
		return EMSGTXT;
	}
	public void setEMSGTXT(String eMSGTXT) {
		EMSGTXT = eMSGTXT;
	}
	 
	public String getFeeRate() {
		return FeeRate;
	}
	public void setFeeRate(String feeRate) {
		FeeRate = feeRate;
	}
	public String getFee() {
		return Fee;
	}
	public void setFee(String fee) {
		Fee = fee;
	}
	public String getFee_M() {
		return Fee_M;
	}
	public void setFee_M(String fee_M) {
		Fee_M = fee_M;
	}
	public String getFeeRate_M() {
		return FeeRate_M;
	}
	public void setFeeRate_M(String feeRate_M) {
		FeeRate_M = feeRate_M;
	}
	public String getFeeRate_H() {
		return FeeRate_H;
	}
	public void setFeeRate_H(String feeRate_H) {
		FeeRate_H = feeRate_H;
	}
	public String getFee_H() {
		return Fee_H;
	}
	public void setFee_H(String fee_H) {
		Fee_H = fee_H;
	}
	public String getSPRefId() {
		return SPRefId;
	}
	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
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
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	public String getFundName() {
		return FundName;
	}
	public void setFundName(String fundName) {
		FundName = fundName;
	}
	public String getEviNum() {
		return EviNum;
	}
	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}
	public String getCurCode() {
		return CurCode;
	}
	public void setCurCode(String curCode) {
		CurCode = curCode;
	}
	public String getSign() {
		return Sign;
	}
	public void setSign(String sign) {
		Sign = sign;
	}
	public String getAcctBal() {
		return AcctBal;
	}
	public void setAcctBal(String acctBal) {
		AcctBal = acctBal;
	}
	public String getCtrl_Code() {
		return Ctrl_Code;
	}
	public void setCtrl_Code(String ctrl_Code) {
		Ctrl_Code = ctrl_Code;
	}
	public String getBeneCode() {
		return BeneCode;
	}
	public void setBeneCode(String beneCode) {
		BeneCode = beneCode;
	}
	public String getAvailBalSign() {
		return AvailBalSign;
	}
	public void setAvailBalSign(String availBalSign) {
		AvailBalSign = availBalSign;
	}
	public String getAvailBal() {
		return AvailBal;
	}
	public void setAvailBal(String availBal) {
		AvailBal = availBal;
	}
	
	
	
}
