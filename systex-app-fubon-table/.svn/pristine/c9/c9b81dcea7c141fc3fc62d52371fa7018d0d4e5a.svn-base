package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_UNPLAN_RPT_DTLPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String AO_CODE;

    /** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String YEARMON;

    /** full constructor */
    public TBPMS_UNPLAN_RPT_DTLPK(String AO_CODE, String CUST_ID, String YEARMON) {
        this.AO_CODE = AO_CODE;
        this.CUST_ID = CUST_ID;
        this.YEARMON = YEARMON;
    }

    /** default constructor */
    public TBPMS_UNPLAN_RPT_DTLPK() {
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
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
            .append("AO_CODE", getAO_CODE())
            .append("CUST_ID", getCUST_ID())
            .append("YEARMON", getYEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_UNPLAN_RPT_DTLPK) ) return false;
        TBPMS_UNPLAN_RPT_DTLPK castOther = (TBPMS_UNPLAN_RPT_DTLPK) other;
        return new EqualsBuilder()
            .append(this.getAO_CODE(), castOther.getAO_CODE())
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getYEARMON(), castOther.getYEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAO_CODE())
            .append(getCUST_ID())
            .append(getYEARMON())
            .toHashCode();
    }

}
