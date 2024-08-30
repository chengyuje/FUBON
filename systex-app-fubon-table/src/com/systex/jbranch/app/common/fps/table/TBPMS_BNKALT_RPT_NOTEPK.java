package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_BNKALT_RPT_NOTEPK  implements Serializable  {

    /** identifier field */
    private Timestamp ACCESS_TIME;

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String DEVICE_ID;

    /** identifier field */
    private String EMP_ID;

    /** full constructor */
    public TBPMS_BNKALT_RPT_NOTEPK(Timestamp ACCESS_TIME, String BRANCH_NBR, String DEVICE_ID, String EMP_ID) {
        this.ACCESS_TIME = ACCESS_TIME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.DEVICE_ID = DEVICE_ID;
        this.EMP_ID = EMP_ID;
    }

    /** default constructor */
    public TBPMS_BNKALT_RPT_NOTEPK() {
    }

    public Timestamp getACCESS_TIME() {
        return this.ACCESS_TIME;
    }

    public void setACCESS_TIME(Timestamp ACCESS_TIME) {
        this.ACCESS_TIME = ACCESS_TIME;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getDEVICE_ID() {
        return this.DEVICE_ID;
    }

    public void setDEVICE_ID(String DEVICE_ID) {
        this.DEVICE_ID = DEVICE_ID;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ACCESS_TIME", getACCESS_TIME())
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("DEVICE_ID", getDEVICE_ID())
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_BNKALT_RPT_NOTEPK) ) return false;
        TBPMS_BNKALT_RPT_NOTEPK castOther = (TBPMS_BNKALT_RPT_NOTEPK) other;
        return new EqualsBuilder()
            .append(this.getACCESS_TIME(), castOther.getACCESS_TIME())
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getDEVICE_ID(), castOther.getDEVICE_ID())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getACCESS_TIME())
            .append(getBRANCH_NBR())
            .append(getDEVICE_ID())
            .append(getEMP_ID())
            .toHashCode();
    }

}
