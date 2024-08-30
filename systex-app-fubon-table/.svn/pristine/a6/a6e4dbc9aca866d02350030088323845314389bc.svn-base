package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_QUESTIONNAIRE_FLW_DETAILPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String EXAM_VERSION;

    /** identifier field */
    private String SIGNOFF_NUM;

    /** full constructor */
    public TBKYC_QUESTIONNAIRE_FLW_DETAILPK(String EXAM_VERSION, String SIGNOFF_NUM) {
        this.EXAM_VERSION = EXAM_VERSION;
        this.SIGNOFF_NUM = SIGNOFF_NUM;
    }

    /** default constructor */
    public TBKYC_QUESTIONNAIRE_FLW_DETAILPK() {
    }

    public String getEXAM_VERSION() {
        return this.EXAM_VERSION;
    }

    public void setEXAM_VERSION(String EXAM_VERSION) {
        this.EXAM_VERSION = EXAM_VERSION;
    }

    public String getSIGNOFF_NUM() {
        return this.SIGNOFF_NUM;
    }

    public void setSIGNOFF_NUM(String SIGNOFF_NUM) {
        this.SIGNOFF_NUM = SIGNOFF_NUM;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EXAM_VERSION", getEXAM_VERSION())
            .append("SIGNOFF_NUM", getSIGNOFF_NUM())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBKYC_QUESTIONNAIRE_FLW_DETAILPK) ) return false;
        TBKYC_QUESTIONNAIRE_FLW_DETAILPK castOther = (TBKYC_QUESTIONNAIRE_FLW_DETAILPK) other;
        return new EqualsBuilder()
            .append(this.getEXAM_VERSION(), castOther.getEXAM_VERSION())
            .append(this.getSIGNOFF_NUM(), castOther.getSIGNOFF_NUM())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEXAM_VERSION())
            .append(getSIGNOFF_NUM())
            .toHashCode();
    }

}
