package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_NF_REDEEM_DVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DPK comp_id;

	/** nullable persistent field */
	private String BATCH_SEQ;

	/** nullable persistent field */
	private BigDecimal BATCH_NO;

	/** nullable persistent field */
	private String RDM_PROD_ID;

	/** nullable persistent field */
	private String RDM_PROD_NAME;

	/** nullable persistent field */
	private String RDM_PROD_CURR;

	/** nullable persistent field */
	private String RDM_PROD_RISK_LV;

	/** nullable persistent field */
	private String RDM_TRADE_TYPE;

	/** nullable persistent field */
	private String RDM_CERTIFICATE_ID;

	/** nullable persistent field */
	private String RDM_TRUST_CURR_TYPE;

	/** nullable persistent field */
	private String RDM_TRUST_CURR;

	/** nullable persistent field */
	private BigDecimal RDM_UNIT;

	/** nullable persistent field */
	private BigDecimal RDM_TRUST_AMT;

	/** nullable persistent field */
	private String REDEEM_TYPE;

	/** nullable persistent field */
	private BigDecimal UNIT_NUM;

	/** nullable persistent field */
	private BigDecimal PRESENT_VAL;

	/** nullable persistent field */
	private String CREDIT_ACCT;

	/** nullable persistent field */
	private String TRUST_ACCT;

	/** nullable persistent field */
	private String IS_END_CERTIFICATE;

	/** nullable persistent field */
	private String IS_RE_PURCHASE;

	/** nullable persistent field */
	private String TRADE_DATE_TYPE;

	/** nullable persistent field */
	private Timestamp TRADE_DATE;

	/** nullable persistent field */
	private BigDecimal RDM_FEE;

	/** nullable persistent field */
	private BigDecimal MGM_FEE;

	/** nullable persistent field */
	private String PCH_PROD_ID;

	/** nullable persistent field */
	private String PCH_PROD_NAME;

	/** nullable persistent field */
	private String PCH_PROD_CURR;

	/** nullable persistent field */
	private String PCH_PROD_RISK_LV;

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
	private String SHORT_TYPE;

	/** nullable persistent field */
	private String NOT_VERTIFY;

	/** nullable persistent field */
	private String PROSPECTUS_TYPE;
	
	private String CONTRACT_ID;
	
	private String TRUST_PEOP_NUM;

	/** nullable persistent field */
	private String RDM_TRADE_TYPE_D;
	
	/** nullable persistent field */
	private BigDecimal OVS_PRIVATE_SEQ;
	
	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_D";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBSOT_NF_REDEEM_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DPK comp_id, String BATCH_SEQ, BigDecimal BATCH_NO, String RDM_PROD_ID, String RDM_PROD_NAME, String RDM_PROD_CURR, String RDM_PROD_RISK_LV, String RDM_TRADE_TYPE, String RDM_CERTIFICATE_ID, String RDM_TRUST_CURR_TYPE, String RDM_TRUST_CURR, BigDecimal RDM_UNIT, BigDecimal RDM_TRUST_AMT, String REDEEM_TYPE, BigDecimal UNIT_NUM, BigDecimal PRESENT_VAL, String CREDIT_ACCT, String TRUST_ACCT, String IS_END_CERTIFICATE, String IS_RE_PURCHASE, String TRADE_DATE_TYPE, Timestamp TRADE_DATE, BigDecimal RDM_FEE, BigDecimal MGM_FEE, String PCH_PROD_ID, String PCH_PROD_NAME, String PCH_PROD_CURR, String PCH_PROD_RISK_LV, BigDecimal DEFAULT_FEE_RATE, String FEE_TYPE, String BARGAIN_STATUS, String BARGAIN_APPLY_SEQ, BigDecimal FEE_RATE, BigDecimal FEE, BigDecimal FEE_DISCOUNT, BigDecimal STOP_LOSS_PERC, BigDecimal TAKE_PROFIT_PERC, String PL_NOTIFY_WAYS, String NARRATOR_ID, String NARRATOR_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String GROUP_OFA, String SHORT_TYPE, String NOT_VERTIFY, String PROSPECTUS_TYPE, Long version, String CONTRACT_ID, String TRUST_PEOP_NUM, String RDM_TRADE_TYPE_D) {
		this.comp_id = comp_id;
		this.BATCH_SEQ = BATCH_SEQ;
		this.BATCH_NO = BATCH_NO;
		this.RDM_PROD_ID = RDM_PROD_ID;
		this.RDM_PROD_NAME = RDM_PROD_NAME;
		this.RDM_PROD_CURR = RDM_PROD_CURR;
		this.RDM_PROD_RISK_LV = RDM_PROD_RISK_LV;
		this.RDM_TRADE_TYPE = RDM_TRADE_TYPE;
		this.RDM_CERTIFICATE_ID = RDM_CERTIFICATE_ID;
		this.RDM_TRUST_CURR_TYPE = RDM_TRUST_CURR_TYPE;
		this.RDM_TRUST_CURR = RDM_TRUST_CURR;
		this.RDM_UNIT = RDM_UNIT;
		this.RDM_TRUST_AMT = RDM_TRUST_AMT;
		this.REDEEM_TYPE = REDEEM_TYPE;
		this.UNIT_NUM = UNIT_NUM;
		this.PRESENT_VAL = PRESENT_VAL;
		this.CREDIT_ACCT = CREDIT_ACCT;
		this.TRUST_ACCT = TRUST_ACCT;
		this.IS_END_CERTIFICATE = IS_END_CERTIFICATE;
		this.IS_RE_PURCHASE = IS_RE_PURCHASE;
		this.TRADE_DATE_TYPE = TRADE_DATE_TYPE;
		this.TRADE_DATE = TRADE_DATE;
		this.RDM_FEE = RDM_FEE;
		this.MGM_FEE = MGM_FEE;
		this.PCH_PROD_ID = PCH_PROD_ID;
		this.PCH_PROD_NAME = PCH_PROD_NAME;
		this.PCH_PROD_CURR = PCH_PROD_CURR;
		this.PCH_PROD_RISK_LV = PCH_PROD_RISK_LV;
		this.DEFAULT_FEE_RATE = DEFAULT_FEE_RATE;
		this.FEE_TYPE = FEE_TYPE;
		this.BARGAIN_STATUS = BARGAIN_STATUS;
		this.BARGAIN_APPLY_SEQ = BARGAIN_APPLY_SEQ;
		this.FEE_RATE = FEE_RATE;
		this.FEE = FEE;
		this.FEE_DISCOUNT = FEE_DISCOUNT;
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
		this.SHORT_TYPE = SHORT_TYPE;
		this.NOT_VERTIFY = NOT_VERTIFY;
		this.PROSPECTUS_TYPE = PROSPECTUS_TYPE;
		this.version = version;
		this.CONTRACT_ID = CONTRACT_ID;
		this.TRUST_PEOP_NUM = TRUST_PEOP_NUM;
		this.RDM_TRADE_TYPE_D = RDM_TRADE_TYPE_D;
	}

	/** default constructor */
	public TBSOT_NF_REDEEM_DVO() {
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
	public TBSOT_NF_REDEEM_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DPK comp_id) {
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

	public String getRDM_PROD_ID() {
		return this.RDM_PROD_ID;
	}

	public void setRDM_PROD_ID(String RDM_PROD_ID) {
		this.RDM_PROD_ID = RDM_PROD_ID;
	}

	public String getRDM_PROD_NAME() {
		return this.RDM_PROD_NAME;
	}

	public void setRDM_PROD_NAME(String RDM_PROD_NAME) {
		this.RDM_PROD_NAME = RDM_PROD_NAME;
	}

	public String getRDM_PROD_CURR() {
		return this.RDM_PROD_CURR;
	}

	public void setRDM_PROD_CURR(String RDM_PROD_CURR) {
		this.RDM_PROD_CURR = RDM_PROD_CURR;
	}

	public String getRDM_PROD_RISK_LV() {
		return this.RDM_PROD_RISK_LV;
	}

	public void setRDM_PROD_RISK_LV(String RDM_PROD_RISK_LV) {
		this.RDM_PROD_RISK_LV = RDM_PROD_RISK_LV;
	}

	public String getRDM_TRADE_TYPE() {
		return this.RDM_TRADE_TYPE;
	}

	public void setRDM_TRADE_TYPE(String RDM_TRADE_TYPE) {
		this.RDM_TRADE_TYPE = RDM_TRADE_TYPE;
	}

	public String getRDM_CERTIFICATE_ID() {
		return this.RDM_CERTIFICATE_ID;
	}

	public void setRDM_CERTIFICATE_ID(String RDM_CERTIFICATE_ID) {
		this.RDM_CERTIFICATE_ID = RDM_CERTIFICATE_ID;
	}

	public String getRDM_TRUST_CURR_TYPE() {
		return this.RDM_TRUST_CURR_TYPE;
	}

	public void setRDM_TRUST_CURR_TYPE(String RDM_TRUST_CURR_TYPE) {
		this.RDM_TRUST_CURR_TYPE = RDM_TRUST_CURR_TYPE;
	}

	public String getRDM_TRUST_CURR() {
		return this.RDM_TRUST_CURR;
	}

	public void setRDM_TRUST_CURR(String RDM_TRUST_CURR) {
		this.RDM_TRUST_CURR = RDM_TRUST_CURR;
	}

	public BigDecimal getRDM_UNIT() {
		return this.RDM_UNIT;
	}

	public void setRDM_UNIT(BigDecimal RDM_UNIT) {
		this.RDM_UNIT = RDM_UNIT;
	}

	public BigDecimal getRDM_TRUST_AMT() {
		return this.RDM_TRUST_AMT;
	}

	public void setRDM_TRUST_AMT(BigDecimal RDM_TRUST_AMT) {
		this.RDM_TRUST_AMT = RDM_TRUST_AMT;
	}

	public String getREDEEM_TYPE() {
		return this.REDEEM_TYPE;
	}

	public void setREDEEM_TYPE(String REDEEM_TYPE) {
		this.REDEEM_TYPE = REDEEM_TYPE;
	}

	public BigDecimal getUNIT_NUM() {
		return this.UNIT_NUM;
	}

	public void setUNIT_NUM(BigDecimal UNIT_NUM) {
		this.UNIT_NUM = UNIT_NUM;
	}

	public BigDecimal getPRESENT_VAL() {
		return this.PRESENT_VAL;
	}

	public void setPRESENT_VAL(BigDecimal PRESENT_VAL) {
		this.PRESENT_VAL = PRESENT_VAL;
	}

	public String getCREDIT_ACCT() {
		return this.CREDIT_ACCT;
	}

	public void setCREDIT_ACCT(String CREDIT_ACCT) {
		this.CREDIT_ACCT = CREDIT_ACCT;
	}

	public String getTRUST_ACCT() {
		return this.TRUST_ACCT;
	}

	public void setTRUST_ACCT(String TRUST_ACCT) {
		this.TRUST_ACCT = TRUST_ACCT;
	}

	public String getIS_END_CERTIFICATE() {
		return this.IS_END_CERTIFICATE;
	}

	public void setIS_END_CERTIFICATE(String IS_END_CERTIFICATE) {
		this.IS_END_CERTIFICATE = IS_END_CERTIFICATE;
	}

	public String getIS_RE_PURCHASE() {
		return this.IS_RE_PURCHASE;
	}

	public void setIS_RE_PURCHASE(String IS_RE_PURCHASE) {
		this.IS_RE_PURCHASE = IS_RE_PURCHASE;
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

	public BigDecimal getRDM_FEE() {
		return this.RDM_FEE;
	}

	public void setRDM_FEE(BigDecimal RDM_FEE) {
		this.RDM_FEE = RDM_FEE;
	}

	public BigDecimal getMGM_FEE() {
		return this.MGM_FEE;
	}

	public void setMGM_FEE(BigDecimal MGM_FEE) {
		this.MGM_FEE = MGM_FEE;
	}

	public String getPCH_PROD_ID() {
		return this.PCH_PROD_ID;
	}

	public void setPCH_PROD_ID(String PCH_PROD_ID) {
		this.PCH_PROD_ID = PCH_PROD_ID;
	}

	public String getPCH_PROD_NAME() {
		return this.PCH_PROD_NAME;
	}

	public void setPCH_PROD_NAME(String PCH_PROD_NAME) {
		this.PCH_PROD_NAME = PCH_PROD_NAME;
	}

	public String getPCH_PROD_CURR() {
		return this.PCH_PROD_CURR;
	}

	public void setPCH_PROD_CURR(String PCH_PROD_CURR) {
		this.PCH_PROD_CURR = PCH_PROD_CURR;
	}

	public String getPCH_PROD_RISK_LV() {
		return this.PCH_PROD_RISK_LV;
	}

	public void setPCH_PROD_RISK_LV(String PCH_PROD_RISK_LV) {
		this.PCH_PROD_RISK_LV = PCH_PROD_RISK_LV;
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

	public String getSHORT_TYPE() {
		return this.SHORT_TYPE;
	}

	public void setSHORT_TYPE(String SHORT_TYPE) {
		this.SHORT_TYPE = SHORT_TYPE;
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

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getcomp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof TBSOT_NF_REDEEM_DVO))
			return false;
		TBSOT_NF_REDEEM_DVO castOther = (TBSOT_NF_REDEEM_DVO) other;
		return new EqualsBuilder().append(this.getcomp_id(), castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}

	public String getRDM_TRADE_TYPE_D() {
		return RDM_TRADE_TYPE_D;
	}

	public void setRDM_TRADE_TYPE_D(String RDM_TRADE_TYPE_D) {
		this.RDM_TRADE_TYPE_D = RDM_TRADE_TYPE_D;
	}

	public BigDecimal getOVS_PRIVATE_SEQ() {
		return OVS_PRIVATE_SEQ;
	}

	public void setOVS_PRIVATE_SEQ(BigDecimal oVS_PRIVATE_SEQ) {
		OVS_PRIVATE_SEQ = oVS_PRIVATE_SEQ;
	}
	
}
