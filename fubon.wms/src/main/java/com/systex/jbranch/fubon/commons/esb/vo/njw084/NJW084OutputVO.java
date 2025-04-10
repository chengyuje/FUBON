package com.systex.jbranch.fubon.commons.esb.vo.njw084;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJW084OutputVO {
	@XmlElement
	private String SPRefId; // 傳送序號
	@XmlElement
	private String AcctId16; // 帳號
	@XmlElement
	private String Name; // 姓名
	@XmlElement
	private String SEX; // 性別
	@XmlElement
	private String Occur; // 資料筆數
	@XmlElement(name = "TxRepeat")
	private List<NJW084OutputDetailVO> details;
	

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

	public String getOccur() {
		return Occur;
	}

	public void setOccur(String occur) {
		Occur = occur;
	}

	public List<NJW084OutputDetailVO> getDetails() {
		return details;
	}

	public void setDetails(List<NJW084OutputDetailVO> details) {
		this.details = details;
	}

}
