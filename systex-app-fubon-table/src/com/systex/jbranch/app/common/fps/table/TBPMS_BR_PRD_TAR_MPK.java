package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_BR_PRD_TAR_MPK  implements Serializable  {

    /** identifier field */
    private String BRANCH_AREA_ID;

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String DATA_YEARMON;

    /** identifier field */
    private String REGION_CENTER_ID;

    /** full constructor */
    public TBPMS_BR_PRD_TAR_MPK(String BRANCH_AREA_ID, String BRANCH_NBR, String DATA_YEARMON, String REGION_CENTER_ID) {
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_NBR = BRANCH_NBR;
        this.DATA_YEARMON = DATA_YEARMON;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
    }

    /** default constructor */
    public TBPMS_BR_PRD_TAR_MPK() {
    }

    public String getBRANCH_AREA_ID() {
        return this.BRANCH_AREA_ID;
    }

    public void setBRANCH_AREA_ID(String BRANCH_AREA_ID) {
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getDATA_YEARMON() {
        return this.DATA_YEARMON;
    }

    public void setDATA_YEARMON(String DATA_YEARMON) {
        this.DATA_YEARMON = DATA_YEARMON;
    }

    public String getREGION_CENTER_ID() {
        return this.REGION_CENTER_ID;
    }

    public void setREGION_CENTER_ID(String REGION_CENTER_ID) {
        this.REGION_CENTER_ID = REGION_CENTER_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_AREA_ID", getBRANCH_AREA_ID())
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("DATA_YEARMON", getDATA_YEARMON())
            .append("REGION_CENTER_ID", getREGION_CENTER_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_BR_PRD_TAR_MPK) ) return false;
        TBPMS_BR_PRD_TAR_MPK castOther = (TBPMS_BR_PRD_TAR_MPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_AREA_ID(), castOther.getBRANCH_AREA_ID())
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getDATA_YEARMON(), castOther.getDATA_YEARMON())
            .append(this.getREGION_CENTER_ID(), castOther.getREGION_CENTER_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_AREA_ID())
            .append(getBRANCH_NBR())
            .append(getDATA_YEARMON())
            .append(getREGION_CENTER_ID())
            .toHashCode();
    }

}
