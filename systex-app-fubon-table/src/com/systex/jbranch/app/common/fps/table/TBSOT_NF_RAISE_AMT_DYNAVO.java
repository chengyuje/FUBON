package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_NF_RAISE_AMT_DYNAVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNAPK comp_id;

	/** nullable persistent field */
	private String BATCH_SEQ;

	/** nullable persistent field */
	private BigDecimal BATCH_NO;

	/** nullable persistent field */
	private String PROD_ID;

	/** nullable persistent field */
	private String PROD_NAME;

	/** nullable persistent field */
	private String PROD_ID_C1;

	/** nullable persistent field */
	private String PROD_NAME_C1;
	
	/** nullable persistent field */
	private String PROD_ID_C2;

	/** nullable persistent field */
	private String PROD_NAME_C2;
	
	/** nullable persistent field */
	private String PROD_ID_C3;

	/** nullable persistent field */
	private String PROD_NAME_C3;
	
	/** nullable persistent field */
	private String PROD_ID_C4;

	/** nullable persistent field */
	private String PROD_NAME_C4;
	
	/** nullable persistent field */
	private String PROD_ID_C5;

	/** nullable persistent field */
	private String PROD_NAME_C5;
	
	/** nullable persistent field */
	private String PROD_CURR;

	/** nullable persistent field */
	private String PROD_RISK_LV;

	/** nullable persistent field */
	private String PROD_RISK_LV_C1;
	
	/** nullable persistent field */
	private String PROD_RISK_LV_C2;
	
	/** nullable persistent field */
	private String PROD_RISK_LV_C3;
	
	/** nullable persistent field */
	private String PROD_RISK_LV_C4;
	
	/** nullable persistent field */
	private String PROD_RISK_LV_C5;
	
	/** nullable persistent field */
	private BigDecimal PROD_MIN_BUY_AMT;
	
	/** nullable persistent field */
	private BigDecimal PROD_MIN_GRD_AMT;
	
	/** nullable persistent field */
	private String TRUST_CURR_TYPE;

	/** nullable persistent field */
	private String TRUST_CURR;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT_C1;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT_C2;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT_C3;

	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT_C4;
	
	/** nullable persistent field */
	private BigDecimal PURCHASE_AMT_C5;
	
	/** nullable persistent field */
	private String CERTIFICATE_ID;
	
	/** nullable persistent field */
	private BigDecimal RAISE_AMT;
	
	/** nullable persistent field */
	private String TRANSFER_DATE;

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
	private BigDecimal ENGAGED_ROI1;
	
	/** nullable persistent field */
	private BigDecimal ENGAGED_ROI2;
	
	/** nullable persistent field */
	private BigDecimal ENGAGED_ROI3;
	
	/** nullable persistent field */
	private String NARRATOR_ID;

	/** nullable persistent field */
	private String NARRATOR_NAME;

	/** nullable persistent field */
	private String GROUP_OFA;

	/** nullable persistent field */
	private String NOT_VERTIFY;

	/** nullable persistent field */
	private String PROSPECTUS_TYPE;
	

	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNA";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** default constructor */
	public TBSOT_NF_RAISE_AMT_DYNAVO() {
	}

	/** minimal constructor */
	public TBSOT_NF_RAISE_AMT_DYNAVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNAPK comp_id) {
		this.comp_id = comp_id;
	}
	
	public com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNAPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNAPK comp_id) {
		this.comp_id = comp_id;
	}
	
	public String getBATCH_SEQ() {
		return BATCH_SEQ;
	}

	public void setBATCH_SEQ(String bATCH_SEQ) {
		BATCH_SEQ = bATCH_SEQ;
	}

	public BigDecimal getBATCH_NO() {
		return BATCH_NO;
	}

	public void setBATCH_NO(BigDecimal bATCH_NO) {
		BATCH_NO = bATCH_NO;
	}

	public String getPROD_ID() {
		return PROD_ID;
	}

	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}

	public String getPROD_NAME() {
		return PROD_NAME;
	}

	public void setPROD_NAME(String pROD_NAME) {
		PROD_NAME = pROD_NAME;
	}

	public String getPROD_ID_C1() {
		return PROD_ID_C1;
	}

	public void setPROD_ID_C1(String pROD_ID_C1) {
		PROD_ID_C1 = pROD_ID_C1;
	}

	public String getPROD_NAME_C1() {
		return PROD_NAME_C1;
	}

	public void setPROD_NAME_C1(String pROD_NAME_C1) {
		PROD_NAME_C1 = pROD_NAME_C1;
	}

	public String getPROD_ID_C2() {
		return PROD_ID_C2;
	}

	public void setPROD_ID_C2(String pROD_ID_C2) {
		PROD_ID_C2 = pROD_ID_C2;
	}

	public String getPROD_NAME_C2() {
		return PROD_NAME_C2;
	}

	public void setPROD_NAME_C2(String pROD_NAME_C2) {
		PROD_NAME_C2 = pROD_NAME_C2;
	}

	public String getPROD_ID_C3() {
		return PROD_ID_C3;
	}

	public void setPROD_ID_C3(String pROD_ID_C3) {
		PROD_ID_C3 = pROD_ID_C3;
	}

	public String getPROD_NAME_C3() {
		return PROD_NAME_C3;
	}

	public void setPROD_NAME_C3(String pROD_NAME_C3) {
		PROD_NAME_C3 = pROD_NAME_C3;
	}

	public String getPROD_CURR() {
		return PROD_CURR;
	}

	public void setPROD_CURR(String pROD_CURR) {
		PROD_CURR = pROD_CURR;
	}

	public String getPROD_RISK_LV() {
		return PROD_RISK_LV;
	}

	public void setPROD_RISK_LV(String pROD_RISK_LV) {
		PROD_RISK_LV = pROD_RISK_LV;
	}

	public String getPROD_RISK_LV_C1() {
		return PROD_RISK_LV_C1;
	}

	public void setPROD_RISK_LV_C1(String pROD_RISK_LV_C1) {
		PROD_RISK_LV_C1 = pROD_RISK_LV_C1;
	}

	public String getPROD_RISK_LV_C2() {
		return PROD_RISK_LV_C2;
	}

	public void setPROD_RISK_LV_C2(String pROD_RISK_LV_C2) {
		PROD_RISK_LV_C2 = pROD_RISK_LV_C2;
	}

	public String getPROD_RISK_LV_C3() {
		return PROD_RISK_LV_C3;
	}

	public void setPROD_RISK_LV_C3(String pROD_RISK_LV_C3) {
		PROD_RISK_LV_C3 = pROD_RISK_LV_C3;
	}

	public BigDecimal getPROD_MIN_BUY_AMT() {
		return PROD_MIN_BUY_AMT;
	}

	public void setPROD_MIN_BUY_AMT(BigDecimal pROD_MIN_BUY_AMT) {
		PROD_MIN_BUY_AMT = pROD_MIN_BUY_AMT;
	}

	public BigDecimal getPROD_MIN_GRD_AMT() {
		return PROD_MIN_GRD_AMT;
	}

	public void setPROD_MIN_GRD_AMT(BigDecimal pROD_MIN_GRD_AMT) {
		PROD_MIN_GRD_AMT = pROD_MIN_GRD_AMT;
	}

	public String getTRUST_CURR_TYPE() {
		return TRUST_CURR_TYPE;
	}

	public void setTRUST_CURR_TYPE(String tRUST_CURR_TYPE) {
		TRUST_CURR_TYPE = tRUST_CURR_TYPE;
	}

	public String getTRUST_CURR() {
		return TRUST_CURR;
	}

	public void setTRUST_CURR(String tRUST_CURR) {
		TRUST_CURR = tRUST_CURR;
	}

	public BigDecimal getPURCHASE_AMT() {
		return PURCHASE_AMT;
	}

	public void setPURCHASE_AMT(BigDecimal pURCHASE_AMT) {
		PURCHASE_AMT = pURCHASE_AMT;
	}

	public BigDecimal getPURCHASE_AMT_C1() {
		return PURCHASE_AMT_C1;
	}

	public void setPURCHASE_AMT_C1(BigDecimal pURCHASE_AMT_C1) {
		PURCHASE_AMT_C1 = pURCHASE_AMT_C1;
	}

	public BigDecimal getPURCHASE_AMT_C2() {
		return PURCHASE_AMT_C2;
	}

	public void setPURCHASE_AMT_C2(BigDecimal pURCHASE_AMT_C2) {
		PURCHASE_AMT_C2 = pURCHASE_AMT_C2;
	}

	public BigDecimal getPURCHASE_AMT_C3() {
		return PURCHASE_AMT_C3;
	}

	public void setPURCHASE_AMT_C3(BigDecimal pURCHASE_AMT_C3) {
		PURCHASE_AMT_C3 = pURCHASE_AMT_C3;
	}

	public BigDecimal getDEFAULT_FEE_RATE() {
		return DEFAULT_FEE_RATE;
	}

	public void setDEFAULT_FEE_RATE(BigDecimal dEFAULT_FEE_RATE) {
		DEFAULT_FEE_RATE = dEFAULT_FEE_RATE;
	}

	public String getFEE_TYPE() {
		return FEE_TYPE;
	}

	public void setFEE_TYPE(String fEE_TYPE) {
		FEE_TYPE = fEE_TYPE;
	}

	public String getBARGAIN_STATUS() {
		return BARGAIN_STATUS;
	}

	public void setBARGAIN_STATUS(String bARGAIN_STATUS) {
		BARGAIN_STATUS = bARGAIN_STATUS;
	}

	public String getBARGAIN_APPLY_SEQ() {
		return BARGAIN_APPLY_SEQ;
	}

	public void setBARGAIN_APPLY_SEQ(String bARGAIN_APPLY_SEQ) {
		BARGAIN_APPLY_SEQ = bARGAIN_APPLY_SEQ;
	}

	public BigDecimal getFEE_RATE() {
		return FEE_RATE;
	}

	public void setFEE_RATE(BigDecimal fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}

	public BigDecimal getFEE() {
		return FEE;
	}

	public void setFEE(BigDecimal fEE) {
		FEE = fEE;
	}

	public BigDecimal getFEE_DISCOUNT() {
		return FEE_DISCOUNT;
	}

	public void setFEE_DISCOUNT(BigDecimal fEE_DISCOUNT) {
		FEE_DISCOUNT = fEE_DISCOUNT;
	}

	public String getDEBIT_ACCT() {
		return DEBIT_ACCT;
	}

	public void setDEBIT_ACCT(String dEBIT_ACCT) {
		DEBIT_ACCT = dEBIT_ACCT;
	}

	public String getTRUST_ACCT() {
		return TRUST_ACCT;
	}

	public void setTRUST_ACCT(String tRUST_ACCT) {
		TRUST_ACCT = tRUST_ACCT;
	}

	public String getCREDIT_ACCT() {
		return CREDIT_ACCT;
	}

	public void setCREDIT_ACCT(String cREDIT_ACCT) {
		CREDIT_ACCT = cREDIT_ACCT;
	}

	public String getTRADE_DATE_TYPE() {
		return TRADE_DATE_TYPE;
	}

	public void setTRADE_DATE_TYPE(String tRADE_DATE_TYPE) {
		TRADE_DATE_TYPE = tRADE_DATE_TYPE;
	}

	public Timestamp getTRADE_DATE() {
		return TRADE_DATE;
	}

	public void setTRADE_DATE(Timestamp tRADE_DATE) {
		TRADE_DATE = tRADE_DATE;
	}

	public String getNARRATOR_ID() {
		return NARRATOR_ID;
	}

	public void setNARRATOR_ID(String nARRATOR_ID) {
		NARRATOR_ID = nARRATOR_ID;
	}

	public String getNARRATOR_NAME() {
		return NARRATOR_NAME;
	}

	public void setNARRATOR_NAME(String nARRATOR_NAME) {
		NARRATOR_NAME = nARRATOR_NAME;
	}

	public String getGROUP_OFA() {
		return GROUP_OFA;
	}

	public void setGROUP_OFA(String gROUP_OFA) {
		GROUP_OFA = gROUP_OFA;
	}

	public String getNOT_VERTIFY() {
		return NOT_VERTIFY;
	}

	public void setNOT_VERTIFY(String nOT_VERTIFY) {
		NOT_VERTIFY = nOT_VERTIFY;
	}

	public String getPROSPECTUS_TYPE() {
		return PROSPECTUS_TYPE;
	}

	public void setPROSPECTUS_TYPE(String pROSPECTUS_TYPE) {
		PROSPECTUS_TYPE = pROSPECTUS_TYPE;
	}

	public String getPROD_ID_C4() {
		return PROD_ID_C4;
	}

	public void setPROD_ID_C4(String pROD_ID_C4) {
		PROD_ID_C4 = pROD_ID_C4;
	}

	public String getPROD_NAME_C4() {
		return PROD_NAME_C4;
	}

	public void setPROD_NAME_C4(String pROD_NAME_C4) {
		PROD_NAME_C4 = pROD_NAME_C4;
	}

	public String getPROD_ID_C5() {
		return PROD_ID_C5;
	}

	public void setPROD_ID_C5(String pROD_ID_C5) {
		PROD_ID_C5 = pROD_ID_C5;
	}

	public String getPROD_NAME_C5() {
		return PROD_NAME_C5;
	}

	public void setPROD_NAME_C5(String pROD_NAME_C5) {
		PROD_NAME_C5 = pROD_NAME_C5;
	}

	public String getPROD_RISK_LV_C4() {
		return PROD_RISK_LV_C4;
	}

	public void setPROD_RISK_LV_C4(String pROD_RISK_LV_C4) {
		PROD_RISK_LV_C4 = pROD_RISK_LV_C4;
	}

	public String getPROD_RISK_LV_C5() {
		return PROD_RISK_LV_C5;
	}

	public void setPROD_RISK_LV_C5(String pROD_RISK_LV_C5) {
		PROD_RISK_LV_C5 = pROD_RISK_LV_C5;
	}

	public BigDecimal getPURCHASE_AMT_C4() {
		return PURCHASE_AMT_C4;
	}

	public void setPURCHASE_AMT_C4(BigDecimal pURCHASE_AMT_C4) {
		PURCHASE_AMT_C4 = pURCHASE_AMT_C4;
	}

	public BigDecimal getPURCHASE_AMT_C5() {
		return PURCHASE_AMT_C5;
	}

	public void setPURCHASE_AMT_C5(BigDecimal pURCHASE_AMT_C5) {
		PURCHASE_AMT_C5 = pURCHASE_AMT_C5;
	}

	public String getCERTIFICATE_ID() {
		return CERTIFICATE_ID;
	}

	public void setCERTIFICATE_ID(String cERTIFICATE_ID) {
		CERTIFICATE_ID = cERTIFICATE_ID;
	}

	public BigDecimal getRAISE_AMT() {
		return RAISE_AMT;
	}

	public void setRAISE_AMT(BigDecimal rAISE_AMT) {
		RAISE_AMT = rAISE_AMT;
	}

	public String getTRANSFER_DATE() {
		return TRANSFER_DATE;
	}

	public void setTRANSFER_DATE(String tRANSFER_DATE) {
		TRANSFER_DATE = tRANSFER_DATE;
	}

	public BigDecimal getENGAGED_ROI1() {
		return ENGAGED_ROI1;
	}

	public void setENGAGED_ROI1(BigDecimal eNGAGED_ROI1) {
		ENGAGED_ROI1 = eNGAGED_ROI1;
	}

	public BigDecimal getENGAGED_ROI2() {
		return ENGAGED_ROI2;
	}

	public void setENGAGED_ROI2(BigDecimal eNGAGED_ROI2) {
		ENGAGED_ROI2 = eNGAGED_ROI2;
	}

	public BigDecimal getENGAGED_ROI3() {
		return ENGAGED_ROI3;
	}

	public void setENGAGED_ROI3(BigDecimal eNGAGED_ROI3) {
		ENGAGED_ROI3 = eNGAGED_ROI3;
	}

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getcomp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof TBSOT_NF_RAISE_AMT_DYNAVO))
			return false;
		TBSOT_NF_RAISE_AMT_DYNAVO castOther = (TBSOT_NF_RAISE_AMT_DYNAVO) other;
		return new EqualsBuilder().append(this.getcomp_id(), castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}

}
