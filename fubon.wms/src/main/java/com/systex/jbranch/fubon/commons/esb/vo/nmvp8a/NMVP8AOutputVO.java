package com.systex.jbranch.fubon.commons.esb.vo.nmvp8a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.esb.vo.nmvp7a.NMVP7AOutputDetailsVO;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP8AOutputVO {
	
	@XmlElement
	private String SPRefId;		//傳送序號
	
	@XmlElement
	private String INS_TYPE;	//5.信託財產管理運用
	
	@XmlElement
	private String CON_TYPE;	//約定方式(約定多項時只給序號最小者) 1.委託人單獨  2.委託人＋全體信託監察人 3.特定信託監察人 4.其他契約約定 5.受益人＋全體信託監察人 6.全體信託監察人 7.授權第三人
	
	@XmlElement
	private String OCCUR;		//資料筆數
	
	@XmlElement(name="TxRepeat")
	private List<NMVP8AOutputDetailsVO> details;

	public String getSPRefId() {
		return SPRefId;
	}

	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}

	public String getINS_TYPE() {
		return INS_TYPE;
	}

	public void setINS_TYPE(String iNS_TYPE) {
		INS_TYPE = iNS_TYPE;
	}

	public String getCON_TYPE() {
		return CON_TYPE;
	}

	public void setCON_TYPE(String cON_TYPE) {
		CON_TYPE = cON_TYPE;
	}

	public String getOCCUR() {
		return OCCUR;
	}

	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}

	public List<NMVP8AOutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NMVP8AOutputDetailsVO> details) {
		this.details = details;
	}
	
}