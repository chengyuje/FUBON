package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSROLEUSERASSVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSROLEUSERASSPK comp_id;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSROLEUSERASS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSROLEUSERASSVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSROLEUSERASSPK comp_id, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYSROLEUSERASSVO() {
    }

    /** minimal constructor */
    public TBSYSROLEUSERASSVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSROLEUSERASSPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSROLEUSERASSPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSROLEUSERASSPK comp_id) {
        this.comp_id = comp_id;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSROLEUSERASSVO) ) return false;
        TBSYSROLEUSERASSVO castOther = (TBSYSROLEUSERASSVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
