package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_CSM_MAINPK  implements Serializable  {

    /** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String DATA_DATE;

    /** identifier field */
    private String QTN_TYPE;

    /** full constructor */
    public TBSQM_CSM_MAINPK(String CUST_ID, String DATA_DATE, String QTN_TYPE) {
        this.CUST_ID = CUST_ID;
        this.DATA_DATE = DATA_DATE;
        this.QTN_TYPE = QTN_TYPE;
    }

    /** default constructor */
    public TBSQM_CSM_MAINPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(String DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
    }

    public String getQTN_TYPE() {
        return this.QTN_TYPE;
    }

    public void setQTN_TYPE(String QTN_TYPE) {
        this.QTN_TYPE = QTN_TYPE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("DATA_DATE", getDATA_DATE())
            .append("QTN_TYPE", getQTN_TYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSQM_CSM_MAINPK) ) return false;
        TBSQM_CSM_MAINPK castOther = (TBSQM_CSM_MAINPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getDATA_DATE(), castOther.getDATA_DATE())
            .append(this.getQTN_TYPE(), castOther.getQTN_TYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getDATA_DATE())
            .append(getQTN_TYPE())
            .toHashCode();
    }

}
