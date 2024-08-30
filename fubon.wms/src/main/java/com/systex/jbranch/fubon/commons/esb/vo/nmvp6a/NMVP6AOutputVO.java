package com.systex.jbranch.fubon.commons.esb.vo.nmvp6a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP6AOutputVO {
	
	@XmlElement
	private String SPRefId;
	
	@XmlElement
	private String CHKOUT_LASTDAY;
	
	@XmlElement
	private String OCCUR;
	
	@XmlElement
	private String GUARDIANSHIP_FLAG; // 空白：無監護輔助 1.監護宣告 2輔助宣告

	
	@XmlElement(name="TxRepeat")
	private List<NMVP6AOutputDetailsVO> details;

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
	
	

	public String getGUARDIANSHIP_FLAG() {
		return GUARDIANSHIP_FLAG;
	}

	public void setGUARDIANSHIP_FLAG(String gUARDIANSHIP_FLAG) {
		GUARDIANSHIP_FLAG = gUARDIANSHIP_FLAG;
	}

	public List<NMVP6AOutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NMVP6AOutputDetailsVO> details) {
		this.details = details;
	}
	
}