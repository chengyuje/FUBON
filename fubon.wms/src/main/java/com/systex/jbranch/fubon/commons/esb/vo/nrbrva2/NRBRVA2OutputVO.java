package com.systex.jbranch.fubon.commons.esb.vo.nrbrva2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVA2OutputVO {
	
	@XmlElement
	private String ERR_COD;	//錯誤碼
	
	@XmlElement
	private String ERR_TXT;	//錯誤訊息
	
	@XmlElement
	private String DEFAULT_BEST_FEE_RATE;	//預設最優的手續費率

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

	public String getDEFAULT_BEST_FEE_RATE() {
		return DEFAULT_BEST_FEE_RATE;
	}

	public void setDEFAULT_BEST_FEE_RATE(String dEFAULT_BEST_FEE_RATE) {
		DEFAULT_BEST_FEE_RATE = dEFAULT_BEST_FEE_RATE;
	}

	
}