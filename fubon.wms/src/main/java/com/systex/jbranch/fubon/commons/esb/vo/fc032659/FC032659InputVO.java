package com.systex.jbranch.fubon.commons.esb.vo.fc032659;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;




/**
 *  Created by Valentino on 2017/03/29.
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032659InputVO { 
	 
	@XmlElement
	private String CUST_NO;// 客戶統一編號
	@XmlElement
	private String SEP01;// 0xB8 FKey
	@XmlElement
	private String FUNC;// 查詢類別
	@XmlElement
	private String SEP02;// 0xB8 FKey
	@XmlElement
	private String COD_01;// 查詢代碼
	@XmlElement
	private String SEP03;// 0xB8 FKey
	@XmlElement
	private String CTRY_COD;// 查詢分行
	@XmlElement
	private String SEP04;// 0xB8 FKey
	@XmlElement
	private String BUS_ZIP;// 查詢帳單年月
	@XmlElement
	private String SEP05;// 0xB8 FKey
	@XmlElement
	private String ENDKey;// 0xFE FKey
	public String getCUST_NO() {
		return CUST_NO;
	}
	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}
	public String getSEP01() {
		return SEP01;
	}
	public void setSEP01(String sEP01) {
		SEP01 = sEP01;
	}
	public String getFUNC() {
		return FUNC;
	}
	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}
	public String getSEP02() {
		return SEP02;
	}
	public void setSEP02(String sEP02) {
		SEP02 = sEP02;
	}
	public String getCOD_01() {
		return COD_01;
	}
	public void setCOD_01(String cOD_01) {
		COD_01 = cOD_01;
	}
	public String getSEP03() {
		return SEP03;
	}
	public void setSEP03(String sEP03) {
		SEP03 = sEP03;
	}
	public String getCTRY_COD() {
		return CTRY_COD;
	}
	public void setCTRY_COD(String cTRY_COD) {
		CTRY_COD = cTRY_COD;
	}
	public String getSEP04() {
		return SEP04;
	}
	public void setSEP04(String sEP04) {
		SEP04 = sEP04;
	}
	public String getBUS_ZIP() {
		return BUS_ZIP;
	}
	public void setBUS_ZIP(String bUS_ZIP) {
		BUS_ZIP = bUS_ZIP;
	}
	public String getSEP05() {
		return SEP05;
	}
	public void setSEP05(String sEP05) {
		SEP05 = sEP05;
	}
	public String getENDKey() {
		return ENDKey;
	}
	public void setENDKey(String eNDKey) {
		ENDKey = eNDKey;
	}

	
	
}
