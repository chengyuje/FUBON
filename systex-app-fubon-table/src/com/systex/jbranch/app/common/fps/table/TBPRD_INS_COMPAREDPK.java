package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_COMPAREDPK  implements Serializable  {

    /** identifier field */
    private String INSDATA_PRD_ID;

    /** identifier field */
    private BigDecimal KEY_NO;

    /** full constructor */
    public TBPRD_INS_COMPAREDPK(String INSDATA_PRD_ID, BigDecimal KEY_NO) {
        this.INSDATA_PRD_ID = INSDATA_PRD_ID;
        this.KEY_NO = KEY_NO;
    }

    /** default constructor */
    public TBPRD_INS_COMPAREDPK() {
    }

    public String getINSDATA_PRD_ID() {
        return this.INSDATA_PRD_ID;
    }

    public void setINSDATA_PRD_ID(String INSDATA_PRD_ID) {
        this.INSDATA_PRD_ID = INSDATA_PRD_ID;
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INSDATA_PRD_ID", getINSDATA_PRD_ID())
            .append("KEY_NO", getKEY_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_INS_COMPAREDPK) ) return false;
        TBPRD_INS_COMPAREDPK castOther = (TBPRD_INS_COMPAREDPK) other;
        return new EqualsBuilder()
            .append(this.getINSDATA_PRD_ID(), castOther.getINSDATA_PRD_ID())
            .append(this.getKEY_NO(), castOther.getKEY_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getINSDATA_PRD_ID())
            .append(getKEY_NO())
            .toHashCode();
    }

}
