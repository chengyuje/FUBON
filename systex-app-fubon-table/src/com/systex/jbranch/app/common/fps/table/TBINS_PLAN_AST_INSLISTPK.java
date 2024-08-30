package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PLAN_AST_INSLISTPK  implements Serializable  {

    /** identifier field */
    private BigDecimal OUTBUY_DTL_KEYNO;

    /** identifier field */
    private BigDecimal PLAN_D_KEYNO;

    /** full constructor */
    public TBINS_PLAN_AST_INSLISTPK(BigDecimal OUTBUY_DTL_KEYNO, BigDecimal PLAN_D_KEYNO) {
        this.OUTBUY_DTL_KEYNO = OUTBUY_DTL_KEYNO;
        this.PLAN_D_KEYNO = PLAN_D_KEYNO;
    }

    /** default constructor */
    public TBINS_PLAN_AST_INSLISTPK() {
    }

    public BigDecimal getOUTBUY_DTL_KEYNO() {
        return this.OUTBUY_DTL_KEYNO;
    }

    public void setOUTBUY_DTL_KEYNO(BigDecimal OUTBUY_DTL_KEYNO) {
        this.OUTBUY_DTL_KEYNO = OUTBUY_DTL_KEYNO;
    }

    public BigDecimal getPLAN_D_KEYNO() {
        return this.PLAN_D_KEYNO;
    }

    public void setPLAN_D_KEYNO(BigDecimal PLAN_D_KEYNO) {
        this.PLAN_D_KEYNO = PLAN_D_KEYNO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("OUTBUY_DTL_KEYNO", getOUTBUY_DTL_KEYNO())
            .append("PLAN_D_KEYNO", getPLAN_D_KEYNO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBINS_PLAN_AST_INSLISTPK) ) return false;
        TBINS_PLAN_AST_INSLISTPK castOther = (TBINS_PLAN_AST_INSLISTPK) other;
        return new EqualsBuilder()
            .append(this.getOUTBUY_DTL_KEYNO(), castOther.getOUTBUY_DTL_KEYNO())
            .append(this.getPLAN_D_KEYNO(), castOther.getPLAN_D_KEYNO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getOUTBUY_DTL_KEYNO())
            .append(getPLAN_D_KEYNO())
            .toHashCode();
    }

}
