package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_APPLY_MAINVO extends VOBase {

    /** identifier field */
    private String APPLY_SEQ;

    /** persistent field */
    private String ACT_SEQ;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String REC_NAME;

    /** nullable persistent field */
    private String REC_TEL_NO;

    /** nullable persistent field */
    private String REC_MOBILE_NO;

    /** nullable persistent field */
    private String ADDRESS;

    /** nullable persistent field */
    private BigDecimal EXCHANGE_POINTS;

    /** nullable persistent field */
    private BigDecimal EXCHANGE_REWARD;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_APPLY_MAINVO(String APPLY_SEQ, String ACT_SEQ, String CUST_ID, String REC_NAME, String REC_TEL_NO, String REC_MOBILE_NO, String ADDRESS, BigDecimal EXCHANGE_POINTS, BigDecimal EXCHANGE_REWARD, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.APPLY_SEQ = APPLY_SEQ;
        this.ACT_SEQ = ACT_SEQ;
        this.CUST_ID = CUST_ID;
        this.REC_NAME = REC_NAME;
        this.REC_TEL_NO = REC_TEL_NO;
        this.REC_MOBILE_NO = REC_MOBILE_NO;
        this.ADDRESS = ADDRESS;
        this.EXCHANGE_POINTS = EXCHANGE_POINTS;
        this.EXCHANGE_REWARD = EXCHANGE_REWARD;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_APPLY_MAINVO() {
    }

    /** minimal constructor */
    public TBMGM_APPLY_MAINVO(String APPLY_SEQ, String ACT_SEQ) {
        this.APPLY_SEQ = APPLY_SEQ;
        this.ACT_SEQ = ACT_SEQ;
    }

    public String getAPPLY_SEQ() {
        return this.APPLY_SEQ;
    }

    public void setAPPLY_SEQ(String APPLY_SEQ) {
        this.APPLY_SEQ = APPLY_SEQ;
    }

    public String getACT_SEQ() {
        return this.ACT_SEQ;
    }

    public void setACT_SEQ(String ACT_SEQ) {
        this.ACT_SEQ = ACT_SEQ;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getREC_NAME() {
        return this.REC_NAME;
    }

    public void setREC_NAME(String REC_NAME) {
        this.REC_NAME = REC_NAME;
    }

    public String getREC_TEL_NO() {
        return this.REC_TEL_NO;
    }

    public void setREC_TEL_NO(String REC_TEL_NO) {
        this.REC_TEL_NO = REC_TEL_NO;
    }

    public String getREC_MOBILE_NO() {
        return this.REC_MOBILE_NO;
    }

    public void setREC_MOBILE_NO(String REC_MOBILE_NO) {
        this.REC_MOBILE_NO = REC_MOBILE_NO;
    }

    public String getADDRESS() {
        return this.ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public BigDecimal getEXCHANGE_POINTS() {
        return this.EXCHANGE_POINTS;
    }

    public void setEXCHANGE_POINTS(BigDecimal EXCHANGE_POINTS) {
        this.EXCHANGE_POINTS = EXCHANGE_POINTS;
    }

    public BigDecimal getEXCHANGE_REWARD() {
        return this.EXCHANGE_REWARD;
    }

    public void setEXCHANGE_REWARD(BigDecimal EXCHANGE_REWARD) {
        this.EXCHANGE_REWARD = EXCHANGE_REWARD;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("APPLY_SEQ", getAPPLY_SEQ())
            .toString();
    }

}
