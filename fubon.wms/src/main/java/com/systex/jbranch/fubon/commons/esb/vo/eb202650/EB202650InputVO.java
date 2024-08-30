package com.systex.jbranch.fubon.commons.esb.vo.eb202650;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/03/03.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB202650InputVO {
	@XmlElement
	private String FUNC_COD;	//功能
	@XmlElement
	private String ITEM;		//項目
	@XmlElement
	private String ACNO_SA;		//存款帳號
	@XmlElement
	private String STA_DATE;	//起日
	@XmlElement
	private String END_DATE;	//迄日
	
	public String getFUNC_COD() {
		return FUNC_COD;
	}
	public void setFUNC_COD(String fUNC_COD) {
		FUNC_COD = fUNC_COD;
	}
	public String getITEM() {
		return ITEM;
	}
	public void setITEM(String iTEM) {
		ITEM = iTEM;
	}
	public String getACNO_SA() {
		return ACNO_SA;
	}
	public void setACNO_SA(String aCNO_SA) {
		ACNO_SA = aCNO_SA;
	}
	public String getSTA_DATE() {
		return STA_DATE;
	}
	public void setSTA_DATE(String sTA_DATE) {
		STA_DATE = sTA_DATE;
	}
	public String getEND_DATE() {
		return END_DATE;
	}
	public void setEND_DATE(String eND_DATE) {
		END_DATE = eND_DATE;
	}
}