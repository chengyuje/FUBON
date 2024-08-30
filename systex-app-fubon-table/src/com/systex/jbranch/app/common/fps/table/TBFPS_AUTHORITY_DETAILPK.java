package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_AUTHORITY_DETAILPK  implements Serializable  {

    /** identifier field */
    private String PARAM_NO;

    /** identifier field */
    private String SEQ_NO;

    /** full constructor */
    public TBFPS_AUTHORITY_DETAILPK(String PARAM_NO, String SEQ_NO) {
        this.PARAM_NO = PARAM_NO;
        this.SEQ_NO = SEQ_NO;
    }

    /** default constructor */
    public TBFPS_AUTHORITY_DETAILPK() {
    }

    public String getPARAM_NO() {
        return this.PARAM_NO;
    }

    public void setPARAM_NO(String PARAM_NO) {
        this.PARAM_NO = PARAM_NO;
    }

    public String getSEQ_NO() {
        return this.SEQ_NO;
    }

    public void setSEQ_NO(String SEQ_NO) {
        this.SEQ_NO = SEQ_NO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PARAM_NO", getPARAM_NO())
            .append("SEQ_NO", getSEQ_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_AUTHORITY_DETAILPK) ) return false;
        TBFPS_AUTHORITY_DETAILPK castOther = (TBFPS_AUTHORITY_DETAILPK) other;
        return new EqualsBuilder()
            .append(this.getPARAM_NO(), castOther.getPARAM_NO())
            .append(this.getSEQ_NO(), castOther.getSEQ_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPARAM_NO())
            .append(getSEQ_NO())
            .toHashCode();
    }

}
