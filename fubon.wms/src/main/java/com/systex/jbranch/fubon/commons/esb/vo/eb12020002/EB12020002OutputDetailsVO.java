package com.systex.jbranch.fubon.commons.esb.vo.eb12020002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class EB12020002OutputDetailsVO {
	
	@XmlElement
	private String ACT_TYP;	//帳號類別
	@XmlElement
	private String ACNO;	//定存帳號
	@XmlElement
	private String BRA;	//分行
	@XmlElement
	private String CUR;	//幣別
	@XmlElement
	private String OPN_DPR_AMT;	//存單金額
	@XmlElement
	private String OPN_DPR_AMT_TWD;	//折台金額
	@XmlElement
	private String SLIP_NO;	//存單號碼
	@XmlElement
	private String DPR_MM;	//存期(月)
	@XmlElement
	private String DPR_DDD;	//存期(日)
	@XmlElement
	private String BK_VALUE;	//存單起日
	@XmlElement
	private String DUE_DTE;	//存單迄日
	@XmlElement
	private String CLS_DTE;	//解約日
	@XmlElement
	private String AUTO_TR_TYP;	//續存方式
	@XmlElement
	private String AUTO_TR_CNT;	//約定續存次數
	@XmlElement
	private String AUTO_TR_ACT;	//已續存次數
	@XmlElement
	private String AUTO_TR_LEFT;	//剩餘續存次數
	@XmlElement
	private String TR_ACNO;	//台定轉帳帳號
	@XmlElement
	private String INT_TYP;	//利率別
	@XmlElement
	private String INT_RATE;	//訂約利率
	@XmlElement
	private String INT_PAY_TYP;	//利息領取方式
	@XmlElement
	private String LST_INT_DATE;	//上次領息迄日
	@XmlElement
	private String CRLN_UTL;	//已質借金額
	@XmlElement
	private String LOAN_BAL;	//可質借額度
	@XmlElement
	private String ACM_DP_CNT;	//已繳納期數
	@XmlElement
	private String ACNO_SA;	//本利轉入帳號
	@XmlElement
	private String SLIP_STS;	//定存狀態
	@XmlElement
	private String PROD_NAME;	//外定專案名稱
	@XmlElement
	private String INT;	//原幣淨利息
	@XmlElement
	private String DR_CUR;	//扣款幣別
	@XmlElement
	private String DR_AMT;	//扣款金額
	@XmlElement
	private String FXRATE;	//扣款匯率
	@XmlElement
	private String WEEK_FLG;	//週存單註記(外定專用)
	@XmlElement
	private String INT_UN_SIGN;	//解約應付利息(正負號)
	@XmlElement
	private String INT_UN;	//解約應付利息
	@XmlElement
	private String TR_ACNO_TYP;	//綜活帳號類別
	

	
	public String getBRA() {
		return BRA;
	}
	public void setBRA(String bRA) {
		BRA = bRA;
	}
	public String getOPN_DPR_AMT_TWD() {
		return OPN_DPR_AMT_TWD;
	}
	public void setOPN_DPR_AMT_TWD(String oPN_DPR_AMT_TWD) {
		OPN_DPR_AMT_TWD = oPN_DPR_AMT_TWD;
	}
	public String getACT_TYP() {
		return ACT_TYP;
	}
	public void setACT_TYP(String aCT_TYP) {
		ACT_TYP = aCT_TYP;
	}
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public String getCUR() {
		return CUR;
	}
	public void setCUR(String cUR) {
		CUR = cUR;
	}
	public String getOPN_DPR_AMT() {
		return OPN_DPR_AMT;
	}
	public void setOPN_DPR_AMT(String oPN_DPR_AMT) {
		OPN_DPR_AMT = oPN_DPR_AMT;
	}
	public String getSLIP_NO() {
		return SLIP_NO;
	}
	public void setSLIP_NO(String sLIP_NO) {
		SLIP_NO = sLIP_NO;
	}
	public String getDPR_MM() {
		return DPR_MM;
	}
	public void setDPR_MM(String dPR_MM) {
		DPR_MM = dPR_MM;
	}
	public String getDPR_DDD() {
		return DPR_DDD;
	}
	public void setDPR_DDD(String dPR_DDD) {
		DPR_DDD = dPR_DDD;
	}
	public String getBK_VALUE() {
		return BK_VALUE;
	}
	public void setBK_VALUE(String bK_VALUE) {
		BK_VALUE = bK_VALUE;
	}
	public String getDUE_DTE() {
		return DUE_DTE;
	}
	public void setDUE_DTE(String dUE_DTE) {
		DUE_DTE = dUE_DTE;
	}
	public String getCLS_DTE() {
		return CLS_DTE;
	}
	public void setCLS_DTE(String cLS_DTE) {
		CLS_DTE = cLS_DTE;
	}
	public String getAUTO_TR_TYP() {
		return AUTO_TR_TYP;
	}
	public void setAUTO_TR_TYP(String aUTO_TR_TYP) {
		AUTO_TR_TYP = aUTO_TR_TYP;
	}
	public String getAUTO_TR_CNT() {
		return AUTO_TR_CNT;
	}
	public void setAUTO_TR_CNT(String aUTO_TR_CNT) {
		AUTO_TR_CNT = aUTO_TR_CNT;
	}
	public String getAUTO_TR_ACT() {
		return AUTO_TR_ACT;
	}
	public void setAUTO_TR_ACT(String aUTO_TR_ACT) {
		AUTO_TR_ACT = aUTO_TR_ACT;
	}
	public String getAUTO_TR_LEFT() {
		return AUTO_TR_LEFT;
	}
	public void setAUTO_TR_LEFT(String aUTO_TR_LEFT) {
		AUTO_TR_LEFT = aUTO_TR_LEFT;
	}
	public String getTR_ACNO() {
		return TR_ACNO;
	}
	public void setTR_ACNO(String tR_ACNO) {
		TR_ACNO = tR_ACNO;
	}
	public String getINT_TYP() {
		return INT_TYP;
	}
	public void setINT_TYP(String iNT_TYP) {
		INT_TYP = iNT_TYP;
	}
	public String getINT_RATE() {
		return INT_RATE;
	}
	public void setINT_RATE(String iNT_RATE) {
		INT_RATE = iNT_RATE;
	}
	public String getINT_PAY_TYP() {
		return INT_PAY_TYP;
	}
	public void setINT_PAY_TYP(String iNT_PAY_TYP) {
		INT_PAY_TYP = iNT_PAY_TYP;
	}
	public String getLST_INT_DATE() {
		return LST_INT_DATE;
	}
	public void setLST_INT_DATE(String lST_INT_DATE) {
		LST_INT_DATE = lST_INT_DATE;
	}
	public String getCRLN_UTL() {
		return CRLN_UTL;
	}
	public void setCRLN_UTL(String cRLN_UTL) {
		CRLN_UTL = cRLN_UTL;
	}
	public String getLOAN_BAL() {
		return LOAN_BAL;
	}
	public void setLOAN_BAL(String lOAN_BAL) {
		LOAN_BAL = lOAN_BAL;
	}
	public String getACM_DP_CNT() {
		return ACM_DP_CNT;
	}
	public void setACM_DP_CNT(String aCM_DP_CNT) {
		ACM_DP_CNT = aCM_DP_CNT;
	}
	public String getACNO_SA() {
		return ACNO_SA;
	}
	public void setACNO_SA(String aCNO_SA) {
		ACNO_SA = aCNO_SA;
	}
	public String getSLIP_STS() {
		return SLIP_STS;
	}
	public void setSLIP_STS(String sLIP_STS) {
		SLIP_STS = sLIP_STS;
	}
	public String getPROD_NAME() {
		return PROD_NAME;
	}
	public void setPROD_NAME(String pROD_NAME) {
		PROD_NAME = pROD_NAME;
	}
	public String getINT() {
		return INT;
	}
	public void setINT(String iNT) {
		INT = iNT;
	}
	public String getDR_CUR() {
		return DR_CUR;
	}
	public void setDR_CUR(String dR_CUR) {
		DR_CUR = dR_CUR;
	}
	public String getDR_AMT() {
		return DR_AMT;
	}
	public void setDR_AMT(String dR_AMT) {
		DR_AMT = dR_AMT;
	}
	public String getFXRATE() {
		return FXRATE;
	}
	public void setFXRATE(String fXRATE) {
		FXRATE = fXRATE;
	}
	public String getWEEK_FLG() {
		return WEEK_FLG;
	}
	public void setWEEK_FLG(String wEEK_FLG) {
		WEEK_FLG = wEEK_FLG;
	}
	public String getINT_UN_SIGN() {
		return INT_UN_SIGN;
	}
	public void setINT_UN_SIGN(String iNT_UN_SIGN) {
		INT_UN_SIGN = iNT_UN_SIGN;
	}
	public String getINT_UN() {
		return INT_UN;
	}
	public void setINT_UN(String iNT_UN) {
		INT_UN = iNT_UN;
	}
	public String getTR_ACNO_TYP() {
		return TR_ACNO_TYP;
	}
	public void setTR_ACNO_TYP(String tR_ACNO_TYP) {
		TR_ACNO_TYP = tR_ACNO_TYP;
	}
	
	
}