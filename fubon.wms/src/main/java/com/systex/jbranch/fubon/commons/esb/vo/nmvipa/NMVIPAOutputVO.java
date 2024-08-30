package com.systex.jbranch.fubon.commons.esb.vo.nmvipa;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVIPAOutputVO {
	@XmlElement
    private String FUNCTION;
	@XmlElement
    private String OCCUR;
	@XmlElement(name="TxRepeat")
	private List<NMVIPAOutputDetailsVO> details;
	
	
	public List<NMVIPAOutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<NMVIPAOutputDetailsVO> details) {
		this.details = details;
	}
	public String getFUNCTION() {
		return FUNCTION;
	}
	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}
	public String getOCCUR() {
		return OCCUR;
	}
	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}
	
	
}