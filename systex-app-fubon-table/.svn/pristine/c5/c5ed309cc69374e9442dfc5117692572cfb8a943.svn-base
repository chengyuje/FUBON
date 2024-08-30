package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_CUST_ASS_BRH_SETVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_ASS_BRH_SETPK comp_id;

    /** nullable persistent field */
    private BigDecimal PRIORITY_ORDER;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_ASS_BRH_SET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_CUST_ASS_BRH_SETVO(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_ASS_BRH_SETPK comp_id, BigDecimal PRIORITY_ORDER, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PRIORITY_ORDER = PRIORITY_ORDER;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_TRS_CUST_ASS_BRH_SETVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_CUST_ASS_BRH_SETVO(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_ASS_BRH_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_ASS_BRH_SETPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_ASS_BRH_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getPRIORITY_ORDER() {
        return this.PRIORITY_ORDER;
    }

    public void setPRIORITY_ORDER(BigDecimal PRIORITY_ORDER) {
        this.PRIORITY_ORDER = PRIORITY_ORDER;
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
        if ( !(other instanceof TBCRM_TRS_CUST_ASS_BRH_SETVO) ) return false;
        TBCRM_TRS_CUST_ASS_BRH_SETVO castOther = (TBCRM_TRS_CUST_ASS_BRH_SETVO) other;
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
