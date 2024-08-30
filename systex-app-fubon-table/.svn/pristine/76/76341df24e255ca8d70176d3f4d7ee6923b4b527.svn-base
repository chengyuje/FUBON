package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DAILY_SALES_DEP_N_UVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEP_N_UPK comp_id;

    /** nullable persistent field */
    private String NAME;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private BigDecimal AMT_NTD;

    /** nullable persistent field */
    private String SUPERVISOR_FLAG;

    /** nullable persistent field */
    private String NOTE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEP_N_U";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_DAILY_SALES_DEP_N_UVO(com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEP_N_UPK comp_id, String NAME, String EMP_ID, BigDecimal AMT_NTD, String SUPERVISOR_FLAG, String NOTE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.NAME = NAME;
        this.EMP_ID = EMP_ID;
        this.AMT_NTD = AMT_NTD;
        this.SUPERVISOR_FLAG = SUPERVISOR_FLAG;
        this.NOTE = NOTE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_DAILY_SALES_DEP_N_UVO() {
    }

    /** minimal constructor */
    public TBPMS_DAILY_SALES_DEP_N_UVO(com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEP_N_UPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEP_N_UPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEP_N_UPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getNAME() {
        return this.NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public BigDecimal getAMT_NTD() {
        return this.AMT_NTD;
    }

    public void setAMT_NTD(BigDecimal AMT_NTD) {
        this.AMT_NTD = AMT_NTD;
    }

    public String getSUPERVISOR_FLAG() {
        return this.SUPERVISOR_FLAG;
    }

    public void setSUPERVISOR_FLAG(String SUPERVISOR_FLAG) {
        this.SUPERVISOR_FLAG = SUPERVISOR_FLAG;
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
        if ( !(other instanceof TBPMS_DAILY_SALES_DEP_N_UVO) ) return false;
        TBPMS_DAILY_SALES_DEP_N_UVO castOther = (TBPMS_DAILY_SALES_DEP_N_UVO) other;
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
