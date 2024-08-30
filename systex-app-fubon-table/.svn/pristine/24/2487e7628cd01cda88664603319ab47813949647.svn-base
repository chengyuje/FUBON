package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MONTHLY_KYC_RPTVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_KYC_RPTPK comp_id;

    /** nullable persistent field */
    private String SUPERVISOR_FLAG;

    /** nullable persistent field */
    private String NOTE;

    /** nullable persistent field */
    private String HR_ATTR;

    /** nullable persistent field */
    private String SIGNOFF_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_KYC_RPT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_MONTHLY_KYC_RPTVO(com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_KYC_RPTPK comp_id, String SUPERVISOR_FLAG, String NOTE, String HR_ATTR, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String SIGNOFF_DATE, Long version) {
        this.comp_id = comp_id;
        this.SUPERVISOR_FLAG = SUPERVISOR_FLAG;
        this.NOTE = NOTE;
        this.HR_ATTR = HR_ATTR;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.SIGNOFF_DATE = SIGNOFF_DATE;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_MONTHLY_KYC_RPTVO() {
    }

    /** minimal constructor */
    public TBPMS_MONTHLY_KYC_RPTVO(com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_KYC_RPTPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_KYC_RPTPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_KYC_RPTPK comp_id) {
        this.comp_id = comp_id;
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

    public String getHR_ATTR() {
        return this.HR_ATTR;
    }

    public void setHR_ATTR(String HR_ATTR) {
        this.HR_ATTR = HR_ATTR;
    }

    public String getSIGNOFF_DATE() {
        return this.SIGNOFF_DATE;
    }

    public void setSIGNOFF_DATE(String SIGNOFF_DATE) {
        this.SIGNOFF_DATE = SIGNOFF_DATE;
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
        if ( !(other instanceof TBPMS_MONTHLY_KYC_RPTVO) ) return false;
        TBPMS_MONTHLY_KYC_RPTVO castOther = (TBPMS_MONTHLY_KYC_RPTVO) other;
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
