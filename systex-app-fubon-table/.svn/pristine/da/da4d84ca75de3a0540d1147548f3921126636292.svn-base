package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_QUESTIONNAIREPK  implements Serializable  {

    /** identifier field */
    private String EXAM_VERSION;

    /** identifier field */
    private String QUESTION_VERSION;

    /** full constructor */
    public TBSYS_QUESTIONNAIREPK(String EXAM_VERSION, String QUESTION_VERSION) {
        this.EXAM_VERSION = EXAM_VERSION;
        this.QUESTION_VERSION = QUESTION_VERSION;
    }

    /** default constructor */
    public TBSYS_QUESTIONNAIREPK() {
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
            .append("EXAM_VERSION", getEXAM_VERSION())
            .append("QUESTION_VERSION", getQUESTION_VERSION())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYS_QUESTIONNAIREPK) ) return false;
        TBSYS_QUESTIONNAIREPK castOther = (TBSYS_QUESTIONNAIREPK) other;
        return new EqualsBuilder()
            .append(this.getEXAM_VERSION(), castOther.getEXAM_VERSION())
            .append(this.getQUESTION_VERSION(), castOther.getQUESTION_VERSION())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEXAM_VERSION())
            .append(getQUESTION_VERSION())
            .toHashCode();
    }

}
