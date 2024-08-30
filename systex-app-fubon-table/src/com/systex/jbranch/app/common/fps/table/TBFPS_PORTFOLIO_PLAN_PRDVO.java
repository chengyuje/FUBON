package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBFPS_PORTFOLIO_PLAN_PRDVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_PRDPK comp_id;

	/** persistent field */
	private String PRD_NAME;
	private String RISKCATE_ID;
	private String PROD_CURR;
	private String TRUST_CURR;
	private BigDecimal PURCHASE_ORG_AMT;
	private BigDecimal PURCHASE_TWD_AMT;
	private BigDecimal PURCHASE_ORG_AMT_AFTER;
	private BigDecimal PURCHASE_TWD_AMT_AFTER;
	private BigDecimal PRD_RATIO;
	private BigDecimal LIMIT_ORG_AMT;
	private BigDecimal LIMIT_ORG_AMT_R;
	private String PROD_TAG;
	private String TRANSCATION_TYPE;
	private String PORTFOLIO_TYPE;
	private BigDecimal PORTFOLIO_RATIO;
	private BigDecimal PORTFOLIO_AMT;
	private BigDecimal AVAILABLE_AMT;
	private BigDecimal PLANNED_AMT;





	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_PRD";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBFPS_PORTFOLIO_PLAN_PRDVO(
			com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_PRDPK comp_id,
			String PRD_NAME, String RISKCATE_ID, String PROD_CURR, String TRUST_CURR, BigDecimal PURCHASE_ORG_AMT, BigDecimal PURCHASE_TWD_AMT, BigDecimal PURCHASE_ORG_AMT_AFTER, BigDecimal PURCHASE_TWD_AMT_AFTER, BigDecimal PRD_RATIO, BigDecimal LIMIT_ORG_AMT, BigDecimal LIMIT_ORG_AMT_R, String TRANSCATION_TYPE, String PROD_TAG, String PORTFOLIO_TYPE, BigDecimal PORTFOLIO_RATIO, BigDecimal PORTFOLIO_AMT, BigDecimal AVAILABLE_AMT, BigDecimal PLANNED_AMT, Timestamp createtime,
			String creator, String modifier, Timestamp lastupdate, Long version) {
		this.comp_id = comp_id;
		this.PRD_NAME               = PRD_NAME;
		this.RISKCATE_ID            = RISKCATE_ID;
		this.PROD_CURR              = PROD_CURR;
		this.TRUST_CURR             = TRUST_CURR;
		this.PURCHASE_ORG_AMT       = PURCHASE_ORG_AMT;
		this.PURCHASE_TWD_AMT       = PURCHASE_TWD_AMT;
		this.PURCHASE_ORG_AMT_AFTER = PURCHASE_ORG_AMT_AFTER;
		this.PURCHASE_TWD_AMT_AFTER = PURCHASE_TWD_AMT_AFTER;
		this.PRD_RATIO              = PRD_RATIO;
		this.LIMIT_ORG_AMT          = LIMIT_ORG_AMT;
		this.LIMIT_ORG_AMT_R        = LIMIT_ORG_AMT_R;
		this.PROD_TAG               = PROD_TAG;
		this.TRANSCATION_TYPE       = TRANSCATION_TYPE;
		this.PORTFOLIO_TYPE         = PORTFOLIO_TYPE;
		this.PORTFOLIO_RATIO        = PORTFOLIO_RATIO;
		this.PORTFOLIO_AMT          = PORTFOLIO_AMT;
		this.AVAILABLE_AMT          = AVAILABLE_AMT;
		this.PLANNED_AMT            = PLANNED_AMT;
		this.createtime = createtime;
		this.creator = creator;
		this.modifier = modifier;
		this.lastupdate = lastupdate;
		this.version = version;
	}

	/** default constructor */
	public TBFPS_PORTFOLIO_PLAN_PRDVO() {
	}

	/** minimal constructor */
	public TBFPS_PORTFOLIO_PLAN_PRDVO(
			com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_PRDPK comp_id,
			String PRD_NAME, String RISKCATE_ID, String PROD_CURR, String TRUST_CURR, BigDecimal PURCHASE_ORG_AMT, BigDecimal PURCHASE_TWD_AMT, BigDecimal PURCHASE_ORG_AMT_AFTER, BigDecimal PURCHASE_TWD_AMT_AFTER, BigDecimal PRD_RATIO, BigDecimal LIMIT_ORG_AMT, BigDecimal LIMIT_ORG_AMT_R, String PROD_TAG, String TRANSCATION_TYPE, String PORTFOLIO_TYPE, BigDecimal PORTFOLIO_RATIO, BigDecimal PORTFOLIO_AMT, BigDecimal AVAILABLE_AMT, BigDecimal PLANNED_AMT) {
		this.comp_id = comp_id;
		this.PRD_NAME               = PRD_NAME;
		this.RISKCATE_ID            = RISKCATE_ID;
		this.PROD_CURR              = PROD_CURR;
		this.TRUST_CURR             = TRUST_CURR;
		this.PURCHASE_ORG_AMT       = PURCHASE_ORG_AMT;
		this.PURCHASE_TWD_AMT       = PURCHASE_TWD_AMT;
		this.PURCHASE_ORG_AMT_AFTER = PURCHASE_ORG_AMT_AFTER;
		this.PURCHASE_TWD_AMT_AFTER = PURCHASE_TWD_AMT_AFTER;
		this.PRD_RATIO              = PRD_RATIO;
		this.LIMIT_ORG_AMT          = LIMIT_ORG_AMT;
		this.LIMIT_ORG_AMT_R        = LIMIT_ORG_AMT_R;
		this.PROD_TAG               = PROD_TAG;
		this.TRANSCATION_TYPE       = TRANSCATION_TYPE;
		this.PORTFOLIO_TYPE         = PORTFOLIO_TYPE;
		this.PORTFOLIO_RATIO        = PORTFOLIO_RATIO;
		this.PORTFOLIO_AMT          = PORTFOLIO_AMT;
		this.AVAILABLE_AMT          = AVAILABLE_AMT;
		this.PLANNED_AMT            = PLANNED_AMT;
	}

	public com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_PRDPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(
			com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_PRDPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_PRDPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(
			com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_PRDPK comp_id) {
		this.comp_id = comp_id;
	}


	public String getPRD_NAME() {
		return PRD_NAME;
	}

	public void setPRD_NAME(String pRD_NAME) {
		PRD_NAME = pRD_NAME;
	}

	public String getRISKCATE_ID() {
		return RISKCATE_ID;
	}

	public void setRISKCATE_ID(String rISKCATE_ID) {
		RISKCATE_ID = rISKCATE_ID;
	}

	public String getPROD_CURR() {
		return PROD_CURR;
	}

	public void setPROD_CURR(String pROD_CURR) {
		PROD_CURR = pROD_CURR;
	}

	public String getTRUST_CURR() {
		return TRUST_CURR;
	}

	public void setTRUST_CURR(String tRUST_CURR) {
		TRUST_CURR = tRUST_CURR;
	}

	public BigDecimal getPURCHASE_ORG_AMT() {
		return PURCHASE_ORG_AMT;
	}

	public void setPURCHASE_ORG_AMT(BigDecimal pURCHASE_ORG_AMT) {
		PURCHASE_ORG_AMT = pURCHASE_ORG_AMT;
	}

	public BigDecimal getPURCHASE_TWD_AMT() {
		return PURCHASE_TWD_AMT;
	}

	public void setPURCHASE_TWD_AMT(BigDecimal pURCHASE_TWD_AMT) {
		PURCHASE_TWD_AMT = pURCHASE_TWD_AMT;
	}

	public BigDecimal getPURCHASE_ORG_AMT_AFTER() {
		return PURCHASE_ORG_AMT_AFTER;
	}

	public void setPURCHASE_ORG_AMT_AFTER(BigDecimal pURCHASE_ORG_AMT_AFTER) {
		PURCHASE_ORG_AMT_AFTER = pURCHASE_ORG_AMT_AFTER;
	}

	public BigDecimal getPURCHASE_TWD_AMT_AFTER() {
		return PURCHASE_TWD_AMT_AFTER;
	}

	public void setPURCHASE_TWD_AMT_AFTER(BigDecimal pURCHASE_TWD_AMT_AFTER) {
		PURCHASE_TWD_AMT_AFTER = pURCHASE_TWD_AMT_AFTER;
	}

	public BigDecimal getPRD_RATIO() {
		return PRD_RATIO;
	}

	public void setPRD_RATIO(BigDecimal pRD_RATIO) {
		PRD_RATIO = pRD_RATIO;
	}

	public BigDecimal getLIMIT_ORG_AMT() {
		return LIMIT_ORG_AMT;
	}

	public void setLIMIT_ORG_AMT(BigDecimal lIMIT_ORG_AMT) {
		LIMIT_ORG_AMT = lIMIT_ORG_AMT;
	}

	public BigDecimal getLIMIT_ORG_AMT_R() {
		return LIMIT_ORG_AMT_R;
	}

	public void setLIMIT_ORG_AMT_R(BigDecimal lIMIT_ORG_AMT_R) {
		LIMIT_ORG_AMT_R = lIMIT_ORG_AMT_R;
	}

	public String getPROD_TAG() {
		return PROD_TAG;
	}

	public void setPROD_TAG(String pROD_TAG) {
		PROD_TAG = pROD_TAG;
	}

	public String getTRANSCATION_TYPE() {
		return TRANSCATION_TYPE;
	}

	public void setTRANSCATION_TYPE(String tRANSCATION_TYPE) {
		TRANSCATION_TYPE = tRANSCATION_TYPE;
	}

	public String getPORTFOLIO_TYPE() {
		return PORTFOLIO_TYPE;
	}

	public void setPORTFOLIO_TYPE(String pORTFOLIO_TYPE) {
		PORTFOLIO_TYPE = pORTFOLIO_TYPE;
	}

	public BigDecimal getPORTFOLIO_RATIO() {
		return PORTFOLIO_RATIO;
	}

	public void setPORTFOLIO_RATIO(BigDecimal pORTFOLIO_RATIO) {
		PORTFOLIO_RATIO = pORTFOLIO_RATIO;
	}

	public BigDecimal getPORTFOLIO_AMT() {
		return PORTFOLIO_AMT;
	}

	public void setPORTFOLIO_AMT(BigDecimal pORTFOLIO_AMT) {
		PORTFOLIO_AMT = pORTFOLIO_AMT;
	}

	public BigDecimal getAVAILABLE_AMT() {
		return AVAILABLE_AMT;
	}

	public void setAVAILABLE_AMT(BigDecimal aVAILABLE_AMT) {
		AVAILABLE_AMT = aVAILABLE_AMT;
	}

	public BigDecimal getPLANNED_AMT() {
		return PLANNED_AMT;
	}

	public void setPLANNED_AMT(BigDecimal pLANNED_AMT) {
		PLANNED_AMT = pLANNED_AMT;
	}

	public static String getTableUid() {
		return TABLE_UID;
	}

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getcomp_id())
				.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof TBFPS_PORTFOLIO_PLAN_PRDVO))
			return false;
		TBFPS_PORTFOLIO_PLAN_PRDVO castOther = (TBFPS_PORTFOLIO_PLAN_PRDVO) other;
		return new EqualsBuilder().append(this.getcomp_id(),
				castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}

}
