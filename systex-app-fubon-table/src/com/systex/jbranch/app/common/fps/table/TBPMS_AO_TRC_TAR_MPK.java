package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_AO_TRC_TAR_MPK  implements Serializable  {

    /** identifier field */
    private String EMP_ID;

    /** identifier field */
    private String MAIN_PRD_ID;

    /** identifier field */
    private String YEARMON;

    /** full constructor */
    public TBPMS_AO_TRC_TAR_MPK(String EMP_ID, String MAIN_PRD_ID, String YEARMON) {
        this.EMP_ID = EMP_ID;
        this.MAIN_PRD_ID = MAIN_PRD_ID;
        this.YEARMON = YEARMON;
    }

    /** default constructor */
    public TBPMS_AO_TRC_TAR_MPK() {
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getMAIN_PRD_ID() {
        return this.MAIN_PRD_ID;
    }

    public void setMAIN_PRD_ID(String MAIN_PRD_ID) {
        this.MAIN_PRD_ID = MAIN_PRD_ID;
    }

    public String getYEARMON() {
        return this.YEARMON;
    }

    public void setYEARMON(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMP_ID", getEMP_ID())
            .append("MAIN_PRD_ID", getMAIN_PRD_ID())
            .append("YEARMON", getYEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_AO_TRC_TAR_MPK) ) return false;
        TBPMS_AO_TRC_TAR_MPK castOther = (TBPMS_AO_TRC_TAR_MPK) other;
        return new EqualsBuilder()
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getMAIN_PRD_ID(), castOther.getMAIN_PRD_ID())
            .append(this.getYEARMON(), castOther.getYEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEMP_ID())
            .append(getMAIN_PRD_ID())
            .append(getYEARMON())
            .toHashCode();
    }

}
