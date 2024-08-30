package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_POT_CF_DTL_INVVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_DTL_INVPK comp_id;

    /** nullable persistent field */
    private String PRD_TYPE;

    /** nullable persistent field */
    private String PRD_DTL;

    /** nullable persistent field */
    private String CRCY_TYPE;

    /** nullable persistent field */
    private BigDecimal BAL_ORGD;

    /** nullable persistent field */
    private BigDecimal BAL_NTD;

    /** nullable persistent field */
    private BigDecimal INT_RET;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_DTL_INV";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_POT_CF_DTL_INVVO(com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_DTL_INVPK comp_id, String PRD_TYPE, String PRD_DTL, String CRCY_TYPE, BigDecimal BAL_ORGD, BigDecimal BAL_NTD, BigDecimal INT_RET, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PRD_TYPE = PRD_TYPE;
        this.PRD_DTL = PRD_DTL;
        this.CRCY_TYPE = CRCY_TYPE;
        this.BAL_ORGD = BAL_ORGD;
        this.BAL_NTD = BAL_NTD;
        this.INT_RET = INT_RET;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_POT_CF_DTL_INVVO() {
    }

    /** minimal constructor */
    public TBPMS_POT_CF_DTL_INVVO(com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_DTL_INVPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_DTL_INVPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_DTL_INVPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPRD_TYPE() {
        return this.PRD_TYPE;
    }

    public void setPRD_TYPE(String PRD_TYPE) {
        this.PRD_TYPE = PRD_TYPE;
    }

    public String getPRD_DTL() {
        return this.PRD_DTL;
    }

    public void setPRD_DTL(String PRD_DTL) {
        this.PRD_DTL = PRD_DTL;
    }

    public String getCRCY_TYPE() {
        return this.CRCY_TYPE;
    }

    public void setCRCY_TYPE(String CRCY_TYPE) {
        this.CRCY_TYPE = CRCY_TYPE;
    }

    public BigDecimal getBAL_ORGD() {
        return this.BAL_ORGD;
    }

    public void setBAL_ORGD(BigDecimal BAL_ORGD) {
        this.BAL_ORGD = BAL_ORGD;
    }

    public BigDecimal getBAL_NTD() {
        return this.BAL_NTD;
    }

    public void setBAL_NTD(BigDecimal BAL_NTD) {
        this.BAL_NTD = BAL_NTD;
    }

    public BigDecimal getINT_RET() {
        return this.INT_RET;
    }

    public void setINT_RET(BigDecimal INT_RET) {
        this.INT_RET = INT_RET;
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
        if ( !(other instanceof TBPMS_POT_CF_DTL_INVVO) ) return false;
        TBPMS_POT_CF_DTL_INVVO castOther = (TBPMS_POT_CF_DTL_INVVO) other;
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
