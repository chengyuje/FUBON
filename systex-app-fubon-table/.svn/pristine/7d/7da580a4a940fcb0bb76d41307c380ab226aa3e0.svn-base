package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_PORTFOLIO_PLAN_FILE_LOGPK  implements Serializable  {

    /** identifier field */
    private BigDecimal LOG_SEQ;

    /** identifier field */
    private String PLAN_ID;

    /** identifier field */
    private String PLAN_TYPE;

    /** identifier field */
    private BigDecimal SEQ_NO;

    /** full constructor */
    public TBFPS_PORTFOLIO_PLAN_FILE_LOGPK(BigDecimal LOG_SEQ, String PLAN_ID, String PLAN_TYPE, BigDecimal SEQ_NO) {
        this.LOG_SEQ = LOG_SEQ;
        this.PLAN_ID = PLAN_ID;
        this.PLAN_TYPE = PLAN_TYPE;
        this.SEQ_NO = SEQ_NO;
    }

    /** default constructor */
    public TBFPS_PORTFOLIO_PLAN_FILE_LOGPK() {
    }

    public BigDecimal getLOG_SEQ() {
        return this.LOG_SEQ;
    }

    public void setLOG_SEQ(BigDecimal LOG_SEQ) {
        this.LOG_SEQ = LOG_SEQ;
    }

    public String getPLAN_ID() {
        return this.PLAN_ID;
    }

    public void setPLAN_ID(String PLAN_ID) {
        this.PLAN_ID = PLAN_ID;
    }

    public String getPLAN_TYPE() {
        return this.PLAN_TYPE;
    }

    public void setPLAN_TYPE(String PLAN_TYPE) {
        this.PLAN_TYPE = PLAN_TYPE;
    }

    public BigDecimal getSEQ_NO() {
        return this.SEQ_NO;
    }

    public void setSEQ_NO(BigDecimal SEQ_NO) {
        this.SEQ_NO = SEQ_NO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("LOG_SEQ", getLOG_SEQ())
            .append("PLAN_ID", getPLAN_ID())
            .append("PLAN_TYPE", getPLAN_TYPE())
            .append("SEQ_NO", getSEQ_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_PORTFOLIO_PLAN_FILE_LOGPK) ) return false;
        TBFPS_PORTFOLIO_PLAN_FILE_LOGPK castOther = (TBFPS_PORTFOLIO_PLAN_FILE_LOGPK) other;
        return new EqualsBuilder()
            .append(this.getLOG_SEQ(), castOther.getLOG_SEQ())
            .append(this.getPLAN_ID(), castOther.getPLAN_ID())
            .append(this.getPLAN_TYPE(), castOther.getPLAN_TYPE())
            .append(this.getSEQ_NO(), castOther.getSEQ_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getLOG_SEQ())
            .append(getPLAN_ID())
            .append(getPLAN_TYPE())
            .append(getSEQ_NO())
            .toHashCode();
    }

}
