package com.systex.jbranch.fubon.commons.esb.vo.nrbrvc4;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVC4InputVO {
	
	@XmlElement
	private String EMP_ID;
	
	@XmlElement
	private String CUST_ID;
	
	@XmlElement
	private String APPLY_BEGIN_DATE;
	
	@XmlElement
	private String APPLY_END_DATE;

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getAPPLY_BEGIN_DATE() {
		return APPLY_BEGIN_DATE;
	}

	public void setAPPLY_BEGIN_DATE(String aPPLY_BEGIN_DATE) {
		APPLY_BEGIN_DATE = aPPLY_BEGIN_DATE;
	}

	public String getAPPLY_END_DATE() {
		return APPLY_END_DATE;
	}

	public void setAPPLY_END_DATE(String aPPLY_END_DATE) {
		APPLY_END_DATE = aPPLY_END_DATE;
	}

}