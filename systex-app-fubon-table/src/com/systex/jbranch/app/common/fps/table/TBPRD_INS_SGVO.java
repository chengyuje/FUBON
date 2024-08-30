package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_SGVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPRD_INS_SGPK comp_id;

    /** persistent field */
    private BigDecimal GUARANTEE_ANNUAL;

    /** persistent field */
    private String PRD_UNIT;

    /** persistent field */
    private String CURR_CD;

    /** nullable persistent field */
    private String IS_LIFELONG;

    /** persistent field */
    private Timestamp SALE_SDATE;

    /** nullable persistent field */
    private Timestamp SALE_EDATE;

    /** nullable persistent field */
    private String IS_ANNUITY;

    /** nullable persistent field */
    private String IS_INCREASING;

    /** nullable persistent field */
    private String IS_REPAY;

    /** nullable persistent field */
    private String IS_RATE_CHANGE;

    /** nullable persistent field */
    private String OBU_BUY;

    /** persistent field */
    private String NO_VALUE_YEAR;

    /** persistent field */
    private String MAIN_RIDER;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_SG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_SGVO(com.systex.jbranch.app.common.fps.table.TBPRD_INS_SGPK comp_id, BigDecimal GUARANTEE_ANNUAL, String PRD_UNIT, String CURR_CD, String IS_LIFELONG, Timestamp SALE_SDATE, Timestamp SALE_EDATE, String IS_ANNUITY, String IS_INCREASING, String IS_REPAY, String IS_RATE_CHANGE, String OBU_BUY, String NO_VALUE_YEAR, String MAIN_RIDER, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.GUARANTEE_ANNUAL = GUARANTEE_ANNUAL;
        this.PRD_UNIT = PRD_UNIT;
        this.CURR_CD = CURR_CD;
        this.IS_LIFELONG = IS_LIFELONG;
        this.SALE_SDATE = SALE_SDATE;
        this.SALE_EDATE = SALE_EDATE;
        this.IS_ANNUITY = IS_ANNUITY;
        this.IS_INCREASING = IS_INCREASING;
        this.IS_REPAY = IS_REPAY;
        this.IS_RATE_CHANGE = IS_RATE_CHANGE;
        this.OBU_BUY = OBU_BUY;
        this.NO_VALUE_YEAR = NO_VALUE_YEAR;
        this.MAIN_RIDER = MAIN_RIDER;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_SGVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_SGVO(com.systex.jbranch.app.common.fps.table.TBPRD_INS_SGPK comp_id, BigDecimal GUARANTEE_ANNUAL, String PRD_UNIT, String CURR_CD, Timestamp SALE_SDATE, String NO_VALUE_YEAR, String MAIN_RIDER) {
        this.comp_id = comp_id;
        this.GUARANTEE_ANNUAL = GUARANTEE_ANNUAL;
        this.PRD_UNIT = PRD_UNIT;
        this.CURR_CD = CURR_CD;
        this.SALE_SDATE = SALE_SDATE;
        this.NO_VALUE_YEAR = NO_VALUE_YEAR;
        this.MAIN_RIDER = MAIN_RIDER;
    }

    public com.systex.jbranch.app.common.fps.table.TBPRD_INS_SGPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPRD_INS_SGPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getGUARANTEE_ANNUAL() {
        return this.GUARANTEE_ANNUAL;
    }

    public void setGUARANTEE_ANNUAL(BigDecimal GUARANTEE_ANNUAL) {
        this.GUARANTEE_ANNUAL = GUARANTEE_ANNUAL;
    }

    public String getPRD_UNIT() {
        return this.PRD_UNIT;
    }

    public void setPRD_UNIT(String PRD_UNIT) {
        this.PRD_UNIT = PRD_UNIT;
    }

    public String getCURR_CD() {
        return this.CURR_CD;
    }

    public void setCURR_CD(String CURR_CD) {
        this.CURR_CD = CURR_CD;
    }

    public String getIS_LIFELONG() {
        return this.IS_LIFELONG;
    }

    public void setIS_LIFELONG(String IS_LIFELONG) {
        this.IS_LIFELONG = IS_LIFELONG;
    }

    public Timestamp getSALE_SDATE() {
        return this.SALE_SDATE;
    }

    public void setSALE_SDATE(Timestamp SALE_SDATE) {
        this.SALE_SDATE = SALE_SDATE;
    }

    public Timestamp getSALE_EDATE() {
        return this.SALE_EDATE;
    }

    public void setSALE_EDATE(Timestamp SALE_EDATE) {
        this.SALE_EDATE = SALE_EDATE;
    }

    public String getIS_ANNUITY() {
        return this.IS_ANNUITY;
    }

    public void setIS_ANNUITY(String IS_ANNUITY) {
        this.IS_ANNUITY = IS_ANNUITY;
    }

    public String getIS_INCREASING() {
        return this.IS_INCREASING;
    }

    public void setIS_INCREASING(String IS_INCREASING) {
        this.IS_INCREASING = IS_INCREASING;
    }

    public String getIS_REPAY() {
        return this.IS_REPAY;
    }

    public void setIS_REPAY(String IS_REPAY) {
        this.IS_REPAY = IS_REPAY;
    }

    public String getIS_RATE_CHANGE() {
        return this.IS_RATE_CHANGE;
    }

    public void setIS_RATE_CHANGE(String IS_RATE_CHANGE) {
        this.IS_RATE_CHANGE = IS_RATE_CHANGE;
    }

    public String getOBU_BUY() {
        return this.OBU_BUY;
    }

    public void setOBU_BUY(String OBU_BUY) {
        this.OBU_BUY = OBU_BUY;
    }

    public String getNO_VALUE_YEAR() {
        return this.NO_VALUE_YEAR;
    }

    public void setNO_VALUE_YEAR(String NO_VALUE_YEAR) {
        this.NO_VALUE_YEAR = NO_VALUE_YEAR;
    }

    public String getMAIN_RIDER() {
        return this.MAIN_RIDER;
    }

    public void setMAIN_RIDER(String MAIN_RIDER) {
        this.MAIN_RIDER = MAIN_RIDER;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_INS_SGVO) ) return false;
        TBPRD_INS_SGVO castOther = (TBPRD_INS_SGVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
