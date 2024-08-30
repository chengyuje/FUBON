package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_REPORTVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private Blob REPORT_FILE;
    
    /** nullable persistent field */
    private Blob REPORT_FILE_ENG;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_REPORT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBKYC_REPORTVO(String SEQ, Blob REPORT_FILE, Blob REPORT_FILE_ENG, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.REPORT_FILE = REPORT_FILE;
        this.REPORT_FILE_ENG = REPORT_FILE_ENG;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBKYC_REPORTVO() {
    }

    /** minimal constructor */
    public TBKYC_REPORTVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public Blob getREPORT_FILE() {
        return this.REPORT_FILE;
    }

    public void setREPORT_FILE(Blob REPORT_FILE) {
        this.REPORT_FILE = REPORT_FILE;
    }

    public Blob getREPORT_FILE_ENG() {
		return REPORT_FILE_ENG;
	}

	public void setREPORT_FILE_ENG(Blob rEPORT_FILE_ENG) {
		REPORT_FILE_ENG = rEPORT_FILE_ENG;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
