package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_FILE_MAINVO extends VOBase {

    /** identifier field */
    private String DOC_ID;

    /** nullable persistent field */
    private String DOC_NAME;

    /** nullable persistent field */
    private String SUBSYSTEM_TYPE;

    /** nullable persistent field */
    private String DOC_TYPE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_FILE_MAINVO(String DOC_ID, String DOC_NAME, String SUBSYSTEM_TYPE, String DOC_TYPE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.DOC_ID = DOC_ID;
        this.DOC_NAME = DOC_NAME;
        this.SUBSYSTEM_TYPE = SUBSYSTEM_TYPE;
        this.DOC_TYPE = DOC_TYPE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_FILE_MAINVO() {
    }

    /** minimal constructor */
    public TBSYS_FILE_MAINVO(String DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public String getDOC_ID() {
        return this.DOC_ID;
    }

    public void setDOC_ID(String DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public String getDOC_NAME() {
        return this.DOC_NAME;
    }

    public void setDOC_NAME(String DOC_NAME) {
        this.DOC_NAME = DOC_NAME;
    }

    public String getSUBSYSTEM_TYPE() {
        return this.SUBSYSTEM_TYPE;
    }

    public void setSUBSYSTEM_TYPE(String SUBSYSTEM_TYPE) {
        this.SUBSYSTEM_TYPE = SUBSYSTEM_TYPE;
    }

    public String getDOC_TYPE() {
        return this.DOC_TYPE;
    }

    public void setDOC_TYPE(String DOC_TYPE) {
        this.DOC_TYPE = DOC_TYPE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DOC_ID", getDOC_ID())
            .toString();
    }

}
