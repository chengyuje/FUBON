package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DAY_TERM_INT_CDVO extends VOBase {

    /** identifier field */
    private String DEP_NO;

    /** nullable persistent field */
    private Timestamp TERM_DATE;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String OP_AREA_ID;

    /** nullable persistent field */
    private String OP_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String ACCOUNT;

    /** nullable persistent field */
    private String CRCY_TYPE;

    /** nullable persistent field */
    private String AMT;

    /** nullable persistent field */
    private String REASON;

    /** nullable persistent field */
    private String NOTE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_DAY_TERM_INT_CD";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_DAY_TERM_INT_CDVO(String DEP_NO, Timestamp TERM_DATE, String REGION_CENTER_ID, String REGION_CENTER_NAME, String OP_AREA_ID, String OP_AREA_NAME, String BRANCH_NBR, String BRANCH_NAME, String AO_CODE, String CUST_ID, String CUST_NAME, String EMP_ID, String EMP_NAME, String ACCOUNT, String CRCY_TYPE, String AMT, String REASON, String NOTE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.DEP_NO = DEP_NO;
        this.TERM_DATE = TERM_DATE;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.OP_AREA_ID = OP_AREA_ID;
        this.OP_AREA_NAME = OP_AREA_NAME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.AO_CODE = AO_CODE;
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.EMP_ID = EMP_ID;
        this.EMP_NAME = EMP_NAME;
        this.ACCOUNT = ACCOUNT;
        this.CRCY_TYPE = CRCY_TYPE;
        this.AMT = AMT;
        this.REASON = REASON;
        this.NOTE = NOTE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_DAY_TERM_INT_CDVO() {
    }

    /** minimal constructor */
    public TBPMS_DAY_TERM_INT_CDVO(String DEP_NO) {
        this.DEP_NO = DEP_NO;
    }

    public String getDEP_NO() {
        return this.DEP_NO;
    }

    public void setDEP_NO(String DEP_NO) {
        this.DEP_NO = DEP_NO;
    }

    public Timestamp getTERM_DATE() {
        return this.TERM_DATE;
    }

    public void setTERM_DATE(Timestamp TERM_DATE) {
        this.TERM_DATE = TERM_DATE;
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

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
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

    public String getACCOUNT() {
        return this.ACCOUNT;
    }

    public void setACCOUNT(String ACCOUNT) {
        this.ACCOUNT = ACCOUNT;
    }

    public String getCRCY_TYPE() {
        return this.CRCY_TYPE;
    }

    public void setCRCY_TYPE(String CRCY_TYPE) {
        this.CRCY_TYPE = CRCY_TYPE;
    }

    public String getAMT() {
        return this.AMT;
    }

    public void setAMT(String AMT) {
        this.AMT = AMT;
    }

    public String getREASON() {
        return this.REASON;
    }

    public void setREASON(String REASON) {
        this.REASON = REASON;
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
            .append("DEP_NO", getDEP_NO())
            .toString();
    }

}
