package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_10CMDT_PLAN_AO_LISTPK  implements Serializable  {

    /** identifier field */
    private String EMP_ID;

    /** identifier field */
    private String PRJ_CODE;

    /** full constructor */
    public TBCRM_10CMDT_PLAN_AO_LISTPK(String EMP_ID, String PRJ_CODE) {
        this.EMP_ID = EMP_ID;
        this.PRJ_CODE = PRJ_CODE;
    }

    /** default constructor */
    public TBCRM_10CMDT_PLAN_AO_LISTPK() {
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getPRJ_CODE() {
        return this.PRJ_CODE;
    }

    public void setPRJ_CODE(String PRJ_CODE) {
        this.PRJ_CODE = PRJ_CODE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMP_ID", getEMP_ID())
            .append("PRJ_CODE", getPRJ_CODE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_10CMDT_PLAN_AO_LISTPK) ) return false;
        TBCRM_10CMDT_PLAN_AO_LISTPK castOther = (TBCRM_10CMDT_PLAN_AO_LISTPK) other;
        return new EqualsBuilder()
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getPRJ_CODE(), castOther.getPRJ_CODE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEMP_ID())
            .append(getPRJ_CODE())
            .toHashCode();
    }

}
