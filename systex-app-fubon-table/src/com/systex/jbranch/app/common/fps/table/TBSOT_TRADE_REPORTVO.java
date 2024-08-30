package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSOT_TRADE_REPORTVO extends VOBase {

    /** identifier field */
    private String TRADE_SEQ;

    /** nullable persistent field */
    private Blob REPORT_FILE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_REPORT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSOT_TRADE_REPORTVO(String TRADE_SEQ, Blob REPORT_FILE) {
        this.TRADE_SEQ = TRADE_SEQ;
        this.REPORT_FILE = REPORT_FILE;
    }

    /** default constructor */
    public TBSOT_TRADE_REPORTVO() {
    }

    /** minimal constructor */
    public TBSOT_TRADE_REPORTVO(String TRADE_SEQ) {
        this.TRADE_SEQ = TRADE_SEQ;
    }

    public String getTRADE_SEQ() {
        return this.TRADE_SEQ;
    }

    public void setTRADE_SEQ(String TRADE_SEQ) {
        this.TRADE_SEQ = TRADE_SEQ;
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
            .append("TRADE_SEQ", getTRADE_SEQ())
            .toString();
    }

}
