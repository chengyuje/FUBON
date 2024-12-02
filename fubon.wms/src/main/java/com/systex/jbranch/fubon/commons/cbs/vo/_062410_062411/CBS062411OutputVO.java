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
	private String ReportEngAdd1;
	private String ReportEngAdd2;
	private String ReportEngAdd3;
	private String ReportEngAdd4;
	private String ReportCountry;
	private String TaxCountry1;
	private String TaxCode1;
	private String NoTaxCodeReason1;
	private String SeqNumber1;
	private String TaxCountry2;
	private String TaxCode2;
	private String NoTaxCodeReason2;
	private String SeqNumber2;
	private String TaxCountry3;
	private String TaxCode3;
	private String NoTaxCodeReason3;
	private String SeqNumber3;
	private String TaxCountry4;
	private String TaxCode4;
	private String NoTaxCodeReason4;
	private String SeqNumber4;
	private String TaxCountry5;
	private String TaxCode5;
	private String NoTaxCodeReason5;
	private String SeqNumber5;
	private String BirthCountry;
	private String BirthCity;
	private String AnnotationFlag;

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

	public String getReportEngAdd1() {
		return ReportEngAdd1;
	}

	public void setReportEngAdd1(String reportEngAdd1) {
		ReportEngAdd1 = reportEngAdd1;
	}

	public String getReportEngAdd2() {
		return ReportEngAdd2;
	}

	public void setReportEngAdd2(String reportEngAdd2) {
		ReportEngAdd2 = reportEngAdd2;
	}

	public String getReportEngAdd3() {
		return ReportEngAdd3;
	}

	public void setReportEngAdd3(String reportEngAdd3) {
		ReportEngAdd3 = reportEngAdd3;
	}

	public String getReportEngAdd4() {
		return ReportEngAdd4;
	}

	public void setReportEngAdd4(String reportEngAdd4) {
		ReportEngAdd4 = reportEngAdd4;
	}

	public String getReportCountry() {
		return ReportCountry;
	}

	public void setReportCountry(String reportCountry) {
		ReportCountry = reportCountry;
	}

	public String getTaxCountry1() {
		return TaxCountry1;
	}

	public void setTaxCountry1(String taxCountry1) {
		TaxCountry1 = taxCountry1;
	}

	public String getTaxCode1() {
		return TaxCode1;
	}

	public void setTaxCode1(String taxCode1) {
		TaxCode1 = taxCode1;
	}

	public String getNoTaxCodeReason1() {
		return NoTaxCodeReason1;
	}

	public void setNoTaxCodeReason1(String noTaxCodeReason1) {
		NoTaxCodeReason1 = noTaxCodeReason1;
	}

	public String getSeqNumber1() {
		return SeqNumber1;
	}

	public void setSeqNumber1(String seqNumber1) {
		SeqNumber1 = seqNumber1;
	}

	public String getTaxCountry2() {
		return TaxCountry2;
	}

	public void setTaxCountry2(String taxCountry2) {
		TaxCountry2 = taxCountry2;
	}

	public String getTaxCode2() {
		return TaxCode2;
	}

	public void setTaxCode2(String taxCode2) {
		TaxCode2 = taxCode2;
	}

	public String getNoTaxCodeReason2() {
		return NoTaxCodeReason2;
	}

	public void setNoTaxCodeReason2(String noTaxCodeReason2) {
		NoTaxCodeReason2 = noTaxCodeReason2;
	}

	public String getSeqNumber2() {
		return SeqNumber2;
	}

	public void setSeqNumber2(String seqNumber2) {
		SeqNumber2 = seqNumber2;
	}

	public String getTaxCountry3() {
		return TaxCountry3;
	}

	public void setTaxCountry3(String taxCountry3) {
		TaxCountry3 = taxCountry3;
	}

	public String getTaxCode3() {
		return TaxCode3;
	}

	public void setTaxCode3(String taxCode3) {
		TaxCode3 = taxCode3;
	}

	public String getNoTaxCodeReason3() {
		return NoTaxCodeReason3;
	}

	public void setNoTaxCodeReason3(String noTaxCodeReason3) {
		NoTaxCodeReason3 = noTaxCodeReason3;
	}

	public String getSeqNumber3() {
		return SeqNumber3;
	}

	public void setSeqNumber3(String seqNumber3) {
		SeqNumber3 = seqNumber3;
	}

	public String getTaxCountry4() {
		return TaxCountry4;
	}

	public void setTaxCountry4(String taxCountry4) {
		TaxCountry4 = taxCountry4;
	}

	public String getTaxCode4() {
		return TaxCode4;
	}

	public void setTaxCode4(String taxCode4) {
		TaxCode4 = taxCode4;
	}

	public String getNoTaxCodeReason4() {
		return NoTaxCodeReason4;
	}

	public void setNoTaxCodeReason4(String noTaxCodeReason4) {
		NoTaxCodeReason4 = noTaxCodeReason4;
	}

	public String getSeqNumber4() {
		return SeqNumber4;
	}

	public void setSeqNumber4(String seqNumber4) {
		SeqNumber4 = seqNumber4;
	}

	public String getTaxCountry5() {
		return TaxCountry5;
	}

	public void setTaxCountry5(String taxCountry5) {
		TaxCountry5 = taxCountry5;
	}

	public String getTaxCode5() {
		return TaxCode5;
	}

	public void setTaxCode5(String taxCode5) {
		TaxCode5 = taxCode5;
	}

	public String getNoTaxCodeReason5() {
		return NoTaxCodeReason5;
	}

	public void setNoTaxCodeReason5(String noTaxCodeReason5) {
		NoTaxCodeReason5 = noTaxCodeReason5;
	}

	public String getSeqNumber5() {
		return SeqNumber5;
	}

	public void setSeqNumber5(String seqNumber5) {
		SeqNumber5 = seqNumber5;
	}

	public String getBirthCountry() {
		return BirthCountry;
	}

	public void setBirthCountry(String birthCountry) {
		BirthCountry = birthCountry;
	}

	public String getBirthCity() {
		return BirthCity;
	}

	public void setBirthCity(String birthCity) {
		BirthCity = birthCity;
	}

	public String getAnnotationFlag() {
		return AnnotationFlag;
	}

	public void setAnnotationFlag(String annotationFlag) {
		AnnotationFlag = annotationFlag;
	}

}
