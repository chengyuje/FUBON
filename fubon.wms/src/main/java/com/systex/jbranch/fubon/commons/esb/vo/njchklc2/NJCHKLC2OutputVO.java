package com.systex.jbranch.fubon.commons.esb.vo.njchklc2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Jimmy on 2017/03/10.
 * 
 * 
 */


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJCHKLC2OutputVO {

	@XmlElement
	private String CHSOURCE;//來源別
	@XmlElement
	private String TXTYPE;//交易類別
	@XmlElement
	private String EMPID;//員工編號
	@XmlElement
	private String SHOW_FLG;//是否出無證照畫面
	@XmlElement
	private String ABEND_CODE;//錯誤碼
	@XmlElement
	private String RESULT_CHK;//符合不符合
	@XmlElement
	private String MEMO;//錯誤訊息或證照中文

	 
	public String getABEND_CODE() {
		return ABEND_CODE;
	}
	public String getRESULT_CHK() {
		return RESULT_CHK;
	}
	public String getMEMO() {
		return MEMO;
	}
	public void setABEND_CODE(String aBEND_CODE) {
		ABEND_CODE = aBEND_CODE;
	}
	public void setRESULT_CHK(String rESULT_CHK) {
		RESULT_CHK = rESULT_CHK;
	}
	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}
	public String getCHSOURCE() {
		return CHSOURCE;
	}
	public String getTXTYPE() {
		return TXTYPE;
	}
	public String getEMPID() {
		return EMPID;
	}
	public String getSHOW_FLG() {
		return SHOW_FLG;
	}
	public void setCHSOURCE(String cHSOURCE) {
		CHSOURCE = cHSOURCE;
	}
	public void setTXTYPE(String tXTYPE) {
		TXTYPE = tXTYPE;
	}
	public void setEMPID(String eMPID) {
		EMPID = eMPID;
	}
	public void setSHOW_FLG(String sHOW_FLG) {
		SHOW_FLG = sHOW_FLG;
	}
	
	
}
