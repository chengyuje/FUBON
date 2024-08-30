package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_IPO_PARAM_BRVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRPK comp_id;

    /** nullable persistent field */
    private BigDecimal TARGET;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_IPO_PARAM_BRVO(com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRPK comp_id, BigDecimal TARGET, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.TARGET = TARGET;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_IPO_PARAM_BRVO() {
    }

    /** minimal constructor */
    public TBPMS_IPO_PARAM_BRVO(com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getTARGET() {
        return this.TARGET;
    }

    public void setTARGET(BigDecimal TARGET) {
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
        if ( !(other instanceof TBPMS_IPO_PARAM_BRVO) ) return false;
        TBPMS_IPO_PARAM_BRVO castOther = (TBPMS_IPO_PARAM_BRVO) other;
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
