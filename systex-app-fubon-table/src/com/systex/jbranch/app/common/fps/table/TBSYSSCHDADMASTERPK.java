package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSSCHDADMASTERPK  implements Serializable  {

    /** identifier field */
    private BigDecimal AUDITID;

    /** identifier field */
    private String SCHEDULEID;

    /** full constructor */
    public TBSYSSCHDADMASTERPK(BigDecimal AUDITID, String SCHEDULEID) {
        this.AUDITID = AUDITID;
        this.SCHEDULEID = SCHEDULEID;
    }

    /** default constructor */
    public TBSYSSCHDADMASTERPK() {
    }

    public BigDecimal getAUDITID() {
        return this.AUDITID;
    }

    public void setAUDITID(BigDecimal AUDITID) {
        this.AUDITID = AUDITID;
    }

    public String getSCHEDULEID() {
        return this.SCHEDULEID;
    }

    public void setSCHEDULEID(String SCHEDULEID) {
        this.SCHEDULEID = SCHEDULEID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AUDITID", getAUDITID())
            .append("SCHEDULEID", getSCHEDULEID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSSCHDADMASTERPK) ) return false;
        TBSYSSCHDADMASTERPK castOther = (TBSYSSCHDADMASTERPK) other;
        return new EqualsBuilder()
            .append(this.getAUDITID(), castOther.getAUDITID())
            .append(this.getSCHEDULEID(), castOther.getSCHEDULEID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAUDITID())
            .append(getSCHEDULEID())
            .toHashCode();
    }

}
