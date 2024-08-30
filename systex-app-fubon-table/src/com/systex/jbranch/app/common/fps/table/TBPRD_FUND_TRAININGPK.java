package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_FUND_TRAININGPK  implements Serializable  {

    private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String TRAGET_ID;

    /** full constructor */
    public TBPRD_FUND_TRAININGPK(String CUST_ID, String TRAGET_ID) {
        this.CUST_ID = CUST_ID;
        this.TRAGET_ID = TRAGET_ID;
    }

    /** default constructor */
    public TBPRD_FUND_TRAININGPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getTRAGET_ID() {
        return this.TRAGET_ID;
    }

    public void setTRAGET_ID(String TRAGET_ID) {
        this.TRAGET_ID = TRAGET_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("TRAGET_ID", getTRAGET_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_FUND_TRAININGPK) ) return false;
        TBPRD_FUND_TRAININGPK castOther = (TBPRD_FUND_TRAININGPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getTRAGET_ID(), castOther.getTRAGET_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getTRAGET_ID())
            .toHashCode();
    }

}
