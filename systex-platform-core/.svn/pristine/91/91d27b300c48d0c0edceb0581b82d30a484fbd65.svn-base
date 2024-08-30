package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSOVERPRINTDOCUMENTVO extends VOBase {

    /** identifier field */
    private String DID;

    /** persistent field */
    private Blob NAME;

    /** nullable persistent field */
    private Blob DATA;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTDOCUMENT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSOVERPRINTDOCUMENTVO(String DID, Blob NAME, Blob DATA, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.DID = DID;
        this.NAME = NAME;
        this.DATA = DATA;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYSOVERPRINTDOCUMENTVO() {
    }

    /** minimal constructor */
    public TBSYSOVERPRINTDOCUMENTVO(String DID, Blob NAME) {
        this.DID = DID;
        this.NAME = NAME;
    }

    public String getDID() {
        return this.DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }

    public Blob getNAME() {
        return this.NAME;
    }

    public void setNAME(Blob NAME) {
        this.NAME = NAME;
    }

    public Blob getDATA() {
        return this.DATA;
    }

    public void setDATA(Blob DATA) {
        this.DATA = DATA;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DID", getDID())
            .toString();
    }

}
