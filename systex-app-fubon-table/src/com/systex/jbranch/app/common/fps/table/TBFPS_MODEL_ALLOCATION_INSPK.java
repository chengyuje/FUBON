package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_MODEL_ALLOCATION_INSPK  implements Serializable  {

    /** identifier field */
    private String PRD_ID;

    /** identifier field */
    private BigDecimal SEQNO;

    /** identifier field */
    private String TARGET_ID;

    /** full constructor */
    public TBFPS_MODEL_ALLOCATION_INSPK(String PRD_ID, BigDecimal SEQNO, String TARGET_ID) {
        this.PRD_ID = PRD_ID;
        this.SEQNO = SEQNO;
        this.TARGET_ID = TARGET_ID;
    }

    /** default constructor */
    public TBFPS_MODEL_ALLOCATION_INSPK() {
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

    public String getTARGET_ID() {
        return this.TARGET_ID;
    }

    public void setTARGET_ID(String TARGET_ID) {
        this.TARGET_ID = TARGET_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRD_ID", getPRD_ID())
            .append("SEQNO", getSEQNO())
            .append("TARGET_ID", getTARGET_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_MODEL_ALLOCATION_INSPK) ) return false;
        TBFPS_MODEL_ALLOCATION_INSPK castOther = (TBFPS_MODEL_ALLOCATION_INSPK) other;
        return new EqualsBuilder()
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getSEQNO(), castOther.getSEQNO())
            .append(this.getTARGET_ID(), castOther.getTARGET_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRD_ID())
            .append(getSEQNO())
            .append(getTARGET_ID())
            .toHashCode();
    }

}
