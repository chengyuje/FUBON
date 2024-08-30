package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSFPSEJVO extends VOBase {

    /** identifier field */
    private Long RECID;

    /** persistent field */
    private Timestamp TXNDATETIME;

    /** nullable persistent field */
    private String BRCHID;

    /** persistent field */
    private String WSID;

    /** nullable persistent field */
    private String TXNCODE;

    /** nullable persistent field */
    private String TELLERID;

    /** nullable persistent field */
    private String DEPID;

    /** nullable persistent field */
    private String ROLEID;

    /** nullable persistent field */
    private String CUSTOMERID;

    /** nullable persistent field */
    private String CUSTOMERNAME;

    /** nullable persistent field */
    private String BIZCODENAME;

    /** nullable persistent field */
    private String MEMO;

    /** nullable persistent field */
    private String RESULTDATA;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSFPSEJ";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSFPSEJVO(Timestamp TXNDATETIME, String BRCHID, String WSID, String TXNCODE, String TELLERID, String DEPID, String ROLEID, String CUSTOMERID, String CUSTOMERNAME, String BIZCODENAME, String MEMO, String RESULTDATA, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.TXNDATETIME = TXNDATETIME;
        this.BRCHID = BRCHID;
        this.WSID = WSID;
        this.TXNCODE = TXNCODE;
        this.TELLERID = TELLERID;
        this.DEPID = DEPID;
        this.ROLEID = ROLEID;
        this.CUSTOMERID = CUSTOMERID;
        this.CUSTOMERNAME = CUSTOMERNAME;
        this.BIZCODENAME = BIZCODENAME;
        this.MEMO = MEMO;
        this.RESULTDATA = RESULTDATA;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSFPSEJVO() {
    }

    /** minimal constructor */
    public TBSYSFPSEJVO(Timestamp TXNDATETIME, String WSID) {
        this.TXNDATETIME = TXNDATETIME;
        this.WSID = WSID;
    }

    public Long getRECID() {
        return this.RECID;
    }

    public void setRECID(Long RECID) {
        this.RECID = RECID;
    }

    public Timestamp getTXNDATETIME() {
        return this.TXNDATETIME;
    }

    public void setTXNDATETIME(Timestamp TXNDATETIME) {
        this.TXNDATETIME = TXNDATETIME;
    }

    public String getBRCHID() {
        return this.BRCHID;
    }

    public void setBRCHID(String BRCHID) {
        this.BRCHID = BRCHID;
    }

    public String getWSID() {
        return this.WSID;
    }

    public void setWSID(String WSID) {
        this.WSID = WSID;
    }

    public String getTXNCODE() {
        return this.TXNCODE;
    }

    public void setTXNCODE(String TXNCODE) {
        this.TXNCODE = TXNCODE;
    }

    public String getTELLERID() {
        return this.TELLERID;
    }

    public void setTELLERID(String TELLERID) {
        this.TELLERID = TELLERID;
    }

    public String getDEPID() {
        return this.DEPID;
    }

    public void setDEPID(String DEPID) {
        this.DEPID = DEPID;
    }

    public String getROLEID() {
        return this.ROLEID;
    }

    public void setROLEID(String ROLEID) {
        this.ROLEID = ROLEID;
    }

    public String getCUSTOMERID() {
        return this.CUSTOMERID;
    }

    public void setCUSTOMERID(String CUSTOMERID) {
        this.CUSTOMERID = CUSTOMERID;
    }

    public String getCUSTOMERNAME() {
        return this.CUSTOMERNAME;
    }

    public void setCUSTOMERNAME(String CUSTOMERNAME) {
        this.CUSTOMERNAME = CUSTOMERNAME;
    }

    public String getBIZCODENAME() {
        return this.BIZCODENAME;
    }

    public void setBIZCODENAME(String BIZCODENAME) {
        this.BIZCODENAME = BIZCODENAME;
    }

    public String getMEMO() {
        return this.MEMO;
    }

    public void setMEMO(String MEMO) {
        this.MEMO = MEMO;
    }

    public String getRESULTDATA() {
        return this.RESULTDATA;
    }

    public void setRESULTDATA(String RESULTDATA) {
        this.RESULTDATA = RESULTDATA;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("RECID", getRECID())
            .toString();
    }

}
