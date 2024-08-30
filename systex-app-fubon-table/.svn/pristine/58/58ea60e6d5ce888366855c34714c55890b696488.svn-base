package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBESB_LOGPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String ESB_SNO;

    /** identifier field */
    private String ESB_STATUS;

    /** full constructor */
    public TBESB_LOGPK(String ESB_SNO, String ESB_STATUS) {
        this.ESB_SNO = ESB_SNO;
        this.ESB_STATUS = ESB_STATUS;
    }

    /** default constructor */
    public TBESB_LOGPK() {
    }

    public String getESB_SNO() {
        return this.ESB_SNO;
    }

    public void setESB_SNO(String ESB_SNO) {
        this.ESB_SNO = ESB_SNO;
    }

    public String getESB_STATUS() {
        return this.ESB_STATUS;
    }

    public void setESB_STATUS(String ESB_STATUS) {
        this.ESB_STATUS = ESB_STATUS;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ESB_SNO", getESB_SNO())
            .append("ESB_STATUS", getESB_STATUS())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBESB_LOGPK) ) return false;
        TBESB_LOGPK castOther = (TBESB_LOGPK) other;
        return new EqualsBuilder()
            .append(this.getESB_SNO(), castOther.getESB_SNO())
            .append(this.getESB_STATUS(), castOther.getESB_STATUS())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getESB_SNO())
            .append(getESB_STATUS())
            .toHashCode();
    }

}
