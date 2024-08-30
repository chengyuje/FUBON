package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DAILY_CONTRACTPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private BigDecimal N_SEQ;

    /** identifier field */
    private String POLICY_NO;

    /** full constructor */
    public TBPMS_DAILY_CONTRACTPK(BigDecimal N_SEQ, String POLICY_NO) {
        this.N_SEQ = N_SEQ;
        this.POLICY_NO = POLICY_NO;
    }

    /** default constructor */
    public TBPMS_DAILY_CONTRACTPK() {
    }

    public BigDecimal getN_SEQ() {
        return this.N_SEQ;
    }

    public void setN_SEQ(BigDecimal N_SEQ) {
        this.N_SEQ = N_SEQ;
    }

    public String getPOLICY_NO() {
        return this.POLICY_NO;
    }

    public void setPOLICY_NO(String POLICY_NO) {
        this.POLICY_NO = POLICY_NO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("N_SEQ", getN_SEQ())
            .append("POLICY_NO", getPOLICY_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_DAILY_CONTRACTPK) ) return false;
        TBPMS_DAILY_CONTRACTPK castOther = (TBPMS_DAILY_CONTRACTPK) other;
        return new EqualsBuilder()
            .append(this.getN_SEQ(), castOther.getN_SEQ())
            .append(this.getPOLICY_NO(), castOther.getPOLICY_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getN_SEQ())
            .append(getPOLICY_NO())
            .toHashCode();
    }

}
