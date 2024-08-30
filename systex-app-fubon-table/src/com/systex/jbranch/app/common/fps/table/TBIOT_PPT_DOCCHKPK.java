package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_PPT_DOCCHKPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private BigDecimal DOC_SEQ;

    /** identifier field */
    private BigDecimal INS_KEYNO;

    /** full constructor */
    public TBIOT_PPT_DOCCHKPK(BigDecimal DOC_SEQ, BigDecimal INS_KEYNO) {
        this.DOC_SEQ = DOC_SEQ;
        this.INS_KEYNO = INS_KEYNO;
    }

    /** default constructor */
    public TBIOT_PPT_DOCCHKPK() {
    }

    public BigDecimal getDOC_SEQ() {
        return this.DOC_SEQ;
    }

    public void setDOC_SEQ(BigDecimal DOC_SEQ) {
        this.DOC_SEQ = DOC_SEQ;
    }

    public BigDecimal getINS_KEYNO() {
        return this.INS_KEYNO;
    }

    public void setINS_KEYNO(BigDecimal INS_KEYNO) {
        this.INS_KEYNO = INS_KEYNO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DOC_SEQ", getDOC_SEQ())
            .append("INS_KEYNO", getINS_KEYNO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBIOT_PPT_DOCCHKPK) ) return false;
        TBIOT_PPT_DOCCHKPK castOther = (TBIOT_PPT_DOCCHKPK) other;
        return new EqualsBuilder()
            .append(this.getDOC_SEQ(), castOther.getDOC_SEQ())
            .append(this.getINS_KEYNO(), castOther.getINS_KEYNO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDOC_SEQ())
            .append(getINS_KEYNO())
            .toHashCode();
    }

}
