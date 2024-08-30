package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_PROFEEPK  implements Serializable  {

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

    /** identifier field */
    private String PROD_CODE;

    /** full constructor */
    public TBCRM_CUST_PROFEEPK(String CUST_ID, String DATA_MONTH, String DATA_YEAR, String PROD_CODE) {
        this.CUST_ID = CUST_ID;
        this.DATA_MONTH = DATA_MONTH;
        this.DATA_YEAR = DATA_YEAR;
        this.PROD_CODE = PROD_CODE;
    }

    /** default constructor */
    public TBCRM_CUST_PROFEEPK() {
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

    public String getPROD_CODE() {
        return this.PROD_CODE;
    }

    public void setPROD_CODE(String PROD_CODE) {
        this.PROD_CODE = PROD_CODE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("DATA_MONTH", getDATA_MONTH())
            .append("DATA_YEAR", getDATA_YEAR())
            .append("PROD_CODE", getPROD_CODE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_CUST_PROFEEPK) ) return false;
        TBCRM_CUST_PROFEEPK castOther = (TBCRM_CUST_PROFEEPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getDATA_MONTH(), castOther.getDATA_MONTH())
            .append(this.getDATA_YEAR(), castOther.getDATA_YEAR())
            .append(this.getPROD_CODE(), castOther.getPROD_CODE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getDATA_MONTH())
            .append(getDATA_YEAR())
            .append(getPROD_CODE())
            .toHashCode();
    }

}
