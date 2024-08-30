package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_DOCCHKVO extends VOBase {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** nullable persistent field */
    private String REG_TYPE;

    /** nullable persistent field */
    private String OTH_TYPE;

    /** nullable persistent field */
    private String INSPRD_ID;

    /** nullable persistent field */
    private String DOC_TYPE;

    /** nullable persistent field */
    private String DOC_NAME;

    /** nullable persistent field */
    private String DOC_LEVEL;

    /** nullable persistent field */
    private String SIGN_INC;

    /** nullable persistent field */
    private BigDecimal DOC_SEQ;

    /** nullable persistent field */
    private String APPROVER;

    /** nullable persistent field */
    private Timestamp APP_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_DOCCHK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_DOCCHKVO(BigDecimal KEY_NO, String REG_TYPE, String OTH_TYPE, String INSPRD_ID, String DOC_TYPE, String DOC_NAME, String DOC_LEVEL, String SIGN_INC, BigDecimal DOC_SEQ, String APPROVER, Timestamp APP_DATE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.KEY_NO = KEY_NO;
        this.REG_TYPE = REG_TYPE;
        this.OTH_TYPE = OTH_TYPE;
        this.INSPRD_ID = INSPRD_ID;
        this.DOC_TYPE = DOC_TYPE;
        this.DOC_NAME = DOC_NAME;
        this.DOC_LEVEL = DOC_LEVEL;
        this.SIGN_INC = SIGN_INC;
        this.DOC_SEQ = DOC_SEQ;
        this.APPROVER = APPROVER;
        this.APP_DATE = APP_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_DOCCHKVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_DOCCHKVO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public String getREG_TYPE() {
        return this.REG_TYPE;
    }

    public void setREG_TYPE(String REG_TYPE) {
        this.REG_TYPE = REG_TYPE;
    }

    public String getOTH_TYPE() {
        return this.OTH_TYPE;
    }

    public void setOTH_TYPE(String OTH_TYPE) {
        this.OTH_TYPE = OTH_TYPE;
    }

    public String getINSPRD_ID() {
        return this.INSPRD_ID;
    }

    public void setINSPRD_ID(String INSPRD_ID) {
        this.INSPRD_ID = INSPRD_ID;
    }

    public String getDOC_TYPE() {
        return this.DOC_TYPE;
    }

    public void setDOC_TYPE(String DOC_TYPE) {
        this.DOC_TYPE = DOC_TYPE;
    }

    public String getDOC_NAME() {
        return this.DOC_NAME;
    }

    public void setDOC_NAME(String DOC_NAME) {
        this.DOC_NAME = DOC_NAME;
    }

    public String getDOC_LEVEL() {
        return this.DOC_LEVEL;
    }

    public void setDOC_LEVEL(String DOC_LEVEL) {
        this.DOC_LEVEL = DOC_LEVEL;
    }

    public String getSIGN_INC() {
        return this.SIGN_INC;
    }

    public void setSIGN_INC(String SIGN_INC) {
        this.SIGN_INC = SIGN_INC;
    }

    public BigDecimal getDOC_SEQ() {
        return this.DOC_SEQ;
    }

    public void setDOC_SEQ(BigDecimal DOC_SEQ) {
        this.DOC_SEQ = DOC_SEQ;
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
