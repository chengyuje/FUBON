package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_NF_TRANSFER_DYNAVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DYNAPK comp_id;

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
	private String TRUST_CURR_TYPE;

	/** nullable persistent field */
	private String TRUST_CURR;

	/** nullable persistent field */
	private String TRUST_ACCT;
	
	/** nullable persistent field */
	private String CREDIT_ACCT;
	
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
	private String IN_PROD_ID;
	
	/** nullable persistent field */
	private String IN_PROD_NAME;
	
	/** nullable persistent field */
	private String IN_PROD_RISK_LV;
	
	/** nullable persistent field */
	private String IN_PROD_CURR; 
	
	/** nullable persistent field */
	private String IN_PROD_C1_YN;
	
	/** nullable persistent field */
	private String IN_PROD_C2_YN;
	
	/** nullable persistent field */
	private String IN_PROD_C3_YN;
	
	/** nullable persistent field */
	private String IN_PROD_C4_YN;
	
	/** nullable persistent field */
	private String IN_PROD_C5_YN;
	
	/** nullable persistent field */
	private String BATCH_SEQ_C1;
	
	/** nullable persistent field */
	private String BATCH_SEQ_C2;
	
	/** nullable persistent field */
	private String BATCH_SEQ_C3;
	
	/** nullable persistent field */
	private String BATCH_SEQ_C4;
	
	/** nullable persistent field */
	private String BATCH_SEQ_C5;
	
	/** nullable persistent field */
	private String CERTIFICATE_ID;

	/** nullable persistent field */
	private String TRADE_DATE_TYPE;

	/** nullable persistent field */
	private Timestamp TRADE_DATE;
	
	/** nullable persistent field */
	private String PROSPECTUS_TYPE;
	
	/** nullable persistent field */
	private String TRANSFER_TYPE;
	
	/** nullable persistent field */
	private BigDecimal UNIT_NUM;
	
	/** nullable persistent field */
	private BigDecimal UNIT_NUM_C1;
	
	/** nullable persistent field */
	private BigDecimal UNIT_NUM_C2;
	
	/** nullable persistent field */
	private BigDecimal UNIT_NUM_C3;
	
	/** nullable persistent field */
	private BigDecimal UNIT_NUM_C4;
	
	/** nullable persistent field */
	private BigDecimal UNIT_NUM_C5;
	
	/** nullable persistent field */
	private BigDecimal PRESENT_VAL;
	
	/** nullable persistent field */
	private String NARRATOR_ID;

	/** nullable persistent field */
	private String NARRATOR_NAME;
	
	private String DEBIT_ACCT;
	
	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DYNA";

	public String getDEBIT_ACCT() {
		return DEBIT_ACCT;
	}

	public void setDEBIT_ACCT(String dEBIT_ACCT) {
		DEBIT_ACCT = dEBIT_ACCT;
	}

	public String getTableuid() {
		return TABLE_UID;
	}

	/** default constructor */
	public TBSOT_NF_TRANSFER_DYNAVO() {
	}

	/** minimal constructor */
	public TBSOT_NF_TRANSFER_DYNAVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DYNAPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DYNAPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DYNAPK comp_id) {
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

	public String getIN_PROD_ID() {
		return IN_PROD_ID;
	}

	public void setIN_PROD_ID(String iN_PROD_ID) {
		IN_PROD_ID = iN_PROD_ID;
	}

	public String getIN_PROD_NAME() {
		return IN_PROD_NAME;
	}

	public void setIN_PROD_NAME(String iN_PROD_NAME) {
		IN_PROD_NAME = iN_PROD_NAME;
	}

	public String getIN_PROD_RISK_LV() {
		return IN_PROD_RISK_LV;
	}

	public void setIN_PROD_RISK_LV(String iN_PROD_RISK_LV) {
		IN_PROD_RISK_LV = iN_PROD_RISK_LV;
	}

	public String getIN_PROD_CURR() {
		return IN_PROD_CURR;
	}

	public void setIN_PROD_CURR(String iN_PROD_CURR) {
		IN_PROD_CURR = iN_PROD_CURR;
	}

	public String getIN_PROD_C1_YN() {
		return IN_PROD_C1_YN;
	}

	public void setIN_PROD_C1_YN(String iN_PROD_C1_YN) {
		IN_PROD_C1_YN = iN_PROD_C1_YN;
	}

	public String getIN_PROD_C2_YN() {
		return IN_PROD_C2_YN;
	}

	public void setIN_PROD_C2_YN(String iN_PROD_C2_YN) {
		IN_PROD_C2_YN = iN_PROD_C2_YN;
	}

	public String getIN_PROD_C3_YN() {
		return IN_PROD_C3_YN;
	}

	public void setIN_PROD_C3_YN(String iN_PROD_C3_YN) {
		IN_PROD_C3_YN = iN_PROD_C3_YN;
	}

	public String getIN_PROD_C4_YN() {
		return IN_PROD_C4_YN;
	}

	public void setIN_PROD_C4_YN(String iN_PROD_C4_YN) {
		IN_PROD_C4_YN = iN_PROD_C4_YN;
	}

	public String getIN_PROD_C5_YN() {
		return IN_PROD_C5_YN;
	}

	public void setIN_PROD_C5_YN(String iN_PROD_C5_YN) {
		IN_PROD_C5_YN = iN_PROD_C5_YN;
	}

	public String getBATCH_SEQ_C1() {
		return BATCH_SEQ_C1;
	}

	public void setBATCH_SEQ_C1(String bATCH_SEQ_C1) {
		BATCH_SEQ_C1 = bATCH_SEQ_C1;
	}

	public String getBATCH_SEQ_C2() {
		return BATCH_SEQ_C2;
	}

	public void setBATCH_SEQ_C2(String bATCH_SEQ_C2) {
		BATCH_SEQ_C2 = bATCH_SEQ_C2;
	}

	public String getBATCH_SEQ_C3() {
		return BATCH_SEQ_C3;
	}

	public void setBATCH_SEQ_C3(String bATCH_SEQ_C3) {
		BATCH_SEQ_C3 = bATCH_SEQ_C3;
	}

	public String getBATCH_SEQ_C4() {
		return BATCH_SEQ_C4;
	}

	public void setBATCH_SEQ_C4(String bATCH_SEQ_C4) {
		BATCH_SEQ_C4 = bATCH_SEQ_C4;
	}

	public String getBATCH_SEQ_C5() {
		return BATCH_SEQ_C5;
	}

	public void setBATCH_SEQ_C5(String bATCH_SEQ_C5) {
		BATCH_SEQ_C5 = bATCH_SEQ_C5;
	}

	public String getCERTIFICATE_ID() {
		return CERTIFICATE_ID;
	}

	public void setCERTIFICATE_ID(String cERTIFICATE_ID) {
		CERTIFICATE_ID = cERTIFICATE_ID;
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

	public String getPROSPECTUS_TYPE() {
		return PROSPECTUS_TYPE;
	}

	public void setPROSPECTUS_TYPE(String pROSPECTUS_TYPE) {
		PROSPECTUS_TYPE = pROSPECTUS_TYPE;
	}

	public String getTRANSFER_TYPE() {
		return TRANSFER_TYPE;
	}

	public void setTRANSFER_TYPE(String tRANSFER_TYPE) {
		TRANSFER_TYPE = tRANSFER_TYPE;
	}

	public BigDecimal getUNIT_NUM() {
		return UNIT_NUM;
	}

	public void setUNIT_NUM(BigDecimal uNIT_NUM) {
		UNIT_NUM = uNIT_NUM;
	}

	public BigDecimal getUNIT_NUM_C1() {
		return UNIT_NUM_C1;
	}

	public void setUNIT_NUM_C1(BigDecimal uNIT_NUM_C1) {
		UNIT_NUM_C1 = uNIT_NUM_C1;
	}

	public BigDecimal getUNIT_NUM_C2() {
		return UNIT_NUM_C2;
	}

	public void setUNIT_NUM_C2(BigDecimal uNIT_NUM_C2) {
		UNIT_NUM_C2 = uNIT_NUM_C2;
	}

	public BigDecimal getUNIT_NUM_C3() {
		return UNIT_NUM_C3;
	}

	public void setUNIT_NUM_C3(BigDecimal uNIT_NUM_C3) {
		UNIT_NUM_C3 = uNIT_NUM_C3;
	}

	public BigDecimal getUNIT_NUM_C4() {
		return UNIT_NUM_C4;
	}

	public void setUNIT_NUM_C4(BigDecimal uNIT_NUM_C4) {
		UNIT_NUM_C4 = uNIT_NUM_C4;
	}

	public BigDecimal getUNIT_NUM_C5() {
		return UNIT_NUM_C5;
	}

	public void setUNIT_NUM_C5(BigDecimal uNIT_NUM_C5) {
		UNIT_NUM_C5 = uNIT_NUM_C5;
	}

	public BigDecimal getPRESENT_VAL() {
		return PRESENT_VAL;
	}

	public void setPRESENT_VAL(BigDecimal pRESENT_VAL) {
		PRESENT_VAL = pRESENT_VAL;
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

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getcomp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof TBSOT_NF_TRANSFER_DYNAVO))
			return false;
		TBSOT_NF_TRANSFER_DYNAVO castOther = (TBSOT_NF_TRANSFER_DYNAVO) other;
		return new EqualsBuilder().append(this.getcomp_id(), castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}
}
