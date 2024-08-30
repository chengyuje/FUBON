package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_REF_TARG_BRHPK  implements Serializable  {

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String REF_PROD;

    /** identifier field */
    private String YYYYMM;

    /** full constructor */
    public TBCAM_REF_TARG_BRHPK(String BRANCH_NBR, String REF_PROD, String YYYYMM) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.REF_PROD = REF_PROD;
        this.YYYYMM = YYYYMM;
    }

    /** default constructor */
    public TBCAM_REF_TARG_BRHPK() {
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getREF_PROD() {
        return this.REF_PROD;
    }

    public void setREF_PROD(String REF_PROD) {
        this.REF_PROD = REF_PROD;
    }

    public String getYYYYMM() {
        return this.YYYYMM;
    }

    public void setYYYYMM(String YYYYMM) {
        this.YYYYMM = YYYYMM;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("REF_PROD", getREF_PROD())
            .append("YYYYMM", getYYYYMM())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_REF_TARG_BRHPK) ) return false;
        TBCAM_REF_TARG_BRHPK castOther = (TBCAM_REF_TARG_BRHPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getREF_PROD(), castOther.getREF_PROD())
            .append(this.getYYYYMM(), castOther.getYYYYMM())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getREF_PROD())
            .append(getYYYYMM())
            .toHashCode();
    }

}
