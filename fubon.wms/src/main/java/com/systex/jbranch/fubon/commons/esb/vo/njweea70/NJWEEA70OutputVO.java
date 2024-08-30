package com.systex.jbranch.fubon.commons.esb.vo.njweea70;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJWEEA70OutputVO {
	@XmlElement
	private String MSGID;   	// 錯誤碼
	
	@XmlElement
	private String MSGTXT;    	// 錯誤訊息
	
	@XmlElement(name = "TxRepeat")
	private List<NJWEEA70OutputDetailVO> details;

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

	public List<NJWEEA70OutputDetailVO> getDetails() {
        return details;
    }

    public void setDetails(List<NJWEEA70OutputDetailVO> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "NJWEEA7OutputVO{details=" + details +'}';
    }
}