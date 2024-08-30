package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_INS_TARGET_SETVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_INS_TARGET_SETPK comp_id;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String BRANCH_CLS;

    /** nullable persistent field */
    private BigDecimal IV_TAR_AMT;

    /** nullable persistent field */
    private BigDecimal IV_TAR_FEE;

    /** nullable persistent field */
    private BigDecimal OT_TAR_AMT;

    /** nullable persistent field */
    private BigDecimal OT_TAR_FEE;

    /** nullable persistent field */
    private BigDecimal SY_TAR_AMT;

    /** nullable persistent field */
    private BigDecimal SY_TAR_FEE;

    /** nullable persistent field */
    private BigDecimal LY_TAR_AMT;

    /** nullable persistent field */
    private BigDecimal LY_TAR_FEE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_INS_TARGET_SET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_INS_TARGET_SETVO(com.systex.jbranch.app.common.fps.table.TBPMS_INS_TARGET_SETPK comp_id, String REGION_CENTER_ID, String REGION_CENTER_NAME, String BRANCH_AREA_ID, String BRANCH_AREA_NAME, String BRANCH_NAME, String BRANCH_CLS, BigDecimal IV_TAR_AMT, BigDecimal IV_TAR_FEE, BigDecimal OT_TAR_AMT, BigDecimal OT_TAR_FEE, BigDecimal SY_TAR_AMT, BigDecimal SY_TAR_FEE, BigDecimal LY_TAR_AMT, BigDecimal LY_TAR_FEE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
        this.BRANCH_NAME = BRANCH_NAME;
        this.BRANCH_CLS = BRANCH_CLS;
        this.IV_TAR_AMT = IV_TAR_AMT;
        this.IV_TAR_FEE = IV_TAR_FEE;
        this.OT_TAR_AMT = OT_TAR_AMT;
        this.OT_TAR_FEE = OT_TAR_FEE;
        this.SY_TAR_AMT = SY_TAR_AMT;
        this.SY_TAR_FEE = SY_TAR_FEE;
        this.LY_TAR_AMT = LY_TAR_AMT;
        this.LY_TAR_FEE = LY_TAR_FEE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_INS_TARGET_SETVO() {
    }

    /** minimal constructor */
    public TBPMS_INS_TARGET_SETVO(com.systex.jbranch.app.common.fps.table.TBPMS_INS_TARGET_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_INS_TARGET_SETPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_INS_TARGET_SETPK comp_id) {
        this.comp_id = comp_id;
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

    public String getBRANCH_NAME() {
        return this.BRANCH_NAME;
    }

    public void setBRANCH_NAME(String BRANCH_NAME) {
        this.BRANCH_NAME = BRANCH_NAME;
    }

    public String getBRANCH_CLS() {
        return this.BRANCH_CLS;
    }

    public void setBRANCH_CLS(String BRANCH_CLS) {
        this.BRANCH_CLS = BRANCH_CLS;
    }

    public BigDecimal getIV_TAR_AMT() {
        return this.IV_TAR_AMT;
    }

    public void setIV_TAR_AMT(BigDecimal IV_TAR_AMT) {
        this.IV_TAR_AMT = IV_TAR_AMT;
    }

    public BigDecimal getIV_TAR_FEE() {
        return this.IV_TAR_FEE;
    }

    public void setIV_TAR_FEE(BigDecimal IV_TAR_FEE) {
        this.IV_TAR_FEE = IV_TAR_FEE;
    }

    public BigDecimal getOT_TAR_AMT() {
        return this.OT_TAR_AMT;
    }

    public void setOT_TAR_AMT(BigDecimal OT_TAR_AMT) {
        this.OT_TAR_AMT = OT_TAR_AMT;
    }

    public BigDecimal getOT_TAR_FEE() {
        return this.OT_TAR_FEE;
    }

    public void setOT_TAR_FEE(BigDecimal OT_TAR_FEE) {
        this.OT_TAR_FEE = OT_TAR_FEE;
    }

    public BigDecimal getSY_TAR_AMT() {
        return this.SY_TAR_AMT;
    }

    public void setSY_TAR_AMT(BigDecimal SY_TAR_AMT) {
        this.SY_TAR_AMT = SY_TAR_AMT;
    }

    public BigDecimal getSY_TAR_FEE() {
        return this.SY_TAR_FEE;
    }

    public void setSY_TAR_FEE(BigDecimal SY_TAR_FEE) {
        this.SY_TAR_FEE = SY_TAR_FEE;
    }

    public BigDecimal getLY_TAR_AMT() {
        return this.LY_TAR_AMT;
    }

    public void setLY_TAR_AMT(BigDecimal LY_TAR_AMT) {
        this.LY_TAR_AMT = LY_TAR_AMT;
    }

    public BigDecimal getLY_TAR_FEE() {
        return this.LY_TAR_FEE;
    }

    public void setLY_TAR_FEE(BigDecimal LY_TAR_FEE) {
        this.LY_TAR_FEE = LY_TAR_FEE;
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
        if ( !(other instanceof TBPMS_INS_TARGET_SETVO) ) return false;
        TBPMS_INS_TARGET_SETVO castOther = (TBPMS_INS_TARGET_SETVO) other;
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
