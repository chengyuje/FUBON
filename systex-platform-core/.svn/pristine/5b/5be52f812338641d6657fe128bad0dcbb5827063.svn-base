package com.systex.jbranch.platform.common.platformdao.table;


import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;




/** @author Hibernate CodeGenerator */
public class TbsysbranchVO extends VOBase {

    /** identifier field */
    private String brchid;

    /** persistent field */
    private String name;

    /** persistent field */
    private String opdate;

    /** persistent field */
    private String txnflag;

    /** persistent field */
    private String useflag;

   
    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysbranch";
    public String getTableuid(){
    	return TABLE_UID;
    }
    
    /** full constructor */
    public TbsysbranchVO(String brchid, String name, String opdate, String txnflag, String useflag, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.brchid = brchid;
        this.name = name;
        this.opdate = opdate;
        this.txnflag = txnflag;
        this.useflag = useflag;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsysbranchVO() {
    }

    /** minimal constructor */
    public TbsysbranchVO(String brchid, String name, String opdate, String txnflag, String useflag) {
        this.brchid = brchid;
        this.name = name;
        this.opdate = opdate;
        this.txnflag = txnflag;
        this.useflag = useflag;
    }

    public String getBrchid() {
        return this.brchid;
    }

    public void setBrchid(String brchid) {
        this.brchid = brchid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpdate() {
        return this.opdate;
    }

    public void setOpdate(String opdate) {
        this.opdate = opdate;
    }

    public String getTxnflag() {
        return this.txnflag;
    }

    public void setTxnflag(String txnflag) {
        this.txnflag = txnflag;
    }

    public String getUseflag() {
        return this.useflag;
    }

    public void setUseflag(String useflag) {
        this.useflag = useflag;
    }


    public String toString() {
        return new ToStringBuilder(this)
            .append("brchid", getBrchid())
            .toString();
    }

}
