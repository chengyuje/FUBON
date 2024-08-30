package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_PRD_SHARED_LINKPK  implements Serializable  {
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String DOC_ID;

    /** identifier field */
    private String PTYPE;

    /** full constructor */
    public TBSYS_PRD_SHARED_LINKPK(String DOC_ID, String PTYPE) {
        this.DOC_ID = DOC_ID;
        this.PTYPE = PTYPE;
    }

    /** default constructor */
    public TBSYS_PRD_SHARED_LINKPK() {
    }

    public String getDOC_ID() {
        return this.DOC_ID;
    }

    public void setDOC_ID(String DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public String getPTYPE() {
        return this.PTYPE;
    }

    public void setPTYPE(String PTYPE) {
        this.PTYPE = PTYPE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DOC_ID", getDOC_ID())
            .append("PTYPE", getPTYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYS_PRD_SHARED_LINKPK) ) return false;
        TBSYS_PRD_SHARED_LINKPK castOther = (TBSYS_PRD_SHARED_LINKPK) other;
        return new EqualsBuilder()
            .append(this.getDOC_ID(), castOther.getDOC_ID())
            .append(this.getPTYPE(), castOther.getPTYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDOC_ID())
            .append(getPTYPE())
            .toHashCode();
    }

}
