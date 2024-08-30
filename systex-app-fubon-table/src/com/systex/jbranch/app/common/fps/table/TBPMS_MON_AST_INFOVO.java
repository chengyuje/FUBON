package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MON_AST_INFOVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_INFOPK comp_id;

    /** nullable persistent field */
    private String PRD_TYPE;

    /** nullable persistent field */
    private String INFO;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_INFO";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_MON_AST_INFOVO(com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_INFOPK comp_id, String PRD_TYPE, String INFO, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PRD_TYPE = PRD_TYPE;
        this.INFO = INFO;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_MON_AST_INFOVO() {
    }

    /** minimal constructor */
    public TBPMS_MON_AST_INFOVO(com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_INFOPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_INFOPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_INFOPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPRD_TYPE() {
        return this.PRD_TYPE;
    }

    public void setPRD_TYPE(String PRD_TYPE) {
        this.PRD_TYPE = PRD_TYPE;
    }

    public String getINFO() {
        return this.INFO;
    }

    public void setINFO(String INFO) {
        this.INFO = INFO;
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
        if ( !(other instanceof TBPMS_MON_AST_INFOVO) ) return false;
        TBPMS_MON_AST_INFOVO castOther = (TBPMS_MON_AST_INFOVO) other;
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
