package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_ASSET_PRINT_HISPK  implements Serializable  {

    /** identifier field */
    private Timestamp PRINT_DATE;

    /** identifier field */
    private BigDecimal SEQ;

    /** full constructor */
    public TBCRM_CUST_ASSET_PRINT_HISPK(Timestamp PRINT_DATE, BigDecimal SEQ) {
        this.PRINT_DATE = PRINT_DATE;
        this.SEQ = SEQ;
    }

    /** default constructor */
    public TBCRM_CUST_ASSET_PRINT_HISPK() {
    }

    public Timestamp getPRINT_DATE() {
        return this.PRINT_DATE;
    }

    public void setPRINT_DATE(Timestamp PRINT_DATE) {
        this.PRINT_DATE = PRINT_DATE;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRINT_DATE", getPRINT_DATE())
            .append("SEQ", getSEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_CUST_ASSET_PRINT_HISPK) ) return false;
        TBCRM_CUST_ASSET_PRINT_HISPK castOther = (TBCRM_CUST_ASSET_PRINT_HISPK) other;
        return new EqualsBuilder()
            .append(this.getPRINT_DATE(), castOther.getPRINT_DATE())
            .append(this.getSEQ(), castOther.getSEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRINT_DATE())
            .append(getSEQ())
            .toHashCode();
    }

}
