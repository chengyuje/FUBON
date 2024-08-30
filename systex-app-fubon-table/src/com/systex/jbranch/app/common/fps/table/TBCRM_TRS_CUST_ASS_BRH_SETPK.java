package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_CUST_ASS_BRH_SETPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String ASS_BRH;

    /** identifier field */
    private String FCH_MAST_BRH;

    /** full constructor */
    public TBCRM_TRS_CUST_ASS_BRH_SETPK(String ASS_BRH, String FCH_MAST_BRH) {
        this.ASS_BRH = ASS_BRH;
        this.FCH_MAST_BRH = FCH_MAST_BRH;
    }

    /** default constructor */
    public TBCRM_TRS_CUST_ASS_BRH_SETPK() {
    }

    public String getASS_BRH() {
        return this.ASS_BRH;
    }

    public void setASS_BRH(String ASS_BRH) {
        this.ASS_BRH = ASS_BRH;
    }

    public String getFCH_MAST_BRH() {
        return this.FCH_MAST_BRH;
    }

    public void setFCH_MAST_BRH(String FCH_MAST_BRH) {
        this.FCH_MAST_BRH = FCH_MAST_BRH;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ASS_BRH", getASS_BRH())
            .append("FCH_MAST_BRH", getFCH_MAST_BRH())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_TRS_CUST_ASS_BRH_SETPK) ) return false;
        TBCRM_TRS_CUST_ASS_BRH_SETPK castOther = (TBCRM_TRS_CUST_ASS_BRH_SETPK) other;
        return new EqualsBuilder()
            .append(this.getASS_BRH(), castOther.getASS_BRH())
            .append(this.getFCH_MAST_BRH(), castOther.getFCH_MAST_BRH())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getASS_BRH())
            .append(getFCH_MAST_BRH())
            .toHashCode();
    }

}
