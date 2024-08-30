package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_AGE_SGVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPRD_INS_AGE_SGPK comp_id;

    /** persistent field */
    private BigDecimal MIN_AGE;

    /** persistent field */
    private BigDecimal MAX_AGE;

    /** persistent field */
    private BigDecimal EXPIRED_AGE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_AGE_SG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_AGE_SGVO(com.systex.jbranch.app.common.fps.table.TBPRD_INS_AGE_SGPK comp_id, BigDecimal MIN_AGE, BigDecimal MAX_AGE, BigDecimal EXPIRED_AGE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.MIN_AGE = MIN_AGE;
        this.MAX_AGE = MAX_AGE;
        this.EXPIRED_AGE = EXPIRED_AGE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_AGE_SGVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_AGE_SGVO(com.systex.jbranch.app.common.fps.table.TBPRD_INS_AGE_SGPK comp_id, BigDecimal MIN_AGE, BigDecimal MAX_AGE, BigDecimal EXPIRED_AGE) {
        this.comp_id = comp_id;
        this.MIN_AGE = MIN_AGE;
        this.MAX_AGE = MAX_AGE;
        this.EXPIRED_AGE = EXPIRED_AGE;
    }

    public com.systex.jbranch.app.common.fps.table.TBPRD_INS_AGE_SGPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPRD_INS_AGE_SGPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getMIN_AGE() {
        return this.MIN_AGE;
    }

    public void setMIN_AGE(BigDecimal MIN_AGE) {
        this.MIN_AGE = MIN_AGE;
    }

    public BigDecimal getMAX_AGE() {
        return this.MAX_AGE;
    }

    public void setMAX_AGE(BigDecimal MAX_AGE) {
        this.MAX_AGE = MAX_AGE;
    }

    public BigDecimal getEXPIRED_AGE() {
        return this.EXPIRED_AGE;
    }

    public void setEXPIRED_AGE(BigDecimal EXPIRED_AGE) {
        this.EXPIRED_AGE = EXPIRED_AGE;
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
        if ( !(other instanceof TBPRD_INS_AGE_SGVO) ) return false;
        TBPRD_INS_AGE_SGVO castOther = (TBPRD_INS_AGE_SGVO) other;
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
