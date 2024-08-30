package com.systex.jbranch.fubon.commons.esb.vo.nfbrng;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 動態鎖利母基金加碼
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRNGOutputVO {
	@XmlElement
	private String ERR_COD;                 //錯誤碼
    @XmlElement
	private String ERR_TXT;                 //錯誤訊息
    @XmlElement
	private String FEE;						//手續費
	@XmlElement
	private String FEE_RATE;				//手續費率    
    
    
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
	public String getFEE() {
		return FEE;
	}
	public void setFEE(String fEE) {
		FEE = fEE;
	}
	public String getFEE_RATE() {
		return FEE_RATE;
	}
	public void setFEE_RATE(String fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}
	
}
