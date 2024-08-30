package com.systex.jbranch.fubon.commons.esb.vo.nrbrvc2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVC2OutputVO {
	
	@XmlElement
	private String ERR_COD;	//錯誤碼
	
	@XmlElement
	private String ERR_TXT;	//錯誤訊息
	
    @XmlElement
    private String TX_FLG;              //是否可承作交易

	public String getTX_FLG() {
		return TX_FLG;
	}

	public void setTX_FLG(String tX_FLG) {
		TX_FLG = tX_FLG;
	}

	public String getERR_COD() {
		return ERR_COD;
	}

	public void setERR_COD(String eRR_COD) {
		ERR_COD = eRR_COD;
	}

	public String getERR_TXT() {
		return ERR_TXT;
	}

	public void setERR_TXT(String eRR_TXT) {
		ERR_TXT = eRR_TXT;
	}
	

}