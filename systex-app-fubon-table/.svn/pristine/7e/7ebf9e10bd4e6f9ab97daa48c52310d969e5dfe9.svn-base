package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSOT_BN_TRADE_DPK  implements Serializable  {

    /** identifier field */
    private BigDecimal SEQ_NO;

    /** identifier field */
    private String TRADE_SEQ;

    /** full constructor */
    public TBSOT_BN_TRADE_DPK(BigDecimal SEQ_NO, String TRADE_SEQ) {
        this.SEQ_NO = SEQ_NO;
        this.TRADE_SEQ = TRADE_SEQ;
    }

    /** default constructor */
    public TBSOT_BN_TRADE_DPK() {
    }

    public BigDecimal getSEQ_NO() {
        return this.SEQ_NO;
    }

    public void setSEQ_NO(BigDecimal SEQ_NO) {
        this.SEQ_NO = SEQ_NO;
    }

    public String getTRADE_SEQ() {
        return this.TRADE_SEQ;
    }

    public void setTRADE_SEQ(String TRADE_SEQ) {
        this.TRADE_SEQ = TRADE_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ_NO", getSEQ_NO())
            .append("TRADE_SEQ", getTRADE_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSOT_BN_TRADE_DPK) ) return false;
        TBSOT_BN_TRADE_DPK castOther = (TBSOT_BN_TRADE_DPK) other;
        return new EqualsBuilder()
            .append(this.getSEQ_NO(), castOther.getSEQ_NO())
            .append(this.getTRADE_SEQ(), castOther.getTRADE_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSEQ_NO())
            .append(getTRADE_SEQ())
            .toHashCode();
    }

}
