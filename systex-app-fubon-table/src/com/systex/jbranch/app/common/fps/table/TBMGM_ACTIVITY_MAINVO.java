package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_ACTIVITY_MAINVO extends VOBase {

    /** identifier field */
    private String ACT_SEQ;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String ACT_NAME;

    /** nullable persistent field */
    private Timestamp EFF_DATE;

    /** nullable persistent field */
    private Timestamp DEADLINE;

    /** nullable persistent field */
    private Timestamp EXC_DEADLINE;

    /** nullable persistent field */
    private String ACT_CONTENT;

    /** nullable persistent field */
    private String ACT_APPROACH;

    /** nullable persistent field */
    private String PRECAUTIONS;

    /** nullable persistent field */
    private String TEMP_YN;

    /** nullable persistent field */
    private String DELETE_YN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_ACTIVITY_MAINVO(String ACT_SEQ, String ACT_TYPE, String ACT_NAME, Timestamp EFF_DATE, Timestamp DEADLINE, Timestamp EXC_DEADLINE, String ACT_CONTENT, String ACT_APPROACH, String PRECAUTIONS, String TEMP_YN, String DELETE_YN, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.ACT_SEQ = ACT_SEQ;
        this.ACT_TYPE = ACT_TYPE;
        this.ACT_NAME = ACT_NAME;
        this.EFF_DATE = EFF_DATE;
        this.DEADLINE = DEADLINE;
        this.EXC_DEADLINE = EXC_DEADLINE;
        this.ACT_CONTENT = ACT_CONTENT;
        this.ACT_APPROACH = ACT_APPROACH;
        this.PRECAUTIONS = PRECAUTIONS;
        this.TEMP_YN = TEMP_YN;
        this.DELETE_YN = DELETE_YN;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_ACTIVITY_MAINVO() {
    }

    /** minimal constructor */
    public TBMGM_ACTIVITY_MAINVO(String ACT_SEQ) {
        this.ACT_SEQ = ACT_SEQ;
    }

    public String getACT_SEQ() {
        return this.ACT_SEQ;
    }

    public void setACT_SEQ(String ACT_SEQ) {
        this.ACT_SEQ = ACT_SEQ;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getACT_NAME() {
        return this.ACT_NAME;
    }

    public void setACT_NAME(String ACT_NAME) {
        this.ACT_NAME = ACT_NAME;
    }

    public Timestamp getEFF_DATE() {
        return this.EFF_DATE;
    }

    public void setEFF_DATE(Timestamp EFF_DATE) {
        this.EFF_DATE = EFF_DATE;
    }

    public Timestamp getDEADLINE() {
        return this.DEADLINE;
    }

    public void setDEADLINE(Timestamp DEADLINE) {
        this.DEADLINE = DEADLINE;
    }

    public Timestamp getEXC_DEADLINE() {
        return this.EXC_DEADLINE;
    }

    public void setEXC_DEADLINE(Timestamp EXC_DEADLINE) {
        this.EXC_DEADLINE = EXC_DEADLINE;
    }

    public String getACT_CONTENT() {
        return this.ACT_CONTENT;
    }

    public void setACT_CONTENT(String ACT_CONTENT) {
        this.ACT_CONTENT = ACT_CONTENT;
    }

    public String getACT_APPROACH() {
        return this.ACT_APPROACH;
    }

    public void setACT_APPROACH(String ACT_APPROACH) {
        this.ACT_APPROACH = ACT_APPROACH;
    }

    public String getPRECAUTIONS() {
        return this.PRECAUTIONS;
    }

    public void setPRECAUTIONS(String PRECAUTIONS) {
        this.PRECAUTIONS = PRECAUTIONS;
    }

    public String getTEMP_YN() {
        return this.TEMP_YN;
    }

    public void setTEMP_YN(String TEMP_YN) {
        this.TEMP_YN = TEMP_YN;
    }

    public String getDELETE_YN() {
        return this.DELETE_YN;
    }

    public void setDELETE_YN(String DELETE_YN) {
        this.DELETE_YN = DELETE_YN;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ACT_SEQ", getACT_SEQ())
            .toString();
    }

}
