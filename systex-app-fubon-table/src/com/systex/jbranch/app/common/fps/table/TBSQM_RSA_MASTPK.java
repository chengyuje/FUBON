package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_RSA_MASTPK  implements Serializable  {

    /** identifier field */
    private String AUDIT_TYPE;

    /** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String YEARQTR;

    /** full constructor */
    public TBSQM_RSA_MASTPK(String AUDIT_TYPE, String CUST_ID, String YEARQTR) {
        this.AUDIT_TYPE = AUDIT_TYPE;
        this.CUST_ID = CUST_ID;
        this.YEARQTR = YEARQTR;
    }

    /** default constructor */
    public TBSQM_RSA_MASTPK() {
    }

    public String getAUDIT_TYPE() {
        return this.AUDIT_TYPE;
    }

    public void setAUDIT_TYPE(String AUDIT_TYPE) {
        this.AUDIT_TYPE = AUDIT_TYPE;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getYEARQTR() {
        return this.YEARQTR;
    }

    public void setYEARQTR(String YEARQTR) {
        this.YEARQTR = YEARQTR;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AUDIT_TYPE", getAUDIT_TYPE())
            .append("CUST_ID", getCUST_ID())
            .append("YEARQTR", getYEARQTR())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSQM_RSA_MASTPK) ) return false;
        TBSQM_RSA_MASTPK castOther = (TBSQM_RSA_MASTPK) other;
        return new EqualsBuilder()
            .append(this.getAUDIT_TYPE(), castOther.getAUDIT_TYPE())
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getYEARQTR(), castOther.getYEARQTR())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAUDIT_TYPE())
            .append(getCUST_ID())
            .append(getYEARQTR())
            .toHashCode();
    }

}
