package com.systex.jbranch.fubon.commons.esb.vo.sdprc09a;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.esb.vo.sdactq4.SDACTQ4OutputDetailsVO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDPRC09AOutputVO {
	@XmlElement(name = "TxRepeat")
    private List<SDPRC09AOutputDetailVO> details;

	public List<SDPRC09AOutputDetailVO> getDetails() {
		return details;
	}

	public void setDetails(List<SDPRC09AOutputDetailVO> details) {
		this.details = details;
	}
	
	
	
}
