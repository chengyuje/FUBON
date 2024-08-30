package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_PAYMENT_TYPE_SGPK  implements Serializable  {

    /** identifier field */
    private BigDecimal INSPRD_ANNUAL;

    /** identifier field */
    private String PAY_TYPE;

    /** identifier field */
    private String PRD_ID;

    /** full constructor */
    public TBPRD_INS_PAYMENT_TYPE_SGPK(BigDecimal INSPRD_ANNUAL, String PAY_TYPE, String PRD_ID) {
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
        this.PAY_TYPE = PAY_TYPE;
        this.PRD_ID = PRD_ID;
    }

    /** default constructor */
    public TBPRD_INS_PAYMENT_TYPE_SGPK() {
    }

    public BigDecimal getINSPRD_ANNUAL() {
        return this.INSPRD_ANNUAL;
    }

    public void setINSPRD_ANNUAL(BigDecimal INSPRD_ANNUAL) {
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
    }

    public String getPAY_TYPE() {
        return this.PAY_TYPE;
    }

    public void setPAY_TYPE(String PAY_TYPE) {
        this.PAY_TYPE = PAY_TYPE;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INSPRD_ANNUAL", getINSPRD_ANNUAL())
            .append("PAY_TYPE", getPAY_TYPE())
            .append("PRD_ID", getPRD_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_INS_PAYMENT_TYPE_SGPK) ) return false;
        TBPRD_INS_PAYMENT_TYPE_SGPK castOther = (TBPRD_INS_PAYMENT_TYPE_SGPK) other;
        return new EqualsBuilder()
            .append(this.getINSPRD_ANNUAL(), castOther.getINSPRD_ANNUAL())
            .append(this.getPAY_TYPE(), castOther.getPAY_TYPE())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getINSPRD_ANNUAL())
            .append(getPAY_TYPE())
            .append(getPRD_ID())
            .toHashCode();
    }

}
