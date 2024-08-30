package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysschdaddetailPK  implements Serializable  {

    /** identifier field */
    private long detailid;

    /** identifier field */
    private String auditid;
    
    /** identifier field */
    private String scheduleid;

    /** full constructor */
    public TbsysschdaddetailPK(long detailid, String auditid, String scheduleid) {
        this.detailid = detailid;
        this.auditid = auditid;
        this.scheduleid = scheduleid;
    }

    /** default constructor */
    public TbsysschdaddetailPK() {
    }

    public long getdetailid() {
		return detailid;
	}

	public void setdetailid(long detailid) {
		this.detailid = detailid;
	}

	public String getauditid() {
		return auditid;
	}

	public void setauditid(String auditid) {
		this.auditid = auditid;
	}

	public String getscheduleid() {
		return scheduleid;
	}

	public void setscheduleid(String scheduleid) {
		this.scheduleid = scheduleid;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("detailid", getdetailid())
            .append("auditid", getauditid())
            .append("scheduleid", getscheduleid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysschdaddetailPK) ) return false;
        TbsysschdaddetailPK castOther = (TbsysschdaddetailPK) other;
        return new EqualsBuilder()
            .append(this.getdetailid(), castOther.getdetailid())
            .append(this.getauditid(), castOther.getauditid())
            .append(this.getscheduleid(), castOther.getscheduleid())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getdetailid())
            .append(getauditid())
            .append(getscheduleid())
            .toHashCode();
    }

}
