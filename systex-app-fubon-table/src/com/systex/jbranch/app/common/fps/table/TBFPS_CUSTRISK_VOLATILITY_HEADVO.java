package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_CUSTRISK_VOLATILITY_HEADVO extends VOBase {

    /** identifier field */
    private String PARAM_NO;

    /** persistent field */
    private Timestamp EFFECT_START_DATE;

    /** nullable persistent field */
    private Timestamp EFFECT_END_DATE;

    /** nullable persistent field */
    private Timestamp SUBMIT_DATE;

    /** persistent field */
    private String STATUS;

    /** nullable persistent field */
    private String ALERT_TYPE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITY_HEAD";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_CUSTRISK_VOLATILITY_HEADVO(String PARAM_NO, Timestamp EFFECT_START_DATE, Timestamp EFFECT_END_DATE, Timestamp SUBMIT_DATE, String STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String ALERT_TYPE, Long version) {
        this.PARAM_NO = PARAM_NO;
        this.EFFECT_START_DATE = EFFECT_START_DATE;
        this.EFFECT_END_DATE = EFFECT_END_DATE;
        this.SUBMIT_DATE = SUBMIT_DATE;
        this.STATUS = STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.ALERT_TYPE = ALERT_TYPE;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_CUSTRISK_VOLATILITY_HEADVO() {
    }

    /** minimal constructor */
    public TBFPS_CUSTRISK_VOLATILITY_HEADVO(String PARAM_NO, Timestamp EFFECT_START_DATE, String STATUS) {
        this.PARAM_NO = PARAM_NO;
        this.EFFECT_START_DATE = EFFECT_START_DATE;
        this.STATUS = STATUS;
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

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getALERT_TYPE() {
        return this.ALERT_TYPE;
    }

    public void setALERT_TYPE(String ALERT_TYPE) {
        this.ALERT_TYPE = ALERT_TYPE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PARAM_NO", getPARAM_NO())
            .toString();
    }

}
