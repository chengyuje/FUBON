package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_CSM_FLOW_DTLPK  implements Serializable  {

    /** identifier field */
    private String CASE_NO;

    /** identifier field */
    private String SIGNOFF_NUM;

    /** full constructor */
    public TBSQM_CSM_FLOW_DTLPK(String CASE_NO, String SIGNOFF_NUM) {
        this.CASE_NO = CASE_NO;
        this.SIGNOFF_NUM = SIGNOFF_NUM;
    }

    /** default constructor */
    public TBSQM_CSM_FLOW_DTLPK() {
    }

    public String getCASE_NO() {
        return this.CASE_NO;
    }

    public void setCASE_NO(String CASE_NO) {
        this.CASE_NO = CASE_NO;
    }

    public String getSIGNOFF_NUM() {
        return this.SIGNOFF_NUM;
    }

    public void setSIGNOFF_NUM(String SIGNOFF_NUM) {
        this.SIGNOFF_NUM = SIGNOFF_NUM;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CASE_NO", getCASE_NO())
            .append("SIGNOFF_NUM", getSIGNOFF_NUM())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSQM_CSM_FLOW_DTLPK) ) return false;
        TBSQM_CSM_FLOW_DTLPK castOther = (TBSQM_CSM_FLOW_DTLPK) other;
        return new EqualsBuilder()
            .append(this.getCASE_NO(), castOther.getCASE_NO())
            .append(this.getSIGNOFF_NUM(), castOther.getSIGNOFF_NUM())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCASE_NO())
            .append(getSIGNOFF_NUM())
            .toHashCode();
    }

}
