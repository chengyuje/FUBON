package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_POT_CF_DTL_INSPK implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String POLICY_NO;

    /** identifier field */
    private String POLICY_SEQ;

    /** full constructor */
    public TBPMS_POT_CF_DTL_INSPK(String CUST_ID, String POLICY_NO, String POLICY_SEQ) {
        this.CUST_ID = CUST_ID;
        this.POLICY_NO = POLICY_NO;
        this.POLICY_SEQ = POLICY_SEQ;
    }

    /** default constructor */
    public TBPMS_POT_CF_DTL_INSPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getPOLICY_NO() {
        return this.POLICY_NO;
    }

    public void setPOLICY_NO(String POLICY_NO) {
        this.POLICY_NO = POLICY_NO;
    }

    public String getPOLICY_SEQ() {
        return this.POLICY_SEQ;
    }

    public void setPOLICY_SEQ(String POLICY_SEQ) {
        this.POLICY_SEQ = POLICY_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("POLICY_NO", getPOLICY_NO())
            .append("POLICY_SEQ", getPOLICY_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_POT_CF_DTL_INSPK) ) return false;
        TBPMS_POT_CF_DTL_INSPK castOther = (TBPMS_POT_CF_DTL_INSPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getPOLICY_NO(), castOther.getPOLICY_NO())
            .append(this.getPOLICY_SEQ(), castOther.getPOLICY_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getPOLICY_NO())
            .append(getPOLICY_SEQ())
            .toHashCode();
    }

}
