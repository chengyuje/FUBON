package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PLAN_SUG_INSLISTPK  implements Serializable  {

    /** identifier field */
    private BigDecimal INSPRD_KEYNO;

    /** identifier field */
    private BigDecimal PLAN_D_KEYNO;

    /** full constructor */
    public TBINS_PLAN_SUG_INSLISTPK(BigDecimal INSPRD_KEYNO, BigDecimal PLAN_D_KEYNO) {
        this.INSPRD_KEYNO = INSPRD_KEYNO;
        this.PLAN_D_KEYNO = PLAN_D_KEYNO;
    }

    /** default constructor */
    public TBINS_PLAN_SUG_INSLISTPK() {
    }

    public BigDecimal getINSPRD_KEYNO() {
        return this.INSPRD_KEYNO;
    }

    public void setINSPRD_KEYNO(BigDecimal INSPRD_KEYNO) {
        this.INSPRD_KEYNO = INSPRD_KEYNO;
    }

    public BigDecimal getPLAN_D_KEYNO() {
        return this.PLAN_D_KEYNO;
    }

    public void setPLAN_D_KEYNO(BigDecimal PLAN_D_KEYNO) {
        this.PLAN_D_KEYNO = PLAN_D_KEYNO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INSPRD_KEYNO", getINSPRD_KEYNO())
            .append("PLAN_D_KEYNO", getPLAN_D_KEYNO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBINS_PLAN_SUG_INSLISTPK) ) return false;
        TBINS_PLAN_SUG_INSLISTPK castOther = (TBINS_PLAN_SUG_INSLISTPK) other;
        return new EqualsBuilder()
            .append(this.getINSPRD_KEYNO(), castOther.getINSPRD_KEYNO())
            .append(this.getPLAN_D_KEYNO(), castOther.getPLAN_D_KEYNO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getINSPRD_KEYNO())
            .append(getPLAN_D_KEYNO())
            .toHashCode();
    }

}
