package com.systex.jbranch.fubon.commons.esb.vo.nmvp9a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.esb.vo.nmvp7a.NMVP7AOutputDetailsVO;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP9AOutputVO {
	
	@XmlElement
	private String SPRefId;		//傳送序號
	@XmlElement
	private String OCCUR;		//資料筆數
	
	@XmlElement(name="TxRepeat")
	private List<NMVP9AOutputDetailsVO> details;

	public String getSPRefId() {
		return SPRefId;
	}

	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}

	public String getOCCUR() {
		return OCCUR;
	}

	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}

	public List<NMVP9AOutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NMVP9AOutputDetailsVO> details) {
		this.details = details;
	}
	
}