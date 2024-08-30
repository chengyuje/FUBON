package com.systex.jbranch.fubon.commons.esb.vo.cew012r;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;


/**
 * Created by Stella on 2017/01/05.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CEW012RInputVO {
	@XmlElement
	private String CUS_ID;     //客戶ID
	@XmlElement
	private String CUS_BTH;    //客戶生日
	
	
	
	public String getCUS_ID() {
		return CUS_ID;
	}
	public void setCUS_ID(String cUS_ID) {
		CUS_ID = cUS_ID;
	}
	public String getCUS_BTH() {
		return CUS_BTH;
	}
	public void setCUS_BTH(String cUS_BTH) {
		CUS_BTH = cUS_BTH;
	}
	
	
}
