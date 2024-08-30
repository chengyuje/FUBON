package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DYNAMIC_RPT_DTL_RECVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_DTL_RECPK comp_id;

    /** nullable persistent field */
    private String CONTENT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_DTL_REC";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_DYNAMIC_RPT_DTL_RECVO(com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_DTL_RECPK comp_id, String CONTENT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CONTENT = CONTENT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_DYNAMIC_RPT_DTL_RECVO() {
    }

    /** minimal constructor */
    public TBPMS_DYNAMIC_RPT_DTL_RECVO(com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_DTL_RECPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_DTL_RECPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_DTL_RECPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCONTENT() {
        return this.CONTENT;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
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
        if ( !(other instanceof TBPMS_DYNAMIC_RPT_DTL_RECVO) ) return false;
        TBPMS_DYNAMIC_RPT_DTL_RECVO castOther = (TBPMS_DYNAMIC_RPT_DTL_RECVO) other;
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
