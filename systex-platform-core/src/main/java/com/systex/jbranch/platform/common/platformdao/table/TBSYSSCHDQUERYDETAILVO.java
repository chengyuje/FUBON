package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSSCHDQUERYDETAILVO extends VOBase {

    /** identifier field */
    private Long SEQID;

    /** persistent field */
    private Long QUERYID;

    /** persistent field */
    private Long AUDITID;

    /** nullable persistent field */
    private Timestamp PRE_FIRETIME;

    /** persistent field */
    private Timestamp FIRETIME;

    /** nullable persistent field */
    private Timestamp NEXT_FIRETIME;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSSCHDQUERYDETAIL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSSCHDQUERYDETAILVO(Long QUERYID, Long AUDITID, Timestamp PRE_FIRETIME, Timestamp FIRETIME, Timestamp NEXT_FIRETIME, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.QUERYID = QUERYID;
        this.AUDITID = AUDITID;
        this.PRE_FIRETIME = PRE_FIRETIME;
        this.FIRETIME = FIRETIME;
        this.NEXT_FIRETIME = NEXT_FIRETIME;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSSCHDQUERYDETAILVO() {
    }

    /** minimal constructor */
    public TBSYSSCHDQUERYDETAILVO(Long QUERYID, Long AUDITID, Timestamp FIRETIME) {
        this.QUERYID = QUERYID;
        this.AUDITID = AUDITID;
        this.FIRETIME = FIRETIME;
    }

    public Long getSEQID() {
        return this.SEQID;
    }

    public void setSEQID(Long SEQID) {
        this.SEQID = SEQID;
    }

    public Long getQUERYID() {
        return this.QUERYID;
    }

    public void setQUERYID(Long QUERYID) {
        this.QUERYID = QUERYID;
    }

    public Long getAUDITID() {
        return this.AUDITID;
    }

    public void setAUDITID(Long AUDITID) {
        this.AUDITID = AUDITID;
    }

    public Timestamp getPRE_FIRETIME() {
        return this.PRE_FIRETIME;
    }

    public void setPRE_FIRETIME(Timestamp PRE_FIRETIME) {
        this.PRE_FIRETIME = PRE_FIRETIME;
    }

    public Timestamp getFIRETIME() {
        return this.FIRETIME;
    }

    public void setFIRETIME(Timestamp FIRETIME) {
        this.FIRETIME = FIRETIME;
    }

    public Timestamp getNEXT_FIRETIME() {
        return this.NEXT_FIRETIME;
    }

    public void setNEXT_FIRETIME(Timestamp NEXT_FIRETIME) {
        this.NEXT_FIRETIME = NEXT_FIRETIME;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQID", getSEQID())
            .toString();
    }

}
