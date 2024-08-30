package com.systex.jbranch.fubon.commons.esb.vo.eb202650;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/03/03.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class EB202650OutputDetailsVO {
	@XmlElement
	private String TX_DATE;		//交易日期
	@XmlElement
	private String TX_TIME;		//交易時間
	@XmlElement
	private String CK_NO;		//支票號碼
	@XmlElement
	private String MEMO;		//摘要
	@XmlElement
	private String DRCR;		//借貸註記
	@XmlElement
	private String TX_AMT;		//交易金額
	@XmlElement
	private String RMK; 		//附註
	@XmlElement
	private String PB_BAL;		//結餘金額
	@XmlElement
	private String TX_BRH;		//代辦行
	@XmlElement
	private String RECE_NAME;	//受款人戶名
	@XmlElement
	private String RECE_IDNO;	//受款人ID
	@XmlElement
	private String TX_SRL;		//交易序號
	@XmlElement
	private String ACT_DATE;	//帳務日期
	@XmlElement
	private String MEMO_T;		//摘要英文代碼
	@XmlElement
	private String CK_DTE_DATE;	//票據到期日
	@XmlElement
	private String TX_STS;		//沖正註記
	
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
	public String getCK_NO() {
		return CK_NO;
	}
	public void setCK_NO(String cK_NO) {
		CK_NO = cK_NO;
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
	public String getTX_AMT() {
		return TX_AMT;
	}
	public void setTX_AMT(String tX_AMT) {
		TX_AMT = tX_AMT;
	}
	public String getRMK() {
		return RMK;
	}
	public void setRMK(String rMK) {
		RMK = rMK;
	}
	public String getPB_BAL() {
		return PB_BAL;
	}
	public void setPB_BAL(String pB_BAL) {
		PB_BAL = pB_BAL;
	}
	public String getTX_BRH() {
		return TX_BRH;
	}
	public void setTX_BRH(String tX_BRH) {
		TX_BRH = tX_BRH;
	}
	public String getRECE_NAME() {
		return RECE_NAME;
	}
	public void setRECE_NAME(String rECE_NAME) {
		RECE_NAME = rECE_NAME;
	}
	public String getRECE_IDNO() {
		return RECE_IDNO;
	}
	public void setRECE_IDNO(String rECE_IDNO) {
		RECE_IDNO = rECE_IDNO;
	}
	public String getTX_SRL() {
		return TX_SRL;
	}
	public void setTX_SRL(String tX_SRL) {
		TX_SRL = tX_SRL;
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
	public String getCK_DTE_DATE() {
		return CK_DTE_DATE;
	}
	public void setCK_DTE_DATE(String cK_DTE_DATE) {
		CK_DTE_DATE = cK_DTE_DATE;
	}
	public String getTX_STS() {
		return TX_STS;
	}
	public void setTX_STS(String tX_STS) {
		TX_STS = tX_STS;
	}
	
}