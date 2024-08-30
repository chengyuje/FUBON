package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_NS_MONTHLY_TRADEPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String CERT_NBR;

    /** identifier field */
    private Timestamp MAKE_DATE;

    /** identifier field */
    private String YEARMON;

    /** full constructor */
    public TBPMS_NS_MONTHLY_TRADEPK(String BRANCH_NBR, String CERT_NBR, Timestamp MAKE_DATE, String YEARMON) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.CERT_NBR = CERT_NBR;
        this.MAKE_DATE = MAKE_DATE;
        this.YEARMON = YEARMON;
    }

    /** default constructor */
    public TBPMS_NS_MONTHLY_TRADEPK() {
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getCERT_NBR() {
        return this.CERT_NBR;
    }

    public void setCERT_NBR(String CERT_NBR) {
        this.CERT_NBR = CERT_NBR;
    }

    public Timestamp getMAKE_DATE() {
        return this.MAKE_DATE;
    }

    public void setMAKE_DATE(Timestamp MAKE_DATE) {
        this.MAKE_DATE = MAKE_DATE;
    }

    public String getYEARMON() {
        return this.YEARMON;
    }

    public void setYEARMON(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("CERT_NBR", getCERT_NBR())
            .append("MAKE_DATE", getMAKE_DATE())
            .append("YEARMON", getYEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_NS_MONTHLY_TRADEPK) ) return false;
        TBPMS_NS_MONTHLY_TRADEPK castOther = (TBPMS_NS_MONTHLY_TRADEPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getCERT_NBR(), castOther.getCERT_NBR())
            .append(this.getMAKE_DATE(), castOther.getMAKE_DATE())
            .append(this.getYEARMON(), castOther.getYEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getCERT_NBR())
            .append(getMAKE_DATE())
            .append(getYEARMON())
            .toHashCode();
    }

}
