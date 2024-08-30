package com.systex.jbranch.fubon.commons.esb.vo.nmvp3a;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP3AOutputVO {
	@XmlElement
	private String FUNCTION;
	@XmlElement
	private String TYPE;
	@XmlElement
	private String OCCUR;
	@XmlElement
	private String T0100;
	@XmlElement(name="TxRepeat")
	private List<NMVP3AOutputDetailsVO> details;
	
	
	public String getFUNCTION() {
		return FUNCTION;
	}
	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getOCCUR() {
		return OCCUR;
	}
	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}
	public String getT0100() {
		return T0100;
	}
	public void setT0100(String t0100) {
		T0100 = t0100;
	}
	public List<NMVP3AOutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<NMVP3AOutputDetailsVO> details) {
		this.details = details;
	}
	
	
}