package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INSDATA_COVER_MAINVO extends VOBase {

    /** identifier field */
    private String COVER_NO1;

    /** nullable persistent field */
    private String COVER_DESC;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INSDATA_COVER_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INSDATA_COVER_MAINVO(String COVER_NO1, String COVER_DESC, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.COVER_NO1 = COVER_NO1;
        this.COVER_DESC = COVER_DESC;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INSDATA_COVER_MAINVO() {
    }

    /** minimal constructor */
    public TBPRD_INSDATA_COVER_MAINVO(String COVER_NO1) {
        this.COVER_NO1 = COVER_NO1;
    }

    public String getCOVER_NO1() {
        return this.COVER_NO1;
    }

    public void setCOVER_NO1(String COVER_NO1) {
        this.COVER_NO1 = COVER_NO1;
    }

    public String getCOVER_DESC() {
        return this.COVER_DESC;
    }

    public void setCOVER_DESC(String COVER_DESC) {
        this.COVER_DESC = COVER_DESC;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("COVER_NO1", getCOVER_NO1())
            .toString();
    }

}
