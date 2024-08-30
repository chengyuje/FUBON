package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_INS_TXNPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String INS_ID;

    /** identifier field */
    private String TX_TYPE;

    /** full constructor */
    public TBPMS_INS_TXNPK(String INS_ID, String TX_TYPE) {
        this.INS_ID = INS_ID;
        this.TX_TYPE = TX_TYPE;
    }

    /** default constructor */
    public TBPMS_INS_TXNPK() {
    }

    public String getINS_ID() {
        return this.INS_ID;
    }

    public void setINS_ID(String INS_ID) {
        this.INS_ID = INS_ID;
    }

    public String getTX_TYPE() {
        return this.TX_TYPE;
    }

    public void setTX_TYPE(String TX_TYPE) {
        this.TX_TYPE = TX_TYPE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INS_ID", getINS_ID())
            .append("TX_TYPE", getTX_TYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_INS_TXNPK) ) return false;
        TBPMS_INS_TXNPK castOther = (TBPMS_INS_TXNPK) other;
        return new EqualsBuilder()
            .append(this.getINS_ID(), castOther.getINS_ID())
            .append(this.getTX_TYPE(), castOther.getTX_TYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getINS_ID())
            .append(getTX_TYPE())
            .toHashCode();
    }

}
