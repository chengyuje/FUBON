package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_ANCDOCVO extends VOBase {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** persistent field */
    private String INSPRD_ID;

    /** persistent field */
    private String INSPRD_ANNUAL;

    /** persistent field */
    private String Q_ID;

    /** persistent field */
    private BigDecimal Q_SEQ;

    /** nullable persistent field */
    private String APPROVER;

    /** nullable persistent field */
    private Timestamp APP_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_ANCDOC";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_ANCDOCVO(BigDecimal KEY_NO, String INSPRD_ID, String INSPRD_ANNUAL, String Q_ID, BigDecimal Q_SEQ, String APPROVER, Timestamp APP_DATE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.KEY_NO = KEY_NO;
        this.INSPRD_ID = INSPRD_ID;
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
        this.Q_ID = Q_ID;
        this.Q_SEQ = Q_SEQ;
        this.APPROVER = APPROVER;
        this.APP_DATE = APP_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_ANCDOCVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_ANCDOCVO(BigDecimal KEY_NO, String INSPRD_ID, String INSPRD_ANNUAL, String Q_ID, BigDecimal Q_SEQ) {
        this.KEY_NO = KEY_NO;
        this.INSPRD_ID = INSPRD_ID;
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
        this.Q_ID = Q_ID;
        this.Q_SEQ = Q_SEQ;
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public String getINSPRD_ID() {
        return this.INSPRD_ID;
    }

    public void setINSPRD_ID(String INSPRD_ID) {
        this.INSPRD_ID = INSPRD_ID;
    }

    public String getINSPRD_ANNUAL() {
        return this.INSPRD_ANNUAL;
    }

    public void setINSPRD_ANNUAL(String INSPRD_ANNUAL) {
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
    }

    public String getQ_ID() {
        return this.Q_ID;
    }

    public void setQ_ID(String Q_ID) {
        this.Q_ID = Q_ID;
    }

    public BigDecimal getQ_SEQ() {
        return this.Q_SEQ;
    }

    public void setQ_SEQ(BigDecimal Q_SEQ) {
        this.Q_SEQ = Q_SEQ;
    }

    public String getAPPROVER() {
        return this.APPROVER;
    }

    public void setAPPROVER(String APPROVER) {
        this.APPROVER = APPROVER;
    }

    public Timestamp getAPP_DATE() {
        return this.APP_DATE;
    }

    public void setAPP_DATE(Timestamp APP_DATE) {
        this.APP_DATE = APP_DATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO", getKEY_NO())
            .toString();
    }

}
