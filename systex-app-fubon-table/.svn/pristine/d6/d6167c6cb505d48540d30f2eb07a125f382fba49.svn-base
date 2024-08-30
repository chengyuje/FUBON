package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_GROUPPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String GROUP_ID;

    /** full constructor */
    public TBCRM_CUST_GROUPPK(String CUST_ID, String GROUP_ID) {
        this.CUST_ID = CUST_ID;
        this.GROUP_ID = GROUP_ID;
    }

    /** default constructor */
    public TBCRM_CUST_GROUPPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getGROUP_ID() {
        return this.GROUP_ID;
    }

    public void setGROUP_ID(String GROUP_ID) {
        this.GROUP_ID = GROUP_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("GROUP_ID", getGROUP_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_CUST_GROUPPK) ) return false;
        TBCRM_CUST_GROUPPK castOther = (TBCRM_CUST_GROUPPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getGROUP_ID(), castOther.getGROUP_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getGROUP_ID())
            .toHashCode();
    }

}
