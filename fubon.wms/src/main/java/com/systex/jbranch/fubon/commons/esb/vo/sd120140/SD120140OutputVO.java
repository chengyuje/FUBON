package com.systex.jbranch.fubon.commons.esb.vo.sd120140;

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
public class SD120140OutputVO {
	
	@XmlElement(name="TxRepeat")
	private List<SD120140OutputDetailsVO> details;
	
	public List<SD120140OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<SD120140OutputDetailsVO> details) {
		this.details = details;
	}
	
	
}