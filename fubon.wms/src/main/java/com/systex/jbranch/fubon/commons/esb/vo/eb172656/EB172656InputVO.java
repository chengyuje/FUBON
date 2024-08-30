package com.systex.jbranch.fubon.commons.esb.vo.eb172656;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/03/06.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB172656InputVO {
	@XmlElement
	private String FUNC_COD;	//功能
	@XmlElement
	private String ACNO_FS;		//存款帳號
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
	
	public String getACNO_FS() {
		return ACNO_FS;
	}
	
	public void setACNO_FS(String aCNO_FS) {
		ACNO_FS = aCNO_FS;
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