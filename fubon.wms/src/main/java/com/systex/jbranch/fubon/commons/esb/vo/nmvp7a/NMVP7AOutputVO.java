package com.systex.jbranch.fubon.commons.esb.vo.nmvp7a;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.esb.vo.nmvp6a.NMVP6AOutputDetailsVO;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP7AOutputVO {
	
	@XmlElement
	private String SPRefId;
	
	@XmlElement
	private String CHKOUT_LASTDAY;
	
	@XmlElement
	private String OCCUR;
	
	@XmlElement(name="TxRepeat")
	private List<NMVP7AOutputDetailsVO> details;

	public String getSPRefId() {
		return SPRefId;
	}

	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}

	public String getCHKOUT_LASTDAY() {
		return CHKOUT_LASTDAY;
	}

	public void setCHKOUT_LASTDAY(String cHKOUT_LASTDAY) {
		CHKOUT_LASTDAY = cHKOUT_LASTDAY;
	}

	public String getOCCUR() {
		return OCCUR;
	}

	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}

	public List<NMVP7AOutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NMVP7AOutputDetailsVO> details) {
		this.details = details;
	}

}