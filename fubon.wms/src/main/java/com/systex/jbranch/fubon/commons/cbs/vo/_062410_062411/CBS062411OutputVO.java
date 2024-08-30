package com.systex.jbranch.fubon.commons.cbs.vo._062410_062411;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS062411OutputVO {
	@XmlElement(name = "TxRepeat")
	private List<CBS062411OutputDetailsVO> details;
	private String CustNo;
	private String IdType;
	private String IdNo;
	private String Function;
	private String CustName;
	private String CustNameEng;
	private String EngAddress;
	private String Country;
	private String AgreementSignFlag;
	private String SignDate;
	private String USIndex1;
	private String USIndex2;
	private String USIndex3;
	private String USIndex4;
	private String USIndex5;
	private String USIndex6;
	private String USIndex7;
	private String USIndex8;
	private String IdentityNo;
	private String CurrIdentityDesc;
	private String PrevIdentityNo;
	private String LastYrIdentityNo;
	private String USTaxCode;
	private String SetUpDate;
	private String DocCollected;
	private String MaintBranch;
	private String SrchDtElectronic;
	private String SrchDtManual;
	private String MandIdentificatnDt;

	public CBS062411OutputVO() {
		this.CustNo = "";
		this.IdType = "";
		this.IdNo = "";
		this.Function = "";
		this.CustName = "";
		this.CustNameEng = "";
		this.EngAddress = "";
		this.Country = "";
		this.AgreementSignFlag = "";
		this.SignDate = "";
		this.USIndex1 = "";
		this.USIndex2 = "";
		this.USIndex3 = "";
		this.USIndex4 = "";
		this.USIndex5 = "";
		this.USIndex6 = "";
		this.USIndex7 = "";
		this.USIndex8 = "";
		this.IdentityNo = "";
		this.CurrIdentityDesc = "";
		this.PrevIdentityNo = "";
		this.LastYrIdentityNo = "";
		this.USTaxCode = "";
		this.SetUpDate = "";
		this.DocCollected = "";
		this.MaintBranch = "";
		this.SrchDtElectronic = "";
		this.SrchDtManual = "";
		this.MandIdentificatnDt = "";

	}

	public List<CBS062411OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<CBS062411OutputDetailsVO> details) {
		this.details = details;
	}

	public String getCustNo() {
		return CustNo;
	}

	public void setCustNo(String custNo) {
		CustNo = custNo;
	}

	public String getIdType() {
		return IdType;
	}

	public void setIdType(String idType) {
		IdType = idType;
	}

	public String getIdNo() {
		return IdNo;
	}

	public void setIdNo(String idNo) {
		IdNo = idNo;
	}

	public String getFunction() {
		return Function;
	}

	public void setFunction(String function) {
		Function = function;
	}

	public String getCustName() {
		return CustName;
	}

	public void setCustName(String custName) {
		CustName = custName;
	}

	public String getCustNameEng() {
		return CustNameEng;
	}

	public void setCustNameEng(String custNameEng) {
		CustNameEng = custNameEng;
	}

	public String getEngAddress() {
		return EngAddress;
	}

	public void setEngAddress(String engAddress) {
		EngAddress = engAddress;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public String getAgreementSignFlag() {
		return AgreementSignFlag;
	}

	public void setAgreementSignFlag(String agreementSignFlag) {
		AgreementSignFlag = agreementSignFlag;
	}

	public String getSignDate() {
		return SignDate;
	}

	public void setSignDate(String signDate) {
		SignDate = signDate;
	}

	public String getUSIndex1() {
		return USIndex1;
	}

	public void setUSIndex1(String uSIndex1) {
		USIndex1 = uSIndex1;
	}

	public String getUSIndex2() {
		return USIndex2;
	}

	public void setUSIndex2(String uSIndex2) {
		USIndex2 = uSIndex2;
	}

	public String getUSIndex3() {
		return USIndex3;
	}

	public void setUSIndex3(String uSIndex3) {
		USIndex3 = uSIndex3;
	}

	public String getUSIndex4() {
		return USIndex4;
	}

	public void setUSIndex4(String uSIndex4) {
		USIndex4 = uSIndex4;
	}

	public String getUSIndex5() {
		return USIndex5;
	}

	public void setUSIndex5(String uSIndex5) {
		USIndex5 = uSIndex5;
	}

	public String getUSIndex6() {
		return USIndex6;
	}

	public void setUSIndex6(String uSIndex6) {
		USIndex6 = uSIndex6;
	}

	public String getUSIndex7() {
		return USIndex7;
	}

	public void setUSIndex7(String uSIndex7) {
		USIndex7 = uSIndex7;
	}

	public String getUSIndex8() {
		return USIndex8;
	}

	public void setUSIndex8(String uSIndex8) {
		USIndex8 = uSIndex8;
	}

	public String getIdentityNo() {
		return IdentityNo;
	}

	public void setIdentityNo(String identityNo) {
		IdentityNo = identityNo;
	}

	public String getCurrIdentityDesc() {
		return CurrIdentityDesc;
	}

	public void setCurrIdentityDesc(String currIdentityDesc) {
		CurrIdentityDesc = currIdentityDesc;
	}

	public String getPrevIdentityNo() {
		return PrevIdentityNo;
	}

	public void setPrevIdentityNo(String prevIdentityNo) {
		PrevIdentityNo = prevIdentityNo;
	}

	public String getLastYrIdentityNo() {
		return LastYrIdentityNo;
	}

	public void setLastYrIdentityNo(String lastYrIdentityNo) {
		LastYrIdentityNo = lastYrIdentityNo;
	}

	public String getUSTaxCode() {
		return USTaxCode;
	}

	public void setUSTaxCode(String uSTaxCode) {
		USTaxCode = uSTaxCode;
	}

	public String getSetUpDate() {
		return SetUpDate;
	}

	public void setSetUpDate(String setUpDate) {
		SetUpDate = setUpDate;
	}

	public String getDocCollected() {
		return DocCollected;
	}

	public void setDocCollected(String docCollected) {
		DocCollected = docCollected;
	}

	public String getMaintBranch() {
		return MaintBranch;
	}

	public void setMaintBranch(String maintBranch) {
		MaintBranch = maintBranch;
	}

	public String getSrchDtElectronic() {
		return SrchDtElectronic;
	}

	public void setSrchDtElectronic(String srchDtElectronic) {
		SrchDtElectronic = srchDtElectronic;
	}

	public String getSrchDtManual() {
		return SrchDtManual;
	}

	public void setSrchDtManual(String srchDtManual) {
		SrchDtManual = srchDtManual;
	}

	public String getMandIdentificatnDt() {
		return MandIdentificatnDt;
	}

	public void setMandIdentificatnDt(String mandIdentificatnDt) {
		MandIdentificatnDt = mandIdentificatnDt;
	}

}
