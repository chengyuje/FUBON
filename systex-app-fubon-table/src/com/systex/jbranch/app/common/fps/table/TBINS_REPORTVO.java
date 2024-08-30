package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_REPORTVO extends VOBase {

    /** identifier field */
    private BigDecimal KEYNO;

    /** nullable persistent field */
    private String PLAN_ID;

    /** nullable persistent field */
    private String FILE_NAME;

    /** nullable persistent field */
    private Blob REPORT_FILE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_REPORT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_REPORTVO(BigDecimal KEYNO, String PLAN_ID, String FILE_NAME, Blob REPORT_FILE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.KEYNO = KEYNO;
        this.PLAN_ID = PLAN_ID;
        this.FILE_NAME = FILE_NAME;
        this.REPORT_FILE = REPORT_FILE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_REPORTVO() {
    }

    /** minimal constructor */
    public TBINS_REPORTVO(BigDecimal KEYNO) {
        this.KEYNO = KEYNO;
    }

    public BigDecimal getKEYNO() {
        return this.KEYNO;
    }

    public void setKEYNO(BigDecimal KEYNO) {
        this.KEYNO = KEYNO;
    }

    public String getPLAN_ID() {
        return this.PLAN_ID;
    }

    public void setPLAN_ID(String PLAN_ID) {
        this.PLAN_ID = PLAN_ID;
    }

    public String getFILE_NAME() {
        return this.FILE_NAME;
    }

    public void setFILE_NAME(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
    }

    public Blob getREPORT_FILE() {
        return this.REPORT_FILE;
    }

    public void setREPORT_FILE(Blob REPORT_FILE) {
        this.REPORT_FILE = REPORT_FILE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEYNO", getKEYNO())
            .toString();
    }

}
