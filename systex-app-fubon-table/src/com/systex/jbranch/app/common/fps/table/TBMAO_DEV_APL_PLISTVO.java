package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMAO_DEV_APL_PLISTVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private String DEV_NBR;

    /** nullable persistent field */
    private Timestamp USE_DATE;

    /** nullable persistent field */
    private String USE_PERIOD;

    /** nullable persistent field */
    private String APL_EMP_ID;

    /** nullable persistent field */
    private Timestamp APL_DATETIME;

    /** nullable persistent field */
    private String VISIT_CUST_LIST;

    /** nullable persistent field */
    private String DEV_STATUS;

    /** nullable persistent field */
    private Timestamp DEV_TAKE_DATETIME;

    /** nullable persistent field */
    private String DEV_TAKE_EMP_ID;

    /** nullable persistent field */
    private Timestamp DEV_RETURN_DATETIME;

    /** nullable persistent field */
    private String DEV_RETURN_EMP_ID;

    /** nullable persistent field */
    private String LETGO_EMP_ID;

    /** nullable persistent field */
    private String LETGO_YN;

    /** nullable persistent field */
    private Timestamp LETGO_DATETIME;

    /** nullable persistent field */
    private String USE_PERIOD_S_TIME;

    /** nullable persistent field */
    private String USE_PERIOD_E_TIME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMAO_DEV_APL_PLIST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMAO_DEV_APL_PLISTVO(String SEQ, String DEV_NBR, Timestamp USE_DATE, String USE_PERIOD, String APL_EMP_ID, Timestamp APL_DATETIME, String VISIT_CUST_LIST, String DEV_STATUS, Timestamp DEV_TAKE_DATETIME, String DEV_TAKE_EMP_ID, Timestamp DEV_RETURN_DATETIME, String DEV_RETURN_EMP_ID, String LETGO_EMP_ID, String LETGO_YN, Timestamp LETGO_DATETIME, String USE_PERIOD_S_TIME, String USE_PERIOD_E_TIME, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.DEV_NBR = DEV_NBR;
        this.USE_DATE = USE_DATE;
        this.USE_PERIOD = USE_PERIOD;
        this.APL_EMP_ID = APL_EMP_ID;
        this.APL_DATETIME = APL_DATETIME;
        this.VISIT_CUST_LIST = VISIT_CUST_LIST;
        this.DEV_STATUS = DEV_STATUS;
        this.DEV_TAKE_DATETIME = DEV_TAKE_DATETIME;
        this.DEV_TAKE_EMP_ID = DEV_TAKE_EMP_ID;
        this.DEV_RETURN_DATETIME = DEV_RETURN_DATETIME;
        this.DEV_RETURN_EMP_ID = DEV_RETURN_EMP_ID;
        this.LETGO_EMP_ID = LETGO_EMP_ID;
        this.LETGO_YN = LETGO_YN;
        this.LETGO_DATETIME = LETGO_DATETIME;
        this.USE_PERIOD_S_TIME = USE_PERIOD_S_TIME;
        this.USE_PERIOD_E_TIME = USE_PERIOD_E_TIME;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.DEV_NBR = DEV_NBR;
        this.version = version;
        this.version = version;
    }

    /** default constructor */
    public TBMAO_DEV_APL_PLISTVO() {
    }

    /** minimal constructor */
    public TBMAO_DEV_APL_PLISTVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getDEV_NBR() {
        return this.DEV_NBR;
    }

    public void setDEV_NBR(String DEV_NBR) {
        this.DEV_NBR = DEV_NBR;
    }

    public Timestamp getUSE_DATE() {
        return this.USE_DATE;
    }

    public void setUSE_DATE(Timestamp USE_DATE) {
        this.USE_DATE = USE_DATE;
    }

    public String getUSE_PERIOD() {
        return this.USE_PERIOD;
    }

    public void setUSE_PERIOD(String USE_PERIOD) {
        this.USE_PERIOD = USE_PERIOD;
    }

    public String getAPL_EMP_ID() {
        return this.APL_EMP_ID;
    }

    public void setAPL_EMP_ID(String APL_EMP_ID) {
        this.APL_EMP_ID = APL_EMP_ID;
    }

    public Timestamp getAPL_DATETIME() {
        return this.APL_DATETIME;
    }

    public void setAPL_DATETIME(Timestamp APL_DATETIME) {
        this.APL_DATETIME = APL_DATETIME;
    }

    public String getVISIT_CUST_LIST() {
        return this.VISIT_CUST_LIST;
    }

    public void setVISIT_CUST_LIST(String VISIT_CUST_LIST) {
        this.VISIT_CUST_LIST = VISIT_CUST_LIST;
    }

    public String getDEV_STATUS() {
        return this.DEV_STATUS;
    }

    public void setDEV_STATUS(String DEV_STATUS) {
        this.DEV_STATUS = DEV_STATUS;
    }

    public Timestamp getDEV_TAKE_DATETIME() {
        return this.DEV_TAKE_DATETIME;
    }

    public void setDEV_TAKE_DATETIME(Timestamp DEV_TAKE_DATETIME) {
        this.DEV_TAKE_DATETIME = DEV_TAKE_DATETIME;
    }

    public String getDEV_TAKE_EMP_ID() {
        return this.DEV_TAKE_EMP_ID;
    }

    public void setDEV_TAKE_EMP_ID(String DEV_TAKE_EMP_ID) {
        this.DEV_TAKE_EMP_ID = DEV_TAKE_EMP_ID;
    }

    public Timestamp getDEV_RETURN_DATETIME() {
        return this.DEV_RETURN_DATETIME;
    }

    public void setDEV_RETURN_DATETIME(Timestamp DEV_RETURN_DATETIME) {
        this.DEV_RETURN_DATETIME = DEV_RETURN_DATETIME;
    }

    public String getDEV_RETURN_EMP_ID() {
        return this.DEV_RETURN_EMP_ID;
    }

    public void setDEV_RETURN_EMP_ID(String DEV_RETURN_EMP_ID) {
        this.DEV_RETURN_EMP_ID = DEV_RETURN_EMP_ID;
    }

    public String getLETGO_EMP_ID() {
        return this.LETGO_EMP_ID;
    }

    public void setLETGO_EMP_ID(String LETGO_EMP_ID) {
        this.LETGO_EMP_ID = LETGO_EMP_ID;
    }

    public String getLETGO_YN() {
        return this.LETGO_YN;
    }

    public void setLETGO_YN(String LETGO_YN) {
        this.LETGO_YN = LETGO_YN;
    }

    public Timestamp getLETGO_DATETIME() {
        return this.LETGO_DATETIME;
    }

    public void setLETGO_DATETIME(Timestamp LETGO_DATETIME) {
        this.LETGO_DATETIME = LETGO_DATETIME;
    }

    public String getUSE_PERIOD_S_TIME() {
        return this.USE_PERIOD_S_TIME;
    }

    public void setUSE_PERIOD_S_TIME(String USE_PERIOD_S_TIME) {
        this.USE_PERIOD_S_TIME = USE_PERIOD_S_TIME;
    }

    public String getUSE_PERIOD_E_TIME() {
        return this.USE_PERIOD_E_TIME;
    }

    public void setUSE_PERIOD_E_TIME(String USE_PERIOD_E_TIME) {
        this.USE_PERIOD_E_TIME = USE_PERIOD_E_TIME;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
