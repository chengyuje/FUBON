package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSREPORTDETAILVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAILPK comp_id;

    /** persistent field */
    private Blob REPORTDATA;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAIL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSREPORTDETAILVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAILPK comp_id, Blob REPORTDATA, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.comp_id = comp_id;
        this.REPORTDATA = REPORTDATA;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSREPORTDETAILVO() {
    }

    /** minimal constructor */
    public TBSYSREPORTDETAILVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAILPK comp_id, Blob REPORTDATA) {
        this.comp_id = comp_id;
        this.REPORTDATA = REPORTDATA;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAILPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public Blob getREPORTDATA() {
        return this.REPORTDATA;
    }

    public void setREPORTDATA(Blob REPORTDATA) {
        this.REPORTDATA = REPORTDATA;
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
        if ( !(other instanceof TBSYSREPORTDETAILVO) ) return false;
        TBSYSREPORTDETAILVO castOther = (TBSYSREPORTDETAILVO) other;
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
