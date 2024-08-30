package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_TRACK_PRO_SETPK  implements Serializable  {

    /** identifier field */
    private String DATA_YEARMON;

    /** identifier field */
    private String MAIN_COM_NBR;

    /** identifier field */
    private String PRD_ID;

    /** full constructor */
    public TBPMS_TRACK_PRO_SETPK(String DATA_YEARMON, String MAIN_COM_NBR, String PRD_ID) {
        this.DATA_YEARMON = DATA_YEARMON;
        this.MAIN_COM_NBR = MAIN_COM_NBR;
        this.PRD_ID = PRD_ID;
    }

    /** default constructor */
    public TBPMS_TRACK_PRO_SETPK() {
    }

    public String getDATA_YEARMON() {
        return this.DATA_YEARMON;
    }

    public void setDATA_YEARMON(String DATA_YEARMON) {
        this.DATA_YEARMON = DATA_YEARMON;
    }

    public String getMAIN_COM_NBR() {
        return this.MAIN_COM_NBR;
    }

    public void setMAIN_COM_NBR(String MAIN_COM_NBR) {
        this.MAIN_COM_NBR = MAIN_COM_NBR;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DATA_YEARMON", getDATA_YEARMON())
            .append("MAIN_COM_NBR", getMAIN_COM_NBR())
            .append("PRD_ID", getPRD_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_TRACK_PRO_SETPK) ) return false;
        TBPMS_TRACK_PRO_SETPK castOther = (TBPMS_TRACK_PRO_SETPK) other;
        return new EqualsBuilder()
            .append(this.getDATA_YEARMON(), castOther.getDATA_YEARMON())
            .append(this.getMAIN_COM_NBR(), castOther.getMAIN_COM_NBR())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDATA_YEARMON())
            .append(getMAIN_COM_NBR())
            .append(getPRD_ID())
            .toHashCode();
    }

}
