package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_CALENDARPK  implements Serializable  {

    /** identifier field */
    private String CAL_TYPE;

    /** identifier field */
    private Timestamp CUS_DATE;

    /** identifier field */
    private String PRD_ID;

    /** identifier field */
    private String PRD_TYPE;

    /** full constructor */
    public TBPRD_CALENDARPK(String CAL_TYPE, Timestamp CUS_DATE, String PRD_ID, String PRD_TYPE) {
        this.CAL_TYPE = CAL_TYPE;
        this.CUS_DATE = CUS_DATE;
        this.PRD_ID = PRD_ID;
        this.PRD_TYPE = PRD_TYPE;
    }

    /** default constructor */
    public TBPRD_CALENDARPK() {
    }

    public String getCAL_TYPE() {
        return this.CAL_TYPE;
    }

    public void setCAL_TYPE(String CAL_TYPE) {
        this.CAL_TYPE = CAL_TYPE;
    }

    public Timestamp getCUS_DATE() {
        return this.CUS_DATE;
    }

    public void setCUS_DATE(Timestamp CUS_DATE) {
        this.CUS_DATE = CUS_DATE;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getPRD_TYPE() {
        return this.PRD_TYPE;
    }

    public void setPRD_TYPE(String PRD_TYPE) {
        this.PRD_TYPE = PRD_TYPE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CAL_TYPE", getCAL_TYPE())
            .append("CUS_DATE", getCUS_DATE())
            .append("PRD_ID", getPRD_ID())
            .append("PRD_TYPE", getPRD_TYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_CALENDARPK) ) return false;
        TBPRD_CALENDARPK castOther = (TBPRD_CALENDARPK) other;
        return new EqualsBuilder()
            .append(this.getCAL_TYPE(), castOther.getCAL_TYPE())
            .append(this.getCUS_DATE(), castOther.getCUS_DATE())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getPRD_TYPE(), castOther.getPRD_TYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCAL_TYPE())
            .append(getCUS_DATE())
            .append(getPRD_ID())
            .append(getPRD_TYPE())
            .toHashCode();
    }

}
