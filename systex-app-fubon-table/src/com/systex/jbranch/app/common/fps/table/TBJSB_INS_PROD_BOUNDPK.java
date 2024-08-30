package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_BOUNDPK  implements Serializable  {

    /** identifier field */
    private String BOUNDITEM;

    /** identifier field */
    private String BOUNDNAME;

    /** identifier field */
    private String CHANNEL;

    /** identifier field */
    private BigDecimal PRODUCTSERIALNUM;

    /** full constructor */
    public TBJSB_INS_PROD_BOUNDPK(String BOUNDITEM, String BOUNDNAME, String CHANNEL, BigDecimal PRODUCTSERIALNUM) {
        this.BOUNDITEM = BOUNDITEM;
        this.BOUNDNAME = BOUNDNAME;
        this.CHANNEL = CHANNEL;
        this.PRODUCTSERIALNUM = PRODUCTSERIALNUM;
    }

    /** default constructor */
    public TBJSB_INS_PROD_BOUNDPK() {
    }

    public String getBOUNDITEM() {
        return this.BOUNDITEM;
    }

    public void setBOUNDITEM(String BOUNDITEM) {
        this.BOUNDITEM = BOUNDITEM;
    }

    public String getBOUNDNAME() {
        return this.BOUNDNAME;
    }

    public void setBOUNDNAME(String BOUNDNAME) {
        this.BOUNDNAME = BOUNDNAME;
    }

    public String getCHANNEL() {
        return this.CHANNEL;
    }

    public void setCHANNEL(String CHANNEL) {
        this.CHANNEL = CHANNEL;
    }

    public BigDecimal getPRODUCTSERIALNUM() {
        return this.PRODUCTSERIALNUM;
    }

    public void setPRODUCTSERIALNUM(BigDecimal PRODUCTSERIALNUM) {
        this.PRODUCTSERIALNUM = PRODUCTSERIALNUM;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BOUNDITEM", getBOUNDITEM())
            .append("BOUNDNAME", getBOUNDNAME())
            .append("CHANNEL", getCHANNEL())
            .append("PRODUCTSERIALNUM", getPRODUCTSERIALNUM())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBJSB_INS_PROD_BOUNDPK) ) return false;
        TBJSB_INS_PROD_BOUNDPK castOther = (TBJSB_INS_PROD_BOUNDPK) other;
        return new EqualsBuilder()
            .append(this.getBOUNDITEM(), castOther.getBOUNDITEM())
            .append(this.getBOUNDNAME(), castOther.getBOUNDNAME())
            .append(this.getCHANNEL(), castOther.getCHANNEL())
            .append(this.getPRODUCTSERIALNUM(), castOther.getPRODUCTSERIALNUM())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBOUNDITEM())
            .append(getBOUNDNAME())
            .append(getCHANNEL())
            .append(getPRODUCTSERIALNUM())
            .toHashCode();
    }

}
