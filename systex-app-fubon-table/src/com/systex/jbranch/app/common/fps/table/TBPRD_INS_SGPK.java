package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_SGPK  implements Serializable  {

    /** identifier field */
    private BigDecimal INSPRD_ANNUAL;

    /** identifier field */
    private String INSPRD_NAME;

    /** identifier field */
    private String PRD_ID;

    /** full constructor */
    public TBPRD_INS_SGPK(BigDecimal INSPRD_ANNUAL, String INSPRD_NAME, String PRD_ID) {
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
        this.INSPRD_NAME = INSPRD_NAME;
        this.PRD_ID = PRD_ID;
    }

    /** default constructor */
    public TBPRD_INS_SGPK() {
    }

    public BigDecimal getINSPRD_ANNUAL() {
        return this.INSPRD_ANNUAL;
    }

    public void setINSPRD_ANNUAL(BigDecimal INSPRD_ANNUAL) {
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
    }

    public String getINSPRD_NAME() {
        return this.INSPRD_NAME;
    }

    public void setINSPRD_NAME(String INSPRD_NAME) {
        this.INSPRD_NAME = INSPRD_NAME;
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
            .append("INSPRD_NAME", getINSPRD_NAME())
            .append("PRD_ID", getPRD_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_INS_SGPK) ) return false;
        TBPRD_INS_SGPK castOther = (TBPRD_INS_SGPK) other;
        return new EqualsBuilder()
            .append(this.getINSPRD_ANNUAL(), castOther.getINSPRD_ANNUAL())
            .append(this.getINSPRD_NAME(), castOther.getINSPRD_NAME())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getINSPRD_ANNUAL())
            .append(getINSPRD_NAME())
            .append(getPRD_ID())
            .toHashCode();
    }

}
