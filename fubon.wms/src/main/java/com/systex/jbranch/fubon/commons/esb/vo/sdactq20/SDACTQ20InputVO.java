package com.systex.jbranch.fubon.commons.esb.vo.sdactq20;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created Created on 2023/08/30
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ20InputVO {
	@XmlElement
	private String COMFIRM;			//電文次數 1:檢核 2:確認
	@XmlElement
	private String INV_TARGET;		//投資標的 XXX/XXX => 放CURRENCY_PAIR
	@XmlElement
	private String CURR_PAIR;		//商品幣別 人民幣只能處理CNY, 不可以有CNH
	@XmlElement
	private String RISKCATE_ID;		//商品風險等級
	@XmlElement
	private BigDecimal PROD_MONS;		//天期
	@XmlElement
	private BigDecimal MIN_BUY_AMT;		//最低承作金額
	@XmlElement
	private BigDecimal UNIT_BUY_AMT;	//累加金額
	@XmlElement
	private BigDecimal STRIKE_PRICE;	//履約價
	@XmlElement
	private BigDecimal FA_PROFIT;		//理專收益
	@XmlElement
	private BigDecimal FTP_AMT;			//小額存款FTP
	@XmlElement
	private String PURCHASE_DATE;	//申購日
	@XmlElement
	private String CHARGE_DATE;		//扣款(起息)日
	@XmlElement
	private String EXCHANGE_DATE;	//比價日
	@XmlElement
	private String DUE_DATE;		//到期(入帳)日
	@XmlElement
	private String PROD_DAYS;		//產品天期
	@XmlElement
	private BigDecimal GT_STRIKE_RATE;	//大於履約價
	@XmlElement
	private BigDecimal GT_DIVIDEND_AMT;	//大於履約價付息金額
	@XmlElement
	private BigDecimal LT_STRIKE_RATE;	//小於履約價
	@XmlElement
	private BigDecimal LT_DIVIDENT_AMT;	//小於履約價付息金額	
	@XmlElement
	private String OBU_YN;			//OBU註記：Y為OBU客戶, N非OBU客戶
	@XmlElement
	private BigDecimal TRADER_CHARGE;	//交易員CHARGE
    @XmlElement
	private String IVID;        //客戶證號
    @XmlElement
	private String IVBRH;       //推薦分行別
    @XmlElement
	private String RECBRH;      //錄音分行別
    @XmlElement
	private String SDPRD;       //產品編號
    @XmlElement
	private String IVCUAC;      //活存帳號
    @XmlElement
	private String IVTDAC;      //定存帳號
    @XmlElement
	private BigDecimal IVAMT2;  //簽約金額
    @XmlElement
	private String RECNO;       //錄音序號
    @XmlElement
	private String AGENT;       //解說專員
    @XmlElement
	private String TXBOSS;      //覆核主管
    @XmlElement
	private String TRADERID;    //授權交易人員
    
	public String getCOMFIRM() {
		return COMFIRM;
	}
	public void setCOMFIRM(String cOMFIRM) {
		COMFIRM = cOMFIRM;
	}
	public String getINV_TARGET() {
		return INV_TARGET;
	}
	public void setINV_TARGET(String iNV_TARGET) {
		INV_TARGET = iNV_TARGET;
	}
	public String getCURR_PAIR() {
		return CURR_PAIR;
	}
	public void setCURR_PAIR(String cURR_PAIR) {
		CURR_PAIR = cURR_PAIR;
	}
	public String getRISKCATE_ID() {
		return RISKCATE_ID;
	}
	public void setRISKCATE_ID(String rISKCATE_ID) {
		RISKCATE_ID = rISKCATE_ID;
	}
	public BigDecimal getPROD_MONS() {
		return PROD_MONS;
	}
	public void setPROD_MONS(BigDecimal pROD_MONS) {
		PROD_MONS = pROD_MONS;
	}
	public BigDecimal getMIN_BUY_AMT() {
		return MIN_BUY_AMT;
	}
	public void setMIN_BUY_AMT(BigDecimal mIN_BUY_AMT) {
		MIN_BUY_AMT = mIN_BUY_AMT;
	}
	public BigDecimal getUNIT_BUY_AMT() {
		return UNIT_BUY_AMT;
	}
	public void setUNIT_BUY_AMT(BigDecimal uNIT_BUY_AMT) {
		UNIT_BUY_AMT = uNIT_BUY_AMT;
	}
	public BigDecimal getSTRIKE_PRICE() {
		return STRIKE_PRICE;
	}
	public void setSTRIKE_PRICE(BigDecimal sTRIKE_PRICE) {
		STRIKE_PRICE = sTRIKE_PRICE;
	}
	public BigDecimal getFA_PROFIT() {
		return FA_PROFIT;
	}
	public void setFA_PROFIT(BigDecimal fA_PROFIT) {
		FA_PROFIT = fA_PROFIT;
	}
	public BigDecimal getFTP_AMT() {
		return FTP_AMT;
	}
	public void setFTP_AMT(BigDecimal fTP_AMT) {
		FTP_AMT = fTP_AMT;
	}
	public String getPURCHASE_DATE() {
		return PURCHASE_DATE;
	}
	public void setPURCHASE_DATE(String pURCHASE_DATE) {
		PURCHASE_DATE = pURCHASE_DATE;
	}
	public String getCHARGE_DATE() {
		return CHARGE_DATE;
	}
	public void setCHARGE_DATE(String cHARGE_DATE) {
		CHARGE_DATE = cHARGE_DATE;
	}
	public String getEXCHANGE_DATE() {
		return EXCHANGE_DATE;
	}
	public void setEXCHANGE_DATE(String eXCHANGE_DATE) {
		EXCHANGE_DATE = eXCHANGE_DATE;
	}
	public String getDUE_DATE() {
		return DUE_DATE;
	}
	public void setDUE_DATE(String dUE_DATE) {
		DUE_DATE = dUE_DATE;
	}
	public String getPROD_DAYS() {
		return PROD_DAYS;
	}
	public void setPROD_DAYS(String pROD_DAYS) {
		PROD_DAYS = pROD_DAYS;
	}
	public BigDecimal getGT_STRIKE_RATE() {
		return GT_STRIKE_RATE;
	}
	public void setGT_STRIKE_RATE(BigDecimal gT_STRIKE_RATE) {
		GT_STRIKE_RATE = gT_STRIKE_RATE;
	}
	public BigDecimal getGT_DIVIDEND_AMT() {
		return GT_DIVIDEND_AMT;
	}
	public void setGT_DIVIDEND_AMT(BigDecimal gT_DIVIDEND_AMT) {
		GT_DIVIDEND_AMT = gT_DIVIDEND_AMT;
	}
	public BigDecimal getLT_STRIKE_RATE() {
		return LT_STRIKE_RATE;
	}
	public void setLT_STRIKE_RATE(BigDecimal lT_STRIKE_RATE) {
		LT_STRIKE_RATE = lT_STRIKE_RATE;
	}
	public BigDecimal getLT_DIVIDENT_AMT() {
		return LT_DIVIDENT_AMT;
	}
	public void setLT_DIVIDENT_AMT(BigDecimal lT_DIVIDENT_AMT) {
		LT_DIVIDENT_AMT = lT_DIVIDENT_AMT;
	}
	public String getOBU_YN() {
		return OBU_YN;
	}
	public void setOBU_YN(String oBU_YN) {
		OBU_YN = oBU_YN;
	}
	public BigDecimal getTRADER_CHARGE() {
		return TRADER_CHARGE;
	}
	public void setTRADER_CHARGE(BigDecimal tRADER_CHARGE) {
		TRADER_CHARGE = tRADER_CHARGE;
	}
	public String getIVID() {
		return IVID;
	}
	public void setIVID(String iVID) {
		IVID = iVID;
	}
	public String getIVBRH() {
		return IVBRH;
	}
	public void setIVBRH(String iVBRH) {
		IVBRH = iVBRH;
	}
	public String getRECBRH() {
		return RECBRH;
	}
	public void setRECBRH(String rECBRH) {
		RECBRH = rECBRH;
	}
	public String getSDPRD() {
		return SDPRD;
	}
	public void setSDPRD(String sDPRD) {
		SDPRD = sDPRD;
	}
	public String getIVCUAC() {
		return IVCUAC;
	}
	public void setIVCUAC(String iVCUAC) {
		IVCUAC = iVCUAC;
	}
	public String getIVTDAC() {
		return IVTDAC;
	}
	public void setIVTDAC(String iVTDAC) {
		IVTDAC = iVTDAC;
	}
	public BigDecimal getIVAMT2() {
		return IVAMT2;
	}
	public void setIVAMT2(BigDecimal iVAMT2) {
		IVAMT2 = iVAMT2;
	}
	public String getRECNO() {
		return RECNO;
	}
	public void setRECNO(String rECNO) {
		RECNO = rECNO;
	}
	public String getAGENT() {
		return AGENT;
	}
	public void setAGENT(String aGENT) {
		AGENT = aGENT;
	}
	public String getTXBOSS() {
		return TXBOSS;
	}
	public void setTXBOSS(String tXBOSS) {
		TXBOSS = tXBOSS;
	}
	public String getTRADERID() {
		return TRADERID;
	}
	public void setTRADERID(String tRADERID) {
		TRADERID = tRADERID;
	}
    
}
