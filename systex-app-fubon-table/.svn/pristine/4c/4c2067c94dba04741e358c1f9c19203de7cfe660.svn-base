package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_PREMATCH_PDFVO extends VOBase {

    /** identifier field */
    private String PREMATCH_SEQ;

    /** nullable persistent field */
    private Blob PDF_FILE;



public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_PREMATCH_PDF";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_PREMATCH_PDFVO(String PREMATCH_SEQ, Blob PDF_FILE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.PREMATCH_SEQ = PREMATCH_SEQ;
        this.PDF_FILE = PDF_FILE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_PREMATCH_PDFVO() {
    }

    /** minimal constructor */
    public TBIOT_PREMATCH_PDFVO(String PREMATCH_SEQ) {
        this.PREMATCH_SEQ = PREMATCH_SEQ;
    }

    public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}

	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
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
            .append("PREMATCH_SEQ", getPREMATCH_SEQ())
            .toString();
    }

}
