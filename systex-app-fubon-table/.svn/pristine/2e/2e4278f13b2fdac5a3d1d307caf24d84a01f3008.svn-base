package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_REF_TARG_ROLPK  implements Serializable  {

    /** identifier field */
    private String REF_PROD;

    /** identifier field */
    private String SALES_ROLE;

    /** identifier field */
    private String YYYYMM;

    /** full constructor */
    public TBCAM_REF_TARG_ROLPK(String REF_PROD, String SALES_ROLE, String YYYYMM) {
        this.REF_PROD = REF_PROD;
        this.SALES_ROLE = SALES_ROLE;
        this.YYYYMM = YYYYMM;
    }

    /** default constructor */
    public TBCAM_REF_TARG_ROLPK() {
    }

    public String getREF_PROD() {
        return this.REF_PROD;
    }

    public void setREF_PROD(String REF_PROD) {
        this.REF_PROD = REF_PROD;
    }

    public String getSALES_ROLE() {
        return this.SALES_ROLE;
    }

    public void setSALES_ROLE(String SALES_ROLE) {
        this.SALES_ROLE = SALES_ROLE;
    }

    public String getYYYYMM() {
        return this.YYYYMM;
    }

    public void setYYYYMM(String YYYYMM) {
        this.YYYYMM = YYYYMM;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("REF_PROD", getREF_PROD())
            .append("SALES_ROLE", getSALES_ROLE())
            .append("YYYYMM", getYYYYMM())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_REF_TARG_ROLPK) ) return false;
        TBCAM_REF_TARG_ROLPK castOther = (TBCAM_REF_TARG_ROLPK) other;
        return new EqualsBuilder()
            .append(this.getREF_PROD(), castOther.getREF_PROD())
            .append(this.getSALES_ROLE(), castOther.getSALES_ROLE())
            .append(this.getYYYYMM(), castOther.getYYYYMM())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getREF_PROD())
            .append(getSALES_ROLE())
            .append(getYYYYMM())
            .toHashCode();
    }

}
