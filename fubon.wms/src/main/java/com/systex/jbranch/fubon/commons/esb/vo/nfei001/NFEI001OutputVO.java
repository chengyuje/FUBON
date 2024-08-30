package com.systex.jbranch.fubon.commons.esb.vo.nfei001;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFEI001OutputVO {
	@XmlElement
	private String SPRefId; // 傳送序號
	@XmlElement
	private String AcctId16; // 身分證ID
	@XmlElement
	private String Occur; // 資料比數
	@XmlElement
	private String EMSGID; // 錯誤ID
	@XmlElement
	private String EMSGTXT; //錯誤訊息
	@XmlElement(name = "TxRepeat")
	private List<NFEI001OutputDetailsVO> details;
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
	public String getOccur() {
		return Occur;
	}
	public void setOccur(String occur) {
		Occur = occur;
	}
	public String getEMSGID() {
		return EMSGID;
	}
	public void setEMSGID(String eMSGID) {
		EMSGID = eMSGID;
	}
	public String getEMSGTXT() {
		return EMSGTXT;
	}
	public void setEMSGTXT(String eMSGTXT) {
		EMSGTXT = eMSGTXT;
	}
	public List<NFEI001OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<NFEI001OutputDetailsVO> details) {
		this.details = details;
	}
	
	
	
	

}
