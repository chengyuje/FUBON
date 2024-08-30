package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_MGMVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** persistent field */
    private String ACT_SEQ;

    /** persistent field */
    private String MGM_CUST_ID;

    /** nullable persistent field */
    private String MGM_SIGN_STATUS;

    /** nullable persistent field */
    private Timestamp MGM_SIGN_REVIEW_DATE;

    /** nullable persistent field */
    private String BE_MGM_CUST_ID;

    /** nullable persistent field */
    private String BE_MGM_CUST_NAME;

    /** nullable persistent field */
    private String BE_MGM_CUST_PHONE;

    /** nullable persistent field */
    private String BE_MGM_SIGN_STATUS;

    /** nullable persistent field */
    private Timestamp BE_MGM_SIGN_REVIEW_DATE;

    /** nullable persistent field */
    private Timestamp MGM_START_DATE;

    /** nullable persistent field */
    private Timestamp MGM_END_DATE;

    /** nullable persistent field */
    private BigDecimal INS_SELL_VOL;

    /** nullable persistent field */
    private String POINTS_TYPE;

    /** nullable persistent field */
    private String MGM_APPR_STATUS;

    /** nullable persistent field */
    private Timestamp ALL_REVIEW_DATE;

    /** nullable persistent field */
    private BigDecimal APPR_POINTS;

    /** nullable persistent field */
    private Timestamp APPR_DATE;

    /** nullable persistent field */
    private String RELEASE_YN;

    /** nullable persistent field */
    private Timestamp RELEASE_DATE;

    /** nullable persistent field */
    private BigDecimal BE_EDIT_POINTS;

    /** nullable persistent field */
    private String POINTS_MODIFIER;

    /** nullable persistent field */
    private Timestamp POINTS_MODIFY_DATE;

    /** nullable persistent field */
    private String MODIFY_REASON;

    /** nullable persistent field */
    private String MEMO;

    /** nullable persistent field */
    private String DELETE_YN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_MGM";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_MGMVO(String SEQ, String ACT_SEQ, String MGM_CUST_ID, String MGM_SIGN_STATUS, Timestamp MGM_SIGN_REVIEW_DATE, String BE_MGM_CUST_ID, String BE_MGM_CUST_NAME, String BE_MGM_CUST_PHONE, String BE_MGM_SIGN_STATUS, Timestamp BE_MGM_SIGN_REVIEW_DATE, Timestamp MGM_START_DATE, Timestamp MGM_END_DATE, BigDecimal INS_SELL_VOL, String POINTS_TYPE, String MGM_APPR_STATUS, Timestamp ALL_REVIEW_DATE, BigDecimal APPR_POINTS, Timestamp APPR_DATE, String RELEASE_YN, Timestamp RELEASE_DATE, BigDecimal BE_EDIT_POINTS, String POINTS_MODIFIER, Timestamp POINTS_MODIFY_DATE, String MODIFY_REASON, String MEMO, String DELETE_YN, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.ACT_SEQ = ACT_SEQ;
        this.MGM_CUST_ID = MGM_CUST_ID;
        this.MGM_SIGN_STATUS = MGM_SIGN_STATUS;
        this.MGM_SIGN_REVIEW_DATE = MGM_SIGN_REVIEW_DATE;
        this.BE_MGM_CUST_ID = BE_MGM_CUST_ID;
        this.BE_MGM_CUST_NAME = BE_MGM_CUST_NAME;
        this.BE_MGM_CUST_PHONE = BE_MGM_CUST_PHONE;
        this.BE_MGM_SIGN_STATUS = BE_MGM_SIGN_STATUS;
        this.BE_MGM_SIGN_REVIEW_DATE = BE_MGM_SIGN_REVIEW_DATE;
        this.MGM_START_DATE = MGM_START_DATE;
        this.MGM_END_DATE = MGM_END_DATE;
        this.INS_SELL_VOL = INS_SELL_VOL;
        this.POINTS_TYPE = POINTS_TYPE;
        this.MGM_APPR_STATUS = MGM_APPR_STATUS;
        this.ALL_REVIEW_DATE = ALL_REVIEW_DATE;
        this.APPR_POINTS = APPR_POINTS;
        this.APPR_DATE = APPR_DATE;
        this.RELEASE_YN = RELEASE_YN;
        this.RELEASE_DATE = RELEASE_DATE;
        this.BE_EDIT_POINTS = BE_EDIT_POINTS;
        this.POINTS_MODIFIER = POINTS_MODIFIER;
        this.POINTS_MODIFY_DATE = POINTS_MODIFY_DATE;
        this.MODIFY_REASON = MODIFY_REASON;
        this.MEMO = MEMO;
        this.DELETE_YN = DELETE_YN;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_MGMVO() {
    }

    /** minimal constructor */
    public TBMGM_MGMVO(String SEQ, String ACT_SEQ, String MGM_CUST_ID) {
        this.SEQ = SEQ;
        this.ACT_SEQ = ACT_SEQ;
        this.MGM_CUST_ID = MGM_CUST_ID;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getACT_SEQ() {
        return this.ACT_SEQ;
    }

    public void setACT_SEQ(String ACT_SEQ) {
        this.ACT_SEQ = ACT_SEQ;
    }

    public String getMGM_CUST_ID() {
        return this.MGM_CUST_ID;
    }

    public void setMGM_CUST_ID(String MGM_CUST_ID) {
        this.MGM_CUST_ID = MGM_CUST_ID;
    }

    public String getMGM_SIGN_STATUS() {
        return this.MGM_SIGN_STATUS;
    }

    public void setMGM_SIGN_STATUS(String MGM_SIGN_STATUS) {
        this.MGM_SIGN_STATUS = MGM_SIGN_STATUS;
    }

    public Timestamp getMGM_SIGN_REVIEW_DATE() {
        return this.MGM_SIGN_REVIEW_DATE;
    }

    public void setMGM_SIGN_REVIEW_DATE(Timestamp MGM_SIGN_REVIEW_DATE) {
        this.MGM_SIGN_REVIEW_DATE = MGM_SIGN_REVIEW_DATE;
    }

    public String getBE_MGM_CUST_ID() {
        return this.BE_MGM_CUST_ID;
    }

    public void setBE_MGM_CUST_ID(String BE_MGM_CUST_ID) {
        this.BE_MGM_CUST_ID = BE_MGM_CUST_ID;
    }

    public String getBE_MGM_CUST_NAME() {
        return this.BE_MGM_CUST_NAME;
    }

    public void setBE_MGM_CUST_NAME(String BE_MGM_CUST_NAME) {
        this.BE_MGM_CUST_NAME = BE_MGM_CUST_NAME;
    }

    public String getBE_MGM_CUST_PHONE() {
        return this.BE_MGM_CUST_PHONE;
    }

    public void setBE_MGM_CUST_PHONE(String BE_MGM_CUST_PHONE) {
        this.BE_MGM_CUST_PHONE = BE_MGM_CUST_PHONE;
    }

    public String getBE_MGM_SIGN_STATUS() {
        return this.BE_MGM_SIGN_STATUS;
    }

    public void setBE_MGM_SIGN_STATUS(String BE_MGM_SIGN_STATUS) {
        this.BE_MGM_SIGN_STATUS = BE_MGM_SIGN_STATUS;
    }

    public Timestamp getBE_MGM_SIGN_REVIEW_DATE() {
        return this.BE_MGM_SIGN_REVIEW_DATE;
    }

    public void setBE_MGM_SIGN_REVIEW_DATE(Timestamp BE_MGM_SIGN_REVIEW_DATE) {
        this.BE_MGM_SIGN_REVIEW_DATE = BE_MGM_SIGN_REVIEW_DATE;
    }

    public Timestamp getMGM_START_DATE() {
        return this.MGM_START_DATE;
    }

    public void setMGM_START_DATE(Timestamp MGM_START_DATE) {
        this.MGM_START_DATE = MGM_START_DATE;
    }

    public Timestamp getMGM_END_DATE() {
        return this.MGM_END_DATE;
    }

    public void setMGM_END_DATE(Timestamp MGM_END_DATE) {
        this.MGM_END_DATE = MGM_END_DATE;
    }

    public BigDecimal getINS_SELL_VOL() {
        return this.INS_SELL_VOL;
    }

    public void setINS_SELL_VOL(BigDecimal INS_SELL_VOL) {
        this.INS_SELL_VOL = INS_SELL_VOL;
    }

    public String getPOINTS_TYPE() {
        return this.POINTS_TYPE;
    }

    public void setPOINTS_TYPE(String POINTS_TYPE) {
        this.POINTS_TYPE = POINTS_TYPE;
    }

    public String getMGM_APPR_STATUS() {
        return this.MGM_APPR_STATUS;
    }

    public void setMGM_APPR_STATUS(String MGM_APPR_STATUS) {
        this.MGM_APPR_STATUS = MGM_APPR_STATUS;
    }

    public Timestamp getALL_REVIEW_DATE() {
        return this.ALL_REVIEW_DATE;
    }

    public void setALL_REVIEW_DATE(Timestamp ALL_REVIEW_DATE) {
        this.ALL_REVIEW_DATE = ALL_REVIEW_DATE;
    }

    public BigDecimal getAPPR_POINTS() {
        return this.APPR_POINTS;
    }

    public void setAPPR_POINTS(BigDecimal APPR_POINTS) {
        this.APPR_POINTS = APPR_POINTS;
    }

    public Timestamp getAPPR_DATE() {
        return this.APPR_DATE;
    }

    public void setAPPR_DATE(Timestamp APPR_DATE) {
        this.APPR_DATE = APPR_DATE;
    }

    public String getRELEASE_YN() {
        return this.RELEASE_YN;
    }

    public void setRELEASE_YN(String RELEASE_YN) {
        this.RELEASE_YN = RELEASE_YN;
    }

    public Timestamp getRELEASE_DATE() {
        return this.RELEASE_DATE;
    }

    public void setRELEASE_DATE(Timestamp RELEASE_DATE) {
        this.RELEASE_DATE = RELEASE_DATE;
    }

    public BigDecimal getBE_EDIT_POINTS() {
        return this.BE_EDIT_POINTS;
    }

    public void setBE_EDIT_POINTS(BigDecimal BE_EDIT_POINTS) {
        this.BE_EDIT_POINTS = BE_EDIT_POINTS;
    }

    public String getPOINTS_MODIFIER() {
        return this.POINTS_MODIFIER;
    }

    public void setPOINTS_MODIFIER(String POINTS_MODIFIER) {
        this.POINTS_MODIFIER = POINTS_MODIFIER;
    }

    public Timestamp getPOINTS_MODIFY_DATE() {
        return this.POINTS_MODIFY_DATE;
    }

    public void setPOINTS_MODIFY_DATE(Timestamp POINTS_MODIFY_DATE) {
        this.POINTS_MODIFY_DATE = POINTS_MODIFY_DATE;
    }

    public String getMODIFY_REASON() {
        return this.MODIFY_REASON;
    }

    public void setMODIFY_REASON(String MODIFY_REASON) {
        this.MODIFY_REASON = MODIFY_REASON;
    }

    public String getMEMO() {
        return this.MEMO;
    }

    public void setMEMO(String MEMO) {
        this.MEMO = MEMO;
    }

    public String getDELETE_YN() {
        return this.DELETE_YN;
    }

    public void setDELETE_YN(String DELETE_YN) {
        this.DELETE_YN = DELETE_YN;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
