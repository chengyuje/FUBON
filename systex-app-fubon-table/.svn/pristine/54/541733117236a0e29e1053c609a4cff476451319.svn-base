package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MON_AST_INFOPK  implements Serializable  {

    /** identifier field */
    private String INFO_TYPE;

    /** identifier field */
    private String PRD_ID;

    /** identifier field */
    private String YEARMON;

    /** full constructor */
    public TBPMS_MON_AST_INFOPK(String INFO_TYPE, String PRD_ID, String YEARMON) {
        this.INFO_TYPE = INFO_TYPE;
        this.PRD_ID = PRD_ID;
        this.YEARMON = YEARMON;
    }

    /** default constructor */
    public TBPMS_MON_AST_INFOPK() {
    }

    public String getINFO_TYPE() {
        return this.INFO_TYPE;
    }

    public void setINFO_TYPE(String INFO_TYPE) {
        this.INFO_TYPE = INFO_TYPE;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getYEARMON() {
        return this.YEARMON;
    }

    public void setYEARMON(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INFO_TYPE", getINFO_TYPE())
            .append("PRD_ID", getPRD_ID())
            .append("YEARMON", getYEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_MON_AST_INFOPK) ) return false;
        TBPMS_MON_AST_INFOPK castOther = (TBPMS_MON_AST_INFOPK) other;
        return new EqualsBuilder()
            .append(this.getINFO_TYPE(), castOther.getINFO_TYPE())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getYEARMON(), castOther.getYEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getINFO_TYPE())
            .append(getPRD_ID())
            .append(getYEARMON())
            .toHashCode();
    }

}
