package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DYNAMIC_RPT_DTLPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private BigDecimal ROW_SEQ;

    /** identifier field */
    private BigDecimal SEQ;

    /** full constructor */
    public TBPMS_DYNAMIC_RPT_DTLPK(BigDecimal ROW_SEQ, BigDecimal SEQ) {
        this.ROW_SEQ = ROW_SEQ;
        this.SEQ = SEQ;
    }

    /** default constructor */
    public TBPMS_DYNAMIC_RPT_DTLPK() {
    }

    public BigDecimal getROW_SEQ() {
        return this.ROW_SEQ;
    }

    public void setROW_SEQ(BigDecimal ROW_SEQ) {
        this.ROW_SEQ = ROW_SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ROW_SEQ", getROW_SEQ())
            .append("SEQ", getSEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_DYNAMIC_RPT_DTLPK) ) return false;
        TBPMS_DYNAMIC_RPT_DTLPK castOther = (TBPMS_DYNAMIC_RPT_DTLPK) other;
        return new EqualsBuilder()
            .append(this.getROW_SEQ(), castOther.getROW_SEQ())
            .append(this.getSEQ(), castOther.getSEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getROW_SEQ())
            .append(getSEQ())
            .toHashCode();
    }

}
