package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_CSM_NFI_SCOREVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCOREPK comp_id;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String SATISFACTION_W;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private BigDecimal DEDUCTION_INITIAL;

    /** nullable persistent field */
    private BigDecimal DEDUCTION_FINAL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCORE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSQM_CSM_NFI_SCOREVO(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCOREPK comp_id, String BRANCH_NBR, String SATISFACTION_W, String EMP_NAME, String EMP_ID, BigDecimal DEDUCTION_INITIAL, BigDecimal DEDUCTION_FINAL, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.BRANCH_NBR = BRANCH_NBR;
        this.SATISFACTION_W = SATISFACTION_W;
        this.EMP_NAME = EMP_NAME;
        this.EMP_ID = EMP_ID;
        this.DEDUCTION_INITIAL = DEDUCTION_INITIAL;
        this.DEDUCTION_FINAL = DEDUCTION_FINAL;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSQM_CSM_NFI_SCOREVO() {
    }

    /** minimal constructor */
    public TBSQM_CSM_NFI_SCOREVO(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCOREPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCOREPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCOREPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getSATISFACTION_W() {
        return this.SATISFACTION_W;
    }

    public void setSATISFACTION_W(String SATISFACTION_W) {
        this.SATISFACTION_W = SATISFACTION_W;
    }

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public BigDecimal getDEDUCTION_INITIAL() {
        return this.DEDUCTION_INITIAL;
    }

    public void setDEDUCTION_INITIAL(BigDecimal DEDUCTION_INITIAL) {
        this.DEDUCTION_INITIAL = DEDUCTION_INITIAL;
    }

    public BigDecimal getDEDUCTION_FINAL() {
        return this.DEDUCTION_FINAL;
    }

    public void setDEDUCTION_FINAL(BigDecimal DEDUCTION_FINAL) {
        this.DEDUCTION_FINAL = DEDUCTION_FINAL;
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
        if ( !(other instanceof TBSQM_CSM_NFI_SCOREVO) ) return false;
        TBSQM_CSM_NFI_SCOREVO castOther = (TBSQM_CSM_NFI_SCOREVO) other;
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
