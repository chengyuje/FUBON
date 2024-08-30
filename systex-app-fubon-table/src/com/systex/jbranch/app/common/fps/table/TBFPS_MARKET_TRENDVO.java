package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_MARKET_TRENDVO extends VOBase {

    /** identifier field */
    private String PARAM_NO;

    /** nullable persistent field */
    private Timestamp EFFECT_START_DATE;

    /** nullable persistent field */
    private Timestamp EFFECT_END_DATE;

    /** nullable persistent field */
    private Timestamp SUBMIT_DATE;

    /** nullable persistent field */
    private String MARKET_OVERVIEW;

    /** nullable persistent field */
    private String STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_MARKET_TREND";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_MARKET_TRENDVO(String PARAM_NO, Timestamp EFFECT_START_DATE, Timestamp EFFECT_END_DATE, Timestamp SUBMIT_DATE, String MARKET_OVERVIEW, String STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.PARAM_NO = PARAM_NO;
        this.EFFECT_START_DATE = EFFECT_START_DATE;
        this.EFFECT_END_DATE = EFFECT_END_DATE;
        this.SUBMIT_DATE = SUBMIT_DATE;
        this.MARKET_OVERVIEW = MARKET_OVERVIEW;
        this.STATUS = STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_MARKET_TRENDVO() {
    }

    /** minimal constructor */
    public TBFPS_MARKET_TRENDVO(String PARAM_NO) {
        this.PARAM_NO = PARAM_NO;
    }

    public String getPARAM_NO() {
        return this.PARAM_NO;
    }

    public void setPARAM_NO(String PARAM_NO) {
        this.PARAM_NO = PARAM_NO;
    }

    public Timestamp getEFFECT_START_DATE() {
        return this.EFFECT_START_DATE;
    }

    public void setEFFECT_START_DATE(Timestamp EFFECT_START_DATE) {
        this.EFFECT_START_DATE = EFFECT_START_DATE;
    }

    public Timestamp getEFFECT_END_DATE() {
        return this.EFFECT_END_DATE;
    }

    public void setEFFECT_END_DATE(Timestamp EFFECT_END_DATE) {
        this.EFFECT_END_DATE = EFFECT_END_DATE;
    }

    public Timestamp getSUBMIT_DATE() {
        return this.SUBMIT_DATE;
    }

    public void setSUBMIT_DATE(Timestamp SUBMIT_DATE) {
        this.SUBMIT_DATE = SUBMIT_DATE;
    }

    public String getMARKET_OVERVIEW() {
        return this.MARKET_OVERVIEW;
    }

    public void setMARKET_OVERVIEW(String MARKET_OVERVIEW) {
        this.MARKET_OVERVIEW = MARKET_OVERVIEW;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PARAM_NO", getPARAM_NO())
            .toString();
    }

}
