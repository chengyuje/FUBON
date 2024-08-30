package com.systex.jbranch.fubon.commons.esb.vo.ccm002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class CCM002OutputDetailsVO {
	@XmlElement
	private String ACCT_TYPE; //帳號類別
	@XmlElement
	private String PINKEY; //查詢KEY值
	@XmlElement
	private String CARD_TBBNCD; //BONS碼
	@XmlElement
	private String CARD_TBBNNM; //BONS說明
	@XmlElement
	private String CARD_BOUSAV; //紅利積點餘額
	
	public String getACCT_TYPE() {
		return ACCT_TYPE;
	}
	public void setACCT_TYPE(String aCCT_TYPE) {
		ACCT_TYPE = aCCT_TYPE;
	}
	public String getPINKEY() {
		return PINKEY;
	}
	public void setPINKEY(String pINKEY) {
		PINKEY = pINKEY;
	}
	public String getCARD_TBBNCD() {
		return CARD_TBBNCD;
	}
	public void setCARD_TBBNCD(String cARD_TBBNCD) {
		CARD_TBBNCD = cARD_TBBNCD;
	}
	public String getCARD_TBBNNM() {
		return CARD_TBBNNM;
	}
	public void setCARD_TBBNNM(String cARD_TBBNNM) {
		CARD_TBBNNM = cARD_TBBNNM;
	}
	public String getCARD_BOUSAV() {
		return CARD_BOUSAV;
	}
	public void setCARD_BOUSAV(String cARD_BOUSAV) {
		CARD_BOUSAV = cARD_BOUSAV;
	}

}