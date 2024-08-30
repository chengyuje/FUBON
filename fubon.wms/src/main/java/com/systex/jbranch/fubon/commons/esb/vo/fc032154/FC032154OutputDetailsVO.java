package com.systex.jbranch.fubon.commons.esb.vo.fc032154;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class FC032154OutputDetailsVO {
	@XmlElement
	private String COD; 		//電話代碼
	@XmlElement
	private String LOST_FLG; 	//失聯電話
	@XmlElement
	private String TEL_NO; 		//電話號碼
	@XmlElement
	private String EXT; 		//轉接
	@XmlElement
	private String TYP; 		//類別
	@XmlElement
	private String DAY_USE; 	//日間性質
	@XmlElement
	private String NIGHT_USE; 	//晚上性質
	@XmlElement
	private String VOICE_USE; 	//語音性質
	@XmlElement
	private String FAX_USE; 	//傳真性質
	@XmlElement
	private String MTN_DATE; 	//異動日期
	
	
	public String getCOD() {
		return COD;
	}
	public void setCOD(String cOD) {
		COD = cOD;
	}
	public String getLOST_FLG() {
		return LOST_FLG;
	}
	public void setLOST_FLG(String lOST_FLG) {
		LOST_FLG = lOST_FLG;
	}
	public String getTEL_NO() {
		return TEL_NO;
	}
	public void setTEL_NO(String tEL_NO) {
		TEL_NO = tEL_NO;
	}
	public String getEXT() {
		return EXT;
	}
	public void setEXT(String eXT) {
		EXT = eXT;
	}
	public String getTYP() {
		return TYP;
	}
	public void setTYP(String tYP) {
		TYP = tYP;
	}
	public String getDAY_USE() {
		return DAY_USE;
	}
	public void setDAY_USE(String dAY_USE) {
		DAY_USE = dAY_USE;
	}
	public String getNIGHT_USE() {
		return NIGHT_USE;
	}
	public void setNIGHT_USE(String nIGHT_USE) {
		NIGHT_USE = nIGHT_USE;
	}
	public String getVOICE_USE() {
		return VOICE_USE;
	}
	public void setVOICE_USE(String vOICE_USE) {
		VOICE_USE = vOICE_USE;
	}
	public String getFAX_USE() {
		return FAX_USE;
	}
	public void setFAX_USE(String fAX_USE) {
		FAX_USE = fAX_USE;
	}
	public String getMTN_DATE() {
		return MTN_DATE;
	}
	public void setMTN_DATE(String mTN_DATE) {
		MTN_DATE = mTN_DATE;
	}
	
	
}