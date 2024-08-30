package com.systex.jbranch.fubon.commons.esb.vo.nfbrne;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 國外私募基金贖回修正單位數交易電文
 * NFBRNE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRNEInputVO {
	@XmlElement
	private String applyDate;
	@XmlElement
	private String effDate;
	@XmlElement
	private String keyinNo;
	@XmlElement
	private String custId;
	@XmlElement
	private String eviNum;
	@XmlElement
	private String prodId;
	@XmlElement
	private String unitNum;
	@XmlElement
	private String rdmNum;
	@XmlElement
	private String adjRdmNum;
	
	
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getEffDate() {
		return effDate;
	}
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}
	public String getKeyinNo() {
		return keyinNo;
	}
	public void setKeyinNo(String keyinNo) {
		this.keyinNo = keyinNo;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getEviNum() {
		return eviNum;
	}
	public void setEviNum(String eviNum) {
		this.eviNum = eviNum;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getUnitNum() {
		return unitNum;
	}
	public void setUnitNum(String unitNum) {
		this.unitNum = unitNum;
	}
	public String getRdmNum() {
		return rdmNum;
	}
	public void setRdmNum(String rdmNum) {
		this.rdmNum = rdmNum;
	}
	public String getAdjRdmNum() {
		return adjRdmNum;
	}
	public void setAdjRdmNum(String adjRdmNum) {
		this.adjRdmNum = adjRdmNum;
	}

}
