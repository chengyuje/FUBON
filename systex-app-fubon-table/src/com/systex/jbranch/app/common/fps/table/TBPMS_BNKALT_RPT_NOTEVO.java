package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_BNKALT_RPT_NOTEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPT_NOTEPK comp_id;

    /** nullable persistent field */
    private String SUPERVISOR_FLAG;

    /** nullable persistent field */
    private Timestamp NOTE_TIME;

    /** nullable persistent field */
    private String NOTE;

    /** nullable persistent field */
    private String RELATION;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPT_NOTE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_BNKALT_RPT_NOTEVO(com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPT_NOTEPK comp_id, String SUPERVISOR_FLAG, Timestamp NOTE_TIME, String NOTE, String RELATION, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.SUPERVISOR_FLAG = SUPERVISOR_FLAG;
        this.NOTE_TIME = NOTE_TIME;
        this.NOTE = NOTE;
        this.RELATION = RELATION;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_BNKALT_RPT_NOTEVO() {
    }

    /** minimal constructor */
    public TBPMS_BNKALT_RPT_NOTEVO(com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPT_NOTEPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPT_NOTEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPT_NOTEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getSUPERVISOR_FLAG() {
        return this.SUPERVISOR_FLAG;
    }

    public void setSUPERVISOR_FLAG(String SUPERVISOR_FLAG) {
        this.SUPERVISOR_FLAG = SUPERVISOR_FLAG;
    }

    public Timestamp getNOTE_TIME() {
        return this.NOTE_TIME;
    }

    public void setNOTE_TIME(Timestamp NOTE_TIME) {
        this.NOTE_TIME = NOTE_TIME;
    }

    public String getNOTE() {
        return this.NOTE;
    }

    public void setNOTE(String NOTE) {
        this.NOTE = NOTE;
    }

    public String getRELATION() {
        return this.RELATION;
    }

    public void setRELATION(String RELATION) {
        this.RELATION = RELATION;
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
        if ( !(other instanceof TBPMS_BNKALT_RPT_NOTEVO) ) return false;
        TBPMS_BNKALT_RPT_NOTEVO castOther = (TBPMS_BNKALT_RPT_NOTEVO) other;
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
