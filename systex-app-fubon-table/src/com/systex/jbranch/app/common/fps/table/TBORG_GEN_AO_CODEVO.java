package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_GEN_AO_CODEVO extends VOBase {

    /** identifier field */
    private String AO_CODE;

    /** nullable persistent field */
    private String USE_FLAG;

    /** nullable persistent field */
    private String IS_BLACK_LIST;

    /** nullable persistent field */
    private String USE_EMP_ID;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_GEN_AO_CODE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_GEN_AO_CODEVO(String AO_CODE, String USE_FLAG, String IS_BLACK_LIST, String USE_EMP_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.AO_CODE = AO_CODE;
        this.USE_FLAG = USE_FLAG;
        this.IS_BLACK_LIST = IS_BLACK_LIST;
        this.USE_EMP_ID = USE_EMP_ID;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBORG_GEN_AO_CODEVO() {
    }

    /** minimal constructor */
    public TBORG_GEN_AO_CODEVO(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getUSE_FLAG() {
        return this.USE_FLAG;
    }

    public void setUSE_FLAG(String USE_FLAG) {
        this.USE_FLAG = USE_FLAG;
    }

    public String getIS_BLACK_LIST() {
        return this.IS_BLACK_LIST;
    }

    public void setIS_BLACK_LIST(String IS_BLACK_LIST) {
        this.IS_BLACK_LIST = IS_BLACK_LIST;
    }

    public String getUSE_EMP_ID() {
        return this.USE_EMP_ID;
    }

    public void setUSE_EMP_ID(String USE_EMP_ID) {
        this.USE_EMP_ID = USE_EMP_ID;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AO_CODE", getAO_CODE())
            .toString();
    }

}
