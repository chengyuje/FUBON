package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_MAPP_PDFVO extends VOBase {

    /** identifier field */
    private String CASE_ID;

    /** nullable persistent field */
    private Blob PDF_FILE;



public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_MAPP_PDF";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_MAPP_PDFVO(String CASE_ID, Blob PDF_FILE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.CASE_ID = CASE_ID;
        this.PDF_FILE = PDF_FILE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_MAPP_PDFVO() {
    }

    /** minimal constructor */
    public TBIOT_MAPP_PDFVO(String CASE_ID) {
        this.CASE_ID = CASE_ID;
    }

    public String getCASE_ID() {
        return this.CASE_ID;
    }

    public void setCASE_ID(String CASE_ID) {
        this.CASE_ID = CASE_ID;
    }

    public Blob getPDF_FILE() {
        return this.PDF_FILE;
    }

    public void setPDF_FILE(Blob PDF_FILE) {
        this.PDF_FILE = PDF_FILE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CASE_ID", getCASE_ID())
            .toString();
    }

}
