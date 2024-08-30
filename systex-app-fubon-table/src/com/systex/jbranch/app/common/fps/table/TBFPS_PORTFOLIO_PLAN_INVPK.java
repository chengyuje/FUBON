package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_PORTFOLIO_PLAN_INVPK  implements Serializable  {

    /** identifier field */
    private String PLAN_ID;

    /** identifier field */
    private String PRD_ID;

    /** identifier field */
    private BigDecimal SEQNO;

    /** full constructor */
    public TBFPS_PORTFOLIO_PLAN_INVPK(String PLAN_ID, String PRD_ID, BigDecimal SEQNO) {
        this.PLAN_ID = PLAN_ID;
        this.PRD_ID = PRD_ID;
        this.SEQNO = SEQNO;
    }

    /** default constructor */
    public TBFPS_PORTFOLIO_PLAN_INVPK() {
    }

    public String getPLAN_ID() {
        return this.PLAN_ID;
    }

    public void setPLAN_ID(String PLAN_ID) {
        this.PLAN_ID = PLAN_ID;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public BigDecimal getSEQNO() {
        return this.SEQNO;
    }

    public void setSEQNO(BigDecimal SEQNO) {
        this.SEQNO = SEQNO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PLAN_ID", getPLAN_ID())
            .append("PRD_ID", getPRD_ID())
            .append("SEQNO", getSEQNO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_PORTFOLIO_PLAN_INVPK) ) return false;
        TBFPS_PORTFOLIO_PLAN_INVPK castOther = (TBFPS_PORTFOLIO_PLAN_INVPK) other;
        return new EqualsBuilder()
            .append(this.getPLAN_ID(), castOther.getPLAN_ID())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getSEQNO(), castOther.getSEQNO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPLAN_ID())
            .append(getPRD_ID())
            .append(getSEQNO())
            .toHashCode();
    }

}
