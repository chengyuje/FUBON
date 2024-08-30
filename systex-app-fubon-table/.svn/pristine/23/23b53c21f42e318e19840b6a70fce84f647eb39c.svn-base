package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_QUESTIONNAIRE_RISK_LEVELPK  implements Serializable  {
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CUST_RL_ID;

    /** identifier field */
    private String RL_VERSION;

    /** full constructor */
    public TBKYC_QUESTIONNAIRE_RISK_LEVELPK(String CUST_RL_ID, String RL_VERSION) {
        this.CUST_RL_ID = CUST_RL_ID;
        this.RL_VERSION = RL_VERSION;
    }

    /** default constructor */
    public TBKYC_QUESTIONNAIRE_RISK_LEVELPK() {
    }

    public String getCUST_RL_ID() {
        return this.CUST_RL_ID;
    }

    public void setCUST_RL_ID(String CUST_RL_ID) {
        this.CUST_RL_ID = CUST_RL_ID;
    }

    public String getRL_VERSION() {
        return this.RL_VERSION;
    }

    public void setRL_VERSION(String RL_VERSION) {
        this.RL_VERSION = RL_VERSION;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_RL_ID", getCUST_RL_ID())
            .append("RL_VERSION", getRL_VERSION())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBKYC_QUESTIONNAIRE_RISK_LEVELPK) ) return false;
        TBKYC_QUESTIONNAIRE_RISK_LEVELPK castOther = (TBKYC_QUESTIONNAIRE_RISK_LEVELPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_RL_ID(), castOther.getCUST_RL_ID())
            .append(this.getRL_VERSION(), castOther.getRL_VERSION())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_RL_ID())
            .append(getRL_VERSION())
            .toHashCode();
    }

}
