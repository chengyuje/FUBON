package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_NF_CHANGE_DVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DPK comp_id;

	/** nullable persistent field */
	private String TRADE_SUB_TYPE;

	/** nullable persistent field */
	private String CERTIFICATE_ID;

	/** nullable persistent field */
	private String B_PROD_ID;

	/** nullable persistent field */
	private String B_PROD_NAME;

	/** nullable persistent field */
	private String B_PROD_CURR;

	/** nullable persistent field */
	private String B_PROD_RISK_LV;

	/** nullable persistent field */
	private String B_TRUST_CURR;

	/** nullable persistent field */
	private String B_TRUST_CURR_TYPE;

	/** nullable persistent field */
	private BigDecimal B_TRUST_AMT;

	/** nullable persistent field */
	private BigDecimal B_PURCHASE_AMT_L;

	/** nullable persistent field */
	private BigDecimal B_PURCHASE_AMT_M;

	/** nullable persistent field */
	private BigDecimal B_PURCHASE_AMT_H;

	/** nullable persistent field */
	private String B_CHARGE_DATE_1;

	/** nullable persistent field */
	private String B_CHARGE_DATE_2;

	/** nullable persistent field */
	private String B_CHARGE_DATE_3;

	/** nullable persistent field */
	private String B_CHARGE_DATE_4;

	/** nullable persistent field */
	private String B_CHARGE_DATE_5;

	/** nullable persistent field */
	private String B_CHARGE_DATE_6;

	/** nullable persistent field */
	private String B_DEBIT_ACCT;

	/** nullable persistent field */
	private String B_CREDIT_ACCT;

	/** nullable persistent field */
	private String B_TRUST_ACCT;

	/** nullable persistent field */
	private String B_CERTIFICATE_STATUS;

	/** nullable persistent field */
	private String F_PROD_ID;

	/** nullable persistent field */
	private String F_PROD_NAME;

	/** nullable persistent field */
	private String F_PROD_CURR;

	/** nullable persistent field */
	private String F_PROD_RISK_LV;

	/** nullable persistent field */
	private String F_TRUST_CURR;

	/** nullable persistent field */
	private BigDecimal F_PURCHASE_AMT_L;

	/** nullable persistent field */
	private BigDecimal F_PURCHASE_AMT_M;

	/** nullable persistent field */
	private BigDecimal F_PURCHASE_AMT_H;

	/** nullable persistent field */
	private String F_CHARGE_DATE_1;

	/** nullable persistent field */
	private String F_CHARGE_DATE_2;

	/** nullable persistent field */
	private String F_CHARGE_DATE_3;

	/** nullable persistent field */
	private String F_CHARGE_DATE_4;

	/** nullable persistent field */
	private String F_CHARGE_DATE_5;

	/** nullable persistent field */
	private String F_CHARGE_DATE_6;

	/** nullable persistent field */
	private String F_DEBIT_ACCT;

	/** nullable persistent field */
	private String F_CREDIT_ACCT;

	/** nullable persistent field */
	private String F_CERTIFICATE_STATUS;

	/** nullable persistent field */
	private Timestamp F_HOLD_START_DATE;

	/** nullable persistent field */
	private Timestamp F_HOLD_END_DATE;

	/** nullable persistent field */
	private Timestamp F_RESUME_DATE;

	/** nullable persistent field */
	private BigDecimal F_FEE_L;

	/** nullable persistent field */
	private BigDecimal F_FEE_M;

	/** nullable persistent field */
	private BigDecimal F_FEE_H;

	/** nullable persistent field */
	private String NARRATOR_ID;

	/** nullable persistent field */
	private String NARRATOR_NAME;

	/** nullable persistent field */
	private String CHANGE_TYPE;

	/** nullable persistent field */
	private String TRADE_DATE_TYPE;

	/** nullable persistent field */
	private Timestamp TRADE_DATE;

	/** nullable persistent field */
	private String B_NOT_VERTIFY;

	/** nullable persistent field */
	private String PROSPECTUS_TYPE;

	/** nullable persistent field */
	private BigDecimal B_PURCHASE_AMT_EXCH_L;

	/** nullable persistent field */
	private BigDecimal B_PURCHASE_AMT_EXCH_M;

	/** nullable persistent field */
	private BigDecimal B_PURCHASE_AMT_EXCH_H;

	/** nullable persistent field */
	private String B_EXCH_CURR;

	/** nullable persistent field */
	private String TRADE_SUB_TYPE_D;

	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_D";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBSOT_NF_CHANGE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DPK comp_id, String TRADE_SUB_TYPE, String CERTIFICATE_ID, String B_PROD_ID, String B_PROD_NAME, String B_PROD_CURR, String B_PROD_RISK_LV, String B_TRUST_CURR, String B_TRUST_CURR_TYPE, BigDecimal B_TRUST_AMT, BigDecimal B_PURCHASE_AMT_L, BigDecimal B_PURCHASE_AMT_M, BigDecimal B_PURCHASE_AMT_H, String B_CHARGE_DATE_1, String B_CHARGE_DATE_2, String B_CHARGE_DATE_3, String B_CHARGE_DATE_4, String B_CHARGE_DATE_5, String B_CHARGE_DATE_6, String B_DEBIT_ACCT, String B_CREDIT_ACCT, String B_TRUST_ACCT, String B_CERTIFICATE_STATUS, String F_PROD_ID, String F_PROD_NAME, String F_PROD_CURR, String F_PROD_RISK_LV, String F_TRUST_CURR, BigDecimal F_PURCHASE_AMT_L, BigDecimal F_PURCHASE_AMT_M, BigDecimal F_PURCHASE_AMT_H, String F_CHARGE_DATE_1, String F_CHARGE_DATE_2, String F_CHARGE_DATE_3, String F_CHARGE_DATE_4, String F_CHARGE_DATE_5, String F_CHARGE_DATE_6, String F_DEBIT_ACCT, String F_CREDIT_ACCT, String F_CERTIFICATE_STATUS, Timestamp F_HOLD_START_DATE, Timestamp F_HOLD_END_DATE, Timestamp F_RESUME_DATE, BigDecimal F_FEE_L, BigDecimal F_FEE_M, BigDecimal F_FEE_H, String NARRATOR_ID, String NARRATOR_NAME, String CHANGE_TYPE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String TRADE_DATE_TYPE, Timestamp TRADE_DATE, String B_NOT_VERTIFY, String PROSPECTUS_TYPE, BigDecimal B_PURCHASE_AMT_EXCH_L, BigDecimal B_PURCHASE_AMT_EXCH_M, BigDecimal B_PURCHASE_AMT_EXCH_H, String B_EXCH_CURR, Long version, String TRADE_SUB_TYPE_D) {
		this.comp_id = comp_id;
		this.TRADE_SUB_TYPE = TRADE_SUB_TYPE;
		this.CERTIFICATE_ID = CERTIFICATE_ID;
		this.B_PROD_ID = B_PROD_ID;
		this.B_PROD_NAME = B_PROD_NAME;
		this.B_PROD_CURR = B_PROD_CURR;
		this.B_PROD_RISK_LV = B_PROD_RISK_LV;
		this.B_TRUST_CURR = B_TRUST_CURR;
		this.B_TRUST_CURR_TYPE = B_TRUST_CURR_TYPE;
		this.B_TRUST_AMT = B_TRUST_AMT;
		this.B_PURCHASE_AMT_L = B_PURCHASE_AMT_L;
		this.B_PURCHASE_AMT_M = B_PURCHASE_AMT_M;
		this.B_PURCHASE_AMT_H = B_PURCHASE_AMT_H;
		this.B_CHARGE_DATE_1 = B_CHARGE_DATE_1;
		this.B_CHARGE_DATE_2 = B_CHARGE_DATE_2;
		this.B_CHARGE_DATE_3 = B_CHARGE_DATE_3;
		this.B_CHARGE_DATE_4 = B_CHARGE_DATE_4;
		this.B_CHARGE_DATE_5 = B_CHARGE_DATE_5;
		this.B_CHARGE_DATE_6 = B_CHARGE_DATE_6;
		this.B_DEBIT_ACCT = B_DEBIT_ACCT;
		this.B_CREDIT_ACCT = B_CREDIT_ACCT;
		this.B_TRUST_ACCT = B_TRUST_ACCT;
		this.B_CERTIFICATE_STATUS = B_CERTIFICATE_STATUS;
		this.F_PROD_ID = F_PROD_ID;
		this.F_PROD_NAME = F_PROD_NAME;
		this.F_PROD_CURR = F_PROD_CURR;
		this.F_PROD_RISK_LV = F_PROD_RISK_LV;
		this.F_TRUST_CURR = F_TRUST_CURR;
		this.F_PURCHASE_AMT_L = F_PURCHASE_AMT_L;
		this.F_PURCHASE_AMT_M = F_PURCHASE_AMT_M;
		this.F_PURCHASE_AMT_H = F_PURCHASE_AMT_H;
		this.F_CHARGE_DATE_1 = F_CHARGE_DATE_1;
		this.F_CHARGE_DATE_2 = F_CHARGE_DATE_2;
		this.F_CHARGE_DATE_3 = F_CHARGE_DATE_3;
		this.F_CHARGE_DATE_4 = F_CHARGE_DATE_4;
		this.F_CHARGE_DATE_5 = F_CHARGE_DATE_5;
		this.F_CHARGE_DATE_6 = F_CHARGE_DATE_6;
		this.F_DEBIT_ACCT = F_DEBIT_ACCT;
		this.F_CREDIT_ACCT = F_CREDIT_ACCT;
		this.F_CERTIFICATE_STATUS = F_CERTIFICATE_STATUS;
		this.F_HOLD_START_DATE = F_HOLD_START_DATE;
		this.F_HOLD_END_DATE = F_HOLD_END_DATE;
		this.F_RESUME_DATE = F_RESUME_DATE;
		this.F_FEE_L = F_FEE_L;
		this.F_FEE_M = F_FEE_M;
		this.F_FEE_H = F_FEE_H;
		this.NARRATOR_ID = NARRATOR_ID;
		this.NARRATOR_NAME = NARRATOR_NAME;
		this.CHANGE_TYPE = CHANGE_TYPE;
		this.createtime = createtime;
		this.creator = creator;
		this.modifier = modifier;
		this.lastupdate = lastupdate;
		this.TRADE_DATE_TYPE = TRADE_DATE_TYPE;
		this.TRADE_DATE = TRADE_DATE;
		this.B_NOT_VERTIFY = B_NOT_VERTIFY;
		this.PROSPECTUS_TYPE = PROSPECTUS_TYPE;
		this.B_PURCHASE_AMT_EXCH_L = B_PURCHASE_AMT_EXCH_L;
		this.B_PURCHASE_AMT_EXCH_M = B_PURCHASE_AMT_EXCH_M;
		this.B_PURCHASE_AMT_EXCH_H = B_PURCHASE_AMT_EXCH_H;
		this.B_EXCH_CURR = B_EXCH_CURR;
		this.version = version;
		this.TRADE_SUB_TYPE_D = TRADE_SUB_TYPE_D;
	}

	/** default constructor */
	public TBSOT_NF_CHANGE_DVO() {
	}

	/** minimal constructor */
	public TBSOT_NF_CHANGE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DPK comp_id) {
		this.comp_id = comp_id;
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

	public String getB_PROD_ID() {
		return this.B_PROD_ID;
	}

	public void setB_PROD_ID(String B_PROD_ID) {
		this.B_PROD_ID = B_PROD_ID;
	}

	public String getB_PROD_NAME() {
		return this.B_PROD_NAME;
	}

	public void setB_PROD_NAME(String B_PROD_NAME) {
		this.B_PROD_NAME = B_PROD_NAME;
	}

	public String getB_PROD_CURR() {
		return this.B_PROD_CURR;
	}

	public void setB_PROD_CURR(String B_PROD_CURR) {
		this.B_PROD_CURR = B_PROD_CURR;
	}

	public String getB_PROD_RISK_LV() {
		return this.B_PROD_RISK_LV;
	}

	public void setB_PROD_RISK_LV(String B_PROD_RISK_LV) {
		this.B_PROD_RISK_LV = B_PROD_RISK_LV;
	}

	public String getB_TRUST_CURR() {
		return this.B_TRUST_CURR;
	}

	public void setB_TRUST_CURR(String B_TRUST_CURR) {
		this.B_TRUST_CURR = B_TRUST_CURR;
	}

	public String getB_TRUST_CURR_TYPE() {
		return this.B_TRUST_CURR_TYPE;
	}

	public void setB_TRUST_CURR_TYPE(String B_TRUST_CURR_TYPE) {
		this.B_TRUST_CURR_TYPE = B_TRUST_CURR_TYPE;
	}

	public BigDecimal getB_TRUST_AMT() {
		return this.B_TRUST_AMT;
	}

	public void setB_TRUST_AMT(BigDecimal B_TRUST_AMT) {
		this.B_TRUST_AMT = B_TRUST_AMT;
	}

	public BigDecimal getB_PURCHASE_AMT_L() {
		return this.B_PURCHASE_AMT_L;
	}

	public void setB_PURCHASE_AMT_L(BigDecimal B_PURCHASE_AMT_L) {
		this.B_PURCHASE_AMT_L = B_PURCHASE_AMT_L;
	}

	public BigDecimal getB_PURCHASE_AMT_M() {
		return this.B_PURCHASE_AMT_M;
	}

	public void setB_PURCHASE_AMT_M(BigDecimal B_PURCHASE_AMT_M) {
		this.B_PURCHASE_AMT_M = B_PURCHASE_AMT_M;
	}

	public BigDecimal getB_PURCHASE_AMT_H() {
		return this.B_PURCHASE_AMT_H;
	}

	public void setB_PURCHASE_AMT_H(BigDecimal B_PURCHASE_AMT_H) {
		this.B_PURCHASE_AMT_H = B_PURCHASE_AMT_H;
	}

	public String getB_CHARGE_DATE_1() {
		return this.B_CHARGE_DATE_1;
	}

	public void setB_CHARGE_DATE_1(String B_CHARGE_DATE_1) {
		this.B_CHARGE_DATE_1 = B_CHARGE_DATE_1;
	}

	public String getB_CHARGE_DATE_2() {
		return this.B_CHARGE_DATE_2;
	}

	public void setB_CHARGE_DATE_2(String B_CHARGE_DATE_2) {
		this.B_CHARGE_DATE_2 = B_CHARGE_DATE_2;
	}

	public String getB_CHARGE_DATE_3() {
		return this.B_CHARGE_DATE_3;
	}

	public void setB_CHARGE_DATE_3(String B_CHARGE_DATE_3) {
		this.B_CHARGE_DATE_3 = B_CHARGE_DATE_3;
	}

	public String getB_CHARGE_DATE_4() {
		return this.B_CHARGE_DATE_4;
	}

	public void setB_CHARGE_DATE_4(String B_CHARGE_DATE_4) {
		this.B_CHARGE_DATE_4 = B_CHARGE_DATE_4;
	}

	public String getB_CHARGE_DATE_5() {
		return this.B_CHARGE_DATE_5;
	}

	public void setB_CHARGE_DATE_5(String B_CHARGE_DATE_5) {
		this.B_CHARGE_DATE_5 = B_CHARGE_DATE_5;
	}

	public String getB_CHARGE_DATE_6() {
		return this.B_CHARGE_DATE_6;
	}

	public void setB_CHARGE_DATE_6(String B_CHARGE_DATE_6) {
		this.B_CHARGE_DATE_6 = B_CHARGE_DATE_6;
	}

	public String getB_DEBIT_ACCT() {
		return this.B_DEBIT_ACCT;
	}

	public void setB_DEBIT_ACCT(String B_DEBIT_ACCT) {
		this.B_DEBIT_ACCT = B_DEBIT_ACCT;
	}

	public String getB_CREDIT_ACCT() {
		return this.B_CREDIT_ACCT;
	}

	public void setB_CREDIT_ACCT(String B_CREDIT_ACCT) {
		this.B_CREDIT_ACCT = B_CREDIT_ACCT;
	}

	public String getB_TRUST_ACCT() {
		return this.B_TRUST_ACCT;
	}

	public void setB_TRUST_ACCT(String B_TRUST_ACCT) {
		this.B_TRUST_ACCT = B_TRUST_ACCT;
	}

	public String getB_CERTIFICATE_STATUS() {
		return this.B_CERTIFICATE_STATUS;
	}

	public void setB_CERTIFICATE_STATUS(String B_CERTIFICATE_STATUS) {
		this.B_CERTIFICATE_STATUS = B_CERTIFICATE_STATUS;
	}

	public String getF_PROD_ID() {
		return this.F_PROD_ID;
	}

	public void setF_PROD_ID(String F_PROD_ID) {
		this.F_PROD_ID = F_PROD_ID;
	}

	public String getF_PROD_NAME() {
		return this.F_PROD_NAME;
	}

	public void setF_PROD_NAME(String F_PROD_NAME) {
		this.F_PROD_NAME = F_PROD_NAME;
	}

	public String getF_PROD_CURR() {
		return this.F_PROD_CURR;
	}

	public void setF_PROD_CURR(String F_PROD_CURR) {
		this.F_PROD_CURR = F_PROD_CURR;
	}

	public String getF_PROD_RISK_LV() {
		return this.F_PROD_RISK_LV;
	}

	public void setF_PROD_RISK_LV(String F_PROD_RISK_LV) {
		this.F_PROD_RISK_LV = F_PROD_RISK_LV;
	}

	public String getF_TRUST_CURR() {
		return this.F_TRUST_CURR;
	}

	public void setF_TRUST_CURR(String F_TRUST_CURR) {
		this.F_TRUST_CURR = F_TRUST_CURR;
	}

	public BigDecimal getF_PURCHASE_AMT_L() {
		return this.F_PURCHASE_AMT_L;
	}

	public void setF_PURCHASE_AMT_L(BigDecimal F_PURCHASE_AMT_L) {
		this.F_PURCHASE_AMT_L = F_PURCHASE_AMT_L;
	}

	public BigDecimal getF_PURCHASE_AMT_M() {
		return this.F_PURCHASE_AMT_M;
	}

	public void setF_PURCHASE_AMT_M(BigDecimal F_PURCHASE_AMT_M) {
		this.F_PURCHASE_AMT_M = F_PURCHASE_AMT_M;
	}

	public BigDecimal getF_PURCHASE_AMT_H() {
		return this.F_PURCHASE_AMT_H;
	}

	public void setF_PURCHASE_AMT_H(BigDecimal F_PURCHASE_AMT_H) {
		this.F_PURCHASE_AMT_H = F_PURCHASE_AMT_H;
	}

	public String getF_CHARGE_DATE_1() {
		return this.F_CHARGE_DATE_1;
	}

	public void setF_CHARGE_DATE_1(String F_CHARGE_DATE_1) {
		this.F_CHARGE_DATE_1 = F_CHARGE_DATE_1;
	}

	public String getF_CHARGE_DATE_2() {
		return this.F_CHARGE_DATE_2;
	}

	public void setF_CHARGE_DATE_2(String F_CHARGE_DATE_2) {
		this.F_CHARGE_DATE_2 = F_CHARGE_DATE_2;
	}

	public String getF_CHARGE_DATE_3() {
		return this.F_CHARGE_DATE_3;
	}

	public void setF_CHARGE_DATE_3(String F_CHARGE_DATE_3) {
		this.F_CHARGE_DATE_3 = F_CHARGE_DATE_3;
	}

	public String getF_CHARGE_DATE_4() {
		return this.F_CHARGE_DATE_4;
	}

	public void setF_CHARGE_DATE_4(String F_CHARGE_DATE_4) {
		this.F_CHARGE_DATE_4 = F_CHARGE_DATE_4;
	}

	public String getF_CHARGE_DATE_5() {
		return this.F_CHARGE_DATE_5;
	}

	public void setF_CHARGE_DATE_5(String F_CHARGE_DATE_5) {
		this.F_CHARGE_DATE_5 = F_CHARGE_DATE_5;
	}

	public String getF_CHARGE_DATE_6() {
		return this.F_CHARGE_DATE_6;
	}

	public void setF_CHARGE_DATE_6(String F_CHARGE_DATE_6) {
		this.F_CHARGE_DATE_6 = F_CHARGE_DATE_6;
	}

	public String getF_DEBIT_ACCT() {
		return this.F_DEBIT_ACCT;
	}

	public void setF_DEBIT_ACCT(String F_DEBIT_ACCT) {
		this.F_DEBIT_ACCT = F_DEBIT_ACCT;
	}

	public String getF_CREDIT_ACCT() {
		return this.F_CREDIT_ACCT;
	}

	public void setF_CREDIT_ACCT(String F_CREDIT_ACCT) {
		this.F_CREDIT_ACCT = F_CREDIT_ACCT;
	}

	public String getF_CERTIFICATE_STATUS() {
		return this.F_CERTIFICATE_STATUS;
	}

	public void setF_CERTIFICATE_STATUS(String F_CERTIFICATE_STATUS) {
		this.F_CERTIFICATE_STATUS = F_CERTIFICATE_STATUS;
	}

	public Timestamp getF_HOLD_START_DATE() {
		return this.F_HOLD_START_DATE;
	}

	public void setF_HOLD_START_DATE(Timestamp F_HOLD_START_DATE) {
		this.F_HOLD_START_DATE = F_HOLD_START_DATE;
	}

	public Timestamp getF_HOLD_END_DATE() {
		return this.F_HOLD_END_DATE;
	}

	public void setF_HOLD_END_DATE(Timestamp F_HOLD_END_DATE) {
		this.F_HOLD_END_DATE = F_HOLD_END_DATE;
	}

	public Timestamp getF_RESUME_DATE() {
		return this.F_RESUME_DATE;
	}

	public void setF_RESUME_DATE(Timestamp F_RESUME_DATE) {
		this.F_RESUME_DATE = F_RESUME_DATE;
	}

	public BigDecimal getF_FEE_L() {
		return this.F_FEE_L;
	}

	public void setF_FEE_L(BigDecimal F_FEE_L) {
		this.F_FEE_L = F_FEE_L;
	}

	public BigDecimal getF_FEE_M() {
		return this.F_FEE_M;
	}

	public void setF_FEE_M(BigDecimal F_FEE_M) {
		this.F_FEE_M = F_FEE_M;
	}

	public BigDecimal getF_FEE_H() {
		return this.F_FEE_H;
	}

	public void setF_FEE_H(BigDecimal F_FEE_H) {
		this.F_FEE_H = F_FEE_H;
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

	public String getCHANGE_TYPE() {
		return this.CHANGE_TYPE;
	}

	public void setCHANGE_TYPE(String CHANGE_TYPE) {
		this.CHANGE_TYPE = CHANGE_TYPE;
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

	public String getB_NOT_VERTIFY() {
		return this.B_NOT_VERTIFY;
	}

	public void setB_NOT_VERTIFY(String B_NOT_VERTIFY) {
		this.B_NOT_VERTIFY = B_NOT_VERTIFY;
	}

	public String getPROSPECTUS_TYPE() {
		return this.PROSPECTUS_TYPE;
	}

	public void setPROSPECTUS_TYPE(String PROSPECTUS_TYPE) {
		this.PROSPECTUS_TYPE = PROSPECTUS_TYPE;
	}

	public BigDecimal getB_PURCHASE_AMT_EXCH_L() {
		return this.B_PURCHASE_AMT_EXCH_L;
	}

	public void setB_PURCHASE_AMT_EXCH_L(BigDecimal B_PURCHASE_AMT_EXCH_L) {
		this.B_PURCHASE_AMT_EXCH_L = B_PURCHASE_AMT_EXCH_L;
	}

	public BigDecimal getB_PURCHASE_AMT_EXCH_M() {
		return this.B_PURCHASE_AMT_EXCH_M;
	}

	public void setB_PURCHASE_AMT_EXCH_M(BigDecimal B_PURCHASE_AMT_EXCH_M) {
		this.B_PURCHASE_AMT_EXCH_M = B_PURCHASE_AMT_EXCH_M;
	}

	public BigDecimal getB_PURCHASE_AMT_EXCH_H() {
		return this.B_PURCHASE_AMT_EXCH_H;
	}

	public void setB_PURCHASE_AMT_EXCH_H(BigDecimal B_PURCHASE_AMT_EXCH_H) {
		this.B_PURCHASE_AMT_EXCH_H = B_PURCHASE_AMT_EXCH_H;
	}

	public String getB_EXCH_CURR() {
		return this.B_EXCH_CURR;
	}

	public void setB_EXCH_CURR(String B_EXCH_CURR) {
		this.B_EXCH_CURR = B_EXCH_CURR;
	}

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getcomp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof TBSOT_NF_CHANGE_DVO))
			return false;
		TBSOT_NF_CHANGE_DVO castOther = (TBSOT_NF_CHANGE_DVO) other;
		return new EqualsBuilder().append(this.getcomp_id(), castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}

	public String getTRADE_SUB_TYPE_D() {
		return TRADE_SUB_TYPE_D;
	}

	public void setTRADE_SUB_TYPE_D(String TRADE_SUB_TYPE_D) {
		this.TRADE_SUB_TYPE_D = TRADE_SUB_TYPE_D;
	}
}
