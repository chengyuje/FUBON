package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_AUTHORITY_DETAILVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITY_DETAILPK comp_id;

    /** nullable persistent field */
    private String ROLE_ID;

    /** nullable persistent field */
    private String EMP_ID;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITY_DETAIL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_AUTHORITY_DETAILVO(com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITY_DETAILPK comp_id, String ROLE_ID, String EMP_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.ROLE_ID = ROLE_ID;
        this.EMP_ID = EMP_ID;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_AUTHORITY_DETAILVO() {
    }

    /** minimal constructor */
    public TBFPS_AUTHORITY_DETAILVO(com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITY_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITY_DETAILPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBFPS_AUTHORITY_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getROLE_ID() {
        return this.ROLE_ID;
    }

    public void setROLE_ID(String ROLE_ID) {
        this.ROLE_ID = ROLE_ID;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
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
        if ( !(other instanceof TBFPS_AUTHORITY_DETAILVO) ) return false;
        TBFPS_AUTHORITY_DETAILVO castOther = (TBFPS_AUTHORITY_DETAILVO) other;
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
