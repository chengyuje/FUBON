package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_POT_CF_DTL_INVPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CUST_ID;

    /** identifier field */
    private BigDecimal SEQ;

    /** full constructor */
    public TBPMS_POT_CF_DTL_INVPK(String CUST_ID, BigDecimal SEQ) {
        this.CUST_ID = CUST_ID;
        this.SEQ = SEQ;
    }

    /** default constructor */
    public TBPMS_POT_CF_DTL_INVPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("SEQ", getSEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_POT_CF_DTL_INVPK) ) return false;
        TBPMS_POT_CF_DTL_INVPK castOther = (TBPMS_POT_CF_DTL_INVPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getSEQ(), castOther.getSEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getSEQ())
            .toHashCode();
    }

}
