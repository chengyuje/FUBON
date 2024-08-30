package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysschdadmasterPK  implements Serializable  {

	/** identifier field */
    private long auditid;
    
	/** identifier field */
    private String scheduleid;

    /** full constructor */
    public TbsysschdadmasterPK(long auditid, String scheduleid) {
    	this.auditid = auditid;
    	this.scheduleid = scheduleid;
    }

    /** default constructor */
    public TbsysschdadmasterPK() {
    }

    public String getscheduleid() {
		return scheduleid;
	}

	public void setscheduleid(String scheduleid) {
		this.scheduleid = scheduleid;
	}

	public long getauditid() {
		return auditid;
	}

	public void setauditid(long auditid) {
		this.auditid = auditid;
	}

	public String toString() {
        return new ToStringBuilder(this)
        	.append("auditid", getauditid())
            .append("scheduleid", getscheduleid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysschdadmasterPK) ) return false;
        TbsysschdadmasterPK castOther = (TbsysschdadmasterPK) other;
        return new EqualsBuilder()
        	.append(this.getauditid(), castOther.getauditid())
            .append(this.getscheduleid(), castOther.getscheduleid())           
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
        	.append(getauditid())
            .append(getscheduleid())            
            .toHashCode();
    }

}
