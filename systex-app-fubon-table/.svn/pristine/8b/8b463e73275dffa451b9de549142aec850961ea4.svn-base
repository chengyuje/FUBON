package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_PORTFOLIO_PLAN_RPTPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String PLAN_ID;
    private String REPORT_ID;


    /** full constructor */
    public TBFPS_PORTFOLIO_PLAN_RPTPK(String PLAN_ID) {
        this.PLAN_ID = PLAN_ID;
    }

    /** default constructor */
    public TBFPS_PORTFOLIO_PLAN_RPTPK() {
    }
    
	public String getPLAN_ID() {
		return PLAN_ID;
	}

	public void setPLAN_ID(String pLAN_ID) {
		PLAN_ID = pLAN_ID;
	}

	public String getREPORT_ID() {
		return REPORT_ID;
	}

	public void setREPORT_ID(String rEPORT_ID) {
		REPORT_ID = rEPORT_ID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("PLAN_ID", getPLAN_ID())
            .append("REPORT_ID", getREPORT_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_PORTFOLIO_PLAN_RPTPK) ) return false;
        TBFPS_PORTFOLIO_PLAN_RPTPK castOther = (TBFPS_PORTFOLIO_PLAN_RPTPK) other;
        return new EqualsBuilder()
            .append(this.getPLAN_ID(), castOther.getPLAN_ID())
            .append(this.getREPORT_ID(), castOther.getREPORT_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPLAN_ID())
            .append(getREPORT_ID())
            .toHashCode();
    }

}
