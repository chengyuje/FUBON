package com.systex.jbranch.fubon.commons.esb.vo.cew012r;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;



/**
 * Created by Stella on 2017/01/05.
 *  Modify by Stella on 2017/01/17.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType

public class CEW012ROutputVO {
	@XmlElement
	private String CNT; 
	@XmlElement(name="TxRepeat")
	private List<CEW012ROutputDetailsVO> details;
	
	public String getCNT() {
		return CNT;
	}
	public void setCNT(String cNT) {
		CNT = cNT;
	}
	public List<CEW012ROutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<CEW012ROutputDetailsVO> details) {
		this.details = details;
	}
	
}
