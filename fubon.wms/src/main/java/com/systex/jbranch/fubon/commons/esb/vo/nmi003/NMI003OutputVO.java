package com.systex.jbranch.fubon.commons.esb.vo.nmi003;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/02/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMI003OutputVO {	
	@XmlElement
	private String MSGID;   	// 錯誤碼
	@XmlElement
	private String MSGTXT;    	// 錯誤訊息
	
    @XmlElement(name="TxRepeat")
    private List<NMI003OutputVODetails> details;
       
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
	public List<NMI003OutputVODetails> getDetails() {
		return details;
	}
	public void setDetails(List<NMI003OutputVODetails> details) {
		this.details = details;
	}
	
}
