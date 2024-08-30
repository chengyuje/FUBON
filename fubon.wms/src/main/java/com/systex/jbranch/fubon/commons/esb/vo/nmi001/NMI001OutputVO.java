package com.systex.jbranch.fubon.commons.esb.vo.nmi001;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMI001OutputVO {
	@XmlElement
	private String MSGID;   	// 錯誤碼
	
	@XmlElement
	private String MSGTXT;    	// 錯誤訊息
	
    @XmlElement(name="TxRepeat")
    private List<NMI001OutputVODetails> details;
    
	public String getMSGID() {
		return MSGID;
	}

	public void setMSGID(String mSGID) {
		MSGID = mSGID;
	}

	public String getMSGTXT() {
		return MSGTXT;
	}

	public void setMSGTXT(String mSGTXT) {
		MSGTXT = mSGTXT;
	}

	public List<NMI001OutputVODetails> getDetails() {
		return details;
	}
	
	public void setDetails(List<NMI001OutputVODetails> details) {
		this.details = details;
	}
}
