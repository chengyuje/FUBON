package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MNGR_EVAL_SETVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SETPK comp_id;

    /** nullable persistent field */
    private String COACHING_POINT_A;

    /** nullable persistent field */
    private String COACHING_POINT_B;

    /** nullable persistent field */
    private String COACHING_POINT_C;

    /** nullable persistent field */
    private String COACHING_POINT_D;

    /** nullable persistent field */
    private String COACHING_FREQ;

    /** nullable persistent field */
    private Timestamp MAINTAIN_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_MNGR_EVAL_SETVO(com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SETPK comp_id, String COACHING_POINT_A, String COACHING_POINT_B, String COACHING_POINT_C, String COACHING_POINT_D, String COACHING_FREQ, Timestamp MAINTAIN_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.COACHING_POINT_A = COACHING_POINT_A;
        this.COACHING_POINT_B = COACHING_POINT_B;
        this.COACHING_POINT_C = COACHING_POINT_C;
        this.COACHING_POINT_D = COACHING_POINT_D;
        this.COACHING_FREQ = COACHING_FREQ;
        this.MAINTAIN_DATE = MAINTAIN_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_MNGR_EVAL_SETVO() {
    }

    /** minimal constructor */
    public TBPMS_MNGR_EVAL_SETVO(com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SETPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCOACHING_POINT_A() {
        return this.COACHING_POINT_A;
    }

    public void setCOACHING_POINT_A(String COACHING_POINT_A) {
        this.COACHING_POINT_A = COACHING_POINT_A;
    }

    public String getCOACHING_POINT_B() {
        return this.COACHING_POINT_B;
    }

    public void setCOACHING_POINT_B(String COACHING_POINT_B) {
        this.COACHING_POINT_B = COACHING_POINT_B;
    }

    public String getCOACHING_POINT_C() {
        return this.COACHING_POINT_C;
    }

    public void setCOACHING_POINT_C(String COACHING_POINT_C) {
        this.COACHING_POINT_C = COACHING_POINT_C;
    }

    public String getCOACHING_POINT_D() {
        return this.COACHING_POINT_D;
    }

    public void setCOACHING_POINT_D(String COACHING_POINT_D) {
        this.COACHING_POINT_D = COACHING_POINT_D;
    }

    public String getCOACHING_FREQ() {
        return this.COACHING_FREQ;
    }

    public void setCOACHING_FREQ(String COACHING_FREQ) {
        this.COACHING_FREQ = COACHING_FREQ;
    }

    public Timestamp getMAINTAIN_DATE() {
        return this.MAINTAIN_DATE;
    }

    public void setMAINTAIN_DATE(Timestamp MAINTAIN_DATE) {
        this.MAINTAIN_DATE = MAINTAIN_DATE;
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
        if ( !(other instanceof TBPMS_MNGR_EVAL_SETVO) ) return false;
        TBPMS_MNGR_EVAL_SETVO castOther = (TBPMS_MNGR_EVAL_SETVO) other;
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
