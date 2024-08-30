package com.systex.jbranch.app.server.fps.appvo.vo;

public class InfoVO {

	private String custName;//客戶姓名
	private String custNum;//客戶編號
	
	private String birthDate;//生日
	private String telOffice;//聯絡電話(公司)
	private String telHome;//聯絡電話(住家)
	private String telMobile;//行動電話
	private String regiAddr;//戶籍地址
	private String permAddr;//通訊地址
	private String openDate;//開戶日期
	private String billWay;//對帳單寄送方式
	private String aoCode;//理專員編
	private String aoName;//理專姓名
	private String riskAttr;//風險屬性
	private String kycDate;//問卷填寫日
	private String vipCodeI;//客戶屬性
	private String custType;//客戶類型
	private String acFlag;
	private String acFlagTime;
	private String pcFlag;
	private String pcFlagTime;
	private String education;//學歷
	private String sickFlag;//重大傷病註記
	private String creditCust;//是用為信用卡戶
	private String creditBillWay;//信用卡對帳單寄送方式
	private String netBankCust;//申請網路銀行
	private String voiceCust;//申請語音
	private String agreeInsSale;//同意保險共用行銷
	private String agreePhoneSale;//同意電話行銷
	private String branchNbr;	 //分行代碼 2013/11/07 tds 變更
	
	private String isSalarycust;	//是否為薪轉戶
	private String visaDebit;	 //申請Visa Debit卡

	public String getIsSalarycust() {
		return isSalarycust;
	}
	public void setIsSalarycust(String isSalarycust) {
		this.isSalarycust = isSalarycust;
	}
	public String getVisaDebit() {
		return visaDebit;
	}
	public void setVisaDebit(String visaDebit) {
		this.visaDebit = visaDebit;
	}
	//暫時保留
	private String custNo;
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustNum() {
		return custNum;
	}
	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getTelOffice() {
		return telOffice;
	}
	public void setTelOffice(String telOffice) {
		this.telOffice = telOffice;
	}
	public String getTelHome() {
		return telHome;
	}
	public void setTelHome(String telHome) {
		this.telHome = telHome;
	}
	public String getTelMobile() {
		return telMobile;
	}
	public void setTelMobile(String telMobile) {
		this.telMobile = telMobile;
	}
	public String getRegiAddr() {
		return regiAddr;
	}
	public void setRegiAddr(String regiAddr) {
		this.regiAddr = regiAddr;
	}
	public String getPermAddr() {
		return permAddr;
	}
	public void setPermAddr(String permAddr) {
		this.permAddr = permAddr;
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public String getBillWay() {
		return billWay;
	}
	public void setBillWay(String billWay) {
		this.billWay = billWay;
	}
	public String getAoCode() {
		return aoCode;
	}
	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}
	public String getAoName() {
		return aoName;
	}
	public void setAoName(String aoName) {
		this.aoName = aoName;
	}
	public String getRiskAttr() {
		return riskAttr;
	}
	public void setRiskAttr(String riskAttr) {
		this.riskAttr = riskAttr;
	}
	public String getKycDate() {
		return kycDate;
	}
	public void setKycDate(String kycDate) {
		this.kycDate = kycDate;
	}
	public String getVipCodeI() {
		return vipCodeI;
	}
	public void setVipCodeI(String vipCodeI) {
		this.vipCodeI = vipCodeI;
	}
	public String getCustType() {
		return custType;
	}
	public void setCustType(String custType) {
		this.custType = custType;
	}
	public String getAcFlag() {
		return acFlag;
	}
	public void setAcFlag(String acFlag) {
		this.acFlag = acFlag;
	}
	public String getAcFlagTime() {
		return acFlagTime;
	}
	public void setAcFlagTime(String acFlagTime) {
		this.acFlagTime = acFlagTime;
	}
	public String getPcFlag() {
		return pcFlag;
	}
	public void setPcFlag(String pcFlag) {
		this.pcFlag = pcFlag;
	}
	public String getPcFlagTime() {
		return pcFlagTime;
	}
	public void setPcFlagTime(String pcFlagTime) {
		this.pcFlagTime = pcFlagTime;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getSickFlag() {
		return sickFlag;
	}
	public void setSickFlag(String sickFlag) {
		this.sickFlag = sickFlag;
	}
	public String getCreditCust() {
		return creditCust;
	}
	public void setCreditCust(String creditCust) {
		this.creditCust = creditCust;
	}
	public String getCreditBillWay() {
		return creditBillWay;
	}
	public void setCreditBillWay(String creditBillWay) {
		this.creditBillWay = creditBillWay;
	}
	public String getNetBankCust() {
		return netBankCust;
	}
	public void setNetBankCust(String netBankCust) {
		this.netBankCust = netBankCust;
	}
	public String getVoiceCust() {
		return voiceCust;
	}
	public void setVoiceCust(String voiceCust) {
		this.voiceCust = voiceCust;
	}
	public String getAgreeInsSale() {
		return agreeInsSale;
	}
	public void setAgreeInsSale(String agreeInsSale) {
		this.agreeInsSale = agreeInsSale;
	}
	public String getAgreePhoneSale() {
		return agreePhoneSale;
	}
	public void setAgreePhoneSale(String agreePhoneSale) {
		this.agreePhoneSale = agreePhoneSale;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	
}
