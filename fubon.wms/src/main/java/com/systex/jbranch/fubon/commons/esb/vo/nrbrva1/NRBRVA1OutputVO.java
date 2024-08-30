package com.systex.jbranch.fubon.commons.esb.vo.nrbrva1;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVA1OutputVO {
	
	@XmlElement
	private String ERR_COD;	//錯誤碼
	
	@XmlElement
	private String ERR_TXT;	//錯誤訊息
	
	@XmlElement
	private String DEFAULT_FEE_RATE;	//表定手續費率

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

	public String getDEFAULT_FEE_RATE() {
		return DEFAULT_FEE_RATE;
	}

	public void setDEFAULT_FEE_RATE(String dEFAULT_FEE_RATE) {
		DEFAULT_FEE_RATE = dEFAULT_FEE_RATE;
	}
	
}