package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_VIPPK  implements Serializable  {

    /** identifier field */
    private String ACT_SEQ;

    /** identifier field */
    private String CUST_ID;

    /** full constructor */
    public TBMGM_VIPPK(String ACT_SEQ, String CUST_ID) {
        this.ACT_SEQ = ACT_SEQ;
        this.CUST_ID = CUST_ID;
    }

    /** default constructor */
    public TBMGM_VIPPK() {
    }

    public String getACT_SEQ() {
        return this.ACT_SEQ;
    }

    public void setACT_SEQ(String ACT_SEQ) {
        this.ACT_SEQ = ACT_SEQ;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ACT_SEQ", getACT_SEQ())
            .append("CUST_ID", getCUST_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBMGM_VIPPK) ) return false;
        TBMGM_VIPPK castOther = (TBMGM_VIPPK) other;
        return new EqualsBuilder()
            .append(this.getACT_SEQ(), castOther.getACT_SEQ())
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getACT_SEQ())
            .append(getCUST_ID())
            .toHashCode();
    }

}
