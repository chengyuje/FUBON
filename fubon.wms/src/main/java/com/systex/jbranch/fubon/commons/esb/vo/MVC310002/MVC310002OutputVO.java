package com.systex.jbranch.fubon.commons.esb.vo.MVC310002;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SamTu on 2021/11/10.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class MVC310002OutputVO {
	@XmlElement
	private String CUST_ID; // 身份證統一編號/居留證號
	@XmlElement
	private String ID_TYPE; // ID種類
	@XmlElement
	private String CUST_NAME; // 中文戶名(全部全形)
	@XmlElement
	private String ENG_NAME; // 英文戶名(全部全形或全部半形)
	@XmlElement
	private String NEXT_KEY; // Next Key

	@XmlElement(name = "TxRepeat")
	private List<MVC310002OutputDetailsVO> details;

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getID_TYPE() {
		return ID_TYPE;
	}

	public void setID_TYPE(String iD_TYPE) {
		ID_TYPE = iD_TYPE;
	}

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public String getENG_NAME() {
		return ENG_NAME;
	}

	public void setENG_NAME(String eNG_NAME) {
		ENG_NAME = eNG_NAME;
	}

	public String getNEXT_KEY() {
		return NEXT_KEY;
	}

	public void setNEXT_KEY(String nEXT_KEY) {
		NEXT_KEY = nEXT_KEY;
	}

	public List<MVC310002OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<MVC310002OutputDetailsVO> details) {
		this.details = details;
	}
	
	

}
