package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_SIGN_FORMVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private String MGM_SIGN_FORM_NAME;

    /** nullable persistent field */
    private Blob MGM_SIGN_FORM;

    /** nullable persistent field */
    private String BE_MGM_SIGN_FORM_NAME;

    /** nullable persistent field */
    private Blob BE_MGM_SIGN_FORM;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_SIGN_FORM";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_SIGN_FORMVO(String SEQ, String MGM_SIGN_FORM_NAME, Blob MGM_SIGN_FORM, String BE_MGM_SIGN_FORM_NAME, Blob BE_MGM_SIGN_FORM, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.MGM_SIGN_FORM_NAME = MGM_SIGN_FORM_NAME;
        this.MGM_SIGN_FORM = MGM_SIGN_FORM;
        this.BE_MGM_SIGN_FORM_NAME = BE_MGM_SIGN_FORM_NAME;
        this.BE_MGM_SIGN_FORM = BE_MGM_SIGN_FORM;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_SIGN_FORMVO() {
    }

    /** minimal constructor */
    public TBMGM_SIGN_FORMVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getMGM_SIGN_FORM_NAME() {
        return this.MGM_SIGN_FORM_NAME;
    }

    public void setMGM_SIGN_FORM_NAME(String MGM_SIGN_FORM_NAME) {
        this.MGM_SIGN_FORM_NAME = MGM_SIGN_FORM_NAME;
    }

    public Blob getMGM_SIGN_FORM() {
        return this.MGM_SIGN_FORM;
    }

    public void setMGM_SIGN_FORM(Blob MGM_SIGN_FORM) {
        this.MGM_SIGN_FORM = MGM_SIGN_FORM;
    }

    public String getBE_MGM_SIGN_FORM_NAME() {
        return this.BE_MGM_SIGN_FORM_NAME;
    }

    public void setBE_MGM_SIGN_FORM_NAME(String BE_MGM_SIGN_FORM_NAME) {
        this.BE_MGM_SIGN_FORM_NAME = BE_MGM_SIGN_FORM_NAME;
    }

    public Blob getBE_MGM_SIGN_FORM() {
        return this.BE_MGM_SIGN_FORM;
    }

    public void setBE_MGM_SIGN_FORM(Blob BE_MGM_SIGN_FORM) {
        this.BE_MGM_SIGN_FORM = BE_MGM_SIGN_FORM;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
