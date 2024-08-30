package com.systex.jbranch.fubon.commons.esb.vo.eb12020002;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/08.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB12020002OutputVO {
	@XmlElement
	private String MSG_COD;	//回傳代碼
	@XmlElement
	private String MSG_TXT;	//訊息中文代碼
	@XmlElement(name="TxRepeat")
	private List<EB12020002OutputDetailsVO> details;


	public String getMSG_COD() {
		return MSG_COD;
	}

	public void setMSG_COD(String mSG_COD) {
		MSG_COD = mSG_COD;
	}

	public String getMSG_TXT() {
		return MSG_TXT;
	}

	public void setMSG_TXT(String mSG_TXT) {
		MSG_TXT = mSG_TXT;
	}

	public List<EB12020002OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<EB12020002OutputDetailsVO> details) {
		this.details = details;
	}
	
	
}