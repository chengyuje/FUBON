package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSOT_SI_TRADE_DVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DPK comp_id;

    /** nullable persistent field */
    private String BATCH_SEQ;

    /** nullable persistent field */
    private String TRADE_SUB_TYPE;

    /** nullable persistent field */
    private String RECEIVED_NO;

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
    private BigDecimal PROD_MIN_BUY_AMT;

    /** nullable persistent field */
    private BigDecimal PROD_MIN_GRD_AMT;

    /** nullable persistent field */
    private BigDecimal PURCHASE_AMT;

    /** nullable persistent field */
    private BigDecimal REF_VAL;

    /** nullable persistent field */
    private Timestamp REF_VAL_DATE;

    /** nullable persistent field */
    private String ENTRUST_TYPE;

    /** nullable persistent field */
    private BigDecimal ENTRUST_AMT;

    /** nullable persistent field */
    private String DEBIT_ACCT;

    /** nullable persistent field */
    private String PROD_ACCT;

    /** nullable persistent field */
    private Timestamp TRADE_DATE;

    /** nullable persistent field */
    private String NARRATOR_ID;

    /** nullable persistent field */
    private String NARRATOR_NAME;

    /** nullable persistent field */
    private String BOSS_ID;
    
    /** nullable persistent field */
    private String AUTH_ID;

    /** nullable persistent field */
    private String OVER_CENTRATE_YN;
    
    /** nullable persistent field */
    private String Q4_BRANCH_NBR;
    
public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_D";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSOT_SI_TRADE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DPK comp_id, String BATCH_SEQ, String TRADE_SUB_TYPE, String RECEIVED_NO, String CERTIFICATE_ID, String PROD_ID, String PROD_NAME, String PROD_CURR, String PROD_RISK_LV, BigDecimal PROD_MIN_BUY_AMT, BigDecimal PROD_MIN_GRD_AMT, BigDecimal PURCHASE_AMT, BigDecimal REF_VAL, Timestamp REF_VAL_DATE, String ENTRUST_TYPE, BigDecimal ENTRUST_AMT, String DEBIT_ACCT, String PROD_ACCT, Timestamp TRADE_DATE, String NARRATOR_ID, String NARRATOR_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String BOSS_ID, Long version) {
        this.comp_id = comp_id;
        this.BATCH_SEQ = BATCH_SEQ;
        this.TRADE_SUB_TYPE = TRADE_SUB_TYPE;
        this.RECEIVED_NO = RECEIVED_NO;
        this.CERTIFICATE_ID = CERTIFICATE_ID;
        this.PROD_ID = PROD_ID;
        this.PROD_NAME = PROD_NAME;
        this.PROD_CURR = PROD_CURR;
        this.PROD_RISK_LV = PROD_RISK_LV;
        this.PROD_MIN_BUY_AMT = PROD_MIN_BUY_AMT;
        this.PROD_MIN_GRD_AMT = PROD_MIN_GRD_AMT;
        this.PURCHASE_AMT = PURCHASE_AMT;
        this.REF_VAL = REF_VAL;
        this.REF_VAL_DATE = REF_VAL_DATE;
        this.ENTRUST_TYPE = ENTRUST_TYPE;
        this.ENTRUST_AMT = ENTRUST_AMT;
        this.DEBIT_ACCT = DEBIT_ACCT;
        this.PROD_ACCT = PROD_ACCT;
        this.TRADE_DATE = TRADE_DATE;
        this.NARRATOR_ID = NARRATOR_ID;
        this.NARRATOR_NAME = NARRATOR_NAME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.BOSS_ID = BOSS_ID;
        this.version = version;
    }

    /** default constructor */
    public TBSOT_SI_TRADE_DVO() {
    }

    /** minimal constructor */
    public TBSOT_SI_TRADE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getBATCH_SEQ() {
        return this.BATCH_SEQ;
    }

    public void setBATCH_SEQ(String BATCH_SEQ) {
        this.BATCH_SEQ = BATCH_SEQ;
    }

    public String getTRADE_SUB_TYPE() {
        return this.TRADE_SUB_TYPE;
    }

    public void setTRADE_SUB_TYPE(String TRADE_SUB_TYPE) {
        this.TRADE_SUB_TYPE = TRADE_SUB_TYPE;
    }

    public String getRECEIVED_NO() {
        return this.RECEIVED_NO;
    }

    public void setRECEIVED_NO(String RECEIVED_NO) {
        this.RECEIVED_NO = RECEIVED_NO;
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

    public BigDecimal getPROD_MIN_BUY_AMT() {
        return this.PROD_MIN_BUY_AMT;
    }

    public void setPROD_MIN_BUY_AMT(BigDecimal PROD_MIN_BUY_AMT) {
        this.PROD_MIN_BUY_AMT = PROD_MIN_BUY_AMT;
    }

    public BigDecimal getPROD_MIN_GRD_AMT() {
        return this.PROD_MIN_GRD_AMT;
    }

    public void setPROD_MIN_GRD_AMT(BigDecimal PROD_MIN_GRD_AMT) {
        this.PROD_MIN_GRD_AMT = PROD_MIN_GRD_AMT;
    }

    public BigDecimal getPURCHASE_AMT() {
        return this.PURCHASE_AMT;
    }

    public void setPURCHASE_AMT(BigDecimal PURCHASE_AMT) {
        this.PURCHASE_AMT = PURCHASE_AMT;
    }

    public BigDecimal getREF_VAL() {
        return this.REF_VAL;
    }

    public void setREF_VAL(BigDecimal REF_VAL) {
        this.REF_VAL = REF_VAL;
    }

    public Timestamp getREF_VAL_DATE() {
        return this.REF_VAL_DATE;
    }

    public void setREF_VAL_DATE(Timestamp REF_VAL_DATE) {
        this.REF_VAL_DATE = REF_VAL_DATE;
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

    public String getDEBIT_ACCT() {
        return this.DEBIT_ACCT;
    }

    public void setDEBIT_ACCT(String DEBIT_ACCT) {
        this.DEBIT_ACCT = DEBIT_ACCT;
    }

    public String getPROD_ACCT() {
        return this.PROD_ACCT;
    }

    public void setPROD_ACCT(String PROD_ACCT) {
        this.PROD_ACCT = PROD_ACCT;
    }

    public Timestamp getTRADE_DATE() {
        return this.TRADE_DATE;
    }

    public void setTRADE_DATE(Timestamp TRADE_DATE) {
        this.TRADE_DATE = TRADE_DATE;
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

    public String getBOSS_ID() {
        return this.BOSS_ID;
    }

    public void setBOSS_ID(String BOSS_ID) {
        this.BOSS_ID = BOSS_ID;
    }

    public String getAUTH_ID() {
		return AUTH_ID;
	}

	public void setAUTH_ID(String aUTH_ID) {
		AUTH_ID = aUTH_ID;
	}

	public String getOVER_CENTRATE_YN() {
		return OVER_CENTRATE_YN;
	}

	public void setOVER_CENTRATE_YN(String oVER_CENTRATE_YN) {
		OVER_CENTRATE_YN = oVER_CENTRATE_YN;
	}

	public String getQ4_BRANCH_NBR() {
		return Q4_BRANCH_NBR;
	}

	public void setQ4_BRANCH_NBR(String q4_BRANCH_NBR) {
		Q4_BRANCH_NBR = q4_BRANCH_NBR;
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
        if ( !(other instanceof TBSOT_SI_TRADE_DVO) ) return false;
        TBSOT_SI_TRADE_DVO castOther = (TBSOT_SI_TRADE_DVO) other;
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
