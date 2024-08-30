package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_SEC_ITMFUNASSPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String ITEMID;

    /** identifier field */
    private String FUNCTIONID;

    /** full constructor */
    public TBFPS_SEC_ITMFUNASSPK(String ITEMID, String FUNCTIONID) {
        this.ITEMID = ITEMID;
        this.FUNCTIONID = FUNCTIONID;
    }

    /** default constructor */
    public TBFPS_SEC_ITMFUNASSPK() {
    }

    public String getITEMID() {
        return this.ITEMID;
    }

    public void setITEMID(String ITEMID) {
        this.ITEMID = ITEMID;
    }

    public String getFUNCTIONID() {
        return this.FUNCTIONID;
    }

    public void setFUNCTIONID(String FUNCTIONID) {
        this.FUNCTIONID = FUNCTIONID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ITEMID", getITEMID())
            .append("FUNCTIONID", getFUNCTIONID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_SEC_ITMFUNASSPK) ) return false;
        TBFPS_SEC_ITMFUNASSPK castOther = (TBFPS_SEC_ITMFUNASSPK) other;
        return new EqualsBuilder()
            .append(this.getITEMID(), castOther.getITEMID())
            .append(this.getFUNCTIONID(), castOther.getFUNCTIONID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getITEMID())
            .append(getFUNCTIONID())
            .toHashCode();
    }

}
