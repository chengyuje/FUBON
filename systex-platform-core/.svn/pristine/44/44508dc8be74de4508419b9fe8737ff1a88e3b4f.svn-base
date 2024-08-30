package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TbsyssecuitmoduassPK implements Serializable {

    /** identifier field */
    private String moduleid;

    /** identifier field */
    private String itemid;

    /** full constructor */
    public TbsyssecuitmoduassPK(String moduleid, String itemid) {
        this.moduleid = moduleid;
        this.itemid = itemid;
    }

    /** default constructor */
    public TbsyssecuitmoduassPK() {
    }

    public String getModuleid() {
        return this.moduleid;
    }

    public void setModuleid(String moduleid) {
        this.moduleid = moduleid;
    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("moduleid", getModuleid())
            .append("itemid", getItemid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecuitmoduassPK) ) return false;
        TbsyssecuitmoduassPK castOther = (TbsyssecuitmoduassPK) other;
        return new EqualsBuilder()
            .append(this.getModuleid(), castOther.getModuleid())
            .append(this.getItemid(), castOther.getItemid())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getModuleid())
            .append(getItemid())
            .toHashCode();
    }

}
