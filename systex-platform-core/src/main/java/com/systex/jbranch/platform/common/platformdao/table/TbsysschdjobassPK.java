package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysschdjobassPK  implements Serializable  {

    /** identifier field */
    private String scheduleid;

    /** identifier field */
    private String jobid;

    /** full constructor */
    public TbsysschdjobassPK(String scheduleid, String jobid) {
        this.scheduleid = scheduleid;
        this.jobid = jobid;
    }

    /** default constructor */
    public TbsysschdjobassPK() {
    }

    public String getScheduleid() {
        return this.scheduleid;
    }

    public void setScheduleid(String scheduleid) {
        this.scheduleid = scheduleid;
    }

    public String getJobid() {
        return this.jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("scheduleid", getScheduleid())
            .append("jobid", getJobid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysschdjobassPK) ) return false;
        TbsysschdjobassPK castOther = (TbsysschdjobassPK) other;
        return new EqualsBuilder()
            .append(this.getScheduleid(), castOther.getScheduleid())
            .append(this.getJobid(), castOther.getJobid())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getScheduleid())
            .append(getJobid())
            .toHashCode();
    }

}
