package com.systex.jbranch.fubon.commons.esb.vo.bkdcd003;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/06.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class BKDCD003OutputDetailVO {
	@XmlElement
	private String TRANID;					//交易編號PK
	@XmlElement
	private String TRADEDATE;				//交易日
	@XmlElement
	private String SPOTRATE;				//參考即期價格
	@XmlElement
	private String EXPIRYDATE;				//比價日
	@XmlElement
	private String DELIVERYDATEAMOUNT;		//到期金額
	@XmlElement
	private String MARKETEVALATIONDATE;		//市場評估日
	@XmlElement
	private String CURRENCY;				//商品幣別
	@XmlElement
	private String DELIVERYDATE;			//到期日
	@XmlElement
	private String STRIKE;					//履約價
	@XmlElement
	private String EXPIRYDATESPOTRATE;		//比價日即期價
	@XmlElement
	private String INTERESTAMOUNT;			//利息金額
	@XmlElement
	private String COMPARETOAMOUNT;			//與本金之百分比
	@XmlElement
	private String MAPPINGCURRENCY;			//相對幣別
	@XmlElement
	private String DCDAMOUNT;				//交易金額
	@XmlElement
	private String YIELD;					//產品收益率
	@XmlElement
	private String CURRENCYCHANGE;			//幣轉狀態
	@XmlElement
	private String TOTALFEE;				//總費用
	@XmlElement
	private String TRADESTATUS;				//交易狀態
	@XmlElement
	private String LSFORM;					//FORMAT_COD
	@XmlElement
	private String DCDAMOUNTNTD;			//參考台幣價格
	@XmlElement
	private String BREAKEVENID;				//保值型
	@XmlElement
	private String EARNAMOUNT;				//收益金額
	@XmlElement
	private String CUSTOMERACCOUNT;			//扣款帳號
	@XmlElement
	private String DCDACCOUNT;				//組合式商品帳號
	@XmlElement
	private String STRIKE2;					//觸發匯率
	@XmlElement
	private String PAYDATE;					//起息日期
		
	
	
	public String getMARKETEVALATIONDATE() {
		return MARKETEVALATIONDATE;
	}
	public void setMARKETEVALATIONDATE(String mARKETEVALATIONDATE) {
		MARKETEVALATIONDATE = mARKETEVALATIONDATE;
	}
	public String getINTERESTAMOUNT() {
		return INTERESTAMOUNT;
	}
	public void setINTERESTAMOUNT(String iNTERESTAMOUNT) {
		INTERESTAMOUNT = iNTERESTAMOUNT;
	}
	public String getCOMPARETOAMOUNT() {
		return COMPARETOAMOUNT;
	}
	public void setCOMPARETOAMOUNT(String cOMPARETOAMOUNT) {
		COMPARETOAMOUNT = cOMPARETOAMOUNT;
	}
	public String getTOTALFEE() {
		return TOTALFEE;
	}
	public void setTOTALFEE(String tOTALFEE) {
		TOTALFEE = tOTALFEE;
	}
	public String getTRADESTATUS() {
		return TRADESTATUS;
	}
	public void setTRADESTATUS(String tRADESTATUS) {
		TRADESTATUS = tRADESTATUS;
	}
	public String getLSFORM() {
		return LSFORM;
	}
	public void setLSFORM(String lSFORM) {
		LSFORM = lSFORM;
	}
	public String getDCDAMOUNTNTD() {
		return DCDAMOUNTNTD;
	}
	public void setDCDAMOUNTNTD(String dCDAMOUNTNTD) {
		DCDAMOUNTNTD = dCDAMOUNTNTD;
	}
	public String getBREAKEVENID() {
		return BREAKEVENID;
	}
	public void setBREAKEVENID(String bREAKEVENID) {
		BREAKEVENID = bREAKEVENID;
	}
	public String getTRANID() {
		return TRANID;
	}
	public void setTRANID(String tRANID) {
		TRANID = tRANID;
	}
	public String getDCDAMOUNT() {
		return DCDAMOUNT;
	}
	public void setDCDAMOUNT(String dCDAMOUNT) {
		DCDAMOUNT = dCDAMOUNT;
	}
	public String getCURRENCY() {
		return CURRENCY;
	}
	public void setCURRENCY(String cURRENCY) {
		CURRENCY = cURRENCY;
	}
	public String getMAPPINGCURRENCY() {
		return MAPPINGCURRENCY;
	}
	public void setMAPPINGCURRENCY(String mAPPINGCURRENCY) {
		MAPPINGCURRENCY = mAPPINGCURRENCY;
	}
	public String getTRADEDATE() {
		return TRADEDATE;
	}
	public void setTRADEDATE(String tRADEDATE) {
		TRADEDATE = tRADEDATE;
	}
	public String getEXPIRYDATE() {
		return EXPIRYDATE;
	}
	public void setEXPIRYDATE(String eXPIRYDATE) {
		EXPIRYDATE = eXPIRYDATE;
	}
	public String getDELIVERYDATE() {
		return DELIVERYDATE;
	}
	public void setDELIVERYDATE(String dELIVERYDATE) {
		DELIVERYDATE = dELIVERYDATE;
	}
	public String getYIELD() {
		return YIELD;
	}
	public void setYIELD(String yIELD) {
		YIELD = yIELD;
	}
	public String getEARNAMOUNT() {
		return EARNAMOUNT;
	}
	public void setEARNAMOUNT(String eARNAMOUNT) {
		EARNAMOUNT = eARNAMOUNT;
	}
	public String getCURRENCYCHANGE() {
		return CURRENCYCHANGE;
	}
	public void setCURRENCYCHANGE(String cURRENCYCHANGE) {
		CURRENCYCHANGE = cURRENCYCHANGE;
	}
	public String getDELIVERYDATEAMOUNT() {
		return DELIVERYDATEAMOUNT;
	}
	public void setDELIVERYDATEAMOUNT(String dELIVERYDATEAMOUNT) {
		DELIVERYDATEAMOUNT = dELIVERYDATEAMOUNT;
	}
	public String getCUSTOMERACCOUNT() {
		return CUSTOMERACCOUNT;
	}
	public void setCUSTOMERACCOUNT(String cUSTOMERACCOUNT) {
		CUSTOMERACCOUNT = cUSTOMERACCOUNT;
	}
	public String getDCDACCOUNT() {
		return DCDACCOUNT;
	}
	public void setDCDACCOUNT(String dCDACCOUNT) {
		DCDACCOUNT = dCDACCOUNT;
	}
	public String getSTRIKE2() {
		return STRIKE2;
	}
	public void setSTRIKE2(String sTRIKE2) {
		STRIKE2 = sTRIKE2;
	}
	public String getPAYDATE() {
		return PAYDATE;
	}
	public void setPAYDATE(String pAYDATE) {
		PAYDATE = pAYDATE;
	}
	public String getSPOTRATE() {
		return SPOTRATE;
	}
	public void setSPOTRATE(String sPOTRATE) {
		SPOTRATE = sPOTRATE;
	}
	public String getSTRIKE() {
		return STRIKE;
	}
	public void setSTRIKE(String sTRIKE) {
		STRIKE = sTRIKE;
	}
	public String getEXPIRYDATESPOTRATE() {
		return EXPIRYDATESPOTRATE;
	}
	public void setEXPIRYDATESPOTRATE(String eXPIRYDATESPOTRATE) {
		EXPIRYDATESPOTRATE = eXPIRYDATESPOTRATE;
	}
	
	
}