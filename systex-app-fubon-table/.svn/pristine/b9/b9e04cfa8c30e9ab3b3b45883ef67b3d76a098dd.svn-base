package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_SFA_PARA_DOC_MAPPPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String SFA_DOC_ID;

    /** identifier field */
    private String SFA_PARA_ID;

    /** full constructor */
    public TBCAM_SFA_PARA_DOC_MAPPPK(String SFA_DOC_ID, String SFA_PARA_ID) {
        this.SFA_DOC_ID = SFA_DOC_ID;
        this.SFA_PARA_ID = SFA_PARA_ID;
    }

    /** default constructor */
    public TBCAM_SFA_PARA_DOC_MAPPPK() {
    }

    public String getSFA_DOC_ID() {
        return this.SFA_DOC_ID;
    }

    public void setSFA_DOC_ID(String SFA_DOC_ID) {
        this.SFA_DOC_ID = SFA_DOC_ID;
    }

    public String getSFA_PARA_ID() {
        return this.SFA_PARA_ID;
    }

    public void setSFA_PARA_ID(String SFA_PARA_ID) {
        this.SFA_PARA_ID = SFA_PARA_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SFA_DOC_ID", getSFA_DOC_ID())
            .append("SFA_PARA_ID", getSFA_PARA_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_SFA_PARA_DOC_MAPPPK) ) return false;
        TBCAM_SFA_PARA_DOC_MAPPPK castOther = (TBCAM_SFA_PARA_DOC_MAPPPK) other;
        return new EqualsBuilder()
            .append(this.getSFA_DOC_ID(), castOther.getSFA_DOC_ID())
            .append(this.getSFA_PARA_ID(), castOther.getSFA_PARA_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSFA_DOC_ID())
            .append(getSFA_PARA_ID())
            .toHashCode();
    }

}
