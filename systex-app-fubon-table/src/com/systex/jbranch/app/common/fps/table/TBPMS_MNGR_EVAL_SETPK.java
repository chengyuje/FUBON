package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MNGR_EVAL_SETPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String JOB_TITLE_ID;

    /** identifier field */
    private BigDecimal MTD_ACH_RATE_E;

    /** identifier field */
    private BigDecimal MTD_ACH_RATE_S;

    /** identifier field */
    private String YEARMON;

    /** full constructor */
    public TBPMS_MNGR_EVAL_SETPK(String JOB_TITLE_ID, BigDecimal MTD_ACH_RATE_E, BigDecimal MTD_ACH_RATE_S, String YEARMON) {
        this.JOB_TITLE_ID = JOB_TITLE_ID;
        this.MTD_ACH_RATE_E = MTD_ACH_RATE_E;
        this.MTD_ACH_RATE_S = MTD_ACH_RATE_S;
        this.YEARMON = YEARMON;
    }

    /** default constructor */
    public TBPMS_MNGR_EVAL_SETPK() {
    }

    public String getJOB_TITLE_ID() {
        return this.JOB_TITLE_ID;
    }

    public void setJOB_TITLE_ID(String JOB_TITLE_ID) {
        this.JOB_TITLE_ID = JOB_TITLE_ID;
    }

    public BigDecimal getMTD_ACH_RATE_E() {
        return this.MTD_ACH_RATE_E;
    }

    public void setMTD_ACH_RATE_E(BigDecimal MTD_ACH_RATE_E) {
        this.MTD_ACH_RATE_E = MTD_ACH_RATE_E;
    }

    public BigDecimal getMTD_ACH_RATE_S() {
        return this.MTD_ACH_RATE_S;
    }

    public void setMTD_ACH_RATE_S(BigDecimal MTD_ACH_RATE_S) {
        this.MTD_ACH_RATE_S = MTD_ACH_RATE_S;
    }

    public String getYEARMON() {
        return this.YEARMON;
    }

    public void setYEARMON(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("JOB_TITLE_ID", getJOB_TITLE_ID())
            .append("MTD_ACH_RATE_E", getMTD_ACH_RATE_E())
            .append("MTD_ACH_RATE_S", getMTD_ACH_RATE_S())
            .append("YEARMON", getYEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_MNGR_EVAL_SETPK) ) return false;
        TBPMS_MNGR_EVAL_SETPK castOther = (TBPMS_MNGR_EVAL_SETPK) other;
        return new EqualsBuilder()
            .append(this.getJOB_TITLE_ID(), castOther.getJOB_TITLE_ID())
            .append(this.getMTD_ACH_RATE_E(), castOther.getMTD_ACH_RATE_E())
            .append(this.getMTD_ACH_RATE_S(), castOther.getMTD_ACH_RATE_S())
            .append(this.getYEARMON(), castOther.getYEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getJOB_TITLE_ID())
            .append(getMTD_ACH_RATE_E())
            .append(getMTD_ACH_RATE_S())
            .append(getYEARMON())
            .toHashCode();
    }

}
