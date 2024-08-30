package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_EMAIL_ATTACHMENTVO extends VOBase {

    /** identifier field */
    private String DOC_ID;

    /** nullable persistent field */
    private String DOCNAME;

    /** nullable persistent field */
    private String FILENAME;

    /** nullable persistent field */
    private String CATEGORY_BY_CONTENT;

    /** nullable persistent field */
    private String CATEGORY_BY_PRODUCT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_EMAIL_ATTACHMENT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_EMAIL_ATTACHMENTVO(String DOC_ID, String DOCNAME, String FILENAME, String CATEGORY_BY_CONTENT, String CATEGORY_BY_PRODUCT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.DOC_ID = DOC_ID;
        this.DOCNAME = DOCNAME;
        this.FILENAME = FILENAME;
        this.CATEGORY_BY_CONTENT = CATEGORY_BY_CONTENT;
        this.CATEGORY_BY_PRODUCT = CATEGORY_BY_PRODUCT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_EMAIL_ATTACHMENTVO() {
    }

    /** minimal constructor */
    public TBSYS_EMAIL_ATTACHMENTVO(String DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public String getDOC_ID() {
        return this.DOC_ID;
    }

    public void setDOC_ID(String DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public String getDOCNAME() {
        return this.DOCNAME;
    }

    public void setDOCNAME(String DOCNAME) {
        this.DOCNAME = DOCNAME;
    }

    public String getFILENAME() {
        return this.FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    public String getCATEGORY_BY_CONTENT() {
        return this.CATEGORY_BY_CONTENT;
    }

    public void setCATEGORY_BY_CONTENT(String CATEGORY_BY_CONTENT) {
        this.CATEGORY_BY_CONTENT = CATEGORY_BY_CONTENT;
    }

    public String getCATEGORY_BY_PRODUCT() {
        return this.CATEGORY_BY_PRODUCT;
    }

    public void setCATEGORY_BY_PRODUCT(String CATEGORY_BY_PRODUCT) {
        this.CATEGORY_BY_PRODUCT = CATEGORY_BY_PRODUCT;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DOC_ID", getDOC_ID())
            .toString();
    }

}
