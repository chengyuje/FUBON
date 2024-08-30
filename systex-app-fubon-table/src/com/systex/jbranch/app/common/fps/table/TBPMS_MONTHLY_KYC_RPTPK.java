package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MONTHLY_KYC_RPTPK  implements Serializable  {

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String DATA_DATE;

    /** identifier field */
    private String SEQ;

    /** full constructor */
    public TBPMS_MONTHLY_KYC_RPTPK(String BRANCH_NBR, String DATA_DATE, String SEQ) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.DATA_DATE = DATA_DATE;
        this.SEQ = SEQ;
    }

    /** default constructor */
    public TBPMS_MONTHLY_KYC_RPTPK() {
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(String DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("DATA_DATE", getDATA_DATE())
            .append("SEQ", getSEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_MONTHLY_KYC_RPTPK) ) return false;
        TBPMS_MONTHLY_KYC_RPTPK castOther = (TBPMS_MONTHLY_KYC_RPTPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getDATA_DATE(), castOther.getDATA_DATE())
            .append(this.getSEQ(), castOther.getSEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getDATA_DATE())
            .append(getSEQ())
            .toHashCode();
    }

}
