package com.systex.jbranch.fubon.commons.esb.vo.fc032659;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Valentino on 2017/03/29.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class FC032659OutputDetailsVO {
	@XmlElement
	private String COD; // 代碼
	@XmlElement
	private String ACNO_CATG;// 帳號類別
	@XmlElement
	private String ACNO; // 往來帳號
	@XmlElement
	private String OPN_DATE; // 生效日
	@XmlElement
	private String CUST_NAME; // 戶名
	public String getCOD() {
		return COD;
	}
	public void setCOD(String cOD) {
		COD = cOD;
	}
	public String getACNO_CATG() {
		return ACNO_CATG;
	}
	public void setACNO_CATG(String aCNO_CATG) {
		ACNO_CATG = aCNO_CATG;
	}
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public String getOPN_DATE() {
		return OPN_DATE;
	}
	public void setOPN_DATE(String oPN_DATE) {
		OPN_DATE = oPN_DATE;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	} 
}