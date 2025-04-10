package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_PRO_FUN_TR_PROD_TARVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_TARPK comp_id;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String OP_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String JOB_TITLE_ID;

    /** nullable persistent field */
    private Timestamp FIRST_DATE;

    /** nullable persistent field */
    private String JOB_TITLE;

    /** nullable persistent field */
    private BigDecimal TAR_AMOUNT;

    /** nullable persistent field */
    private Timestamp MAINTAIN_DATE;

    /** nullable persistent field */
    private String MAINTAIN_NAME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_TAR";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_PRO_FUN_TR_PROD_TARVO(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_TARPK comp_id, String REGION_CENTER_NAME, String OP_AREA_NAME, String BRANCH_NAME, String AO_CODE, String EMP_NAME, String JOB_TITLE_ID, Timestamp FIRST_DATE, String JOB_TITLE, BigDecimal TAR_AMOUNT, Timestamp MAINTAIN_DATE, String MAINTAIN_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.OP_AREA_NAME = OP_AREA_NAME;
        this.BRANCH_NAME = BRANCH_NAME;
        this.AO_CODE = AO_CODE;
        this.EMP_NAME = EMP_NAME;
        this.JOB_TITLE_ID = JOB_TITLE_ID;
        this.FIRST_DATE = FIRST_DATE;
        this.JOB_TITLE = JOB_TITLE;
        this.TAR_AMOUNT = TAR_AMOUNT;
        this.MAINTAIN_DATE = MAINTAIN_DATE;
        this.MAINTAIN_NAME = MAINTAIN_NAME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_PRO_FUN_TR_PROD_TARVO() {
    }

    /** minimal constructor */
    public TBPMS_PRO_FUN_TR_PROD_TARVO(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_TARPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_TARPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_TARPK comp_id) {
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

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getJOB_TITLE_ID() {
        return this.JOB_TITLE_ID;
    }

    public void setJOB_TITLE_ID(String JOB_TITLE_ID) {
        this.JOB_TITLE_ID = JOB_TITLE_ID;
    }

    public Timestamp getFIRST_DATE() {
        return this.FIRST_DATE;
    }

    public void setFIRST_DATE(Timestamp FIRST_DATE) {
        this.FIRST_DATE = FIRST_DATE;
    }

    public String getJOB_TITLE() {
        return this.JOB_TITLE;
    }

    public void setJOB_TITLE(String JOB_TITLE) {
        this.JOB_TITLE = JOB_TITLE;
    }

    public BigDecimal getTAR_AMOUNT() {
        return this.TAR_AMOUNT;
    }

    public void setTAR_AMOUNT(BigDecimal TAR_AMOUNT) {
        this.TAR_AMOUNT = TAR_AMOUNT;
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

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_PRO_FUN_TR_PROD_TARVO) ) return false;
        TBPMS_PRO_FUN_TR_PROD_TARVO castOther = (TBPMS_PRO_FUN_TR_PROD_TARVO) other;
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
