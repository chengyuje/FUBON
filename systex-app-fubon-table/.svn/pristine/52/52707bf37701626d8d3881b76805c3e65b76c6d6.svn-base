package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_IVG_PLAN_CONTENTPK  implements Serializable  {

    /** identifier field */
    private String EMP_ID;

    /** identifier field */
    private BigDecimal IVG_PLAN_SEQ;

    /** identifier field */
    private BigDecimal IVG_RESULT_SEQ;

    /** full constructor */
    public TBCAM_IVG_PLAN_CONTENTPK(String EMP_ID, BigDecimal IVG_PLAN_SEQ, BigDecimal IVG_RESULT_SEQ) {
        this.EMP_ID = EMP_ID;
        this.IVG_PLAN_SEQ = IVG_PLAN_SEQ;
        this.IVG_RESULT_SEQ = IVG_RESULT_SEQ;
    }

    /** default constructor */
    public TBCAM_IVG_PLAN_CONTENTPK() {
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public BigDecimal getIVG_PLAN_SEQ() {
        return this.IVG_PLAN_SEQ;
    }

    public void setIVG_PLAN_SEQ(BigDecimal IVG_PLAN_SEQ) {
        this.IVG_PLAN_SEQ = IVG_PLAN_SEQ;
    }

    public BigDecimal getIVG_RESULT_SEQ() {
        return this.IVG_RESULT_SEQ;
    }

    public void setIVG_RESULT_SEQ(BigDecimal IVG_RESULT_SEQ) {
        this.IVG_RESULT_SEQ = IVG_RESULT_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMP_ID", getEMP_ID())
            .append("IVG_PLAN_SEQ", getIVG_PLAN_SEQ())
            .append("IVG_RESULT_SEQ", getIVG_RESULT_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_IVG_PLAN_CONTENTPK) ) return false;
        TBCAM_IVG_PLAN_CONTENTPK castOther = (TBCAM_IVG_PLAN_CONTENTPK) other;
        return new EqualsBuilder()
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getIVG_PLAN_SEQ(), castOther.getIVG_PLAN_SEQ())
            .append(this.getIVG_RESULT_SEQ(), castOther.getIVG_RESULT_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEMP_ID())
            .append(getIVG_PLAN_SEQ())
            .append(getIVG_RESULT_SEQ())
            .toHashCode();
    }

}
