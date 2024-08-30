package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_ESB_LOGVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSYS_ESB_LOGPK comp_id;

    /** nullable persistent field */
    private String ESB_HTXTID;

    /** nullable persistent field */
    private String MODULE_ID;

    /** nullable persistent field */
    private Blob ESB_FILE;

    /** nullable persistent field */
    private String ESB_HSTANO;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_ESB_LOG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_ESB_LOGVO(com.systex.jbranch.app.common.fps.table.TBSYS_ESB_LOGPK comp_id, String ESB_HTXTID, String MODULE_ID, Blob ESB_FILE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String ESB_HSTANO, Long version) {
        this.comp_id = comp_id;
        this.ESB_HTXTID = ESB_HTXTID;
        this.MODULE_ID = MODULE_ID;
        this.ESB_FILE = ESB_FILE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.ESB_HSTANO = ESB_HSTANO;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_ESB_LOGVO() {
    }

    /** minimal constructor */
    public TBSYS_ESB_LOGVO(com.systex.jbranch.app.common.fps.table.TBSYS_ESB_LOGPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSYS_ESB_LOGPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSYS_ESB_LOGPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getESB_HTXTID() {
        return this.ESB_HTXTID;
    }

    public void setESB_HTXTID(String ESB_HTXTID) {
        this.ESB_HTXTID = ESB_HTXTID;
    }

    public String getMODULE_ID() {
        return this.MODULE_ID;
    }

    public void setMODULE_ID(String MODULE_ID) {
        this.MODULE_ID = MODULE_ID;
    }

    public Blob getESB_FILE() {
        return this.ESB_FILE;
    }

    public void setESB_FILE(Blob ESB_FILE) {
        this.ESB_FILE = ESB_FILE;
    }

    public String getESB_HSTANO() {
        return this.ESB_HSTANO;
    }

    public void setESB_HSTANO(String ESB_HSTANO) {
        this.ESB_HSTANO = ESB_HSTANO;
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
        if ( !(other instanceof TBSYS_ESB_LOGVO) ) return false;
        TBSYS_ESB_LOGVO castOther = (TBSYS_ESB_LOGVO) other;
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
