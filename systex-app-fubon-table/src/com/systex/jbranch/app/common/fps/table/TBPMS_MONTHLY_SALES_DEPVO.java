package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MONTHLY_SALES_DEPVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_SALES_DEPPK comp_id;

    /** nullable persistent field */
    private String NAME;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private BigDecimal AMT_ORGD;

    /** nullable persistent field */
    private BigDecimal AMT_NTD;

    /** nullable persistent field */
    private String SUPERVISOR_FLAG;

    /** nullable persistent field */
    private String NOTE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_SALES_DEP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_MONTHLY_SALES_DEPVO(com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_SALES_DEPPK comp_id, String NAME, String EMP_ID, String AO_CODE, BigDecimal AMT_ORGD, BigDecimal AMT_NTD, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String SUPERVISOR_FLAG, String NOTE, Long version) {
        this.comp_id = comp_id;
        this.NAME = NAME;
        this.EMP_ID = EMP_ID;
        this.AO_CODE = AO_CODE;
        this.AMT_ORGD = AMT_ORGD;
        this.AMT_NTD = AMT_NTD;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.SUPERVISOR_FLAG = SUPERVISOR_FLAG;
        this.NOTE = NOTE;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_MONTHLY_SALES_DEPVO() {
    }

    /** minimal constructor */
    public TBPMS_MONTHLY_SALES_DEPVO(com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_SALES_DEPPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_SALES_DEPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_SALES_DEPPK comp_id) {
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

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public BigDecimal getAMT_ORGD() {
        return this.AMT_ORGD;
    }

    public void setAMT_ORGD(BigDecimal AMT_ORGD) {
        this.AMT_ORGD = AMT_ORGD;
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
        if ( !(other instanceof TBPMS_MONTHLY_SALES_DEPVO) ) return false;
        TBPMS_MONTHLY_SALES_DEPVO castOther = (TBPMS_MONTHLY_SALES_DEPVO) other;
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
