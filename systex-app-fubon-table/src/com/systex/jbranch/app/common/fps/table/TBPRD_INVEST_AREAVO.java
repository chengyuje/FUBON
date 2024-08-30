package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INVEST_AREAVO extends VOBase {

    /** identifier field */
    private String GLOBAL_ID;

    /** nullable persistent field */
    private String MKT_TIER3;

    /** nullable persistent field */
    private String MKT_TIER2;

    /** nullable persistent field */
    private String MKT_TIER1;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INVEST_AREA";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INVEST_AREAVO(String GLOBAL_ID, String MKT_TIER3, String MKT_TIER2, String MKT_TIER1, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.GLOBAL_ID = GLOBAL_ID;
        this.MKT_TIER3 = MKT_TIER3;
        this.MKT_TIER2 = MKT_TIER2;
        this.MKT_TIER1 = MKT_TIER1;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INVEST_AREAVO() {
    }

    /** minimal constructor */
    public TBPRD_INVEST_AREAVO(String GLOBAL_ID) {
        this.GLOBAL_ID = GLOBAL_ID;
    }

    public String getGLOBAL_ID() {
        return this.GLOBAL_ID;
    }

    public void setGLOBAL_ID(String GLOBAL_ID) {
        this.GLOBAL_ID = GLOBAL_ID;
    }

    public String getMKT_TIER3() {
        return this.MKT_TIER3;
    }

    public void setMKT_TIER3(String MKT_TIER3) {
        this.MKT_TIER3 = MKT_TIER3;
    }

    public String getMKT_TIER2() {
        return this.MKT_TIER2;
    }

    public void setMKT_TIER2(String MKT_TIER2) {
        this.MKT_TIER2 = MKT_TIER2;
    }

    public String getMKT_TIER1() {
        return this.MKT_TIER1;
    }

    public void setMKT_TIER1(String MKT_TIER1) {
        this.MKT_TIER1 = MKT_TIER1;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("GLOBAL_ID", getGLOBAL_ID())
            .toString();
    }

}
