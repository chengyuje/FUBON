package com.systex.jbranch.fubon.commons.esb.vo.wms552697;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;



/**
 * Created by SamTu on 2018/03/19.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType

public class WMS552697OutputVO {
	@XmlElement
	private String OCCUR;	//次數
	@XmlElement(name="TxRepeat")
	private List<WMS552697OutputDetailsVO> details;
	
	public String getOCCUR() {
		return OCCUR;
	}
	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}
	public List<WMS552697OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<WMS552697OutputDetailsVO> details) {
		this.details = details;
	}
	
	
	
}
