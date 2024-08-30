package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_EXAMRECORDVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private String EXAM_VERSION;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String RECORD_SEQ;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_EXAMRECORDVO(String SEQ, String EXAM_VERSION, String CUST_ID, String CUST_NAME, String RECORD_SEQ, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.EXAM_VERSION = EXAM_VERSION;
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.RECORD_SEQ = RECORD_SEQ;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_EXAMRECORDVO() {
    }

    /** minimal constructor */
    public TBCAM_EXAMRECORDVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getEXAM_VERSION() {
        return this.EXAM_VERSION;
    }

    public void setEXAM_VERSION(String EXAM_VERSION) {
        this.EXAM_VERSION = EXAM_VERSION;
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

    public String getRECORD_SEQ() {
        return this.RECORD_SEQ;
    }

    public void setRECORD_SEQ(String RECORD_SEQ) {
        this.RECORD_SEQ = RECORD_SEQ;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
