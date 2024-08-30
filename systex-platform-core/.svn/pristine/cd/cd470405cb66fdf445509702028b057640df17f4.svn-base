package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;



 
/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysschdjobassVO extends VOBase {
             
    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK comp_id;

    /** persistent field */
    private BigDecimal joborder;



 
public com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(
			com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK comp_id) {
		this.comp_id = comp_id;
	}

	
public BigDecimal getJoborder() {
		return joborder;
	}

	public void setJoborder(BigDecimal joborder) {
		this.joborder = joborder;
	}

 
public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysschdjobass";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysschdjobassVO(com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK comp_id, BigDecimal joborder, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.comp_id = comp_id;
        this.joborder = joborder;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsysschdjobassVO() {
    }

    /** minimal constructor */
    public TbsysschdjobassVO(com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK comp_id, BigDecimal ordernumber) {
        this.comp_id = comp_id;
        this.joborder = joborder;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK comp_id) {
        this.comp_id = comp_id;
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
        if ( !(other instanceof TbsysschdjobassVO) ) return false;
        TbsysschdjobassVO castOther = (TbsysschdjobassVO) other;
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
