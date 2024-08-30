package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_AO_TRC_TAR_MVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_AO_TRC_TAR_MPK comp_id;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String AO_JOB_RANK;

    /** nullable persistent field */
    private String MAIN_PRD_NAME;

    /** nullable persistent field */
    private BigDecimal TAR_AMT;

    /** nullable persistent field */
    private Timestamp MAINTAIN_DATE;

    /** nullable persistent field */
    private String MAINTAIN_NAME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_AO_TRC_TAR_M";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_AO_TRC_TAR_MVO(com.systex.jbranch.app.common.fps.table.TBPMS_AO_TRC_TAR_MPK comp_id, String REGION_CENTER_ID, String REGION_CENTER_NAME, String BRANCH_AREA_ID, String BRANCH_AREA_NAME, String BRANCH_NBR, String BRANCH_NAME, String AO_CODE, String AO_JOB_RANK, String MAIN_PRD_NAME, BigDecimal TAR_AMT, Timestamp MAINTAIN_DATE, String MAINTAIN_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.AO_CODE = AO_CODE;
        this.AO_JOB_RANK = AO_JOB_RANK;
        this.MAIN_PRD_NAME = MAIN_PRD_NAME;
        this.TAR_AMT = TAR_AMT;
        this.MAINTAIN_DATE = MAINTAIN_DATE;
        this.MAINTAIN_NAME = MAINTAIN_NAME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_AO_TRC_TAR_MVO() {
    }

    /** minimal constructor */
    public TBPMS_AO_TRC_TAR_MVO(com.systex.jbranch.app.common.fps.table.TBPMS_AO_TRC_TAR_MPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_AO_TRC_TAR_MPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_AO_TRC_TAR_MPK comp_id) {
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

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
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

    public String getAO_JOB_RANK() {
        return this.AO_JOB_RANK;
    }

    public void setAO_JOB_RANK(String AO_JOB_RANK) {
        this.AO_JOB_RANK = AO_JOB_RANK;
    }

    public String getMAIN_PRD_NAME() {
        return this.MAIN_PRD_NAME;
    }

    public void setMAIN_PRD_NAME(String MAIN_PRD_NAME) {
        this.MAIN_PRD_NAME = MAIN_PRD_NAME;
    }

    public BigDecimal getTAR_AMT() {
        return this.TAR_AMT;
    }

    public void setTAR_AMT(BigDecimal TAR_AMT) {
        this.TAR_AMT = TAR_AMT;
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
        if ( !(other instanceof TBPMS_AO_TRC_TAR_MVO) ) return false;
        TBPMS_AO_TRC_TAR_MVO castOther = (TBPMS_AO_TRC_TAR_MVO) other;
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
