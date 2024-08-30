package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_QUESTIONNAIRE_FLW_DETAILVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAILPK comp_id;

    /** nullable persistent field */
    private String SIGNOFF_ID;

    /** nullable persistent field */
    private String SIGNOFF_NAME;

    /** nullable persistent field */
    private String SIGNOFF_BANK;

    /** nullable persistent field */
    private String EMP_ROLE;

    /** nullable persistent field */
    private String REMARK;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAIL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBKYC_QUESTIONNAIRE_FLW_DETAILVO(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAILPK comp_id, String SIGNOFF_ID, String SIGNOFF_NAME, String SIGNOFF_BANK, String EMP_ROLE, String REMARK, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.comp_id = comp_id;
        this.SIGNOFF_ID = SIGNOFF_ID;
        this.SIGNOFF_NAME = SIGNOFF_NAME;
        this.SIGNOFF_BANK = SIGNOFF_BANK;
        this.EMP_ROLE = EMP_ROLE;
        this.REMARK = REMARK;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBKYC_QUESTIONNAIRE_FLW_DETAILVO() {
    }

    /** minimal constructor */
    public TBKYC_QUESTIONNAIRE_FLW_DETAILVO(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAILPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getSIGNOFF_ID() {
        return this.SIGNOFF_ID;
    }

    public void setSIGNOFF_ID(String SIGNOFF_ID) {
        this.SIGNOFF_ID = SIGNOFF_ID;
    }

    public String getSIGNOFF_NAME() {
        return this.SIGNOFF_NAME;
    }

    public void setSIGNOFF_NAME(String SIGNOFF_NAME) {
        this.SIGNOFF_NAME = SIGNOFF_NAME;
    }

    public String getSIGNOFF_BANK() {
        return this.SIGNOFF_BANK;
    }

    public void setSIGNOFF_BANK(String SIGNOFF_BANK) {
        this.SIGNOFF_BANK = SIGNOFF_BANK;
    }

    public String getEMP_ROLE() {
        return this.EMP_ROLE;
    }

    public void setEMP_ROLE(String EMP_ROLE) {
        this.EMP_ROLE = EMP_ROLE;
    }

    public String getREMARK() {
        return this.REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
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
        if ( !(other instanceof TBKYC_QUESTIONNAIRE_FLW_DETAILVO) ) return false;
        TBKYC_QUESTIONNAIRE_FLW_DETAILVO castOther = (TBKYC_QUESTIONNAIRE_FLW_DETAILVO) other;
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
