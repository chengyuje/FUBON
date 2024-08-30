package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PARA_LTCAREPK  implements Serializable  {

    /** identifier field */
    private BigDecimal LT_KEYNO;

    /** identifier field */
    private BigDecimal PARA_NO;

    /** full constructor */
    public TBINS_PARA_LTCAREPK(BigDecimal LT_KEYNO, BigDecimal PARA_NO) {
        this.LT_KEYNO = LT_KEYNO;
        this.PARA_NO = PARA_NO;
    }

    /** default constructor */
    public TBINS_PARA_LTCAREPK() {
    }

    public BigDecimal getLT_KEYNO() {
        return this.LT_KEYNO;
    }

    public void setLT_KEYNO(BigDecimal LT_KEYNO) {
        this.LT_KEYNO = LT_KEYNO;
    }

    public BigDecimal getPARA_NO() {
        return this.PARA_NO;
    }

    public void setPARA_NO(BigDecimal PARA_NO) {
        this.PARA_NO = PARA_NO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("LT_KEYNO", getLT_KEYNO())
            .append("PARA_NO", getPARA_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBINS_PARA_LTCAREPK) ) return false;
        TBINS_PARA_LTCAREPK castOther = (TBINS_PARA_LTCAREPK) other;
        return new EqualsBuilder()
            .append(this.getLT_KEYNO(), castOther.getLT_KEYNO())
            .append(this.getPARA_NO(), castOther.getPARA_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getLT_KEYNO())
            .append(getPARA_NO())
            .toHashCode();
    }

}
