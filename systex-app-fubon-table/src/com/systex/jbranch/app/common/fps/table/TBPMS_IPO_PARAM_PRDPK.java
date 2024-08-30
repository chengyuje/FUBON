package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_IPO_PARAM_PRDPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String PRD_ID;

    /** identifier field */
    private BigDecimal PRJ_SEQ;

    /** full constructor */
    public TBPMS_IPO_PARAM_PRDPK(String PRD_ID, BigDecimal PRJ_SEQ) {
        this.PRD_ID = PRD_ID;
        this.PRJ_SEQ = PRJ_SEQ;
    }

    /** default constructor */
    public TBPMS_IPO_PARAM_PRDPK() {
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public BigDecimal getPRJ_SEQ() {
        return this.PRJ_SEQ;
    }

    public void setPRJ_SEQ(BigDecimal PRJ_SEQ) {
        this.PRJ_SEQ = PRJ_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRD_ID", getPRD_ID())
            .append("PRJ_SEQ", getPRJ_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_IPO_PARAM_PRDPK) ) return false;
        TBPMS_IPO_PARAM_PRDPK castOther = (TBPMS_IPO_PARAM_PRDPK) other;
        return new EqualsBuilder()
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getPRJ_SEQ(), castOther.getPRJ_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRD_ID())
            .append(getPRJ_SEQ())
            .toHashCode();
    }

}
