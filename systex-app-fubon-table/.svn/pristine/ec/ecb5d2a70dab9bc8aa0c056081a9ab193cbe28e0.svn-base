package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_MEMBER_PHOTOVO extends VOBase {

    /** identifier field */
    private String EMP_ID;

    /** nullable persistent field */
    private Blob EMP_PHOTO;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_MEMBER_PHOTO";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_MEMBER_PHOTOVO(String EMP_ID, Blob EMP_PHOTO, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.EMP_ID = EMP_ID;
        this.EMP_PHOTO = EMP_PHOTO;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBORG_MEMBER_PHOTOVO() {
    }

    /** minimal constructor */
    public TBORG_MEMBER_PHOTOVO(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public Blob getEMP_PHOTO() {
        return this.EMP_PHOTO;
    }

    public void setEMP_PHOTO(Blob EMP_PHOTO) {
        this.EMP_PHOTO = EMP_PHOTO;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

}
