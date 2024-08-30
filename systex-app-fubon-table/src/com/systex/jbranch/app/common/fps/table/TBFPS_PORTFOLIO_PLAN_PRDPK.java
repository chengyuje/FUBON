package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_PORTFOLIO_PLAN_PRDPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String PLAN_ID;

    /** identifier field */
    private String PRD_ID;
    
    /** identifier field */
    private String PRD_TYPE;

    /** full constructor */
    public TBFPS_PORTFOLIO_PLAN_PRDPK(String PLAN_ID, String PRD_ID, String PRD_TYPE) {
        this.PLAN_ID = PLAN_ID;
        this.PRD_ID = PRD_ID;
        this.PRD_TYPE = PRD_TYPE;
    }

    /** default constructor */
    public TBFPS_PORTFOLIO_PLAN_PRDPK() {
    }
  
	public String getPLAN_ID() {
		return PLAN_ID;
	}

	public void setPLAN_ID(String pLAN_ID) {
		PLAN_ID = pLAN_ID;
	}

	public String getPRD_ID() {
		return PRD_ID;
	}

	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}

	public String getPRD_TYPE() {
		return PRD_TYPE;
	}

	public void setPRD_TYPE(String pRD_TYPE) {
		PRD_TYPE = pRD_TYPE;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("PLAN_ID", getPLAN_ID())
            .append("PRD_ID", getPRD_ID())
            .append("PRD_TYPE", getPRD_TYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBFPS_PORTFOLIO_PLAN_PRDPK) ) return false;
        TBFPS_PORTFOLIO_PLAN_PRDPK castOther = (TBFPS_PORTFOLIO_PLAN_PRDPK) other;
        return new EqualsBuilder()
            .append(this.getPLAN_ID(), castOther.getPLAN_ID())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getPRD_TYPE(), castOther.getPRD_TYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPLAN_ID())
            .append(getPRD_ID())
            .append(getPRD_TYPE())
            .toHashCode();
    }

}
