package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MON_TERM_INT_CDPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String CERT_NBR;

    /** identifier field */
    private String DATA_YEARMON;

    /** full constructor */
    public TBPMS_MON_TERM_INT_CDPK(String CERT_NBR, String DATA_YEARMON) {
        this.CERT_NBR = CERT_NBR;
        this.DATA_YEARMON = DATA_YEARMON;
    }

    /** default constructor */
    public TBPMS_MON_TERM_INT_CDPK() {
    }

    public String getCERT_NBR() {
        return this.CERT_NBR;
    }

    public void setCERT_NBR(String CERT_NBR) {
        this.CERT_NBR = CERT_NBR;
    }

    public String getDATA_YEARMON() {
        return this.DATA_YEARMON;
    }

    public void setDATA_YEARMON(String DATA_YEARMON) {
        this.DATA_YEARMON = DATA_YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CERT_NBR", getCERT_NBR())
            .append("DATA_YEARMON", getDATA_YEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_MON_TERM_INT_CDPK) ) return false;
        TBPMS_MON_TERM_INT_CDPK castOther = (TBPMS_MON_TERM_INT_CDPK) other;
        return new EqualsBuilder()
            .append(this.getCERT_NBR(), castOther.getCERT_NBR())
            .append(this.getDATA_YEARMON(), castOther.getDATA_YEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCERT_NBR())
            .append(getDATA_YEARMON())
            .toHashCode();
    }

}
