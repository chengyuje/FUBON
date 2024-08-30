package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_10CMDT_EBILL_COMFIRM_NYPK  implements Serializable  {

    /** identifier field */
    private String YYYYMM;

    /** identifier field */
    private String EMP_ID;


    /** full constructor */
    public TBPMS_10CMDT_EBILL_COMFIRM_NYPK(String YYYYMM, String EMP_ID) {
        this.YYYYMM = YYYYMM;
        this.EMP_ID = EMP_ID;
    }

    /** default constructor */
    public TBPMS_10CMDT_EBILL_COMFIRM_NYPK() {
    }

    public String getYYYYMM() {
        return this.YYYYMM;
    }

    public void setYYYYMM(String YYYYMM) {
        this.YYYYMM = YYYYMM;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("YYYYMM", getYYYYMM())
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_10CMDT_EBILL_COMFIRM_NYPK) ) return false;
        TBPMS_10CMDT_EBILL_COMFIRM_NYPK castOther = (TBPMS_10CMDT_EBILL_COMFIRM_NYPK) other;
        return new EqualsBuilder()
            .append(this.getYYYYMM(), castOther.getYYYYMM())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getYYYYMM())
            .append(getEMP_ID())
            .toHashCode();
    }

}
