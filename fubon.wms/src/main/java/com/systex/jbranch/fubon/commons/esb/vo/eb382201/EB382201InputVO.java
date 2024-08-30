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
public class EB382201InputVO {
	@XmlElement
	private String ACNO_SL;	//就學帳號
	@XmlElement
	private String YR_TERM;	//學年度-學期
	@XmlElement
	private String LOAN_AMT;	//總額度
	@XmlElement
	private String NAME_COD;	//戶名代號
	@XmlElement
	private String ADDR_COD;	//地址代號
	@XmlElement
	private String TEL_COD_1;	//電話代號１
	@XmlElement
	private String TEL_COD_2;	//電話代號２
	@XmlElement
	private String TEL_COD_3;	//電話代號３
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
	private String INT_CYCLE;	//應繳息日
	@XmlElement
	private String LST_INT_SDATE;	//上次應繳息日
	@XmlElement
	private String DOC_NO;	//借據號碼
	@XmlElement
	private String GOV_INT_TYP;	//自付利息註記
	@XmlElement
	private String AUTO_PAY_ACCT;	//自動扣帳帳號
	@XmlElement
	private String REV_DATE;	//覆審日期
	@XmlElement
	private String RISK_RANK;	//風險等級
	@XmlElement
	private String WORK_FLG;	//在職專班註記
	@XmlElement
	private String TRU_IND;	//信保註記
	@XmlElement
	private String REASON;	//教育階段
	@XmlElement
	private String MSG_TYPE;	//簡訊發送註記
	
	
	public String getACNO_SL() {
		return ACNO_SL;
	}
	public void setACNO_SL(String aCNO_SL) {
		ACNO_SL = aCNO_SL;
	}
	public String getYR_TERM() {
		return YR_TERM;
	}
	public void setYR_TERM(String yR_TERM) {
		YR_TERM = yR_TERM;
	}
	public String getLOAN_AMT() {
		return LOAN_AMT;
	}
	public void setLOAN_AMT(String lOAN_AMT) {
		LOAN_AMT = lOAN_AMT;
	}
	public String getNAME_COD() {
		return NAME_COD;
	}
	public void setNAME_COD(String nAME_COD) {
		NAME_COD = nAME_COD;
	}
	public String getADDR_COD() {
		return ADDR_COD;
	}
	public void setADDR_COD(String aDDR_COD) {
		ADDR_COD = aDDR_COD;
	}
	public String getTEL_COD_1() {
		return TEL_COD_1;
	}
	public void setTEL_COD_1(String tEL_COD_1) {
		TEL_COD_1 = tEL_COD_1;
	}
	public String getTEL_COD_2() {
		return TEL_COD_2;
	}
	public void setTEL_COD_2(String tEL_COD_2) {
		TEL_COD_2 = tEL_COD_2;
	}
	public String getTEL_COD_3() {
		return TEL_COD_3;
	}
	public void setTEL_COD_3(String tEL_COD_3) {
		TEL_COD_3 = tEL_COD_3;
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
	public String getDOC_NO() {
		return DOC_NO;
	}
	public void setDOC_NO(String dOC_NO) {
		DOC_NO = dOC_NO;
	}
	public String getGOV_INT_TYP() {
		return GOV_INT_TYP;
	}
	public void setGOV_INT_TYP(String gOV_INT_TYP) {
		GOV_INT_TYP = gOV_INT_TYP;
	}
	public String getAUTO_PAY_ACCT() {
		return AUTO_PAY_ACCT;
	}
	public void setAUTO_PAY_ACCT(String aUTO_PAY_ACCT) {
		AUTO_PAY_ACCT = aUTO_PAY_ACCT;
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
	public String getREASON() {
		return REASON;
	}
	public void setREASON(String rEASON) {
		REASON = rEASON;
	}
	public String getMSG_TYPE() {
		return MSG_TYPE;
	}
	public void setMSG_TYPE(String mSG_TYPE) {
		MSG_TYPE = mSG_TYPE;
	}

}