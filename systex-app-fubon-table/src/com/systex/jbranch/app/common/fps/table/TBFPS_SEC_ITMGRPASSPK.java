package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_SEC_ITMGRPASSPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String GROUP_ID;

    /** identifier field */
    private String ITEMID;

    /** full constructor */
    public TBFPS_SEC_ITMGRPASSPK(String GROUP_ID, String ITEMID) {
        this.GROUP_ID = GROUP_ID;
        this.ITEMID = ITEMID;
    }

    /** default constructor */
    public TBFPS_SEC_ITMGRPASSPK() {
    }

    public String getGROUP_ID() {
        return this.GROUP_ID;
    }

    public void setGROUP_ID(String GROUP_ID) {
        this.GROUP_ID = GROUP_ID;
    }

    public String getITEMID() {
        return this.ITEMID;
    }

    public void setITEMID(String ITEMID) {
        this.ITEMID = ITEMID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("GROUP_ID", getGROUP_ID())
            .append("ITEMID", getITEMID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_SEC_ITMGRPASSPK) ) return false;
        TBFPS_SEC_ITMGRPASSPK castOther = (TBFPS_SEC_ITMGRPASSPK) other;
        return new EqualsBuilder()
            .append(this.getGROUP_ID(), castOther.getGROUP_ID())
            .append(this.getITEMID(), castOther.getITEMID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getGROUP_ID())
            .append(getITEMID())
            .toHashCode();
    }

}
