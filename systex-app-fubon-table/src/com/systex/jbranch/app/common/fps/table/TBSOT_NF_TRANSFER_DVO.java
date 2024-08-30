package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_NF_TRANSFER_DVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DPK comp_id;

	/** nullable persistent field */
	private String BATCH_SEQ;

	/** nullable persistent field */
	private BigDecimal BATCH_NO;

	/** nullable persistent field */
	private String OUT_PROD_ID;

	/** nullable persistent field */
	private String OUT_PROD_NAME;

	/** nullable persistent field */
	private String OUT_PROD_CURR;

	/** nullable persistent field */
	private String OUT_PROD_RISK_LV;

	/** nullable persistent field */
	private BigDecimal OUT_TRUST_AMT;

	/** nullable persistent field */
	private String OUT_TRADE_TYPE;

	/** nullable persistent field */
	private String OUT_CERTIFICATE_ID;

	/** nullable persistent field */
	private String OUT_TRUST_CURR_TYPE;

	/** nullable persistent field */
	private String OUT_TRUST_CURR;

	/** nullable persistent field */
	private BigDecimal OUT_UNIT;

	/** nullable persistent field */
	private BigDecimal OUT_PRESENT_VAL;

	/** nullable persistent field */
	private String OUT_TRUST_ACCT;

	/** nullable persistent field */
	private String TRANSFER_TYPE;

	/** nullable persistent field */
	private String IN_PROD_ID_1;

	/** nullable persistent field */
	private String IN_PROD_NAME_1;

	/** nullable persistent field */
	private String IN_PROD_CURR_1;

	/** nullable persistent field */
	private String IN_PROD_RISK_LV_1;

	/** nullable persistent field */
	private BigDecimal IN_UNIT_1;

	/** nullable persistent field */
	private BigDecimal IN_PRESENT_VAL_1;

	/** nullable persistent field */
	private String IN_PROD_ID_2;

	/** nullable persistent field */
	private String IN_PROD_NAME_2;

	/** nullable persistent field */
	private String IN_PROD_CURR_2;

	/** nullable persistent field */
	private String IN_PROD_RISK_LV_2;

	/** nullable persistent field */
	private BigDecimal IN_UNIT_2;

	/** nullable persistent field */
	private BigDecimal IN_PRESENT_VAL_2;

	/** nullable persistent field */
	private String IN_PROD_ID_3;

	/** nullable persistent field */
	private String IN_PROD_NAME_3;

	/** nullable persistent field */
	private String IN_PROD_CURR_3;

	/** nullable persistent field */
	private String IN_PROD_RISK_LV_3;

	/** nullable persistent field */
	private BigDecimal IN_UNIT_3;

	/** nullable persistent field */
	private BigDecimal IN_PRESENT_VAL_3;

	/** nullable persistent field */
	private String FEE_DEBIT_ACCT;

	/** nullable persistent field */
	private String TRADE_DATE_TYPE;

	/** nullable persistent field */
	private Timestamp TRADE_DATE;

	/** nullable persistent field */
	private BigDecimal FEE;

	/** nullable persistent field */
	private String NARRATOR_ID;

	/** nullable persistent field */
	private String NARRATOR_NAME;

	/** nullable persistent field */
	private String OUT_NOT_VERTIFY;

	/** nullable persistent field */
	private String SHORT_TYPE;

	/** nullable persistent field */
	private String PROSPECTUS_TYPE;

	/** nullable persistent field */
	private String OUT_TRADE_TYPE_D;
	
	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_D";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBSOT_NF_TRANSFER_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DPK comp_id, String BATCH_SEQ, BigDecimal BATCH_NO, String OUT_PROD_ID, String OUT_PROD_NAME, String OUT_PROD_CURR, String OUT_PROD_RISK_LV, BigDecimal OUT_TRUST_AMT, String OUT_TRADE_TYPE, String OUT_CERTIFICATE_ID, String OUT_TRUST_CURR_TYPE, String OUT_TRUST_CURR, BigDecimal OUT_UNIT, BigDecimal OUT_PRESENT_VAL, String OUT_TRUST_ACCT, String TRANSFER_TYPE, String IN_PROD_ID_1, String IN_PROD_NAME_1, String IN_PROD_CURR_1, String IN_PROD_RISK_LV_1, BigDecimal IN_UNIT_1, BigDecimal IN_PRESENT_VAL_1, String IN_PROD_ID_2, String IN_PROD_NAME_2, String IN_PROD_CURR_2, String IN_PROD_RISK_LV_2, BigDecimal IN_UNIT_2, BigDecimal IN_PRESENT_VAL_2, String IN_PROD_ID_3, String IN_PROD_NAME_3, String IN_PROD_CURR_3, String IN_PROD_RISK_LV_3, BigDecimal IN_UNIT_3, BigDecimal IN_PRESENT_VAL_3, String FEE_DEBIT_ACCT, String TRADE_DATE_TYPE, Timestamp TRADE_DATE, BigDecimal FEE, String NARRATOR_ID, String NARRATOR_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String OUT_NOT_VERTIFY, String SHORT_TYPE, String PROSPECTUS_TYPE, Long version, String OUT_TRADE_TYPE_D) {
		this.comp_id = comp_id;
		this.BATCH_SEQ = BATCH_SEQ;
		this.BATCH_NO = BATCH_NO;
		this.OUT_PROD_ID = OUT_PROD_ID;
		this.OUT_PROD_NAME = OUT_PROD_NAME;
		this.OUT_PROD_CURR = OUT_PROD_CURR;
		this.OUT_PROD_RISK_LV = OUT_PROD_RISK_LV;
		this.OUT_TRUST_AMT = OUT_TRUST_AMT;
		this.OUT_TRADE_TYPE = OUT_TRADE_TYPE;
		this.OUT_CERTIFICATE_ID = OUT_CERTIFICATE_ID;
		this.OUT_TRUST_CURR_TYPE = OUT_TRUST_CURR_TYPE;
		this.OUT_TRUST_CURR = OUT_TRUST_CURR;
		this.OUT_UNIT = OUT_UNIT;
		this.OUT_PRESENT_VAL = OUT_PRESENT_VAL;
		this.OUT_TRUST_ACCT = OUT_TRUST_ACCT;
		this.TRANSFER_TYPE = TRANSFER_TYPE;
		this.IN_PROD_ID_1 = IN_PROD_ID_1;
		this.IN_PROD_NAME_1 = IN_PROD_NAME_1;
		this.IN_PROD_CURR_1 = IN_PROD_CURR_1;
		this.IN_PROD_RISK_LV_1 = IN_PROD_RISK_LV_1;
		this.IN_UNIT_1 = IN_UNIT_1;
		this.IN_PRESENT_VAL_1 = IN_PRESENT_VAL_1;
		this.IN_PROD_ID_2 = IN_PROD_ID_2;
		this.IN_PROD_NAME_2 = IN_PROD_NAME_2;
		this.IN_PROD_CURR_2 = IN_PROD_CURR_2;
		this.IN_PROD_RISK_LV_2 = IN_PROD_RISK_LV_2;
		this.IN_UNIT_2 = IN_UNIT_2;
		this.IN_PRESENT_VAL_2 = IN_PRESENT_VAL_2;
		this.IN_PROD_ID_3 = IN_PROD_ID_3;
		this.IN_PROD_NAME_3 = IN_PROD_NAME_3;
		this.IN_PROD_CURR_3 = IN_PROD_CURR_3;
		this.IN_PROD_RISK_LV_3 = IN_PROD_RISK_LV_3;
		this.IN_UNIT_3 = IN_UNIT_3;
		this.IN_PRESENT_VAL_3 = IN_PRESENT_VAL_3;
		this.FEE_DEBIT_ACCT = FEE_DEBIT_ACCT;
		this.TRADE_DATE_TYPE = TRADE_DATE_TYPE;
		this.TRADE_DATE = TRADE_DATE;
		this.FEE = FEE;
		this.NARRATOR_ID = NARRATOR_ID;
		this.NARRATOR_NAME = NARRATOR_NAME;
		this.createtime = createtime;
		this.creator = creator;
		this.modifier = modifier;
		this.lastupdate = lastupdate;
		this.OUT_NOT_VERTIFY = OUT_NOT_VERTIFY;
		this.SHORT_TYPE = SHORT_TYPE;
		this.PROSPECTUS_TYPE = PROSPECTUS_TYPE;
		this.version = version;
		this.OUT_TRADE_TYPE_D = OUT_TRADE_TYPE_D;
	}

	/** default constructor */
	public TBSOT_NF_TRANSFER_DVO() {
	}

	/** minimal constructor */
	public TBSOT_NF_TRANSFER_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DPK comp_id) {
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

	public String getOUT_PROD_ID() {
		return this.OUT_PROD_ID;
	}

	public void setOUT_PROD_ID(String OUT_PROD_ID) {
		this.OUT_PROD_ID = OUT_PROD_ID;
	}

	public String getOUT_PROD_NAME() {
		return this.OUT_PROD_NAME;
	}

	public void setOUT_PROD_NAME(String OUT_PROD_NAME) {
		this.OUT_PROD_NAME = OUT_PROD_NAME;
	}

	public String getOUT_PROD_CURR() {
		return this.OUT_PROD_CURR;
	}

	public void setOUT_PROD_CURR(String OUT_PROD_CURR) {
		this.OUT_PROD_CURR = OUT_PROD_CURR;
	}

	public String getOUT_PROD_RISK_LV() {
		return this.OUT_PROD_RISK_LV;
	}

	public void setOUT_PROD_RISK_LV(String OUT_PROD_RISK_LV) {
		this.OUT_PROD_RISK_LV = OUT_PROD_RISK_LV;
	}

	public BigDecimal getOUT_TRUST_AMT() {
		return this.OUT_TRUST_AMT;
	}

	public void setOUT_TRUST_AMT(BigDecimal OUT_TRUST_AMT) {
		this.OUT_TRUST_AMT = OUT_TRUST_AMT;
	}

	public String getOUT_TRADE_TYPE() {
		return this.OUT_TRADE_TYPE;
	}

	public void setOUT_TRADE_TYPE(String OUT_TRADE_TYPE) {
		this.OUT_TRADE_TYPE = OUT_TRADE_TYPE;
	}

	public String getOUT_CERTIFICATE_ID() {
		return this.OUT_CERTIFICATE_ID;
	}

	public void setOUT_CERTIFICATE_ID(String OUT_CERTIFICATE_ID) {
		this.OUT_CERTIFICATE_ID = OUT_CERTIFICATE_ID;
	}

	public String getOUT_TRUST_CURR_TYPE() {
		return this.OUT_TRUST_CURR_TYPE;
	}

	public void setOUT_TRUST_CURR_TYPE(String OUT_TRUST_CURR_TYPE) {
		this.OUT_TRUST_CURR_TYPE = OUT_TRUST_CURR_TYPE;
	}

	public String getOUT_TRUST_CURR() {
		return this.OUT_TRUST_CURR;
	}

	public void setOUT_TRUST_CURR(String OUT_TRUST_CURR) {
		this.OUT_TRUST_CURR = OUT_TRUST_CURR;
	}

	public BigDecimal getOUT_UNIT() {
		return this.OUT_UNIT;
	}

	public void setOUT_UNIT(BigDecimal OUT_UNIT) {
		this.OUT_UNIT = OUT_UNIT;
	}

	public BigDecimal getOUT_PRESENT_VAL() {
		return this.OUT_PRESENT_VAL;
	}

	public void setOUT_PRESENT_VAL(BigDecimal OUT_PRESENT_VAL) {
		this.OUT_PRESENT_VAL = OUT_PRESENT_VAL;
	}

	public String getOUT_TRUST_ACCT() {
		return this.OUT_TRUST_ACCT;
	}

	public void setOUT_TRUST_ACCT(String OUT_TRUST_ACCT) {
		this.OUT_TRUST_ACCT = OUT_TRUST_ACCT;
	}

	public String getTRANSFER_TYPE() {
		return this.TRANSFER_TYPE;
	}

	public void setTRANSFER_TYPE(String TRANSFER_TYPE) {
		this.TRANSFER_TYPE = TRANSFER_TYPE;
	}

	public String getIN_PROD_ID_1() {
		return this.IN_PROD_ID_1;
	}

	public void setIN_PROD_ID_1(String IN_PROD_ID_1) {
		this.IN_PROD_ID_1 = IN_PROD_ID_1;
	}

	public String getIN_PROD_NAME_1() {
		return this.IN_PROD_NAME_1;
	}

	public void setIN_PROD_NAME_1(String IN_PROD_NAME_1) {
		this.IN_PROD_NAME_1 = IN_PROD_NAME_1;
	}

	public String getIN_PROD_CURR_1() {
		return this.IN_PROD_CURR_1;
	}

	public void setIN_PROD_CURR_1(String IN_PROD_CURR_1) {
		this.IN_PROD_CURR_1 = IN_PROD_CURR_1;
	}

	public String getIN_PROD_RISK_LV_1() {
		return this.IN_PROD_RISK_LV_1;
	}

	public void setIN_PROD_RISK_LV_1(String IN_PROD_RISK_LV_1) {
		this.IN_PROD_RISK_LV_1 = IN_PROD_RISK_LV_1;
	}

	public BigDecimal getIN_UNIT_1() {
		return this.IN_UNIT_1;
	}

	public void setIN_UNIT_1(BigDecimal IN_UNIT_1) {
		this.IN_UNIT_1 = IN_UNIT_1;
	}

	public BigDecimal getIN_PRESENT_VAL_1() {
		return this.IN_PRESENT_VAL_1;
	}

	public void setIN_PRESENT_VAL_1(BigDecimal IN_PRESENT_VAL_1) {
		this.IN_PRESENT_VAL_1 = IN_PRESENT_VAL_1;
	}

	public String getIN_PROD_ID_2() {
		return this.IN_PROD_ID_2;
	}

	public void setIN_PROD_ID_2(String IN_PROD_ID_2) {
		this.IN_PROD_ID_2 = IN_PROD_ID_2;
	}

	public String getIN_PROD_NAME_2() {
		return this.IN_PROD_NAME_2;
	}

	public void setIN_PROD_NAME_2(String IN_PROD_NAME_2) {
		this.IN_PROD_NAME_2 = IN_PROD_NAME_2;
	}

	public String getIN_PROD_CURR_2() {
		return this.IN_PROD_CURR_2;
	}

	public void setIN_PROD_CURR_2(String IN_PROD_CURR_2) {
		this.IN_PROD_CURR_2 = IN_PROD_CURR_2;
	}

	public String getIN_PROD_RISK_LV_2() {
		return this.IN_PROD_RISK_LV_2;
	}

	public void setIN_PROD_RISK_LV_2(String IN_PROD_RISK_LV_2) {
		this.IN_PROD_RISK_LV_2 = IN_PROD_RISK_LV_2;
	}

	public BigDecimal getIN_UNIT_2() {
		return this.IN_UNIT_2;
	}

	public void setIN_UNIT_2(BigDecimal IN_UNIT_2) {
		this.IN_UNIT_2 = IN_UNIT_2;
	}

	public BigDecimal getIN_PRESENT_VAL_2() {
		return this.IN_PRESENT_VAL_2;
	}

	public void setIN_PRESENT_VAL_2(BigDecimal IN_PRESENT_VAL_2) {
		this.IN_PRESENT_VAL_2 = IN_PRESENT_VAL_2;
	}

	public String getIN_PROD_ID_3() {
		return this.IN_PROD_ID_3;
	}

	public void setIN_PROD_ID_3(String IN_PROD_ID_3) {
		this.IN_PROD_ID_3 = IN_PROD_ID_3;
	}

	public String getIN_PROD_NAME_3() {
		return this.IN_PROD_NAME_3;
	}

	public void setIN_PROD_NAME_3(String IN_PROD_NAME_3) {
		this.IN_PROD_NAME_3 = IN_PROD_NAME_3;
	}

	public String getIN_PROD_CURR_3() {
		return this.IN_PROD_CURR_3;
	}

	public void setIN_PROD_CURR_3(String IN_PROD_CURR_3) {
		this.IN_PROD_CURR_3 = IN_PROD_CURR_3;
	}

	public String getIN_PROD_RISK_LV_3() {
		return this.IN_PROD_RISK_LV_3;
	}

	public void setIN_PROD_RISK_LV_3(String IN_PROD_RISK_LV_3) {
		this.IN_PROD_RISK_LV_3 = IN_PROD_RISK_LV_3;
	}

	public BigDecimal getIN_UNIT_3() {
		return this.IN_UNIT_3;
	}

	public void setIN_UNIT_3(BigDecimal IN_UNIT_3) {
		this.IN_UNIT_3 = IN_UNIT_3;
	}

	public BigDecimal getIN_PRESENT_VAL_3() {
		return this.IN_PRESENT_VAL_3;
	}

	public void setIN_PRESENT_VAL_3(BigDecimal IN_PRESENT_VAL_3) {
		this.IN_PRESENT_VAL_3 = IN_PRESENT_VAL_3;
	}

	public String getFEE_DEBIT_ACCT() {
		return this.FEE_DEBIT_ACCT;
	}

	public void setFEE_DEBIT_ACCT(String FEE_DEBIT_ACCT) {
		this.FEE_DEBIT_ACCT = FEE_DEBIT_ACCT;
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

	public BigDecimal getFEE() {
		return this.FEE;
	}

	public void setFEE(BigDecimal FEE) {
		this.FEE = FEE;
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

	public String getOUT_NOT_VERTIFY() {
		return this.OUT_NOT_VERTIFY;
	}

	public void setOUT_NOT_VERTIFY(String OUT_NOT_VERTIFY) {
		this.OUT_NOT_VERTIFY = OUT_NOT_VERTIFY;
	}

	public String getSHORT_TYPE() {
		return this.SHORT_TYPE;
	}

	public void setSHORT_TYPE(String SHORT_TYPE) {
		this.SHORT_TYPE = SHORT_TYPE;
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
		if (!(other instanceof TBSOT_NF_TRANSFER_DVO))
			return false;
		TBSOT_NF_TRANSFER_DVO castOther = (TBSOT_NF_TRANSFER_DVO) other;
		return new EqualsBuilder().append(this.getcomp_id(), castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}

	public String getOUT_TRADE_TYPE_D() {
		return OUT_TRADE_TYPE_D;
	}

	public void setOUT_TRADE_TYPE_D(String OUT_TRADE_TYPE_D) {
		this.OUT_TRADE_TYPE_D = OUT_TRADE_TYPE_D;
	}
}
