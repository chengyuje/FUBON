package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSOVERPRINTBFASSPK  implements Serializable  {

    /** identifier field */
    private String BNAME;

    /** identifier field */
    private String DID;

    /** identifier field */
    private BigDecimal FID;

    /** full constructor */
    public TBSYSOVERPRINTBFASSPK(String BNAME, String DID, BigDecimal FID) {
        this.BNAME = BNAME;
        this.DID = DID;
        this.FID = FID;
    }

    /** default constructor */
    public TBSYSOVERPRINTBFASSPK() {
    }

    public String getBNAME() {
        return this.BNAME;
    }

    public void setBNAME(String BNAME) {
        this.BNAME = BNAME;
    }

    public String getDID() {
        return this.DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }

    public BigDecimal getFID() {
        return this.FID;
    }

    public void setFID(BigDecimal FID) {
        this.FID = FID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BNAME", getBNAME())
            .append("DID", getDID())
            .append("FID", getFID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSOVERPRINTBFASSPK) ) return false;
        TBSYSOVERPRINTBFASSPK castOther = (TBSYSOVERPRINTBFASSPK) other;
        return new EqualsBuilder()
            .append(this.getBNAME(), castOther.getBNAME())
            .append(this.getDID(), castOther.getDID())
            .append(this.getFID(), castOther.getFID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBNAME())
            .append(getDID())
            .append(getFID())
            .toHashCode();
    }

}
