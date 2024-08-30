package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_PS_SA_INS_EMPFUNDPK  implements Serializable  {

    /** identifier field */
    private String EMPID;

    /** identifier field */
    private String FUND_CODE;

    /** identifier field */
    private String PERSON_ID;

    /** full constructor */
    public TBORG_PS_SA_INS_EMPFUNDPK(String EMPID, String FUND_CODE, String PERSON_ID) {
        this.EMPID = EMPID;
        this.FUND_CODE = FUND_CODE;
        this.PERSON_ID = PERSON_ID;
    }

    /** default constructor */
    public TBORG_PS_SA_INS_EMPFUNDPK() {
    }

    public String getEMPID() {
        return this.EMPID;
    }

    public void setEMPID(String EMPID) {
        this.EMPID = EMPID;
    }

    public String getFUND_CODE() {
        return this.FUND_CODE;
    }

    public void setFUND_CODE(String FUND_CODE) {
        this.FUND_CODE = FUND_CODE;
    }

    public String getPERSON_ID() {
        return this.PERSON_ID;
    }

    public void setPERSON_ID(String PERSON_ID) {
        this.PERSON_ID = PERSON_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMPID", getEMPID())
            .append("FUND_CODE", getFUND_CODE())
            .append("PERSON_ID", getPERSON_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBORG_PS_SA_INS_EMPFUNDPK) ) return false;
        TBORG_PS_SA_INS_EMPFUNDPK castOther = (TBORG_PS_SA_INS_EMPFUNDPK) other;
        return new EqualsBuilder()
            .append(this.getEMPID(), castOther.getEMPID())
            .append(this.getFUND_CODE(), castOther.getFUND_CODE())
            .append(this.getPERSON_ID(), castOther.getPERSON_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEMPID())
            .append(getFUND_CODE())
            .append(getPERSON_ID())
            .toHashCode();
    }

}
