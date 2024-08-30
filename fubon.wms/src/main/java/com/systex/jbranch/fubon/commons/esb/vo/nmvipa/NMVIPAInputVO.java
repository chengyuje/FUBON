package com.systex.jbranch.fubon.commons.esb.vo.nmvipa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVIPAInputVO {
	@XmlElement
	private String FUNCTION;	//功能碼
	@XmlElement
	private String RANGE;	//範圍
	@XmlElement
	private String CUSID;	//客戶ID
	@XmlElement
	private String UNIT;	//金融單位
	
	public String getFUNCTION() {
		return FUNCTION;
	}
	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}
	public String getRANGE() {
		return RANGE;
	}
	public void setRANGE(String rANGE) {
		RANGE = rANGE;
	}
	public String getCUSID() {
		return CUSID;
	}
	public void setCUSID(String cUSID) {
		CUSID = cUSID;
	}
	public String getUNIT() {
		return UNIT;
	}
	public void setUNIT(String uNIT) {
		UNIT = uNIT;
	}
	

}