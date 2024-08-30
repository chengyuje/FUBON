package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSFA_ORG_DEFNVO extends VOBase {

    /** identifier field */
    private String TERRITORY_ID;

    /** nullable persistent field */
    private String DESCR;

    /** nullable persistent field */
    private String BRCH_CLS;

    /** nullable persistent field */
    private Short LEVEL_ID;

    /** nullable persistent field */
    private String ORG_SEQ;

    /** nullable persistent field */
    private String PAR_TERRITORY_ID;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSFA_ORG_DEFN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSFA_ORG_DEFNVO(String TERRITORY_ID, String DESCR, String BRCH_CLS, Short LEVEL_ID, String ORG_SEQ, String PAR_TERRITORY_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.TERRITORY_ID = TERRITORY_ID;
        this.DESCR = DESCR;
        this.BRCH_CLS = BRCH_CLS;
        this.LEVEL_ID = LEVEL_ID;
        this.ORG_SEQ = ORG_SEQ;
        this.PAR_TERRITORY_ID = PAR_TERRITORY_ID;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSFA_ORG_DEFNVO() {
    }

    /** minimal constructor */
    public TBSFA_ORG_DEFNVO(String TERRITORY_ID) {
        this.TERRITORY_ID = TERRITORY_ID;
    }

    public String getTERRITORY_ID() {
        return this.TERRITORY_ID;
    }

    public void setTERRITORY_ID(String TERRITORY_ID) {
        this.TERRITORY_ID = TERRITORY_ID;
    }

    public String getDESCR() {
        return this.DESCR;
    }

    public void setDESCR(String DESCR) {
        this.DESCR = DESCR;
    }

    public String getBRCH_CLS() {
        return this.BRCH_CLS;
    }

    public void setBRCH_CLS(String BRCH_CLS) {
        this.BRCH_CLS = BRCH_CLS;
    }

    public Short getLEVEL_ID() {
        return this.LEVEL_ID;
    }

    public void setLEVEL_ID(Short LEVEL_ID) {
        this.LEVEL_ID = LEVEL_ID;
    }

    public String getORG_SEQ() {
        return this.ORG_SEQ;
    }

    public void setORG_SEQ(String ORG_SEQ) {
        this.ORG_SEQ = ORG_SEQ;
    }

    public String getPAR_TERRITORY_ID() {
        return this.PAR_TERRITORY_ID;
    }

    public void setPAR_TERRITORY_ID(String PAR_TERRITORY_ID) {
        this.PAR_TERRITORY_ID = PAR_TERRITORY_ID;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("TERRITORY_ID", getTERRITORY_ID())
            .toString();
    }

}
