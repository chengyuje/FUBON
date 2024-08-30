package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private BigDecimal ANSWER_SEQ;

    /** identifier field */
    private String EXAM_VERSION;

    /** identifier field */
    private String QUESTION_VERSION;

    /** full constructor */
    public TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK(BigDecimal ANSWER_SEQ, String EXAM_VERSION, String QUESTION_VERSION) {
        this.ANSWER_SEQ = ANSWER_SEQ;
        this.EXAM_VERSION = EXAM_VERSION;
        this.QUESTION_VERSION = QUESTION_VERSION;
    }

    /** default constructor */
    public TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK() {
    }

    public BigDecimal getANSWER_SEQ() {
        return this.ANSWER_SEQ;
    }

    public void setANSWER_SEQ(BigDecimal ANSWER_SEQ) {
        this.ANSWER_SEQ = ANSWER_SEQ;
    }

    public String getEXAM_VERSION() {
        return this.EXAM_VERSION;
    }

    public void setEXAM_VERSION(String EXAM_VERSION) {
        this.EXAM_VERSION = EXAM_VERSION;
    }

    public String getQUESTION_VERSION() {
        return this.QUESTION_VERSION;
    }

    public void setQUESTION_VERSION(String QUESTION_VERSION) {
        this.QUESTION_VERSION = QUESTION_VERSION;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ANSWER_SEQ", getANSWER_SEQ())
            .append("EXAM_VERSION", getEXAM_VERSION())
            .append("QUESTION_VERSION", getQUESTION_VERSION())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK) ) return false;
        TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK castOther = (TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK) other;
        return new EqualsBuilder()
            .append(this.getANSWER_SEQ(), castOther.getANSWER_SEQ())
            .append(this.getEXAM_VERSION(), castOther.getEXAM_VERSION())
            .append(this.getQUESTION_VERSION(), castOther.getQUESTION_VERSION())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getANSWER_SEQ())
            .append(getEXAM_VERSION())
            .append(getQUESTION_VERSION())
            .toHashCode();
    }

}
