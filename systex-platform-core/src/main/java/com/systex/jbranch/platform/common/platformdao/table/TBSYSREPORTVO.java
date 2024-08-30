package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSREPORTVO extends VOBase {

    /** identifier field */
    private Long GEN_ID;

    /** nullable persistent field */
    private String TEMP_PATH;

    /** persistent field */
    private String STATUS;

    /** persistent field */
    private String REQUEST_HOST;

    /** nullable persistent field */
    private BigDecimal GEN_TIME;

    /** nullable persistent field */
    private String MESSAGE;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSREPORTVO(String TEMP_PATH, String STATUS, String REQUEST_HOST, BigDecimal GEN_TIME, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, String MESSAGE, Long version) {
        this.TEMP_PATH = TEMP_PATH;
        this.STATUS = STATUS;
        this.REQUEST_HOST = REQUEST_HOST;
        this.GEN_TIME = GEN_TIME;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.MESSAGE = MESSAGE;
        this.version = version;
    }

    /** default constructor */
    public TBSYSREPORTVO() {
    }

    /** minimal constructor */
    public TBSYSREPORTVO(String STATUS, String REQUEST_HOST) {
        this.STATUS = STATUS;
        this.REQUEST_HOST = REQUEST_HOST;
    }

    public Long getGEN_ID() {
        return this.GEN_ID;
    }

    public void setGEN_ID(Long GEN_ID) {
        this.GEN_ID = GEN_ID;
    }

    public String getTEMP_PATH() {
        return this.TEMP_PATH;
    }

    public void setTEMP_PATH(String TEMP_PATH) {
        this.TEMP_PATH = TEMP_PATH;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getREQUEST_HOST() {
        return this.REQUEST_HOST;
    }

    public void setREQUEST_HOST(String REQUEST_HOST) {
        this.REQUEST_HOST = REQUEST_HOST;
    }

    public BigDecimal getGEN_TIME() {
        return this.GEN_TIME;
    }

    public void setGEN_TIME(BigDecimal GEN_TIME) {
        this.GEN_TIME = GEN_TIME;
    }

    public String getMESSAGE() {
        return this.MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("GEN_ID", getGEN_ID())
            .toString();
    }

}
