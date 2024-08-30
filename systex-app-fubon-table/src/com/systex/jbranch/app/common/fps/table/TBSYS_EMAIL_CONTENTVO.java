package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_EMAIL_CONTENTVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String CONTENT;

    /** nullable persistent field */
    private String CATEGORY_BY_CONTENT;

    /** nullable persistent field */
    private String CATEGORY_BY_PRODUCT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_EMAIL_CONTENT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_EMAIL_CONTENTVO(BigDecimal SEQ, String CONTENT, String CATEGORY_BY_CONTENT, String CATEGORY_BY_PRODUCT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.CONTENT = CONTENT;
        this.CATEGORY_BY_CONTENT = CATEGORY_BY_CONTENT;
        this.CATEGORY_BY_PRODUCT = CATEGORY_BY_PRODUCT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_EMAIL_CONTENTVO() {
    }

    /** minimal constructor */
    public TBSYS_EMAIL_CONTENTVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getCONTENT() {
        return this.CONTENT;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
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
            .append("SEQ", getSEQ())
            .toString();
    }

}
