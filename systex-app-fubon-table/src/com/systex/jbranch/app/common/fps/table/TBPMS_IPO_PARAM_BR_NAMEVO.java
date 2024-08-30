package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_IPO_PARAM_BR_NAMEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEPK comp_id;

    /** nullable persistent field */
    private String TARGET;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAME";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_IPO_PARAM_BR_NAMEVO(com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEPK comp_id, String TARGET, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.TARGET = TARGET;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_IPO_PARAM_BR_NAMEVO() {
    }

    /** minimal constructor */
    public TBPMS_IPO_PARAM_BR_NAMEVO(com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTARGET() {
        return this.TARGET;
    }

    public void setTARGET(String TARGET) {
        this.TARGET = TARGET;
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
        if ( !(other instanceof TBPMS_IPO_PARAM_BR_NAMEVO) ) return false;
        TBPMS_IPO_PARAM_BR_NAMEVO castOther = (TBPMS_IPO_PARAM_BR_NAMEVO) other;
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
