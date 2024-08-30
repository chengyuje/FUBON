package com.systex.jbranch.fubon.commons.esb.vo.sc120100;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SC120100DetailOutputVO {
	//2018.05.11 變數變動 by SamTu
	@XmlElement
	private String IDNO;// 客戶ID
	
	@XmlElement
	private String ACNO;// 帳號
	
	@XmlElement
	private String TYPE;// 帳號類別
	
	@XmlElement
	private String WA_X_ATYPE;// 產品大類
	
	@XmlElement
	private String WA_X_ICAT;// 產品子類
	
	@XmlElement
	private String AVAL_AMT;// 可用餘額
	
	@XmlElement
	private String SLIP_NO;// 存單號碼
	
	@XmlElement
	private String CUR_COD;// 幣別
	
	@XmlElement
	private String ACT_STS;// 戶況 ( 台幣定存 )
	
	@XmlElement
	private String DOC_NO;// 文件編號 ( 放款 )
	
	@XmlElement
	private String DUE_DATE;// 到期日
	
	@XmlElement
	private String RATE;// 匯率 (改適用利率)
	
	@XmlElement
	private String CLASS;// 所屬板塊
	
	@XmlElement
	private String ACT_BAL;// 帳上餘額 ( 原幣 )
	
	@XmlElement
	private String ACT_BAL_NT;// 存款帳上餘額 / 貸款餘額 ( 折合台幣 )
	
	@XmlElement
	private String GLCD_LOAN;// 會計科目 ( 判斷是否為催呆帳戶 )
	
	@XmlElement
	private String ORI_LOAN_BAL;// 存款投資餘額或放款原貸款金額 ( 原幣 )
	
	@XmlElement
	private String LOAN_TERM;// 已繳期數
	
	@XmlElement
	private String LOAN_TYP;// 貸款類型
	
	@XmlElement
	private String WA_X_PROJ_CODE;// 專案代碼
	
	@XmlElement
	private String WA_LN_NATURE_TYPE;// 性質別
	
	@XmlElement
	private String ACNO_SA;// 自動扣繳帳號
	
	@XmlElement
	private String ACNO_IN_FLAG;// 限約定轉入帳號註記 (Y/N)
	
	@XmlElement
	private String TD_NO;// 存單號碼
	
	@XmlElement
	private String INT_CYCLE;// 應繳日
	
	@XmlElement
	private String INS_AMT;// 每期期金
	
	@XmlElement
	private String PRN_STR_DATE;// 就貸：開始還款日 企業貸款：交易日
	
	@XmlElement
	private String PART_RECV;// 部份銷帳
	
	@XmlElement
	private String ACH_FLG;// ACH 註記 0- 當日申請 1- 核印中 Y- 核印成功
	
	@XmlElement
	private String SPEC_STS;// 特殊戶況註記 Y/N
	
	@XmlElement
	private String DIGITAL_FLG;// 數位存款註記 Y/N
	
	@XmlElement
	private String PENL_STR_DATE;// 寬限期起算日
	
	@XmlElement
	private String PRN_ALW_DUR;// 寬限期
	
	@XmlElement
	private String PENL_TERMS;// 綁約到期日
	
	@XmlElement
	private String INT_RATE_SPD;// 利率加碼碼距
	
	@XmlElement
	private String INT_RATE_TYP;// 利率基準
	
	@XmlElement
	private String INT_LN_TYP;// 利率性質別
	
	@XmlElement
	private String INT_ADJ_DUR;// 利率調整週期
	
	@XmlElement
	private String RECLOAN_AVAL_AMT;// 循環型貸款(回復式)可用額度

	@XmlElement
	private String ACHIVE_ORG_NM;// 進件業務單位名稱
	
	public String getACHIVE_ORG_NM() {
		return ACHIVE_ORG_NM;
	}

	public void setACHIVE_ORG_NM(String aCHIVE_ORG_NM) {
		ACHIVE_ORG_NM = aCHIVE_ORG_NM;
	}

	public String getIDNO() {
		return IDNO;
	}

	public void setIDNO(String iDNO) {
		IDNO = iDNO;
	}

	public String getACNO() {
		return ACNO;
	}

	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getWA_X_ATYPE() {
		return WA_X_ATYPE;
	}

	public void setWA_X_ATYPE(String wA_X_ATYPE) {
		WA_X_ATYPE = wA_X_ATYPE;
	}

	public String getWA_X_ICAT() {
		return WA_X_ICAT;
	}

	public void setWA_X_ICAT(String wA_X_ICAT) {
		WA_X_ICAT = wA_X_ICAT;
	}

	public String getAVAL_AMT() {
		return AVAL_AMT;
	}

	public void setAVAL_AMT(String aVAL_AMT) {
		AVAL_AMT = aVAL_AMT;
	}

	public String getSLIP_NO() {
		return SLIP_NO;
	}

	public void setSLIP_NO(String sLIP_NO) {
		SLIP_NO = sLIP_NO;
	}

	public String getCUR_COD() {
		return CUR_COD;
	}

	public void setCUR_COD(String cUR_COD) {
		CUR_COD = cUR_COD;
	}

	public String getACT_STS() {
		return ACT_STS;
	}

	public void setACT_STS(String aCT_STS) {
		ACT_STS = aCT_STS;
	}

	public String getDOC_NO() {
		return DOC_NO;
	}

	public void setDOC_NO(String dOC_NO) {
		DOC_NO = dOC_NO;
	}

	public String getDUE_DATE() {
		return DUE_DATE;
	}

	public void setDUE_DATE(String dUE_DATE) {
		DUE_DATE = dUE_DATE;
	}

	public String getRATE() {
		return RATE;
	}

	public void setRATE(String rATE) {
		RATE = rATE;
	}

	public String getCLASS() {
		return CLASS;
	}

	public void setCLASS(String cLASS) {
		CLASS = cLASS;
	}

	public String getACT_BAL() {
		return ACT_BAL;
	}

	public void setACT_BAL(String aCT_BAL) {
		ACT_BAL = aCT_BAL;
	}

	public String getACT_BAL_NT() {
		return ACT_BAL_NT;
	}

	public void setACT_BAL_NT(String aCT_BAL_NT) {
		ACT_BAL_NT = aCT_BAL_NT;
	}

	public String getGLCD_LOAN() {
		return GLCD_LOAN;
	}

	public void setGLCD_LOAN(String gLCD_LOAN) {
		GLCD_LOAN = gLCD_LOAN;
	}

	public String getORI_LOAN_BAL() {
		return ORI_LOAN_BAL;
	}

	public void setORI_LOAN_BAL(String oRI_LOAN_BAL) {
		ORI_LOAN_BAL = oRI_LOAN_BAL;
	}

	public String getLOAN_TERM() {
		return LOAN_TERM;
	}

	public void setLOAN_TERM(String lOAN_TERM) {
		LOAN_TERM = lOAN_TERM;
	}

	public String getLOAN_TYP() {
		return LOAN_TYP;
	}

	public void setLOAN_TYP(String lOAN_TYP) {
		LOAN_TYP = lOAN_TYP;
	}

	public String getWA_X_PROJ_CODE() {
		return WA_X_PROJ_CODE;
	}

	public void setWA_X_PROJ_CODE(String wA_X_PROJ_CODE) {
		WA_X_PROJ_CODE = wA_X_PROJ_CODE;
	}

	public String getWA_LN_NATURE_TYPE() {
		return WA_LN_NATURE_TYPE;
	}

	public void setWA_LN_NATURE_TYPE(String wA_LN_NATURE_TYPE) {
		WA_LN_NATURE_TYPE = wA_LN_NATURE_TYPE;
	}

	public String getACNO_SA() {
		return ACNO_SA;
	}

	public void setACNO_SA(String aCNO_SA) {
		ACNO_SA = aCNO_SA;
	}

	public String getACNO_IN_FLAG() {
		return ACNO_IN_FLAG;
	}

	public void setACNO_IN_FLAG(String aCNO_IN_FLAG) {
		ACNO_IN_FLAG = aCNO_IN_FLAG;
	}

	public String getTD_NO() {
		return TD_NO;
	}

	public void setTD_NO(String tD_NO) {
		TD_NO = tD_NO;
	}

	public String getINT_CYCLE() {
		return INT_CYCLE;
	}

	public void setINT_CYCLE(String iNT_CYCLE) {
		INT_CYCLE = iNT_CYCLE;
	}

	public String getINS_AMT() {
		return INS_AMT;
	}

	public void setINS_AMT(String iNS_AMT) {
		INS_AMT = iNS_AMT;
	}

	public String getPRN_STR_DATE() {
		return PRN_STR_DATE;
	}

	public void setPRN_STR_DATE(String pRN_STR_DATE) {
		PRN_STR_DATE = pRN_STR_DATE;
	}

	public String getPART_RECV() {
		return PART_RECV;
	}

	public void setPART_RECV(String pART_RECV) {
		PART_RECV = pART_RECV;
	}

	public String getACH_FLG() {
		return ACH_FLG;
	}

	public void setACH_FLG(String aCH_FLG) {
		ACH_FLG = aCH_FLG;
	}

	public String getSPEC_STS() {
		return SPEC_STS;
	}

	public void setSPEC_STS(String sPEC_STS) {
		SPEC_STS = sPEC_STS;
	}

	public String getDIGITAL_FLG() {
		return DIGITAL_FLG;
	}

	public void setDIGITAL_FLG(String dIGITAL_FLG) {
		DIGITAL_FLG = dIGITAL_FLG;
	}

	public String getPENL_STR_DATE() {
		return PENL_STR_DATE;
	}

	public void setPENL_STR_DATE(String pENL_STR_DATE) {
		PENL_STR_DATE = pENL_STR_DATE;
	}

	public String getPRN_ALW_DUR() {
		return PRN_ALW_DUR;
	}

	public void setPRN_ALW_DUR(String pRN_ALW_DUR) {
		PRN_ALW_DUR = pRN_ALW_DUR;
	}

	public String getPENL_TERMS() {
		return PENL_TERMS;
	}

	public void setPENL_TERMS(String pENL_TERMS) {
		PENL_TERMS = pENL_TERMS;
	}

	public String getINT_RATE_SPD() {
		return INT_RATE_SPD;
	}

	public void setINT_RATE_SPD(String iNT_RATE_SPD) {
		INT_RATE_SPD = iNT_RATE_SPD;
	}

	public String getINT_RATE_TYP() {
		return INT_RATE_TYP;
	}

	public void setINT_RATE_TYP(String iNT_RATE_TYP) {
		INT_RATE_TYP = iNT_RATE_TYP;
	}

	public String getINT_LN_TYP() {
		return INT_LN_TYP;
	}

	public void setINT_LN_TYP(String iNT_LN_TYP) {
		INT_LN_TYP = iNT_LN_TYP;
	}

	public String getINT_ADJ_DUR() {
		return INT_ADJ_DUR;
	}

	public void setINT_ADJ_DUR(String iNT_ADJ_DUR) {
		INT_ADJ_DUR = iNT_ADJ_DUR;
	}

	public String getRECLOAN_AVAL_AMT() {
		return RECLOAN_AVAL_AMT;
	}

	public void setRECLOAN_AVAL_AMT(String rECLOAN_AVAL_AMT) {
		RECLOAN_AVAL_AMT = rECLOAN_AVAL_AMT;
	}

}