package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_CON_NOTEPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String DATA_MONTH;

    /** identifier field */
    private String DATA_YEAR;

    /** full constructor */
    public TBCRM_CUST_CON_NOTEPK(String CUST_ID, String DATA_MONTH, String DATA_YEAR) {
        this.CUST_ID = CUST_ID;
        this.DATA_MONTH = DATA_MONTH;
        this.DATA_YEAR = DATA_YEAR;
    }

    /** default constructor */
    public TBCRM_CUST_CON_NOTEPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getDATA_MONTH() {
        return this.DATA_MONTH;
    }

    public void setDATA_MONTH(String DATA_MONTH) {
        this.DATA_MONTH = DATA_MONTH;
    }

    public String getDATA_YEAR() {
        return this.DATA_YEAR;
    }

    public void setDATA_YEAR(String DATA_YEAR) {
        this.DATA_YEAR = DATA_YEAR;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("DATA_MONTH", getDATA_MONTH())
            .append("DATA_YEAR", getDATA_YEAR())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_CUST_CON_NOTEPK) ) return false;
        TBCRM_CUST_CON_NOTEPK castOther = (TBCRM_CUST_CON_NOTEPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getDATA_MONTH(), castOther.getDATA_MONTH())
            .append(this.getDATA_YEAR(), castOther.getDATA_YEAR())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getDATA_MONTH())
            .append(getDATA_YEAR())
            .toHashCode();
    }

}
