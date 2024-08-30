package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TbsyssecurolpriassPK implements Serializable {

    /** identifier field */
    private String privilegeid;

    /** identifier field */
    private String roleid;

    /** full constructor */
    public TbsyssecurolpriassPK(String privilegeid, String roleid) {
        this.privilegeid = privilegeid;
        this.roleid = roleid;
    }

    /** default constructor */
    public TbsyssecurolpriassPK() {
    }

    public String getPrivilegeid() {
        return this.privilegeid;
    }

    public void setPrivilegeid(String privilegeid) {
        this.privilegeid = privilegeid;
    }

    public String getRoleid() {
        return this.roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("privilegeid", getPrivilegeid())
            .append("roleid", getRoleid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecurolpriassPK) ) return false;
        TbsyssecurolpriassPK castOther = (TbsyssecurolpriassPK) other;
        return new EqualsBuilder()
            .append(this.getPrivilegeid(), castOther.getPrivilegeid())
            .append(this.getRoleid(), castOther.getRoleid())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPrivilegeid())
            .append(getRoleid())
            .toHashCode();
    }

}
