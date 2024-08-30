package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_EMP_HIRE_FILEVO extends VOBase {

    /** identifier field */
    private String HIRE_FILE_NAME;

    /** nullable persistent field */
    private Blob HIRE_FILE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_EMP_HIRE_FILE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_EMP_HIRE_FILEVO(String HIRE_FILE_NAME, Blob HIRE_FILE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.HIRE_FILE_NAME = HIRE_FILE_NAME;
        this.HIRE_FILE = HIRE_FILE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBORG_EMP_HIRE_FILEVO() {
    }

    /** minimal constructor */
    public TBORG_EMP_HIRE_FILEVO(String HIRE_FILE_NAME) {
        this.HIRE_FILE_NAME = HIRE_FILE_NAME;
    }

    public String getHIRE_FILE_NAME() {
        return this.HIRE_FILE_NAME;
    }

    public void setHIRE_FILE_NAME(String HIRE_FILE_NAME) {
        this.HIRE_FILE_NAME = HIRE_FILE_NAME;
    }

    public Blob getHIRE_FILE() {
        return this.HIRE_FILE;
    }

    public void setHIRE_FILE(Blob HIRE_FILE) {
        this.HIRE_FILE = HIRE_FILE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("HIRE_FILE_NAME", getHIRE_FILE_NAME())
            .toString();
    }

}
