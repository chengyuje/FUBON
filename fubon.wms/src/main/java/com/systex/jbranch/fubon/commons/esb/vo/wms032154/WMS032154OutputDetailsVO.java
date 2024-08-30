package com.systex.jbranch.fubon.commons.esb.vo.wms032154;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class WMS032154OutputDetailsVO {
	@XmlElement
	private String CUST_NAME; // 客戶名稱
	@XmlElement
	private String LOST_FLG1; // 戶籍電話失聯註記
	@XmlElement
	private String reg_tel; // 戶籍電話
	@XmlElement
	private String reg_tel_ext; // 戶籍電話分機
	@XmlElement
	private String LOST_FLG2; // 聯絡電話1-1失聯註記
	@XmlElement
	private String resd_tel; // 聯絡電話1-1
	@XmlElement
	private String resd_tel_ext; // 聯絡電話分機
	@XmlElement
	private String LOST_FLG3; // 聯絡電話1-2失聯註記
	@XmlElement
	private String con_tel; // 聯絡電話1-2
	@XmlElement
	private String con_tel_ext; // 聯絡電話1-2分機
	@XmlElement
	private String LOST_FLG4; // 公司電話失聯註記
	@XmlElement
	private String cmpy_tel; // 公司電話
	@XmlElement
	private String cmpy_ext; // 公司電話分機
	@XmlElement
	private String LOST_FLG5; // 行動電話失聯註記
	@XmlElement
	private String handphone; // 行動電話
	@XmlElement
	private String LOST_FLG6; // 傳真失聯註記
	@XmlElement
	private String fax; // 傳真
	@XmlElement
	private String MTN_DATE; // 異動日期
	@XmlElement
	private String MTN_BRH; // 維護分行
	@XmlElement
	private String LOST_FLG7; // 通訊1-地址/通訊電話 失聯
	@XmlElement
	private String LOST_FLG8; // 通訊2-地址/通訊電話 失聯
	@XmlElement
	private String LOST_FLG9; // 公司電話 失聯
	@XmlElement
	private String LOST_FLG10; //  戶籍-地址/通訊電話 失聯
	@XmlElement
	private String LOST_FLG11; // 行動電話1 失聯
	@XmlElement
	private String LOST_FLG12; // Email/行動電話2
	@XmlElement
	private String LOST_FLG13; // 傳真電話1/傳真電話2
	@XmlElement
	private String LOST_FLG14; // 通訊軟體類型1/帳號
	@XmlElement
	private String LOST_FLG15; // 通訊軟體類型2/帳號
	@XmlElement
	private String LOST_FLG16; // 通訊軟體類型3/帳號
	@XmlElement
	private String LOST_FLG17; // 通訊軟體類型4/帳號
	@XmlElement
	private String LOST_FLG18; // 通訊軟體類型5/帳號
	@XmlElement
	private String LOST_FLG19; // 公司地址-失聯
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
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
	public String getMTN_DATE() {
		return MTN_DATE;
	}
	public void setMTN_DATE(String mTN_DATE) {
		MTN_DATE = mTN_DATE;
	}
	public String getMTN_BRH() {
		return MTN_BRH;
	}
	public void setMTN_BRH(String mTN_BRH) {
		MTN_BRH = mTN_BRH;
	}
	public String getLOST_FLG7() {
		return LOST_FLG7;
	}
	public void setLOST_FLG7(String lOST_FLG7) {
		LOST_FLG7 = lOST_FLG7;
	}
	public String getLOST_FLG8() {
		return LOST_FLG8;
	}
	public void setLOST_FLG8(String lOST_FLG8) {
		LOST_FLG8 = lOST_FLG8;
	}
	public String getLOST_FLG9() {
		return LOST_FLG9;
	}
	public void setLOST_FLG9(String lOST_FLG9) {
		LOST_FLG9 = lOST_FLG9;
	}
	public String getLOST_FLG10() {
		return LOST_FLG10;
	}
	public void setLOST_FLG10(String lOST_FLG10) {
		LOST_FLG10 = lOST_FLG10;
	}
	public String getLOST_FLG11() {
		return LOST_FLG11;
	}
	public void setLOST_FLG11(String lOST_FLG11) {
		LOST_FLG11 = lOST_FLG11;
	}
	public String getLOST_FLG12() {
		return LOST_FLG12;
	}
	public void setLOST_FLG12(String lOST_FLG12) {
		LOST_FLG12 = lOST_FLG12;
	}
	public String getLOST_FLG13() {
		return LOST_FLG13;
	}
	public void setLOST_FLG13(String lOST_FLG13) {
		LOST_FLG13 = lOST_FLG13;
	}
	public String getLOST_FLG14() {
		return LOST_FLG14;
	}
	public void setLOST_FLG14(String lOST_FLG14) {
		LOST_FLG14 = lOST_FLG14;
	}
	public String getLOST_FLG15() {
		return LOST_FLG15;
	}
	public void setLOST_FLG15(String lOST_FLG15) {
		LOST_FLG15 = lOST_FLG15;
	}
	public String getLOST_FLG16() {
		return LOST_FLG16;
	}
	public void setLOST_FLG16(String lOST_FLG16) {
		LOST_FLG16 = lOST_FLG16;
	}
	public String getLOST_FLG17() {
		return LOST_FLG17;
	}
	public void setLOST_FLG17(String lOST_FLG17) {
		LOST_FLG17 = lOST_FLG17;
	}
	public String getLOST_FLG18() {
		return LOST_FLG18;
	}
	public void setLOST_FLG18(String lOST_FLG18) {
		LOST_FLG18 = lOST_FLG18;
	}
	public String getLOST_FLG19() {
		return LOST_FLG19;
	}
	public void setLOST_FLG19(String lOST_FLG19) {
		LOST_FLG19 = lOST_FLG19;
	}

	
}