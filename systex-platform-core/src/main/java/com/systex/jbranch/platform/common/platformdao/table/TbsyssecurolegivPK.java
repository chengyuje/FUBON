package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TbsyssecurolegivPK implements Serializable {

    /** identifier field */
    private String giver;

    /** identifier field */
    private String receiver;

    /** full constructor */
    public TbsyssecurolegivPK(String giver, String receiver) {
        this.giver = giver;
        this.receiver = receiver;
    }

    /** default constructor */
    public TbsyssecurolegivPK() {
    }

    public String getGiver() {
        return this.giver;
    }

    public void setGiver(String giver) {
        this.giver = giver;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("giver", getGiver())
            .append("receiver", getReceiver())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecurolegivPK) ) return false;
        TbsyssecurolegivPK castOther = (TbsyssecurolegivPK) other;
        return new EqualsBuilder()
            .append(this.getGiver(), castOther.getGiver())
            .append(this.getReceiver(), castOther.getReceiver())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getGiver())
            .append(getReceiver())
            .toHashCode();
    }

}
