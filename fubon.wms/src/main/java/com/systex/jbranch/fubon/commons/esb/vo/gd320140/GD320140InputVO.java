package com.systex.jbranch.fubon.commons.esb.vo.gd320140;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author sam
 * @date 2018/01/19
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
@Component
@Scope("request")
public class GD320140InputVO {
	@XmlElement //來源別(必要)
	private String SOURCE;	
	@XmlElement //交易別
	private String CATEGORY;	
	@XmlElement //交易分行
	private String BRH;	
	@XmlElement //交易櫃員
	private String USER;	
	@XmlElement //覆核主管
	private String DIRECTOR;	
	@XmlElement //黃金帳號
	private String ACNO;	
	@XmlElement //FLAG4
	private String FLAG4;	
	@XmlElement //CHANNEL ID
	private String CHANNEL;	
	@XmlElement //UUID
	private String UUID;	
	@XmlElement //功能
	private String FUNC_COD;	
	@XmlElement //客戶統編
	private String IDNO;	
	@XmlElement //歸戶代碼
	private String NAME_COD;	
	@XmlElement //是否傳送至結束
	private String Convey_END;	
	@XmlElement //每次傳送筆數
	private String Convey_NUMBER;
	@XmlElement //KEY
	private String KEY;
	
	public String getSOURCE() {
		return SOURCE;
	}
	public void setSOURCE(String sOURCE) {
		SOURCE = sOURCE;
	}
	public String getCATEGORY() {
		return CATEGORY;
	}
	public void setCATEGORY(String cATEGORY) {
		CATEGORY = cATEGORY;
	}
	public String getBRH() {
		return BRH;
	}
	public void setBRH(String bRH) {
		BRH = bRH;
	}
	public String getUSER() {
		return USER;
	}
	public void setUSER(String uSER) {
		USER = uSER;
	}
	public String getDIRECTOR() {
		return DIRECTOR;
	}
	public void setDIRECTOR(String dIRECTOR) {
		DIRECTOR = dIRECTOR;
	}
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public String getFLAG4() {
		return FLAG4;
	}
	public void setFLAG4(String fLAG4) {
		FLAG4 = fLAG4;
	}
	public String getCHANNEL() {
		return CHANNEL;
	}
	public void setCHANNEL(String cHANNEL) {
		CHANNEL = cHANNEL;
	}
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getFUNC_COD() {
		return FUNC_COD;
	}
	public void setFUNC_COD(String fUNC_COD) {
		FUNC_COD = fUNC_COD;
	}
	public String getIDNO() {
		return IDNO;
	}
	public void setIDNO(String iDNO) {
		IDNO = iDNO;
	}
	public String getNAME_COD() {
		return NAME_COD;
	}
	public void setNAME_COD(String nAME_COD) {
		NAME_COD = nAME_COD;
	}
	public String getConvey_END() {
		return Convey_END;
	}
	public void setConvey_END(String convey_END) {
		Convey_END = convey_END;
	}
	public String getConvey_NUMBER() {
		return Convey_NUMBER;
	}
	public void setConvey_NUMBER(String convey_NUMBER) {
		Convey_NUMBER = convey_NUMBER;
	}
	public String getKEY() {
		return KEY;
	}
	public void setKEY(String kEY) {
		KEY = kEY;
	}
	
	
	
	
}