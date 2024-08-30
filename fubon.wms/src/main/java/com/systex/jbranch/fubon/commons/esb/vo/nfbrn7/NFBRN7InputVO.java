package com.systex.jbranch.fubon.commons.esb.vo.nfbrn7;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN7InputVO {
	@XmlElement
	private String ERR_COD;		//錯誤碼
	@XmlElement
	private String ERR_TXT;		//錯誤訊息
	@XmlElement
	private String CUST_ID;		//客戶ID 
	@XmlElement
	private String BRANCH_NBR;	//分行別(信託行)
	@XmlElement
	private String TRUST_CURR_TYPE;		//信託業務別
	@XmlElement
	private String TRADE_SUB_TYPE;		//交易類別
	@XmlElement
	private String TRADE_DATE;		//交易日期
	@XmlElement
	private String EFF_DATE;		//生效日期
	@XmlElement
	private String PROD_ID;		//基金代號/基金套餐
	@XmlElement
	private String GROUP_IFA;	//優惠團體代碼
	@XmlElement
	private BigDecimal PURCHASE_AMT_L;	//扣款金額低	
	@XmlElement
	private BigDecimal PURCHASE_AMT_M;	//扣款金額中
	@XmlElement
	private BigDecimal PURCHASE_AMT_H;	//扣款金額高
	@XmlElement
	private String BTH_COUPON;		//生日券
	@XmlElement
	private String AUTO_CX;		//自動換匯
	@XmlElement
	private String BARGAIN_APPLY_SEQ;	//議價編號
	
	
	public String getERR_COD() {
		return ERR_COD;
	}
	public void setERR_COD(String eRR_COD) {
		ERR_COD = eRR_COD;
	}
	public String getERR_TXT() {
		return ERR_TXT;
	}
	public void setERR_TXT(String eRR_TXT) {
		ERR_TXT = eRR_TXT;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	public String getTRUST_CURR_TYPE() {
		return TRUST_CURR_TYPE;
	}
	public void setTRUST_CURR_TYPE(String tRUST_CURR_TYPE) {
		TRUST_CURR_TYPE = tRUST_CURR_TYPE;
	}
	public String getTRADE_SUB_TYPE() {
		return TRADE_SUB_TYPE;
	}
	public void setTRADE_SUB_TYPE(String tRADE_SUB_TYPE) {
		TRADE_SUB_TYPE = tRADE_SUB_TYPE;
	}
	
	public String getTRADE_DATE() {
		return TRADE_DATE;
	}
	public void setTRADE_DATE(String tRADE_DATE) {
		TRADE_DATE = tRADE_DATE;
	}
	public String getEFF_DATE() {
		return EFF_DATE;
	}
	public void setEFF_DATE(String eFF_DATE) {
		EFF_DATE = eFF_DATE;
	}
	public String getPROD_ID() {
		return PROD_ID;
	}
	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}
	public String getGROUP_IFA() {
		return GROUP_IFA;
	}
	public void setGROUP_IFA(String gROUP_IFA) {
		GROUP_IFA = gROUP_IFA;
	}
	
	public BigDecimal getPURCHASE_AMT_L() {
		return PURCHASE_AMT_L;
	}
	public void setPURCHASE_AMT_L(BigDecimal pURCHASE_AMT_L) {
		PURCHASE_AMT_L = pURCHASE_AMT_L;
	}
	public BigDecimal getPURCHASE_AMT_M() {
		return PURCHASE_AMT_M;
	}
	public void setPURCHASE_AMT_M(BigDecimal pURCHASE_AMT_M) {
		PURCHASE_AMT_M = pURCHASE_AMT_M;
	}
	public BigDecimal getPURCHASE_AMT_H() {
		return PURCHASE_AMT_H;
	}
	public void setPURCHASE_AMT_H(BigDecimal pURCHASE_AMT_H) {
		PURCHASE_AMT_H = pURCHASE_AMT_H;
	}
	public String getBTH_COUPON() {
		return BTH_COUPON;
	}
	public void setBTH_COUPON(String bTH_COUPON) {
		BTH_COUPON = bTH_COUPON;
	}
	public String getAUTO_CX() {
		return AUTO_CX;
	}
	public void setAUTO_CX(String aUTO_CX) {
		AUTO_CX = aUTO_CX;
	}
	public String getBARGAIN_APPLY_SEQ() {
		return BARGAIN_APPLY_SEQ;
	}
	public void setBARGAIN_APPLY_SEQ(String bARGAIN_APPLY_SEQ) {
		BARGAIN_APPLY_SEQ = bARGAIN_APPLY_SEQ;
	}
	
	
	
}
