package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;







/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_ETF_CLOSING_PRICEPK  implements Serializable  {

    /** identifier field */
    private String PRODUCT_NO;

    /** identifier field */
    private Timestamp SOU_DATE;

    /** full constructor */
    public TBPRD_ETF_CLOSING_PRICEPK(String PRODUCT_NO, Timestamp SOU_DATE) {
        this.PRODUCT_NO = PRODUCT_NO;
        this.SOU_DATE = SOU_DATE;
    }

    /** default constructor */
    public TBPRD_ETF_CLOSING_PRICEPK() {
    }

    public String getPRODUCT_NO() {
        return this.PRODUCT_NO;
    }

    public void setPRODUCT_NO(String PRODUCT_NO) {
        this.PRODUCT_NO = PRODUCT_NO;
    }

    public Timestamp getSOU_DATE() {
        return this.SOU_DATE;
    }

    public void setSOU_DATE(Timestamp SOU_DATE) {
        this.SOU_DATE = SOU_DATE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRODUCT_NO", getPRODUCT_NO())
            .append("SOU_DATE", getSOU_DATE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_ETF_CLOSING_PRICEPK) ) return false;
        TBPRD_ETF_CLOSING_PRICEPK castOther = (TBPRD_ETF_CLOSING_PRICEPK) other;
        return new EqualsBuilder()
            .append(this.getPRODUCT_NO(), castOther.getPRODUCT_NO())
            .append(this.getSOU_DATE(), castOther.getSOU_DATE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRODUCT_NO())
            .append(getSOU_DATE())
            .toHashCode();
    }

}
