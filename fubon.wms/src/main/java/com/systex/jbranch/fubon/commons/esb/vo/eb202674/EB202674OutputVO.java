package com.systex.jbranch.fubon.commons.esb.vo.eb202674;

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
public class EB202674OutputVO {
	@XmlElement
	private String CustName;
	@XmlElement
	private String SEX_FLG;
	@XmlElement
	private String BIRTH_DATE;
	@XmlElement
	private String VB_FLG;
	@XmlElement(name="TxRepeat")
	private List<EB202674OutputDetailsVO> details;
	
	
	public String getCustName() {
		return CustName;
	}
	public void setCustName(String custName) {
		CustName = custName;
	}
	public String getSEX_FLG() {
		return SEX_FLG;
	}
	public void setSEX_FLG(String sEX_FLG) {
		SEX_FLG = sEX_FLG;
	}
	public String getBIRTH_DATE() {
		return BIRTH_DATE;
	}
	public void setBIRTH_DATE(String bIRTH_DATE) {
		BIRTH_DATE = bIRTH_DATE;
	}
	public String getVB_FLG() {
		return VB_FLG;
	}
	public void setVB_FLG(String vB_FLG) {
		VB_FLG = vB_FLG;
	}
	public List<EB202674OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<EB202674OutputDetailsVO> details) {
		this.details = details;
	}
	
	
}