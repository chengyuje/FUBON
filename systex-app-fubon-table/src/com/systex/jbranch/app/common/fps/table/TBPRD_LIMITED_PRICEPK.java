package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_LIMITED_PRICEPK  implements Serializable  {
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String PRD_ID;

    /** full constructor */
    public TBPRD_LIMITED_PRICEPK(String CUST_ID, String PRD_ID) {
        this.CUST_ID = CUST_ID;
        this.PRD_ID = PRD_ID;
    }

    /** default constructor */
    public TBPRD_LIMITED_PRICEPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("PRD_ID", getPRD_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_LIMITED_PRICEPK) ) return false;
        TBPRD_LIMITED_PRICEPK castOther = (TBPRD_LIMITED_PRICEPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getPRD_ID())
            .toHashCode();
    }

}
