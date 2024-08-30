package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_PPT_DOCCHKVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBIOT_PPT_DOCCHKPK comp_id;

    /** nullable persistent field */
    private String DOC_OTH;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_PPT_DOCCHK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_PPT_DOCCHKVO(com.systex.jbranch.app.common.fps.table.TBIOT_PPT_DOCCHKPK comp_id, String DOC_OTH, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.DOC_OTH = DOC_OTH;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_PPT_DOCCHKVO() {
    }

    /** minimal constructor */
    public TBIOT_PPT_DOCCHKVO(com.systex.jbranch.app.common.fps.table.TBIOT_PPT_DOCCHKPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBIOT_PPT_DOCCHKPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBIOT_PPT_DOCCHKPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getDOC_OTH() {
        return this.DOC_OTH;
    }

    public void setDOC_OTH(String DOC_OTH) {
        this.DOC_OTH = DOC_OTH;
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
        if ( !(other instanceof TBIOT_PPT_DOCCHKVO) ) return false;
        TBIOT_PPT_DOCCHKVO castOther = (TBIOT_PPT_DOCCHKVO) other;
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
