package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSPARAMETERPK  implements Serializable  {

    /** identifier field */
    private String PARAM_TYPE;

    /** identifier field */
    private String PARAM_CODE;

    /** full constructor */
    public TBSYSPARAMETERPK(String PARAM_TYPE, String PARAM_CODE) {
        this.PARAM_TYPE = PARAM_TYPE;
        this.PARAM_CODE = PARAM_CODE;
    }

    /** default constructor */
    public TBSYSPARAMETERPK() {
    }

    public String getPARAM_TYPE() {
        return this.PARAM_TYPE;
    }

    public void setPARAM_TYPE(String PARAM_TYPE) {
        this.PARAM_TYPE = PARAM_TYPE;
    }

    public String getPARAM_CODE() {
        return this.PARAM_CODE;
    }

    public void setPARAM_CODE(String PARAM_CODE) {
        this.PARAM_CODE = PARAM_CODE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PARAM_TYPE", getPARAM_TYPE())
            .append("PARAM_CODE", getPARAM_CODE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSPARAMETERPK) ) return false;
        TBSYSPARAMETERPK castOther = (TBSYSPARAMETERPK) other;
        return new EqualsBuilder()
            .append(this.getPARAM_TYPE(), castOther.getPARAM_TYPE())
            .append(this.getPARAM_CODE(), castOther.getPARAM_CODE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPARAM_TYPE())
            .append(getPARAM_CODE())
            .toHashCode();
    }

}
