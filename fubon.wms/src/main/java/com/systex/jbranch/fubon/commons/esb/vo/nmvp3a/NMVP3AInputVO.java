package com.systex.jbranch.fubon.commons.esb.vo.nmvp3a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP3AInputVO {
	@XmlElement
	private String FUNCTION;	//功能碼
	@XmlElement
	private String TYPE;	//預留
	@XmlElement
	private String NMNO;	//契約編號
	@XmlElement
	private String UNIT;	//金融單位
	
	public String getFUNCTION() {
		return FUNCTION;
	}
	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getNMNO() {
		return NMNO;
	}
	public void setNMNO(String nMNO) {
		NMNO = nMNO;
	}
	public String getUNIT() {
		return UNIT;
	}
	public void setUNIT(String uNIT) {
		UNIT = uNIT;
	}
	
}