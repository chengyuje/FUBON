package com.systex.jbranch.fubon.commons.esb.vo.nb052650;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/18.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NB052650InputVO {
	@XmlElement
	private String I_FUNC_SEL; // 功能
	@XmlElement
	private String I_CUST_NO; // 客戶ID
	
	
	@XmlElement
	private String FUNC_SEL; // 功能
	@XmlElement
	private String SEP01; // FKey
	@XmlElement
	private String SEP02; // FKey
	@XmlElement
	private String INQ_STR_DATE; // 查詢起日
	@XmlElement
	private String SEP03; // FKey
	@XmlElement
	private String INQ_END_DATE; // 查詢迄日
	@XmlElement
	private String SEP04; // FKey
	@XmlElement
	private String ENDKEY; // FKey

	



	public String getI_CUST_NO() {
		return I_CUST_NO;
	}

	public void setI_CUST_NO(String i_CUST_NO) {
		I_CUST_NO = i_CUST_NO;
	}

	public String getI_FUNC_SEL() {
		return I_FUNC_SEL;
	}

	public void setI_FUNC_SEL(String i_FUNC_SEL) {
		I_FUNC_SEL = i_FUNC_SEL;
	}

	public String getFUNC_SEL() {
		return FUNC_SEL;
	}

	public void setFUNC_SEL(String fUNC_SEL) {
		FUNC_SEL = fUNC_SEL;
	}


	public String getINQ_STR_DATE() {
		return INQ_STR_DATE;
	}

	public void setINQ_STR_DATE(String iNQ_STR_DATE) {
		INQ_STR_DATE = iNQ_STR_DATE;
	}

	public String getINQ_END_DATE() {
		return INQ_END_DATE;
	}

	public void setINQ_END_DATE(String iNQ_END_DATE) {
		INQ_END_DATE = iNQ_END_DATE;
	}

	public String getSEP01() {
		return SEP01;
	}

	public void setSEP01(String SEP01) {
		this.SEP01 = SEP01;
	}

	public String getSEP02() {
		return SEP02;
	}

	public void setSEP02(String SEP02) {
		this.SEP02 = SEP02;
	}

	public String getSEP03() {
		return SEP03;
	}

	public void setSEP03(String SEP03) {
		this.SEP03 = SEP03;
	}

	public String getSEP04() {
		return SEP04;
	}

	public void setSEP04(String SEP04) {
		this.SEP04 = SEP04;
	}

	public String getENDKEY() {
		return ENDKEY;
	}

	public void setENDKEY(String ENDKEY) {
		this.ENDKEY = ENDKEY;
	}
}
