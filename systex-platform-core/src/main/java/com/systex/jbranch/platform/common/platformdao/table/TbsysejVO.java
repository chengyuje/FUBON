package com.systex.jbranch.platform.common.platformdao.table;


import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;




/** @author Hibernate CodeGenerator */
public class TbsysejVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsysejPK comp_id;

    /** persistent field */
    private String txntime;

    /** persistent field */
    private String printflag;

    /** persistent field */
    private String status;

    /** persistent field */
    private String txncode;

    /** persistent field */
    private String outputType;

    /** persistent field */
    private String trmid;

    /** persistent field */
    private String tellerid;

    /** persistent field */
    private String txnseq;

    /** persistent field */
    private String account;

    /** persistent field */
    private String supvno;

    /** persistent field */
    private BigDecimal amount;

    /** persistent field */
    private String crdb;

    /** nullable persistent field */
    private Clob ouotputData;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysej";
    public String getTableuid(){
    	return TABLE_UID;
    }
   
    /** full constructor */
    public TbsysejVO(com.systex.jbranch.platform.common.platformdao.table.TbsysejPK comp_id, String txntime, String printflag, String status, String txncode, String outputType, String trmid, String tellerid, String txnseq, String account, String supvno, BigDecimal amount, String crdb, Clob ouotputData, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.comp_id = comp_id;
        this.txntime = txntime;
        this.printflag = printflag;
        this.status = status;
        this.txncode = txncode;
        this.outputType = outputType;
        this.trmid = trmid;
        this.tellerid = tellerid;
        this.txnseq = txnseq;
        this.account = account;
        this.supvno = supvno;
        this.amount = amount;
        this.crdb = crdb;
        this.ouotputData = ouotputData;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsysejVO() {
    }

    /** minimal constructor */
    public TbsysejVO(com.systex.jbranch.platform.common.platformdao.table.TbsysejPK comp_id, String txntime, String printflag, String status, String txncode, String outputType, String trmid, String tellerid, String txnseq, String account, String supvno, BigDecimal amount, String crdb) {
        this.comp_id = comp_id;
        this.txntime = txntime;
        this.printflag = printflag;
        this.status = status;
        this.txncode = txncode;
        this.outputType = outputType;
        this.trmid = trmid;
        this.tellerid = tellerid;
        this.txnseq = txnseq;
        this.account = account;
        this.supvno = supvno;
        this.amount = amount;
        this.crdb = crdb;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsysejPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(com.systex.jbranch.platform.common.platformdao.table.TbsysejPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTxntime() {
        return this.txntime;
    }

    public void setTxntime(String txntime) {
        this.txntime = txntime;
    }

    public String getPrintflag() {
        return this.printflag;
    }

    public void setPrintflag(String printflag) {
        this.printflag = printflag;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTxncode() {
        return this.txncode;
    }

    public void setTxncode(String txncode) {
        this.txncode = txncode;
    }

    public String getOutputType() {
        return this.outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getTrmid() {
        return this.trmid;
    }

    public void setTrmid(String trmid) {
        this.trmid = trmid;
    }

    public String getTellerid() {
        return this.tellerid;
    }

    public void setTellerid(String tellerid) {
        this.tellerid = tellerid;
    }

    public String getTxnseq() {
        return this.txnseq;
    }

    public void setTxnseq(String txnseq) {
        this.txnseq = txnseq;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSupvno() {
        return this.supvno;
    }

    public void setSupvno(String supvno) {
        this.supvno = supvno;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCrdb() {
        return this.crdb;
    }

    public void setCrdb(String crdb) {
        this.crdb = crdb;
    }

    public Clob getOuotputData() {
        return this.ouotputData;
    }

    public void setOuotputData(Clob ouotputData) {
        this.ouotputData = ouotputData;
    }

 
    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysejVO) ) return false;
        TbsysejVO castOther = (TbsysejVO) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
