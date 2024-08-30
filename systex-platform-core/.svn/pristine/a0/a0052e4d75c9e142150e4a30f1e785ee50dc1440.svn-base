package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TbsyssecuroatvalaPK implements Serializable {

    /** identifier field */
    private String attributeid;

    /** identifier field */
    private String roleid;

    /** full constructor */
    public TbsyssecuroatvalaPK(String attributeid, String roleid) {
        this.attributeid = attributeid;
        this.roleid = roleid;
    }

    /** default constructor */
    public TbsyssecuroatvalaPK() {
    }

    public String getAttributeid() {
        return this.attributeid;
    }

    public void setAttributeid(String attributeid) {
        this.attributeid = attributeid;
    }

    public String getRoleid() {
        return this.roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("attributeid", getAttributeid())
            .append("roleid", getRoleid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecuroatvalaPK) ) return false;
        TbsyssecuroatvalaPK castOther = (TbsyssecuroatvalaPK) other;
        return new EqualsBuilder()
            .append(this.getAttributeid(), castOther.getAttributeid())
            .append(this.getRoleid(), castOther.getRoleid())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAttributeid())
            .append(getRoleid())
            .toHashCode();
    }

}
