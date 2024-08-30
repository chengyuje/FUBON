package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_CSM_IMPROVE_DTLVO extends VOBase {

    /** identifier field */
    private String CASE_NO;

    /** nullable persistent field */
    private String END_DATE;

    /** nullable persistent field */
    private String BRH_DESC;

    /** nullable persistent field */
    private BigDecimal WAITING_TIME;

    /** nullable persistent field */
    private BigDecimal WORKING_TIME;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String CUR_JOB;

    /** nullable persistent field */
    private BigDecimal CUR_JOB_Y;

    /** nullable persistent field */
    private BigDecimal CUR_JOB_M;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String SUP_EMP_NAME;

    /** nullable persistent field */
    private String SUP_EMP_ID;

    /** nullable persistent field */
    private String SUP_CUR_JOB;

    /** nullable persistent field */
    private String IMPROVE_DESC;

    /** nullable persistent field */
    private String OP_SUP_REMARK;

    /** nullable persistent field */
    private String RC_VICE_SUP_REMARK;

    /** nullable persistent field */
    private String LAST_VISIT_DATE;

    /** nullable persistent field */
    private String CON_DEGREE;

    /** nullable persistent field */
    private BigDecimal FRQ_DAY;

    /** nullable persistent field */
    private String DEDUCTION_INITIAL;

    /** nullable persistent field */
    private String RC_SUP_REMARK;

    /** nullable persistent field */
    private String HEADMGR_REMARK;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSQM_CSM_IMPROVE_DTL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSQM_CSM_IMPROVE_DTLVO(String CASE_NO, String END_DATE, String BRH_DESC, BigDecimal WAITING_TIME, BigDecimal WORKING_TIME, String EMP_NAME, String EMP_ID, String CUR_JOB, BigDecimal CUR_JOB_Y, BigDecimal CUR_JOB_M, String AO_CODE, String SUP_EMP_NAME, String SUP_EMP_ID, String SUP_CUR_JOB, String IMPROVE_DESC, String OP_SUP_REMARK, String RC_VICE_SUP_REMARK, String LAST_VISIT_DATE, String CON_DEGREE, BigDecimal FRQ_DAY, String DEDUCTION_INITIAL, String RC_SUP_REMARK, String HEADMGR_REMARK, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.CASE_NO = CASE_NO;
        this.END_DATE = END_DATE;
        this.BRH_DESC = BRH_DESC;
        this.WAITING_TIME = WAITING_TIME;
        this.WORKING_TIME = WORKING_TIME;
        this.EMP_NAME = EMP_NAME;
        this.EMP_ID = EMP_ID;
        this.CUR_JOB = CUR_JOB;
        this.CUR_JOB_Y = CUR_JOB_Y;
        this.CUR_JOB_M = CUR_JOB_M;
        this.AO_CODE = AO_CODE;
        this.SUP_EMP_NAME = SUP_EMP_NAME;
        this.SUP_EMP_ID = SUP_EMP_ID;
        this.SUP_CUR_JOB = SUP_CUR_JOB;
        this.IMPROVE_DESC = IMPROVE_DESC;
        this.OP_SUP_REMARK = OP_SUP_REMARK;
        this.RC_VICE_SUP_REMARK = RC_VICE_SUP_REMARK;
        this.LAST_VISIT_DATE = LAST_VISIT_DATE;
        this.CON_DEGREE = CON_DEGREE;
        this.FRQ_DAY = FRQ_DAY;
        this.DEDUCTION_INITIAL = DEDUCTION_INITIAL;
        this.RC_SUP_REMARK = RC_SUP_REMARK;
        this.HEADMGR_REMARK = HEADMGR_REMARK;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSQM_CSM_IMPROVE_DTLVO() {
    }

    /** minimal constructor */
    public TBSQM_CSM_IMPROVE_DTLVO(String CASE_NO) {
        this.CASE_NO = CASE_NO;
    }

    public String getCASE_NO() {
        return this.CASE_NO;
    }

    public void setCASE_NO(String CASE_NO) {
        this.CASE_NO = CASE_NO;
    }

    public String getEND_DATE() {
        return this.END_DATE;
    }

    public void setEND_DATE(String END_DATE) {
        this.END_DATE = END_DATE;
    }

    public String getBRH_DESC() {
        return this.BRH_DESC;
    }

    public void setBRH_DESC(String BRH_DESC) {
        this.BRH_DESC = BRH_DESC;
    }

    public BigDecimal getWAITING_TIME() {
        return this.WAITING_TIME;
    }

    public void setWAITING_TIME(BigDecimal WAITING_TIME) {
        this.WAITING_TIME = WAITING_TIME;
    }

    public BigDecimal getWORKING_TIME() {
        return this.WORKING_TIME;
    }

    public void setWORKING_TIME(BigDecimal WORKING_TIME) {
        this.WORKING_TIME = WORKING_TIME;
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

    public String getCUR_JOB() {
        return this.CUR_JOB;
    }

    public void setCUR_JOB(String CUR_JOB) {
        this.CUR_JOB = CUR_JOB;
    }

    public BigDecimal getCUR_JOB_Y() {
        return this.CUR_JOB_Y;
    }

    public void setCUR_JOB_Y(BigDecimal CUR_JOB_Y) {
        this.CUR_JOB_Y = CUR_JOB_Y;
    }

    public BigDecimal getCUR_JOB_M() {
        return this.CUR_JOB_M;
    }

    public void setCUR_JOB_M(BigDecimal CUR_JOB_M) {
        this.CUR_JOB_M = CUR_JOB_M;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getSUP_EMP_NAME() {
        return this.SUP_EMP_NAME;
    }

    public void setSUP_EMP_NAME(String SUP_EMP_NAME) {
        this.SUP_EMP_NAME = SUP_EMP_NAME;
    }

    public String getSUP_EMP_ID() {
        return this.SUP_EMP_ID;
    }

    public void setSUP_EMP_ID(String SUP_EMP_ID) {
        this.SUP_EMP_ID = SUP_EMP_ID;
    }

    public String getSUP_CUR_JOB() {
        return this.SUP_CUR_JOB;
    }

    public void setSUP_CUR_JOB(String SUP_CUR_JOB) {
        this.SUP_CUR_JOB = SUP_CUR_JOB;
    }

    public String getIMPROVE_DESC() {
        return this.IMPROVE_DESC;
    }

    public void setIMPROVE_DESC(String IMPROVE_DESC) {
        this.IMPROVE_DESC = IMPROVE_DESC;
    }

    public String getOP_SUP_REMARK() {
        return this.OP_SUP_REMARK;
    }

    public void setOP_SUP_REMARK(String OP_SUP_REMARK) {
        this.OP_SUP_REMARK = OP_SUP_REMARK;
    }

    public String getRC_VICE_SUP_REMARK() {
        return this.RC_VICE_SUP_REMARK;
    }

    public void setRC_VICE_SUP_REMARK(String RC_VICE_SUP_REMARK) {
        this.RC_VICE_SUP_REMARK = RC_VICE_SUP_REMARK;
    }

    public String getLAST_VISIT_DATE() {
        return this.LAST_VISIT_DATE;
    }

    public void setLAST_VISIT_DATE(String LAST_VISIT_DATE) {
        this.LAST_VISIT_DATE = LAST_VISIT_DATE;
    }

    public String getCON_DEGREE() {
        return this.CON_DEGREE;
    }

    public void setCON_DEGREE(String CON_DEGREE) {
        this.CON_DEGREE = CON_DEGREE;
    }

    public BigDecimal getFRQ_DAY() {
        return this.FRQ_DAY;
    }

    public void setFRQ_DAY(BigDecimal FRQ_DAY) {
        this.FRQ_DAY = FRQ_DAY;
    }

    public String getDEDUCTION_INITIAL() {
        return this.DEDUCTION_INITIAL;
    }

    public void setDEDUCTION_INITIAL(String DEDUCTION_INITIAL) {
        this.DEDUCTION_INITIAL = DEDUCTION_INITIAL;
    }

    public String getRC_SUP_REMARK() {
        return this.RC_SUP_REMARK;
    }

    public void setRC_SUP_REMARK(String RC_SUP_REMARK) {
        this.RC_SUP_REMARK = RC_SUP_REMARK;
    }

    public String getHEADMGR_REMARK() {
        return this.HEADMGR_REMARK;
    }

    public void setHEADMGR_REMARK(String HEADMGR_REMARK) {
        this.HEADMGR_REMARK = HEADMGR_REMARK;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CASE_NO", getCASE_NO())
            .toString();
    }

}
