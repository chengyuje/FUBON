package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MNGR_EVAL_SCHEDULEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEPK comp_id;

    /** persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private Timestamp COACH_DATE;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_ID;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String JOB_TITLE;

    /** nullable persistent field */
    private String COACHING_FREQ;

    /** nullable persistent field */
    private String COACHING_POINT_A;

    /** nullable persistent field */
    private String COACHING_POINT_B;

    /** nullable persistent field */
    private String COACHING_POINT_C;

    /** nullable persistent field */
    private String COACHING_POINT_D;

    /** nullable persistent field */
    private String FIN_TYPE;

    /** nullable persistent field */
    private Timestamp FIN_DATE;

    /** nullable persistent field */
    private String DIR_REV;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_MNGR_EVAL_SCHEDULEVO(com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEPK comp_id, String EMP_ID, Timestamp COACH_DATE, String REGION_CENTER_ID, String REGION_CENTER_NAME, String BRANCH_AREA_ID, String BRANCH_AREA_NAME, String BRANCH_ID, String BRANCH_NAME, String EMP_NAME, String JOB_TITLE, String COACHING_FREQ, String COACHING_POINT_A, String COACHING_POINT_B, String COACHING_POINT_C, String COACHING_POINT_D, String FIN_TYPE, Timestamp FIN_DATE, String DIR_REV, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.EMP_ID = EMP_ID;
        this.COACH_DATE = COACH_DATE;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
        this.BRANCH_ID = BRANCH_ID;
        this.BRANCH_NAME = BRANCH_NAME;
        this.EMP_NAME = EMP_NAME;
        this.JOB_TITLE = JOB_TITLE;
        this.COACHING_FREQ = COACHING_FREQ;
        this.COACHING_POINT_A = COACHING_POINT_A;
        this.COACHING_POINT_B = COACHING_POINT_B;
        this.COACHING_POINT_C = COACHING_POINT_C;
        this.COACHING_POINT_D = COACHING_POINT_D;
        this.FIN_TYPE = FIN_TYPE;
        this.FIN_DATE = FIN_DATE;
        this.DIR_REV = DIR_REV;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_MNGR_EVAL_SCHEDULEVO() {
    }

    /** minimal constructor */
    public TBPMS_MNGR_EVAL_SCHEDULEVO(com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEPK comp_id, String EMP_ID) {
        this.comp_id = comp_id;
        this.EMP_ID = EMP_ID;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public Timestamp getCOACH_DATE() {
        return this.COACH_DATE;
    }

    public void setCOACH_DATE(Timestamp COACH_DATE) {
        this.COACH_DATE = COACH_DATE;
    }

    public String getREGION_CENTER_ID() {
        return this.REGION_CENTER_ID;
    }

    public void setREGION_CENTER_ID(String REGION_CENTER_ID) {
        this.REGION_CENTER_ID = REGION_CENTER_ID;
    }

    public String getREGION_CENTER_NAME() {
        return this.REGION_CENTER_NAME;
    }

    public void setREGION_CENTER_NAME(String REGION_CENTER_NAME) {
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
    }

    public String getBRANCH_AREA_ID() {
        return this.BRANCH_AREA_ID;
    }

    public void setBRANCH_AREA_ID(String BRANCH_AREA_ID) {
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
    }

    public String getBRANCH_AREA_NAME() {
        return this.BRANCH_AREA_NAME;
    }

    public void setBRANCH_AREA_NAME(String BRANCH_AREA_NAME) {
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
    }

    public String getBRANCH_ID() {
        return this.BRANCH_ID;
    }

    public void setBRANCH_ID(String BRANCH_ID) {
        this.BRANCH_ID = BRANCH_ID;
    }

    public String getBRANCH_NAME() {
        return this.BRANCH_NAME;
    }

    public void setBRANCH_NAME(String BRANCH_NAME) {
        this.BRANCH_NAME = BRANCH_NAME;
    }

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getJOB_TITLE() {
        return this.JOB_TITLE;
    }

    public void setJOB_TITLE(String JOB_TITLE) {
        this.JOB_TITLE = JOB_TITLE;
    }

    public String getCOACHING_FREQ() {
        return this.COACHING_FREQ;
    }

    public void setCOACHING_FREQ(String COACHING_FREQ) {
        this.COACHING_FREQ = COACHING_FREQ;
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

    public String getFIN_TYPE() {
        return this.FIN_TYPE;
    }

    public void setFIN_TYPE(String FIN_TYPE) {
        this.FIN_TYPE = FIN_TYPE;
    }

    public Timestamp getFIN_DATE() {
        return this.FIN_DATE;
    }

    public void setFIN_DATE(Timestamp FIN_DATE) {
        this.FIN_DATE = FIN_DATE;
    }

    public String getDIR_REV() {
        return this.DIR_REV;
    }

    public void setDIR_REV(String DIR_REV) {
        this.DIR_REV = DIR_REV;
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
        if ( !(other instanceof TBPMS_MNGR_EVAL_SCHEDULEVO) ) return false;
        TBPMS_MNGR_EVAL_SCHEDULEVO castOther = (TBPMS_MNGR_EVAL_SCHEDULEVO) other;
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
