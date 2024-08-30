package com.systex.jbranch.fubon.commons.esb.vo.hd00070000;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 交易明細歷史查詢 HFMTID
 * 
 * @author sam
 * 2020.07.21
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class HD00070000OutputVO {
	
	@XmlElement(name="TxRepeat")
	private List<HD00070000OutputDetailsVO> details;

	public List<HD00070000OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<HD00070000OutputDetailsVO> details) {
		this.details = details;
	}
	
	
}
