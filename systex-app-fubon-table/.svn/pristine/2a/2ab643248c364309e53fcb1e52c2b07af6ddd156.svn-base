package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_INS_TARGET_SETPK  implements Serializable  {

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String YEARMON;

    /** full constructor */
    public TBPMS_INS_TARGET_SETPK(String BRANCH_NBR, String YEARMON) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.YEARMON = YEARMON;
    }

    /** default constructor */
    public TBPMS_INS_TARGET_SETPK() {
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getYEARMON() {
        return this.YEARMON;
    }

    public void setYEARMON(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("YEARMON", getYEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_INS_TARGET_SETPK) ) return false;
        TBPMS_INS_TARGET_SETPK castOther = (TBPMS_INS_TARGET_SETPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getYEARMON(), castOther.getYEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getYEARMON())
            .toHashCode();
    }

}
