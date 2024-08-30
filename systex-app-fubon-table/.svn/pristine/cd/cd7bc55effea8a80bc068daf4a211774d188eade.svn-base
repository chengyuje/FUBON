package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_IPO_PARAM_BR_NAMEPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private BigDecimal COL_NO;

    /** identifier field */
    private BigDecimal PRJ_SEQ;

    /** full constructor */
    public TBPMS_IPO_PARAM_BR_NAMEPK(BigDecimal COL_NO, BigDecimal PRJ_SEQ) {
        this.COL_NO = COL_NO;
        this.PRJ_SEQ = PRJ_SEQ;
    }

    /** default constructor */
    public TBPMS_IPO_PARAM_BR_NAMEPK() {
    }

    public BigDecimal getCOL_NO() {
        return this.COL_NO;
    }

    public void setCOL_NO(BigDecimal COL_NO) {
        this.COL_NO = COL_NO;
    }

    public BigDecimal getPRJ_SEQ() {
        return this.PRJ_SEQ;
    }

    public void setPRJ_SEQ(BigDecimal PRJ_SEQ) {
        this.PRJ_SEQ = PRJ_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("COL_NO", getCOL_NO())
            .append("PRJ_SEQ", getPRJ_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_IPO_PARAM_BR_NAMEPK) ) return false;
        TBPMS_IPO_PARAM_BR_NAMEPK castOther = (TBPMS_IPO_PARAM_BR_NAMEPK) other;
        return new EqualsBuilder()
            .append(this.getCOL_NO(), castOther.getCOL_NO())
            .append(this.getPRJ_SEQ(), castOther.getPRJ_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCOL_NO())
            .append(getPRJ_SEQ())
            .toHashCode();
    }

}
