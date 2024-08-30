package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_INS_ACCEPT_TIMEVO extends VOBase {

    /** identifier field */
    private String YEARMON;

    /** nullable persistent field */
    private String YEARS;

    /** nullable persistent field */
    private Timestamp INS_PROFIT_S;

    /** nullable persistent field */
    private Timestamp INS_PROFIT_E;

    /** nullable persistent field */
    private Timestamp ACCEPT_TIME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_INS_ACCEPT_TIME";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_INS_ACCEPT_TIMEVO(String YEARMON, String YEARS, Timestamp INS_PROFIT_S, Timestamp INS_PROFIT_E, Timestamp ACCEPT_TIME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.YEARMON = YEARMON;
        this.YEARS = YEARS;
        this.INS_PROFIT_S = INS_PROFIT_S;
        this.INS_PROFIT_E = INS_PROFIT_E;
        this.ACCEPT_TIME = ACCEPT_TIME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_INS_ACCEPT_TIMEVO() {
    }

    /** minimal constructor */
    public TBPMS_INS_ACCEPT_TIMEVO(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String getYEARMON() {
        return this.YEARMON;
    }

    public void setYEARMON(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String getYEARS() {
        return this.YEARS;
    }

    public void setYEARS(String YEARS) {
        this.YEARS = YEARS;
    }

    public Timestamp getINS_PROFIT_S() {
        return this.INS_PROFIT_S;
    }

    public void setINS_PROFIT_S(Timestamp INS_PROFIT_S) {
        this.INS_PROFIT_S = INS_PROFIT_S;
    }

    public Timestamp getINS_PROFIT_E() {
        return this.INS_PROFIT_E;
    }

    public void setINS_PROFIT_E(Timestamp INS_PROFIT_E) {
        this.INS_PROFIT_E = INS_PROFIT_E;
    }

    public Timestamp getACCEPT_TIME() {
        return this.ACCEPT_TIME;
    }

    public void setACCEPT_TIME(Timestamp ACCEPT_TIME) {
        this.ACCEPT_TIME = ACCEPT_TIME;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("YEARMON", getYEARMON())
            .toString();
    }

}
