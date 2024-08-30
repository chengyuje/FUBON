package com.systex.jbranch.fubon.commons.esb.vo.nmvp4a;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675OutputDetailsVO;

/**
 * 台外幣活存明細查詢
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP4AOutputVO {
	@XmlElement
	private String SPRefId;
	@XmlElement
	private String SETTLE_DATE;
	@XmlElement
	private String OCCUR;	
	@XmlElement(name="TxRepeat")
	private List<NMVP4AOutputDetailsVO> details;

	public String getSPRefId() {
		return SPRefId;
	}

	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}

	public String getSETTLE_DATE() {
		return SETTLE_DATE;
	}

	public void setSETTLE_DATE(String sETTLE_DATE) {
		SETTLE_DATE = sETTLE_DATE;
	}

	public String getOCCUR() {
		return OCCUR;
	}

	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}

	public List<NMVP4AOutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NMVP4AOutputDetailsVO> details) {
		this.details = details;
	}
	
}