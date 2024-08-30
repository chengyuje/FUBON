package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_NATIONALITYPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String COUNTRY_ID;

    /** identifier field */
    private String TYPE;

    /** full constructor */
    public TBPRD_NATIONALITYPK(String COUNTRY_ID, String TYPE) {
        this.COUNTRY_ID = COUNTRY_ID;
        this.TYPE = TYPE;
    }

    /** default constructor */
    public TBPRD_NATIONALITYPK() {
    }

    public String getCOUNTRY_ID() {
        return this.COUNTRY_ID;
    }

    public void setCOUNTRY_ID(String COUNTRY_ID) {
        this.COUNTRY_ID = COUNTRY_ID;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("COUNTRY_ID", getCOUNTRY_ID())
            .append("TYPE", getTYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_NATIONALITYPK) ) return false;
        TBPRD_NATIONALITYPK castOther = (TBPRD_NATIONALITYPK) other;
        return new EqualsBuilder()
            .append(this.getCOUNTRY_ID(), castOther.getCOUNTRY_ID())
            .append(this.getTYPE(), castOther.getTYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCOUNTRY_ID())
            .append(getTYPE())
            .toHashCode();
    }

}
