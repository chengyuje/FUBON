package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_SALES_PLANVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANPK comp_id;

    /** nullable persistent field */
    private String PLAN_YEARMON;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String SRC_TYPE;

    /** nullable persistent field */
    private BigDecimal CF_AMT;

    /** nullable persistent field */
    private String EST_PRD;

    /** nullable persistent field */
    private BigDecimal EST_AMT;

    /** nullable persistent field */
    private BigDecimal EST_EARNINGS_RATE;

    /** nullable persistent field */
    private BigDecimal EST_EARNINGS;

    /** nullable persistent field */
    private Timestamp ACTION_DATE;

    /** nullable persistent field */
    private Timestamp MEETING_DATE_S;

    /** nullable persistent field */
    private Timestamp MEETING_DATE_E;

    /** nullable persistent field */
    private Timestamp CLOSE_DATE;

    /** nullable persistent field */
    private String OS_FLAG;

    /** nullable persistent field */
    private String PFD_FLAG;

    /** nullable persistent field */
    private String BE_FLAG;

    /** nullable persistent field */
    private String FA_FLAG;

    /** nullable persistent field */
    private String IA_FLAG;

    /** nullable persistent field */
    private String VISIT_PURPOSE;

    /** nullable persistent field */
    private String VISIT_PURPOSE_OTHER;

    /** nullable persistent field */
    private String KEY_ISSUE;

    /** nullable persistent field */
    private String CLOSE_FLAG;

    /** nullable persistent field */
    private String CLOSE_C_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLAN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_SALES_PLANVO(com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANPK comp_id, String PLAN_YEARMON, String BRANCH_NBR, String AO_CODE, String EMP_ID, String EMP_NAME, String CUST_NAME, String SRC_TYPE, BigDecimal CF_AMT, String EST_PRD, BigDecimal EST_AMT, BigDecimal EST_EARNINGS_RATE, BigDecimal EST_EARNINGS, Timestamp ACTION_DATE, Timestamp MEETING_DATE_S, Timestamp MEETING_DATE_E, Timestamp CLOSE_DATE, String OS_FLAG, String PFD_FLAG, String BE_FLAG, String FA_FLAG, String IA_FLAG, String VISIT_PURPOSE, String VISIT_PURPOSE_OTHER, String KEY_ISSUE, String CLOSE_FLAG, String CLOSE_C_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PLAN_YEARMON = PLAN_YEARMON;
        this.BRANCH_NBR = BRANCH_NBR;
        this.AO_CODE = AO_CODE;
        this.EMP_ID = EMP_ID;
        this.EMP_NAME = EMP_NAME;
        this.CUST_NAME = CUST_NAME;
        this.SRC_TYPE = SRC_TYPE;
        this.CF_AMT = CF_AMT;
        this.EST_PRD = EST_PRD;
        this.EST_AMT = EST_AMT;
        this.EST_EARNINGS_RATE = EST_EARNINGS_RATE;
        this.EST_EARNINGS = EST_EARNINGS;
        this.ACTION_DATE = ACTION_DATE;
        this.MEETING_DATE_S = MEETING_DATE_S;
        this.MEETING_DATE_E = MEETING_DATE_E;
        this.CLOSE_DATE = CLOSE_DATE;
        this.OS_FLAG = OS_FLAG;
        this.PFD_FLAG = PFD_FLAG;
        this.BE_FLAG = BE_FLAG;
        this.FA_FLAG = FA_FLAG;
        this.IA_FLAG = IA_FLAG;
        this.VISIT_PURPOSE = VISIT_PURPOSE;
        this.VISIT_PURPOSE_OTHER = VISIT_PURPOSE_OTHER;
        this.KEY_ISSUE = KEY_ISSUE;
        this.CLOSE_FLAG = CLOSE_FLAG;
        this.CLOSE_C_DATE = CLOSE_C_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_SALES_PLANVO() {
    }

    /** minimal constructor */
    public TBPMS_SALES_PLANVO(com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPLAN_YEARMON() {
        return this.PLAN_YEARMON;
    }

    public void setPLAN_YEARMON(String PLAN_YEARMON) {
        this.PLAN_YEARMON = PLAN_YEARMON;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
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

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public String getSRC_TYPE() {
        return this.SRC_TYPE;
    }

    public void setSRC_TYPE(String SRC_TYPE) {
        this.SRC_TYPE = SRC_TYPE;
    }

    public BigDecimal getCF_AMT() {
        return this.CF_AMT;
    }

    public void setCF_AMT(BigDecimal CF_AMT) {
        this.CF_AMT = CF_AMT;
    }

    public String getEST_PRD() {
        return this.EST_PRD;
    }

    public void setEST_PRD(String EST_PRD) {
        this.EST_PRD = EST_PRD;
    }

    public BigDecimal getEST_AMT() {
        return this.EST_AMT;
    }

    public void setEST_AMT(BigDecimal EST_AMT) {
        this.EST_AMT = EST_AMT;
    }

    public BigDecimal getEST_EARNINGS_RATE() {
        return this.EST_EARNINGS_RATE;
    }

    public void setEST_EARNINGS_RATE(BigDecimal EST_EARNINGS_RATE) {
        this.EST_EARNINGS_RATE = EST_EARNINGS_RATE;
    }

    public BigDecimal getEST_EARNINGS() {
        return this.EST_EARNINGS;
    }

    public void setEST_EARNINGS(BigDecimal EST_EARNINGS) {
        this.EST_EARNINGS = EST_EARNINGS;
    }

    public Timestamp getACTION_DATE() {
        return this.ACTION_DATE;
    }

    public void setACTION_DATE(Timestamp ACTION_DATE) {
        this.ACTION_DATE = ACTION_DATE;
    }

    public Timestamp getMEETING_DATE_S() {
        return this.MEETING_DATE_S;
    }

    public void setMEETING_DATE_S(Timestamp MEETING_DATE_S) {
        this.MEETING_DATE_S = MEETING_DATE_S;
    }

    public Timestamp getMEETING_DATE_E() {
        return this.MEETING_DATE_E;
    }

    public void setMEETING_DATE_E(Timestamp MEETING_DATE_E) {
        this.MEETING_DATE_E = MEETING_DATE_E;
    }

    public Timestamp getCLOSE_DATE() {
        return this.CLOSE_DATE;
    }

    public void setCLOSE_DATE(Timestamp CLOSE_DATE) {
        this.CLOSE_DATE = CLOSE_DATE;
    }

    public String getOS_FLAG() {
        return this.OS_FLAG;
    }

    public void setOS_FLAG(String OS_FLAG) {
        this.OS_FLAG = OS_FLAG;
    }

    public String getPFD_FLAG() {
        return this.PFD_FLAG;
    }

    public void setPFD_FLAG(String PFD_FLAG) {
        this.PFD_FLAG = PFD_FLAG;
    }

    public String getBE_FLAG() {
        return this.BE_FLAG;
    }

    public void setBE_FLAG(String BE_FLAG) {
        this.BE_FLAG = BE_FLAG;
    }

    public String getFA_FLAG() {
        return this.FA_FLAG;
    }

    public void setFA_FLAG(String FA_FLAG) {
        this.FA_FLAG = FA_FLAG;
    }

    public String getIA_FLAG() {
        return this.IA_FLAG;
    }

    public void setIA_FLAG(String IA_FLAG) {
        this.IA_FLAG = IA_FLAG;
    }

    public String getVISIT_PURPOSE() {
        return this.VISIT_PURPOSE;
    }

    public void setVISIT_PURPOSE(String VISIT_PURPOSE) {
        this.VISIT_PURPOSE = VISIT_PURPOSE;
    }

    public String getVISIT_PURPOSE_OTHER() {
        return this.VISIT_PURPOSE_OTHER;
    }

    public void setVISIT_PURPOSE_OTHER(String VISIT_PURPOSE_OTHER) {
        this.VISIT_PURPOSE_OTHER = VISIT_PURPOSE_OTHER;
    }

    public String getKEY_ISSUE() {
        return this.KEY_ISSUE;
    }

    public void setKEY_ISSUE(String KEY_ISSUE) {
        this.KEY_ISSUE = KEY_ISSUE;
    }

    public String getCLOSE_FLAG() {
        return this.CLOSE_FLAG;
    }

    public void setCLOSE_FLAG(String CLOSE_FLAG) {
        this.CLOSE_FLAG = CLOSE_FLAG;
    }

    public String getCLOSE_C_DATE() {
        return this.CLOSE_C_DATE;
    }

    public void setCLOSE_C_DATE(String CLOSE_C_DATE) {
        this.CLOSE_C_DATE = CLOSE_C_DATE;
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
        if ( !(other instanceof TBPMS_SALES_PLANVO) ) return false;
        TBPMS_SALES_PLANVO castOther = (TBPMS_SALES_PLANVO) other;
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
