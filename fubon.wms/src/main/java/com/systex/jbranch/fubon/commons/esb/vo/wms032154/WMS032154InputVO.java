package com.systex.jbranch.fubon.commons.esb.vo.wms032154;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Walalala on 2016/12/21.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class WMS032154InputVO {
	@XmlElement
	private String FUNC;// 功能
	@XmlElement
	private String CUST_NO;// 統一編號
	@XmlElement
	private String IDTYPE;// 證件類型
	@XmlElement
	private String LOST_FLG1;// 戶籍電話失聯註記
	@XmlElement
	private String reg_tel;// 戶籍電話
	@XmlElement
	private String reg_tel_ext;// 戶籍電話分機
	@XmlElement
	private String LOST_FLG2;// 聯絡電話1-1失聯註記
	@XmlElement
	private String resd_tel;// 聯絡電話1-1
	@XmlElement
	private String resd_tel_ext;// 聯絡電話分機
	@XmlElement
	private String LOST_FLG3;// 聯絡電話1-2失聯註記
	@XmlElement
	private String con_tel;// 聯絡電話1-2
	@XmlElement
	private String con_tel_ext;// 聯絡電話1-2分機
	@XmlElement
	private String LOST_FLG4;// 公司電話失聯註記
	@XmlElement
	private String cmpy_tel;// 公司電話
	@XmlElement
	private String cmpy_ext;// 公司電話分機
	@XmlElement
	private String LOST_FLG5;// 行動電話失聯註記
	@XmlElement
	private String handphone;// 行動電話
	@XmlElement
	private String LOST_FLG6;// 傳真失聯註記
	@XmlElement
	private String fax;// 傳真

	@XmlElement
	private String addr;//
	@XmlElement
	private String mail;//

	@XmlElement
	private String CHILD_NO;
	@XmlElement
	private String EDUCATION;
	@XmlElement
	private String CAREER;
	@XmlElement
	private String MARRAGE;

	@XmlElement
	private String CVALUE;

	@XmlElement
	private String SICK_TYPE;

	@XmlElement
	private String BRANCH; // 067157上送分行

	@XmlElement
	private String KYC_TEST_DATE; // 因應KYC冷靜期增加變數
	
	@XmlElement
	private String KYC_EXPIRY_DATE; // 新增KYC到期日變數

	public String getBRANCH() {
		return BRANCH;
	}

	public void setBRANCH(String bRANCH) {
		BRANCH = bRANCH;
	}

	public String getSICK_TYPE() {
		return SICK_TYPE;
	}

	public void setSICK_TYPE(String sICK_TYPE) {
		SICK_TYPE = sICK_TYPE;
	}

	public String getCVALUE() {
		return CVALUE;
	}

	public void setCVALUE(String cVALUE) {
		CVALUE = cVALUE;
	}

	public String getCHILD_NO() {
		return CHILD_NO;
	}

	public void setCHILD_NO(String cHILD_NO) {
		CHILD_NO = cHILD_NO;
	}

	public String getEDUCATION() {
		return EDUCATION;
	}

	public void setEDUCATION(String eDUCATION) {
		EDUCATION = eDUCATION;
	}

	public String getCAREER() {
		return CAREER;
	}

	public void setCAREER(String cAREER) {
		CAREER = cAREER;
	}

	public String getMARRAGE() {
		return MARRAGE;
	}

	public void setMARRAGE(String mARRAGE) {
		MARRAGE = mARRAGE;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFUNC() {
		return FUNC;
	}

	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}

	public String getCUST_NO() {
		return CUST_NO;
	}

	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}

	public String getIDTYPE() {
		return IDTYPE;
	}

	public void setIDTYPE(String iDTYPE) {
		IDTYPE = iDTYPE;
	}

	public String getLOST_FLG1() {
		return LOST_FLG1;
	}

	public void setLOST_FLG1(String lOST_FLG1) {
		LOST_FLG1 = lOST_FLG1;
	}

	public String getReg_tel() {
		return reg_tel;
	}

	public void setReg_tel(String reg_tel) {
		this.reg_tel = reg_tel;
	}

	public String getReg_tel_ext() {
		return reg_tel_ext;
	}

	public void setReg_tel_ext(String reg_tel_ext) {
		this.reg_tel_ext = reg_tel_ext;
	}

	public String getLOST_FLG2() {
		return LOST_FLG2;
	}

	public void setLOST_FLG2(String lOST_FLG2) {
		LOST_FLG2 = lOST_FLG2;
	}

	public String getResd_tel() {
		return resd_tel;
	}

	public void setResd_tel(String resd_tel) {
		this.resd_tel = resd_tel;
	}

	public String getResd_tel_ext() {
		return resd_tel_ext;
	}

	public void setResd_tel_ext(String resd_tel_ext) {
		this.resd_tel_ext = resd_tel_ext;
	}

	public String getLOST_FLG3() {
		return LOST_FLG3;
	}

	public void setLOST_FLG3(String lOST_FLG3) {
		LOST_FLG3 = lOST_FLG3;
	}

	public String getCon_tel() {
		return con_tel;
	}

	public void setCon_tel(String con_tel) {
		this.con_tel = con_tel;
	}

	public String getCon_tel_ext() {
		return con_tel_ext;
	}

	public void setCon_tel_ext(String con_tel_ext) {
		this.con_tel_ext = con_tel_ext;
	}

	public String getLOST_FLG4() {
		return LOST_FLG4;
	}

	public void setLOST_FLG4(String lOST_FLG4) {
		LOST_FLG4 = lOST_FLG4;
	}

	public String getCmpy_tel() {
		return cmpy_tel;
	}

	public void setCmpy_tel(String cmpy_tel) {
		this.cmpy_tel = cmpy_tel;
	}

	public String getCmpy_ext() {
		return cmpy_ext;
	}

	public void setCmpy_ext(String cmpy_ext) {
		this.cmpy_ext = cmpy_ext;
	}

	public String getLOST_FLG5() {
		return LOST_FLG5;
	}

	public void setLOST_FLG5(String lOST_FLG5) {
		LOST_FLG5 = lOST_FLG5;
	}

	public String getHandphone() {
		return handphone;
	}

	public void setHandphone(String handphone) {
		this.handphone = handphone;
	}

	public String getLOST_FLG6() {
		return LOST_FLG6;
	}

	public void setLOST_FLG6(String lOST_FLG6) {
		LOST_FLG6 = lOST_FLG6;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getKYC_TEST_DATE() {
		return KYC_TEST_DATE;
	}

	public void setKYC_TEST_DATE(String kYC_TEST_DATE) {
		KYC_TEST_DATE = kYC_TEST_DATE;
	}
	
	public String getKYC_EXPIRY_DATE() {
		return KYC_EXPIRY_DATE;
	}

	public void setKYC_EXPIRY_DATE(String kYC_EXPIRY_DATE) {
		KYC_EXPIRY_DATE = kYC_EXPIRY_DATE;
	}

	public WMS032154InputVO() {
		this.FUNC = "";
		this.CUST_NO = "";
		this.IDTYPE = "";
		this.LOST_FLG1 = "";
		this.reg_tel = "";
		this.reg_tel_ext = "";
		this.LOST_FLG2 = "";
		this.resd_tel = "";
		this.resd_tel_ext = "";
		this.LOST_FLG3 = "";
		this.con_tel = "";
		this.con_tel_ext = "";
		this.LOST_FLG4 = "";
		this.cmpy_tel = "";
		this.cmpy_ext = "";
		this.LOST_FLG5 = "";
		this.handphone = "";
		this.LOST_FLG6 = "";
		this.fax = "";
		this.addr = "";
		this.mail = "";
		this.CHILD_NO = "";
		this.EDUCATION = "";
		this.CAREER = "";
		this.MARRAGE = "";
		this.CVALUE = "";
		this.SICK_TYPE = "";
		this.BRANCH = "";
		this.KYC_TEST_DATE = "";
		this.KYC_EXPIRY_DATE = "";
	}

}