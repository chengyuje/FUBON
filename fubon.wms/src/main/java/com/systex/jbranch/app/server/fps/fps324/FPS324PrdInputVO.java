package com.systex.jbranch.app.server.fps.fps324;

import java.math.BigDecimal;


public class FPS324PrdInputVO {

    private String PLAN_ID;
    private BigDecimal SEQNO;
    private String PRD_ID;
    private String PTYPE;
    private String RISK_TYPE;
    private String CURRENCY_TYPE;
    private String TRUST_CURR;
    private String RAISE_FUND;
    private String MARKET_CIS;
    private BigDecimal PURCHASE_ORG_AMT;
    private BigDecimal PURCHASE_TWD_AMT;
    private BigDecimal PORTFOLIO_RATIO;
    private BigDecimal LIMIT_ORG_AMT;
    private String PORTFOLIO_TYPE;
    private String FND_CODE;
    private String TXN_TYPE;//交易指示
    private String INV_TYPE;//投資方式
    private String STATUS;
    private BigDecimal EX_RATE;
    private BigDecimal PURCHASE_ORG_AMT_ORDER;
    private BigDecimal PURCHASE_TWD_AMT_ORDER;
    private String ORDER_STATUS;
    private String PRD_SOURCE_FLAG;
    private String TARGETS;

    private String action;

    public FPS324PrdInputVO() {
    }

	public String getPLAN_ID() {
		return PLAN_ID;
	}

	public void setPLAN_ID(String pLAN_ID) {
		PLAN_ID = pLAN_ID;
	}

	public BigDecimal getSEQNO() {
		return SEQNO;
	}

	public void setSEQNO(BigDecimal sEQNO) {
		SEQNO = sEQNO;
	}

	public String getPRD_ID() {
		return PRD_ID;
	}

	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}

	public String getPTYPE() {
		return PTYPE;
	}

	public void setPTYPE(String pTYPE) {
		PTYPE = pTYPE;
	}

	public String getRISK_TYPE() {
		return RISK_TYPE;
	}

	public void setRISK_TYPE(String rISK_TYPE) {
		RISK_TYPE = rISK_TYPE;
	}

	public String getCURRENCY_TYPE() {
		return CURRENCY_TYPE;
	}

	public void setCURRENCY_TYPE(String cURRENCY_TYPE) {
		CURRENCY_TYPE = cURRENCY_TYPE;
	}

	public String getTRUST_CURR() {
		return TRUST_CURR;
	}

	public void setTRUST_CURR(String tRUST_CURR) {
		TRUST_CURR = tRUST_CURR;
	}

	public String getRAISE_FUND() {
		return RAISE_FUND;
	}

	public void setRAISE_FUND(String rAISE_FUND) {
		RAISE_FUND = rAISE_FUND;
	}

	public String getMARKET_CIS() {
		return MARKET_CIS;
	}

	public void setMARKET_CIS(String mARKET_CIS) {
		MARKET_CIS = mARKET_CIS;
	}

	public BigDecimal getPURCHASE_ORG_AMT() {
		return PURCHASE_ORG_AMT;
	}

	public void setPURCHASE_ORG_AMT(BigDecimal pURCHASE_ORG_AMT) {
		PURCHASE_ORG_AMT = pURCHASE_ORG_AMT;
	}

	public BigDecimal getPURCHASE_TWD_AMT() {
		return PURCHASE_TWD_AMT;
	}

	public void setPURCHASE_TWD_AMT(BigDecimal pURCHASE_TWD_AMT) {
		PURCHASE_TWD_AMT = pURCHASE_TWD_AMT;
	}

	public BigDecimal getPORTFOLIO_RATIO() {
		return PORTFOLIO_RATIO;
	}

	public void setPORTFOLIO_RATIO(BigDecimal pORTFOLIO_RATIO) {
		PORTFOLIO_RATIO = pORTFOLIO_RATIO;
	}

	public BigDecimal getLIMIT_ORG_AMT() {
		return LIMIT_ORG_AMT;
	}

	public void setLIMIT_ORG_AMT(BigDecimal lIMIT_ORG_AMT) {
		LIMIT_ORG_AMT = lIMIT_ORG_AMT;
	}

	public String getPORTFOLIO_TYPE() {
		return PORTFOLIO_TYPE;
	}

	public void setPORTFOLIO_TYPE(String pORTFOLIO_TYPE) {
		PORTFOLIO_TYPE = pORTFOLIO_TYPE;
	}

	public String getFND_CODE() {
		return FND_CODE;
	}

	public void setFND_CODE(String fND_CODE) {
		FND_CODE = fND_CODE;
	}

	public String getTXN_TYPE() {
		return TXN_TYPE;
	}

	public void setTXN_TYPE(String tXN_TYPE) {
		TXN_TYPE = tXN_TYPE;
	}
	
	public String getINV_TYPE() {
		return INV_TYPE;
	}

	public void setINV_TYPE(String iNV_TYPE) {
		INV_TYPE = iNV_TYPE;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public BigDecimal getEX_RATE() {
		return EX_RATE;
	}

	public void setEX_RATE(BigDecimal eX_RATE) {
		EX_RATE = eX_RATE;
	}

	public BigDecimal getPURCHASE_ORG_AMT_ORDER() {
		return PURCHASE_ORG_AMT_ORDER;
	}

	public void setPURCHASE_ORG_AMT_ORDER(BigDecimal pURCHASE_ORG_AMT_ORDER) {
		PURCHASE_ORG_AMT_ORDER = pURCHASE_ORG_AMT_ORDER;
	}

	public BigDecimal getPURCHASE_TWD_AMT_ORDER() {
		return PURCHASE_TWD_AMT_ORDER;
	}

	public void setPURCHASE_TWD_AMT_ORDER(BigDecimal pURCHASE_TWD_AMT_ORDER) {
		PURCHASE_TWD_AMT_ORDER = pURCHASE_TWD_AMT_ORDER;
	}

	public String getORDER_STATUS() {
		return ORDER_STATUS;
	}

	public void setORDER_STATUS(String oRDER_STATUS) {
		ORDER_STATUS = oRDER_STATUS;
	}

	public String getPRD_SOURCE_FLAG() {
		return PRD_SOURCE_FLAG;
	}

	public void setPRD_SOURCE_FLAG(String pRD_SOURCE_FLAG) {
		PRD_SOURCE_FLAG = pRD_SOURCE_FLAG;
	}
	
	public String getTARGETS() {
		return TARGETS;
	}
	public void setTARGETS(String tARGETS) {
		TARGETS = tARGETS;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
