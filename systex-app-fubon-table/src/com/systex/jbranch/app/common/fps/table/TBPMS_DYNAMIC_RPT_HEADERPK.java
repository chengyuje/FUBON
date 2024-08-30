package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DYNAMIC_RPT_HEADERPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private BigDecimal COL_SEQ;

    /** identifier field */
    private BigDecimal SEQ;

    /** full constructor */
    public TBPMS_DYNAMIC_RPT_HEADERPK(BigDecimal COL_SEQ, BigDecimal SEQ) {
        this.COL_SEQ = COL_SEQ;
        this.SEQ = SEQ;
    }

    /** default constructor */
    public TBPMS_DYNAMIC_RPT_HEADERPK() {
    }

    public BigDecimal getCOL_SEQ() {
        return this.COL_SEQ;
    }

    public void setCOL_SEQ(BigDecimal COL_SEQ) {
        this.COL_SEQ = COL_SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("COL_SEQ", getCOL_SEQ())
            .append("SEQ", getSEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_DYNAMIC_RPT_HEADERPK) ) return false;
        TBPMS_DYNAMIC_RPT_HEADERPK castOther = (TBPMS_DYNAMIC_RPT_HEADERPK) other;
        return new EqualsBuilder()
            .append(this.getCOL_SEQ(), castOther.getCOL_SEQ())
            .append(this.getSEQ(), castOther.getSEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCOL_SEQ())
            .append(getSEQ())
            .toHashCode();
    }

}
