package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MNGR_EVAL_SCHEDULEPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String AO_CODE;

    /** identifier field */
    private String DATE_S;

    /** full constructor */
    public TBPMS_MNGR_EVAL_SCHEDULEPK(String AO_CODE, String DATE_S) {
        this.AO_CODE = AO_CODE;
        this.DATE_S = DATE_S;
    }

    /** default constructor */
    public TBPMS_MNGR_EVAL_SCHEDULEPK() {
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getDATE_S() {
        return this.DATE_S;
    }

    public void setDATE_S(String DATE_S) {
        this.DATE_S = DATE_S;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AO_CODE", getAO_CODE())
            .append("DATE_S", getDATE_S())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_MNGR_EVAL_SCHEDULEPK) ) return false;
        TBPMS_MNGR_EVAL_SCHEDULEPK castOther = (TBPMS_MNGR_EVAL_SCHEDULEPK) other;
        return new EqualsBuilder()
            .append(this.getAO_CODE(), castOther.getAO_CODE())
            .append(this.getDATE_S(), castOther.getDATE_S())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAO_CODE())
            .append(getDATE_S())
            .toHashCode();
    }

}
