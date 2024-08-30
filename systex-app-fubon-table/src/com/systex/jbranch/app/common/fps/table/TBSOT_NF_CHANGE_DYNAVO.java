package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_NF_CHANGE_DYNAVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DYNAPK comp_id;

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
	private String PROD_STATUS_C1;
	
	/** nullable persistent field */
	private String PROD_STATUS_C2;
	
	/** nullable persistent field */
	private String PROD_STATUS_C3;
	
	/** nullable persistent field */
	private String PROD_STATUS_C4;
	
	/** nullable persistent field */
	private String PROD_STATUS_C5;
	
	/** nullable persistent field */
	private String TRANSFER_DATE;
	
	/** nullable persistent field */
	private String CHG_STATUS_YN;
	
	/** nullable persistent field */
	private String CHG_AMOUNT_YN;
	
	/** nullable persistent field */
	private String CHG_TRANSDATE_YN;
	
	/** nullable persistent field */
	private String CHG_ADDPROD_YN;
	
	/** nullable persistent field */
	private String F_PROD_STATUS_C1;
	
	/** nullable persistent field */
	private String F_PROD_STATUS_C2;
	
	/** nullable persistent field */
	private String F_PROD_STATUS_C3;
	
	/** nullable persistent field */
	private String F_PROD_STATUS_C4;
	
	/** nullable persistent field */
	private String F_PROD_STATUS_C5;
	
	/** nullable persistent field */
	private BigDecimal F_PURCHASE_AMT_C1;

	/** nullable persistent field */
	private BigDecimal F_PURCHASE_AMT_C2;

	/** nullable persistent field */
	private BigDecimal F_PURCHASE_AMT_C3;

	/** nullable persistent field */
	private BigDecimal F_PURCHASE_AMT_C4;
	
	/** nullable persistent field */
	private BigDecimal F_PURCHASE_AMT_C5;
	
	/** nullable persistent field */
	private String F_TRANSFER_DATE_1;
	
	/** nullable persistent field */
	private String F_TRANSFER_DATE_2;
	
	/** nullable persistent field */
	private String F_TRANSFER_DATE_3;
	
	/** nullable persistent field */
	private String F_TRANSFER_DATE_4;
	
	/** nullable persistent field */
	private String F_TRANSFER_DATE_5;
	
	/** nullable persistent field */
	private String F_TRANSFER_DATE_6;
	
	/** nullable persistent field */
	private String F_PROD_ID_C1;

	/** nullable persistent field */
	private String F_PROD_NAME_C1;
	
	/** nullable persistent field */
	private String F_PROD_ID_C2;

	/** nullable persistent field */
	private String F_PROD_NAME_C2;
	
	/** nullable persistent field */
	private String F_PROD_ID_C3;

	/** nullable persistent field */
	private String F_PROD_NAME_C3;
	
	/** nullable persistent field */
	private String F_PROD_ID_C4;

	/** nullable persistent field */
	private String F_PROD_NAME_C4;
	
	/** nullable persistent field */
	private String F_PROD_ID_C5;

	/** nullable persistent field */
	private String F_PROD_NAME_C5;
	
	/** nullable persistent field */
	private BigDecimal F_ADDPROD_AMT_C1;
	
	/** nullable persistent field */
	private BigDecimal F_ADDPROD_AMT_C2;
	
	/** nullable persistent field */
	private BigDecimal F_ADDPROD_AMT_C3;
	
	/** nullable persistent field */
	private BigDecimal F_ADDPROD_AMT_C4;
	
	/** nullable persistent field */
	private BigDecimal F_ADDPROD_AMT_C5;
	
	/** nullable persistent field */
	private String F_PROD_RISK_LV_C1;
	
	/** nullable persistent field */
	private String F_PROD_RISK_LV_C2;
	
	/** nullable persistent field */
	private String F_PROD_RISK_LV_C3;
	
	/** nullable persistent field */
	private String F_PROD_RISK_LV_C4;
	
	/** nullable persistent field */
	private String F_PROD_RISK_LV_C5;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_BUY_AMT_C1;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_BUY_AMT_C2;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_BUY_AMT_C3;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_BUY_AMT_C4;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_BUY_AMT_C5;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_GRD_AMT_C1;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_GRD_AMT_C2;

	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_GRD_AMT_C3;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_GRD_AMT_C4;
	
	/** nullable persistent field */
	private BigDecimal F_PROD_MIN_GRD_AMT_C5;
	
	/** nullable persistent field */
	private String BATCH_SEQ_STATUS;
	
	/** nullable persistent field */
	private String BATCH_SEQ_AMOUNT;
	
	/** nullable persistent field */
	private String BATCH_SEQ_TRANSDATE;
	
	/** nullable persistent field */
	private String BATCH_SEQ_ADDPROD;
	
	/** nullable persistent field */
	private String CERTIFICATE_ID;

	/** nullable persistent field */
	private String TRADE_DATE_TYPE;

	/** nullable persistent field */
	private Timestamp TRADE_DATE;
	
	/** nullable persistent field */
	private String NARRATOR_ID;

	/** nullable persistent field */
	private String NARRATOR_NAME;
	
	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DYNA";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** default constructor */
	public TBSOT_NF_CHANGE_DYNAVO() {
	}

	/** minimal constructor */
	public TBSOT_NF_CHANGE_DYNAVO(com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DYNAPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DYNAPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DYNAPK comp_id) {
		this.comp_id = comp_id;
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

	public String getPROD_STATUS_C1() {
		return PROD_STATUS_C1;
	}

	public void setPROD_STATUS_C1(String pROD_STATUS_C1) {
		PROD_STATUS_C1 = pROD_STATUS_C1;
	}

	public String getPROD_STATUS_C2() {
		return PROD_STATUS_C2;
	}

	public void setPROD_STATUS_C2(String pROD_STATUS_C2) {
		PROD_STATUS_C2 = pROD_STATUS_C2;
	}

	public String getPROD_STATUS_C3() {
		return PROD_STATUS_C3;
	}

	public void setPROD_STATUS_C3(String pROD_STATUS_C3) {
		PROD_STATUS_C3 = pROD_STATUS_C3;
	}

	public String getPROD_STATUS_C4() {
		return PROD_STATUS_C4;
	}

	public void setPROD_STATUS_C4(String pROD_STATUS_C4) {
		PROD_STATUS_C4 = pROD_STATUS_C4;
	}

	public String getPROD_STATUS_C5() {
		return PROD_STATUS_C5;
	}

	public String getTRANSFER_DATE() {
		return TRANSFER_DATE;
	}

	public void setTRANSFER_DATE(String tRANSFER_DATE) {
		TRANSFER_DATE = tRANSFER_DATE;
	}

	public void setPROD_STATUS_C5(String pROD_STATUS_C5) {
		PROD_STATUS_C5 = pROD_STATUS_C5;
	}

	public String getCHG_STATUS_YN() {
		return CHG_STATUS_YN;
	}

	public void setCHG_STATUS_YN(String cHG_STATUS_YN) {
		CHG_STATUS_YN = cHG_STATUS_YN;
	}

	public String getCHG_AMOUNT_YN() {
		return CHG_AMOUNT_YN;
	}

	public void setCHG_AMOUNT_YN(String cHG_AMOUNT_YN) {
		CHG_AMOUNT_YN = cHG_AMOUNT_YN;
	}

	public String getCHG_TRANSDATE_YN() {
		return CHG_TRANSDATE_YN;
	}

	public void setCHG_TRANSDATE_YN(String cHG_TRANSDATE_YN) {
		CHG_TRANSDATE_YN = cHG_TRANSDATE_YN;
	}

	public String getCHG_ADDPROD_YN() {
		return CHG_ADDPROD_YN;
	}

	public void setCHG_ADDPROD_YN(String cHG_ADDPROD_YN) {
		CHG_ADDPROD_YN = cHG_ADDPROD_YN;
	}

	public String getF_PROD_STATUS_C1() {
		return F_PROD_STATUS_C1;
	}

	public void setF_PROD_STATUS_C1(String f_PROD_STATUS_C1) {
		F_PROD_STATUS_C1 = f_PROD_STATUS_C1;
	}

	public String getF_PROD_STATUS_C2() {
		return F_PROD_STATUS_C2;
	}

	public void setF_PROD_STATUS_C2(String f_PROD_STATUS_C2) {
		F_PROD_STATUS_C2 = f_PROD_STATUS_C2;
	}

	public String getF_PROD_STATUS_C3() {
		return F_PROD_STATUS_C3;
	}

	public void setF_PROD_STATUS_C3(String f_PROD_STATUS_C3) {
		F_PROD_STATUS_C3 = f_PROD_STATUS_C3;
	}

	public String getF_PROD_STATUS_C4() {
		return F_PROD_STATUS_C4;
	}

	public void setF_PROD_STATUS_C4(String f_PROD_STATUS_C4) {
		F_PROD_STATUS_C4 = f_PROD_STATUS_C4;
	}

	public String getF_PROD_STATUS_C5() {
		return F_PROD_STATUS_C5;
	}

	public void setF_PROD_STATUS_C5(String f_PROD_STATUS_C5) {
		F_PROD_STATUS_C5 = f_PROD_STATUS_C5;
	}

	public BigDecimal getF_PURCHASE_AMT_C1() {
		return F_PURCHASE_AMT_C1;
	}

	public void setF_PURCHASE_AMT_C1(BigDecimal f_PURCHASE_AMT_C1) {
		F_PURCHASE_AMT_C1 = f_PURCHASE_AMT_C1;
	}

	public BigDecimal getF_PURCHASE_AMT_C2() {
		return F_PURCHASE_AMT_C2;
	}

	public void setF_PURCHASE_AMT_C2(BigDecimal f_PURCHASE_AMT_C2) {
		F_PURCHASE_AMT_C2 = f_PURCHASE_AMT_C2;
	}

	public BigDecimal getF_PURCHASE_AMT_C3() {
		return F_PURCHASE_AMT_C3;
	}

	public void setF_PURCHASE_AMT_C3(BigDecimal f_PURCHASE_AMT_C3) {
		F_PURCHASE_AMT_C3 = f_PURCHASE_AMT_C3;
	}

	public BigDecimal getF_PURCHASE_AMT_C4() {
		return F_PURCHASE_AMT_C4;
	}

	public void setF_PURCHASE_AMT_C4(BigDecimal f_PURCHASE_AMT_C4) {
		F_PURCHASE_AMT_C4 = f_PURCHASE_AMT_C4;
	}

	public BigDecimal getF_PURCHASE_AMT_C5() {
		return F_PURCHASE_AMT_C5;
	}

	public void setF_PURCHASE_AMT_C5(BigDecimal f_PURCHASE_AMT_C5) {
		F_PURCHASE_AMT_C5 = f_PURCHASE_AMT_C5;
	}

	public String getF_TRANSFER_DATE_1() {
		return F_TRANSFER_DATE_1;
	}

	public void setF_TRANSFER_DATE_1(String f_TRANSFER_DATE_1) {
		F_TRANSFER_DATE_1 = f_TRANSFER_DATE_1;
	}

	public String getF_TRANSFER_DATE_2() {
		return F_TRANSFER_DATE_2;
	}

	public void setF_TRANSFER_DATE_2(String f_TRANSFER_DATE_2) {
		F_TRANSFER_DATE_2 = f_TRANSFER_DATE_2;
	}

	public String getF_TRANSFER_DATE_3() {
		return F_TRANSFER_DATE_3;
	}

	public void setF_TRANSFER_DATE_3(String f_TRANSFER_DATE_3) {
		F_TRANSFER_DATE_3 = f_TRANSFER_DATE_3;
	}

	public String getF_TRANSFER_DATE_4() {
		return F_TRANSFER_DATE_4;
	}

	public void setF_TRANSFER_DATE_4(String f_TRANSFER_DATE_4) {
		F_TRANSFER_DATE_4 = f_TRANSFER_DATE_4;
	}

	public String getF_TRANSFER_DATE_5() {
		return F_TRANSFER_DATE_5;
	}

	public void setF_TRANSFER_DATE_5(String f_TRANSFER_DATE_5) {
		F_TRANSFER_DATE_5 = f_TRANSFER_DATE_5;
	}

	public String getF_TRANSFER_DATE_6() {
		return F_TRANSFER_DATE_6;
	}

	public void setF_TRANSFER_DATE_6(String f_TRANSFER_DATE_6) {
		F_TRANSFER_DATE_6 = f_TRANSFER_DATE_6;
	}

	public String getF_PROD_ID_C1() {
		return F_PROD_ID_C1;
	}

	public void setF_PROD_ID_C1(String f_PROD_ID_C1) {
		F_PROD_ID_C1 = f_PROD_ID_C1;
	}

	public String getF_PROD_NAME_C1() {
		return F_PROD_NAME_C1;
	}

	public void setF_PROD_NAME_C1(String f_PROD_NAME_C1) {
		F_PROD_NAME_C1 = f_PROD_NAME_C1;
	}

	public String getF_PROD_ID_C2() {
		return F_PROD_ID_C2;
	}

	public void setF_PROD_ID_C2(String f_PROD_ID_C2) {
		F_PROD_ID_C2 = f_PROD_ID_C2;
	}

	public String getF_PROD_NAME_C2() {
		return F_PROD_NAME_C2;
	}

	public void setF_PROD_NAME_C2(String f_PROD_NAME_C2) {
		F_PROD_NAME_C2 = f_PROD_NAME_C2;
	}

	public String getF_PROD_ID_C3() {
		return F_PROD_ID_C3;
	}

	public void setF_PROD_ID_C3(String f_PROD_ID_C3) {
		F_PROD_ID_C3 = f_PROD_ID_C3;
	}

	public String getF_PROD_NAME_C3() {
		return F_PROD_NAME_C3;
	}

	public void setF_PROD_NAME_C3(String f_PROD_NAME_C3) {
		F_PROD_NAME_C3 = f_PROD_NAME_C3;
	}

	public String getF_PROD_ID_C4() {
		return F_PROD_ID_C4;
	}

	public void setF_PROD_ID_C4(String f_PROD_ID_C4) {
		F_PROD_ID_C4 = f_PROD_ID_C4;
	}

	public String getF_PROD_NAME_C4() {
		return F_PROD_NAME_C4;
	}

	public void setF_PROD_NAME_C4(String f_PROD_NAME_C4) {
		F_PROD_NAME_C4 = f_PROD_NAME_C4;
	}

	public String getF_PROD_ID_C5() {
		return F_PROD_ID_C5;
	}

	public void setF_PROD_ID_C5(String f_PROD_ID_C5) {
		F_PROD_ID_C5 = f_PROD_ID_C5;
	}

	public String getF_PROD_NAME_C5() {
		return F_PROD_NAME_C5;
	}

	public void setF_PROD_NAME_C5(String f_PROD_NAME_C5) {
		F_PROD_NAME_C5 = f_PROD_NAME_C5;
	}

	public BigDecimal getF_ADDPROD_AMT_C1() {
		return F_ADDPROD_AMT_C1;
	}

	public void setF_ADDPROD_AMT_C1(BigDecimal f_ADDPROD_AMT_C1) {
		F_ADDPROD_AMT_C1 = f_ADDPROD_AMT_C1;
	}

	public BigDecimal getF_ADDPROD_AMT_C2() {
		return F_ADDPROD_AMT_C2;
	}

	public void setF_ADDPROD_AMT_C2(BigDecimal f_ADDPROD_AMT_C2) {
		F_ADDPROD_AMT_C2 = f_ADDPROD_AMT_C2;
	}

	public BigDecimal getF_ADDPROD_AMT_C3() {
		return F_ADDPROD_AMT_C3;
	}

	public void setF_ADDPROD_AMT_C3(BigDecimal f_ADDPROD_AMT_C3) {
		F_ADDPROD_AMT_C3 = f_ADDPROD_AMT_C3;
	}

	public BigDecimal getF_ADDPROD_AMT_C4() {
		return F_ADDPROD_AMT_C4;
	}

	public void setF_ADDPROD_AMT_C4(BigDecimal f_ADDPROD_AMT_C4) {
		F_ADDPROD_AMT_C4 = f_ADDPROD_AMT_C4;
	}

	public BigDecimal getF_ADDPROD_AMT_C5() {
		return F_ADDPROD_AMT_C5;
	}

	public void setF_ADDPROD_AMT_C5(BigDecimal f_ADDPROD_AMT_C5) {
		F_ADDPROD_AMT_C5 = f_ADDPROD_AMT_C5;
	}

	public String getF_PROD_RISK_LV_C1() {
		return F_PROD_RISK_LV_C1;
	}

	public void setF_PROD_RISK_LV_C1(String f_PROD_RISK_LV_C1) {
		F_PROD_RISK_LV_C1 = f_PROD_RISK_LV_C1;
	}

	public String getF_PROD_RISK_LV_C2() {
		return F_PROD_RISK_LV_C2;
	}

	public void setF_PROD_RISK_LV_C2(String f_PROD_RISK_LV_C2) {
		F_PROD_RISK_LV_C2 = f_PROD_RISK_LV_C2;
	}

	public String getF_PROD_RISK_LV_C3() {
		return F_PROD_RISK_LV_C3;
	}

	public void setF_PROD_RISK_LV_C3(String f_PROD_RISK_LV_C3) {
		F_PROD_RISK_LV_C3 = f_PROD_RISK_LV_C3;
	}

	public String getF_PROD_RISK_LV_C4() {
		return F_PROD_RISK_LV_C4;
	}

	public void setF_PROD_RISK_LV_C4(String f_PROD_RISK_LV_C4) {
		F_PROD_RISK_LV_C4 = f_PROD_RISK_LV_C4;
	}

	public String getF_PROD_RISK_LV_C5() {
		return F_PROD_RISK_LV_C5;
	}

	public void setF_PROD_RISK_LV_C5(String f_PROD_RISK_LV_C5) {
		F_PROD_RISK_LV_C5 = f_PROD_RISK_LV_C5;
	}

	public BigDecimal getF_PROD_MIN_BUY_AMT_C1() {
		return F_PROD_MIN_BUY_AMT_C1;
	}

	public void setF_PROD_MIN_BUY_AMT_C1(BigDecimal f_PROD_MIN_BUY_AMT_C1) {
		F_PROD_MIN_BUY_AMT_C1 = f_PROD_MIN_BUY_AMT_C1;
	}

	public BigDecimal getF_PROD_MIN_BUY_AMT_C2() {
		return F_PROD_MIN_BUY_AMT_C2;
	}

	public void setF_PROD_MIN_BUY_AMT_C2(BigDecimal f_PROD_MIN_BUY_AMT_C2) {
		F_PROD_MIN_BUY_AMT_C2 = f_PROD_MIN_BUY_AMT_C2;
	}

	public BigDecimal getF_PROD_MIN_BUY_AMT_C3() {
		return F_PROD_MIN_BUY_AMT_C3;
	}

	public void setF_PROD_MIN_BUY_AMT_C3(BigDecimal f_PROD_MIN_BUY_AMT_C3) {
		F_PROD_MIN_BUY_AMT_C3 = f_PROD_MIN_BUY_AMT_C3;
	}

	public BigDecimal getF_PROD_MIN_BUY_AMT_C4() {
		return F_PROD_MIN_BUY_AMT_C4;
	}

	public void setF_PROD_MIN_BUY_AMT_C4(BigDecimal f_PROD_MIN_BUY_AMT_C4) {
		F_PROD_MIN_BUY_AMT_C4 = f_PROD_MIN_BUY_AMT_C4;
	}

	public BigDecimal getF_PROD_MIN_BUY_AMT_C5() {
		return F_PROD_MIN_BUY_AMT_C5;
	}

	public void setF_PROD_MIN_BUY_AMT_C5(BigDecimal f_PROD_MIN_BUY_AMT_C5) {
		F_PROD_MIN_BUY_AMT_C5 = f_PROD_MIN_BUY_AMT_C5;
	}

	public BigDecimal getF_PROD_MIN_GRD_AMT_C1() {
		return F_PROD_MIN_GRD_AMT_C1;
	}

	public void setF_PROD_MIN_GRD_AMT_C1(BigDecimal f_PROD_MIN_GRD_AMT_C1) {
		F_PROD_MIN_GRD_AMT_C1 = f_PROD_MIN_GRD_AMT_C1;
	}

	public BigDecimal getF_PROD_MIN_GRD_AMT_C2() {
		return F_PROD_MIN_GRD_AMT_C2;
	}

	public void setF_PROD_MIN_GRD_AMT_C2(BigDecimal f_PROD_MIN_GRD_AMT_C2) {
		F_PROD_MIN_GRD_AMT_C2 = f_PROD_MIN_GRD_AMT_C2;
	}

	public BigDecimal getF_PROD_MIN_GRD_AMT_C3() {
		return F_PROD_MIN_GRD_AMT_C3;
	}

	public void setF_PROD_MIN_GRD_AMT_C3(BigDecimal f_PROD_MIN_GRD_AMT_C3) {
		F_PROD_MIN_GRD_AMT_C3 = f_PROD_MIN_GRD_AMT_C3;
	}

	public BigDecimal getF_PROD_MIN_GRD_AMT_C4() {
		return F_PROD_MIN_GRD_AMT_C4;
	}

	public void setF_PROD_MIN_GRD_AMT_C4(BigDecimal f_PROD_MIN_GRD_AMT_C4) {
		F_PROD_MIN_GRD_AMT_C4 = f_PROD_MIN_GRD_AMT_C4;
	}

	public BigDecimal getF_PROD_MIN_GRD_AMT_C5() {
		return F_PROD_MIN_GRD_AMT_C5;
	}

	public void setF_PROD_MIN_GRD_AMT_C5(BigDecimal f_PROD_MIN_GRD_AMT_C5) {
		F_PROD_MIN_GRD_AMT_C5 = f_PROD_MIN_GRD_AMT_C5;
	}

	public String getBATCH_SEQ_STATUS() {
		return BATCH_SEQ_STATUS;
	}

	public void setBATCH_SEQ_STATUS(String bATCH_SEQ_STATUS) {
		BATCH_SEQ_STATUS = bATCH_SEQ_STATUS;
	}

	public String getBATCH_SEQ_AMOUNT() {
		return BATCH_SEQ_AMOUNT;
	}

	public void setBATCH_SEQ_AMOUNT(String bATCH_SEQ_AMOUNT) {
		BATCH_SEQ_AMOUNT = bATCH_SEQ_AMOUNT;
	}

	public String getBATCH_SEQ_TRANSDATE() {
		return BATCH_SEQ_TRANSDATE;
	}

	public void setBATCH_SEQ_TRANSDATE(String bATCH_SEQ_TRANSDATE) {
		BATCH_SEQ_TRANSDATE = bATCH_SEQ_TRANSDATE;
	}

	public String getBATCH_SEQ_ADDPROD() {
		return BATCH_SEQ_ADDPROD;
	}

	public void setBATCH_SEQ_ADDPROD(String bATCH_SEQ_ADDPROD) {
		BATCH_SEQ_ADDPROD = bATCH_SEQ_ADDPROD;
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
		if (!(other instanceof TBSOT_NF_CHANGE_DYNAVO))
			return false;
		TBSOT_NF_CHANGE_DYNAVO castOther = (TBSOT_NF_CHANGE_DYNAVO) other;
		return new EqualsBuilder().append(this.getcomp_id(), castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}
}
