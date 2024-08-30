package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_SNINFO_REVIEWVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String ISIN_CODE;

    /** nullable persistent field */
    private String CREDIT_RATING_SP;

    /** nullable persistent field */
    private String CREDIT_RATING_MODDY;

    /** nullable persistent field */
    private String CREDIT_RATING_FITCH;

    /** nullable persistent field */
    private String AVOUCH_CREDIT_RATING_SP;

    /** nullable persistent field */
    private String AVOUCH_CREDIT_RATING_MODDY;

    /** nullable persistent field */
    private String AVOUCH_CREDIT_RATING_FITCH;

    /** nullable persistent field */
    private Timestamp START_DATE_OF_BUYBACK;

    /** nullable persistent field */
    private String FIXED_RATE_DURATION;

    /** nullable persistent field */
    private String CURRENCY_EXCHANGE;

    /** nullable persistent field */
    private BigDecimal FLOATING_DIVIDEND_RATE;

    /** nullable persistent field */
    private String INVESTMENT_TARGETS;

    /** nullable persistent field */
    private BigDecimal CNR_YIELD;

    /** nullable persistent field */
    private BigDecimal RATE_OF_CHANNEL;

    /** nullable persistent field */
    private String PERFORMANCE_REVIEW;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;

    /** nullable persistent field */
    private BigDecimal RATE_GUARANTEEPAY;

    /** nullable persistent field */
    private BigDecimal BASE_AMT_OF_PURCHASE;

    /** nullable persistent field */
    private BigDecimal UNIT_AMT_OF_PURCHASE;

    /** nullable persistent field */
    private String STOCK_BOND_TYPE;

    /** nullable persistent field */
    private String PROJECT;

    /** nullable persistent field */
    private String CUSTOMER_LEVEL;

    /** nullable persistent field */
    private BigDecimal BOND_VALUE;

    public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_SNINFO_REVIEW";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
	public TBPRD_SNINFO_REVIEWVO(BigDecimal sEQ, String pRD_ID, String iSIN_CODE, String cREDIT_RATING_SP,
			String cREDIT_RATING_MODDY, String cREDIT_RATING_FITCH, String aVOUCH_CREDIT_RATING_SP,
			String aVOUCH_CREDIT_RATING_MODDY, String aVOUCH_CREDIT_RATING_FITCH, Timestamp sTART_DATE_OF_BUYBACK,
			String fIXED_RATE_DURATION, String cURRENCY_EXCHANGE, BigDecimal fLOATING_DIVIDEND_RATE,
			String iNVESTMENT_TARGETS, BigDecimal cNR_YIELD, BigDecimal rATE_OF_CHANNEL, String pERFORMANCE_REVIEW,
			String aCT_TYPE, String rEVIEW_STATUS, BigDecimal rATE_GUARANTEEPAY, BigDecimal bASE_AMT_OF_PURCHASE,
			BigDecimal uNIT_AMT_OF_PURCHASE, String sTOCK_BOND_TYPE, String pROJECT, String cUSTOMER_LEVEL,
			BigDecimal bOND_VALUE) {
		super();
		SEQ = sEQ;
		PRD_ID = pRD_ID;
		ISIN_CODE = iSIN_CODE;
		CREDIT_RATING_SP = cREDIT_RATING_SP;
		CREDIT_RATING_MODDY = cREDIT_RATING_MODDY;
		CREDIT_RATING_FITCH = cREDIT_RATING_FITCH;
		AVOUCH_CREDIT_RATING_SP = aVOUCH_CREDIT_RATING_SP;
		AVOUCH_CREDIT_RATING_MODDY = aVOUCH_CREDIT_RATING_MODDY;
		AVOUCH_CREDIT_RATING_FITCH = aVOUCH_CREDIT_RATING_FITCH;
		START_DATE_OF_BUYBACK = sTART_DATE_OF_BUYBACK;
		FIXED_RATE_DURATION = fIXED_RATE_DURATION;
		CURRENCY_EXCHANGE = cURRENCY_EXCHANGE;
		FLOATING_DIVIDEND_RATE = fLOATING_DIVIDEND_RATE;
		INVESTMENT_TARGETS = iNVESTMENT_TARGETS;
		CNR_YIELD = cNR_YIELD;
		RATE_OF_CHANNEL = rATE_OF_CHANNEL;
		PERFORMANCE_REVIEW = pERFORMANCE_REVIEW;
		ACT_TYPE = aCT_TYPE;
		REVIEW_STATUS = rEVIEW_STATUS;
		RATE_GUARANTEEPAY = rATE_GUARANTEEPAY;
		BASE_AMT_OF_PURCHASE = bASE_AMT_OF_PURCHASE;
		UNIT_AMT_OF_PURCHASE = uNIT_AMT_OF_PURCHASE;
		STOCK_BOND_TYPE = sTOCK_BOND_TYPE;
		PROJECT = pROJECT;
		CUSTOMER_LEVEL = cUSTOMER_LEVEL;
		BOND_VALUE = bOND_VALUE;
	}

    /** default constructor */
    public TBPRD_SNINFO_REVIEWVO() {
    }

	/** minimal constructor */
    public TBPRD_SNINFO_REVIEWVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getISIN_CODE() {
        return this.ISIN_CODE;
    }

    public void setISIN_CODE(String ISIN_CODE) {
        this.ISIN_CODE = ISIN_CODE;
    }

    public String getCREDIT_RATING_SP() {
        return this.CREDIT_RATING_SP;
    }

    public void setCREDIT_RATING_SP(String CREDIT_RATING_SP) {
        this.CREDIT_RATING_SP = CREDIT_RATING_SP;
    }

    public String getCREDIT_RATING_MODDY() {
        return this.CREDIT_RATING_MODDY;
    }

    public void setCREDIT_RATING_MODDY(String CREDIT_RATING_MODDY) {
        this.CREDIT_RATING_MODDY = CREDIT_RATING_MODDY;
    }

    public String getCREDIT_RATING_FITCH() {
        return this.CREDIT_RATING_FITCH;
    }

    public void setCREDIT_RATING_FITCH(String CREDIT_RATING_FITCH) {
        this.CREDIT_RATING_FITCH = CREDIT_RATING_FITCH;
    }

    public String getAVOUCH_CREDIT_RATING_SP() {
        return this.AVOUCH_CREDIT_RATING_SP;
    }

    public void setAVOUCH_CREDIT_RATING_SP(String AVOUCH_CREDIT_RATING_SP) {
        this.AVOUCH_CREDIT_RATING_SP = AVOUCH_CREDIT_RATING_SP;
    }

    public String getAVOUCH_CREDIT_RATING_MODDY() {
        return this.AVOUCH_CREDIT_RATING_MODDY;
    }

    public void setAVOUCH_CREDIT_RATING_MODDY(String AVOUCH_CREDIT_RATING_MODDY) {
        this.AVOUCH_CREDIT_RATING_MODDY = AVOUCH_CREDIT_RATING_MODDY;
    }

    public String getAVOUCH_CREDIT_RATING_FITCH() {
        return this.AVOUCH_CREDIT_RATING_FITCH;
    }

    public void setAVOUCH_CREDIT_RATING_FITCH(String AVOUCH_CREDIT_RATING_FITCH) {
        this.AVOUCH_CREDIT_RATING_FITCH = AVOUCH_CREDIT_RATING_FITCH;
    }

    public Timestamp getSTART_DATE_OF_BUYBACK() {
        return this.START_DATE_OF_BUYBACK;
    }

    public void setSTART_DATE_OF_BUYBACK(Timestamp START_DATE_OF_BUYBACK) {
        this.START_DATE_OF_BUYBACK = START_DATE_OF_BUYBACK;
    }

    public String getFIXED_RATE_DURATION() {
        return this.FIXED_RATE_DURATION;
    }

    public void setFIXED_RATE_DURATION(String FIXED_RATE_DURATION) {
        this.FIXED_RATE_DURATION = FIXED_RATE_DURATION;
    }

    public String getCURRENCY_EXCHANGE() {
        return this.CURRENCY_EXCHANGE;
    }

    public void setCURRENCY_EXCHANGE(String CURRENCY_EXCHANGE) {
        this.CURRENCY_EXCHANGE = CURRENCY_EXCHANGE;
    }

    public BigDecimal getFLOATING_DIVIDEND_RATE() {
        return this.FLOATING_DIVIDEND_RATE;
    }

    public void setFLOATING_DIVIDEND_RATE(BigDecimal FLOATING_DIVIDEND_RATE) {
        this.FLOATING_DIVIDEND_RATE = FLOATING_DIVIDEND_RATE;
    }

    public String getINVESTMENT_TARGETS() {
        return this.INVESTMENT_TARGETS;
    }

    public void setINVESTMENT_TARGETS(String INVESTMENT_TARGETS) {
        this.INVESTMENT_TARGETS = INVESTMENT_TARGETS;
    }

    public BigDecimal getCNR_YIELD() {
        return this.CNR_YIELD;
    }

    public void setCNR_YIELD(BigDecimal CNR_YIELD) {
        this.CNR_YIELD = CNR_YIELD;
    }

    public BigDecimal getRATE_OF_CHANNEL() {
        return this.RATE_OF_CHANNEL;
    }

    public void setRATE_OF_CHANNEL(BigDecimal RATE_OF_CHANNEL) {
        this.RATE_OF_CHANNEL = RATE_OF_CHANNEL;
    }

    public String getPERFORMANCE_REVIEW() {
        return this.PERFORMANCE_REVIEW;
    }

    public void setPERFORMANCE_REVIEW(String PERFORMANCE_REVIEW) {
        this.PERFORMANCE_REVIEW = PERFORMANCE_REVIEW;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getREVIEW_STATUS() {
        return this.REVIEW_STATUS;
    }

    public void setREVIEW_STATUS(String REVIEW_STATUS) {
        this.REVIEW_STATUS = REVIEW_STATUS;
    }

    public BigDecimal getRATE_GUARANTEEPAY() {
        return RATE_GUARANTEEPAY;
    }

    public void setRATE_GUARANTEEPAY(BigDecimal RATE_GUARANTEEPAY) {
        this.RATE_GUARANTEEPAY = RATE_GUARANTEEPAY;
    }

    public BigDecimal getBASE_AMT_OF_PURCHASE() {
        return BASE_AMT_OF_PURCHASE;
    }

    public void setBASE_AMT_OF_PURCHASE(BigDecimal BASE_AMT_OF_PURCHASE) {
        this.BASE_AMT_OF_PURCHASE = BASE_AMT_OF_PURCHASE;
    }

    public BigDecimal getUNIT_AMT_OF_PURCHASE() {
        return UNIT_AMT_OF_PURCHASE;
    }

    public void setUNIT_AMT_OF_PURCHASE(BigDecimal UNIT_AMT_OF_PURCHASE) {
        this.UNIT_AMT_OF_PURCHASE = UNIT_AMT_OF_PURCHASE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

    public String getPROJECT() {
        return PROJECT;
    }

    public void setPROJECT(String PROJECT) {
        this.PROJECT = PROJECT;
    }

    public String getCUSTOMER_LEVEL() {
        return CUSTOMER_LEVEL;
    }

    public void setCUSTOMER_LEVEL(String CUSTOMER_LEVEL) {
        this.CUSTOMER_LEVEL = CUSTOMER_LEVEL;
    }

    public String getSTOCK_BOND_TYPE() {
        return STOCK_BOND_TYPE;
    }

    public void setSTOCK_BOND_TYPE(String STOCK_BOND_TYPE) {
        this.STOCK_BOND_TYPE = STOCK_BOND_TYPE;
    }

	public BigDecimal getBOND_VALUE() {
		return BOND_VALUE;
	}

	public void setBOND_VALUE(BigDecimal bOND_VALUE) {
		BOND_VALUE = bOND_VALUE;
	}
}
