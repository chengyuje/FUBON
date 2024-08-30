package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_SFA_CAMP_RESPONSEPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CAMPAIGN_ID;

    /** identifier field */
    private String LEAD_STATUS;

    /** full constructor */
    public TBCAM_SFA_CAMP_RESPONSEPK(String CAMPAIGN_ID, String LEAD_STATUS) {
        this.CAMPAIGN_ID = CAMPAIGN_ID;
        this.LEAD_STATUS = LEAD_STATUS;
    }

    /** default constructor */
    public TBCAM_SFA_CAMP_RESPONSEPK() {
    }

    public String getCAMPAIGN_ID() {
        return this.CAMPAIGN_ID;
    }

    public void setCAMPAIGN_ID(String CAMPAIGN_ID) {
        this.CAMPAIGN_ID = CAMPAIGN_ID;
    }

    public String getLEAD_STATUS() {
        return this.LEAD_STATUS;
    }

    public void setLEAD_STATUS(String LEAD_STATUS) {
        this.LEAD_STATUS = LEAD_STATUS;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CAMPAIGN_ID", getCAMPAIGN_ID())
            .append("LEAD_STATUS", getLEAD_STATUS())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_SFA_CAMP_RESPONSEPK) ) return false;
        TBCAM_SFA_CAMP_RESPONSEPK castOther = (TBCAM_SFA_CAMP_RESPONSEPK) other;
        return new EqualsBuilder()
            .append(this.getCAMPAIGN_ID(), castOther.getCAMPAIGN_ID())
            .append(this.getLEAD_STATUS(), castOther.getLEAD_STATUS())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCAMPAIGN_ID())
            .append(getLEAD_STATUS())
            .toHashCode();
    }

}
