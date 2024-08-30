package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_PS_SA_INS_FUND_SGPK  implements Serializable  {

    /** identifier field */
    private String CLASS;

    /** identifier field */
    private String FUNDID;

    /** full constructor */
    public TBORG_PS_SA_INS_FUND_SGPK(String CLASS, String FUNDID) {
        this.CLASS = CLASS;
        this.FUNDID = FUNDID;
    }

    /** default constructor */
    public TBORG_PS_SA_INS_FUND_SGPK() {
    }

    public String getCLASS() {
        return this.CLASS;
    }

    public void setCLASS(String CLASS) {
        this.CLASS = CLASS;
    }

    public String getFUNDID() {
        return this.FUNDID;
    }

    public void setFUNDID(String FUNDID) {
        this.FUNDID = FUNDID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CLASS", getCLASS())
            .append("FUNDID", getFUNDID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBORG_PS_SA_INS_FUND_SGPK) ) return false;
        TBORG_PS_SA_INS_FUND_SGPK castOther = (TBORG_PS_SA_INS_FUND_SGPK) other;
        return new EqualsBuilder()
            .append(this.getCLASS(), castOther.getCLASS())
            .append(this.getFUNDID(), castOther.getFUNDID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCLASS())
            .append(getFUNDID())
            .toHashCode();
    }

}
