package com.systex.jbranch.fubon.commons.esb.vo.eb382201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/08.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB382201OutputVO {
	@XmlElement
	private String CUST_NO;	//統一編號
	@XmlElement
	private String LOAN_AMT;	//貸款金額
	@XmlElement
	private String ACT_STS;	//狀態
	@XmlElement
	private String PROD_STAG;	//債清註記
	@XmlElement
	private String CRLN_AMT;	//總額度
	@XmlElement
	private String AVAIL_BAL;	//可用額度
	@XmlElement
	private String LOAN_BAL;	//授信餘額
	@XmlElement
	private String INS_AMT;	//每期本息
	@XmlElement
	private String NAME_COD;	//戶名代號
	@XmlElement
	private String CUST_NAME;	//戶名
	@XmlElement
	private String ADDR_COD;	//地址代號
	@XmlElement
	private String ADDR_1;	//地址１
	@XmlElement
	private String ADDR_2;	//地址２
	@XmlElement
	private String TEL_COD_1;	//電話代號１
	@XmlElement
	private String TEL_NO_1;	//電話１
	@XmlElement
	private String TEL_COD_2;	//電話代號２
	@XmlElement
	private String TEL_NO_2;	//電話２
	@XmlElement
	private String TEL_COD_3;	//電話代號３
	@XmlElement
	private String TEL_NO_3;	//電話３
	@XmlElement
	private String PRN_PAY_TYP;	//還款方式
	@XmlElement
	private String PRN_PAY_DUR;	//還款週期
	@XmlElement
	private String INT_PAY_TYP;	//繳息方式
	@XmlElement
	private String INT_PAY_DUR;	//繳息週期
	@XmlElement
	private String INT_COD;	//計息基準
	@XmlElement
	private String INT_ADJ_TYP;	//調息方式
	@XmlElement
	private String INT_ADJ_DUR;	//調息週期
	@XmlElement
	private String INT_RATE_TYP;	//利率基準
	@XmlElement
	private String INT_LN_TYP;	//利率性質別
	@XmlElement
	private String INT_RATE_SPD;	//加減碼
	@XmlElement
	private String INT_RATE;	//計息利率
	@XmlElement
	private String PRN_ALW_DUR;	//寬限期
	@XmlElement
	private String ALW_STR_DATE;	//寬限期起算日
	@XmlElement
	private String GRAD_DATE;	//畢業日
	@XmlElement
	private String STR_DATE;	//撥款日
	@XmlElement
	private String FST_INT_DATE;	//起息日
	@XmlElement
	private String END_DATE;	//到期日
	@XmlElement
	private String END_DATE3;	//最後到期日
	@XmlElement
	private String LST_INT_DATE;	//繳息止日
	@XmlElement
	private String INT_CYCLE;	//應繳息日
	@XmlElement
	private String LST_INT_SDATE;	//上次應繳息日
	@XmlElement
	private String LST_PNT_DATE;	//上次計違約金止日
	@XmlElement
	private String APROV_EMP_ID;	//核准主管
	@XmlElement
	private String DOC_NO;	//借據號碼
	@XmlElement
	private String CRE_BRH;	//借據資料建檔行
	@XmlElement
	private String GOV_INT_TYP;	//自付利息註記
	@XmlElement
	private String ALC_BAL;	//預收款餘額
	@XmlElement
	private String AG_FEE_BAL;	//墊付費用餘額
	@XmlElement
	private String AUTO_PAY_ACCT;	//自動扣帳帳號
	@XmlElement
	private String AUTO_PAY_SRC;	//自動扣帳帳號建檔來源
	@XmlElement
	private String GOV_INTEDATE;	//政府息清算止日
	@XmlElement
	private String GOV_IDATE;	//政府起息日
	@XmlElement
	private String GOV_EDATE;	//政府止息日
	@XmlElement
	private String DLY_COD;	//逾期狀況
	@XmlElement
	private String SUB_COD;	//逾期減因
	@XmlElement
	private String RATE;	//借款人利率
	@XmlElement
	private String GOV_RATE;	//政府利率
	@XmlElement
	private String REV_DATE;	//覆審日期
	@XmlElement
	private String RISK_RANK;	//風險等級
	@XmlElement
	private String OVD_DATE;	//轉催收日
	@XmlElement
	private String PARTIAL_FLG;	//部份銷帳註記
	@XmlElement
	private String OVD_ACCT;	//催收帳號
	@XmlElement
	private String OVD_CLS_DATE;	//催收結清日
	@XmlElement
	private String B_GOV_INT_TYP;	//原本自付利息註記
	@XmlElement
	private String WORK_FLG;	//在職專班註記
	@XmlElement
	private String TRU_IND;	//信保註記
	@XmlElement
	private String EXT_DUE_DATE;	//展期止日
	@XmlElement
	private String CLS_DATE;	//結清日
	@XmlElement
	private String REASON;	//教育階段
	@XmlElement
	private String REASON_RMK;	//教育階段說明
	@XmlElement
	private String MSG_TYPE;	//簡訊發送註記
	@XmlElement
	private String AUTO_PAY_ACH;	//ACH狀態
	
	
	public String getCUST_NO() {
		return CUST_NO;
	}
	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}
	public String getLOAN_AMT() {
		return LOAN_AMT;
	}
	public void setLOAN_AMT(String lOAN_AMT) {
		LOAN_AMT = lOAN_AMT;
	}
	public String getACT_STS() {
		return ACT_STS;
	}
	public void setACT_STS(String aCT_STS) {
		ACT_STS = aCT_STS;
	}
	public String getPROD_STAG() {
		return PROD_STAG;
	}
	public void setPROD_STAG(String pROD_STAG) {
		PROD_STAG = pROD_STAG;
	}
	public String getCRLN_AMT() {
		return CRLN_AMT;
	}
	public void setCRLN_AMT(String cRLN_AMT) {
		CRLN_AMT = cRLN_AMT;
	}
	public String getAVAIL_BAL() {
		return AVAIL_BAL;
	}
	public void setAVAIL_BAL(String aVAIL_BAL) {
		AVAIL_BAL = aVAIL_BAL;
	}
	public String getLOAN_BAL() {
		return LOAN_BAL;
	}
	public void setLOAN_BAL(String lOAN_BAL) {
		LOAN_BAL = lOAN_BAL;
	}
	public String getINS_AMT() {
		return INS_AMT;
	}
	public void setINS_AMT(String iNS_AMT) {
		INS_AMT = iNS_AMT;
	}
	public String getNAME_COD() {
		return NAME_COD;
	}
	public void setNAME_COD(String nAME_COD) {
		NAME_COD = nAME_COD;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public String getADDR_COD() {
		return ADDR_COD;
	}
	public void setADDR_COD(String aDDR_COD) {
		ADDR_COD = aDDR_COD;
	}
	public String getADDR_1() {
		return ADDR_1;
	}
	public void setADDR_1(String aDDR_1) {
		ADDR_1 = aDDR_1;
	}
	public String getADDR_2() {
		return ADDR_2;
	}
	public void setADDR_2(String aDDR_2) {
		ADDR_2 = aDDR_2;
	}
	public String getTEL_COD_1() {
		return TEL_COD_1;
	}
	public void setTEL_COD_1(String tEL_COD_1) {
		TEL_COD_1 = tEL_COD_1;
	}
	public String getTEL_NO_1() {
		return TEL_NO_1;
	}
	public void setTEL_NO_1(String tEL_NO_1) {
		TEL_NO_1 = tEL_NO_1;
	}
	public String getTEL_COD_2() {
		return TEL_COD_2;
	}
	public void setTEL_COD_2(String tEL_COD_2) {
		TEL_COD_2 = tEL_COD_2;
	}
	public String getTEL_NO_2() {
		return TEL_NO_2;
	}
	public void setTEL_NO_2(String tEL_NO_2) {
		TEL_NO_2 = tEL_NO_2;
	}
	public String getTEL_COD_3() {
		return TEL_COD_3;
	}
	public void setTEL_COD_3(String tEL_COD_3) {
		TEL_COD_3 = tEL_COD_3;
	}
	public String getTEL_NO_3() {
		return TEL_NO_3;
	}
	public void setTEL_NO_3(String tEL_NO_3) {
		TEL_NO_3 = tEL_NO_3;
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
	public String getINT_PAY_TYP() {
		return INT_PAY_TYP;
	}
	public void setINT_PAY_TYP(String iNT_PAY_TYP) {
		INT_PAY_TYP = iNT_PAY_TYP;
	}
	public String getINT_PAY_DUR() {
		return INT_PAY_DUR;
	}
	public void setINT_PAY_DUR(String iNT_PAY_DUR) {
		INT_PAY_DUR = iNT_PAY_DUR;
	}
	public String getINT_COD() {
		return INT_COD;
	}
	public void setINT_COD(String iNT_COD) {
		INT_COD = iNT_COD;
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
	public String getPRN_ALW_DUR() {
		return PRN_ALW_DUR;
	}
	public void setPRN_ALW_DUR(String pRN_ALW_DUR) {
		PRN_ALW_DUR = pRN_ALW_DUR;
	}
	public String getALW_STR_DATE() {
		return ALW_STR_DATE;
	}
	public void setALW_STR_DATE(String aLW_STR_DATE) {
		ALW_STR_DATE = aLW_STR_DATE;
	}
	public String getGRAD_DATE() {
		return GRAD_DATE;
	}
	public void setGRAD_DATE(String gRAD_DATE) {
		GRAD_DATE = gRAD_DATE;
	}
	public String getSTR_DATE() {
		return STR_DATE;
	}
	public void setSTR_DATE(String sTR_DATE) {
		STR_DATE = sTR_DATE;
	}
	public String getFST_INT_DATE() {
		return FST_INT_DATE;
	}
	public void setFST_INT_DATE(String fST_INT_DATE) {
		FST_INT_DATE = fST_INT_DATE;
	}
	public String getEND_DATE() {
		return END_DATE;
	}
	public void setEND_DATE(String eND_DATE) {
		END_DATE = eND_DATE;
	}
	public String getEND_DATE3() {
		return END_DATE3;
	}
	public void setEND_DATE3(String eND_DATE3) {
		END_DATE3 = eND_DATE3;
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
	public String getLST_INT_SDATE() {
		return LST_INT_SDATE;
	}
	public void setLST_INT_SDATE(String lST_INT_SDATE) {
		LST_INT_SDATE = lST_INT_SDATE;
	}
	public String getLST_PNT_DATE() {
		return LST_PNT_DATE;
	}
	public void setLST_PNT_DATE(String lST_PNT_DATE) {
		LST_PNT_DATE = lST_PNT_DATE;
	}
	public String getAPROV_EMP_ID() {
		return APROV_EMP_ID;
	}
	public void setAPROV_EMP_ID(String aPROV_EMP_ID) {
		APROV_EMP_ID = aPROV_EMP_ID;
	}
	public String getDOC_NO() {
		return DOC_NO;
	}
	public void setDOC_NO(String dOC_NO) {
		DOC_NO = dOC_NO;
	}
	public String getCRE_BRH() {
		return CRE_BRH;
	}
	public void setCRE_BRH(String cRE_BRH) {
		CRE_BRH = cRE_BRH;
	}
	public String getGOV_INT_TYP() {
		return GOV_INT_TYP;
	}
	public void setGOV_INT_TYP(String gOV_INT_TYP) {
		GOV_INT_TYP = gOV_INT_TYP;
	}
	public String getALC_BAL() {
		return ALC_BAL;
	}
	public void setALC_BAL(String aLC_BAL) {
		ALC_BAL = aLC_BAL;
	}
	public String getAG_FEE_BAL() {
		return AG_FEE_BAL;
	}
	public void setAG_FEE_BAL(String aG_FEE_BAL) {
		AG_FEE_BAL = aG_FEE_BAL;
	}
	public String getAUTO_PAY_ACCT() {
		return AUTO_PAY_ACCT;
	}
	public void setAUTO_PAY_ACCT(String aUTO_PAY_ACCT) {
		AUTO_PAY_ACCT = aUTO_PAY_ACCT;
	}
	public String getAUTO_PAY_SRC() {
		return AUTO_PAY_SRC;
	}
	public void setAUTO_PAY_SRC(String aUTO_PAY_SRC) {
		AUTO_PAY_SRC = aUTO_PAY_SRC;
	}
	public String getGOV_INTEDATE() {
		return GOV_INTEDATE;
	}
	public void setGOV_INTEDATE(String gOV_INTEDATE) {
		GOV_INTEDATE = gOV_INTEDATE;
	}
	public String getGOV_IDATE() {
		return GOV_IDATE;
	}
	public void setGOV_IDATE(String gOV_IDATE) {
		GOV_IDATE = gOV_IDATE;
	}
	public String getGOV_EDATE() {
		return GOV_EDATE;
	}
	public void setGOV_EDATE(String gOV_EDATE) {
		GOV_EDATE = gOV_EDATE;
	}
	public String getDLY_COD() {
		return DLY_COD;
	}
	public void setDLY_COD(String dLY_COD) {
		DLY_COD = dLY_COD;
	}
	public String getSUB_COD() {
		return SUB_COD;
	}
	public void setSUB_COD(String sUB_COD) {
		SUB_COD = sUB_COD;
	}
	public String getRATE() {
		return RATE;
	}
	public void setRATE(String rATE) {
		RATE = rATE;
	}
	public String getGOV_RATE() {
		return GOV_RATE;
	}
	public void setGOV_RATE(String gOV_RATE) {
		GOV_RATE = gOV_RATE;
	}
	public String getREV_DATE() {
		return REV_DATE;
	}
	public void setREV_DATE(String rEV_DATE) {
		REV_DATE = rEV_DATE;
	}
	public String getRISK_RANK() {
		return RISK_RANK;
	}
	public void setRISK_RANK(String rISK_RANK) {
		RISK_RANK = rISK_RANK;
	}
	public String getOVD_DATE() {
		return OVD_DATE;
	}
	public void setOVD_DATE(String oVD_DATE) {
		OVD_DATE = oVD_DATE;
	}
	public String getPARTIAL_FLG() {
		return PARTIAL_FLG;
	}
	public void setPARTIAL_FLG(String pARTIAL_FLG) {
		PARTIAL_FLG = pARTIAL_FLG;
	}
	public String getOVD_ACCT() {
		return OVD_ACCT;
	}
	public void setOVD_ACCT(String oVD_ACCT) {
		OVD_ACCT = oVD_ACCT;
	}
	public String getOVD_CLS_DATE() {
		return OVD_CLS_DATE;
	}
	public void setOVD_CLS_DATE(String oVD_CLS_DATE) {
		OVD_CLS_DATE = oVD_CLS_DATE;
	}
	public String getB_GOV_INT_TYP() {
		return B_GOV_INT_TYP;
	}
	public void setB_GOV_INT_TYP(String b_GOV_INT_TYP) {
		B_GOV_INT_TYP = b_GOV_INT_TYP;
	}
	public String getWORK_FLG() {
		return WORK_FLG;
	}
	public void setWORK_FLG(String wORK_FLG) {
		WORK_FLG = wORK_FLG;
	}
	public String getTRU_IND() {
		return TRU_IND;
	}
	public void setTRU_IND(String tRU_IND) {
		TRU_IND = tRU_IND;
	}
	public String getEXT_DUE_DATE() {
		return EXT_DUE_DATE;
	}
	public void setEXT_DUE_DATE(String eXT_DUE_DATE) {
		EXT_DUE_DATE = eXT_DUE_DATE;
	}
	public String getCLS_DATE() {
		return CLS_DATE;
	}
	public void setCLS_DATE(String cLS_DATE) {
		CLS_DATE = cLS_DATE;
	}
	public String getREASON() {
		return REASON;
	}
	public void setREASON(String rEASON) {
		REASON = rEASON;
	}
	public String getREASON_RMK() {
		return REASON_RMK;
	}
	public void setREASON_RMK(String rEASON_RMK) {
		REASON_RMK = rEASON_RMK;
	}
	public String getMSG_TYPE() {
		return MSG_TYPE;
	}
	public void setMSG_TYPE(String mSG_TYPE) {
		MSG_TYPE = mSG_TYPE;
	}
	public String getAUTO_PAY_ACH() {
		return AUTO_PAY_ACH;
	}
	public void setAUTO_PAY_ACH(String aUTO_PAY_ACH) {
		AUTO_PAY_ACH = aUTO_PAY_ACH;
	}
	
	
	
}