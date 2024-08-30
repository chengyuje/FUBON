package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_PRO_FUN_TR_PROD_SETVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETPK comp_id;

    /** nullable persistent field */
    private BigDecimal LEVEL_DISTANCE;

    /** nullable persistent field */
    private BigDecimal PRODUCT_GOALS;

    /** nullable persistent field */
    private BigDecimal ADVAN_GOALS;

    /** nullable persistent field */
    private BigDecimal PRO_STR_LINE;

    /** nullable persistent field */
    private BigDecimal PRO_HOR_LINE;

    /** nullable persistent field */
    private BigDecimal PRO_SLA_LINE;

    /** nullable persistent field */
    private BigDecimal DEM_STR_LINE;

    /** nullable persistent field */
    private BigDecimal DEM_HOR_LINE;

    /** nullable persistent field */
    private BigDecimal DEM_SLA_LINE;

    /** nullable persistent field */
    private BigDecimal FIX_SAL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_PRO_FUN_TR_PROD_SETVO(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETPK comp_id, BigDecimal LEVEL_DISTANCE, BigDecimal PRODUCT_GOALS, BigDecimal ADVAN_GOALS, BigDecimal PRO_STR_LINE, BigDecimal PRO_HOR_LINE, BigDecimal PRO_SLA_LINE, BigDecimal DEM_STR_LINE, BigDecimal DEM_HOR_LINE, BigDecimal DEM_SLA_LINE, BigDecimal FIX_SAL, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.LEVEL_DISTANCE = LEVEL_DISTANCE;
        this.PRODUCT_GOALS = PRODUCT_GOALS;
        this.ADVAN_GOALS = ADVAN_GOALS;
        this.PRO_STR_LINE = PRO_STR_LINE;
        this.PRO_HOR_LINE = PRO_HOR_LINE;
        this.PRO_SLA_LINE = PRO_SLA_LINE;
        this.DEM_STR_LINE = DEM_STR_LINE;
        this.DEM_HOR_LINE = DEM_HOR_LINE;
        this.DEM_SLA_LINE = DEM_SLA_LINE;
        this.FIX_SAL = FIX_SAL;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_PRO_FUN_TR_PROD_SETVO() {
    }

    /** minimal constructor */
    public TBPMS_PRO_FUN_TR_PROD_SETVO(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getLEVEL_DISTANCE() {
        return this.LEVEL_DISTANCE;
    }

    public void setLEVEL_DISTANCE(BigDecimal LEVEL_DISTANCE) {
        this.LEVEL_DISTANCE = LEVEL_DISTANCE;
    }

    public BigDecimal getPRODUCT_GOALS() {
        return this.PRODUCT_GOALS;
    }

    public void setPRODUCT_GOALS(BigDecimal PRODUCT_GOALS) {
        this.PRODUCT_GOALS = PRODUCT_GOALS;
    }

    public BigDecimal getADVAN_GOALS() {
        return this.ADVAN_GOALS;
    }

    public void setADVAN_GOALS(BigDecimal ADVAN_GOALS) {
        this.ADVAN_GOALS = ADVAN_GOALS;
    }

    public BigDecimal getPRO_STR_LINE() {
        return this.PRO_STR_LINE;
    }

    public void setPRO_STR_LINE(BigDecimal PRO_STR_LINE) {
        this.PRO_STR_LINE = PRO_STR_LINE;
    }

    public BigDecimal getPRO_HOR_LINE() {
        return this.PRO_HOR_LINE;
    }

    public void setPRO_HOR_LINE(BigDecimal PRO_HOR_LINE) {
        this.PRO_HOR_LINE = PRO_HOR_LINE;
    }

    public BigDecimal getPRO_SLA_LINE() {
        return this.PRO_SLA_LINE;
    }

    public void setPRO_SLA_LINE(BigDecimal PRO_SLA_LINE) {
        this.PRO_SLA_LINE = PRO_SLA_LINE;
    }

    public BigDecimal getDEM_STR_LINE() {
        return this.DEM_STR_LINE;
    }

    public void setDEM_STR_LINE(BigDecimal DEM_STR_LINE) {
        this.DEM_STR_LINE = DEM_STR_LINE;
    }

    public BigDecimal getDEM_HOR_LINE() {
        return this.DEM_HOR_LINE;
    }

    public void setDEM_HOR_LINE(BigDecimal DEM_HOR_LINE) {
        this.DEM_HOR_LINE = DEM_HOR_LINE;
    }

    public BigDecimal getDEM_SLA_LINE() {
        return this.DEM_SLA_LINE;
    }

    public void setDEM_SLA_LINE(BigDecimal DEM_SLA_LINE) {
        this.DEM_SLA_LINE = DEM_SLA_LINE;
    }

    public BigDecimal getFIX_SAL() {
        return this.FIX_SAL;
    }

    public void setFIX_SAL(BigDecimal FIX_SAL) {
        this.FIX_SAL = FIX_SAL;
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
        if ( !(other instanceof TBPMS_PRO_FUN_TR_PROD_SETVO) ) return false;
        TBPMS_PRO_FUN_TR_PROD_SETVO castOther = (TBPMS_PRO_FUN_TR_PROD_SETVO) other;
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
