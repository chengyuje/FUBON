package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_EXAMRECORD_DETAILVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD_DETAILPK comp_id;

    /** nullable persistent field */
    private String REMARK;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD_DETAIL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_EXAMRECORD_DETAILVO(com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD_DETAILPK comp_id, String REMARK, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REMARK = REMARK;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_EXAMRECORD_DETAILVO() {
    }

    /** minimal constructor */
    public TBCAM_EXAMRECORD_DETAILVO(com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD_DETAILPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getREMARK() {
        return this.REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
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
        if ( !(other instanceof TBCAM_EXAMRECORD_DETAILVO) ) return false;
        TBCAM_EXAMRECORD_DETAILVO castOther = (TBCAM_EXAMRECORD_DETAILVO) other;
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
