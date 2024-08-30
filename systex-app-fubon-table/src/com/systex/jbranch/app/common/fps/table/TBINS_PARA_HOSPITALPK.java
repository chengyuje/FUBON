package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PARA_HOSPITALPK  implements Serializable  {

    /** identifier field */
    private BigDecimal H_KEYNO;

    /** identifier field */
    private BigDecimal PARA_NO;

    /** full constructor */
    public TBINS_PARA_HOSPITALPK(BigDecimal H_KEYNO, BigDecimal PARA_NO) {
        this.H_KEYNO = H_KEYNO;
        this.PARA_NO = PARA_NO;
    }

    /** default constructor */
    public TBINS_PARA_HOSPITALPK() {
    }

    public BigDecimal getH_KEYNO() {
        return this.H_KEYNO;
    }

    public void setH_KEYNO(BigDecimal H_KEYNO) {
        this.H_KEYNO = H_KEYNO;
    }

    public BigDecimal getPARA_NO() {
        return this.PARA_NO;
    }

    public void setPARA_NO(BigDecimal PARA_NO) {
        this.PARA_NO = PARA_NO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("H_KEYNO", getH_KEYNO())
            .append("PARA_NO", getPARA_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBINS_PARA_HOSPITALPK) ) return false;
        TBINS_PARA_HOSPITALPK castOther = (TBINS_PARA_HOSPITALPK) other;
        return new EqualsBuilder()
            .append(this.getH_KEYNO(), castOther.getH_KEYNO())
            .append(this.getPARA_NO(), castOther.getPARA_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getH_KEYNO())
            .append(getPARA_NO())
            .toHashCode();
    }

}
