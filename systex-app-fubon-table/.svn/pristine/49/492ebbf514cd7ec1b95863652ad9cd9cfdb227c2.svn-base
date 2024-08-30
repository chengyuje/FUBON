package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_EXAMRECORD_DETAILPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private BigDecimal ANSWER_SEQ;

    /** identifier field */
    private String QUESTION_VERSION;

    /** identifier field */
    private String RECORD_SEQ;

    /** full constructor */
    public TBCAM_EXAMRECORD_DETAILPK(BigDecimal ANSWER_SEQ, String QUESTION_VERSION, String RECORD_SEQ) {
        this.ANSWER_SEQ = ANSWER_SEQ;
        this.QUESTION_VERSION = QUESTION_VERSION;
        this.RECORD_SEQ = RECORD_SEQ;
    }

    /** default constructor */
    public TBCAM_EXAMRECORD_DETAILPK() {
    }

    public BigDecimal getANSWER_SEQ() {
        return this.ANSWER_SEQ;
    }

    public void setANSWER_SEQ(BigDecimal ANSWER_SEQ) {
        this.ANSWER_SEQ = ANSWER_SEQ;
    }

    public String getQUESTION_VERSION() {
        return this.QUESTION_VERSION;
    }

    public void setQUESTION_VERSION(String QUESTION_VERSION) {
        this.QUESTION_VERSION = QUESTION_VERSION;
    }

    public String getRECORD_SEQ() {
        return this.RECORD_SEQ;
    }

    public void setRECORD_SEQ(String RECORD_SEQ) {
        this.RECORD_SEQ = RECORD_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ANSWER_SEQ", getANSWER_SEQ())
            .append("QUESTION_VERSION", getQUESTION_VERSION())
            .append("RECORD_SEQ", getRECORD_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_EXAMRECORD_DETAILPK) ) return false;
        TBCAM_EXAMRECORD_DETAILPK castOther = (TBCAM_EXAMRECORD_DETAILPK) other;
        return new EqualsBuilder()
            .append(this.getANSWER_SEQ(), castOther.getANSWER_SEQ())
            .append(this.getQUESTION_VERSION(), castOther.getQUESTION_VERSION())
            .append(this.getRECORD_SEQ(), castOther.getRECORD_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getANSWER_SEQ())
            .append(getQUESTION_VERSION())
            .append(getRECORD_SEQ())
            .toHashCode();
    }

}
