package com.systex.jbranch.fubon.commons.esb.vo.hd00070000;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 交易明細歷史查詢 HFMTID
 * 
 * @author sam
 * 2020.07.21
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class HD00070000InputVO {
	@XmlElement
	private String PAGE_NUM; // 起始資料頁數
	@XmlElement
	private String PAGE_SIZE; // 每頁回傳筆數
	@XmlElement
	private String FUNC; // 功能
	@XmlElement
	private String ITEM; // 選項
	@XmlElement
	private String ACNO_SA; // 存款帳號
	@XmlElement
	private String TX_TYPE; // "交易類別	1-全部(台活+外活)	2-台幣活期	3-台幣定期	4-外幣活期	5-外幣定期"
	@XmlElement
	private String CKSNO; // 支票號碼
	@XmlElement
	private String TXDATES; // 起迄日期(起) YYYYMMDD
	@XmlElement
	private String TXDATEE; // 起迄日期(迄) YYYYMMDD
	@XmlElement
	private String JRNL_NO; // 執行序號
	@XmlElement
	private String RPT_NO; // 報表編號
	@XmlElement
	private String RPT_BRH; // 報表分行
	@XmlElement
	private String CUST_NM; // 客戶名稱

	public String getPAGE_NUM() {
		return PAGE_NUM;
	}

	public void setPAGE_NUM(String pAGE_NUM) {
		PAGE_NUM = pAGE_NUM;
	}

	public String getPAGE_SIZE() {
		return PAGE_SIZE;
	}

	public void setPAGE_SIZE(String pAGE_SIZE) {
		PAGE_SIZE = pAGE_SIZE;
	}

	public String getFUNC() {
		return FUNC;
	}

	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}

	public String getITEM() {
		return ITEM;
	}

	public void setITEM(String iTEM) {
		ITEM = iTEM;
	}

	public String getACNO_SA() {
		return ACNO_SA;
	}

	public void setACNO_SA(String aCNO_SA) {
		ACNO_SA = aCNO_SA;
	}

	public String getTX_TYPE() {
		return TX_TYPE;
	}

	public void setTX_TYPE(String tX_TYPE) {
		TX_TYPE = tX_TYPE;
	}

	public String getCKSNO() {
		return CKSNO;
	}

	public void setCKSNO(String cKSNO) {
		CKSNO = cKSNO;
	}

	public String getTXDATES() {
		return TXDATES;
	}

	public void setTXDATES(String tXDATES) {
		TXDATES = tXDATES;
	}

	public String getTXDATEE() {
		return TXDATEE;
	}

	public void setTXDATEE(String tXDATEE) {
		TXDATEE = tXDATEE;
	}

	public String getJRNL_NO() {
		return JRNL_NO;
	}

	public void setJRNL_NO(String jRNL_NO) {
		JRNL_NO = jRNL_NO;
	}

	public String getRPT_NO() {
		return RPT_NO;
	}

	public void setRPT_NO(String rPT_NO) {
		RPT_NO = rPT_NO;
	}

	public String getRPT_BRH() {
		return RPT_BRH;
	}

	public void setRPT_BRH(String rPT_BRH) {
		RPT_BRH = rPT_BRH;
	}

	public String getCUST_NM() {
		return CUST_NM;
	}

	public void setCUST_NM(String cUST_NM) {
		CUST_NM = cUST_NM;
	}

}
