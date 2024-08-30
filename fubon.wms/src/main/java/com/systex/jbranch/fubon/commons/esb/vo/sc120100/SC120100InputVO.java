package com.systex.jbranch.fubon.commons.esb.vo.sc120100;

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
public class SC120100InputVO {
	
	//2018.05.11 變數變動 by SamTu
	@XmlElement
	private String CUST_NO;
	@XmlElement
	private String IDTYPE;
	@XmlElement
	private String ACNO;
	@XmlElement
	private String FUNC_COD;
	@XmlElement
	private String DATA1;
	
	public String getDATA1() {
		return DATA1;
	}
	public void setDATA1(String dATA1) {
		DATA1 = dATA1;
	}
	public String getCUST_NO() {
		return CUST_NO;
	}
	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}
	public String getIDTYPE() {
		return IDTYPE;
	}
	public void setIDTYPE(String iDTYPE) {
		IDTYPE = iDTYPE;
	}
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public String getFUNC_COD() {
		return FUNC_COD;
	}
	public void setFUNC_COD(String fUNC_COD) {
		FUNC_COD = fUNC_COD;
	}


	
}