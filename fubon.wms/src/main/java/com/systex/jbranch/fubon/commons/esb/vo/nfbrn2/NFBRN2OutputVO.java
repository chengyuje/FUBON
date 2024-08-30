package com.systex.jbranch.fubon.commons.esb.vo.nfbrn2;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN2OutputVO {
    @XmlElement
	private String ERR_COD;           //錯誤碼
    @XmlElement
	private String ERR_TXT;          //錯誤訊息
    @XmlElement
	private String TX_FLG;           //是否可承作交易
    @XmlElement
	private String ERR_COD_1;          //錯誤碼1
    @XmlElement
	private String ERR_TXT_1;          //錯誤訊息1
    @XmlElement
	private String SHORT_1;          //是否為短線1
    @XmlElement
	private String ERR_COD_2;          //錯誤碼2
    @XmlElement
	private String ERR_TXT_2;          //錯誤訊息2    
    @XmlElement
	private String SHORT_2;          //是否為短線2
    
	public String getERR_COD() {
		return ERR_COD;
	}
	public void setERR_COD(String err_cod) {
		ERR_COD = err_cod;
	}
	public String getERR_TXT() {
		return ERR_TXT;
	}
	public void setERR_TXT(String err_txt) {
		ERR_TXT = err_txt;
	}
	public String getTX_FLG() {
		return TX_FLG;
	}
	public void setTX_FLG(String tx_flg) {
		TX_FLG = tx_flg;
	}
	public String getERR_COD_1() {
		return ERR_COD_1;
	}
	public void setERR_COD_1(String err_cod_1) {
		ERR_COD_1 = err_cod_1;
	}
	public String getERR_TXT_1() {
		return ERR_TXT_1;
	}
	public void setERR_TXT_1(String err_txt_1) {
		ERR_TXT_1 = err_txt_1;
	}
	public String getSHORT_1() {
		return SHORT_1;
	}
	public void setSHORT_1(String short_1) {
		SHORT_1 = short_1;
	}
	public String getERR_COD_2() {
		return ERR_COD_2;
	}
	public void setERR_COD_2(String err_cod_2) {
		ERR_COD_2 = err_cod_2;
	}
	public String getERR_TXT_2() {
		return ERR_TXT_2;
	}
	public void setERR_TXT_2(String err_txt_2) {
		ERR_TXT_2 = err_txt_2;
	}
	public String getSHORT_2() {
		return SHORT_2;
	}
	public void setSHORT_2(String short_2) {
		SHORT_2 = short_2;
	}
}
