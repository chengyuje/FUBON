package com.systex.jbranch.fubon.commons.esb.vo.njbrvc2;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1OutputVODetials;

/**
 * 
 * 長效單查詢明細下行電文
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVC2OutputVO {
	@XmlElement
    private String SPRefId;   //傳送序號
	@XmlElement
    private String GtcNo;   //長效單號    
    @XmlElement
    private String Occur;   //筆數
    @XmlElement(name = "TxRepeat")
    private List<NJBRVC2OutputVODetails> details;
    
	public String getSPRefId() {
		return SPRefId;
	}
	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}
	public String getGtcNo() {
		return GtcNo;
	}
	public void setGtcNo(String gtcNo) {
		GtcNo = gtcNo;
	}
	public String getOccur() {
		return Occur;
	}
	public void setOccur(String occur) {
		Occur = occur;
	}
	public List<NJBRVC2OutputVODetails> getDetails() {
		return details;
	}
	public void setDetails(List<NJBRVC2OutputVODetails> details) {
		this.details = details;
	}   
    
}
