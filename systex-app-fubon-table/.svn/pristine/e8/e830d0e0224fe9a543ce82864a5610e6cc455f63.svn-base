package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_FAIAVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBORG_FAIAPK comp_id;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_FAIA";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_FAIAVO(com.systex.jbranch.app.common.fps.table.TBORG_FAIAPK comp_id, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBORG_FAIAVO() {
    }

    /** minimal constructor */
    public TBORG_FAIAVO(com.systex.jbranch.app.common.fps.table.TBORG_FAIAPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBORG_FAIAPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBORG_FAIAPK comp_id) {
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
        if ( !(other instanceof TBORG_FAIAVO) ) return false;
        TBORG_FAIAVO castOther = (TBORG_FAIAVO) other;
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
