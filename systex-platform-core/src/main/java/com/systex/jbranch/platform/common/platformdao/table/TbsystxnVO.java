package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsystxnVO extends VOBase {

    /** identifier field */
    private String txncode;

    /** persistent field */
    private String txnname;

    /** persistent field */
    private String systype;

    /** persistent field */
    private String crdb;

    /** persistent field */
    private String jrntype;

    /** persistent field */
    private String passbookflag;

    /** persistent field */
    private short timeout;

    /** persistent field */
    private String communicationtype;

    /** persistent field */
    private String sealflag;

    /** persistent field */
    private String supflag;

    private String moduleid;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsystxn";
    public String getTableuid(){
    	return TABLE_UID;
    }
    
    /** full constructor */
    public TbsystxnVO(String txncode, String txnname, String systype, String crdb, String jrntype, String passbookflag, short timeout, String communicationtype, String sealflag, String supflag,String moduleid, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.txncode = txncode;
        this.txnname = txnname;
        this.systype = systype;
        this.crdb = crdb;
        this.jrntype = jrntype;
        this.passbookflag = passbookflag;
        this.timeout = timeout;
        this.communicationtype = communicationtype;
        this.sealflag = sealflag;
        this.supflag = supflag;
        this.moduleid = moduleid;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsystxnVO() {
    }

    /** minimal constructor */
    public TbsystxnVO(String txncode, String txnname, String systype, String crdb, String jrntype, String passbookflag, short timeout, String communicationtype, String sealflag, String supflag,String moduleid) {
        this.txncode = txncode;
        this.txnname = txnname;
        this.systype = systype;
        this.crdb = crdb;
        this.jrntype = jrntype;
        this.passbookflag = passbookflag;
        this.timeout = timeout;
        this.communicationtype = communicationtype;
        this.sealflag = sealflag;
        this.supflag = supflag;
        this.moduleid = moduleid;
    }

    public String getTxncode() {
        return this.txncode;
    }

    public void setTxncode(String txncode) {
        this.txncode = txncode;
    }

    public String getTxnname() {
        return this.txnname;
    }

    public void setTxnname(String txnname) {
        this.txnname = txnname;
    }

    public String getSystype() {
        return this.systype;
    }

    public void setSystype(String systype) {
        this.systype = systype;
    }

    public String getCrdb() {
        return this.crdb;
    }

    public void setCrdb(String crdb) {
        this.crdb = crdb;
    }

    public String getJrntype() {
        return this.jrntype;
    }

    public void setJrntype(String jrntype) {
        this.jrntype = jrntype;
    }

    public String getPassbookflag() {
        return this.passbookflag;
    }

    public void setPassbookflag(String passbookflag) {
        this.passbookflag = passbookflag;
    }

    public short getTimeout() {
        return this.timeout;
    }

    public void setTimeout(short timeout) {
        this.timeout = timeout;
    }

    public String getCommunicationtype() {
        return this.communicationtype;
    }

    public void setCommunicationtype(String communicationtype) {
        this.communicationtype = communicationtype;
    }

    public String getSealflag() {
        return this.sealflag;
    }

    public void setSealflag(String sealflag) {
        this.sealflag = sealflag;
    }

    public String getSupflag() {
        return this.supflag;
    }

    public void setSupflag(String supflag) {
        this.supflag = supflag;
    }

 
    public String getModuleid() {
        return this.moduleid;
    }

    public void setModuleid(String moduleid) {
        this.moduleid = moduleid;
    }
    
    public String toString() {
        return new ToStringBuilder(this)
            .append("txncode", getTxncode())
            .toString();
    }

}
