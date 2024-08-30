package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_PORTFOLIO_PLAN_SPPVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPPK comp_id;

    /** persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String PTYPE;

    /** nullable persistent field */
    private String RISK_TYPE;

    /** nullable persistent field */
    private String CURRENCY_TYPE;

    /** nullable persistent field */
    private String TRUST_CURR;

    /** nullable persistent field */
    private String MARKET_CIS;

    /** nullable persistent field */
    private BigDecimal PURCHASE_ORG_AMT;

    /** nullable persistent field */
    private BigDecimal PURCHASE_TWD_AMT;

    /** nullable persistent field */
    private BigDecimal PORTFOLIO_RATIO;

    /** nullable persistent field */
    private BigDecimal LIMIT_ORG_AMT;

    /** nullable persistent field */
    private String PORTFOLIO_TYPE;

    /** nullable persistent field */
    private String FND_CODE;

    /** nullable persistent field */
    private String INV_TYPE;

    /** nullable persistent field */
    private String TXN_TYPE;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private BigDecimal EX_RATE;

    /** nullable persistent field */
    private BigDecimal PURCHASE_ORG_AMT_ORDER;

    /** nullable persistent field */
    private BigDecimal PURCHASE_TWD_AMT_ORDER;

    /** nullable persistent field */
    private BigDecimal FEE_ORG_AMT_ORDER;

    /** nullable persistent field */
    private BigDecimal FEE_TWD_AMT_ORDER;

    /** nullable persistent field */
    private BigDecimal RDM_ORG_AMT_ORDER;

    /** nullable persistent field */
    private BigDecimal RDM_TWD_AMT_ORDER;

    /** nullable persistent field */
    private String ORDER_STATUS;

    /** nullable persistent field */
    private String PRD_SOURCE_FLAG;

    /** nullable persistent field */
    private String POLICY_NO;

    /** nullable persistent field */
    private BigDecimal POLICY_SEQ;

    /** nullable persistent field */
    private String ID_DUP;

    /** nullable persistent field */
    private String TARGETS;

    /** nullable persistent field */
    private String CERTIFICATE_ID;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_PORTFOLIO_PLAN_SPPVO(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPPK comp_id, String PRD_ID, String PTYPE, String RISK_TYPE, String CURRENCY_TYPE, String TRUST_CURR, String MARKET_CIS, BigDecimal PURCHASE_ORG_AMT, BigDecimal PURCHASE_TWD_AMT, BigDecimal PORTFOLIO_RATIO, BigDecimal LIMIT_ORG_AMT, String PORTFOLIO_TYPE, String FND_CODE, String INV_TYPE, String TXN_TYPE, String STATUS, BigDecimal EX_RATE, BigDecimal PURCHASE_ORG_AMT_ORDER, BigDecimal PURCHASE_TWD_AMT_ORDER, BigDecimal FEE_ORG_AMT_ORDER, BigDecimal FEE_TWD_AMT_ORDER, BigDecimal RDM_ORG_AMT_ORDER, BigDecimal RDM_TWD_AMT_ORDER, String ORDER_STATUS, String PRD_SOURCE_FLAG, String POLICY_NO, BigDecimal POLICY_SEQ, String ID_DUP, String TARGETS, String CERTIFICATE_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PRD_ID = PRD_ID;
        this.PTYPE = PTYPE;
        this.RISK_TYPE = RISK_TYPE;
        this.CURRENCY_TYPE = CURRENCY_TYPE;
        this.TRUST_CURR = TRUST_CURR;
        this.MARKET_CIS = MARKET_CIS;
        this.PURCHASE_ORG_AMT = PURCHASE_ORG_AMT;
        this.PURCHASE_TWD_AMT = PURCHASE_TWD_AMT;
        this.PORTFOLIO_RATIO = PORTFOLIO_RATIO;
        this.LIMIT_ORG_AMT = LIMIT_ORG_AMT;
        this.PORTFOLIO_TYPE = PORTFOLIO_TYPE;
        this.FND_CODE = FND_CODE;
        this.INV_TYPE = INV_TYPE;
        this.TXN_TYPE = TXN_TYPE;
        this.STATUS = STATUS;
        this.EX_RATE = EX_RATE;
        this.PURCHASE_ORG_AMT_ORDER = PURCHASE_ORG_AMT_ORDER;
        this.PURCHASE_TWD_AMT_ORDER = PURCHASE_TWD_AMT_ORDER;
        this.FEE_ORG_AMT_ORDER = FEE_ORG_AMT_ORDER;
        this.FEE_TWD_AMT_ORDER = FEE_TWD_AMT_ORDER;
        this.RDM_ORG_AMT_ORDER = RDM_ORG_AMT_ORDER;
        this.RDM_TWD_AMT_ORDER = RDM_TWD_AMT_ORDER;
        this.ORDER_STATUS = ORDER_STATUS;
        this.PRD_SOURCE_FLAG = PRD_SOURCE_FLAG;
        this.POLICY_NO = POLICY_NO;
        this.POLICY_SEQ = POLICY_SEQ;
        this.ID_DUP = ID_DUP;
        this.TARGETS = TARGETS;
        this.CERTIFICATE_ID = CERTIFICATE_ID;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_PORTFOLIO_PLAN_SPPVO() {
    }

    /** minimal constructor */
    public TBFPS_PORTFOLIO_PLAN_SPPVO(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPPK comp_id, String PRD_ID) {
        this.comp_id = comp_id;
        this.PRD_ID = PRD_ID;
    }

    public com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_SPPPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getPTYPE() {
        return this.PTYPE;
    }

    public void setPTYPE(String PTYPE) {
        this.PTYPE = PTYPE;
    }

    public String getRISK_TYPE() {
        return this.RISK_TYPE;
    }

    public void setRISK_TYPE(String RISK_TYPE) {
        this.RISK_TYPE = RISK_TYPE;
    }

    public String getCURRENCY_TYPE() {
        return this.CURRENCY_TYPE;
    }

    public void setCURRENCY_TYPE(String CURRENCY_TYPE) {
        this.CURRENCY_TYPE = CURRENCY_TYPE;
    }

    public String getTRUST_CURR() {
        return this.TRUST_CURR;
    }

    public void setTRUST_CURR(String TRUST_CURR) {
        this.TRUST_CURR = TRUST_CURR;
    }

    public String getMARKET_CIS() {
        return this.MARKET_CIS;
    }

    public void setMARKET_CIS(String MARKET_CIS) {
        this.MARKET_CIS = MARKET_CIS;
    }

    public BigDecimal getPURCHASE_ORG_AMT() {
        return this.PURCHASE_ORG_AMT;
    }

    public void setPURCHASE_ORG_AMT(BigDecimal PURCHASE_ORG_AMT) {
        this.PURCHASE_ORG_AMT = PURCHASE_ORG_AMT;
    }

    public BigDecimal getPURCHASE_TWD_AMT() {
        return this.PURCHASE_TWD_AMT;
    }

    public void setPURCHASE_TWD_AMT(BigDecimal PURCHASE_TWD_AMT) {
        this.PURCHASE_TWD_AMT = PURCHASE_TWD_AMT;
    }

    public BigDecimal getPORTFOLIO_RATIO() {
        return this.PORTFOLIO_RATIO;
    }

    public void setPORTFOLIO_RATIO(BigDecimal PORTFOLIO_RATIO) {
        this.PORTFOLIO_RATIO = PORTFOLIO_RATIO;
    }

    public BigDecimal getLIMIT_ORG_AMT() {
        return this.LIMIT_ORG_AMT;
    }

    public void setLIMIT_ORG_AMT(BigDecimal LIMIT_ORG_AMT) {
        this.LIMIT_ORG_AMT = LIMIT_ORG_AMT;
    }

    public String getPORTFOLIO_TYPE() {
        return this.PORTFOLIO_TYPE;
    }

    public void setPORTFOLIO_TYPE(String PORTFOLIO_TYPE) {
        this.PORTFOLIO_TYPE = PORTFOLIO_TYPE;
    }

    public String getFND_CODE() {
        return this.FND_CODE;
    }

    public void setFND_CODE(String FND_CODE) {
        this.FND_CODE = FND_CODE;
    }

    public String getINV_TYPE() {
        return this.INV_TYPE;
    }

    public void setINV_TYPE(String INV_TYPE) {
        this.INV_TYPE = INV_TYPE;
    }

    public String getTXN_TYPE() {
        return this.TXN_TYPE;
    }

    public void setTXN_TYPE(String TXN_TYPE) {
        this.TXN_TYPE = TXN_TYPE;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public BigDecimal getEX_RATE() {
        return this.EX_RATE;
    }

    public void setEX_RATE(BigDecimal EX_RATE) {
        this.EX_RATE = EX_RATE;
    }

    public BigDecimal getPURCHASE_ORG_AMT_ORDER() {
        return this.PURCHASE_ORG_AMT_ORDER;
    }

    public void setPURCHASE_ORG_AMT_ORDER(BigDecimal PURCHASE_ORG_AMT_ORDER) {
        this.PURCHASE_ORG_AMT_ORDER = PURCHASE_ORG_AMT_ORDER;
    }

    public BigDecimal getPURCHASE_TWD_AMT_ORDER() {
        return this.PURCHASE_TWD_AMT_ORDER;
    }

    public void setPURCHASE_TWD_AMT_ORDER(BigDecimal PURCHASE_TWD_AMT_ORDER) {
        this.PURCHASE_TWD_AMT_ORDER = PURCHASE_TWD_AMT_ORDER;
    }

    public BigDecimal getFEE_ORG_AMT_ORDER() {
        return this.FEE_ORG_AMT_ORDER;
    }

    public void setFEE_ORG_AMT_ORDER(BigDecimal FEE_ORG_AMT_ORDER) {
        this.FEE_ORG_AMT_ORDER = FEE_ORG_AMT_ORDER;
    }

    public BigDecimal getFEE_TWD_AMT_ORDER() {
        return this.FEE_TWD_AMT_ORDER;
    }

    public void setFEE_TWD_AMT_ORDER(BigDecimal FEE_TWD_AMT_ORDER) {
        this.FEE_TWD_AMT_ORDER = FEE_TWD_AMT_ORDER;
    }

    public BigDecimal getRDM_ORG_AMT_ORDER() {
        return this.RDM_ORG_AMT_ORDER;
    }

    public void setRDM_ORG_AMT_ORDER(BigDecimal RDM_ORG_AMT_ORDER) {
        this.RDM_ORG_AMT_ORDER = RDM_ORG_AMT_ORDER;
    }

    public BigDecimal getRDM_TWD_AMT_ORDER() {
        return this.RDM_TWD_AMT_ORDER;
    }

    public void setRDM_TWD_AMT_ORDER(BigDecimal RDM_TWD_AMT_ORDER) {
        this.RDM_TWD_AMT_ORDER = RDM_TWD_AMT_ORDER;
    }

    public String getORDER_STATUS() {
        return this.ORDER_STATUS;
    }

    public void setORDER_STATUS(String ORDER_STATUS) {
        this.ORDER_STATUS = ORDER_STATUS;
    }

    public String getPRD_SOURCE_FLAG() {
        return this.PRD_SOURCE_FLAG;
    }

    public void setPRD_SOURCE_FLAG(String PRD_SOURCE_FLAG) {
        this.PRD_SOURCE_FLAG = PRD_SOURCE_FLAG;
    }

    public String getPOLICY_NO() {
        return this.POLICY_NO;
    }

    public void setPOLICY_NO(String POLICY_NO) {
        this.POLICY_NO = POLICY_NO;
    }

    public BigDecimal getPOLICY_SEQ() {
        return this.POLICY_SEQ;
    }

    public void setPOLICY_SEQ(BigDecimal POLICY_SEQ) {
        this.POLICY_SEQ = POLICY_SEQ;
    }

    public String getID_DUP() {
        return this.ID_DUP;
    }

    public void setID_DUP(String ID_DUP) {
        this.ID_DUP = ID_DUP;
    }

    public String getTARGETS() {
        return this.TARGETS;
    }

    public void setTARGETS(String TARGETS) {
        this.TARGETS = TARGETS;
    }

    public String getCERTIFICATE_ID() {
        return this.CERTIFICATE_ID;
    }

    public void setCERTIFICATE_ID(String CERTIFICATE_ID) {
        this.CERTIFICATE_ID = CERTIFICATE_ID;
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
        if ( !(other instanceof TBFPS_PORTFOLIO_PLAN_SPPVO) ) return false;
        TBFPS_PORTFOLIO_PLAN_SPPVO castOther = (TBFPS_PORTFOLIO_PLAN_SPPVO) other;
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
