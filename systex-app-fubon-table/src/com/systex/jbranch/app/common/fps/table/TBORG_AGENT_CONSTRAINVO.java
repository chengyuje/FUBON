package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_AGENT_CONSTRAINVO extends VOBase {

    /** identifier field */
    private String EMP_ID;

    /** nullable persistent field */
    private String AGENT_ID_1;

    /** nullable persistent field */
    private String AGENT_ID_2;

    /** nullable persistent field */
    private String AGENT_ID_3;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_AGENT_CONSTRAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_AGENT_CONSTRAINVO(String EMP_ID, String AGENT_ID_1, String AGENT_ID_2, String AGENT_ID_3, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.EMP_ID = EMP_ID;
        this.AGENT_ID_1 = AGENT_ID_1;
        this.AGENT_ID_2 = AGENT_ID_2;
        this.AGENT_ID_3 = AGENT_ID_3;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBORG_AGENT_CONSTRAINVO() {
    }

    /** minimal constructor */
    public TBORG_AGENT_CONSTRAINVO(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getAGENT_ID_1() {
        return this.AGENT_ID_1;
    }

    public void setAGENT_ID_1(String AGENT_ID_1) {
        this.AGENT_ID_1 = AGENT_ID_1;
    }

    public String getAGENT_ID_2() {
        return this.AGENT_ID_2;
    }

    public void setAGENT_ID_2(String AGENT_ID_2) {
        this.AGENT_ID_2 = AGENT_ID_2;
    }

    public String getAGENT_ID_3() {
        return this.AGENT_ID_3;
    }

    public void setAGENT_ID_3(String AGENT_ID_3) {
        this.AGENT_ID_3 = AGENT_ID_3;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getREVIEW_STATUS() {
        return this.REVIEW_STATUS;
    }

    public void setREVIEW_STATUS(String REVIEW_STATUS) {
        this.REVIEW_STATUS = REVIEW_STATUS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

}
