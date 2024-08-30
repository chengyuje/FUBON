package com.systex.jbranch.app.server.fps.cmsub302;

import java.util.Date;
import java.util.List;

public class PrintInsExaminationReportInputVO {

	private String insCustId;
	private String insCTCBCust;
	private String insCustName;
	private Date insCustBirthday;
	private String insCustGender;
	private String insCustMarriageStatus;
	private String printType;
	private List lstFamily;
	private List lstException;
	
	private Boolean viewSum = false;
	private Boolean viewSumAll = false;
	private Boolean viewStructure = false;
	private Boolean insPaySum = false;
	private Boolean insPayDetail = false;
	private Boolean indYearSum = false;
	private Boolean familyYear = false;
	private Boolean familyFeeGap = false;
	private Boolean familyBalance = false;
	private Boolean familyFinSecData = false;
	private Boolean policyDetail = false;
	
	//add @xiaoxichen 20120301
	private Boolean returnYear = false;//是否列印逐年還本金額一覽表
	private Boolean insFeeYear = false;//是否列印逐年保費一覽表
	private Boolean wholeLifeYear = false;//是否列印逐年壽險保障一覽表
	private Boolean accidentYear = false;//是否列印逐年意外保障一覽表
	private Boolean dreadSickYear = false;//是否列印逐年重大疾病保障一覽表
	private Boolean cancerYear = false;//是否列印逐年癌症保障一覽表
	// 2012/08/16,Carey,add,BEGIN
	private String custName;// 2012/08/16,Carey,add
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	// 2012/08/16,Carey,END
	public String getPrintType() {
		return printType;
	}
	public void setPrintType(String printType) {
		this.printType = printType;
	}
	public List getLstFamily() {
		return lstFamily;
	}
	public void setLstFamily(List lstFamily) {
		this.lstFamily = lstFamily;
	}
	public Boolean getReturnYear() {
		return returnYear;
	}
	public void setReturnYear(Boolean returnYear) {
		this.returnYear = returnYear;
	}
	public Boolean getInsFeeYear() {
		return insFeeYear;
	}
	public void setInsFeeYear(Boolean insFeeYear) {
		this.insFeeYear = insFeeYear;
	}
	public Boolean getWholeLifeYear() {
		return wholeLifeYear;
	}
	public void setWholeLifeYear(Boolean wholeLifeYear) {
		this.wholeLifeYear = wholeLifeYear;
	}
	public Boolean getAccidentYear() {
		return accidentYear;
	}
	public void setAccidentYear(Boolean accidentYear) {
		this.accidentYear = accidentYear;
	}
	public Boolean getDreadSickYear() {
		return dreadSickYear;
	}
	public void setDreadSickYear(Boolean dreadSickYear) {
		this.dreadSickYear = dreadSickYear;
	}
	public Boolean getCancerYear() {
		return cancerYear;
	}
	public void setCancerYear(Boolean cancerYear) {
		this.cancerYear = cancerYear;
	}
	public String getInsCTCBCust() {
		return insCTCBCust;
	}
	public void setInsCTCBCust(String insCTCBCust) {
		this.insCTCBCust = insCTCBCust;
	}
	public String getInsCustName() {
		return insCustName;
	}
	public void setInsCustName(String insCustName) {
		this.insCustName = insCustName;
	}
	public Date getInsCustBirthday() {
		return insCustBirthday;
	}
	public void setInsCustBirthday(Date insCustBirthday) {
		this.insCustBirthday = insCustBirthday;
	}
	public String getInsCustGender() {
		return insCustGender;
	}
	public void setInsCustGender(String insCustGender) {
		this.insCustGender = insCustGender;
	}
	public String getInsCustMarriageStatus() {
		return insCustMarriageStatus;
	}
	public void setInsCustMarriageStatus(String insCustMarriageStatus) {
		this.insCustMarriageStatus = insCustMarriageStatus;
	}
	public Boolean getPolicyDetail() {
		return policyDetail;
	}
	public void setPolicyDetail(Boolean policyDetail) {
		this.policyDetail = policyDetail;
	}
	public Boolean getViewSumAll() {
		return viewSumAll;
	}
	public void setViewSumAll(Boolean viewSumAll) {
		this.viewSumAll = viewSumAll;
	}
	public String getInsCustId() {
		return insCustId;
	}
	public void setInsCustId(String insCustId) {
		this.insCustId = insCustId;
	}
	public List getLstException() {
		return lstException;
	}
	public void setLstException(List lstException) {
		this.lstException = lstException;
	}
	public Boolean getViewSum() {
		return viewSum;
	}
	public void setViewSum(Boolean viewSum) {
		this.viewSum = viewSum;
	}
	public Boolean getViewStructure() {
		return viewStructure;
	}
	public void setViewStructure(Boolean viewStructure) {
		this.viewStructure = viewStructure;
	}
	public Boolean getInsPaySum() {
		return insPaySum;
	}
	public void setInsPaySum(Boolean insPaySum) {
		this.insPaySum = insPaySum;
	}
	public Boolean getInsPayDetail() {
		return insPayDetail;
	}
	public void setInsPayDetail(Boolean insPayDetail) {
		this.insPayDetail = insPayDetail;
	}
	public Boolean getIndYearSum() {
		return indYearSum;
	}
	public void setIndYearSum(Boolean indYearSum) {
		this.indYearSum = indYearSum;
	}
	public Boolean getFamilyYear() {
		return familyYear;
	}
	public void setFamilyYear(Boolean familyYear) {
		this.familyYear = familyYear;
	}
	public Boolean getFamilyFeeGap() {
		return familyFeeGap;
	}
	public void setFamilyFeeGap(Boolean familyFeeGap) {
		this.familyFeeGap = familyFeeGap;
	}
	public Boolean getFamilyBalance() {
		return familyBalance;
	}
	public void setFamilyBalance(Boolean familyBalance) {
		this.familyBalance = familyBalance;
	}
	public Boolean getFamilyFinSecData() {
		return familyFinSecData;
	}
	public void setFamilyFinSecData(Boolean familyFinSecData) {
		this.familyFinSecData = familyFinSecData;
	}
}
