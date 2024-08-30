package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TbsyssecpriassassoPK implements Serializable {

    /** identifier field */
    private String assignmentid;

    /** identifier field */
    private String privilegeid;

    /** full constructor */
    public TbsyssecpriassassoPK(String assignmentid, String privilegeid) {
        this.assignmentid = assignmentid;
        this.privilegeid = privilegeid;
    }

    /** default constructor */
    public TbsyssecpriassassoPK() {
    }

    public String getAssignmentid() {
        return this.assignmentid;
    }

    public void setAssignmentid(String assignmentid) {
        this.assignmentid = assignmentid;
    }

    public String getPrivilegeid() {
        return this.privilegeid;
    }

    public void setPrivilegeid(String privilegeid) {
        this.privilegeid = privilegeid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("assignmentid", getAssignmentid())
            .append("privilegeid", getPrivilegeid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecpriassassoPK) ) return false;
        TbsyssecpriassassoPK castOther = (TbsyssecpriassassoPK) other;
        return new EqualsBuilder()
            .append(this.getAssignmentid(), castOther.getAssignmentid())
            .append(this.getPrivilegeid(), castOther.getPrivilegeid())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAssignmentid())
            .append(getPrivilegeid())
            .toHashCode();
    }

}
