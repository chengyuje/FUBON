package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_EXCEPT_KYC_RPTVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_EXCEPT_KYC_RPTPK comp_id;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String OP_AREA_ID;

    /** nullable persistent field */
    private String OP_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private Timestamp TEST_DATE;

    /** nullable persistent field */
    private Timestamp REVIEW_DATE;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String RISK_TLRC;

    /** nullable persistent field */
    private String OUT_KYC_FLAG;

    /** nullable persistent field */
    private String DEL_KYC_FLAG;

    /** nullable persistent field */
    private String DEL_REASON;

    /** nullable persistent field */
    private String SUPERVISOR_FLAG;

    /** nullable persistent field */
    private Timestamp REVIEW_TIME;

    /** nullable persistent field */
    private String REVIEW_MAN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_EXCEPT_KYC_RPT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_EXCEPT_KYC_RPTVO(com.systex.jbranch.app.common.fps.table.TBPMS_EXCEPT_KYC_RPTPK comp_id, String REGION_CENTER_ID, String REGION_CENTER_NAME, String OP_AREA_ID, String OP_AREA_NAME, String BRANCH_NAME, String AO_CODE, String EMP_ID, Timestamp TEST_DATE, Timestamp REVIEW_DATE, String CUST_NAME, String RISK_TLRC, String OUT_KYC_FLAG, String DEL_KYC_FLAG, String DEL_REASON, String SUPERVISOR_FLAG, Timestamp REVIEW_TIME, String REVIEW_MAN, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.OP_AREA_ID = OP_AREA_ID;
        this.OP_AREA_NAME = OP_AREA_NAME;
        this.BRANCH_NAME = BRANCH_NAME;
        this.AO_CODE = AO_CODE;
        this.EMP_ID = EMP_ID;
        this.TEST_DATE = TEST_DATE;
        this.REVIEW_DATE = REVIEW_DATE;
        this.CUST_NAME = CUST_NAME;
        this.RISK_TLRC = RISK_TLRC;
        this.OUT_KYC_FLAG = OUT_KYC_FLAG;
        this.DEL_KYC_FLAG = DEL_KYC_FLAG;
        this.DEL_REASON = DEL_REASON;
        this.SUPERVISOR_FLAG = SUPERVISOR_FLAG;
        this.REVIEW_TIME = REVIEW_TIME;
        this.REVIEW_MAN = REVIEW_MAN;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_EXCEPT_KYC_RPTVO() {
    }

    /** minimal constructor */
    public TBPMS_EXCEPT_KYC_RPTVO(com.systex.jbranch.app.common.fps.table.TBPMS_EXCEPT_KYC_RPTPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_EXCEPT_KYC_RPTPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_EXCEPT_KYC_RPTPK comp_id) {
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

    public String getOP_AREA_ID() {
        return this.OP_AREA_ID;
    }

    public void setOP_AREA_ID(String OP_AREA_ID) {
        this.OP_AREA_ID = OP_AREA_ID;
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

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public Timestamp getTEST_DATE() {
        return this.TEST_DATE;
    }

    public void setTEST_DATE(Timestamp TEST_DATE) {
        this.TEST_DATE = TEST_DATE;
    }

    public Timestamp getREVIEW_DATE() {
        return this.REVIEW_DATE;
    }

    public void setREVIEW_DATE(Timestamp REVIEW_DATE) {
        this.REVIEW_DATE = REVIEW_DATE;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public String getRISK_TLRC() {
        return this.RISK_TLRC;
    }

    public void setRISK_TLRC(String RISK_TLRC) {
        this.RISK_TLRC = RISK_TLRC;
    }

    public String getOUT_KYC_FLAG() {
        return this.OUT_KYC_FLAG;
    }

    public void setOUT_KYC_FLAG(String OUT_KYC_FLAG) {
        this.OUT_KYC_FLAG = OUT_KYC_FLAG;
    }

    public String getDEL_KYC_FLAG() {
        return this.DEL_KYC_FLAG;
    }

    public void setDEL_KYC_FLAG(String DEL_KYC_FLAG) {
        this.DEL_KYC_FLAG = DEL_KYC_FLAG;
    }

    public String getDEL_REASON() {
        return this.DEL_REASON;
    }

    public void setDEL_REASON(String DEL_REASON) {
        this.DEL_REASON = DEL_REASON;
    }

    public String getSUPERVISOR_FLAG() {
        return this.SUPERVISOR_FLAG;
    }

    public void setSUPERVISOR_FLAG(String SUPERVISOR_FLAG) {
        this.SUPERVISOR_FLAG = SUPERVISOR_FLAG;
    }

    public Timestamp getREVIEW_TIME() {
        return this.REVIEW_TIME;
    }

    public void setREVIEW_TIME(Timestamp REVIEW_TIME) {
        this.REVIEW_TIME = REVIEW_TIME;
    }

    public String getREVIEW_MAN() {
        return this.REVIEW_MAN;
    }

    public void setREVIEW_MAN(String REVIEW_MAN) {
        this.REVIEW_MAN = REVIEW_MAN;
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
        if ( !(other instanceof TBPMS_EXCEPT_KYC_RPTVO) ) return false;
        TBPMS_EXCEPT_KYC_RPTVO castOther = (TBPMS_EXCEPT_KYC_RPTVO) other;
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
