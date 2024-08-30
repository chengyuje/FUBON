package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_SIINFOVO extends VOBase {

    /** identifier field */
    private String PRD_ID;

    /** nullable persistent field */
    private Timestamp TRANS_DATE;

    /** nullable persistent field */
    private Timestamp VALUE_DATE;

    /** nullable persistent field */
    private Timestamp START_DATE_OF_BUYBACK;

    /** nullable persistent field */
    private BigDecimal BASE_AMT_OF_PURCHASE;

    /** nullable persistent field */
    private BigDecimal UNIT_AMT_OF_PURCHASE;

    /** nullable persistent field */
    private BigDecimal FREQUENCY_OF_INTEST_PAY;

    /** nullable persistent field */
    private BigDecimal FIXED_DIVIDEND_RATE;

    /** nullable persistent field */
    private String FIXED_RATE_DURATION;

    /** nullable persistent field */
    private BigDecimal FLOATING_DIVIDEND_RATE;

    /** nullable persistent field */
    private String CURRENCY_EXCHANGE;

    /** nullable persistent field */
    private String INVESTMENT_TARGETS;

    /** nullable persistent field */
    private BigDecimal CNR_YIELD;

    /** nullable persistent field */
    private Timestamp INV_SDATE;

    /** nullable persistent field */
    private Timestamp INV_EDATE;

    /** nullable persistent field */
    private Timestamp F_DATE;

    /** nullable persistent field */
    private String PERFORMANCE_REVIEW;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;

    /** nullable persistent field */
    private BigDecimal RATE_OF_RETURN;
    
    /** nullable persistent field */
    private String STOCK_BOND_TYPE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_SIINFO";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_SIINFOVO(String PRD_ID, Timestamp TRANS_DATE, Timestamp VALUE_DATE, Timestamp START_DATE_OF_BUYBACK, BigDecimal BASE_AMT_OF_PURCHASE, BigDecimal UNIT_AMT_OF_PURCHASE, BigDecimal FREQUENCY_OF_INTEST_PAY, BigDecimal FIXED_DIVIDEND_RATE, String FIXED_RATE_DURATION, BigDecimal FLOATING_DIVIDEND_RATE, String CURRENCY_EXCHANGE, String INVESTMENT_TARGETS, BigDecimal CNR_YIELD, Timestamp INV_SDATE, Timestamp INV_EDATE, Timestamp F_DATE, String PERFORMANCE_REVIEW, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, BigDecimal RATE_OF_RETURN, Long version, String STOCK_BOND_TYPE) {
        this.PRD_ID = PRD_ID;
        this.TRANS_DATE = TRANS_DATE;
        this.VALUE_DATE = VALUE_DATE;
        this.START_DATE_OF_BUYBACK = START_DATE_OF_BUYBACK;
        this.BASE_AMT_OF_PURCHASE = BASE_AMT_OF_PURCHASE;
        this.UNIT_AMT_OF_PURCHASE = UNIT_AMT_OF_PURCHASE;
        this.FREQUENCY_OF_INTEST_PAY = FREQUENCY_OF_INTEST_PAY;
        this.FIXED_DIVIDEND_RATE = FIXED_DIVIDEND_RATE;
        this.FIXED_RATE_DURATION = FIXED_RATE_DURATION;
        this.FLOATING_DIVIDEND_RATE = FLOATING_DIVIDEND_RATE;
        this.CURRENCY_EXCHANGE = CURRENCY_EXCHANGE;
        this.INVESTMENT_TARGETS = INVESTMENT_TARGETS;
        this.CNR_YIELD = CNR_YIELD;
        this.INV_SDATE = INV_SDATE;
        this.INV_EDATE = INV_EDATE;
        this.F_DATE = F_DATE;
        this.PERFORMANCE_REVIEW = PERFORMANCE_REVIEW;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.RATE_OF_RETURN = RATE_OF_RETURN;
        this.version = version;
        this.STOCK_BOND_TYPE = STOCK_BOND_TYPE;
    }

    /** default constructor */
    public TBPRD_SIINFOVO() {
    }

    /** minimal constructor */
    public TBPRD_SIINFOVO(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public Timestamp getTRANS_DATE() {
        return this.TRANS_DATE;
    }

    public void setTRANS_DATE(Timestamp TRANS_DATE) {
        this.TRANS_DATE = TRANS_DATE;
    }

    public Timestamp getVALUE_DATE() {
        return this.VALUE_DATE;
    }

    public void setVALUE_DATE(Timestamp VALUE_DATE) {
        this.VALUE_DATE = VALUE_DATE;
    }

    public Timestamp getSTART_DATE_OF_BUYBACK() {
        return this.START_DATE_OF_BUYBACK;
    }

    public void setSTART_DATE_OF_BUYBACK(Timestamp START_DATE_OF_BUYBACK) {
        this.START_DATE_OF_BUYBACK = START_DATE_OF_BUYBACK;
    }

    public BigDecimal getBASE_AMT_OF_PURCHASE() {
        return this.BASE_AMT_OF_PURCHASE;
    }

    public void setBASE_AMT_OF_PURCHASE(BigDecimal BASE_AMT_OF_PURCHASE) {
        this.BASE_AMT_OF_PURCHASE = BASE_AMT_OF_PURCHASE;
    }

    public BigDecimal getUNIT_AMT_OF_PURCHASE() {
        return this.UNIT_AMT_OF_PURCHASE;
    }

    public void setUNIT_AMT_OF_PURCHASE(BigDecimal UNIT_AMT_OF_PURCHASE) {
        this.UNIT_AMT_OF_PURCHASE = UNIT_AMT_OF_PURCHASE;
    }

    public BigDecimal getFREQUENCY_OF_INTEST_PAY() {
        return this.FREQUENCY_OF_INTEST_PAY;
    }

    public void setFREQUENCY_OF_INTEST_PAY(BigDecimal FREQUENCY_OF_INTEST_PAY) {
        this.FREQUENCY_OF_INTEST_PAY = FREQUENCY_OF_INTEST_PAY;
    }

    public BigDecimal getFIXED_DIVIDEND_RATE() {
        return this.FIXED_DIVIDEND_RATE;
    }

    public void setFIXED_DIVIDEND_RATE(BigDecimal FIXED_DIVIDEND_RATE) {
        this.FIXED_DIVIDEND_RATE = FIXED_DIVIDEND_RATE;
    }

    public String getFIXED_RATE_DURATION() {
        return this.FIXED_RATE_DURATION;
    }

    public void setFIXED_RATE_DURATION(String FIXED_RATE_DURATION) {
        this.FIXED_RATE_DURATION = FIXED_RATE_DURATION;
    }

    public BigDecimal getFLOATING_DIVIDEND_RATE() {
        return this.FLOATING_DIVIDEND_RATE;
    }

    public void setFLOATING_DIVIDEND_RATE(BigDecimal FLOATING_DIVIDEND_RATE) {
        this.FLOATING_DIVIDEND_RATE = FLOATING_DIVIDEND_RATE;
    }

    public String getCURRENCY_EXCHANGE() {
        return this.CURRENCY_EXCHANGE;
    }

    public void setCURRENCY_EXCHANGE(String CURRENCY_EXCHANGE) {
        this.CURRENCY_EXCHANGE = CURRENCY_EXCHANGE;
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

    public Timestamp getINV_SDATE() {
        return this.INV_SDATE;
    }

    public void setINV_SDATE(Timestamp INV_SDATE) {
        this.INV_SDATE = INV_SDATE;
    }

    public Timestamp getINV_EDATE() {
        return this.INV_EDATE;
    }

    public void setINV_EDATE(Timestamp INV_EDATE) {
        this.INV_EDATE = INV_EDATE;
    }

    public Timestamp getF_DATE() {
        return this.F_DATE;
    }

    public void setF_DATE(Timestamp F_DATE) {
        this.F_DATE = F_DATE;
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

    public BigDecimal getRATE_OF_RETURN() {
        return this.RATE_OF_RETURN;
    }

    public void setRATE_OF_RETURN(BigDecimal RATE_OF_RETURN) {
        this.RATE_OF_RETURN = RATE_OF_RETURN;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRD_ID", getPRD_ID())
            .toString();
    }

	public String getSTOCK_BOND_TYPE() {
		return STOCK_BOND_TYPE;
	}

	public void setSTOCK_BOND_TYPE(String sTOCK_BOND_TYPE) {
		STOCK_BOND_TYPE = sTOCK_BOND_TYPE;
	}

}
