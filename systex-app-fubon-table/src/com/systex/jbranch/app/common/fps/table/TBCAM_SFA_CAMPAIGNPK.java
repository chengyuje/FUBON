package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_SFA_CAMPAIGNPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CAMPAIGN_ID;

    /** identifier field */
    private String STEP_ID;

    /** full constructor */
    public TBCAM_SFA_CAMPAIGNPK(String CAMPAIGN_ID, String STEP_ID) {
        this.CAMPAIGN_ID = CAMPAIGN_ID;
        this.STEP_ID = STEP_ID;
    }

    /** default constructor */
    public TBCAM_SFA_CAMPAIGNPK() {
    }

    public String getCAMPAIGN_ID() {
        return this.CAMPAIGN_ID;
    }

    public void setCAMPAIGN_ID(String CAMPAIGN_ID) {
        this.CAMPAIGN_ID = CAMPAIGN_ID;
    }

    public String getSTEP_ID() {
        return this.STEP_ID;
    }

    public void setSTEP_ID(String STEP_ID) {
        this.STEP_ID = STEP_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CAMPAIGN_ID", getCAMPAIGN_ID())
            .append("STEP_ID", getSTEP_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_SFA_CAMPAIGNPK) ) return false;
        TBCAM_SFA_CAMPAIGNPK castOther = (TBCAM_SFA_CAMPAIGNPK) other;
        return new EqualsBuilder()
            .append(this.getCAMPAIGN_ID(), castOther.getCAMPAIGN_ID())
            .append(this.getSTEP_ID(), castOther.getSTEP_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCAMPAIGN_ID())
            .append(getSTEP_ID())
            .toHashCode();
    }

}
