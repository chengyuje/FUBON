package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_DEFNVO extends VOBase {

    /** identifier field */
    private String DEPT_ID;

    /** persistent field */
    private String DEPT_NAME;

    /** persistent field */
    private String ORG_TYPE;

    /** nullable persistent field */
    private String PARENT_DEPT_ID;

    /** nullable persistent field */
    private String DEPT_DEGREE;

    /** nullable persistent field */
    private String DEPT_GROUP;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_DEFN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_DEFNVO(String DEPT_ID, String DEPT_NAME, String ORG_TYPE, String PARENT_DEPT_ID, String DEPT_DEGREE, String DEPT_GROUP, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.DEPT_ID = DEPT_ID;
        this.DEPT_NAME = DEPT_NAME;
        this.ORG_TYPE = ORG_TYPE;
        this.PARENT_DEPT_ID = PARENT_DEPT_ID;
        this.DEPT_DEGREE = DEPT_DEGREE;
        this.DEPT_GROUP = DEPT_GROUP;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBORG_DEFNVO() {
    }

    /** minimal constructor */
    public TBORG_DEFNVO(String DEPT_ID, String DEPT_NAME, String ORG_TYPE) {
        this.DEPT_ID = DEPT_ID;
        this.DEPT_NAME = DEPT_NAME;
        this.ORG_TYPE = ORG_TYPE;
    }

    public String getDEPT_ID() {
        return this.DEPT_ID;
    }

    public void setDEPT_ID(String DEPT_ID) {
        this.DEPT_ID = DEPT_ID;
    }

    public String getDEPT_NAME() {
        return this.DEPT_NAME;
    }

    public void setDEPT_NAME(String DEPT_NAME) {
        this.DEPT_NAME = DEPT_NAME;
    }

    public String getORG_TYPE() {
        return this.ORG_TYPE;
    }

    public void setORG_TYPE(String ORG_TYPE) {
        this.ORG_TYPE = ORG_TYPE;
    }

    public String getPARENT_DEPT_ID() {
        return this.PARENT_DEPT_ID;
    }

    public void setPARENT_DEPT_ID(String PARENT_DEPT_ID) {
        this.PARENT_DEPT_ID = PARENT_DEPT_ID;
    }

    public String getDEPT_DEGREE() {
        return this.DEPT_DEGREE;
    }

    public void setDEPT_DEGREE(String DEPT_DEGREE) {
        this.DEPT_DEGREE = DEPT_DEGREE;
    }

    public String getDEPT_GROUP() {
        return this.DEPT_GROUP;
    }

    public void setDEPT_GROUP(String DEPT_GROUP) {
        this.DEPT_GROUP = DEPT_GROUP;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DEPT_ID", getDEPT_ID())
            .toString();
    }

}
