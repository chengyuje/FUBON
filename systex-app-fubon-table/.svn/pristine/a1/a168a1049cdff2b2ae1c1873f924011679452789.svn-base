package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_RSA_RC_BRPK  implements Serializable  {

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String EMP_ID;

    /** identifier field */
    private String YEARQTR;

    /** full constructor */
    public TBSQM_RSA_RC_BRPK(String BRANCH_NBR, String EMP_ID, String YEARQTR) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.EMP_ID = EMP_ID;
        this.YEARQTR = YEARQTR;
    }

    /** default constructor */
    public TBSQM_RSA_RC_BRPK() {
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getYEARQTR() {
        return this.YEARQTR;
    }

    public void setYEARQTR(String YEARQTR) {
        this.YEARQTR = YEARQTR;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("EMP_ID", getEMP_ID())
            .append("YEARQTR", getYEARQTR())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSQM_RSA_RC_BRPK) ) return false;
        TBSQM_RSA_RC_BRPK castOther = (TBSQM_RSA_RC_BRPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getYEARQTR(), castOther.getYEARQTR())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getEMP_ID())
            .append(getYEARQTR())
            .toHashCode();
    }

}
