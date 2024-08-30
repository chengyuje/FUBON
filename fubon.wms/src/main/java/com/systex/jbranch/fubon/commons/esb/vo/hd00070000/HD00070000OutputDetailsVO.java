package com.systex.jbranch.fubon.commons.esb.vo.hd00070000;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 交易明細歷史查詢 HFMTID
 * 
 * @author sam 2020.07.21
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class HD00070000OutputDetailsVO {

	// 下行_HD00070000_1
	@XmlElement
	private String ACT_DATE; // 1_帳務日
	@XmlElement
	private String TXDATE; // 1_交易日
	@XmlElement
	private String BK_VALUE; // 1_起息日
	@XmlElement
	private String MEMO; // 1_摘要
	@XmlElement
	private String CKSNO; // 1_支票號碼
	@XmlElement
	private String DEBIT_AMT; // 1_借方金額
	@XmlElement
	private String CREDIT_AMT; // 1_貸方金額
	@XmlElement
	private String ACT_BAL; // 1_結餘金額
	@XmlElement
	private String TX_TIME; // 1_交易時間
	@XmlElement
	private String EMP_NO; // 1_櫃員編號
	@XmlElement
	private String ACT_BRH; // 1_代辦行
	@XmlElement
	private String TX_SEQ; // 1_交易序號
	@XmlElement
	private String DRCR; // 1_借貸別   1出，借方   2入，貸方

	// 下行_HD00070000_4
	@XmlElement
	private String BRH; // 分行
	@XmlElement
	private String CK_NO; // 支票號碼
	@XmlElement
	private String PAY_DATE; // 兌付日
	@XmlElement
	private String TIC_AMT; // 票據金額
	@XmlElement
	private String TX_SRL; // 交易序號
	@XmlElement
	private String TIP_BRH; // 提示行
	@XmlElement
	private String TIP_BRH_NAME; // 提示名稱
	
	public String getACT_DATE() {
		return ACT_DATE;
	}
	public void setACT_DATE(String aCT_DATE) {
		ACT_DATE = aCT_DATE;
	}
	public String getTXDATE() {
		return TXDATE;
	}
	public void setTXDATE(String tXDATE) {
		TXDATE = tXDATE;
	}
	public String getBK_VALUE() {
		return BK_VALUE;
	}
	public void setBK_VALUE(String bK_VALUE) {
		BK_VALUE = bK_VALUE;
	}
	public String getMEMO() {
		return MEMO;
	}
	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}
	public String getCKSNO() {
		return CKSNO;
	}
	public void setCKSNO(String cKSNO) {
		CKSNO = cKSNO;
	}
	public String getDEBIT_AMT() {
		return DEBIT_AMT;
	}
	public void setDEBIT_AMT(String dEBIT_AMT) {
		DEBIT_AMT = dEBIT_AMT;
	}
	public String getCREDIT_AMT() {
		return CREDIT_AMT;
	}
	public void setCREDIT_AMT(String cREDIT_AMT) {
		CREDIT_AMT = cREDIT_AMT;
	}
	public String getACT_BAL() {
		return ACT_BAL;
	}
	public void setACT_BAL(String aCT_BAL) {
		ACT_BAL = aCT_BAL;
	}
	public String getTX_TIME() {
		return TX_TIME;
	}
	public void setTX_TIME(String tX_TIME) {
		TX_TIME = tX_TIME;
	}
	public String getEMP_NO() {
		return EMP_NO;
	}
	public void setEMP_NO(String eMP_NO) {
		EMP_NO = eMP_NO;
	}
	public String getACT_BRH() {
		return ACT_BRH;
	}
	public void setACT_BRH(String aCT_BRH) {
		ACT_BRH = aCT_BRH;
	}
	public String getTX_SEQ() {
		return TX_SEQ;
	}
	public void setTX_SEQ(String tX_SEQ) {
		TX_SEQ = tX_SEQ;
	}
	public String getDRCR() {
		return DRCR;
	}
	public void setDRCR(String dRCR) {
		DRCR = dRCR;
	}
	public String getBRH() {
		return BRH;
	}
	public void setBRH(String bRH) {
		BRH = bRH;
	}
	public String getCK_NO() {
		return CK_NO;
	}
	public void setCK_NO(String cK_NO) {
		CK_NO = cK_NO;
	}
	public String getPAY_DATE() {
		return PAY_DATE;
	}
	public void setPAY_DATE(String pAY_DATE) {
		PAY_DATE = pAY_DATE;
	}
	public String getTIC_AMT() {
		return TIC_AMT;
	}
	public void setTIC_AMT(String tIC_AMT) {
		TIC_AMT = tIC_AMT;
	}
	public String getTX_SRL() {
		return TX_SRL;
	}
	public void setTX_SRL(String tX_SRL) {
		TX_SRL = tX_SRL;
	}
	public String getTIP_BRH() {
		return TIP_BRH;
	}
	public void setTIP_BRH(String tIP_BRH) {
		TIP_BRH = tIP_BRH;
	}
	public String getTIP_BRH_NAME() {
		return TIP_BRH_NAME;
	}
	public void setTIP_BRH_NAME(String tIP_BRH_NAME) {
		TIP_BRH_NAME = tIP_BRH_NAME;
	}
	
	

}
