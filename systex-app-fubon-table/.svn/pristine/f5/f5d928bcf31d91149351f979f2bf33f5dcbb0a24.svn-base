package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DAILY_FIPETLPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String DATA_DATE;

    /** identifier field */
    private String TXN_NO;

    /** full constructor */
    public TBPMS_DAILY_FIPETLPK(String DATA_DATE, String TXN_NO) {
        this.DATA_DATE = DATA_DATE;
        this.TXN_NO = TXN_NO;
    }

    /** default constructor */
    public TBPMS_DAILY_FIPETLPK() {
    }

    public String getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(String DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
    }

    public String getTXN_NO() {
        return this.TXN_NO;
    }

    public void setTXN_NO(String TXN_NO) {
        this.TXN_NO = TXN_NO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DATA_DATE", getDATA_DATE())
            .append("TXN_NO", getTXN_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_DAILY_FIPETLPK) ) return false;
        TBPMS_DAILY_FIPETLPK castOther = (TBPMS_DAILY_FIPETLPK) other;
        return new EqualsBuilder()
            .append(this.getDATA_DATE(), castOther.getDATA_DATE())
            .append(this.getTXN_NO(), castOther.getTXN_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDATA_DATE())
            .append(getTXN_NO())
            .toHashCode();
    }

}
