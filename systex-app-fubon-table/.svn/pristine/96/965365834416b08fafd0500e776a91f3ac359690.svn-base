package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_CUSTRISK_VOLATILITYPK  implements Serializable  {

    /** identifier field */
    private String CUST_RISK_ATR;

    /** identifier field */
    private String PARAM_NO;

    /** full constructor */
    public TBFPS_CUSTRISK_VOLATILITYPK(String CUST_RISK_ATR, String PARAM_NO) {
        this.CUST_RISK_ATR = CUST_RISK_ATR;
        this.PARAM_NO = PARAM_NO;
    }

    /** default constructor */
    public TBFPS_CUSTRISK_VOLATILITYPK() {
    }

    public String getCUST_RISK_ATR() {
        return this.CUST_RISK_ATR;
    }

    public void setCUST_RISK_ATR(String CUST_RISK_ATR) {
        this.CUST_RISK_ATR = CUST_RISK_ATR;
    }

    public String getPARAM_NO() {
        return this.PARAM_NO;
    }

    public void setPARAM_NO(String PARAM_NO) {
        this.PARAM_NO = PARAM_NO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_RISK_ATR", getCUST_RISK_ATR())
            .append("PARAM_NO", getPARAM_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_CUSTRISK_VOLATILITYPK) ) return false;
        TBFPS_CUSTRISK_VOLATILITYPK castOther = (TBFPS_CUSTRISK_VOLATILITYPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_RISK_ATR(), castOther.getCUST_RISK_ATR())
            .append(this.getPARAM_NO(), castOther.getPARAM_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_RISK_ATR())
            .append(getPARAM_NO())
            .toHashCode();
    }

}
