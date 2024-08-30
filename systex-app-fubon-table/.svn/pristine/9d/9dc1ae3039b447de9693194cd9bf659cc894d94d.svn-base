package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_LIFEITEMPK  implements Serializable  {

    /** identifier field */
    private BigDecimal ITEMSERIALNUM;

    /** identifier field */
    private BigDecimal PRODUCTSERIALNUM;

    /** full constructor */
    public TBJSB_INS_PROD_LIFEITEMPK(BigDecimal ITEMSERIALNUM, BigDecimal PRODUCTSERIALNUM) {
        this.ITEMSERIALNUM = ITEMSERIALNUM;
        this.PRODUCTSERIALNUM = PRODUCTSERIALNUM;
    }

    /** default constructor */
    public TBJSB_INS_PROD_LIFEITEMPK() {
    }

    public BigDecimal getITEMSERIALNUM() {
        return this.ITEMSERIALNUM;
    }

    public void setITEMSERIALNUM(BigDecimal ITEMSERIALNUM) {
        this.ITEMSERIALNUM = ITEMSERIALNUM;
    }

    public BigDecimal getPRODUCTSERIALNUM() {
        return this.PRODUCTSERIALNUM;
    }

    public void setPRODUCTSERIALNUM(BigDecimal PRODUCTSERIALNUM) {
        this.PRODUCTSERIALNUM = PRODUCTSERIALNUM;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ITEMSERIALNUM", getITEMSERIALNUM())
            .append("PRODUCTSERIALNUM", getPRODUCTSERIALNUM())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBJSB_INS_PROD_LIFEITEMPK) ) return false;
        TBJSB_INS_PROD_LIFEITEMPK castOther = (TBJSB_INS_PROD_LIFEITEMPK) other;
        return new EqualsBuilder()
            .append(this.getITEMSERIALNUM(), castOther.getITEMSERIALNUM())
            .append(this.getPRODUCTSERIALNUM(), castOther.getPRODUCTSERIALNUM())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getITEMSERIALNUM())
            .append(getPRODUCTSERIALNUM())
            .toHashCode();
    }

}
