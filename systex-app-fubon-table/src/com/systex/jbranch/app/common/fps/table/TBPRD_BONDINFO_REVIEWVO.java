package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_BONDINFO_REVIEWVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String ISIN_CODE;

    /** nullable persistent field */
    private String BOND_PRIORITY;

    /** nullable persistent field */
    private String CREDIT_RATING_SP;

    /** nullable persistent field */
    private String CREDIT_RATING_MODDY;

    /** nullable persistent field */
    private String CREDIT_RATING_FITCH;

    /** nullable persistent field */
    private String BOND_CREDIT_RATING_SP;

    /** nullable persistent field */
    private String BOND_CREDIT_RATING_MODDY;

    /** nullable persistent field */
    private String BOND_CREDIT_RATING_FITCH;

    /** nullable persistent field */
    private String ISSUER_BUYBACK;

    /** nullable persistent field */
    private String RISK_CHECKLIST;

    /** nullable persistent field */
    private BigDecimal CNR_YIELD;

    /** nullable persistent field */
    private BigDecimal CNR_MULTIPLE;

    /** nullable persistent field */
    private Timestamp MULTIPLE_SDATE;

    /** nullable persistent field */
    private Timestamp MULTIPLE_EDATE;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;

    /** nullable persistent field */
    private String PROJECT;

    /** nullable persistent field */
    private String CUSTOMER_LEVEL;


    public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_BONDINFO_REVIEW";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_BONDINFO_REVIEWVO(BigDecimal SEQ, String PRD_ID, String ISIN_CODE, String BOND_PRIORITY, String CREDIT_RATING_SP, String CREDIT_RATING_MODDY, String CREDIT_RATING_FITCH, String BOND_CREDIT_RATING_SP, String BOND_CREDIT_RATING_MODDY, String BOND_CREDIT_RATING_FITCH, String ISSUER_BUYBACK, String RISK_CHECKLIST, BigDecimal CNR_YIELD, BigDecimal CNR_MULTIPLE, Timestamp MULTIPLE_SDATE, Timestamp MULTIPLE_EDATE, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version, String PROJECT, String CUSTOMER_LEVEL) {
        this.SEQ = SEQ;
        this.PRD_ID = PRD_ID;
        this.ISIN_CODE = ISIN_CODE;
        this.BOND_PRIORITY = BOND_PRIORITY;
        this.CREDIT_RATING_SP = CREDIT_RATING_SP;
        this.CREDIT_RATING_MODDY = CREDIT_RATING_MODDY;
        this.CREDIT_RATING_FITCH = CREDIT_RATING_FITCH;
        this.BOND_CREDIT_RATING_SP = BOND_CREDIT_RATING_SP;
        this.BOND_CREDIT_RATING_MODDY = BOND_CREDIT_RATING_MODDY;
        this.BOND_CREDIT_RATING_FITCH = BOND_CREDIT_RATING_FITCH;
        this.ISSUER_BUYBACK = ISSUER_BUYBACK;
        this.RISK_CHECKLIST = RISK_CHECKLIST;
        this.CNR_YIELD = CNR_YIELD;
        this.CNR_MULTIPLE = CNR_MULTIPLE;
        this.MULTIPLE_SDATE = MULTIPLE_SDATE;
        this.MULTIPLE_EDATE = MULTIPLE_EDATE;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
        this.PROJECT = PROJECT;
        this.CUSTOMER_LEVEL = CUSTOMER_LEVEL;
    }


    /** default constructor */
    public TBPRD_BONDINFO_REVIEWVO() {
    }

    /** minimal constructor */
    public TBPRD_BONDINFO_REVIEWVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getISIN_CODE() {
        return this.ISIN_CODE;
    }

    public void setISIN_CODE(String ISIN_CODE) {
        this.ISIN_CODE = ISIN_CODE;
    }

    public String getBOND_PRIORITY() {
        return this.BOND_PRIORITY;
    }

    public void setBOND_PRIORITY(String BOND_PRIORITY) {
        this.BOND_PRIORITY = BOND_PRIORITY;
    }

    public String getCREDIT_RATING_SP() {
        return this.CREDIT_RATING_SP;
    }

    public void setCREDIT_RATING_SP(String CREDIT_RATING_SP) {
        this.CREDIT_RATING_SP = CREDIT_RATING_SP;
    }

    public String getCREDIT_RATING_MODDY() {
        return this.CREDIT_RATING_MODDY;
    }

    public void setCREDIT_RATING_MODDY(String CREDIT_RATING_MODDY) {
        this.CREDIT_RATING_MODDY = CREDIT_RATING_MODDY;
    }

    public String getCREDIT_RATING_FITCH() {
        return this.CREDIT_RATING_FITCH;
    }

    public void setCREDIT_RATING_FITCH(String CREDIT_RATING_FITCH) {
        this.CREDIT_RATING_FITCH = CREDIT_RATING_FITCH;
    }

    public String getBOND_CREDIT_RATING_SP() {
        return this.BOND_CREDIT_RATING_SP;
    }

    public void setBOND_CREDIT_RATING_SP(String BOND_CREDIT_RATING_SP) {
        this.BOND_CREDIT_RATING_SP = BOND_CREDIT_RATING_SP;
    }

    public String getBOND_CREDIT_RATING_MODDY() {
        return this.BOND_CREDIT_RATING_MODDY;
    }

    public void setBOND_CREDIT_RATING_MODDY(String BOND_CREDIT_RATING_MODDY) {
        this.BOND_CREDIT_RATING_MODDY = BOND_CREDIT_RATING_MODDY;
    }

    public String getBOND_CREDIT_RATING_FITCH() {
        return this.BOND_CREDIT_RATING_FITCH;
    }

    public void setBOND_CREDIT_RATING_FITCH(String BOND_CREDIT_RATING_FITCH) {
        this.BOND_CREDIT_RATING_FITCH = BOND_CREDIT_RATING_FITCH;
    }

    public String getISSUER_BUYBACK() {
        return this.ISSUER_BUYBACK;
    }

    public void setISSUER_BUYBACK(String ISSUER_BUYBACK) {
        this.ISSUER_BUYBACK = ISSUER_BUYBACK;
    }

    public String getRISK_CHECKLIST() {
        return this.RISK_CHECKLIST;
    }

    public void setRISK_CHECKLIST(String RISK_CHECKLIST) {
        this.RISK_CHECKLIST = RISK_CHECKLIST;
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

    public String getCUSTOMER_LEVEL() {
        return CUSTOMER_LEVEL;
    }

    public void setCUSTOMER_LEVEL(String CUSTOMER_LEVEL) {
        this.CUSTOMER_LEVEL = CUSTOMER_LEVEL;
    }

    public String getPROJECT() {
        return PROJECT;
    }

    public void setPROJECT(String PROJECT) {
        this.PROJECT = PROJECT;
    }
}
