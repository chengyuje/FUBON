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
public class EB312201InputVO {
	@XmlElement
	private String ACNO_LN;	//授信帳號
	@XmlElement
	private String DOC_NO;	//文件編號
	@XmlElement
	private String DOC_SEQ;	//文件編號序號
	@XmlElement
	private String STR_DATE;	//撥貸日
	@XmlElement
	private String END_DATE;	//到期日
	@XmlElement
	private String RMK_CNT;	//授信筆數
	@XmlElement
	private String LST_INT_SDATE;	//上次應繳息日
	@XmlElement
	private String INT_CYCLE;	//繳息相當日
	@XmlElement
	private String AUTO_PAY_PFLG;	//自動扣帳-本金
	@XmlElement
	private String AUTO_PAY_IFLG;	//自動扣帳-利息
	@XmlElement
	private String AUTO_PAY_ACCT;	//自動扣帳帳號
	@XmlElement
	private String RFH_FLG;	//重整註記
	@XmlElement
	private String SLIP_FLG_PRN;	//列印通知單-本金
	@XmlElement
	private String SLIP_FLG_INT;	//列印通知單-利息
	@XmlElement
	private String DRF_NO;	//匯票號碼
	@XmlElement
	private String OVD_ACCT;	//催收對方帳號
	@XmlElement
	private String RECAL_FLG;	//本息重算
	@XmlElement
	private String DLY_TOT_PRD;	//緩付期間
	@XmlElement
	private String INS_PRN;	//均攤本金
	@XmlElement
	private String RSK_NAT;	//風險國家別
	@XmlElement
	private String RES_STR_DATE;	//緩繳期利息攤還起日
	@XmlElement
	private String SLIP_FLG;	//同意證券資產化
	@XmlElement
	private String PRN_ALW_DUR;	//本金攤還期數
	@XmlElement
	private String CLS_DATE;	//銷案日期
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
	private String FEE_ADV;	//攤還餘額
	@XmlElement
	private String LN_TRANS_ACNO;	//轉貸前撥款帳號
	@XmlElement
	private String OVD_DATE;	//議價編號
	@XmlElement
	private String INT_RATE_SPD1;	//FTP_RATE
	@XmlElement
	private String DTL_TYP;	//融資方式(允許空白)
	@XmlElement
	private String PRO_DATE;	//原貸通知單號
	@XmlElement
	private String TX_SEQ1;	//批次號
	@XmlElement
	private String INT_FLG;	//信保逾放註記
	@XmlElement
	private String LMT_RATE2;	//折台匯率
	@XmlElement
	private String INS_AMT;	//折台金額
	
	
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
	public String getSTR_DATE() {
		return STR_DATE;
	}
	public void setSTR_DATE(String sTR_DATE) {
		STR_DATE = sTR_DATE;
	}
	public String getEND_DATE() {
		return END_DATE;
	}
	public void setEND_DATE(String eND_DATE) {
		END_DATE = eND_DATE;
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
	public String getINT_CYCLE() {
		return INT_CYCLE;
	}
	public void setINT_CYCLE(String iNT_CYCLE) {
		INT_CYCLE = iNT_CYCLE;
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
	public String getAUTO_PAY_ACCT() {
		return AUTO_PAY_ACCT;
	}
	public void setAUTO_PAY_ACCT(String aUTO_PAY_ACCT) {
		AUTO_PAY_ACCT = aUTO_PAY_ACCT;
	}
	public String getRFH_FLG() {
		return RFH_FLG;
	}
	public void setRFH_FLG(String rFH_FLG) {
		RFH_FLG = rFH_FLG;
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
	public String getRECAL_FLG() {
		return RECAL_FLG;
	}
	public void setRECAL_FLG(String rECAL_FLG) {
		RECAL_FLG = rECAL_FLG;
	}
	public String getDLY_TOT_PRD() {
		return DLY_TOT_PRD;
	}
	public void setDLY_TOT_PRD(String dLY_TOT_PRD) {
		DLY_TOT_PRD = dLY_TOT_PRD;
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
	public String getSLIP_FLG() {
		return SLIP_FLG;
	}
	public void setSLIP_FLG(String sLIP_FLG) {
		SLIP_FLG = sLIP_FLG;
	}
	public String getPRN_ALW_DUR() {
		return PRN_ALW_DUR;
	}
	public void setPRN_ALW_DUR(String pRN_ALW_DUR) {
		PRN_ALW_DUR = pRN_ALW_DUR;
	}
	public String getCLS_DATE() {
		return CLS_DATE;
	}
	public void setCLS_DATE(String cLS_DATE) {
		CLS_DATE = cLS_DATE;
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
	public String getLN_TRANS_ACNO() {
		return LN_TRANS_ACNO;
	}
	public void setLN_TRANS_ACNO(String lN_TRANS_ACNO) {
		LN_TRANS_ACNO = lN_TRANS_ACNO;
	}
	public String getOVD_DATE() {
		return OVD_DATE;
	}
	public void setOVD_DATE(String oVD_DATE) {
		OVD_DATE = oVD_DATE;
	}
	public String getINT_RATE_SPD1() {
		return INT_RATE_SPD1;
	}
	public void setINT_RATE_SPD1(String iNT_RATE_SPD1) {
		INT_RATE_SPD1 = iNT_RATE_SPD1;
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
	public String getLMT_RATE2() {
		return LMT_RATE2;
	}
	public void setLMT_RATE2(String lMT_RATE2) {
		LMT_RATE2 = lMT_RATE2;
	}
	public String getINS_AMT() {
		return INS_AMT;
	}
	public void setINS_AMT(String iNS_AMT) {
		INS_AMT = iNS_AMT;
	}


}