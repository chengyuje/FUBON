package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_SFA_LE_REA_LOGPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private Timestamp LE_REA_DTTM;

    /** identifier field */
    private String SFA_LEAD_ID;

    /** full constructor */
    public TBCAM_SFA_LE_REA_LOGPK(Timestamp LE_REA_DTTM, String SFA_LEAD_ID) {
        this.LE_REA_DTTM = LE_REA_DTTM;
        this.SFA_LEAD_ID = SFA_LEAD_ID;
    }

    /** default constructor */
    public TBCAM_SFA_LE_REA_LOGPK() {
    }

    public Timestamp getLE_REA_DTTM() {
        return this.LE_REA_DTTM;
    }

    public void setLE_REA_DTTM(Timestamp LE_REA_DTTM) {
        this.LE_REA_DTTM = LE_REA_DTTM;
    }

    public String getSFA_LEAD_ID() {
        return this.SFA_LEAD_ID;
    }

    public void setSFA_LEAD_ID(String SFA_LEAD_ID) {
        this.SFA_LEAD_ID = SFA_LEAD_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("LE_REA_DTTM", getLE_REA_DTTM())
            .append("SFA_LEAD_ID", getSFA_LEAD_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_SFA_LE_REA_LOGPK) ) return false;
        TBCAM_SFA_LE_REA_LOGPK castOther = (TBCAM_SFA_LE_REA_LOGPK) other;
        return new EqualsBuilder()
            .append(this.getLE_REA_DTTM(), castOther.getLE_REA_DTTM())
            .append(this.getSFA_LEAD_ID(), castOther.getSFA_LEAD_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getLE_REA_DTTM())
            .append(getSFA_LEAD_ID())
            .toHashCode();
    }

}
