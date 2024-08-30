package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_SPPEDU_DETAILPK  implements Serializable  {

    /** identifier field */
    private String CHILD_NAME;

    /** identifier field */
    private String CUST_ID;

    /** full constructor */
    public TBINS_SPPEDU_DETAILPK(String CHILD_NAME, String CUST_ID) {
        this.CHILD_NAME = CHILD_NAME;
        this.CUST_ID = CUST_ID;
    }

    /** default constructor */
    public TBINS_SPPEDU_DETAILPK() {
    }

    public String getCHILD_NAME() {
        return this.CHILD_NAME;
    }

    public void setCHILD_NAME(String CHILD_NAME) {
        this.CHILD_NAME = CHILD_NAME;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CHILD_NAME", getCHILD_NAME())
            .append("CUST_ID", getCUST_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBINS_SPPEDU_DETAILPK) ) return false;
        TBINS_SPPEDU_DETAILPK castOther = (TBINS_SPPEDU_DETAILPK) other;
        return new EqualsBuilder()
            .append(this.getCHILD_NAME(), castOther.getCHILD_NAME())
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCHILD_NAME())
            .append(getCUST_ID())
            .toHashCode();
    }

}
