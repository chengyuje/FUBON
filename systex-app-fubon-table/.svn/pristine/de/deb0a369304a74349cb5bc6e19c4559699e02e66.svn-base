package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_PRO_FUN_TR_PROD_SETPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String DATA_YEARMON;

    /** identifier field */
    private String JOB_TITLE_ID;

    /** identifier field */
    private String OL_TITLE;

    /** full constructor */
    public TBPMS_PRO_FUN_TR_PROD_SETPK(String DATA_YEARMON, String JOB_TITLE_ID, String OL_TITLE) {
        this.DATA_YEARMON = DATA_YEARMON;
        this.JOB_TITLE_ID = JOB_TITLE_ID;
        this.OL_TITLE = OL_TITLE;
    }

    /** default constructor */
    public TBPMS_PRO_FUN_TR_PROD_SETPK() {
    }

    public String getDATA_YEARMON() {
        return this.DATA_YEARMON;
    }

    public void setDATA_YEARMON(String DATA_YEARMON) {
        this.DATA_YEARMON = DATA_YEARMON;
    }

    public String getJOB_TITLE_ID() {
        return this.JOB_TITLE_ID;
    }

    public void setJOB_TITLE_ID(String JOB_TITLE_ID) {
        this.JOB_TITLE_ID = JOB_TITLE_ID;
    }

    public String getOL_TITLE() {
        return this.OL_TITLE;
    }

    public void setOL_TITLE(String OL_TITLE) {
        this.OL_TITLE = OL_TITLE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DATA_YEARMON", getDATA_YEARMON())
            .append("JOB_TITLE_ID", getJOB_TITLE_ID())
            .append("OL_TITLE", getOL_TITLE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_PRO_FUN_TR_PROD_SETPK) ) return false;
        TBPMS_PRO_FUN_TR_PROD_SETPK castOther = (TBPMS_PRO_FUN_TR_PROD_SETPK) other;
        return new EqualsBuilder()
            .append(this.getDATA_YEARMON(), castOther.getDATA_YEARMON())
            .append(this.getJOB_TITLE_ID(), castOther.getJOB_TITLE_ID())
            .append(this.getOL_TITLE(), castOther.getOL_TITLE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDATA_YEARMON())
            .append(getJOB_TITLE_ID())
            .append(getOL_TITLE())
            .toHashCode();
    }

}
