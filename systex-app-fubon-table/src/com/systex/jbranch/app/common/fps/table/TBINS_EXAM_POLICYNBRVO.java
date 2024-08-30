package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_EXAM_POLICYNBRVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBINS_EXAM_POLICYNBRPK comp_id;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_EXAM_POLICYNBR";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_EXAM_POLICYNBRVO(com.systex.jbranch.app.common.fps.table.TBINS_EXAM_POLICYNBRPK comp_id, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_EXAM_POLICYNBRVO() {
    }

    /** minimal constructor */
    public TBINS_EXAM_POLICYNBRVO(com.systex.jbranch.app.common.fps.table.TBINS_EXAM_POLICYNBRPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBINS_EXAM_POLICYNBRPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBINS_EXAM_POLICYNBRPK comp_id) {
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
        if ( !(other instanceof TBINS_EXAM_POLICYNBRVO) ) return false;
        TBINS_EXAM_POLICYNBRVO castOther = (TBINS_EXAM_POLICYNBRVO) other;
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
