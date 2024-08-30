package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_10CMDT_PLAN_MAINVO extends VOBase {

    /** identifier field */
    private String PRJ_CODE;

    /** nullable persistent field */
    private String PRJ_NAME;

    /** nullable persistent field */
    private Timestamp START_DATE;

    /** nullable persistent field */
    private Timestamp END_DATE;

    /** nullable persistent field */
    private String PRJ_STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_10CMDT_PLAN_MAINVO(String PRJ_CODE, String PRJ_NAME, Timestamp START_DATE, Timestamp END_DATE, String PRJ_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.PRJ_CODE = PRJ_CODE;
        this.PRJ_NAME = PRJ_NAME;
        this.START_DATE = START_DATE;
        this.END_DATE = END_DATE;
        this.PRJ_STATUS = PRJ_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_10CMDT_PLAN_MAINVO() {
    }

    /** minimal constructor */
    public TBCRM_10CMDT_PLAN_MAINVO(String PRJ_CODE) {
        this.PRJ_CODE = PRJ_CODE;
    }

    public String getPRJ_CODE() {
        return this.PRJ_CODE;
    }

    public void setPRJ_CODE(String PRJ_CODE) {
        this.PRJ_CODE = PRJ_CODE;
    }

    public String getPRJ_NAME() {
        return this.PRJ_NAME;
    }

    public void setPRJ_NAME(String PRJ_NAME) {
        this.PRJ_NAME = PRJ_NAME;
    }

    public Timestamp getSTART_DATE() {
        return this.START_DATE;
    }

    public void setSTART_DATE(Timestamp START_DATE) {
        this.START_DATE = START_DATE;
    }

    public Timestamp getEND_DATE() {
        return this.END_DATE;
    }

    public void setEND_DATE(Timestamp END_DATE) {
        this.END_DATE = END_DATE;
    }

    public String getPRJ_STATUS() {
        return this.PRJ_STATUS;
    }

    public void setPRJ_STATUS(String PRJ_STATUS) {
        this.PRJ_STATUS = PRJ_STATUS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRJ_CODE", getPRJ_CODE())
            .toString();
    }

}
