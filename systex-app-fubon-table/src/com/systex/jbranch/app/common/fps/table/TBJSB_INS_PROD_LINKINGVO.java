package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_LINKINGVO extends VOBase {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** nullable persistent field */
    private BigDecimal INSURANCECO;

    /** nullable persistent field */
    private String PRODUCTID;

    /** nullable persistent field */
    private String E_FIELDNAME;

    /** nullable persistent field */
    private String CURR_TYPE;

    /** nullable persistent field */
    private String PREMIUMTABLE;

    /** nullable persistent field */
    private String TARGET_CODE;

    /** nullable persistent field */
    private String TARGET_NAME;

    /** nullable persistent field */
    private String TRANSFER_FLG;

    /** nullable persistent field */
    private String TARGET_CURR;

    /** nullable persistent field */
    private String TARGET_RISK;

    /** nullable persistent field */
    private String INT_TYPE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_LINKING";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBJSB_INS_PROD_LINKINGVO(BigDecimal KEY_NO, BigDecimal INSURANCECO, String PRODUCTID, String E_FIELDNAME, String CURR_TYPE, String PREMIUMTABLE, String TARGET_CODE, String TARGET_NAME, String TRANSFER_FLG, String TARGET_CURR, String TARGET_RISK, String INT_TYPE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.KEY_NO = KEY_NO;
        this.INSURANCECO = INSURANCECO;
        this.PRODUCTID = PRODUCTID;
        this.E_FIELDNAME = E_FIELDNAME;
        this.CURR_TYPE = CURR_TYPE;
        this.PREMIUMTABLE = PREMIUMTABLE;
        this.TARGET_CODE = TARGET_CODE;
        this.TARGET_NAME = TARGET_NAME;
        this.TRANSFER_FLG = TRANSFER_FLG;
        this.TARGET_CURR = TARGET_CURR;
        this.TARGET_RISK = TARGET_RISK;
        this.INT_TYPE = INT_TYPE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBJSB_INS_PROD_LINKINGVO() {
    }

    /** minimal constructor */
    public TBJSB_INS_PROD_LINKINGVO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public BigDecimal getINSURANCECO() {
        return this.INSURANCECO;
    }

    public void setINSURANCECO(BigDecimal INSURANCECO) {
        this.INSURANCECO = INSURANCECO;
    }

    public String getPRODUCTID() {
        return this.PRODUCTID;
    }

    public void setPRODUCTID(String PRODUCTID) {
        this.PRODUCTID = PRODUCTID;
    }

    public String getE_FIELDNAME() {
        return this.E_FIELDNAME;
    }

    public void setE_FIELDNAME(String E_FIELDNAME) {
        this.E_FIELDNAME = E_FIELDNAME;
    }

    public String getCURR_TYPE() {
        return this.CURR_TYPE;
    }

    public void setCURR_TYPE(String CURR_TYPE) {
        this.CURR_TYPE = CURR_TYPE;
    }

    public String getPREMIUMTABLE() {
        return this.PREMIUMTABLE;
    }

    public void setPREMIUMTABLE(String PREMIUMTABLE) {
        this.PREMIUMTABLE = PREMIUMTABLE;
    }

    public String getTARGET_CODE() {
        return this.TARGET_CODE;
    }

    public void setTARGET_CODE(String TARGET_CODE) {
        this.TARGET_CODE = TARGET_CODE;
    }

    public String getTARGET_NAME() {
        return this.TARGET_NAME;
    }

    public void setTARGET_NAME(String TARGET_NAME) {
        this.TARGET_NAME = TARGET_NAME;
    }

    public String getTRANSFER_FLG() {
        return this.TRANSFER_FLG;
    }

    public void setTRANSFER_FLG(String TRANSFER_FLG) {
        this.TRANSFER_FLG = TRANSFER_FLG;
    }

    public String getTARGET_CURR() {
        return this.TARGET_CURR;
    }

    public void setTARGET_CURR(String TARGET_CURR) {
        this.TARGET_CURR = TARGET_CURR;
    }

    public String getTARGET_RISK() {
        return this.TARGET_RISK;
    }

    public void setTARGET_RISK(String TARGET_RISK) {
        this.TARGET_RISK = TARGET_RISK;
    }

    public String getINT_TYPE() {
        return this.INT_TYPE;
    }

    public void setINT_TYPE(String INT_TYPE) {
        this.INT_TYPE = INT_TYPE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO", getKEY_NO())
            .toString();
    }

}
