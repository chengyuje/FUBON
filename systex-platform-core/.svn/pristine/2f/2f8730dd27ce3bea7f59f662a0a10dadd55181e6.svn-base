package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TbsyssecufunitemasPK implements Serializable {

    /** identifier field */
    private String itemid;

    /** identifier field */
    private String functionid;

    /** full constructor */
    public TbsyssecufunitemasPK(String itemid, String functionid) {
        this.itemid = itemid;
        this.functionid = functionid;
    }

    /** default constructor */
    public TbsyssecufunitemasPK() {
    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getFunctionid() {
        return this.functionid;
    }

    public void setFunctionid(String functionid) {
        this.functionid = functionid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("itemid", getItemid())
            .append("functionid", getFunctionid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecufunitemasPK) ) return false;
        TbsyssecufunitemasPK castOther = (TbsyssecufunitemasPK) other;
        return new EqualsBuilder()
            .append(this.getItemid(), castOther.getItemid())
            .append(this.getFunctionid(), castOther.getFunctionid())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getItemid())
            .append(getFunctionid())
            .toHashCode();
    }

}
