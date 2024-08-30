package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSOT_ETF_TRADE_DVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DPK comp_id;

    /** nullable persistent field */
    private String BATCH_SEQ;

    /** nullable persistent field */
    private BigDecimal BATCH_NO;

    /** nullable persistent field */
    private String TRADE_SUB_TYPE;

    /** nullable persistent field */
    private String CERTIFICATE_ID;

    /** nullable persistent field */
    private String PROD_ID;

    /** nullable persistent field */
    private String PROD_NAME;

    /** nullable persistent field */
    private String PROD_CURR;

    /** nullable persistent field */
    private String PROD_RISK_LV;

    /** nullable persistent field */
    private String TRUST_CURR_TYPE;

    /** nullable persistent field */
    private BigDecimal REF_PRICE;

    /** nullable persistent field */
    private String SELL_TYPE;

    /** nullable persistent field */
    private BigDecimal PROD_MIN_BUY_AMT;

    /** nullable persistent field */
    private BigDecimal PROD_MIN_BUY_UNIT;

    /** nullable persistent field */
    private BigDecimal PROD_MIN_GRD_UNIT;

    /** nullable persistent field */
    private BigDecimal UNIT_NUM;

    /** nullable persistent field */
    private String ENTRUST_TYPE;

    /** nullable persistent field */
    private BigDecimal ENTRUST_AMT;

    /** nullable persistent field */
    private BigDecimal CLOSING_PRICE;

    /** nullable persistent field */
    private Timestamp CLOSING_PRICE_DATE;

    /** nullable persistent field */
    private BigDecimal ENTRUST_DISCOUNT;

    /** nullable persistent field */
    private String DUE_DATE_SHOW;

    /** nullable persistent field */
    private Timestamp DUE_DATE;

    /** nullable persistent field */
    private BigDecimal DEFAULT_FEE_RATE;

    /** nullable persistent field */
    private String FEE_TYPE;

    /** nullable persistent field */
    private String BARGAIN_STATUS;

    /** nullable persistent field */
    private String BARGAIN_APPLY_SEQ;

    /** nullable persistent field */
    private BigDecimal FEE_RATE;

    /** nullable persistent field */
    private BigDecimal FEE;

    /** nullable persistent field */
    private BigDecimal FEE_DISCOUNT;

    /** nullable persistent field */
    private String GROUP_OFA;

    /** nullable persistent field */
    private BigDecimal TAX_FEE;

    /** nullable persistent field */
    private BigDecimal MTN_FEE;

    /** nullable persistent field */
    private BigDecimal OTHER_FEE;

    /** nullable persistent field */
    private BigDecimal TOT_AMT;

    /** nullable persistent field */
    private String DEBIT_ACCT;

    /** nullable persistent field */
    private String TRUST_ACCT;

    /** nullable persistent field */
    private String CREDIT_ACCT;

    /** nullable persistent field */
    private Timestamp TRADE_DATE;

    /** nullable persistent field */
    private BigDecimal STOP_LOSS_PERC;

    /** nullable persistent field */
    private BigDecimal TAKE_PROFIT_PERC;

    /** nullable persistent field */
    private String PL_NOTIFY_WAYS;

    /** nullable persistent field */
    private String IS_NEW_PROD;

    /** nullable persistent field */
    private String TRADE_MARKET;

    /** nullable persistent field */
    private String NARRATOR_ID;

    /** nullable persistent field */
    private String NARRATOR_NAME;

    /** nullable persistent field */
    private BigDecimal TRUST_NUM;

    /** nullable persistent field */
    private String CONTRACT_ID;
	
    /** nullable persistent field */
	private String TRUST_PEOP_NUM;

public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_D";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSOT_ETF_TRADE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DPK comp_id, String BATCH_SEQ, BigDecimal BATCH_NO, String TRADE_SUB_TYPE, String CERTIFICATE_ID, String PROD_ID, String PROD_NAME, String PROD_CURR, String PROD_RISK_LV, String TRUST_CURR_TYPE, BigDecimal REF_PRICE, String SELL_TYPE, BigDecimal PROD_MIN_BUY_AMT, BigDecimal PROD_MIN_BUY_UNIT, BigDecimal PROD_MIN_GRD_UNIT, BigDecimal UNIT_NUM, String ENTRUST_TYPE, BigDecimal ENTRUST_AMT, BigDecimal CLOSING_PRICE, Timestamp CLOSING_PRICE_DATE, BigDecimal ENTRUST_DISCOUNT, String DUE_DATE_SHOW, Timestamp DUE_DATE, BigDecimal DEFAULT_FEE_RATE, String FEE_TYPE, String BARGAIN_STATUS, String BARGAIN_APPLY_SEQ, BigDecimal FEE_RATE, BigDecimal FEE, BigDecimal FEE_DISCOUNT, String GROUP_OFA, BigDecimal TAX_FEE, BigDecimal MTN_FEE, BigDecimal OTHER_FEE, BigDecimal TOT_AMT, String DEBIT_ACCT, String TRUST_ACCT, String CREDIT_ACCT, Timestamp TRADE_DATE, BigDecimal STOP_LOSS_PERC, BigDecimal TAKE_PROFIT_PERC, String PL_NOTIFY_WAYS, String IS_NEW_PROD, String TRADE_MARKET, String NARRATOR_ID, String NARRATOR_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, BigDecimal TRUST_NUM, Long version) {
        this.comp_id = comp_id;
        this.BATCH_SEQ = BATCH_SEQ;
        this.BATCH_NO = BATCH_NO;
        this.TRADE_SUB_TYPE = TRADE_SUB_TYPE;
        this.CERTIFICATE_ID = CERTIFICATE_ID;
        this.PROD_ID = PROD_ID;
        this.PROD_NAME = PROD_NAME;
        this.PROD_CURR = PROD_CURR;
        this.PROD_RISK_LV = PROD_RISK_LV;
        this.TRUST_CURR_TYPE = TRUST_CURR_TYPE;
        this.REF_PRICE = REF_PRICE;
        this.SELL_TYPE = SELL_TYPE;
        this.PROD_MIN_BUY_AMT = PROD_MIN_BUY_AMT;
        this.PROD_MIN_BUY_UNIT = PROD_MIN_BUY_UNIT;
        this.PROD_MIN_GRD_UNIT = PROD_MIN_GRD_UNIT;
        this.UNIT_NUM = UNIT_NUM;
        this.ENTRUST_TYPE = ENTRUST_TYPE;
        this.ENTRUST_AMT = ENTRUST_AMT;
        this.CLOSING_PRICE = CLOSING_PRICE;
        this.CLOSING_PRICE_DATE = CLOSING_PRICE_DATE;
        this.ENTRUST_DISCOUNT = ENTRUST_DISCOUNT;
        this.DUE_DATE_SHOW = DUE_DATE_SHOW;
        this.DUE_DATE = DUE_DATE;
        this.DEFAULT_FEE_RATE = DEFAULT_FEE_RATE;
        this.FEE_TYPE = FEE_TYPE;
        this.BARGAIN_STATUS = BARGAIN_STATUS;
        this.BARGAIN_APPLY_SEQ = BARGAIN_APPLY_SEQ;
        this.FEE_RATE = FEE_RATE;
        this.FEE = FEE;
        this.FEE_DISCOUNT = FEE_DISCOUNT;
        this.GROUP_OFA = GROUP_OFA;
        this.TAX_FEE = TAX_FEE;
        this.MTN_FEE = MTN_FEE;
        this.OTHER_FEE = OTHER_FEE;
        this.TOT_AMT = TOT_AMT;
        this.DEBIT_ACCT = DEBIT_ACCT;
        this.TRUST_ACCT = TRUST_ACCT;
        this.CREDIT_ACCT = CREDIT_ACCT;
        this.TRADE_DATE = TRADE_DATE;
        this.STOP_LOSS_PERC = STOP_LOSS_PERC;
        this.TAKE_PROFIT_PERC = TAKE_PROFIT_PERC;
        this.PL_NOTIFY_WAYS = PL_NOTIFY_WAYS;
        this.IS_NEW_PROD = IS_NEW_PROD;
        this.TRADE_MARKET = TRADE_MARKET;
        this.NARRATOR_ID = NARRATOR_ID;
        this.NARRATOR_NAME = NARRATOR_NAME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.TRUST_NUM = TRUST_NUM;
        this.version = version;
    }

    /** default constructor */
    public TBSOT_ETF_TRADE_DVO() {
    }

    /** minimal constructor */
    public TBSOT_ETF_TRADE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getBATCH_SEQ() {
        return this.BATCH_SEQ;
    }

    public void setBATCH_SEQ(String BATCH_SEQ) {
        this.BATCH_SEQ = BATCH_SEQ;
    }

    public BigDecimal getBATCH_NO() {
        return this.BATCH_NO;
    }

    public void setBATCH_NO(BigDecimal BATCH_NO) {
        this.BATCH_NO = BATCH_NO;
    }

    public String getTRADE_SUB_TYPE() {
        return this.TRADE_SUB_TYPE;
    }

    public void setTRADE_SUB_TYPE(String TRADE_SUB_TYPE) {
        this.TRADE_SUB_TYPE = TRADE_SUB_TYPE;
    }

    public String getCERTIFICATE_ID() {
        return this.CERTIFICATE_ID;
    }

    public void setCERTIFICATE_ID(String CERTIFICATE_ID) {
        this.CERTIFICATE_ID = CERTIFICATE_ID;
    }

    public String getPROD_ID() {
        return this.PROD_ID;
    }

    public void setPROD_ID(String PROD_ID) {
        this.PROD_ID = PROD_ID;
    }

    public String getPROD_NAME() {
        return this.PROD_NAME;
    }

    public void setPROD_NAME(String PROD_NAME) {
        this.PROD_NAME = PROD_NAME;
    }

    public String getPROD_CURR() {
        return this.PROD_CURR;
    }

    public void setPROD_CURR(String PROD_CURR) {
        this.PROD_CURR = PROD_CURR;
    }

    public String getPROD_RISK_LV() {
        return this.PROD_RISK_LV;
    }

    public void setPROD_RISK_LV(String PROD_RISK_LV) {
        this.PROD_RISK_LV = PROD_RISK_LV;
    }

    public String getTRUST_CURR_TYPE() {
        return this.TRUST_CURR_TYPE;
    }

    public void setTRUST_CURR_TYPE(String TRUST_CURR_TYPE) {
        this.TRUST_CURR_TYPE = TRUST_CURR_TYPE;
    }

    public BigDecimal getREF_PRICE() {
        return this.REF_PRICE;
    }

    public void setREF_PRICE(BigDecimal REF_PRICE) {
        this.REF_PRICE = REF_PRICE;
    }

    public String getSELL_TYPE() {
        return this.SELL_TYPE;
    }

    public void setSELL_TYPE(String SELL_TYPE) {
        this.SELL_TYPE = SELL_TYPE;
    }

    public BigDecimal getPROD_MIN_BUY_AMT() {
        return this.PROD_MIN_BUY_AMT;
    }

    public void setPROD_MIN_BUY_AMT(BigDecimal PROD_MIN_BUY_AMT) {
        this.PROD_MIN_BUY_AMT = PROD_MIN_BUY_AMT;
    }

    public BigDecimal getPROD_MIN_BUY_UNIT() {
        return this.PROD_MIN_BUY_UNIT;
    }

    public void setPROD_MIN_BUY_UNIT(BigDecimal PROD_MIN_BUY_UNIT) {
        this.PROD_MIN_BUY_UNIT = PROD_MIN_BUY_UNIT;
    }

    public BigDecimal getPROD_MIN_GRD_UNIT() {
        return this.PROD_MIN_GRD_UNIT;
    }

    public void setPROD_MIN_GRD_UNIT(BigDecimal PROD_MIN_GRD_UNIT) {
        this.PROD_MIN_GRD_UNIT = PROD_MIN_GRD_UNIT;
    }

    public BigDecimal getUNIT_NUM() {
        return this.UNIT_NUM;
    }

    public void setUNIT_NUM(BigDecimal UNIT_NUM) {
        this.UNIT_NUM = UNIT_NUM;
    }

    public String getENTRUST_TYPE() {
        return this.ENTRUST_TYPE;
    }

    public void setENTRUST_TYPE(String ENTRUST_TYPE) {
        this.ENTRUST_TYPE = ENTRUST_TYPE;
    }

    public BigDecimal getENTRUST_AMT() {
        return this.ENTRUST_AMT;
    }

    public void setENTRUST_AMT(BigDecimal ENTRUST_AMT) {
        this.ENTRUST_AMT = ENTRUST_AMT;
    }

    public BigDecimal getCLOSING_PRICE() {
        return this.CLOSING_PRICE;
    }

    public void setCLOSING_PRICE(BigDecimal CLOSING_PRICE) {
        this.CLOSING_PRICE = CLOSING_PRICE;
    }

    public Timestamp getCLOSING_PRICE_DATE() {
        return this.CLOSING_PRICE_DATE;
    }

    public void setCLOSING_PRICE_DATE(Timestamp CLOSING_PRICE_DATE) {
        this.CLOSING_PRICE_DATE = CLOSING_PRICE_DATE;
    }

    public BigDecimal getENTRUST_DISCOUNT() {
        return this.ENTRUST_DISCOUNT;
    }

    public void setENTRUST_DISCOUNT(BigDecimal ENTRUST_DISCOUNT) {
        this.ENTRUST_DISCOUNT = ENTRUST_DISCOUNT;
    }

    public String getDUE_DATE_SHOW() {
        return this.DUE_DATE_SHOW;
    }

    public void setDUE_DATE_SHOW(String DUE_DATE_SHOW) {
        this.DUE_DATE_SHOW = DUE_DATE_SHOW;
    }

    public Timestamp getDUE_DATE() {
        return this.DUE_DATE;
    }

    public void setDUE_DATE(Timestamp DUE_DATE) {
        this.DUE_DATE = DUE_DATE;
    }

    public BigDecimal getDEFAULT_FEE_RATE() {
        return this.DEFAULT_FEE_RATE;
    }

    public void setDEFAULT_FEE_RATE(BigDecimal DEFAULT_FEE_RATE) {
        this.DEFAULT_FEE_RATE = DEFAULT_FEE_RATE;
    }

    public String getFEE_TYPE() {
        return this.FEE_TYPE;
    }

    public void setFEE_TYPE(String FEE_TYPE) {
        this.FEE_TYPE = FEE_TYPE;
    }

    public String getBARGAIN_STATUS() {
        return this.BARGAIN_STATUS;
    }

    public void setBARGAIN_STATUS(String BARGAIN_STATUS) {
        this.BARGAIN_STATUS = BARGAIN_STATUS;
    }

    public String getBARGAIN_APPLY_SEQ() {
        return this.BARGAIN_APPLY_SEQ;
    }

    public void setBARGAIN_APPLY_SEQ(String BARGAIN_APPLY_SEQ) {
        this.BARGAIN_APPLY_SEQ = BARGAIN_APPLY_SEQ;
    }

    public BigDecimal getFEE_RATE() {
        return this.FEE_RATE;
    }

    public void setFEE_RATE(BigDecimal FEE_RATE) {
        this.FEE_RATE = FEE_RATE;
    }

    public BigDecimal getFEE() {
        return this.FEE;
    }

    public void setFEE(BigDecimal FEE) {
        this.FEE = FEE;
    }

    public BigDecimal getFEE_DISCOUNT() {
        return this.FEE_DISCOUNT;
    }

    public void setFEE_DISCOUNT(BigDecimal FEE_DISCOUNT) {
        this.FEE_DISCOUNT = FEE_DISCOUNT;
    }

    public String getGROUP_OFA() {
        return this.GROUP_OFA;
    }

    public void setGROUP_OFA(String GROUP_OFA) {
        this.GROUP_OFA = GROUP_OFA;
    }

    public BigDecimal getTAX_FEE() {
        return this.TAX_FEE;
    }

    public void setTAX_FEE(BigDecimal TAX_FEE) {
        this.TAX_FEE = TAX_FEE;
    }

    public BigDecimal getMTN_FEE() {
        return this.MTN_FEE;
    }

    public void setMTN_FEE(BigDecimal MTN_FEE) {
        this.MTN_FEE = MTN_FEE;
    }

    public BigDecimal getOTHER_FEE() {
        return this.OTHER_FEE;
    }

    public void setOTHER_FEE(BigDecimal OTHER_FEE) {
        this.OTHER_FEE = OTHER_FEE;
    }

    public BigDecimal getTOT_AMT() {
        return this.TOT_AMT;
    }

    public void setTOT_AMT(BigDecimal TOT_AMT) {
        this.TOT_AMT = TOT_AMT;
    }

    public String getDEBIT_ACCT() {
        return this.DEBIT_ACCT;
    }

    public void setDEBIT_ACCT(String DEBIT_ACCT) {
        this.DEBIT_ACCT = DEBIT_ACCT;
    }

    public String getTRUST_ACCT() {
        return this.TRUST_ACCT;
    }

    public void setTRUST_ACCT(String TRUST_ACCT) {
        this.TRUST_ACCT = TRUST_ACCT;
    }

    public String getCREDIT_ACCT() {
        return this.CREDIT_ACCT;
    }

    public void setCREDIT_ACCT(String CREDIT_ACCT) {
        this.CREDIT_ACCT = CREDIT_ACCT;
    }

    public Timestamp getTRADE_DATE() {
        return this.TRADE_DATE;
    }

    public void setTRADE_DATE(Timestamp TRADE_DATE) {
        this.TRADE_DATE = TRADE_DATE;
    }

    public BigDecimal getSTOP_LOSS_PERC() {
        return this.STOP_LOSS_PERC;
    }

    public void setSTOP_LOSS_PERC(BigDecimal STOP_LOSS_PERC) {
        this.STOP_LOSS_PERC = STOP_LOSS_PERC;
    }

    public BigDecimal getTAKE_PROFIT_PERC() {
        return this.TAKE_PROFIT_PERC;
    }

    public void setTAKE_PROFIT_PERC(BigDecimal TAKE_PROFIT_PERC) {
        this.TAKE_PROFIT_PERC = TAKE_PROFIT_PERC;
    }

    public String getPL_NOTIFY_WAYS() {
        return this.PL_NOTIFY_WAYS;
    }

    public void setPL_NOTIFY_WAYS(String PL_NOTIFY_WAYS) {
        this.PL_NOTIFY_WAYS = PL_NOTIFY_WAYS;
    }

    public String getIS_NEW_PROD() {
        return this.IS_NEW_PROD;
    }

    public void setIS_NEW_PROD(String IS_NEW_PROD) {
        this.IS_NEW_PROD = IS_NEW_PROD;
    }

    public String getTRADE_MARKET() {
        return this.TRADE_MARKET;
    }

    public void setTRADE_MARKET(String TRADE_MARKET) {
        this.TRADE_MARKET = TRADE_MARKET;
    }

    public String getNARRATOR_ID() {
        return this.NARRATOR_ID;
    }

    public void setNARRATOR_ID(String NARRATOR_ID) {
        this.NARRATOR_ID = NARRATOR_ID;
    }

    public String getNARRATOR_NAME() {
        return this.NARRATOR_NAME;
    }

    public void setNARRATOR_NAME(String NARRATOR_NAME) {
        this.NARRATOR_NAME = NARRATOR_NAME;
    }

    public BigDecimal getTRUST_NUM() {
        return this.TRUST_NUM;
    }

    public void setTRUST_NUM(BigDecimal TRUST_NUM) {
        this.TRUST_NUM = TRUST_NUM;
    }

    public String getCONTRACT_ID() {
		return CONTRACT_ID;
	}

	public void setCONTRACT_ID(String cONTRACT_ID) {
		CONTRACT_ID = cONTRACT_ID;
	}

	public String getTRUST_PEOP_NUM() {
		return TRUST_PEOP_NUM;
	}

	public void setTRUST_PEOP_NUM(String tRUST_PEOP_NUM) {
		TRUST_PEOP_NUM = tRUST_PEOP_NUM;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSOT_ETF_TRADE_DVO) ) return false;
        TBSOT_ETF_TRADE_DVO castOther = (TBSOT_ETF_TRADE_DVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
