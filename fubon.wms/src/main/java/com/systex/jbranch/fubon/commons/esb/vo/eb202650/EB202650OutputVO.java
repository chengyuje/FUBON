package com.systex.jbranch.fubon.commons.esb.vo.eb202650;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/03/03.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB202650OutputVO {
	
	@XmlElement(name="TxRepeat")
	private List<EB202650OutputDetailsVO> details;
	
	public List<EB202650OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<EB202650OutputDetailsVO> details) {
		this.details = details;
	}
	
	
}