package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_COMPAREDVO extends VOBase {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** persistent field */
    private String INSDATA_KEYNO;

    /** nullable persistent field */
    private String IGNORE_Y;

    /** nullable persistent field */
    private String IGNORE_A;

    /** nullable persistent field */
    private String REASON;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_COMPARED";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_COMPAREDVO(BigDecimal KEY_NO, String INSDATA_KEYNO, String IGNORE_Y, String IGNORE_A, String REASON, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.KEY_NO = KEY_NO;
        this.INSDATA_KEYNO = INSDATA_KEYNO;
        this.IGNORE_Y = IGNORE_Y;
        this.IGNORE_A = IGNORE_A;
        this.REASON = REASON;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_COMPAREDVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_COMPAREDVO(BigDecimal KEY_NO, String INSDATA_KEYNO, Timestamp createtime, String creator) {
        this.KEY_NO = KEY_NO;
        this.INSDATA_KEYNO = INSDATA_KEYNO;
        this.createtime = createtime;
        this.creator = creator;
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public String getINSDATA_KEYNO() {
        return this.INSDATA_KEYNO;
    }

    public void setINSDATA_KEYNO(String INSDATA_KEYNO) {
        this.INSDATA_KEYNO = INSDATA_KEYNO;
    }

    public String getIGNORE_Y() {
        return this.IGNORE_Y;
    }

    public void setIGNORE_Y(String IGNORE_Y) {
        this.IGNORE_Y = IGNORE_Y;
    }

    public String getIGNORE_A() {
        return this.IGNORE_A;
    }

    public void setIGNORE_A(String IGNORE_A) {
        this.IGNORE_A = IGNORE_A;
    }

    public String getREASON() {
        return this.REASON;
    }

    public void setREASON(String REASON) {
        this.REASON = REASON;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO", getKEY_NO())
            .toString();
    }

}
