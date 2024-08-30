package com.systex.jbranch.fubon.commons.esb.vo.nrbrq01;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/05/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRQ01OutputVO {
	
	@XmlElement
	private String SPRefId;			//傳送序號
	
	@XmlElement
	private String AcctId16;		//帳號
	
	@XmlElement
	private String Name;			//姓名
	
	@XmlElement
	private String SEX;				//性別 0:女 1:男 2:公司
	
	@XmlElement(name="TxRepeat")
	private List<NRBRQ01OutputDetailsVO> details;

	public String getSPRefId() {
		return SPRefId;
	}

	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}

	public String getAcctId16() {
		return AcctId16;
	}

	public void setAcctId16(String acctId16) {
		AcctId16 = acctId16;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getSEX() {
		return SEX;
	}

	public void setSEX(String sEX) {
		SEX = sEX;
	}

	public List<NRBRQ01OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NRBRQ01OutputDetailsVO> details) {
		this.details = details;
	}
}