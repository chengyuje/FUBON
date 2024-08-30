package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_RANK_INS_TARGETPK  implements Serializable  {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** identifier field */
    private BigDecimal SEQ;

    /** full constructor */
    public TBPRD_RANK_INS_TARGETPK(BigDecimal KEY_NO, BigDecimal SEQ) {
        this.KEY_NO = KEY_NO;
        this.SEQ = SEQ;
    }

    /** default constructor */
    public TBPRD_RANK_INS_TARGETPK() {
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO", getKEY_NO())
            .append("SEQ", getSEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_RANK_INS_TARGETPK) ) return false;
        TBPRD_RANK_INS_TARGETPK castOther = (TBPRD_RANK_INS_TARGETPK) other;
        return new EqualsBuilder()
            .append(this.getKEY_NO(), castOther.getKEY_NO())
            .append(this.getSEQ(), castOther.getSEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getKEY_NO())
            .append(getSEQ())
            .toHashCode();
    }

}
