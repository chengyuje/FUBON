package com.systex.jbranch.fubon.commons.esb.vo.eb372602;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB372602InputVO {
	@XmlElement
	private String FUNC;		//功能碼
	@XmlElement
	private String ACNO_LN;		//授信帳號
	@XmlElement
	private String DOC_NO;		//文件編號
	@XmlElement
	private String DOC_SEQ;		//文件編號序號
	@XmlElement
	private String RCV_DATE;	//預定交易日
	@XmlElement
	private String INT_DATE;	//計息止日
	@XmlElement
	private String DETAIL_FLG;	//查詢明細
	@XmlElement
	private String PRN_AMT;		//預定還本金額
	@XmlElement
	private String FX_EX_RATE;	//匯率
	@XmlElement
	private String PRINT_TYPE;	//列印類別
	@XmlElement
	private String INT_CUR_COD;	//利息列印原幣
	@XmlElement
	private String PAY_PRD;		//列印期數
	@XmlElement
	private String ENDKEY;		//ENDKEY
	
	
	public String getFUNC() {
		return FUNC;
	}
	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}
	public String getACNO_LN() {
		return ACNO_LN;
	}
	public void setACNO_LN(String aCNO_LN) {
		ACNO_LN = aCNO_LN;
	}
	public String getDOC_NO() {
		return DOC_NO;
	}
	public void setDOC_NO(String dOC_NO) {
		DOC_NO = dOC_NO;
	}
	public String getDOC_SEQ() {
		return DOC_SEQ;
	}
	public void setDOC_SEQ(String dOC_SEQ) {
		DOC_SEQ = dOC_SEQ;
	}
	public String getRCV_DATE() {
		return RCV_DATE;
	}
	public void setRCV_DATE(String rCV_DATE) {
		RCV_DATE = rCV_DATE;
	}
	public String getINT_DATE() {
		return INT_DATE;
	}
	public void setINT_DATE(String iNT_DATE) {
		INT_DATE = iNT_DATE;
	}
	public String getDETAIL_FLG() {
		return DETAIL_FLG;
	}
	public void setDETAIL_FLG(String dETAIL_FLG) {
		DETAIL_FLG = dETAIL_FLG;
	}
	public String getPRN_AMT() {
		return PRN_AMT;
	}
	public void setPRN_AMT(String pRN_AMT) {
		PRN_AMT = pRN_AMT;
	}
	public String getFX_EX_RATE() {
		return FX_EX_RATE;
	}
	public void setFX_EX_RATE(String fX_EX_RATE) {
		FX_EX_RATE = fX_EX_RATE;
	}
	public String getPRINT_TYPE() {
		return PRINT_TYPE;
	}
	public void setPRINT_TYPE(String pRINT_TYPE) {
		PRINT_TYPE = pRINT_TYPE;
	}
	public String getINT_CUR_COD() {
		return INT_CUR_COD;
	}
	public void setINT_CUR_COD(String iNT_CUR_COD) {
		INT_CUR_COD = iNT_CUR_COD;
	}
	public String getPAY_PRD() {
		return PAY_PRD;
	}
	public void setPAY_PRD(String pAY_PRD) {
		PAY_PRD = pAY_PRD;
	}
	public String getENDKEY() {
		return ENDKEY;
	}
	public void setENDKEY(String eNDKEY) {
		ENDKEY = eNDKEY;
	}
}