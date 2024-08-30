package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_CSM_NFI_SCOREPK  implements Serializable  {

    /** identifier field */
    private String CASE_NO;

    /** identifier field */
    private String EMP_TYPE;

    /** identifier field */
    private String YEARMON;

    /** full constructor */
    public TBSQM_CSM_NFI_SCOREPK(String CASE_NO, String EMP_TYPE, String YEARMON) {
        this.CASE_NO = CASE_NO;
        this.EMP_TYPE = EMP_TYPE;
        this.YEARMON = YEARMON;
    }

    /** default constructor */
    public TBSQM_CSM_NFI_SCOREPK() {
    }

    public String getCASE_NO() {
        return this.CASE_NO;
    }

    public void setCASE_NO(String CASE_NO) {
        this.CASE_NO = CASE_NO;
    }

    public String getEMP_TYPE() {
        return this.EMP_TYPE;
    }

    public void setEMP_TYPE(String EMP_TYPE) {
        this.EMP_TYPE = EMP_TYPE;
    }

    public String getYEARMON() {
        return this.YEARMON;
    }

    public void setYEARMON(String YEARMON) {
        this.YEARMON = YEARMON;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CASE_NO", getCASE_NO())
            .append("EMP_TYPE", getEMP_TYPE())
            .append("YEARMON", getYEARMON())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSQM_CSM_NFI_SCOREPK) ) return false;
        TBSQM_CSM_NFI_SCOREPK castOther = (TBSQM_CSM_NFI_SCOREPK) other;
        return new EqualsBuilder()
            .append(this.getCASE_NO(), castOther.getCASE_NO())
            .append(this.getEMP_TYPE(), castOther.getEMP_TYPE())
            .append(this.getYEARMON(), castOther.getYEARMON())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCASE_NO())
            .append(getEMP_TYPE())
            .append(getYEARMON())
            .toHashCode();
    }

}
