package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_SPECIAL_CNDVO extends VOBase {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** persistent field */
    private String INSPRD_ID;

    /** persistent field */
    private String PAY_TYPE;

    /** persistent field */
    private String SPECIAL_CONDITION;

    /** persistent field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String APPROVER;

    /** nullable persistent field */
    private Timestamp APP_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_SPECIAL_CND";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_SPECIAL_CNDVO(BigDecimal KEY_NO, String INSPRD_ID, String PAY_TYPE, String SPECIAL_CONDITION, BigDecimal SEQ, String APPROVER, Timestamp APP_DATE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.KEY_NO = KEY_NO;
        this.INSPRD_ID = INSPRD_ID;
        this.PAY_TYPE = PAY_TYPE;
        this.SPECIAL_CONDITION = SPECIAL_CONDITION;
        this.SEQ = SEQ;
        this.APPROVER = APPROVER;
        this.APP_DATE = APP_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_SPECIAL_CNDVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_SPECIAL_CNDVO(BigDecimal KEY_NO, String INSPRD_ID, String PAY_TYPE, String SPECIAL_CONDITION, BigDecimal SEQ) {
        this.KEY_NO = KEY_NO;
        this.INSPRD_ID = INSPRD_ID;
        this.PAY_TYPE = PAY_TYPE;
        this.SPECIAL_CONDITION = SPECIAL_CONDITION;
        this.SEQ = SEQ;
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

    public String getPAY_TYPE() {
        return this.PAY_TYPE;
    }

    public void setPAY_TYPE(String PAY_TYPE) {
        this.PAY_TYPE = PAY_TYPE;
    }

    public String getSPECIAL_CONDITION() {
        return this.SPECIAL_CONDITION;
    }

    public void setSPECIAL_CONDITION(String SPECIAL_CONDITION) {
        this.SPECIAL_CONDITION = SPECIAL_CONDITION;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
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
