package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_MODEL_ALLOCATION_INSVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_INSPK comp_id;

    /** nullable persistent field */
    private String PARAM_NO;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_INS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_MODEL_ALLOCATION_INSVO(com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_INSPK comp_id, String PARAM_NO, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PARAM_NO = PARAM_NO;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_MODEL_ALLOCATION_INSVO() {
    }

    /** minimal constructor */
    public TBFPS_MODEL_ALLOCATION_INSVO(com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_INSPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_INSPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBFPS_MODEL_ALLOCATION_INSPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPARAM_NO() {
        return this.PARAM_NO;
    }

    public void setPARAM_NO(String PARAM_NO) {
        this.PARAM_NO = PARAM_NO;
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
        if ( !(other instanceof TBFPS_MODEL_ALLOCATION_INSVO) ) return false;
        TBFPS_MODEL_ALLOCATION_INSVO castOther = (TBFPS_MODEL_ALLOCATION_INSVO) other;
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
