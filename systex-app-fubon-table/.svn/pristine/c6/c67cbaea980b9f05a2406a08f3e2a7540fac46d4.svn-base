package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_QUESTIONNAIRE_RISK_SCOREPK  implements Serializable  {
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String C_LEVEL;
    
    /** identifier field */
    private String W_LEVEL;

    /** identifier field */
    private String RS_VERSION;

    /** full constructor */
    public TBKYC_QUESTIONNAIRE_RISK_SCOREPK(String RS_VERSION, String C_LEVEL, String W_LEVEL) {
        this.RS_VERSION = RS_VERSION;
        this.C_LEVEL = C_LEVEL;
        this.W_LEVEL = W_LEVEL;
    }

    /** default constructor */
    public TBKYC_QUESTIONNAIRE_RISK_SCOREPK() {
    }

    public String getC_LEVEL() {
		return C_LEVEL;
	}

	public void setC_LEVEL(String c_LEVEL) {
		C_LEVEL = c_LEVEL;
	}

	public String getW_LEVEL() {
		return W_LEVEL;
	}

	public void setW_LEVEL(String w_LEVEL) {
		W_LEVEL = w_LEVEL;
	}

    public String getRS_VERSION() {
        return this.RS_VERSION;
    }

    public void setRS_VERSION(String RS_VERSION) {
        this.RS_VERSION = RS_VERSION;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("RS_VERSION", getRS_VERSION())
            .append("C_LEVEL", getC_LEVEL())
            .append("W_LEVEL", getW_LEVEL())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBKYC_QUESTIONNAIRE_RISK_SCOREPK) ) return false;
        TBKYC_QUESTIONNAIRE_RISK_SCOREPK castOther = (TBKYC_QUESTIONNAIRE_RISK_SCOREPK) other;
        return new EqualsBuilder()
            .append(this.getRS_VERSION(), castOther.getRS_VERSION())
            .append(this.getC_LEVEL(), castOther.getC_LEVEL())
            .append(this.getW_LEVEL(), castOther.getW_LEVEL())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getRS_VERSION())
            .append(getC_LEVEL())
            .append(getW_LEVEL())
            .toHashCode();
    }

}
