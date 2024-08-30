package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_HO_MGMTPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String YEARMON;

    /** full constructor */
    public TBPMS_HO_MGMTPK(String CUST_ID, String YEARMON) {
        this.CUST_ID = CUST_ID;
        this.YEARMON = YEARMON;
    }

    /** default constructor */
    public TBPMS_HO_MGMTPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getYEARMON() {
        return this.YEARMON;
    }

    public void setYEARMON(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("YEARMON", getYEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_HO_MGMTPK) ) return false;
        TBPMS_HO_MGMTPK castOther = (TBPMS_HO_MGMTPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getYEARMON(), castOther.getYEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getYEARMON())
            .toHashCode();
    }

}
