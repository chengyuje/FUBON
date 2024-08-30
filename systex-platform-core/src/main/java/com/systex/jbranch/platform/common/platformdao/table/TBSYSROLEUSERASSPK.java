package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSROLEUSERASSPK  implements Serializable  {

    /** identifier field */
    private String ROLEID;

    /** identifier field */
    private String USERID;

    /** full constructor */
    public TBSYSROLEUSERASSPK(String ROLEID, String USERID) {
        this.ROLEID = ROLEID;
        this.USERID = USERID;
    }

    /** default constructor */
    public TBSYSROLEUSERASSPK() {
    }

    public String getROLEID() {
        return this.ROLEID;
    }

    public void setROLEID(String ROLEID) {
        this.ROLEID = ROLEID;
    }

    public String getUSERID() {
        return this.USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ROLEID", getROLEID())
            .append("USERID", getUSERID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSROLEUSERASSPK) ) return false;
        TBSYSROLEUSERASSPK castOther = (TBSYSROLEUSERASSPK) other;
        return new EqualsBuilder()
            .append(this.getROLEID(), castOther.getROLEID())
            .append(this.getUSERID(), castOther.getUSERID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getROLEID())
            .append(getUSERID())
            .toHashCode();
    }

}
