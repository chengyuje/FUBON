package com.systex.jbranch.fubon.commons.cbs.vo._067050_067115;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS067000OutputVO {
    private String accntNumber1; //客戶號
    private String DefaultString1; //客戶類型
    private String DefInteger1; //帳戶推展經理
    private String title1; //個人稱謂代碼
    private String DefaultString2; //名稱/公司名稱
    private String DefaultString3; //姓
    private String WealthMgtOfficerName; //理財經理代碼
    private String DefaultString5; //聯絡地址1-1
    private String DefaultString6; //地址行2
    private String DefaultString7; //聯絡地址2-1
    private String DefaultString8; //地址行4
    private String ManualSlip; //人工註記
    private String StpInd; //止付註記
    private String JCICIndCode; //JCIC 行業代碼
    private String Category; //類別
    private String SubCategory; //產品子類
    private String Regdate; //免除大型交易報表的登記日
    private String DefaultString17; //主計處行業代碼
    private String PostCodePostal1; //聯絡地址1郵遞區號
    private String PersonalCode1; //個人/
    private String BusinessCode1; //業務
    private String EnterpriseCode1; //企業代碼
    private String LstLoanNatCode1; //最後貸款國別
    private String FILLER1; //FILLER1
    private String DefaultString9; //聯絡地址2郵遞區號
    private String countrycode1; //居住地國籍
    private String DefaultString10; //聯絡電話號碼
    private String DefaultString11; //傳真號碼
    private String DefaultString12; //公司電話號碼
    private String DefaultString13; //手機號碼
    private String nationality1; //國籍
    private String occupanc1; //居者有其屋計劃
    private String languagecode1; //語言代碼
    private String DefaultString14; //證件類型/號碼
    private String date1; //證件簽發日期
    private String DefaultString15; //證件簽發在
    private String id_type1; //證件類型/號碼
    private String dom_risk1; //國內風險
    private String BorderRisk1; //跨國風險
    private String VIP_code1; //VIP代碼
    private String DefaultString28; //客戶狀態
    private String segm_code1; //部門代碼
    private String VIP_code2; //儲物櫃持有人
    private String DefInteger5; //稅號標識
    private String DefInteger2; //帳務行
    private String DefaultString20; //客戶
    private String DefaultString21; //客戶家長
    private String DefaultString22; //行業
    private String DefaultString23; //國家
    private String DefaultString24; //部門
    private String BankRelInd; //銀行關係人註記
    private String FinHoldRelInd; //金控關係人註記
    private String TxnRelInd; //交易關係人註記
    private String PosInFinHold; //身份別註記
    private String countrycode2; //風險國家
    private String DefaultString16; //隸屬代碼
    private String DefInteger4; //業務性質別
    private String DefaultString26; //DefaultString26
    private String id_type2; //新ID類型/序號
    private String promono1; //提示碼
    private String DefInteger6; //帳戶管理員
	@XmlElement(name = "wtax-type")
	private String wtax_type; //預扣稅率別
	@XmlElement(name = "stmt-freq")
	private String stmt_freq; //對帳單頻率
	@XmlElement(name = "stmt-cycle")
	private String stmt_cycle; //週期
	@XmlElement(name = "stmt-day")
    private String stmt_day; //日
    private String FILLER2; //FILLER2
	@XmlElement(name = "cif-date1")
    private String cif_date1; //CIF 新增日期
	@XmlElement(name = "cont-ext1")
	private String cont_ext1; //聯絡電話號碼
	@XmlElement(name = "cmpy-ext1")
	private String cmpy_ext1; //公司電話號碼
	@XmlElement(name = "with-cert1")
	private String with_cert1; //扣繳憑單郵寄
	@XmlElement(name = "cust-srce1")
	private String cust_srce1; //客戶來源
    private String FILLER3; //FILLER3
	@XmlElement(name = "reg-pcode1")
	private String reg_pcode1; //戶籍郵遞區號
	@XmlElement(name = "reg-addr1")
	private String reg_addr1; //戶籍地址 1
	@XmlElement(name = "reg-addr2")
	private String reg_addr2; //戶籍地址 2
	@XmlElement(name = "reg-phne1")
	private String reg_phne1; //戶籍電話號碼
	@XmlElement(name = "reg-ext1")
	private String reg_ext1; //戶籍電話號碼
	@XmlElement(name = "email-addr1")
    private String email_addr1; //電子郵件地址
    private String phone2; //聯絡電話號碼2
	@XmlElement(name = "phone2-ext")
    private String phone2_ext; //phone2-ext
    private String FILLER4; //FILLER4
    private String MaritalStatus1; //婚姻狀況
    private String GrdnIDType1; //監護人1/負責人證件類型/號碼
    private String GrdnIDNo1; //監護人1/負責人證件類型/號碼
    private String GrdnIDType2; //監護人2/負責人證件類型/號碼
    private String GrdnIDNo2; //監護人2/負責人證件類型/號碼
    private String IPLType1; //類型1 /號碼
    private String IPLNo1; //類型1 /號碼
    private String AGLevel1; //AGLevel1
    private String IPLType2; //類型2 /號碼
    private String IPLNo2; //類型2 /號碼
    private String AGLevel2; //AGLevel2
    private String IDType2; //第二個ID類型/號碼
    private String IDNo2; //第二個ID類型/號碼
    private String IDExpDate2; //第二個ID到期日
    private String FILLER5; //FILLER5
    private String SensitiveCat; //利害關係人類別註記
    private String date2; //生日
    private String custlastname; //custlastname
    private String lstmntdate; //最後維護日
    private String BranchID1; //最後維護分行
    private String TellerID1; //最後維護櫃員
    private String Status2; //狀態 2
    private String Status3; //Status3
    private String AssSize1; //企業規模
    private String AnnAudit1; //年度審計
    private String CorpCat1; //公司類別
    private String BussType1; //組織型態
    private String idchgreqby1; //變更證件
    private String fororigid1; //舊證件號碼
    private String majmarket1; //主要的市場參與者
    private String fxind1; //使用外匯系統
    private String mfind1; //使用共同基金系統
    private String factoring1; //使用Factoring 業務
    private String cbcode1; //央行行業代碼
    private String telnotcor1; //電話號碼是正確的嗎？
    private String introdept1; //客戶介紹部門
    private String idchanbr1; //證件變更分行
    private String contcustphone1; //聯絡客戶
    private String FILLER6; //FILLER6
    private String StatementPostMethod; //對帳單寄送方式
    private String SuspStat; //人頭戶狀態
    private String WealthMgtOfficerIDNo; //理財經理代碼
    private String PDSLawAgreeDate; //PDS起訴同意日期
    private String PDSLawAgreeBR; //PDS起訴同意BR
    private String nhipremiumflag; //補充健保費註記
    private String nhipremiummatdate; //補充健保費註記到期日
    private String engname1; //英文戶名1
    private String engname2; //英文戶名2
    private String engname3; //英文戶名3
    private String engname4; //英文戶名4
    private String engaddr1; //英文地址1
    private String engaddr2; //英文地址2
    private String engaddr3; //英文地址3
    private String engaddr4; //英文地址4
    private String offbranch; //歸屬行
    private String oldidtype; //舊證件類型
    private String IDExpDate; //證件到期日
    private String MobleNoWithoutFlags; //行動電話未使用註記
    private String cust_rating_Per_finance; //客戶分級註記_個金
    private String cust_rating_corp_finance; //客戶分級註記_法金
    private String prod_rating_trade; //產品分級_Trade
    private String prod_rating_factoring; //產品分級_Factoring
    private String prod_rating_cash; //產品分級_Cash
	@XmlElement(name = "JCIC-INDU-UPDT")
	private String JCIC_INDU_UPDT; //授信行業別異動日


	public String getAccntNumber1() {
		return accntNumber1;
	}

	public void setAccntNumber1(String accntNumber1) {
		this.accntNumber1 = accntNumber1;
	}

	public String getDefaultString1() {
		return DefaultString1;
	}

	public void setDefaultString1(String defaultString1) {
		DefaultString1 = defaultString1;
	}

	public String getDefInteger1() {
		return DefInteger1;
	}

	public void setDefInteger1(String defInteger1) {
		DefInteger1 = defInteger1;
	}

	public String getTitle1() {
		return title1;
	}

	public void setTitle1(String title1) {
		this.title1 = title1;
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

	public String getWealthMgtOfficerName() {
		return WealthMgtOfficerName;
	}

	public void setWealthMgtOfficerName(String wealthMgtOfficerName) {
		WealthMgtOfficerName = wealthMgtOfficerName;
	}

	public String getDefaultString5() {
		return DefaultString5;
	}

	public void setDefaultString5(String defaultString5) {
		DefaultString5 = defaultString5;
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

	public String getDefaultString8() {
		return DefaultString8;
	}

	public void setDefaultString8(String defaultString8) {
		DefaultString8 = defaultString8;
	}

	public String getManualSlip() {
		return ManualSlip;
	}

	public void setManualSlip(String manualSlip) {
		ManualSlip = manualSlip;
	}

	public String getStpInd() {
		return StpInd;
	}

	public void setStpInd(String stpInd) {
		StpInd = stpInd;
	}

	public String getJCICIndCode() {
		return JCICIndCode;
	}

	public void setJCICIndCode(String JCICIndCode) {
		this.JCICIndCode = JCICIndCode;
	}

	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}

	public String getSubCategory() {
		return SubCategory;
	}

	public void setSubCategory(String subCategory) {
		SubCategory = subCategory;
	}

	public String getRegdate() {
		return Regdate;
	}

	public void setRegdate(String regdate) {
		Regdate = regdate;
	}

	public String getDefaultString17() {
		return DefaultString17;
	}

	public void setDefaultString17(String defaultString17) {
		DefaultString17 = defaultString17;
	}

	public String getPostCodePostal1() {
		return PostCodePostal1;
	}

	public void setPostCodePostal1(String postCodePostal1) {
		PostCodePostal1 = postCodePostal1;
	}

	public String getPersonalCode1() {
		return PersonalCode1;
	}

	public void setPersonalCode1(String personalCode1) {
		PersonalCode1 = personalCode1;
	}

	public String getBusinessCode1() {
		return BusinessCode1;
	}

	public void setBusinessCode1(String businessCode1) {
		BusinessCode1 = businessCode1;
	}

	public String getEnterpriseCode1() {
		return EnterpriseCode1;
	}

	public void setEnterpriseCode1(String enterpriseCode1) {
		EnterpriseCode1 = enterpriseCode1;
	}

	public String getLstLoanNatCode1() {
		return LstLoanNatCode1;
	}

	public void setLstLoanNatCode1(String lstLoanNatCode1) {
		LstLoanNatCode1 = lstLoanNatCode1;
	}

	public String getFILLER1() {
		return FILLER1;
	}

	public void setFILLER1(String FILLER1) {
		this.FILLER1 = FILLER1;
	}

	public String getDefaultString9() {
		return DefaultString9;
	}

	public void setDefaultString9(String defaultString9) {
		DefaultString9 = defaultString9;
	}

	public String getCountrycode1() {
		return countrycode1;
	}

	public void setCountrycode1(String countrycode1) {
		this.countrycode1 = countrycode1;
	}

	public String getDefaultString10() {
		return DefaultString10;
	}

	public void setDefaultString10(String defaultString10) {
		DefaultString10 = defaultString10;
	}

	public String getDefaultString11() {
		return DefaultString11;
	}

	public void setDefaultString11(String defaultString11) {
		DefaultString11 = defaultString11;
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

	public String getNationality1() {
		return nationality1;
	}

	public void setNationality1(String nationality1) {
		this.nationality1 = nationality1;
	}

	public String getOccupanc1() {
		return occupanc1;
	}

	public void setOccupanc1(String occupanc1) {
		this.occupanc1 = occupanc1;
	}

	public String getLanguagecode1() {
		return languagecode1;
	}

	public void setLanguagecode1(String languagecode1) {
		this.languagecode1 = languagecode1;
	}

	public String getDefaultString14() {
		return DefaultString14;
	}

	public void setDefaultString14(String defaultString14) {
		DefaultString14 = defaultString14;
	}

	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	public String getDefaultString15() {
		return DefaultString15;
	}

	public void setDefaultString15(String defaultString15) {
		DefaultString15 = defaultString15;
	}

	public String getId_type1() {
		return id_type1;
	}

	public void setId_type1(String id_type1) {
		this.id_type1 = id_type1;
	}

	public String getDom_risk1() {
		return dom_risk1;
	}

	public void setDom_risk1(String dom_risk1) {
		this.dom_risk1 = dom_risk1;
	}

	public String getBorderRisk1() {
		return BorderRisk1;
	}

	public void setBorderRisk1(String borderRisk1) {
		BorderRisk1 = borderRisk1;
	}

	public String getVIP_code1() {
		return VIP_code1;
	}

	public void setVIP_code1(String VIP_code1) {
		this.VIP_code1 = VIP_code1;
	}

	public String getDefaultString28() {
		return DefaultString28;
	}

	public void setDefaultString28(String defaultString28) {
		DefaultString28 = defaultString28;
	}

	public String getSegm_code1() {
		return segm_code1;
	}

	public void setSegm_code1(String segm_code1) {
		this.segm_code1 = segm_code1;
	}

	public String getVIP_code2() {
		return VIP_code2;
	}

	public void setVIP_code2(String VIP_code2) {
		this.VIP_code2 = VIP_code2;
	}

	public String getDefInteger5() {
		return DefInteger5;
	}

	public void setDefInteger5(String defInteger5) {
		DefInteger5 = defInteger5;
	}

	public String getDefInteger2() {
		return DefInteger2;
	}

	public void setDefInteger2(String defInteger2) {
		DefInteger2 = defInteger2;
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

	public String getDefaultString22() {
		return DefaultString22;
	}

	public void setDefaultString22(String defaultString22) {
		DefaultString22 = defaultString22;
	}

	public String getDefaultString23() {
		return DefaultString23;
	}

	public void setDefaultString23(String defaultString23) {
		DefaultString23 = defaultString23;
	}

	public String getDefaultString24() {
		return DefaultString24;
	}

	public void setDefaultString24(String defaultString24) {
		DefaultString24 = defaultString24;
	}

	public String getBankRelInd() {
		return BankRelInd;
	}

	public void setBankRelInd(String bankRelInd) {
		BankRelInd = bankRelInd;
	}

	public String getFinHoldRelInd() {
		return FinHoldRelInd;
	}

	public void setFinHoldRelInd(String finHoldRelInd) {
		FinHoldRelInd = finHoldRelInd;
	}

	public String getTxnRelInd() {
		return TxnRelInd;
	}

	public void setTxnRelInd(String txnRelInd) {
		TxnRelInd = txnRelInd;
	}

	public String getPosInFinHold() {
		return PosInFinHold;
	}

	public void setPosInFinHold(String posInFinHold) {
		PosInFinHold = posInFinHold;
	}

	public String getCountrycode2() {
		return countrycode2;
	}

	public void setCountrycode2(String countrycode2) {
		this.countrycode2 = countrycode2;
	}

	public String getDefaultString16() {
		return DefaultString16;
	}

	public void setDefaultString16(String defaultString16) {
		DefaultString16 = defaultString16;
	}

	public String getDefInteger4() {
		return DefInteger4;
	}

	public void setDefInteger4(String defInteger4) {
		DefInteger4 = defInteger4;
	}

	public String getDefaultString26() {
		return DefaultString26;
	}

	public void setDefaultString26(String defaultString26) {
		DefaultString26 = defaultString26;
	}

	public String getId_type2() {
		return id_type2;
	}

	public void setId_type2(String id_type2) {
		this.id_type2 = id_type2;
	}

	public String getPromono1() {
		return promono1;
	}

	public void setPromono1(String promono1) {
		this.promono1 = promono1;
	}

	public String getDefInteger6() {
		return DefInteger6;
	}

	public void setDefInteger6(String defInteger6) {
		DefInteger6 = defInteger6;
	}

	public String getWtax_type() {
		return wtax_type;
	}

	public void setWtax_type(String wtax_type) {
		this.wtax_type = wtax_type;
	}

	public String getStmt_freq() {
		return stmt_freq;
	}

	public void setStmt_freq(String stmt_freq) {
		this.stmt_freq = stmt_freq;
	}

	public String getStmt_cycle() {
		return stmt_cycle;
	}

	public void setStmt_cycle(String stmt_cycle) {
		this.stmt_cycle = stmt_cycle;
	}

	public String getStmt_day() {
		return stmt_day;
	}

	public void setStmt_day(String stmt_day) {
		this.stmt_day = stmt_day;
	}

	public String getFILLER2() {
		return FILLER2;
	}

	public void setFILLER2(String FILLER2) {
		this.FILLER2 = FILLER2;
	}

	public String getCif_date1() {
		return cif_date1;
	}

	public void setCif_date1(String cif_date1) {
		this.cif_date1 = cif_date1;
	}

	public String getCont_ext1() {
		return cont_ext1;
	}

	public void setCont_ext1(String cont_ext1) {
		this.cont_ext1 = cont_ext1;
	}

	public String getCmpy_ext1() {
		return cmpy_ext1;
	}

	public void setCmpy_ext1(String cmpy_ext1) {
		this.cmpy_ext1 = cmpy_ext1;
	}

	public String getWith_cert1() {
		return with_cert1;
	}

	public void setWith_cert1(String with_cert1) {
		this.with_cert1 = with_cert1;
	}

	public String getCust_srce1() {
		return cust_srce1;
	}

	public void setCust_srce1(String cust_srce1) {
		this.cust_srce1 = cust_srce1;
	}

	public String getFILLER3() {
		return FILLER3;
	}

	public void setFILLER3(String FILLER3) {
		this.FILLER3 = FILLER3;
	}

	public String getReg_pcode1() {
		return reg_pcode1;
	}

	public void setReg_pcode1(String reg_pcode1) {
		this.reg_pcode1 = reg_pcode1;
	}

	public String getReg_addr1() {
		return reg_addr1;
	}

	public void setReg_addr1(String reg_addr1) {
		this.reg_addr1 = reg_addr1;
	}

	public String getReg_addr2() {
		return reg_addr2;
	}

	public void setReg_addr2(String reg_addr2) {
		this.reg_addr2 = reg_addr2;
	}

	public String getReg_phne1() {
		return reg_phne1;
	}

	public void setReg_phne1(String reg_phne1) {
		this.reg_phne1 = reg_phne1;
	}

	public String getReg_ext1() {
		return reg_ext1;
	}

	public void setReg_ext1(String reg_ext1) {
		this.reg_ext1 = reg_ext1;
	}

	public String getEmail_addr1() {
		return email_addr1;
	}

	public void setEmail_addr1(String email_addr1) {
		this.email_addr1 = email_addr1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getPhone2_ext() {
		return phone2_ext;
	}

	public void setPhone2_ext(String phone2_ext) {
		this.phone2_ext = phone2_ext;
	}

	public String getFILLER4() {
		return FILLER4;
	}

	public void setFILLER4(String FILLER4) {
		this.FILLER4 = FILLER4;
	}

	public String getMaritalStatus1() {
		return MaritalStatus1;
	}

	public void setMaritalStatus1(String maritalStatus1) {
		MaritalStatus1 = maritalStatus1;
	}

	public String getGrdnIDType1() {
		return GrdnIDType1;
	}

	public void setGrdnIDType1(String grdnIDType1) {
		GrdnIDType1 = grdnIDType1;
	}

	public String getGrdnIDNo1() {
		return GrdnIDNo1;
	}

	public void setGrdnIDNo1(String grdnIDNo1) {
		GrdnIDNo1 = grdnIDNo1;
	}

	public String getGrdnIDType2() {
		return GrdnIDType2;
	}

	public void setGrdnIDType2(String grdnIDType2) {
		GrdnIDType2 = grdnIDType2;
	}

	public String getGrdnIDNo2() {
		return GrdnIDNo2;
	}

	public void setGrdnIDNo2(String grdnIDNo2) {
		GrdnIDNo2 = grdnIDNo2;
	}

	public String getIPLType1() {
		return IPLType1;
	}

	public void setIPLType1(String IPLType1) {
		this.IPLType1 = IPLType1;
	}

	public String getIPLNo1() {
		return IPLNo1;
	}

	public void setIPLNo1(String IPLNo1) {
		this.IPLNo1 = IPLNo1;
	}

	public String getAGLevel1() {
		return AGLevel1;
	}

	public void setAGLevel1(String AGLevel1) {
		this.AGLevel1 = AGLevel1;
	}

	public String getIPLType2() {
		return IPLType2;
	}

	public void setIPLType2(String IPLType2) {
		this.IPLType2 = IPLType2;
	}

	public String getIPLNo2() {
		return IPLNo2;
	}

	public void setIPLNo2(String IPLNo2) {
		this.IPLNo2 = IPLNo2;
	}

	public String getAGLevel2() {
		return AGLevel2;
	}

	public void setAGLevel2(String AGLevel2) {
		this.AGLevel2 = AGLevel2;
	}

	public String getIDType2() {
		return IDType2;
	}

	public void setIDType2(String IDType2) {
		this.IDType2 = IDType2;
	}

	public String getIDNo2() {
		return IDNo2;
	}

	public void setIDNo2(String IDNo2) {
		this.IDNo2 = IDNo2;
	}

	public String getIDExpDate2() {
		return IDExpDate2;
	}

	public void setIDExpDate2(String IDExpDate2) {
		this.IDExpDate2 = IDExpDate2;
	}

	public String getFILLER5() {
		return FILLER5;
	}

	public void setFILLER5(String FILLER5) {
		this.FILLER5 = FILLER5;
	}

	public String getSensitiveCat() {
		return SensitiveCat;
	}

	public void setSensitiveCat(String sensitiveCat) {
		SensitiveCat = sensitiveCat;
	}

	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	public String getCustlastname() {
		return custlastname;
	}

	public void setCustlastname(String custlastname) {
		this.custlastname = custlastname;
	}

	public String getLstmntdate() {
		return lstmntdate;
	}

	public void setLstmntdate(String lstmntdate) {
		this.lstmntdate = lstmntdate;
	}

	public String getBranchID1() {
		return BranchID1;
	}

	public void setBranchID1(String branchID1) {
		BranchID1 = branchID1;
	}

	public String getTellerID1() {
		return TellerID1;
	}

	public void setTellerID1(String tellerID1) {
		TellerID1 = tellerID1;
	}

	public String getStatus2() {
		return Status2;
	}

	public void setStatus2(String status2) {
		Status2 = status2;
	}

	public String getStatus3() {
		return Status3;
	}

	public void setStatus3(String status3) {
		Status3 = status3;
	}

	public String getAssSize1() {
		return AssSize1;
	}

	public void setAssSize1(String assSize1) {
		AssSize1 = assSize1;
	}

	public String getAnnAudit1() {
		return AnnAudit1;
	}

	public void setAnnAudit1(String annAudit1) {
		AnnAudit1 = annAudit1;
	}

	public String getCorpCat1() {
		return CorpCat1;
	}

	public void setCorpCat1(String corpCat1) {
		CorpCat1 = corpCat1;
	}

	public String getBussType1() {
		return BussType1;
	}

	public void setBussType1(String bussType1) {
		BussType1 = bussType1;
	}

	public String getIdchgreqby1() {
		return idchgreqby1;
	}

	public void setIdchgreqby1(String idchgreqby1) {
		this.idchgreqby1 = idchgreqby1;
	}

	public String getFororigid1() {
		return fororigid1;
	}

	public void setFororigid1(String fororigid1) {
		this.fororigid1 = fororigid1;
	}

	public String getMajmarket1() {
		return majmarket1;
	}

	public void setMajmarket1(String majmarket1) {
		this.majmarket1 = majmarket1;
	}

	public String getFxind1() {
		return fxind1;
	}

	public void setFxind1(String fxind1) {
		this.fxind1 = fxind1;
	}

	public String getMfind1() {
		return mfind1;
	}

	public void setMfind1(String mfind1) {
		this.mfind1 = mfind1;
	}

	public String getFactoring1() {
		return factoring1;
	}

	public void setFactoring1(String factoring1) {
		this.factoring1 = factoring1;
	}

	public String getCbcode1() {
		return cbcode1;
	}

	public void setCbcode1(String cbcode1) {
		this.cbcode1 = cbcode1;
	}

	public String getTelnotcor1() {
		return telnotcor1;
	}

	public void setTelnotcor1(String telnotcor1) {
		this.telnotcor1 = telnotcor1;
	}

	public String getIntrodept1() {
		return introdept1;
	}

	public void setIntrodept1(String introdept1) {
		this.introdept1 = introdept1;
	}

	public String getIdchanbr1() {
		return idchanbr1;
	}

	public void setIdchanbr1(String idchanbr1) {
		this.idchanbr1 = idchanbr1;
	}

	public String getContcustphone1() {
		return contcustphone1;
	}

	public void setContcustphone1(String contcustphone1) {
		this.contcustphone1 = contcustphone1;
	}

	public String getFILLER6() {
		return FILLER6;
	}

	public void setFILLER6(String FILLER6) {
		this.FILLER6 = FILLER6;
	}

	public String getStatementPostMethod() {
		return StatementPostMethod;
	}

	public void setStatementPostMethod(String statementPostMethod) {
		StatementPostMethod = statementPostMethod;
	}

	public String getSuspStat() {
		return SuspStat;
	}

	public void setSuspStat(String suspStat) {
		SuspStat = suspStat;
	}

	public String getWealthMgtOfficerIDNo() {
		return WealthMgtOfficerIDNo;
	}

	public void setWealthMgtOfficerIDNo(String wealthMgtOfficerIDNo) {
		WealthMgtOfficerIDNo = wealthMgtOfficerIDNo;
	}

	public String getPDSLawAgreeDate() {
		return PDSLawAgreeDate;
	}

	public void setPDSLawAgreeDate(String PDSLawAgreeDate) {
		this.PDSLawAgreeDate = PDSLawAgreeDate;
	}

	public String getPDSLawAgreeBR() {
		return PDSLawAgreeBR;
	}

	public void setPDSLawAgreeBR(String PDSLawAgreeBR) {
		this.PDSLawAgreeBR = PDSLawAgreeBR;
	}

	public String getNhipremiumflag() {
		return nhipremiumflag;
	}

	public void setNhipremiumflag(String nhipremiumflag) {
		this.nhipremiumflag = nhipremiumflag;
	}

	public String getNhipremiummatdate() {
		return nhipremiummatdate;
	}

	public void setNhipremiummatdate(String nhipremiummatdate) {
		this.nhipremiummatdate = nhipremiummatdate;
	}

	public String getEngname1() {
		return engname1;
	}

	public void setEngname1(String engname1) {
		this.engname1 = engname1;
	}

	public String getEngname2() {
		return engname2;
	}

	public void setEngname2(String engname2) {
		this.engname2 = engname2;
	}

	public String getEngname3() {
		return engname3;
	}

	public void setEngname3(String engname3) {
		this.engname3 = engname3;
	}

	public String getEngname4() {
		return engname4;
	}

	public void setEngname4(String engname4) {
		this.engname4 = engname4;
	}

	public String getEngaddr1() {
		return engaddr1;
	}

	public void setEngaddr1(String engaddr1) {
		this.engaddr1 = engaddr1;
	}

	public String getEngaddr2() {
		return engaddr2;
	}

	public void setEngaddr2(String engaddr2) {
		this.engaddr2 = engaddr2;
	}

	public String getEngaddr3() {
		return engaddr3;
	}

	public void setEngaddr3(String engaddr3) {
		this.engaddr3 = engaddr3;
	}

	public String getEngaddr4() {
		return engaddr4;
	}

	public void setEngaddr4(String engaddr4) {
		this.engaddr4 = engaddr4;
	}

	public String getOffbranch() {
		return offbranch;
	}

	public void setOffbranch(String offbranch) {
		this.offbranch = offbranch;
	}

	public String getOldidtype() {
		return oldidtype;
	}

	public void setOldidtype(String oldidtype) {
		this.oldidtype = oldidtype;
	}

	public String getIDExpDate() {
		return IDExpDate;
	}

	public void setIDExpDate(String IDExpDate) {
		this.IDExpDate = IDExpDate;
	}

	public String getMobleNoWithoutFlags() {
		return MobleNoWithoutFlags;
	}

	public void setMobleNoWithoutFlags(String mobleNoWithoutFlags) {
		MobleNoWithoutFlags = mobleNoWithoutFlags;
	}

	public String getCust_rating_Per_finance() {
		return cust_rating_Per_finance;
	}

	public void setCust_rating_Per_finance(String cust_rating_Per_finance) {
		this.cust_rating_Per_finance = cust_rating_Per_finance;
	}

	public String getCust_rating_corp_finance() {
		return cust_rating_corp_finance;
	}

	public void setCust_rating_corp_finance(String cust_rating_corp_finance) {
		this.cust_rating_corp_finance = cust_rating_corp_finance;
	}

	public String getProd_rating_trade() {
		return prod_rating_trade;
	}

	public void setProd_rating_trade(String prod_rating_trade) {
		this.prod_rating_trade = prod_rating_trade;
	}

	public String getProd_rating_factoring() {
		return prod_rating_factoring;
	}

	public void setProd_rating_factoring(String prod_rating_factoring) {
		this.prod_rating_factoring = prod_rating_factoring;
	}

	public String getProd_rating_cash() {
		return prod_rating_cash;
	}

	public void setProd_rating_cash(String prod_rating_cash) {
		this.prod_rating_cash = prod_rating_cash;
	}

	public String getJCIC_INDU_UPDT() {
		return JCIC_INDU_UPDT;
	}

	public void setJCIC_INDU_UPDT(String JCIC_INDU_UPDT) {
		this.JCIC_INDU_UPDT = JCIC_INDU_UPDT;
	}
}
