package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_EXAM_MAINVO extends VOBase {

    /** identifier field */
    private String EXAM_KEYNO;

    /** persistent field */
    private String CUST_ID;

    /** persistent field */
    private Timestamp EXAM_DATE;

    /** nullable persistent field */
    private String PRINT_YN;

    /** nullable persistent field */
    private Timestamp PRINT_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_EXAM_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_EXAM_MAINVO(String EXAM_KEYNO, String CUST_ID, Timestamp EXAM_DATE, String PRINT_YN, Timestamp PRINT_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.EXAM_KEYNO = EXAM_KEYNO;
        this.CUST_ID = CUST_ID;
        this.EXAM_DATE = EXAM_DATE;
        this.PRINT_YN = PRINT_YN;
        this.PRINT_DATE = PRINT_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_EXAM_MAINVO() {
    }

    /** minimal constructor */
    public TBINS_EXAM_MAINVO(String EXAM_KEYNO, String CUST_ID, Timestamp EXAM_DATE) {
        this.EXAM_KEYNO = EXAM_KEYNO;
        this.CUST_ID = CUST_ID;
        this.EXAM_DATE = EXAM_DATE;
    }

    public String getEXAM_KEYNO() {
        return this.EXAM_KEYNO;
    }

    public void setEXAM_KEYNO(String EXAM_KEYNO) {
        this.EXAM_KEYNO = EXAM_KEYNO;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public Timestamp getEXAM_DATE() {
        return this.EXAM_DATE;
    }

    public void setEXAM_DATE(Timestamp EXAM_DATE) {
        this.EXAM_DATE = EXAM_DATE;
    }

    public String getPRINT_YN() {
        return this.PRINT_YN;
    }

    public void setPRINT_YN(String PRINT_YN) {
        this.PRINT_YN = PRINT_YN;
    }

    public Timestamp getPRINT_DATE() {
        return this.PRINT_DATE;
    }

    public void setPRINT_DATE(Timestamp PRINT_DATE) {
        this.PRINT_DATE = PRINT_DATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EXAM_KEYNO", getEXAM_KEYNO())
            .toString();
    }

}
