package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysschdadmasterVO extends VOBase {

	 /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterPK comp_id;

    /** nullable persistent field */
    private String jobid;

    /** nullable persistent field */
    private Timestamp starttime;

    /** nullable persistent field */
    private Timestamp endtime;

    /** nullable persistent field */
    private String type;

    /** nullable persistent field */
    private String status;

    /** nullable persistent field */
    private BigDecimal insertrecord;
    
    public BigDecimal getInsertrecord() {
		return insertrecord;
	}

	public void setInsertrecord(BigDecimal insertrecord) {
		this.insertrecord = insertrecord;
	}

	/** nullable persistent field */
    private BigDecimal updaterecord;


	/** nullable persistent field */
    private BigDecimal failrecord;

    /** nullable persistent field */
    private BigDecimal totalrecord;

    /** nullable persistent field */
    private String result;

    /** nullable persistent field */
    private String description;
    
    /** nullable persistent field */
    private String memo;


    public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysschdadmaster";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysschdadmasterVO(com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterPK comp_id, String jobid, Timestamp starttime, Timestamp endtime, String type, String status, BigDecimal insertrecord, BigDecimal updaterecord, BigDecimal failrecord, BigDecimal totalrecord, String result, String description, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
    	this.comp_id = comp_id;
        this.jobid = jobid;
        this.starttime = starttime;
        this.endtime = endtime;
        this.type = type;
        this.status = status;
        this.insertrecord = insertrecord;
        this.updaterecord = updaterecord;
        this.failrecord = failrecord;
        this.totalrecord = totalrecord;
        this.result = result;
        this.description = description;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsysschdadmasterVO() {
    }

    /** minimal constructor */
    public TbsysschdadmasterVO(com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterPK comp_id) {
        this.comp_id = comp_id;
    }
   
    public String getjobid() {
        return this.jobid;
    }

    public void setjobid(String jobid) {
        this.jobid = jobid;
    }

    public Timestamp getstarttime() {
        return this.starttime;
    }

    public void setstarttime(Timestamp starttime) {
        this.starttime = starttime;
    }

    public Timestamp getendtime() {
        return this.endtime;
    }

    public void setendtime(Timestamp endtime) {
        this.endtime = endtime;
    }

    public String gettype() {
        return this.type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String getstatus() {
        return this.status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public BigDecimal getfailrecord() {
        return this.failrecord;
    }

    public void setfailrecord(BigDecimal failrecord) {
        this.failrecord = failrecord;
    }

    public BigDecimal gettotalrecord() {
        return this.totalrecord;
    }

    public void settotalrecord(BigDecimal totalrecord) {
        this.totalrecord = totalrecord;
    }

    public String getresult() {
        return this.result;
    }

    public void setresult(String result) {
        this.result = result;
    }

    public String getdescription() {
        return this.description;
    }

    public void setdescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getUpdaterecord() {
		return updaterecord;
	}

	public void setUpdaterecord(BigDecimal updaterecord) {
		this.updaterecord = updaterecord;
	}


    public void checkDefaultValue() {
    }
    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysschdadmasterVO) ) return false;
        TbsysschdadmasterVO castOther = (TbsysschdadmasterVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
