package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_ENDPOINT_TRACEVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String TELLERID;

    /** nullable persistent field */
    private String WSID;

    /** nullable persistent field */
    private String BEAN;

    /** nullable persistent field */
    private String METHOD;

    /** nullable persistent field */
    private Timestamp S_TIME;

    /** nullable persistent field */
    private Timestamp E_TIME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_ENDPOINT_TRACE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_ENDPOINT_TRACEVO(BigDecimal SEQ, String TELLERID, String WSID, String BEAN, String METHOD, Timestamp S_TIME, Timestamp E_TIME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.TELLERID = TELLERID;
        this.WSID = WSID;
        this.BEAN = BEAN;
        this.METHOD = METHOD;
        this.S_TIME = S_TIME;
        this.E_TIME = E_TIME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_ENDPOINT_TRACEVO() {
    }

    /** minimal constructor */
    public TBSYS_ENDPOINT_TRACEVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getTELLERID() {
        return this.TELLERID;
    }

    public void setTELLERID(String TELLERID) {
        this.TELLERID = TELLERID;
    }

    public String getWSID() {
        return this.WSID;
    }

    public void setWSID(String WSID) {
        this.WSID = WSID;
    }

    public String getBEAN() {
        return this.BEAN;
    }

    public void setBEAN(String BEAN) {
        this.BEAN = BEAN;
    }

    public String getMETHOD() {
        return this.METHOD;
    }

    public void setMETHOD(String METHOD) {
        this.METHOD = METHOD;
    }

    public Timestamp getS_TIME() {
        return this.S_TIME;
    }

    public void setS_TIME(Timestamp S_TIME) {
        this.S_TIME = S_TIME;
    }

    public Timestamp getE_TIME() {
        return this.E_TIME;
    }

    public void setE_TIME(Timestamp E_TIME) {
        this.E_TIME = E_TIME;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
