package com.systex.jbranch.fubon.commons.esb.vo.eb172656;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/03/06.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class EB172656OutputDetailsVO {
	@XmlElement
	private String TX_DATE;		//交易日期
	
	@XmlElement
	private String TX_TIME;		//交易時間
	
	@XmlElement
	private String TX_CODE;		//交易代號
	
	@XmlElement
	private String MEMO;		//摘要
	
	@XmlElement
	private String DRCR;		//借貸
	
	@XmlElement
	private String CUR;			//幣別
	
	@XmlElement
	private String TX_AMT; 		//交易金額
	
	@XmlElement
	private String FS_BAL;		//存款餘額
	
	@XmlElement
	private String RMK;			//備註
	
	@XmlElement
	private String TX_BRH;		//代辦行
	
	@XmlElement
	private String TX_SRL;		//交易序號
	
	@XmlElement
	private String EMP_NO;		//櫃員代號
	
	@XmlElement
	private String ACT_DATE;	//帳務日期
	
	@XmlElement
	private String MEMO_T;		//摘要代碼

	public String getTX_DATE() {
		return TX_DATE;
	}

	public void setTX_DATE(String tX_DATE) {
		TX_DATE = tX_DATE;
	}

	public String getTX_TIME() {
		return TX_TIME;
	}

	public void setTX_TIME(String tX_TIME) {
		TX_TIME = tX_TIME;
	}

	public String getTX_CODE() {
		return TX_CODE;
	}

	public void setTX_CODE(String tX_CODE) {
		TX_CODE = tX_CODE;
	}

	public String getMEMO() {
		return MEMO;
	}

	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}

	public String getDRCR() {
		return DRCR;
	}

	public void setDRCR(String dRCR) {
		DRCR = dRCR;
	}

	public String getCUR() {
		return CUR;
	}

	public void setCUR(String cUR) {
		CUR = cUR;
	}

	public String getTX_AMT() {
		return TX_AMT;
	}

	public void setTX_AMT(String tX_AMT) {
		TX_AMT = tX_AMT;
	}

	public String getFS_BAL() {
		return FS_BAL;
	}

	public void setFS_BAL(String fS_BAL) {
		FS_BAL = fS_BAL;
	}

	public String getRMK() {
		return RMK;
	}

	public void setRMK(String rMK) {
		RMK = rMK;
	}

	public String getTX_BRH() {
		return TX_BRH;
	}

	public void setTX_BRH(String tX_BRH) {
		TX_BRH = tX_BRH;
	}

	public String getTX_SRL() {
		return TX_SRL;
	}

	public void setTX_SRL(String tX_SRL) {
		TX_SRL = tX_SRL;
	}

	public String getEMP_NO() {
		return EMP_NO;
	}

	public void setEMP_NO(String eMP_NO) {
		EMP_NO = eMP_NO;
	}

	public String getACT_DATE() {
		return ACT_DATE;
	}

	public void setACT_DATE(String aCT_DATE) {
		ACT_DATE = aCT_DATE;
	}

	public String getMEMO_T() {
		return MEMO_T;
	}

	public void setMEMO_T(String mEMO_T) {
		MEMO_T = mEMO_T;
	}
	
}