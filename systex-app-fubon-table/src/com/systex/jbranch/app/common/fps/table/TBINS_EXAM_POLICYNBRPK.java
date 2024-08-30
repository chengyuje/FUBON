package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_EXAM_POLICYNBRPK  implements Serializable  {

    /** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String POLICY_NBR;

    /** full constructor */
    public TBINS_EXAM_POLICYNBRPK(String CUST_ID, String POLICY_NBR) {
        this.CUST_ID = CUST_ID;
        this.POLICY_NBR = POLICY_NBR;
    }

    /** default constructor */
    public TBINS_EXAM_POLICYNBRPK() {
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getPOLICY_NBR() {
        return this.POLICY_NBR;
    }

    public void setPOLICY_NBR(String POLICY_NBR) {
        this.POLICY_NBR = POLICY_NBR;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .append("POLICY_NBR", getPOLICY_NBR())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBINS_EXAM_POLICYNBRPK) ) return false;
        TBINS_EXAM_POLICYNBRPK castOther = (TBINS_EXAM_POLICYNBRPK) other;
        return new EqualsBuilder()
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getPOLICY_NBR(), castOther.getPOLICY_NBR())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCUST_ID())
            .append(getPOLICY_NBR())
            .toHashCode();
    }

}
