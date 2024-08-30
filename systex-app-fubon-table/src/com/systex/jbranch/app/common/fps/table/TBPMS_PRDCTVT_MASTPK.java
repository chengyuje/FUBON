package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_PRDCTVT_MASTPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String AO_CODE;

    /** identifier field */
    private String EMP_ID;

    /** identifier field */
    private String WEEK_START_DATE;

    /** full constructor */
    public TBPMS_PRDCTVT_MASTPK(String AO_CODE, String EMP_ID, String WEEK_START_DATE) {
        this.AO_CODE = AO_CODE;
        this.EMP_ID = EMP_ID;
        this.WEEK_START_DATE = WEEK_START_DATE;
    }

    /** default constructor */
    public TBPMS_PRDCTVT_MASTPK() {
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getWEEK_START_DATE() {
        return this.WEEK_START_DATE;
    }

    public void setWEEK_START_DATE(String WEEK_START_DATE) {
        this.WEEK_START_DATE = WEEK_START_DATE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AO_CODE", getAO_CODE())
            .append("EMP_ID", getEMP_ID())
            .append("WEEK_START_DATE", getWEEK_START_DATE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_PRDCTVT_MASTPK) ) return false;
        TBPMS_PRDCTVT_MASTPK castOther = (TBPMS_PRDCTVT_MASTPK) other;
        return new EqualsBuilder()
            .append(this.getAO_CODE(), castOther.getAO_CODE())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getWEEK_START_DATE(), castOther.getWEEK_START_DATE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAO_CODE())
            .append(getEMP_ID())
            .append(getWEEK_START_DATE())
            .toHashCode();
    }

}
