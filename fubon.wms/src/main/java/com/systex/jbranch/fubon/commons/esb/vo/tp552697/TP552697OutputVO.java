package com.systex.jbranch.fubon.commons.esb.vo.tp552697;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import com.systex.jbranch.fubon.commons.esb.vo.tp552697.TP552697OutputDetailsVO;

/**
 * Created by Stella on 2017/01/05.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType

public class TP552697OutputVO {
	@XmlElement
	private String OCCUR;	//次數
	@XmlElement(name="TxRepeat")
	private List<TP552697OutputDetailsVO> details;
	
	public String getOCCUR() {
		return OCCUR;
	}
	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}
	public List<TP552697OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<TP552697OutputDetailsVO> details) {
		this.details = details;
	}
	
	
	
}
