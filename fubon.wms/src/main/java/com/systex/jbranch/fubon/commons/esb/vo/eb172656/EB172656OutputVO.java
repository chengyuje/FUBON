package com.systex.jbranch.fubon.commons.esb.vo.eb172656;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/03/06.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB172656OutputVO {
	@XmlElement
	private String ACNO_FS;		//帳號
	
	@XmlElement
	private String STA_DATE;	//查詢起日
	
	@XmlElement
	private String END_DATE;	//查詢迄日
	
	@XmlElement(name="TxRepeat")
	private List<EB172656OutputDetailsVO> details;

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

	public List<EB172656OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<EB172656OutputDetailsVO> details) {
		this.details = details;
	}
		
}