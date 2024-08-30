package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_EMAIL_ATTACHMENT_REVIEWVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String DOC_ID;

    /** nullable persistent field */
    private String DOCNAME;

    /** nullable persistent field */
    private String FILENAME;

    /** nullable persistent field */
    private String CATEGORY_BY_CONTENT;

    /** nullable persistent field */
    private String CATEGORY_BY_PRODUCT;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_EMAIL_ATTACHMENT_REVIEW";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_EMAIL_ATTACHMENT_REVIEWVO(BigDecimal SEQ, String DOC_ID, String DOCNAME, String FILENAME, String CATEGORY_BY_CONTENT, String CATEGORY_BY_PRODUCT, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.DOC_ID = DOC_ID;
        this.DOCNAME = DOCNAME;
        this.FILENAME = FILENAME;
        this.CATEGORY_BY_CONTENT = CATEGORY_BY_CONTENT;
        this.CATEGORY_BY_PRODUCT = CATEGORY_BY_PRODUCT;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_EMAIL_ATTACHMENT_REVIEWVO() {
    }

    /** minimal constructor */
    public TBSYS_EMAIL_ATTACHMENT_REVIEWVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
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
            .append("SEQ", getSEQ())
            .toString();
    }

}
