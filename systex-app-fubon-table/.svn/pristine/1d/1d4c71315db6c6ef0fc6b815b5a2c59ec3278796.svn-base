package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMTC_PDFVO extends VOBase {

    /** identifier field */
    private String CON_NO;

    /** persistent field */
    private Blob CON_PDF;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMTC_PDF";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMTC_PDFVO(String CON_NO, Blob CON_PDF, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.CON_NO = CON_NO;
        this.CON_PDF = CON_PDF;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMTC_PDFVO() {
    }

    /** minimal constructor */
    public TBMTC_PDFVO(String CON_NO, Blob CON_PDF) {
        this.CON_NO = CON_NO;
        this.CON_PDF = CON_PDF;
    }

    public String getCON_NO() {
        return this.CON_NO;
    }

    public void setCON_NO(String CON_NO) {
        this.CON_NO = CON_NO;
    }

    public Blob getCON_PDF() {
        return this.CON_PDF;
    }

    public void setCON_PDF(Blob CON_PDF) {
        this.CON_PDF = CON_PDF;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CON_NO", getCON_NO())
            .toString();
    }

}
