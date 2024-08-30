package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_PS_PIPELINEPK  implements Serializable  {

    /** identifier field */
    private String BRANCH_AREA_ID;

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String EMP_ID;

    /** identifier field */
    private BigDecimal PLAN_SEQ;

    /** identifier field */
    private String PLAN_YEARMON;

    /** full constructor */
    public TBPMS_PS_PIPELINEPK(String BRANCH_AREA_ID, String BRANCH_NBR, String CUST_ID, String EMP_ID, BigDecimal PLAN_SEQ, String PLAN_YEARMON) {
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_NBR = BRANCH_NBR;
        this.CUST_ID = CUST_ID;
        this.EMP_ID = EMP_ID;
        this.PLAN_SEQ = PLAN_SEQ;
        this.PLAN_YEARMON = PLAN_YEARMON;
    }

    /** default constructor */
    public TBPMS_PS_PIPELINEPK() {
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

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public BigDecimal getPLAN_SEQ() {
        return this.PLAN_SEQ;
    }

    public void setPLAN_SEQ(BigDecimal PLAN_SEQ) {
        this.PLAN_SEQ = PLAN_SEQ;
    }

    public String getPLAN_YEARMON() {
        return this.PLAN_YEARMON;
    }

    public void setPLAN_YEARMON(String PLAN_YEARMON) {
        this.PLAN_YEARMON = PLAN_YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_AREA_ID", getBRANCH_AREA_ID())
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("CUST_ID", getCUST_ID())
            .append("EMP_ID", getEMP_ID())
            .append("PLAN_SEQ", getPLAN_SEQ())
            .append("PLAN_YEARMON", getPLAN_YEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_PS_PIPELINEPK) ) return false;
        TBPMS_PS_PIPELINEPK castOther = (TBPMS_PS_PIPELINEPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_AREA_ID(), castOther.getBRANCH_AREA_ID())
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getPLAN_SEQ(), castOther.getPLAN_SEQ())
            .append(this.getPLAN_YEARMON(), castOther.getPLAN_YEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_AREA_ID())
            .append(getBRANCH_NBR())
            .append(getCUST_ID())
            .append(getEMP_ID())
            .append(getPLAN_SEQ())
            .append(getPLAN_YEARMON())
            .toHashCode();
    }

}
