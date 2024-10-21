package com.systex.jbranch.fubon.commons.esb.vo.nmvp6a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NMVP6AOutputDetailsVO {

	@XmlElement
	private String CONTRACT_ID;

	@XmlElement
	private String ACC;

	@XmlElement
	private String CUR1;

	@XmlElement
	private String VALUE1;

	@XmlElement
	private String CUR2;

	@XmlElement
	private String VALUE2;

	@XmlElement
	private String CUR3;

	@XmlElement
	private String VALUE3;

	@XmlElement
	private String CUR4;

	@XmlElement
	private String VALUE4;

	@XmlElement
	private String CUR5;
	
	@XmlElement
	private String VALUE5;

	@XmlElement
	private String CONTRACT_P_TYPE;

	@XmlElement
	private String CONTRACT_SPE_FLAG;

	@XmlElement
	private String CREDIT_FLAG; //受益人滿55歲(Y/N)
	
	@XmlElement
	private String TRUST_PEOP_NUM;
	
	@XmlElement
	private String CONTRACT_END_DAY; //契約迄日
	
	public String getCONTRACT_END_DAY() {
		return CONTRACT_END_DAY;
	}

	public void setCONTRACT_END_DAY(String cONTRACT_END_DAY) {
		CONTRACT_END_DAY = cONTRACT_END_DAY;
	}

	public String getTRUST_PEOP_NUM() {
		return TRUST_PEOP_NUM;
	}

	public void setTRUST_PEOP_NUM(String tRUST_PEOP_NUM) {
		TRUST_PEOP_NUM = tRUST_PEOP_NUM;
	}

	public String getCONTRACT_ID() {
		return CONTRACT_ID;
	}

	public void setCONTRACT_ID(String cONTRACT_ID) {
		CONTRACT_ID = cONTRACT_ID;
	}

	public String getACC() {
		return ACC;
	}

	public void setACC(String aCC) {
		ACC = aCC;
	}

	public String getCUR1() {
		return CUR1;
	}

	public void setCUR1(String cUR1) {
		CUR1 = cUR1;
	}

	public String getVALUE1() {
		return VALUE1;
	}

	public void setVALUE1(String vALUE1) {
		VALUE1 = vALUE1;
	}

	public String getCUR2() {
		return CUR2;
	}

	public void setCUR2(String cUR2) {
		CUR2 = cUR2;
	}

	public String getVALUE2() {
		return VALUE2;
	}

	public void setVALUE2(String vALUE2) {
		VALUE2 = vALUE2;
	}

	public String getCUR3() {
		return CUR3;
	}

	public void setCUR3(String cUR3) {
		CUR3 = cUR3;
	}

	public String getVALUE3() {
		return VALUE3;
	}

	public void setVALUE3(String vALUE3) {
		VALUE3 = vALUE3;
	}

	public String getCUR4() {
		return CUR4;
	}

	public void setCUR4(String cUR4) {
		CUR4 = cUR4;
	}

	public String getVALUE4() {
		return VALUE4;
	}

	public void setVALUE4(String vALUE4) {
		VALUE4 = vALUE4;
	}

	public String getCUR5() {
		return CUR5;
	}

	public void setCUR5(String cUR5) {
		CUR5 = cUR5;
	}

	public String getVALUE5() {
		return VALUE5;
	}

	public void setVALUE5(String vALUE5) {
		VALUE5 = vALUE5;
	}

	public String getCONTRACT_P_TYPE() {
		return CONTRACT_P_TYPE;
	}

	public void setCONTRACT_P_TYPE(String cONTRACT_P_TYPE) {
		CONTRACT_P_TYPE = cONTRACT_P_TYPE;
	}

	public String getCONTRACT_SPE_FLAG() {
		return CONTRACT_SPE_FLAG;
	}

	public void setCONTRACT_SPE_FLAG(String cONTRACT_SPE_FLAG) {
		CONTRACT_SPE_FLAG = cONTRACT_SPE_FLAG;
	}

	public String getCREDIT_FLAG() {
		return CREDIT_FLAG;
	}

	public void setCREDIT_FLAG(String cREDIT_FLAG) {
		CREDIT_FLAG = cREDIT_FLAG;
	}



}