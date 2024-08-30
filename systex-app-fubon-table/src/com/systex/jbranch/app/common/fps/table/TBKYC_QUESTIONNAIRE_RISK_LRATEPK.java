package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_QUESTIONNAIRE_RISK_LRATEPK  implements Serializable  {
	private static final long serialVersionUID = 1L;

    /** identifier field */
    private String RLR_VERSION;

    /** identifier field */
    private String CUST_RL_ID;

    /** full constructor */
    public TBKYC_QUESTIONNAIRE_RISK_LRATEPK(String RLR_VERSION, String CUST_RL_ID) {
        this.RLR_VERSION = RLR_VERSION;
        this.CUST_RL_ID = CUST_RL_ID;
    }

    /** default constructor */
    public TBKYC_QUESTIONNAIRE_RISK_LRATEPK() {
    }

    public String getRLR_VERSION() {
		return RLR_VERSION;
	}

	public void setRLR_VERSION(String rLR_VERSION) {
		RLR_VERSION = rLR_VERSION;
	}

	public String getCUST_RL_ID() {
		return CUST_RL_ID;
	}

	public void setCUST_RL_ID(String cUST_RL_ID) {
		CUST_RL_ID = cUST_RL_ID;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("RLR_VERSION", getRLR_VERSION())
            .append("CUST_RL_ID", getCUST_RL_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBKYC_QUESTIONNAIRE_RISK_LRATEPK) ) return false;
        TBKYC_QUESTIONNAIRE_RISK_LRATEPK castOther = (TBKYC_QUESTIONNAIRE_RISK_LRATEPK) other;
        return new EqualsBuilder()
            .append(this.getRLR_VERSION(), castOther.getRLR_VERSION())
            .append(this.getCUST_RL_ID(), castOther.getCUST_RL_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getRLR_VERSION())
            .append(getCUST_RL_ID())
            .toHashCode();
    }

}
