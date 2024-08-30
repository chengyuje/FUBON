package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_SEC_ITMFUNASSVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBFPS_SEC_ITMFUNASSPK comp_id;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_SEC_ITMFUNASS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_SEC_ITMFUNASSVO(com.systex.jbranch.app.common.fps.table.TBFPS_SEC_ITMFUNASSPK comp_id, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.comp_id = comp_id;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_SEC_ITMFUNASSVO() {
    }

    /** minimal constructor */
    public TBFPS_SEC_ITMFUNASSVO(com.systex.jbranch.app.common.fps.table.TBFPS_SEC_ITMFUNASSPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBFPS_SEC_ITMFUNASSPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBFPS_SEC_ITMFUNASSPK comp_id) {
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
        if ( !(other instanceof TBFPS_SEC_ITMFUNASSVO) ) return false;
        TBFPS_SEC_ITMFUNASSVO castOther = (TBFPS_SEC_ITMFUNASSVO) other;
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
