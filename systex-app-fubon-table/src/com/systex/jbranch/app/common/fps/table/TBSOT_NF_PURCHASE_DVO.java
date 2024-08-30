package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_NF_PURCHASE_DVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DPK comp_id;

	/** nullable persistent field */
	private String BATCH_SEQ;

	/** nullable persistent field */
	private BigDecimal BATCH_NO;

	/** nullable persistent field */
	private String TRADE_SUB_TYPE;

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
	private String TRUST_CURR_TYPE;

	/** nullable persistent field */
	private String TRUST_CURR;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT_L;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT_M;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT_H;

	/** nullable persistent field */
	private String IS_AUTO_CX;

	/** nullable persistent field */
	private String CHARGE_DATE_1;

	/** nullable persistent field */
	private String CHARGE_DATE_2;

	/** nullable persistent field */
	private String CHARGE_DATE_3;

	/** nullable persistent field */
	private String CHARGE_DATE_4;

	/** nullable persistent field */
	private String CHARGE_DATE_5;

	/** nullable persistent field */
	private String CHARGE_DATE_6;

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
	private BigDecimal FEE_RATE_L;

	/** nullable persistent field */
	private BigDecimal FEE_RATE_M;

	/** nullable persistent field */
	private BigDecimal FEE_RATE_H;

	/** nullable persistent field */
	private BigDecimal FEE;

	/** nullable persistent field */
	private BigDecimal FEE_L;

	/** nullable persistent field */
	private BigDecimal FEE_M;

	/** nullable persistent field */
	private BigDecimal FEE_H;

	/** nullable persistent field */
	private BigDecimal FEE_DISCOUNT;

	/** nullable persistent field */
	private BigDecimal FEE_DISCOUNT_L;

	/** nullable persistent field */
	private BigDecimal FEE_DISCOUNT_M;

	/** nullable persistent field */
	private BigDecimal FEE_DISCOUNT_H;

	/** nullable persistent field */
	private String DEBIT_ACCT;

	/** nullable persistent field */
	private String TRUST_ACCT;

	/** nullable persistent field */
	private String CREDIT_ACCT;

	/** nullable persistent field */
	private String TRADE_DATE_TYPE;

	/** nullable persistent field */
	private Timestamp TRADE_DATE;

	/** nullable persistent field */
	private BigDecimal STOP_LOSS_PERC;

	/** nullable persistent field */
	private BigDecimal TAKE_PROFIT_PERC;

	/** nullable persistent field */
	private String PL_NOTIFY_WAYS;

	/** nullable persistent field */
	private String NARRATOR_ID;

	/** nullable persistent field */
	private String NARRATOR_NAME;

	/** nullable persistent field */
	private String GROUP_OFA;

	/** nullable persistent field */
	private BigDecimal DEFAULT_FEE_RATE_L;

	/** nullable persistent field */
	private BigDecimal DEFAULT_FEE_RATE_M;

	/** nullable persistent field */
	private BigDecimal DEFAULT_FEE_RATE_H;

	/** nullable persistent field */
	private String NOT_VERTIFY;

	/** nullable persistent field */
	private String PROSPECTUS_TYPE;

	/** nullable persistent field */
	private String PLAN_ID;
	
	private String CONTRACT_ID;
	
	private String TRUST_PEOP_NUM;
	
	private String OVER_CENTRATE_YN;

	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_D";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBSOT_NF_PURCHASE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DPK comp_id, String BATCH_SEQ, BigDecimal BATCH_NO, String TRADE_SUB_TYPE, String PROD_ID, String PROD_NAME, String PROD_CURR, String PROD_RISK_LV, BigDecimal PROD_MIN_BUY_AMT, String TRUST_CURR_TYPE, String TRUST_CURR, BigDecimal PURCHASE_AMT, BigDecimal PURCHASE_AMT_L, BigDecimal PURCHASE_AMT_M, BigDecimal PURCHASE_AMT_H, String IS_AUTO_CX, String CHARGE_DATE_1, String CHARGE_DATE_2, String CHARGE_DATE_3, String CHARGE_DATE_4, String CHARGE_DATE_5, String CHARGE_DATE_6, BigDecimal DEFAULT_FEE_RATE, String FEE_TYPE, String BARGAIN_STATUS, String BARGAIN_APPLY_SEQ, BigDecimal FEE_RATE, BigDecimal FEE_RATE_L, BigDecimal FEE_RATE_M, BigDecimal FEE_RATE_H, BigDecimal FEE, BigDecimal FEE_L, BigDecimal FEE_M, BigDecimal FEE_H, BigDecimal FEE_DISCOUNT, BigDecimal FEE_DISCOUNT_L, BigDecimal FEE_DISCOUNT_M, BigDecimal FEE_DISCOUNT_H, String DEBIT_ACCT, String TRUST_ACCT, String CREDIT_ACCT, String TRADE_DATE_TYPE, Timestamp TRADE_DATE, BigDecimal STOP_LOSS_PERC, BigDecimal TAKE_PROFIT_PERC, String PL_NOTIFY_WAYS, String NARRATOR_ID, String NARRATOR_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String GROUP_OFA, BigDecimal DEFAULT_FEE_RATE_L, BigDecimal DEFAULT_FEE_RATE_M, BigDecimal DEFAULT_FEE_RATE_H, String NOT_VERTIFY, String PROSPECTUS_TYPE, String PLAN_ID, Long version, String CONTRACT_ID, String TRUST_PEOP_NUM) {
		this.comp_id = comp_id;
		this.BATCH_SEQ = BATCH_SEQ;
		this.BATCH_NO = BATCH_NO;
		this.TRADE_SUB_TYPE = TRADE_SUB_TYPE;
		this.PROD_ID = PROD_ID;
		this.PROD_NAME = PROD_NAME;
		this.PROD_CURR = PROD_CURR;
		this.PROD_RISK_LV = PROD_RISK_LV;
		this.PROD_MIN_BUY_AMT = PROD_MIN_BUY_AMT;
		this.TRUST_CURR_TYPE = TRUST_CURR_TYPE;
		this.TRUST_CURR = TRUST_CURR;
		this.PURCHASE_AMT = PURCHASE_AMT;
		this.PURCHASE_AMT_L = PURCHASE_AMT_L;
		this.PURCHASE_AMT_M = PURCHASE_AMT_M;
		this.PURCHASE_AMT_H = PURCHASE_AMT_H;
		this.IS_AUTO_CX = IS_AUTO_CX;
		this.CHARGE_DATE_1 = CHARGE_DATE_1;
		this.CHARGE_DATE_2 = CHARGE_DATE_2;
		this.CHARGE_DATE_3 = CHARGE_DATE_3;
		this.CHARGE_DATE_4 = CHARGE_DATE_4;
		this.CHARGE_DATE_5 = CHARGE_DATE_5;
		this.CHARGE_DATE_6 = CHARGE_DATE_6;
		this.DEFAULT_FEE_RATE = DEFAULT_FEE_RATE;
		this.FEE_TYPE = FEE_TYPE;
		this.BARGAIN_STATUS = BARGAIN_STATUS;
		this.BARGAIN_APPLY_SEQ = BARGAIN_APPLY_SEQ;
		this.FEE_RATE = FEE_RATE;
		this.FEE_RATE_L = FEE_RATE_L;
		this.FEE_RATE_M = FEE_RATE_M;
		this.FEE_RATE_H = FEE_RATE_H;
		this.FEE = FEE;
		this.FEE_L = FEE_L;
		this.FEE_M = FEE_M;
		this.FEE_H = FEE_H;
		this.FEE_DISCOUNT = FEE_DISCOUNT;
		this.FEE_DISCOUNT_L = FEE_DISCOUNT_L;
		this.FEE_DISCOUNT_M = FEE_DISCOUNT_M;
		this.FEE_DISCOUNT_H = FEE_DISCOUNT_H;
		this.DEBIT_ACCT = DEBIT_ACCT;
		this.TRUST_ACCT = TRUST_ACCT;
		this.CREDIT_ACCT = CREDIT_ACCT;
		this.TRADE_DATE_TYPE = TRADE_DATE_TYPE;
		this.TRADE_DATE = TRADE_DATE;
		this.STOP_LOSS_PERC = STOP_LOSS_PERC;
		this.TAKE_PROFIT_PERC = TAKE_PROFIT_PERC;
		this.PL_NOTIFY_WAYS = PL_NOTIFY_WAYS;
		this.NARRATOR_ID = NARRATOR_ID;
		this.NARRATOR_NAME = NARRATOR_NAME;
		this.createtime = createtime;
		this.creator = creator;
		this.modifier = modifier;
		this.lastupdate = lastupdate;
		this.GROUP_OFA = GROUP_OFA;
		this.DEFAULT_FEE_RATE_L = DEFAULT_FEE_RATE_L;
		this.DEFAULT_FEE_RATE_M = DEFAULT_FEE_RATE_M;
		this.DEFAULT_FEE_RATE_H = DEFAULT_FEE_RATE_H;
		this.NOT_VERTIFY = NOT_VERTIFY;
		this.PROSPECTUS_TYPE = PROSPECTUS_TYPE;
		this.PLAN_ID = PLAN_ID;
		this.version = version;
		this.CONTRACT_ID = CONTRACT_ID;
		this.TRUST_PEOP_NUM = TRUST_PEOP_NUM;
	}

	public String getCONTRACT_ID() {
		return CONTRACT_ID;
	}

	public String getTRUST_PEOP_NUM() {
		return TRUST_PEOP_NUM;
	}

	public void setTRUST_PEOP_NUM(String tRUST_PEOP_NUM) {
		TRUST_PEOP_NUM = tRUST_PEOP_NUM;
	}

	public void setCONTRACT_ID(String cONTRACT_ID) {
		CONTRACT_ID = cONTRACT_ID;
	}

	/** default constructor */
	public TBSOT_NF_PURCHASE_DVO() {
	}

	/** minimal constructor */
	public TBSOT_NF_PURCHASE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DPK comp_id) {
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

	public BigDecimal getPURCHASE_AMT() {
		return this.PURCHASE_AMT;
	}

	public void setPURCHASE_AMT(BigDecimal PURCHASE_AMT) {
		this.PURCHASE_AMT = PURCHASE_AMT;
	}

	public BigDecimal getPURCHASE_AMT_L() {
		return this.PURCHASE_AMT_L;
	}

	public void setPURCHASE_AMT_L(BigDecimal PURCHASE_AMT_L) {
		this.PURCHASE_AMT_L = PURCHASE_AMT_L;
	}

	public BigDecimal getPURCHASE_AMT_M() {
		return this.PURCHASE_AMT_M;
	}

	public void setPURCHASE_AMT_M(BigDecimal PURCHASE_AMT_M) {
		this.PURCHASE_AMT_M = PURCHASE_AMT_M;
	}

	public BigDecimal getPURCHASE_AMT_H() {
		return this.PURCHASE_AMT_H;
	}

	public void setPURCHASE_AMT_H(BigDecimal PURCHASE_AMT_H) {
		this.PURCHASE_AMT_H = PURCHASE_AMT_H;
	}

	public String getIS_AUTO_CX() {
		return this.IS_AUTO_CX;
	}

	public void setIS_AUTO_CX(String IS_AUTO_CX) {
		this.IS_AUTO_CX = IS_AUTO_CX;
	}

	public String getCHARGE_DATE_1() {
		return this.CHARGE_DATE_1;
	}

	public void setCHARGE_DATE_1(String CHARGE_DATE_1) {
		this.CHARGE_DATE_1 = CHARGE_DATE_1;
	}

	public String getCHARGE_DATE_2() {
		return this.CHARGE_DATE_2;
	}

	public void setCHARGE_DATE_2(String CHARGE_DATE_2) {
		this.CHARGE_DATE_2 = CHARGE_DATE_2;
	}

	public String getCHARGE_DATE_3() {
		return this.CHARGE_DATE_3;
	}

	public void setCHARGE_DATE_3(String CHARGE_DATE_3) {
		this.CHARGE_DATE_3 = CHARGE_DATE_3;
	}

	public String getCHARGE_DATE_4() {
		return this.CHARGE_DATE_4;
	}

	public void setCHARGE_DATE_4(String CHARGE_DATE_4) {
		this.CHARGE_DATE_4 = CHARGE_DATE_4;
	}

	public String getCHARGE_DATE_5() {
		return this.CHARGE_DATE_5;
	}

	public void setCHARGE_DATE_5(String CHARGE_DATE_5) {
		this.CHARGE_DATE_5 = CHARGE_DATE_5;
	}

	public String getCHARGE_DATE_6() {
		return this.CHARGE_DATE_6;
	}

	public void setCHARGE_DATE_6(String CHARGE_DATE_6) {
		this.CHARGE_DATE_6 = CHARGE_DATE_6;
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

	public BigDecimal getFEE_RATE_L() {
		return this.FEE_RATE_L;
	}

	public void setFEE_RATE_L(BigDecimal FEE_RATE_L) {
		this.FEE_RATE_L = FEE_RATE_L;
	}

	public BigDecimal getFEE_RATE_M() {
		return this.FEE_RATE_M;
	}

	public void setFEE_RATE_M(BigDecimal FEE_RATE_M) {
		this.FEE_RATE_M = FEE_RATE_M;
	}

	public BigDecimal getFEE_RATE_H() {
		return this.FEE_RATE_H;
	}

	public void setFEE_RATE_H(BigDecimal FEE_RATE_H) {
		this.FEE_RATE_H = FEE_RATE_H;
	}

	public BigDecimal getFEE() {
		return this.FEE;
	}

	public void setFEE(BigDecimal FEE) {
		this.FEE = FEE;
	}

	public BigDecimal getFEE_L() {
		return this.FEE_L;
	}

	public void setFEE_L(BigDecimal FEE_L) {
		this.FEE_L = FEE_L;
	}

	public BigDecimal getFEE_M() {
		return this.FEE_M;
	}

	public void setFEE_M(BigDecimal FEE_M) {
		this.FEE_M = FEE_M;
	}

	public BigDecimal getFEE_H() {
		return this.FEE_H;
	}

	public void setFEE_H(BigDecimal FEE_H) {
		this.FEE_H = FEE_H;
	}

	public BigDecimal getFEE_DISCOUNT() {
		return this.FEE_DISCOUNT;
	}

	public void setFEE_DISCOUNT(BigDecimal FEE_DISCOUNT) {
		this.FEE_DISCOUNT = FEE_DISCOUNT;
	}

	public BigDecimal getFEE_DISCOUNT_L() {
		return this.FEE_DISCOUNT_L;
	}

	public void setFEE_DISCOUNT_L(BigDecimal FEE_DISCOUNT_L) {
		this.FEE_DISCOUNT_L = FEE_DISCOUNT_L;
	}

	public BigDecimal getFEE_DISCOUNT_M() {
		return this.FEE_DISCOUNT_M;
	}

	public void setFEE_DISCOUNT_M(BigDecimal FEE_DISCOUNT_M) {
		this.FEE_DISCOUNT_M = FEE_DISCOUNT_M;
	}

	public BigDecimal getFEE_DISCOUNT_H() {
		return this.FEE_DISCOUNT_H;
	}

	public void setFEE_DISCOUNT_H(BigDecimal FEE_DISCOUNT_H) {
		this.FEE_DISCOUNT_H = FEE_DISCOUNT_H;
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

	public String getTRADE_DATE_TYPE() {
		return this.TRADE_DATE_TYPE;
	}

	public void setTRADE_DATE_TYPE(String TRADE_DATE_TYPE) {
		this.TRADE_DATE_TYPE = TRADE_DATE_TYPE;
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

	public String getGROUP_OFA() {
		return this.GROUP_OFA;
	}

	public void setGROUP_OFA(String GROUP_OFA) {
		this.GROUP_OFA = GROUP_OFA;
	}

	public BigDecimal getDEFAULT_FEE_RATE_L() {
		return this.DEFAULT_FEE_RATE_L;
	}

	public void setDEFAULT_FEE_RATE_L(BigDecimal DEFAULT_FEE_RATE_L) {
		this.DEFAULT_FEE_RATE_L = DEFAULT_FEE_RATE_L;
	}

	public BigDecimal getDEFAULT_FEE_RATE_M() {
		return this.DEFAULT_FEE_RATE_M;
	}

	public void setDEFAULT_FEE_RATE_M(BigDecimal DEFAULT_FEE_RATE_M) {
		this.DEFAULT_FEE_RATE_M = DEFAULT_FEE_RATE_M;
	}

	public BigDecimal getDEFAULT_FEE_RATE_H() {
		return this.DEFAULT_FEE_RATE_H;
	}

	public void setDEFAULT_FEE_RATE_H(BigDecimal DEFAULT_FEE_RATE_H) {
		this.DEFAULT_FEE_RATE_H = DEFAULT_FEE_RATE_H;
	}

	public String getNOT_VERTIFY() {
		return this.NOT_VERTIFY;
	}

	public void setNOT_VERTIFY(String NOT_VERTIFY) {
		this.NOT_VERTIFY = NOT_VERTIFY;
	}

	public String getPROSPECTUS_TYPE() {
		return this.PROSPECTUS_TYPE;
	}

	public void setPROSPECTUS_TYPE(String PROSPECTUS_TYPE) {
		this.PROSPECTUS_TYPE = PROSPECTUS_TYPE;
	}

	public String getPLAN_ID() {
		return this.PLAN_ID;
	}

	public void setPLAN_ID(String PLAN_ID) {
		this.PLAN_ID = PLAN_ID;
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
		if (!(other instanceof TBSOT_NF_PURCHASE_DVO))
			return false;
		TBSOT_NF_PURCHASE_DVO castOther = (TBSOT_NF_PURCHASE_DVO) other;
		return new EqualsBuilder().append(this.getcomp_id(), castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}

}
