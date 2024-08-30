package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TbsysejPK implements Serializable {

    /** identifier field */
    private String txndate;

    /** identifier field */
    private String brchid;

    /** identifier field */
    private String wsid;

    /** identifier field */
    private String sectionid;

    /** identifier field */
    private Short recno;

    /** full constructor */
    public TbsysejPK(String txndate, String brchid, String wsid, String sectionid, Short recno) {
        this.txndate = txndate;
        this.brchid = brchid;
        this.wsid = wsid;
        this.sectionid = sectionid;
        this.recno = recno;
    }

    /** default constructor */
    public TbsysejPK() {
    }

    public String getTxndate() {
        return this.txndate;
    }

    public void setTxndate(String txndate) {
        this.txndate = txndate;
    }

    public String getBrchid() {
        return this.brchid;
    }

    public void setBrchid(String brchid) {
        this.brchid = brchid;
    }

    public String getWsid() {
        return this.wsid;
    }

    public void setWsid(String wsid) {
        this.wsid = wsid;
    }

    public String getSectionid() {
        return this.sectionid;
    }

    public void setSectionid(String sectionid) {
        this.sectionid = sectionid;
    }

    public Short getRecno() {
        return this.recno;
    }

    public void setRecno(Short recno) {
        this.recno = recno;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("txndate", getTxndate())
            .append("brchid", getBrchid())
            .append("wsid", getWsid())
            .append("sectionid", getSectionid())
            .append("recno", getRecno())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysejPK) ) return false;
        TbsysejPK castOther = (TbsysejPK) other;
        return new EqualsBuilder()
            .append(this.getTxndate(), castOther.getTxndate())
            .append(this.getBrchid(), castOther.getBrchid())
            .append(this.getWsid(), castOther.getWsid())
            .append(this.getSectionid(), castOther.getSectionid())
            .append(this.getRecno(), castOther.getRecno())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTxndate())
            .append(getBrchid())
            .append(getWsid())
            .append(getSectionid())
            .append(getRecno())
            .toHashCode();
    }

}
