package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_SPP_PRD_RETURNPK  implements Serializable  {

    /** identifier field */
    private String CURRENCY_TYPE;

    /** identifier field */
    private String PLAN_ID;

    /** identifier field */
    private String PRD_ID;

    /** full constructor */
    public TBFPS_SPP_PRD_RETURNPK(String CURRENCY_TYPE, String PLAN_ID, String PRD_ID) {
        this.CURRENCY_TYPE = CURRENCY_TYPE;
        this.PLAN_ID = PLAN_ID;
        this.PRD_ID = PRD_ID;
    }

    /** default constructor */
    public TBFPS_SPP_PRD_RETURNPK() {
    }

    public String getCURRENCY_TYPE() {
        return this.CURRENCY_TYPE;
    }

    public void setCURRENCY_TYPE(String CURRENCY_TYPE) {
        this.CURRENCY_TYPE = CURRENCY_TYPE;
    }

    public String getPLAN_ID() {
        return this.PLAN_ID;
    }

    public void setPLAN_ID(String PLAN_ID) {
        this.PLAN_ID = PLAN_ID;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CURRENCY_TYPE", getCURRENCY_TYPE())
            .append("PLAN_ID", getPLAN_ID())
            .append("PRD_ID", getPRD_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_SPP_PRD_RETURNPK) ) return false;
        TBFPS_SPP_PRD_RETURNPK castOther = (TBFPS_SPP_PRD_RETURNPK) other;
        return new EqualsBuilder()
            .append(this.getCURRENCY_TYPE(), castOther.getCURRENCY_TYPE())
            .append(this.getPLAN_ID(), castOther.getPLAN_ID())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCURRENCY_TYPE())
            .append(getPLAN_ID())
            .append(getPRD_ID())
            .toHashCode();
    }

}
