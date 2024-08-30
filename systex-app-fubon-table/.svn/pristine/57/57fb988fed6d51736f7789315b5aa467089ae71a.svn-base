package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBESB_LOGVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBESB_LOGPK comp_id;

    /** nullable persistent field */
    private String ESB_HTXTID;

    /** nullable persistent field */
    private Blob ESB_FILE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBESB_LOG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBESB_LOGVO(com.systex.jbranch.app.common.fps.table.TBESB_LOGPK comp_id, String ESB_HTXTID, Blob ESB_FILE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.ESB_HTXTID = ESB_HTXTID;
        this.ESB_FILE = ESB_FILE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBESB_LOGVO() {
    }

    /** minimal constructor */
    public TBESB_LOGVO(com.systex.jbranch.app.common.fps.table.TBESB_LOGPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBESB_LOGPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBESB_LOGPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getESB_HTXTID() {
        return this.ESB_HTXTID;
    }

    public void setESB_HTXTID(String ESB_HTXTID) {
        this.ESB_HTXTID = ESB_HTXTID;
    }

    public Blob getESB_FILE() {
        return this.ESB_FILE;
    }

    public void setESB_FILE(Blob ESB_FILE) {
        this.ESB_FILE = ESB_FILE;
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
        if ( !(other instanceof TBESB_LOGVO) ) return false;
        TBESB_LOGVO castOther = (TBESB_LOGVO) other;
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
