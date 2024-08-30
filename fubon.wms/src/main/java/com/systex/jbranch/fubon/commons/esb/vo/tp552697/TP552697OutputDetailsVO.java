package com.systex.jbranch.fubon.commons.esb.vo.tp552697;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;



/**
 * Created by Stella on 2017/01/05.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType

public class TP552697OutputDetailsVO {

	@XmlElement
	private String 	BUS_TYPE;     //業務別
	@XmlElement
	private String NAME_COD;      //戶名代碼
	@XmlElement
	private String USER_ID;       //使用者代碼
	@XmlElement
	private String STS;           //網銀語音狀態
	@XmlElement
	private String SIGN_STS;      //電子契約書基金註記
	@XmlElement
	private String TRAN_FLG;      //轉帳約定狀態
	public String getBUS_TYPE() {
		return BUS_TYPE;
	}
	public void setBUS_TYPE(String bUS_TYPE) {
		BUS_TYPE = bUS_TYPE;
	}
	public String getNAME_COD() {
		return NAME_COD;
	}
	public void setNAME_COD(String nAME_COD) {
		NAME_COD = nAME_COD;
	}
	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}
	public String getSTS() {
		return STS;
	}
	public void setSTS(String sTS) {
		STS = sTS;
	}
	public String getSIGN_STS() {
		return SIGN_STS;
	}
	public void setSIGN_STS(String sIGN_STS) {
		SIGN_STS = sIGN_STS;
	}
	public String getTRAN_FLG() {
		return TRAN_FLG;
	}
	public void setTRAN_FLG(String tRAN_FLG) {
		TRAN_FLG = tRAN_FLG;
	}

		
}
