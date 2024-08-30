package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_SALESRPT_WK_GOALPK  implements Serializable  {

    /** identifier field */
    private String YYYYMM;
    
    /** identifier field */
    private String ROLE_NAME;


    /** full constructor */
    public TBPMS_SALESRPT_WK_GOALPK(String YYYYMM, String ROLE_NAME) {
        this.YYYYMM = YYYYMM;
        this.ROLE_NAME = ROLE_NAME;
    }

    /** default constructor */
    public TBPMS_SALESRPT_WK_GOALPK() {
    }

    public String getYYYYMM() {
        return this.YYYYMM;
    }

    public void setYYYYMM(String YYYYMM) {
        this.YYYYMM = YYYYMM;
    }
    
	public String getROLE_NAME() {
		return ROLE_NAME;
	}

	public void setROLE_NAME(String rOLE_NAME) {
		ROLE_NAME = rOLE_NAME;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("YYYYMM", getYYYYMM())
            .append("ROLE_NAME", getROLE_NAME())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_SALESRPT_WK_GOALPK) ) return false;
        TBPMS_SALESRPT_WK_GOALPK castOther = (TBPMS_SALESRPT_WK_GOALPK) other;
        return new EqualsBuilder()
            .append(this.getYYYYMM(), castOther.getYYYYMM())
            .append(this.getROLE_NAME(), castOther.getROLE_NAME())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getYYYYMM())
            .append(getROLE_NAME())
            .toHashCode();
    }

}
