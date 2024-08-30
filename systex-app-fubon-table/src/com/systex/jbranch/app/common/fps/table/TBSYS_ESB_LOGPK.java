package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_ESB_LOGPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String ESB_SNO;

    /** identifier field */
    private String ESB_TYPE;

    /** full constructor */
    public TBSYS_ESB_LOGPK(String ESB_SNO, String ESB_TYPE) {
        this.ESB_SNO = ESB_SNO;
        this.ESB_TYPE = ESB_TYPE;
    }

    /** default constructor */
    public TBSYS_ESB_LOGPK() {
    }

    public String getESB_SNO() {
        return this.ESB_SNO;
    }

    public void setESB_SNO(String ESB_SNO) {
        this.ESB_SNO = ESB_SNO;
    }

    public String getESB_TYPE() {
        return this.ESB_TYPE;
    }

    public void setESB_TYPE(String ESB_TYPE) {
        this.ESB_TYPE = ESB_TYPE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ESB_SNO", getESB_SNO())
            .append("ESB_TYPE", getESB_TYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYS_ESB_LOGPK) ) return false;
        TBSYS_ESB_LOGPK castOther = (TBSYS_ESB_LOGPK) other;
        return new EqualsBuilder()
            .append(this.getESB_SNO(), castOther.getESB_SNO())
            .append(this.getESB_TYPE(), castOther.getESB_TYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getESB_SNO())
            .append(getESB_TYPE())
            .toHashCode();
    }

}
