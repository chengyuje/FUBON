package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_COMMISSIONVO extends VOBase {

    /** identifier field */
    private String KEY_NO;

    /** nullable persistent field */
    private String INSPRD_ID;

    /** nullable persistent field */
    private String INSPRD_ANNUAL;

    /** nullable persistent field */
    private String ANNUAL;

    /** nullable persistent field */
    private String TYPE;

    /** persistent field */
    private BigDecimal COMM_RATE;

    /** persistent field */
    private BigDecimal CNR_RATE;

    /** nullable persistent field */
    private BigDecimal CNR_YIELD;

    /** nullable persistent field */
    private BigDecimal CNR_MULTIPLE;

    /** nullable persistent field */
    private Timestamp MULTIPLE_SDATE;

    /** nullable persistent field */
    private Timestamp MULTIPLE_EDATE;

    /** nullable persistent field */
    private String APPROVER;

    /** nullable persistent field */
    private Timestamp APP_DATE;

    /** nullable persistent field */
    private BigDecimal COMPANY_NUM;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_COMMISSION";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_COMMISSIONVO(String KEY_NO, String INSPRD_ID, String INSPRD_ANNUAL, String ANNUAL, String TYPE, BigDecimal COMM_RATE, BigDecimal CNR_RATE, BigDecimal CNR_YIELD, BigDecimal CNR_MULTIPLE, Timestamp MULTIPLE_SDATE, Timestamp MULTIPLE_EDATE, String APPROVER, Timestamp APP_DATE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, BigDecimal COMPANY_NUM, Long version) {
        this.KEY_NO = KEY_NO;
        this.INSPRD_ID = INSPRD_ID;
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
        this.ANNUAL = ANNUAL;
        this.TYPE = TYPE;
        this.COMM_RATE = COMM_RATE;
        this.CNR_RATE = CNR_RATE;
        this.CNR_YIELD = CNR_YIELD;
        this.CNR_MULTIPLE = CNR_MULTIPLE;
        this.MULTIPLE_SDATE = MULTIPLE_SDATE;
        this.MULTIPLE_EDATE = MULTIPLE_EDATE;
        this.APPROVER = APPROVER;
        this.APP_DATE = APP_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.COMPANY_NUM = COMPANY_NUM;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_COMMISSIONVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_COMMISSIONVO(String KEY_NO, BigDecimal COMM_RATE, BigDecimal CNR_RATE) {
        this.KEY_NO = KEY_NO;
        this.COMM_RATE = COMM_RATE;
        this.CNR_RATE = CNR_RATE;
    }

    public String getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(String KEY_NO) {
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

    public String getANNUAL() {
        return this.ANNUAL;
    }

    public void setANNUAL(String ANNUAL) {
        this.ANNUAL = ANNUAL;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public BigDecimal getCOMM_RATE() {
        return this.COMM_RATE;
    }

    public void setCOMM_RATE(BigDecimal COMM_RATE) {
        this.COMM_RATE = COMM_RATE;
    }

    public BigDecimal getCNR_RATE() {
        return this.CNR_RATE;
    }

    public void setCNR_RATE(BigDecimal CNR_RATE) {
        this.CNR_RATE = CNR_RATE;
    }

    public BigDecimal getCNR_YIELD() {
        return this.CNR_YIELD;
    }

    public void setCNR_YIELD(BigDecimal CNR_YIELD) {
        this.CNR_YIELD = CNR_YIELD;
    }

    public BigDecimal getCNR_MULTIPLE() {
        return this.CNR_MULTIPLE;
    }

    public void setCNR_MULTIPLE(BigDecimal CNR_MULTIPLE) {
        this.CNR_MULTIPLE = CNR_MULTIPLE;
    }

    public Timestamp getMULTIPLE_SDATE() {
        return this.MULTIPLE_SDATE;
    }

    public void setMULTIPLE_SDATE(Timestamp MULTIPLE_SDATE) {
        this.MULTIPLE_SDATE = MULTIPLE_SDATE;
    }

    public Timestamp getMULTIPLE_EDATE() {
        return this.MULTIPLE_EDATE;
    }

    public void setMULTIPLE_EDATE(Timestamp MULTIPLE_EDATE) {
        this.MULTIPLE_EDATE = MULTIPLE_EDATE;
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

    public BigDecimal getCOMPANY_NUM() {
        return this.COMPANY_NUM;
    }

    public void setCOMPANY_NUM(BigDecimal COMPANY_NUM) {
        this.COMPANY_NUM = COMPANY_NUM;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO", getKEY_NO())
            .toString();
    }

}
