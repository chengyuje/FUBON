package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSOVERPRINTBOOKMARKPK  implements Serializable  {

    /** identifier field */
    private String DID;

    /** identifier field */
    private String NAME;

    /** full constructor */
    public TBSYSOVERPRINTBOOKMARKPK(String DID, String NAME) {
        this.DID = DID;
        this.NAME = NAME;
    }

    /** default constructor */
    public TBSYSOVERPRINTBOOKMARKPK() {
    }

    public String getDID() {
        return this.DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }

    public String getNAME() {
        return this.NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DID", getDID())
            .append("NAME", getNAME())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSOVERPRINTBOOKMARKPK) ) return false;
        TBSYSOVERPRINTBOOKMARKPK castOther = (TBSYSOVERPRINTBOOKMARKPK) other;
        return new EqualsBuilder()
            .append(this.getDID(), castOther.getDID())
            .append(this.getNAME(), castOther.getNAME())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDID())
            .append(getNAME())
            .toHashCode();
    }

}
