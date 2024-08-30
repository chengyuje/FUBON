package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_INVESTOREXAM_D_EXPPK  implements Serializable  {

    /** identifier field */
    private String EXAMID;

    /** identifier field */
    private String PROFILE_TEST_ID;

    /** identifier field */
    private String QCLASS;

    /** identifier field */
    private String QID;

    /** full constructor */
    public TBKYC_INVESTOREXAM_D_EXPPK(String EXAMID, String PROFILE_TEST_ID, String QCLASS, String QID) {
        this.EXAMID = EXAMID;
        this.PROFILE_TEST_ID = PROFILE_TEST_ID;
        this.QCLASS = QCLASS;
        this.QID = QID;
    }

    /** default constructor */
    public TBKYC_INVESTOREXAM_D_EXPPK() {
    }

    public String getEXAMID() {
        return this.EXAMID;
    }

    public void setEXAMID(String EXAMID) {
        this.EXAMID = EXAMID;
    }

    public String getPROFILE_TEST_ID() {
        return this.PROFILE_TEST_ID;
    }

    public void setPROFILE_TEST_ID(String PROFILE_TEST_ID) {
        this.PROFILE_TEST_ID = PROFILE_TEST_ID;
    }

    public String getQCLASS() {
        return this.QCLASS;
    }

    public void setQCLASS(String QCLASS) {
        this.QCLASS = QCLASS;
    }

    public String getQID() {
        return this.QID;
    }

    public void setQID(String QID) {
        this.QID = QID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EXAMID", getEXAMID())
            .append("PROFILE_TEST_ID", getPROFILE_TEST_ID())
            .append("QCLASS", getQCLASS())
            .append("QID", getQID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBKYC_INVESTOREXAM_D_EXPPK) ) return false;
        TBKYC_INVESTOREXAM_D_EXPPK castOther = (TBKYC_INVESTOREXAM_D_EXPPK) other;
        return new EqualsBuilder()
            .append(this.getEXAMID(), castOther.getEXAMID())
            .append(this.getPROFILE_TEST_ID(), castOther.getPROFILE_TEST_ID())
            .append(this.getQCLASS(), castOther.getQCLASS())
            .append(this.getQID(), castOther.getQID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEXAMID())
            .append(getPROFILE_TEST_ID())
            .append(getQCLASS())
            .append(getQID())
            .toHashCode();
    }

}
