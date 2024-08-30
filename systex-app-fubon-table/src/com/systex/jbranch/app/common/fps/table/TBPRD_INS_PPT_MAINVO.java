package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_PPT_MAINVO extends VOBase {

    /** identifier field */
    private BigDecimal INSPRD_KEYNO;

    /** persistent field */
    private String INSPRD_ID;

    /** persistent field */
    private String INSPRD_NAME;

    /** nullable persistent field */
    private String CERT_03;

    /** nullable persistent field */
    private String CERT_05;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_PPT_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_PPT_MAINVO(BigDecimal INSPRD_KEYNO, String INSPRD_ID, String INSPRD_NAME, String CERT_03, String CERT_05, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.INSPRD_KEYNO = INSPRD_KEYNO;
        this.INSPRD_ID = INSPRD_ID;
        this.INSPRD_NAME = INSPRD_NAME;
        this.CERT_03 = CERT_03;
        this.CERT_05 = CERT_05;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_PPT_MAINVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_PPT_MAINVO(BigDecimal INSPRD_KEYNO, String INSPRD_ID, String INSPRD_NAME, Timestamp createtime, String creator) {
        this.INSPRD_KEYNO = INSPRD_KEYNO;
        this.INSPRD_ID = INSPRD_ID;
        this.INSPRD_NAME = INSPRD_NAME;
        this.createtime = createtime;
        this.creator = creator;
    }

    public BigDecimal getINSPRD_KEYNO() {
        return this.INSPRD_KEYNO;
    }

    public void setINSPRD_KEYNO(BigDecimal INSPRD_KEYNO) {
        this.INSPRD_KEYNO = INSPRD_KEYNO;
    }

    public String getINSPRD_ID() {
        return this.INSPRD_ID;
    }

    public void setINSPRD_ID(String INSPRD_ID) {
        this.INSPRD_ID = INSPRD_ID;
    }

    public String getINSPRD_NAME() {
        return this.INSPRD_NAME;
    }

    public void setINSPRD_NAME(String INSPRD_NAME) {
        this.INSPRD_NAME = INSPRD_NAME;
    }

    public String getCERT_03() {
        return this.CERT_03;
    }

    public void setCERT_03(String CERT_03) {
        this.CERT_03 = CERT_03;
    }

    public String getCERT_05() {
        return this.CERT_05;
    }

    public void setCERT_05(String CERT_05) {
        this.CERT_05 = CERT_05;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INSPRD_KEYNO", getINSPRD_KEYNO())
            .toString();
    }

}
