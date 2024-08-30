package com.systex.jbranch.fubon.commons.esb.vo.nfei002;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFEI002OutputVO {
	@XmlElement
	private String SPRefId; // 傳送序號
	@XmlElement
	private String VNACID; // 身分證ID
	@XmlElement
	private String VNIDTY; // IDTYPE
	@XmlElement
	private String EMSGID; // 錯誤ID
	@XmlElement
	private String EMSGTXT; //錯誤訊息
	@XmlElement(name = "TxRepeat")
	private List<NFEI002OutputDetailsVO> details;
	
	public NFEI002OutputVO(){
		this.SPRefId ="";
		this.VNACID="";
		this.VNIDTY="";
		this.EMSGID="";
		this.EMSGTXT="";
				
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


	public String getSPRefId() {
		return SPRefId;
	}

	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}

	public String getVNACID() {
		return VNACID;
	}

	public void setVNACID(String vNACID) {
		VNACID = vNACID;
	}

	public String getVNIDTY() {
		return VNIDTY;
	}

	public void setVNIDTY(String vNIDTY) {
		VNIDTY = vNIDTY;
	}

	public List<NFEI002OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NFEI002OutputDetailsVO> details) {
		this.details = details;
	}

	
	

}
