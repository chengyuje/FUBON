package com.systex.jbranch.fubon.commons.esb.vo.eb312201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/08.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB312201OutputVO {
	@XmlElement
	private String DOC_NO;	//動支文編
	@XmlElement
	private String DOC_SEQ;	//動支序號
	@XmlElement
	private String CUST_NO;	//統一編號
	@XmlElement
	private String CUST_NAME;	//戶名
	@XmlElement
	private String GL_BRH;	//帳務行
	@XmlElement
	private String GL_GLCD4;	//會計科目
	@XmlElement
	private String GL_GLCD5;	//會計子目
	@XmlElement
	private String GL_GLCD6;	//會計細目
	@XmlElement
	private String ADV_COD;	//債清註記
	@XmlElement
	private String SHR_CRLN_COD;	//共用額度註記
	@XmlElement
	private String RCV_BRH;	//代收件分行
	@XmlElement
	private String LST_TK_DATE;	//最後動支日
	@XmlElement
	private String CUR_COD;	//授信幣別
	@XmlElement
	private String LOAN_BAL;	//授信餘額
	@XmlElement
	private String PRN_PAY_TYP;	//還款方式
	@XmlElement
	private String PRN_PAY_DUR;	//還款週期
	@XmlElement
	private String STR_DATE;	//撥貸日
	@XmlElement
	private String FST_LOAN_AMT;	//初撥金額
	@XmlElement
	private String INT_COD;	//計息基準
	@XmlElement
	private String INT_PAY_DUR;	//計息週期
	@XmlElement
	private String FST_INT_DATE;	//起息日
	@XmlElement
	private String LOAN_AMT_CR;	//累計還款
	@XmlElement
	private String PRN_ALW_DUR;	//寬限期
	@XmlElement
	private String END_DATE;	//到期日
	@XmlElement
	private String INS_AMT;	//每期攤還金額
	@XmlElement
	private String RMK_CNT;	//授信筆數
	@XmlElement
	private String LST_INT_SDATE;	//上次應繳息日
	@XmlElement
	private String INT_ADJ_TYP;	//利率調整方式
	@XmlElement
	private String INT_ADJ_DUR;	//利率調整週期
	@XmlElement
	private String LST_INT_DATE;	//繳息止日
	@XmlElement
	private String INT_CYCLE;	//應繳息日
	@XmlElement
	private String INT_RATE_TYP;	//利率依據銀行別
	@XmlElement
	private String INT_RATE_SPD;	//利率加減碼
	@XmlElement
	private String INT_RATE;	//計息利率
	@XmlElement
	private String DRF_FLG;	//匯票兌付記號
	@XmlElement
	private String AUTO_PAY_PFLG;	//自動扣帳-本金
	@XmlElement
	private String AUTO_PAY_IFLG;	//自動扣帳-利息
	@XmlElement
	private String AUTO_PAY_ACH;	//自動扣帳-ACH
	@XmlElement
	private String LST_PNT_DATE;	//上次計違約金止日
	@XmlElement
	private String LOAN_AMT;	//匯票金額
	@XmlElement
	private String AUTO_PAY_ACCT;	//自動扣帳帳號
	@XmlElement
	private String DLY_COD;	//逾期代號
	@XmlElement
	private String OVD_DATE;	//轉催收日期
	@XmlElement
	private String TRU_CORR_ACT;	//信保對方帳號
	@XmlElement
	private String RFH_FLG;	//重整註記
	@XmlElement
	private String BAD_DATE;	//轉呆帳日期
	@XmlElement
	private String SLIP_FLG_PRN;	//列印通知單-本金
	@XmlElement
	private String SLIP_FLG_INT;	//列印通知單-利息
	@XmlElement
	private String TRU_IND;	//信保註記
	@XmlElement
	private String DRF_NO;	//匯票號碼
	@XmlElement
	private String OVD_ACCT;	//催收對方帳號
	@XmlElement
	private String GRP_FLG;	//團體貸款註記
	@XmlElement
	private String SPEC_LN_COD;	//特殊貸款類別
	@XmlElement
	private String DSCT_INT_RATE;	//銀行核定計息利率
	@XmlElement
	private String ALC_AMT;	//預收款餘額
	@XmlElement
	private String DSCT_ADJ_TYP;	//銀行核定利率調整方式
	@XmlElement
	private String DSCT_ADJ_DUR;	//銀行核定利率調整週期
	@XmlElement
	private String DSCT_RATE_TYP;	//銀行核定利率依據銀行別
	@XmlElement
	private String DSCT_RATE_SPD;	//銀行核定利率加減碼
	@XmlElement
	private String RES_TOT_PRD;	//緩付期間
	@XmlElement
	private String INS_PRN;	//均攤本金
	@XmlElement
	private String RSK_NAT;	//風險國家別
	@XmlElement
	private String RES_STR_DATE;	//緩繳期利息攤還起日
	@XmlElement
	private String RES_INT_BAL;	//緩繳期利息餘額
	@XmlElement
	private String SLIP_FLG;	//同意證券資產化
	@XmlElement
	private String INS_DUE_PRD;	//本金攤還期數
	@XmlElement
	private String INS_DUE_DATE;	//本金攤還到期日
	@XmlElement
	private String OFFSET_FLG;	//抵利型房貸註記
	@XmlElement
	private String OFFSET_DATE;	//抵利型房貸日期
	@XmlElement
	private String OFFSET_DATE_CANCEL;	//抵利型房貸註銷日期
	@XmlElement
	private String OFFSET_ACNO_1;	//抵利型房貸帳號一
	@XmlElement
	private String OFFSET_ACNO_2;	//抵利型房貸帳號二
	@XmlElement
	private String EST_COD;	//攤提註記
	@XmlElement
	private String EST_TRM;	//攤提期數
	@XmlElement
	private String FEE_ADV;	//攤提餘額
	@XmlElement
	private String FEE_AMT;	//手續費金額
	@XmlElement
	private String LN_TRANS_ACNO;	//轉貸前撥款帳號
	@XmlElement
	private String MON_FEE;	//當月手續費金額
	@XmlElement
	private String PART_RECV_FLG;	//部份銷帳
	@XmlElement
	private String NEG_NO;	//議價編號
	@XmlElement
	private String FTP_RATE;	//FTP_RATE
	@XmlElement
	private String DTL_TYP;	//融資方式
	@XmlElement
	private String PRO_DATE;	//原貸通知單號
	@XmlElement
	private String TX_SEQ1;	//批次號
	@XmlElement
	private String INT_FLG;	//信保逾放註記
	@XmlElement
	private String EX_RATE;	//折台匯率
	@XmlElement
	private String TWD_AMT;	//折台金額
	
	
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
	public String getCUST_NO() {
		return CUST_NO;
	}
	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public String getGL_BRH() {
		return GL_BRH;
	}
	public void setGL_BRH(String gL_BRH) {
		GL_BRH = gL_BRH;
	}
	public String getGL_GLCD4() {
		return GL_GLCD4;
	}
	public void setGL_GLCD4(String gL_GLCD4) {
		GL_GLCD4 = gL_GLCD4;
	}
	public String getGL_GLCD5() {
		return GL_GLCD5;
	}
	public void setGL_GLCD5(String gL_GLCD5) {
		GL_GLCD5 = gL_GLCD5;
	}
	public String getGL_GLCD6() {
		return GL_GLCD6;
	}
	public void setGL_GLCD6(String gL_GLCD6) {
		GL_GLCD6 = gL_GLCD6;
	}
	public String getADV_COD() {
		return ADV_COD;
	}
	public void setADV_COD(String aDV_COD) {
		ADV_COD = aDV_COD;
	}
	public String getSHR_CRLN_COD() {
		return SHR_CRLN_COD;
	}
	public void setSHR_CRLN_COD(String sHR_CRLN_COD) {
		SHR_CRLN_COD = sHR_CRLN_COD;
	}
	public String getRCV_BRH() {
		return RCV_BRH;
	}
	public void setRCV_BRH(String rCV_BRH) {
		RCV_BRH = rCV_BRH;
	}
	public String getLST_TK_DATE() {
		return LST_TK_DATE;
	}
	public void setLST_TK_DATE(String lST_TK_DATE) {
		LST_TK_DATE = lST_TK_DATE;
	}
	public String getCUR_COD() {
		return CUR_COD;
	}
	public void setCUR_COD(String cUR_COD) {
		CUR_COD = cUR_COD;
	}
	public String getLOAN_BAL() {
		return LOAN_BAL;
	}
	public void setLOAN_BAL(String lOAN_BAL) {
		LOAN_BAL = lOAN_BAL;
	}
	public String getPRN_PAY_TYP() {
		return PRN_PAY_TYP;
	}
	public void setPRN_PAY_TYP(String pRN_PAY_TYP) {
		PRN_PAY_TYP = pRN_PAY_TYP;
	}
	public String getPRN_PAY_DUR() {
		return PRN_PAY_DUR;
	}
	public void setPRN_PAY_DUR(String pRN_PAY_DUR) {
		PRN_PAY_DUR = pRN_PAY_DUR;
	}
	public String getSTR_DATE() {
		return STR_DATE;
	}
	public void setSTR_DATE(String sTR_DATE) {
		STR_DATE = sTR_DATE;
	}
	public String getFST_LOAN_AMT() {
		return FST_LOAN_AMT;
	}
	public void setFST_LOAN_AMT(String fST_LOAN_AMT) {
		FST_LOAN_AMT = fST_LOAN_AMT;
	}
	public String getINT_COD() {
		return INT_COD;
	}
	public void setINT_COD(String iNT_COD) {
		INT_COD = iNT_COD;
	}
	public String getINT_PAY_DUR() {
		return INT_PAY_DUR;
	}
	public void setINT_PAY_DUR(String iNT_PAY_DUR) {
		INT_PAY_DUR = iNT_PAY_DUR;
	}
	public String getFST_INT_DATE() {
		return FST_INT_DATE;
	}
	public void setFST_INT_DATE(String fST_INT_DATE) {
		FST_INT_DATE = fST_INT_DATE;
	}
	public String getLOAN_AMT_CR() {
		return LOAN_AMT_CR;
	}
	public void setLOAN_AMT_CR(String lOAN_AMT_CR) {
		LOAN_AMT_CR = lOAN_AMT_CR;
	}
	public String getPRN_ALW_DUR() {
		return PRN_ALW_DUR;
	}
	public void setPRN_ALW_DUR(String pRN_ALW_DUR) {
		PRN_ALW_DUR = pRN_ALW_DUR;
	}
	public String getEND_DATE() {
		return END_DATE;
	}
	public void setEND_DATE(String eND_DATE) {
		END_DATE = eND_DATE;
	}
	public String getINS_AMT() {
		return INS_AMT;
	}
	public void setINS_AMT(String iNS_AMT) {
		INS_AMT = iNS_AMT;
	}
	public String getRMK_CNT() {
		return RMK_CNT;
	}
	public void setRMK_CNT(String rMK_CNT) {
		RMK_CNT = rMK_CNT;
	}
	public String getLST_INT_SDATE() {
		return LST_INT_SDATE;
	}
	public void setLST_INT_SDATE(String lST_INT_SDATE) {
		LST_INT_SDATE = lST_INT_SDATE;
	}
	public String getINT_ADJ_TYP() {
		return INT_ADJ_TYP;
	}
	public void setINT_ADJ_TYP(String iNT_ADJ_TYP) {
		INT_ADJ_TYP = iNT_ADJ_TYP;
	}
	public String getINT_ADJ_DUR() {
		return INT_ADJ_DUR;
	}
	public void setINT_ADJ_DUR(String iNT_ADJ_DUR) {
		INT_ADJ_DUR = iNT_ADJ_DUR;
	}
	public String getLST_INT_DATE() {
		return LST_INT_DATE;
	}
	public void setLST_INT_DATE(String lST_INT_DATE) {
		LST_INT_DATE = lST_INT_DATE;
	}
	public String getINT_CYCLE() {
		return INT_CYCLE;
	}
	public void setINT_CYCLE(String iNT_CYCLE) {
		INT_CYCLE = iNT_CYCLE;
	}
	public String getINT_RATE_TYP() {
		return INT_RATE_TYP;
	}
	public void setINT_RATE_TYP(String iNT_RATE_TYP) {
		INT_RATE_TYP = iNT_RATE_TYP;
	}
	public String getINT_RATE_SPD() {
		return INT_RATE_SPD;
	}
	public void setINT_RATE_SPD(String iNT_RATE_SPD) {
		INT_RATE_SPD = iNT_RATE_SPD;
	}
	public String getINT_RATE() {
		return INT_RATE;
	}
	public void setINT_RATE(String iNT_RATE) {
		INT_RATE = iNT_RATE;
	}
	public String getDRF_FLG() {
		return DRF_FLG;
	}
	public void setDRF_FLG(String dRF_FLG) {
		DRF_FLG = dRF_FLG;
	}
	public String getAUTO_PAY_PFLG() {
		return AUTO_PAY_PFLG;
	}
	public void setAUTO_PAY_PFLG(String aUTO_PAY_PFLG) {
		AUTO_PAY_PFLG = aUTO_PAY_PFLG;
	}
	public String getAUTO_PAY_IFLG() {
		return AUTO_PAY_IFLG;
	}
	public void setAUTO_PAY_IFLG(String aUTO_PAY_IFLG) {
		AUTO_PAY_IFLG = aUTO_PAY_IFLG;
	}
	public String getAUTO_PAY_ACH() {
		return AUTO_PAY_ACH;
	}
	public void setAUTO_PAY_ACH(String aUTO_PAY_ACH) {
		AUTO_PAY_ACH = aUTO_PAY_ACH;
	}
	public String getLST_PNT_DATE() {
		return LST_PNT_DATE;
	}
	public void setLST_PNT_DATE(String lST_PNT_DATE) {
		LST_PNT_DATE = lST_PNT_DATE;
	}
	public String getLOAN_AMT() {
		return LOAN_AMT;
	}
	public void setLOAN_AMT(String lOAN_AMT) {
		LOAN_AMT = lOAN_AMT;
	}
	public String getAUTO_PAY_ACCT() {
		return AUTO_PAY_ACCT;
	}
	public void setAUTO_PAY_ACCT(String aUTO_PAY_ACCT) {
		AUTO_PAY_ACCT = aUTO_PAY_ACCT;
	}
	public String getDLY_COD() {
		return DLY_COD;
	}
	public void setDLY_COD(String dLY_COD) {
		DLY_COD = dLY_COD;
	}
	public String getOVD_DATE() {
		return OVD_DATE;
	}
	public void setOVD_DATE(String oVD_DATE) {
		OVD_DATE = oVD_DATE;
	}
	public String getTRU_CORR_ACT() {
		return TRU_CORR_ACT;
	}
	public void setTRU_CORR_ACT(String tRU_CORR_ACT) {
		TRU_CORR_ACT = tRU_CORR_ACT;
	}
	public String getRFH_FLG() {
		return RFH_FLG;
	}
	public void setRFH_FLG(String rFH_FLG) {
		RFH_FLG = rFH_FLG;
	}
	public String getBAD_DATE() {
		return BAD_DATE;
	}
	public void setBAD_DATE(String bAD_DATE) {
		BAD_DATE = bAD_DATE;
	}
	public String getSLIP_FLG_PRN() {
		return SLIP_FLG_PRN;
	}
	public void setSLIP_FLG_PRN(String sLIP_FLG_PRN) {
		SLIP_FLG_PRN = sLIP_FLG_PRN;
	}
	public String getSLIP_FLG_INT() {
		return SLIP_FLG_INT;
	}
	public void setSLIP_FLG_INT(String sLIP_FLG_INT) {
		SLIP_FLG_INT = sLIP_FLG_INT;
	}
	public String getTRU_IND() {
		return TRU_IND;
	}
	public void setTRU_IND(String tRU_IND) {
		TRU_IND = tRU_IND;
	}
	public String getDRF_NO() {
		return DRF_NO;
	}
	public void setDRF_NO(String dRF_NO) {
		DRF_NO = dRF_NO;
	}
	public String getOVD_ACCT() {
		return OVD_ACCT;
	}
	public void setOVD_ACCT(String oVD_ACCT) {
		OVD_ACCT = oVD_ACCT;
	}
	public String getGRP_FLG() {
		return GRP_FLG;
	}
	public void setGRP_FLG(String gRP_FLG) {
		GRP_FLG = gRP_FLG;
	}
	public String getSPEC_LN_COD() {
		return SPEC_LN_COD;
	}
	public void setSPEC_LN_COD(String sPEC_LN_COD) {
		SPEC_LN_COD = sPEC_LN_COD;
	}
	public String getDSCT_INT_RATE() {
		return DSCT_INT_RATE;
	}
	public void setDSCT_INT_RATE(String dSCT_INT_RATE) {
		DSCT_INT_RATE = dSCT_INT_RATE;
	}
	public String getALC_AMT() {
		return ALC_AMT;
	}
	public void setALC_AMT(String aLC_AMT) {
		ALC_AMT = aLC_AMT;
	}
	public String getDSCT_ADJ_TYP() {
		return DSCT_ADJ_TYP;
	}
	public void setDSCT_ADJ_TYP(String dSCT_ADJ_TYP) {
		DSCT_ADJ_TYP = dSCT_ADJ_TYP;
	}
	public String getDSCT_ADJ_DUR() {
		return DSCT_ADJ_DUR;
	}
	public void setDSCT_ADJ_DUR(String dSCT_ADJ_DUR) {
		DSCT_ADJ_DUR = dSCT_ADJ_DUR;
	}
	public String getDSCT_RATE_TYP() {
		return DSCT_RATE_TYP;
	}
	public void setDSCT_RATE_TYP(String dSCT_RATE_TYP) {
		DSCT_RATE_TYP = dSCT_RATE_TYP;
	}
	public String getDSCT_RATE_SPD() {
		return DSCT_RATE_SPD;
	}
	public void setDSCT_RATE_SPD(String dSCT_RATE_SPD) {
		DSCT_RATE_SPD = dSCT_RATE_SPD;
	}
	public String getRES_TOT_PRD() {
		return RES_TOT_PRD;
	}
	public void setRES_TOT_PRD(String rES_TOT_PRD) {
		RES_TOT_PRD = rES_TOT_PRD;
	}
	public String getINS_PRN() {
		return INS_PRN;
	}
	public void setINS_PRN(String iNS_PRN) {
		INS_PRN = iNS_PRN;
	}
	public String getRSK_NAT() {
		return RSK_NAT;
	}
	public void setRSK_NAT(String rSK_NAT) {
		RSK_NAT = rSK_NAT;
	}
	public String getRES_STR_DATE() {
		return RES_STR_DATE;
	}
	public void setRES_STR_DATE(String rES_STR_DATE) {
		RES_STR_DATE = rES_STR_DATE;
	}
	public String getRES_INT_BAL() {
		return RES_INT_BAL;
	}
	public void setRES_INT_BAL(String rES_INT_BAL) {
		RES_INT_BAL = rES_INT_BAL;
	}
	public String getSLIP_FLG() {
		return SLIP_FLG;
	}
	public void setSLIP_FLG(String sLIP_FLG) {
		SLIP_FLG = sLIP_FLG;
	}
	public String getINS_DUE_PRD() {
		return INS_DUE_PRD;
	}
	public void setINS_DUE_PRD(String iNS_DUE_PRD) {
		INS_DUE_PRD = iNS_DUE_PRD;
	}
	public String getINS_DUE_DATE() {
		return INS_DUE_DATE;
	}
	public void setINS_DUE_DATE(String iNS_DUE_DATE) {
		INS_DUE_DATE = iNS_DUE_DATE;
	}
	public String getOFFSET_FLG() {
		return OFFSET_FLG;
	}
	public void setOFFSET_FLG(String oFFSET_FLG) {
		OFFSET_FLG = oFFSET_FLG;
	}
	public String getOFFSET_DATE() {
		return OFFSET_DATE;
	}
	public void setOFFSET_DATE(String oFFSET_DATE) {
		OFFSET_DATE = oFFSET_DATE;
	}
	public String getOFFSET_DATE_CANCEL() {
		return OFFSET_DATE_CANCEL;
	}
	public void setOFFSET_DATE_CANCEL(String oFFSET_DATE_CANCEL) {
		OFFSET_DATE_CANCEL = oFFSET_DATE_CANCEL;
	}
	public String getOFFSET_ACNO_1() {
		return OFFSET_ACNO_1;
	}
	public void setOFFSET_ACNO_1(String oFFSET_ACNO_1) {
		OFFSET_ACNO_1 = oFFSET_ACNO_1;
	}
	public String getOFFSET_ACNO_2() {
		return OFFSET_ACNO_2;
	}
	public void setOFFSET_ACNO_2(String oFFSET_ACNO_2) {
		OFFSET_ACNO_2 = oFFSET_ACNO_2;
	}
	public String getEST_COD() {
		return EST_COD;
	}
	public void setEST_COD(String eST_COD) {
		EST_COD = eST_COD;
	}
	public String getEST_TRM() {
		return EST_TRM;
	}
	public void setEST_TRM(String eST_TRM) {
		EST_TRM = eST_TRM;
	}
	public String getFEE_ADV() {
		return FEE_ADV;
	}
	public void setFEE_ADV(String fEE_ADV) {
		FEE_ADV = fEE_ADV;
	}
	public String getFEE_AMT() {
		return FEE_AMT;
	}
	public void setFEE_AMT(String fEE_AMT) {
		FEE_AMT = fEE_AMT;
	}
	public String getLN_TRANS_ACNO() {
		return LN_TRANS_ACNO;
	}
	public void setLN_TRANS_ACNO(String lN_TRANS_ACNO) {
		LN_TRANS_ACNO = lN_TRANS_ACNO;
	}
	public String getMON_FEE() {
		return MON_FEE;
	}
	public void setMON_FEE(String mON_FEE) {
		MON_FEE = mON_FEE;
	}
	public String getPART_RECV_FLG() {
		return PART_RECV_FLG;
	}
	public void setPART_RECV_FLG(String pART_RECV_FLG) {
		PART_RECV_FLG = pART_RECV_FLG;
	}
	public String getNEG_NO() {
		return NEG_NO;
	}
	public void setNEG_NO(String nEG_NO) {
		NEG_NO = nEG_NO;
	}
	public String getFTP_RATE() {
		return FTP_RATE;
	}
	public void setFTP_RATE(String fTP_RATE) {
		FTP_RATE = fTP_RATE;
	}
	public String getDTL_TYP() {
		return DTL_TYP;
	}
	public void setDTL_TYP(String dTL_TYP) {
		DTL_TYP = dTL_TYP;
	}
	public String getPRO_DATE() {
		return PRO_DATE;
	}
	public void setPRO_DATE(String pRO_DATE) {
		PRO_DATE = pRO_DATE;
	}
	public String getTX_SEQ1() {
		return TX_SEQ1;
	}
	public void setTX_SEQ1(String tX_SEQ1) {
		TX_SEQ1 = tX_SEQ1;
	}
	public String getINT_FLG() {
		return INT_FLG;
	}
	public void setINT_FLG(String iNT_FLG) {
		INT_FLG = iNT_FLG;
	}
	public String getEX_RATE() {
		return EX_RATE;
	}
	public void setEX_RATE(String eX_RATE) {
		EX_RATE = eX_RATE;
	}
	public String getTWD_AMT() {
		return TWD_AMT;
	}
	public void setTWD_AMT(String tWD_AMT) {
		TWD_AMT = tWD_AMT;
	}
	
	
}