package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_PPT_MAINVO extends VOBase {

    /** identifier field */
    private BigDecimal INS_KEYNO;

    /** nullable persistent field */
    private String INS_KIND;

    /** nullable persistent field */
    private String INS_ID;

    /** nullable persistent field */
    private String POLICY_NO;

    /** nullable persistent field */
    private String REG_TYPE;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String PROPOSER_NAME;

    /** nullable persistent field */
    private String INSURED_ID;

    /** nullable persistent field */
    private String INSURED_NAME;

    /** nullable persistent field */
    private String PPT_TYPE;

    /** nullable persistent field */
    private Timestamp APPLY_DATE;

    /** nullable persistent field */
    private Timestamp KEYIN_DATE;

    /** nullable persistent field */
    private BigDecimal REAL_PREMIUM;

    /** nullable persistent field */
    private BigDecimal BATCH_INFO_KEYNO;

    /** nullable persistent field */
    private String REF_CON_ID;

    /** nullable persistent field */
    private String RECRUIT_ID;

    /** nullable persistent field */
    private BigDecimal STATUS;

    /** nullable persistent field */
    private String REJ_REASON;

    /** nullable persistent field */
    private String REJ_REASON_OTH;

    /** nullable persistent field */
    private String SIGN_INC;

    /** nullable persistent field */
    private String UNOPEN_ACCT;

    /** nullable persistent field */
    private String DELETE_ID;

    /** nullable persistent field */
    private String DELETE_DATE;

    /** nullable persistent field */
    private String BEF_SIGN_NO;

    /** nullable persistent field */
    private String BEF_SIGN_OPRID;

    /** nullable persistent field */
    private Timestamp BEF_SIGN_DATE;

    /** nullable persistent field */
    private String SIGN_NO;

    /** nullable persistent field */
    private String SIGN_OPRID;

    /** nullable persistent field */
    private Timestamp SIGN_DATE;

    /** nullable persistent field */
    private String AFT_SIGN_OPRID;

    /** nullable persistent field */
    private Timestamp AFT_SIGN_DATE;

    /** nullable persistent field */
    private String REMARK_BANK;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_PPT_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_PPT_MAINVO(BigDecimal INS_KEYNO, String INS_KIND, String INS_ID, String POLICY_NO, String REG_TYPE, String BRANCH_NBR, String CUST_ID, String PROPOSER_NAME, String INSURED_ID, String INSURED_NAME, String PPT_TYPE, Timestamp APPLY_DATE, Timestamp KEYIN_DATE, BigDecimal REAL_PREMIUM, BigDecimal BATCH_INFO_KEYNO, String REF_CON_ID, String RECRUIT_ID, BigDecimal STATUS, String REJ_REASON, String REJ_REASON_OTH, String SIGN_INC, String UNOPEN_ACCT, String DELETE_ID, String DELETE_DATE, String BEF_SIGN_NO, String BEF_SIGN_OPRID, Timestamp BEF_SIGN_DATE, String SIGN_NO, String SIGN_OPRID, Timestamp SIGN_DATE, String AFT_SIGN_OPRID, Timestamp AFT_SIGN_DATE, String REMARK_BANK, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.INS_KEYNO = INS_KEYNO;
        this.INS_KIND = INS_KIND;
        this.INS_ID = INS_ID;
        this.POLICY_NO = POLICY_NO;
        this.REG_TYPE = REG_TYPE;
        this.BRANCH_NBR = BRANCH_NBR;
        this.CUST_ID = CUST_ID;
        this.PROPOSER_NAME = PROPOSER_NAME;
        this.INSURED_ID = INSURED_ID;
        this.INSURED_NAME = INSURED_NAME;
        this.PPT_TYPE = PPT_TYPE;
        this.APPLY_DATE = APPLY_DATE;
        this.KEYIN_DATE = KEYIN_DATE;
        this.REAL_PREMIUM = REAL_PREMIUM;
        this.BATCH_INFO_KEYNO = BATCH_INFO_KEYNO;
        this.REF_CON_ID = REF_CON_ID;
        this.RECRUIT_ID = RECRUIT_ID;
        this.STATUS = STATUS;
        this.REJ_REASON = REJ_REASON;
        this.REJ_REASON_OTH = REJ_REASON_OTH;
        this.SIGN_INC = SIGN_INC;
        this.UNOPEN_ACCT = UNOPEN_ACCT;
        this.DELETE_ID = DELETE_ID;
        this.DELETE_DATE = DELETE_DATE;
        this.BEF_SIGN_NO = BEF_SIGN_NO;
        this.BEF_SIGN_OPRID = BEF_SIGN_OPRID;
        this.BEF_SIGN_DATE = BEF_SIGN_DATE;
        this.SIGN_NO = SIGN_NO;
        this.SIGN_OPRID = SIGN_OPRID;
        this.SIGN_DATE = SIGN_DATE;
        this.AFT_SIGN_OPRID = AFT_SIGN_OPRID;
        this.AFT_SIGN_DATE = AFT_SIGN_DATE;
        this.REMARK_BANK = REMARK_BANK;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_PPT_MAINVO() {
    }

    /** minimal constructor */
    public TBIOT_PPT_MAINVO(BigDecimal INS_KEYNO) {
        this.INS_KEYNO = INS_KEYNO;
    }

    public BigDecimal getINS_KEYNO() {
        return this.INS_KEYNO;
    }

    public void setINS_KEYNO(BigDecimal INS_KEYNO) {
        this.INS_KEYNO = INS_KEYNO;
    }

    public String getINS_KIND() {
        return this.INS_KIND;
    }

    public void setINS_KIND(String INS_KIND) {
        this.INS_KIND = INS_KIND;
    }

    public String getINS_ID() {
        return this.INS_ID;
    }

    public void setINS_ID(String INS_ID) {
        this.INS_ID = INS_ID;
    }

    public String getPOLICY_NO() {
        return this.POLICY_NO;
    }

    public void setPOLICY_NO(String POLICY_NO) {
        this.POLICY_NO = POLICY_NO;
    }

    public String getREG_TYPE() {
        return this.REG_TYPE;
    }

    public void setREG_TYPE(String REG_TYPE) {
        this.REG_TYPE = REG_TYPE;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getPROPOSER_NAME() {
        return this.PROPOSER_NAME;
    }

    public void setPROPOSER_NAME(String PROPOSER_NAME) {
        this.PROPOSER_NAME = PROPOSER_NAME;
    }

    public String getINSURED_ID() {
        return this.INSURED_ID;
    }

    public void setINSURED_ID(String INSURED_ID) {
        this.INSURED_ID = INSURED_ID;
    }

    public String getINSURED_NAME() {
        return this.INSURED_NAME;
    }

    public void setINSURED_NAME(String INSURED_NAME) {
        this.INSURED_NAME = INSURED_NAME;
    }

    public String getPPT_TYPE() {
        return this.PPT_TYPE;
    }

    public void setPPT_TYPE(String PPT_TYPE) {
        this.PPT_TYPE = PPT_TYPE;
    }

    public Timestamp getAPPLY_DATE() {
        return this.APPLY_DATE;
    }

    public void setAPPLY_DATE(Timestamp APPLY_DATE) {
        this.APPLY_DATE = APPLY_DATE;
    }

    public Timestamp getKEYIN_DATE() {
        return this.KEYIN_DATE;
    }

    public void setKEYIN_DATE(Timestamp KEYIN_DATE) {
        this.KEYIN_DATE = KEYIN_DATE;
    }

    public BigDecimal getREAL_PREMIUM() {
        return this.REAL_PREMIUM;
    }

    public void setREAL_PREMIUM(BigDecimal REAL_PREMIUM) {
        this.REAL_PREMIUM = REAL_PREMIUM;
    }

    public BigDecimal getBATCH_INFO_KEYNO() {
        return this.BATCH_INFO_KEYNO;
    }

    public void setBATCH_INFO_KEYNO(BigDecimal BATCH_INFO_KEYNO) {
        this.BATCH_INFO_KEYNO = BATCH_INFO_KEYNO;
    }

    public String getREF_CON_ID() {
        return this.REF_CON_ID;
    }

    public void setREF_CON_ID(String REF_CON_ID) {
        this.REF_CON_ID = REF_CON_ID;
    }

    public String getRECRUIT_ID() {
        return this.RECRUIT_ID;
    }

    public void setRECRUIT_ID(String RECRUIT_ID) {
        this.RECRUIT_ID = RECRUIT_ID;
    }

    public BigDecimal getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(BigDecimal STATUS) {
        this.STATUS = STATUS;
    }

    public String getREJ_REASON() {
        return this.REJ_REASON;
    }

    public void setREJ_REASON(String REJ_REASON) {
        this.REJ_REASON = REJ_REASON;
    }

    public String getREJ_REASON_OTH() {
        return this.REJ_REASON_OTH;
    }

    public void setREJ_REASON_OTH(String REJ_REASON_OTH) {
        this.REJ_REASON_OTH = REJ_REASON_OTH;
    }

    public String getSIGN_INC() {
        return this.SIGN_INC;
    }

    public void setSIGN_INC(String SIGN_INC) {
        this.SIGN_INC = SIGN_INC;
    }

    public String getUNOPEN_ACCT() {
        return this.UNOPEN_ACCT;
    }

    public void setUNOPEN_ACCT(String UNOPEN_ACCT) {
        this.UNOPEN_ACCT = UNOPEN_ACCT;
    }

    public String getDELETE_ID() {
        return this.DELETE_ID;
    }

    public void setDELETE_ID(String DELETE_ID) {
        this.DELETE_ID = DELETE_ID;
    }

    public String getDELETE_DATE() {
        return this.DELETE_DATE;
    }

    public void setDELETE_DATE(String DELETE_DATE) {
        this.DELETE_DATE = DELETE_DATE;
    }

    public String getBEF_SIGN_NO() {
        return this.BEF_SIGN_NO;
    }

    public void setBEF_SIGN_NO(String BEF_SIGN_NO) {
        this.BEF_SIGN_NO = BEF_SIGN_NO;
    }

    public String getBEF_SIGN_OPRID() {
        return this.BEF_SIGN_OPRID;
    }

    public void setBEF_SIGN_OPRID(String BEF_SIGN_OPRID) {
        this.BEF_SIGN_OPRID = BEF_SIGN_OPRID;
    }

    public Timestamp getBEF_SIGN_DATE() {
        return this.BEF_SIGN_DATE;
    }

    public void setBEF_SIGN_DATE(Timestamp BEF_SIGN_DATE) {
        this.BEF_SIGN_DATE = BEF_SIGN_DATE;
    }

    public String getSIGN_NO() {
        return this.SIGN_NO;
    }

    public void setSIGN_NO(String SIGN_NO) {
        this.SIGN_NO = SIGN_NO;
    }

    public String getSIGN_OPRID() {
        return this.SIGN_OPRID;
    }

    public void setSIGN_OPRID(String SIGN_OPRID) {
        this.SIGN_OPRID = SIGN_OPRID;
    }

    public Timestamp getSIGN_DATE() {
        return this.SIGN_DATE;
    }

    public void setSIGN_DATE(Timestamp SIGN_DATE) {
        this.SIGN_DATE = SIGN_DATE;
    }

    public String getAFT_SIGN_OPRID() {
        return this.AFT_SIGN_OPRID;
    }

    public void setAFT_SIGN_OPRID(String AFT_SIGN_OPRID) {
        this.AFT_SIGN_OPRID = AFT_SIGN_OPRID;
    }

    public Timestamp getAFT_SIGN_DATE() {
        return this.AFT_SIGN_DATE;
    }

    public void setAFT_SIGN_DATE(Timestamp AFT_SIGN_DATE) {
        this.AFT_SIGN_DATE = AFT_SIGN_DATE;
    }

    public String getREMARK_BANK() {
        return this.REMARK_BANK;
    }

    public void setREMARK_BANK(String REMARK_BANK) {
        this.REMARK_BANK = REMARK_BANK;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INS_KEYNO", getINS_KEYNO())
            .toString();
    }

}
