package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_BN_TRADE_DVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DPK comp_id;

	/** nullable persistent field */
	private String BATCH_SEQ;

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
	private BigDecimal PROD_MIN_BUY_AMT;

	/** nullable persistent field */
	private BigDecimal PROD_MIN_GRD_AMT;

	/** nullable persistent field */
	private String TRUST_CURR_TYPE;

	/** nullable persistent field */
	private String TRUST_CURR;

	/** nullable persistent field */
	private BigDecimal TRUST_UNIT;

	/** nullable persistent field */
	private String MARKET_TYPE;

	/** nullable persistent field */
	private BigDecimal REF_VAL;

	/** nullable persistent field */
	private Timestamp REF_VAL_DATE;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT;

	/** nullable persistent field */
	private String ENTRUST_TYPE;

	/** nullable persistent field */
	private BigDecimal ENTRUST_AMT;

	/** nullable persistent field */
	private BigDecimal TRUST_AMT;

	/** nullable persistent field */
	private BigDecimal DEFAULT_FEE_RATE;

	/** nullable persistent field */
	private BigDecimal ADV_FEE_RATE;

	/** nullable persistent field */
	private String BARGAIN_APPLY_SEQ;

	/** nullable persistent field */
	private BigDecimal FEE_RATE;

	/** nullable persistent field */
	private BigDecimal FEE;

	/** nullable persistent field */
	private BigDecimal FEE_DISCOUNT;

	/** nullable persistent field */
	private BigDecimal PAYABLE_FEE;

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
	private String NARRATOR_ID;

	/** nullable persistent field */
	private String NARRATOR_NAME;

	/** nullable persistent field */
	private BigDecimal BEST_FEE_RATE;

	/** nullable persistent field */
	private String COLUMN1;

	/** nullable persistent field */
	private BigDecimal BOND_VALUE;

	/** nullable persistent field */
	private String GTC_YN;

	/** nullable persistent field */
	private Timestamp GTC_START_DATE;
	
	/** nullable persistent field */
	private Timestamp GTC_END_DATE;
	
	private String CONTRACT_ID;
	
	private String TRUST_PEOP_NUM;
	
	private String OVER_CENTRATE_YN;

	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_D";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBSOT_BN_TRADE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DPK comp_id, String BATCH_SEQ, String TRADE_SUB_TYPE, String CERTIFICATE_ID, String PROD_ID, String PROD_NAME, String PROD_CURR, String PROD_RISK_LV, BigDecimal PROD_MIN_BUY_AMT, BigDecimal PROD_MIN_GRD_AMT, String TRUST_CURR_TYPE, String TRUST_CURR, BigDecimal TRUST_UNIT, String MARKET_TYPE, BigDecimal REF_VAL, Timestamp REF_VAL_DATE, BigDecimal PURCHASE_AMT, String ENTRUST_TYPE, BigDecimal ENTRUST_AMT, BigDecimal TRUST_AMT, BigDecimal DEFAULT_FEE_RATE, BigDecimal ADV_FEE_RATE, String BARGAIN_APPLY_SEQ, BigDecimal FEE_RATE, BigDecimal FEE, BigDecimal FEE_DISCOUNT, BigDecimal PAYABLE_FEE, BigDecimal TOT_AMT, String DEBIT_ACCT, String TRUST_ACCT, String CREDIT_ACCT, Timestamp TRADE_DATE, String NARRATOR_ID, String NARRATOR_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, BigDecimal BEST_FEE_RATE, String COLUMN1, BigDecimal BOND_VALUE, Long version, String GTC_YN, Timestamp GTC_START_DATE, Timestamp GTC_END_DATE, String CONTRACT_ID, String TRUST_PEOP_NUM) {
		this.comp_id = comp_id;
		this.BATCH_SEQ = BATCH_SEQ;
		this.TRADE_SUB_TYPE = TRADE_SUB_TYPE;
		this.CERTIFICATE_ID = CERTIFICATE_ID;
		this.PROD_ID = PROD_ID;
		this.PROD_NAME = PROD_NAME;
		this.PROD_CURR = PROD_CURR;
		this.PROD_RISK_LV = PROD_RISK_LV;
		this.PROD_MIN_BUY_AMT = PROD_MIN_BUY_AMT;
		this.PROD_MIN_GRD_AMT = PROD_MIN_GRD_AMT;
		this.TRUST_CURR_TYPE = TRUST_CURR_TYPE;
		this.TRUST_CURR = TRUST_CURR;
		this.TRUST_UNIT = TRUST_UNIT;
		this.MARKET_TYPE = MARKET_TYPE;
		this.REF_VAL = REF_VAL;
		this.REF_VAL_DATE = REF_VAL_DATE;
		this.PURCHASE_AMT = PURCHASE_AMT;
		this.ENTRUST_TYPE = ENTRUST_TYPE;
		this.ENTRUST_AMT = ENTRUST_AMT;
		this.TRUST_AMT = TRUST_AMT;
		this.DEFAULT_FEE_RATE = DEFAULT_FEE_RATE;
		this.ADV_FEE_RATE = ADV_FEE_RATE;
		this.BARGAIN_APPLY_SEQ = BARGAIN_APPLY_SEQ;
		this.FEE_RATE = FEE_RATE;
		this.FEE = FEE;
		this.FEE_DISCOUNT = FEE_DISCOUNT;
		this.PAYABLE_FEE = PAYABLE_FEE;
		this.TOT_AMT = TOT_AMT;
		this.DEBIT_ACCT = DEBIT_ACCT;
		this.TRUST_ACCT = TRUST_ACCT;
		this.CREDIT_ACCT = CREDIT_ACCT;
		this.TRADE_DATE = TRADE_DATE;
		this.NARRATOR_ID = NARRATOR_ID;
		this.NARRATOR_NAME = NARRATOR_NAME;
		this.createtime = createtime;
		this.creator = creator;
		this.modifier = modifier;
		this.lastupdate = lastupdate;
		this.BEST_FEE_RATE = BEST_FEE_RATE;
		this.COLUMN1 = COLUMN1;
		this.BOND_VALUE = BOND_VALUE;
		this.version = version;
		this.GTC_YN = GTC_YN;
		this.GTC_START_DATE = GTC_START_DATE;
		this.GTC_END_DATE = GTC_END_DATE;
		this.CONTRACT_ID = CONTRACT_ID;
		this.TRUST_PEOP_NUM = TRUST_PEOP_NUM;
	}

	/** default constructor */
	public TBSOT_BN_TRADE_DVO() {
	}

	public String getTRUST_PEOP_NUM() {
		return TRUST_PEOP_NUM;
	}

	public void setTRUST_PEOP_NUM(String tRUST_PEOP_NUM) {
		TRUST_PEOP_NUM = tRUST_PEOP_NUM;
	}

	public String getCONTRACT_ID() {
		return CONTRACT_ID;
	}

	public void setCONTRACT_ID(String cONTRACT_ID) {
		CONTRACT_ID = cONTRACT_ID;
	}

	/** minimal constructor */
	public TBSOT_BN_TRADE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DPK comp_id) {
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

	public String getTRUST_CURR_TYPE() {
		return this.TRUST_CURR_TYPE;
	}

	public void setTRUST_CURR_TYPE(String TRUST_CURR_TYPE) {
		this.TRUST_CURR_TYPE = TRUST_CURR_TYPE;
	}

	public String getTRUST_CURR() {
		return this.TRUST_CURR;
	}

	public void setTRUST_CURR(String TRUST_CURR) {
		this.TRUST_CURR = TRUST_CURR;
	}

	public BigDecimal getTRUST_UNIT() {
		return this.TRUST_UNIT;
	}

	public void setTRUST_UNIT(BigDecimal TRUST_UNIT) {
		this.TRUST_UNIT = TRUST_UNIT;
	}

	public String getMARKET_TYPE() {
		return this.MARKET_TYPE;
	}

	public void setMARKET_TYPE(String MARKET_TYPE) {
		this.MARKET_TYPE = MARKET_TYPE;
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

	public BigDecimal getPURCHASE_AMT() {
		return this.PURCHASE_AMT;
	}

	public void setPURCHASE_AMT(BigDecimal PURCHASE_AMT) {
		this.PURCHASE_AMT = PURCHASE_AMT;
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

	public BigDecimal getTRUST_AMT() {
		return this.TRUST_AMT;
	}

	public void setTRUST_AMT(BigDecimal TRUST_AMT) {
		this.TRUST_AMT = TRUST_AMT;
	}

	public BigDecimal getDEFAULT_FEE_RATE() {
		return this.DEFAULT_FEE_RATE;
	}

	public void setDEFAULT_FEE_RATE(BigDecimal DEFAULT_FEE_RATE) {
		this.DEFAULT_FEE_RATE = DEFAULT_FEE_RATE;
	}

	public BigDecimal getADV_FEE_RATE() {
		return this.ADV_FEE_RATE;
	}

	public void setADV_FEE_RATE(BigDecimal ADV_FEE_RATE) {
		this.ADV_FEE_RATE = ADV_FEE_RATE;
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

	public BigDecimal getPAYABLE_FEE() {
		return this.PAYABLE_FEE;
	}

	public void setPAYABLE_FEE(BigDecimal PAYABLE_FEE) {
		this.PAYABLE_FEE = PAYABLE_FEE;
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

	public BigDecimal getBEST_FEE_RATE() {
		return this.BEST_FEE_RATE;
	}

	public void setBEST_FEE_RATE(BigDecimal BEST_FEE_RATE) {
		this.BEST_FEE_RATE = BEST_FEE_RATE;
	}

	public String getCOLUMN1() {
		return this.COLUMN1;
	}

	public void setCOLUMN1(String COLUMN1) {
		this.COLUMN1 = COLUMN1;
	}

	public BigDecimal getBOND_VALUE() {
		return this.BOND_VALUE;
	}

	public void setBOND_VALUE(BigDecimal BOND_VALUE) {
		this.BOND_VALUE = BOND_VALUE;
	}

	public String getGTC_YN() {
		return GTC_YN;
	}

	public void setGTC_YN(String gTC_YN) {
		GTC_YN = gTC_YN;
	}

	public Timestamp getGTC_START_DATE() {
		return GTC_START_DATE;
	}

	public void setGTC_START_DATE(Timestamp gTC_START_DATE) {
		GTC_START_DATE = gTC_START_DATE;
	}

	public Timestamp getGTC_END_DATE() {
		return GTC_END_DATE;
	}

	public void setGTC_END_DATE(Timestamp gTC_END_DATE) {
		GTC_END_DATE = gTC_END_DATE;
	}

	public String getOVER_CENTRATE_YN() {
		return OVER_CENTRATE_YN;
	}

	public void setOVER_CENTRATE_YN(String oVER_CENTRATE_YN) {
		OVER_CENTRATE_YN = oVER_CENTRATE_YN;
	}

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getcomp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof TBSOT_BN_TRADE_DVO))
			return false;
		TBSOT_BN_TRADE_DVO castOther = (TBSOT_BN_TRADE_DVO) other;
		return new EqualsBuilder().append(this.getcomp_id(), castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}

}
