package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_SALES_AOCODEPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String AO_CODE;

    /** identifier field */
    private String EMP_ID;

    /** full constructor */
    public TBORG_SALES_AOCODEPK(String AO_CODE, String EMP_ID) {
        this.AO_CODE = AO_CODE;
        this.EMP_ID = EMP_ID;
    }

    /** default constructor */
    public TBORG_SALES_AOCODEPK() {
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AO_CODE", getAO_CODE())
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBORG_SALES_AOCODEPK) ) return false;
        TBORG_SALES_AOCODEPK castOther = (TBORG_SALES_AOCODEPK) other;
        return new EqualsBuilder()
            .append(this.getAO_CODE(), castOther.getAO_CODE())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAO_CODE())
            .append(getEMP_ID())
            .toHashCode();
    }

}
