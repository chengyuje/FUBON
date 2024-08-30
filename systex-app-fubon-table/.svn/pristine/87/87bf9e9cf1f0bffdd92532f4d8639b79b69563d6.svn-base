package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_EMPLOYEE_REC_SGPK  implements Serializable  {

    /** identifier field */
    private String AO_CODE;

    /** identifier field */
    private String EMP_ID;

    /** identifier field */
    private String ORG_ID;

    /** identifier field */
    private Timestamp START_TIME;

    /** full constructor */
    public TBPMS_EMPLOYEE_REC_SGPK(String AO_CODE, String EMP_ID, String ORG_ID, Timestamp START_TIME) {
        this.AO_CODE = AO_CODE;
        this.EMP_ID = EMP_ID;
        this.ORG_ID = ORG_ID;
        this.START_TIME = START_TIME;
    }

    /** default constructor */
    public TBPMS_EMPLOYEE_REC_SGPK() {
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

    public String getORG_ID() {
        return this.ORG_ID;
    }

    public void setORG_ID(String ORG_ID) {
        this.ORG_ID = ORG_ID;
    }

    public Timestamp getSTART_TIME() {
        return this.START_TIME;
    }

    public void setSTART_TIME(Timestamp START_TIME) {
        this.START_TIME = START_TIME;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AO_CODE", getAO_CODE())
            .append("EMP_ID", getEMP_ID())
            .append("ORG_ID", getORG_ID())
            .append("START_TIME", getSTART_TIME())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_EMPLOYEE_REC_SGPK) ) return false;
        TBPMS_EMPLOYEE_REC_SGPK castOther = (TBPMS_EMPLOYEE_REC_SGPK) other;
        return new EqualsBuilder()
            .append(this.getAO_CODE(), castOther.getAO_CODE())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getORG_ID(), castOther.getORG_ID())
            .append(this.getSTART_TIME(), castOther.getSTART_TIME())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAO_CODE())
            .append(getEMP_ID())
            .append(getORG_ID())
            .append(getSTART_TIME())
            .toHashCode();
    }

}
