package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PARA_HEADERVO extends VOBase {

    /** identifier field */
    private BigDecimal PARA_NO;

    /** nullable persistent field */
    private String PARA_TYPE;

    /** nullable persistent field */
    private Timestamp EFFECT_DATE;

    /** nullable persistent field */
    private Timestamp EXPIRY_DATE;

    /** nullable persistent field */
    private Timestamp SUBMIT_DATE;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private String CAL_DESC;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_PARA_HEADER";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_PARA_HEADERVO(BigDecimal PARA_NO, String PARA_TYPE, Timestamp EFFECT_DATE, Timestamp EXPIRY_DATE, Timestamp SUBMIT_DATE, String STATUS, String CAL_DESC, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.PARA_NO = PARA_NO;
        this.PARA_TYPE = PARA_TYPE;
        this.EFFECT_DATE = EFFECT_DATE;
        this.EXPIRY_DATE = EXPIRY_DATE;
        this.SUBMIT_DATE = SUBMIT_DATE;
        this.STATUS = STATUS;
        this.CAL_DESC = CAL_DESC;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_PARA_HEADERVO() {
    }

    /** minimal constructor */
    public TBINS_PARA_HEADERVO(BigDecimal PARA_NO) {
        this.PARA_NO = PARA_NO;
    }

    public BigDecimal getPARA_NO() {
        return this.PARA_NO;
    }

    public void setPARA_NO(BigDecimal PARA_NO) {
        this.PARA_NO = PARA_NO;
    }

    public String getPARA_TYPE() {
        return this.PARA_TYPE;
    }

    public void setPARA_TYPE(String PARA_TYPE) {
        this.PARA_TYPE = PARA_TYPE;
    }

    public Timestamp getEFFECT_DATE() {
        return this.EFFECT_DATE;
    }

    public void setEFFECT_DATE(Timestamp EFFECT_DATE) {
        this.EFFECT_DATE = EFFECT_DATE;
    }

    public Timestamp getEXPIRY_DATE() {
        return this.EXPIRY_DATE;
    }

    public void setEXPIRY_DATE(Timestamp EXPIRY_DATE) {
        this.EXPIRY_DATE = EXPIRY_DATE;
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

    public String getCAL_DESC() {
        return this.CAL_DESC;
    }

    public void setCAL_DESC(String CAL_DESC) {
        this.CAL_DESC = CAL_DESC;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PARA_NO", getPARA_NO())
            .toString();
    }

}
