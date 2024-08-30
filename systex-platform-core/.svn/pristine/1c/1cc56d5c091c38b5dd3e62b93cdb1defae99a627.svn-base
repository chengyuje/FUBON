package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSSCHDJOBCONDIPK  implements Serializable  {

    /** identifier field */
    private String SCHEDULEID;

    /** identifier field */
    private String JOBID;

    /** identifier field */
    private String REQUIREDJOBID;

    /** full constructor */
    public TBSYSSCHDJOBCONDIPK(String SCHEDULEID, String JOBID, String REQUIREDJOBID) {
        this.SCHEDULEID = SCHEDULEID;
        this.JOBID = JOBID;
        this.REQUIREDJOBID = REQUIREDJOBID;
    }

    /** default constructor */
    public TBSYSSCHDJOBCONDIPK() {
    }

    public String getSCHEDULEID() {
        return this.SCHEDULEID;
    }

    public void setSCHEDULEID(String SCHEDULEID) {
        this.SCHEDULEID = SCHEDULEID;
    }

    public String getJOBID() {
        return this.JOBID;
    }

    public void setJOBID(String JOBID) {
        this.JOBID = JOBID;
    }

    public String getREQUIREDJOBID() {
        return this.REQUIREDJOBID;
    }

    public void setREQUIREDJOBID(String REQUIREDJOBID) {
        this.REQUIREDJOBID = REQUIREDJOBID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SCHEDULEID", getSCHEDULEID())
            .append("JOBID", getJOBID())
            .append("REQUIREDJOBID", getREQUIREDJOBID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSSCHDJOBCONDIPK) ) return false;
        TBSYSSCHDJOBCONDIPK castOther = (TBSYSSCHDJOBCONDIPK) other;
        return new EqualsBuilder()
            .append(this.getSCHEDULEID(), castOther.getSCHEDULEID())
            .append(this.getJOBID(), castOther.getJOBID())
            .append(this.getREQUIREDJOBID(), castOther.getREQUIREDJOBID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSCHEDULEID())
            .append(getJOBID())
            .append(getREQUIREDJOBID())
            .toHashCode();
    }

}
