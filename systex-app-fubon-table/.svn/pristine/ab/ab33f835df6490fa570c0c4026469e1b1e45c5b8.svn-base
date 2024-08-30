package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DYNAMIC_RPT_HEADERVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_HEADERPK comp_id;

    /** nullable persistent field */
    private String COL_NAME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_HEADER";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_DYNAMIC_RPT_HEADERVO(com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_HEADERPK comp_id, String COL_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.COL_NAME = COL_NAME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_DYNAMIC_RPT_HEADERVO() {
    }

    /** minimal constructor */
    public TBPMS_DYNAMIC_RPT_HEADERVO(com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_HEADERPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_HEADERPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_HEADERPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCOL_NAME() {
        return this.COL_NAME;
    }

    public void setCOL_NAME(String COL_NAME) {
        this.COL_NAME = COL_NAME;
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
        if ( !(other instanceof TBPMS_DYNAMIC_RPT_HEADERVO) ) return false;
        TBPMS_DYNAMIC_RPT_HEADERVO castOther = (TBPMS_DYNAMIC_RPT_HEADERVO) other;
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
