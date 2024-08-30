package com.systex.jbranch.fubon.commons.cbs.vo._067050_067115;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS067101OutputVO {
    private String DefInteger1; //客戶號
    private String DefaultString1; //名稱
    private String date1; //生日
    private String DefaultString2; //性別 M男/ F女
    private String DefaultString3; //婚姻狀況
    private String DefInteger2; //家屬
    private String DefaultString4; //居住國家號碼
    private String DefaultString5; //雇主
    private String date2; //雇用日期
    private String DefaultString6; //雇主地址
    private String DefaultString7; //職業描述
	@XmlElement(name = "ocpn-code1")
    private String ocpn_code1; //職稱
    private String DefaultString8; //出生地
    private String DefaultString9; //雇主地址2
    private String DefaultString10; //郵遞區號
    private String FILLER; //FILLER
    private String DefaultString22; //DefaultString22
    private String IdType2; //監護人證件類型/號碼
    private String DefaultString12; //監護人證件類型/號碼
    private String DefaultString13; //教育程度
    private String housestat1; //居民狀態
    private String occupancy1; //房屋主人
    private String Signed01; //年收入
    private String DefaultString18; //聯絡1詳細資料 名稱
    private String DefaultString15; //聯絡1詳細資料 關係
    private String DefaultString16; //電話號碼
    private String DefaultString17; //DefaultString17
    private String DefaultString14; //名稱
    private String DefaultString19; //關係
    private String DefaultString20; //電話號碼
    private String DefaultString21; //DefaultString21
    private String LocalLivingStatus; //本地生活水平
    private String OtherLivingRemark; //其他生活備註
    private String OccupationCode; //職業代碼
    private String ResidencePhone2; //住宅電話2
    private String ResidenceExtension2; //住宅電話2
    private String BusinessPhone2; //商務電話
    private String BusinessExtension2; //商務電話
    private String FaxPhone2; //傳真號碼2
    private String MobilePhone2; //手機號碼2
    private String IdType3; //監護人證件類型/號碼2
    private String IdNo3; //監護人證件類型/號碼2
    private String BloodType; //血型
    private String PassportCode; //護照代碼
    private String LstloanCode; //最後貸款碼
    private String OnJobDate; //到職日
    private String YearsWorking; //工作年資
    private String JobStatus; //工作狀態
    private String RecrmtType; //聘雇類型
    private String EmployerIDType; //雇主證件類型
    private String EmplyrIDNo; //雇主證件號碼
    private String CongIDType; //企業證件類型
    private String CongIDNo; //企業證件編號
    private String EmpIND; //雇主標幟
    private String Employer; //雇主
    private String DeptIND; //部門標幟
    private String Department; //部門
    private String PrevEmpIND; //上一個雇主指標
    private String PrevEmp; //上一個雇主
    private String DefaultString11; //證件類型/ 號碼
    private String IdType1; //證件類型/ 號碼
    private String children; //子女人數
    private String rep1name; //法代1姓名
    private String rep1DOB; //法代1出生日期
    private String rep1IdType; //法代1換補領代號
    private String rep1Date; //法代1換補領日期
    private String rep1Photo; //法代1有無照片
    private String rep1Place; //法代1發照地點
    private String rep2name; //法代2姓名
    private String rep2DOB; //法代2出生日期
    private String rep2IdType; //法代2換補領代號
    private String rep2Date; //法代2換補領日期
    private String rep2Photo; //法代2有無照片
    private String rep2Place; //法代2發照地點
    private String incomedt; //年收入資料日期
    private String GuardianShip; //受輔助/受監護宣告
    private String GUA_ASS_NAME; //輔助/監護人姓名
    private String emp_code; //員工註記
    private String FILLER2; //FILLER2
	@XmlElement(name = "CUSM-EX-FLAG-1")
    private String CUSM_EX_FLAG_1; //EX-FLAG-1
	@XmlElement(name = "CUSM-EX-FLAG-2")
	private String CUSM_EX_FLAG_2; //EX-FLAG-2
	@XmlElement(name = "CUSM-EX-FLAG-3;")
	private String CUSM_EX_FLAG_3; //EX-FLAG-3
	@XmlElement(name = "CUSM-EX-FLAG-4")
	private String CUSM_EX_FLAG_4; //EX-FLAG-4
	@XmlElement(name = "CUSM-EX-FLAG-5")
	private String CUSM_EX_FLAG_5; //EX-FLAG-5
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-1")
	private String CUSM_EX_ATTRIBUTE_1; //EX-ATTRIBUTE-1
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-2")
	private String CUSM_EX_ATTRIBUTE_2; //EX-ATTRIBUTE-2
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-3")
	private String CUSM_EX_ATTRIBUTE_3; //EX-ATTRIBUTE-3
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-4")
	private String CUSM_EX_ATTRIBUTE_4; //EX-ATTRIBUTE-4
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-5")
	private String CUSM_EX_ATTRIBUTE_5; //EX-ATTRIBUTE-5
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-6")
	private String CUSM_EX_ATTRIBUTE_6; //EX-ATTRIBUTE-6
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-7")
	private String CUSM_EX_ATTRIBUTE_7; //EX-ATTRIBUTE-7
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-8")
	private String CUSM_EX_ATTRIBUTE_8; //EX-ATTRIBUTE-8
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-9")
	private String CUSM_EX_ATTRIBUTE_9; //EX-ATTRIBUTE-9
	@XmlElement(name = "CUSM-EX-ATTRIBUTE-10")
	private String CUSM_EX_ATTRIBUTE_10; //EX-ATTRIBUTE-10

	public String getDefInteger1() {
		return DefInteger1;
	}

	public void setDefInteger1(String defInteger1) {
		DefInteger1 = defInteger1;
	}

	public String getDefaultString1() {
		return DefaultString1;
	}

	public void setDefaultString1(String defaultString1) {
		DefaultString1 = defaultString1;
	}

	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	public String getDefaultString2() {
		return DefaultString2;
	}

	public void setDefaultString2(String defaultString2) {
		DefaultString2 = defaultString2;
	}

	public String getDefaultString3() {
		return DefaultString3;
	}

	public void setDefaultString3(String defaultString3) {
		DefaultString3 = defaultString3;
	}

	public String getDefInteger2() {
		return DefInteger2;
	}

	public void setDefInteger2(String defInteger2) {
		DefInteger2 = defInteger2;
	}

	public String getDefaultString4() {
		return DefaultString4;
	}

	public void setDefaultString4(String defaultString4) {
		DefaultString4 = defaultString4;
	}

	public String getDefaultString5() {
		return DefaultString5;
	}

	public void setDefaultString5(String defaultString5) {
		DefaultString5 = defaultString5;
	}

	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	public String getDefaultString6() {
		return DefaultString6;
	}

	public void setDefaultString6(String defaultString6) {
		DefaultString6 = defaultString6;
	}

	public String getDefaultString7() {
		return DefaultString7;
	}

	public void setDefaultString7(String defaultString7) {
		DefaultString7 = defaultString7;
	}

	public String getOcpn_code1() {
		return ocpn_code1;
	}

	public void setOcpn_code1(String ocpn_code1) {
		this.ocpn_code1 = ocpn_code1;
	}

	public String getDefaultString8() {
		return DefaultString8;
	}

	public void setDefaultString8(String defaultString8) {
		DefaultString8 = defaultString8;
	}

	public String getDefaultString9() {
		return DefaultString9;
	}

	public void setDefaultString9(String defaultString9) {
		DefaultString9 = defaultString9;
	}

	public String getDefaultString10() {
		return DefaultString10;
	}

	public void setDefaultString10(String defaultString10) {
		DefaultString10 = defaultString10;
	}

	public String getFILLER() {
		return FILLER;
	}

	public void setFILLER(String FILLER) {
		this.FILLER = FILLER;
	}

	public String getDefaultString22() {
		return DefaultString22;
	}

	public void setDefaultString22(String defaultString22) {
		DefaultString22 = defaultString22;
	}

	public String getIdType2() {
		return IdType2;
	}

	public void setIdType2(String idType2) {
		IdType2 = idType2;
	}

	public String getDefaultString12() {
		return DefaultString12;
	}

	public void setDefaultString12(String defaultString12) {
		DefaultString12 = defaultString12;
	}

	public String getDefaultString13() {
		return DefaultString13;
	}

	public void setDefaultString13(String defaultString13) {
		DefaultString13 = defaultString13;
	}

	public String getHousestat1() {
		return housestat1;
	}

	public void setHousestat1(String housestat1) {
		this.housestat1 = housestat1;
	}

	public String getOccupancy1() {
		return occupancy1;
	}

	public void setOccupancy1(String occupancy1) {
		this.occupancy1 = occupancy1;
	}

	public String getSigned01() {
		return Signed01;
	}

	public void setSigned01(String signed01) {
		Signed01 = signed01;
	}

	public String getDefaultString18() {
		return DefaultString18;
	}

	public void setDefaultString18(String defaultString18) {
		DefaultString18 = defaultString18;
	}

	public String getDefaultString15() {
		return DefaultString15;
	}

	public void setDefaultString15(String defaultString15) {
		DefaultString15 = defaultString15;
	}

	public String getDefaultString16() {
		return DefaultString16;
	}

	public void setDefaultString16(String defaultString16) {
		DefaultString16 = defaultString16;
	}

	public String getDefaultString17() {
		return DefaultString17;
	}

	public void setDefaultString17(String defaultString17) {
		DefaultString17 = defaultString17;
	}

	public String getDefaultString14() {
		return DefaultString14;
	}

	public void setDefaultString14(String defaultString14) {
		DefaultString14 = defaultString14;
	}

	public String getDefaultString19() {
		return DefaultString19;
	}

	public void setDefaultString19(String defaultString19) {
		DefaultString19 = defaultString19;
	}

	public String getDefaultString20() {
		return DefaultString20;
	}

	public void setDefaultString20(String defaultString20) {
		DefaultString20 = defaultString20;
	}

	public String getDefaultString21() {
		return DefaultString21;
	}

	public void setDefaultString21(String defaultString21) {
		DefaultString21 = defaultString21;
	}

	public String getLocalLivingStatus() {
		return LocalLivingStatus;
	}

	public void setLocalLivingStatus(String localLivingStatus) {
		LocalLivingStatus = localLivingStatus;
	}

	public String getOtherLivingRemark() {
		return OtherLivingRemark;
	}

	public void setOtherLivingRemark(String otherLivingRemark) {
		OtherLivingRemark = otherLivingRemark;
	}

	public String getOccupationCode() {
		return OccupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		OccupationCode = occupationCode;
	}

	public String getResidencePhone2() {
		return ResidencePhone2;
	}

	public void setResidencePhone2(String residencePhone2) {
		ResidencePhone2 = residencePhone2;
	}

	public String getResidenceExtension2() {
		return ResidenceExtension2;
	}

	public void setResidenceExtension2(String residenceExtension2) {
		ResidenceExtension2 = residenceExtension2;
	}

	public String getBusinessPhone2() {
		return BusinessPhone2;
	}

	public void setBusinessPhone2(String businessPhone2) {
		BusinessPhone2 = businessPhone2;
	}

	public String getBusinessExtension2() {
		return BusinessExtension2;
	}

	public void setBusinessExtension2(String businessExtension2) {
		BusinessExtension2 = businessExtension2;
	}

	public String getFaxPhone2() {
		return FaxPhone2;
	}

	public void setFaxPhone2(String faxPhone2) {
		FaxPhone2 = faxPhone2;
	}

	public String getMobilePhone2() {
		return MobilePhone2;
	}

	public void setMobilePhone2(String mobilePhone2) {
		MobilePhone2 = mobilePhone2;
	}

	public String getIdType3() {
		return IdType3;
	}

	public void setIdType3(String idType3) {
		IdType3 = idType3;
	}

	public String getIdNo3() {
		return IdNo3;
	}

	public void setIdNo3(String idNo3) {
		IdNo3 = idNo3;
	}

	public String getBloodType() {
		return BloodType;
	}

	public void setBloodType(String bloodType) {
		BloodType = bloodType;
	}

	public String getPassportCode() {
		return PassportCode;
	}

	public void setPassportCode(String passportCode) {
		PassportCode = passportCode;
	}

	public String getLstloanCode() {
		return LstloanCode;
	}

	public void setLstloanCode(String lstloanCode) {
		LstloanCode = lstloanCode;
	}

	public String getOnJobDate() {
		return OnJobDate;
	}

	public void setOnJobDate(String onJobDate) {
		OnJobDate = onJobDate;
	}

	public String getYearsWorking() {
		return YearsWorking;
	}

	public void setYearsWorking(String yearsWorking) {
		YearsWorking = yearsWorking;
	}

	public String getJobStatus() {
		return JobStatus;
	}

	public void setJobStatus(String jobStatus) {
		JobStatus = jobStatus;
	}

	public String getRecrmtType() {
		return RecrmtType;
	}

	public void setRecrmtType(String recrmtType) {
		RecrmtType = recrmtType;
	}

	public String getEmployerIDType() {
		return EmployerIDType;
	}

	public void setEmployerIDType(String employerIDType) {
		EmployerIDType = employerIDType;
	}

	public String getEmplyrIDNo() {
		return EmplyrIDNo;
	}

	public void setEmplyrIDNo(String emplyrIDNo) {
		EmplyrIDNo = emplyrIDNo;
	}

	public String getCongIDType() {
		return CongIDType;
	}

	public void setCongIDType(String congIDType) {
		CongIDType = congIDType;
	}

	public String getCongIDNo() {
		return CongIDNo;
	}

	public void setCongIDNo(String congIDNo) {
		CongIDNo = congIDNo;
	}

	public String getEmpIND() {
		return EmpIND;
	}

	public void setEmpIND(String empIND) {
		EmpIND = empIND;
	}

	public String getEmployer() {
		return Employer;
	}

	public void setEmployer(String employer) {
		Employer = employer;
	}

	public String getDeptIND() {
		return DeptIND;
	}

	public void setDeptIND(String deptIND) {
		DeptIND = deptIND;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public String getPrevEmpIND() {
		return PrevEmpIND;
	}

	public void setPrevEmpIND(String prevEmpIND) {
		PrevEmpIND = prevEmpIND;
	}

	public String getPrevEmp() {
		return PrevEmp;
	}

	public void setPrevEmp(String prevEmp) {
		PrevEmp = prevEmp;
	}

	public String getDefaultString11() {
		return DefaultString11;
	}

	public void setDefaultString11(String defaultString11) {
		DefaultString11 = defaultString11;
	}

	public String getIdType1() {
		return IdType1;
	}

	public void setIdType1(String idType1) {
		IdType1 = idType1;
	}

	public String getChildren() {
		return children;
	}

	public void setChildren(String children) {
		this.children = children;
	}

	public String getRep1name() {
		return rep1name;
	}

	public void setRep1name(String rep1name) {
		this.rep1name = rep1name;
	}

	public String getRep1DOB() {
		return rep1DOB;
	}

	public void setRep1DOB(String rep1DOB) {
		this.rep1DOB = rep1DOB;
	}

	public String getRep1IdType() {
		return rep1IdType;
	}

	public void setRep1IdType(String rep1IdType) {
		this.rep1IdType = rep1IdType;
	}

	public String getRep1Date() {
		return rep1Date;
	}

	public void setRep1Date(String rep1Date) {
		this.rep1Date = rep1Date;
	}

	public String getRep1Photo() {
		return rep1Photo;
	}

	public void setRep1Photo(String rep1Photo) {
		this.rep1Photo = rep1Photo;
	}

	public String getRep1Place() {
		return rep1Place;
	}

	public void setRep1Place(String rep1Place) {
		this.rep1Place = rep1Place;
	}

	public String getRep2name() {
		return rep2name;
	}

	public void setRep2name(String rep2name) {
		this.rep2name = rep2name;
	}

	public String getRep2DOB() {
		return rep2DOB;
	}

	public void setRep2DOB(String rep2DOB) {
		this.rep2DOB = rep2DOB;
	}

	public String getRep2IdType() {
		return rep2IdType;
	}

	public void setRep2IdType(String rep2IdType) {
		this.rep2IdType = rep2IdType;
	}

	public String getRep2Date() {
		return rep2Date;
	}

	public void setRep2Date(String rep2Date) {
		this.rep2Date = rep2Date;
	}

	public String getRep2Photo() {
		return rep2Photo;
	}

	public void setRep2Photo(String rep2Photo) {
		this.rep2Photo = rep2Photo;
	}

	public String getRep2Place() {
		return rep2Place;
	}

	public void setRep2Place(String rep2Place) {
		this.rep2Place = rep2Place;
	}

	public String getIncomedt() {
		return incomedt;
	}

	public void setIncomedt(String incomedt) {
		this.incomedt = incomedt;
	}

	public String getGuardianShip() {
		return GuardianShip;
	}

	public void setGuardianShip(String guardianShip) {
		GuardianShip = guardianShip;
	}

	public String getGUA_ASS_NAME() {
		return GUA_ASS_NAME;
	}

	public void setGUA_ASS_NAME(String GUA_ASS_NAME) {
		this.GUA_ASS_NAME = GUA_ASS_NAME;
	}

	public String getEmp_code() {
		return emp_code;
	}

	public void setEmp_code(String emp_code) {
		this.emp_code = emp_code;
	}

	public String getFILLER2() {
		return FILLER2;
	}

	public void setFILLER2(String FILLER2) {
		this.FILLER2 = FILLER2;
	}

	public String getCUSM_EX_FLAG_1() {
		return CUSM_EX_FLAG_1;
	}

	public void setCUSM_EX_FLAG_1(String CUSM_EX_FLAG_1) {
		this.CUSM_EX_FLAG_1 = CUSM_EX_FLAG_1;
	}

	public String getCUSM_EX_FLAG_2() {
		return CUSM_EX_FLAG_2;
	}

	public void setCUSM_EX_FLAG_2(String CUSM_EX_FLAG_2) {
		this.CUSM_EX_FLAG_2 = CUSM_EX_FLAG_2;
	}

	public String getCUSM_EX_FLAG_3() {
		return CUSM_EX_FLAG_3;
	}

	public void setCUSM_EX_FLAG_3(String CUSM_EX_FLAG_3) {
		this.CUSM_EX_FLAG_3 = CUSM_EX_FLAG_3;
	}

	public String getCUSM_EX_FLAG_4() {
		return CUSM_EX_FLAG_4;
	}

	public void setCUSM_EX_FLAG_4(String CUSM_EX_FLAG_4) {
		this.CUSM_EX_FLAG_4 = CUSM_EX_FLAG_4;
	}

	public String getCUSM_EX_FLAG_5() {
		return CUSM_EX_FLAG_5;
	}

	public void setCUSM_EX_FLAG_5(String CUSM_EX_FLAG_5) {
		this.CUSM_EX_FLAG_5 = CUSM_EX_FLAG_5;
	}

	public String getCUSM_EX_ATTRIBUTE_1() {
		return CUSM_EX_ATTRIBUTE_1;
	}

	public void setCUSM_EX_ATTRIBUTE_1(String CUSM_EX_ATTRIBUTE_1) {
		this.CUSM_EX_ATTRIBUTE_1 = CUSM_EX_ATTRIBUTE_1;
	}

	public String getCUSM_EX_ATTRIBUTE_2() {
		return CUSM_EX_ATTRIBUTE_2;
	}

	public void setCUSM_EX_ATTRIBUTE_2(String CUSM_EX_ATTRIBUTE_2) {
		this.CUSM_EX_ATTRIBUTE_2 = CUSM_EX_ATTRIBUTE_2;
	}

	public String getCUSM_EX_ATTRIBUTE_3() {
		return CUSM_EX_ATTRIBUTE_3;
	}

	public void setCUSM_EX_ATTRIBUTE_3(String CUSM_EX_ATTRIBUTE_3) {
		this.CUSM_EX_ATTRIBUTE_3 = CUSM_EX_ATTRIBUTE_3;
	}

	public String getCUSM_EX_ATTRIBUTE_4() {
		return CUSM_EX_ATTRIBUTE_4;
	}

	public void setCUSM_EX_ATTRIBUTE_4(String CUSM_EX_ATTRIBUTE_4) {
		this.CUSM_EX_ATTRIBUTE_4 = CUSM_EX_ATTRIBUTE_4;
	}

	public String getCUSM_EX_ATTRIBUTE_5() {
		return CUSM_EX_ATTRIBUTE_5;
	}

	public void setCUSM_EX_ATTRIBUTE_5(String CUSM_EX_ATTRIBUTE_5) {
		this.CUSM_EX_ATTRIBUTE_5 = CUSM_EX_ATTRIBUTE_5;
	}

	public String getCUSM_EX_ATTRIBUTE_6() {
		return CUSM_EX_ATTRIBUTE_6;
	}

	public void setCUSM_EX_ATTRIBUTE_6(String CUSM_EX_ATTRIBUTE_6) {
		this.CUSM_EX_ATTRIBUTE_6 = CUSM_EX_ATTRIBUTE_6;
	}

	public String getCUSM_EX_ATTRIBUTE_7() {
		return CUSM_EX_ATTRIBUTE_7;
	}

	public void setCUSM_EX_ATTRIBUTE_7(String CUSM_EX_ATTRIBUTE_7) {
		this.CUSM_EX_ATTRIBUTE_7 = CUSM_EX_ATTRIBUTE_7;
	}

	public String getCUSM_EX_ATTRIBUTE_8() {
		return CUSM_EX_ATTRIBUTE_8;
	}

	public void setCUSM_EX_ATTRIBUTE_8(String CUSM_EX_ATTRIBUTE_8) {
		this.CUSM_EX_ATTRIBUTE_8 = CUSM_EX_ATTRIBUTE_8;
	}

	public String getCUSM_EX_ATTRIBUTE_9() {
		return CUSM_EX_ATTRIBUTE_9;
	}

	public void setCUSM_EX_ATTRIBUTE_9(String CUSM_EX_ATTRIBUTE_9) {
		this.CUSM_EX_ATTRIBUTE_9 = CUSM_EX_ATTRIBUTE_9;
	}

	public String getCUSM_EX_ATTRIBUTE_10() {
		return CUSM_EX_ATTRIBUTE_10;
	}

	public void setCUSM_EX_ATTRIBUTE_10(String CUSM_EX_ATTRIBUTE_10) {
		this.CUSM_EX_ATTRIBUTE_10 = CUSM_EX_ATTRIBUTE_10;
	}
}
