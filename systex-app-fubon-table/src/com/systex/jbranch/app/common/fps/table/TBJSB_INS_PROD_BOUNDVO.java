package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_BOUNDVO extends VOBase {

    /** identifier field */
    private TBJSB_INS_PROD_BOUNDPK comp_id;

    /** persistent field */
    private BigDecimal BOUNDRATE;

    /** nullable persistent field */
    private String ADD_1_START_MONTH;

    /** nullable persistent field */
    private String ADD_1_END_MONTH;

    /** nullable persistent field */
    private Float ADD_1_COMMISSIONRATE;

    /** nullable persistent field */
    private Float ADD_1_COMMRATEA;

    /** nullable persistent field */
    private String ADD_2_START_MONTH;

    /** nullable persistent field */
    private String ADD_2_END_MONTH;

    /** nullable persistent field */
    private Float ADD_2_COMMISSIONRATE;

    /** nullable persistent field */
    private Float ADD_2_COMMRATEA;

    /** nullable persistent field */
    private String ADD_3_START_MONTH;

    /** nullable persistent field */
    private String ADD_3_END_MONTH;

    /** nullable persistent field */
    private Float ADD_3_COMMISSIONRATE;

    /** nullable persistent field */
    private Float ADD_3_COMMRATEA;

    /** nullable persistent field */
    private Timestamp MAINT_DATE;

    /** nullable persistent field */
    private String MAINT_USER;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_BOUND";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBJSB_INS_PROD_BOUNDVO(TBJSB_INS_PROD_BOUNDPK comp_id, BigDecimal BOUNDRATE, String ADD_1_START_MONTH, String ADD_1_END_MONTH, Float ADD_1_COMMISSIONRATE, Float ADD_1_COMMRATEA, String ADD_2_START_MONTH, String ADD_2_END_MONTH, Float ADD_2_COMMISSIONRATE, Float ADD_2_COMMRATEA, String ADD_3_START_MONTH, String ADD_3_END_MONTH, Float ADD_3_COMMISSIONRATE, Float ADD_3_COMMRATEA, Timestamp MAINT_DATE, String MAINT_USER, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.BOUNDRATE = BOUNDRATE;
        this.ADD_1_START_MONTH = ADD_1_START_MONTH;
        this.ADD_1_END_MONTH = ADD_1_END_MONTH;
        this.ADD_1_COMMISSIONRATE = ADD_1_COMMISSIONRATE;
        this.ADD_1_COMMRATEA = ADD_1_COMMRATEA;
        this.ADD_2_START_MONTH = ADD_2_START_MONTH;
        this.ADD_2_END_MONTH = ADD_2_END_MONTH;
        this.ADD_2_COMMISSIONRATE = ADD_2_COMMISSIONRATE;
        this.ADD_2_COMMRATEA = ADD_2_COMMRATEA;
        this.ADD_3_START_MONTH = ADD_3_START_MONTH;
        this.ADD_3_END_MONTH = ADD_3_END_MONTH;
        this.ADD_3_COMMISSIONRATE = ADD_3_COMMISSIONRATE;
        this.ADD_3_COMMRATEA = ADD_3_COMMRATEA;
        this.MAINT_DATE = MAINT_DATE;
        this.MAINT_USER = MAINT_USER;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBJSB_INS_PROD_BOUNDVO() {
    }

    /** minimal constructor */
    public TBJSB_INS_PROD_BOUNDVO(TBJSB_INS_PROD_BOUNDPK comp_id, BigDecimal BOUNDRATE) {
        this.comp_id = comp_id;
        this.BOUNDRATE = BOUNDRATE;
    }

    public TBJSB_INS_PROD_BOUNDPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(TBJSB_INS_PROD_BOUNDPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getBOUNDRATE() {
        return this.BOUNDRATE;
    }

    public void setBOUNDRATE(BigDecimal BOUNDRATE) {
        this.BOUNDRATE = BOUNDRATE;
    }

    public String getADD_1_START_MONTH() {
        return this.ADD_1_START_MONTH;
    }

    public void setADD_1_START_MONTH(String ADD_1_START_MONTH) {
        this.ADD_1_START_MONTH = ADD_1_START_MONTH;
    }

    public String getADD_1_END_MONTH() {
        return this.ADD_1_END_MONTH;
    }

    public void setADD_1_END_MONTH(String ADD_1_END_MONTH) {
        this.ADD_1_END_MONTH = ADD_1_END_MONTH;
    }

    public Float getADD_1_COMMISSIONRATE() {
        return this.ADD_1_COMMISSIONRATE;
    }

    public void setADD_1_COMMISSIONRATE(Float ADD_1_COMMISSIONRATE) {
        this.ADD_1_COMMISSIONRATE = ADD_1_COMMISSIONRATE;
    }

    public Float getADD_1_COMMRATEA() {
        return this.ADD_1_COMMRATEA;
    }

    public void setADD_1_COMMRATEA(Float ADD_1_COMMRATEA) {
        this.ADD_1_COMMRATEA = ADD_1_COMMRATEA;
    }

    public String getADD_2_START_MONTH() {
        return this.ADD_2_START_MONTH;
    }

    public void setADD_2_START_MONTH(String ADD_2_START_MONTH) {
        this.ADD_2_START_MONTH = ADD_2_START_MONTH;
    }

    public String getADD_2_END_MONTH() {
        return this.ADD_2_END_MONTH;
    }

    public void setADD_2_END_MONTH(String ADD_2_END_MONTH) {
        this.ADD_2_END_MONTH = ADD_2_END_MONTH;
    }

    public Float getADD_2_COMMISSIONRATE() {
        return this.ADD_2_COMMISSIONRATE;
    }

    public void setADD_2_COMMISSIONRATE(Float ADD_2_COMMISSIONRATE) {
        this.ADD_2_COMMISSIONRATE = ADD_2_COMMISSIONRATE;
    }

    public Float getADD_2_COMMRATEA() {
        return this.ADD_2_COMMRATEA;
    }

    public void setADD_2_COMMRATEA(Float ADD_2_COMMRATEA) {
        this.ADD_2_COMMRATEA = ADD_2_COMMRATEA;
    }

    public String getADD_3_START_MONTH() {
        return this.ADD_3_START_MONTH;
    }

    public void setADD_3_START_MONTH(String ADD_3_START_MONTH) {
        this.ADD_3_START_MONTH = ADD_3_START_MONTH;
    }

    public String getADD_3_END_MONTH() {
        return this.ADD_3_END_MONTH;
    }

    public void setADD_3_END_MONTH(String ADD_3_END_MONTH) {
        this.ADD_3_END_MONTH = ADD_3_END_MONTH;
    }

    public Float getADD_3_COMMISSIONRATE() {
        return this.ADD_3_COMMISSIONRATE;
    }

    public void setADD_3_COMMISSIONRATE(Float ADD_3_COMMISSIONRATE) {
        this.ADD_3_COMMISSIONRATE = ADD_3_COMMISSIONRATE;
    }

    public Float getADD_3_COMMRATEA() {
        return this.ADD_3_COMMRATEA;
    }

    public void setADD_3_COMMRATEA(Float ADD_3_COMMRATEA) {
        this.ADD_3_COMMRATEA = ADD_3_COMMRATEA;
    }

    public Timestamp getMAINT_DATE() {
        return this.MAINT_DATE;
    }

    public void setMAINT_DATE(Timestamp MAINT_DATE) {
        this.MAINT_DATE = MAINT_DATE;
    }

    public String getMAINT_USER() {
        return this.MAINT_USER;
    }

    public void setMAINT_USER(String MAINT_USER) {
        this.MAINT_USER = MAINT_USER;
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
        if ( !(other instanceof TBJSB_INS_PROD_BOUNDVO) ) return false;
        TBJSB_INS_PROD_BOUNDVO castOther = (TBJSB_INS_PROD_BOUNDVO) other;
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
