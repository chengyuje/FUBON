package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_QST_ANSWERPK  implements Serializable  {

    /** identifier field */
    private BigDecimal ANSWER_SEQ;

    /** identifier field */
    private String QUESTION_VERSION;

    /** full constructor */
    public TBSYS_QST_ANSWERPK(BigDecimal ANSWER_SEQ, String QUESTION_VERSION) {
        this.ANSWER_SEQ = ANSWER_SEQ;
        this.QUESTION_VERSION = QUESTION_VERSION;
    }

    /** default constructor */
    public TBSYS_QST_ANSWERPK() {
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("ANSWER_SEQ", getANSWER_SEQ())
            .append("QUESTION_VERSION", getQUESTION_VERSION())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYS_QST_ANSWERPK) ) return false;
        TBSYS_QST_ANSWERPK castOther = (TBSYS_QST_ANSWERPK) other;
        return new EqualsBuilder()
            .append(this.getANSWER_SEQ(), castOther.getANSWER_SEQ())
            .append(this.getQUESTION_VERSION(), castOther.getQUESTION_VERSION())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getANSWER_SEQ())
            .append(getQUESTION_VERSION())
            .toHashCode();
    }

}
