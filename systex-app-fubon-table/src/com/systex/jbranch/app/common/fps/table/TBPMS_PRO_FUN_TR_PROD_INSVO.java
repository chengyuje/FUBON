package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_PRO_FUN_TR_PROD_INSVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_INSPK comp_id;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String OP_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private BigDecimal INV_TAR_AMOUNT;

    /** nullable persistent field */
    private BigDecimal INS_TAR_AMOUNT;

    /** nullable persistent field */
    private BigDecimal TOT_TAR_AMOUNT;

    /** nullable persistent field */
    private Timestamp MAINTAIN_DATE;

    /** nullable persistent field */
    private String MAINTAIN_NAME;

    /** nullable persistent field */
    private String NOTE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_INS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_PRO_FUN_TR_PROD_INSVO(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_INSPK comp_id, String REGION_CENTER_NAME, String OP_AREA_NAME, String BRANCH_NAME, BigDecimal INV_TAR_AMOUNT, BigDecimal INS_TAR_AMOUNT, BigDecimal TOT_TAR_AMOUNT, Timestamp MAINTAIN_DATE, String MAINTAIN_NAME, String NOTE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.OP_AREA_NAME = OP_AREA_NAME;
        this.BRANCH_NAME = BRANCH_NAME;
        this.INV_TAR_AMOUNT = INV_TAR_AMOUNT;
        this.INS_TAR_AMOUNT = INS_TAR_AMOUNT;
        this.TOT_TAR_AMOUNT = TOT_TAR_AMOUNT;
        this.MAINTAIN_DATE = MAINTAIN_DATE;
        this.MAINTAIN_NAME = MAINTAIN_NAME;
        this.NOTE = NOTE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_PRO_FUN_TR_PROD_INSVO() {
    }

    /** minimal constructor */
    public TBPMS_PRO_FUN_TR_PROD_INSVO(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_INSPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_INSPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_INSPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getREGION_CENTER_NAME() {
        return this.REGION_CENTER_NAME;
    }

    public void setREGION_CENTER_NAME(String REGION_CENTER_NAME) {
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
    }

    public String getOP_AREA_NAME() {
        return this.OP_AREA_NAME;
    }

    public void setOP_AREA_NAME(String OP_AREA_NAME) {
        this.OP_AREA_NAME = OP_AREA_NAME;
    }

    public String getBRANCH_NAME() {
        return this.BRANCH_NAME;
    }

    public void setBRANCH_NAME(String BRANCH_NAME) {
        this.BRANCH_NAME = BRANCH_NAME;
    }

    public BigDecimal getINV_TAR_AMOUNT() {
        return this.INV_TAR_AMOUNT;
    }

    public void setINV_TAR_AMOUNT(BigDecimal INV_TAR_AMOUNT) {
        this.INV_TAR_AMOUNT = INV_TAR_AMOUNT;
    }

    public BigDecimal getINS_TAR_AMOUNT() {
        return this.INS_TAR_AMOUNT;
    }

    public void setINS_TAR_AMOUNT(BigDecimal INS_TAR_AMOUNT) {
        this.INS_TAR_AMOUNT = INS_TAR_AMOUNT;
    }

    public BigDecimal getTOT_TAR_AMOUNT() {
        return this.TOT_TAR_AMOUNT;
    }

    public void setTOT_TAR_AMOUNT(BigDecimal TOT_TAR_AMOUNT) {
        this.TOT_TAR_AMOUNT = TOT_TAR_AMOUNT;
    }

    public Timestamp getMAINTAIN_DATE() {
        return this.MAINTAIN_DATE;
    }

    public void setMAINTAIN_DATE(Timestamp MAINTAIN_DATE) {
        this.MAINTAIN_DATE = MAINTAIN_DATE;
    }

    public String getMAINTAIN_NAME() {
        return this.MAINTAIN_NAME;
    }

    public void setMAINTAIN_NAME(String MAINTAIN_NAME) {
        this.MAINTAIN_NAME = MAINTAIN_NAME;
    }

    public String getNOTE() {
        return this.NOTE;
    }

    public void setNOTE(String NOTE) {
        this.NOTE = NOTE;
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
        if ( !(other instanceof TBPMS_PRO_FUN_TR_PROD_INSVO) ) return false;
        TBPMS_PRO_FUN_TR_PROD_INSVO castOther = (TBPMS_PRO_FUN_TR_PROD_INSVO) other;
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
