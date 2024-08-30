package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_FAIAPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String EMP_ID;

    /** identifier field */
    private String SUPT_SALES_TEAM_ID;

    /** full constructor */
    public TBORG_FAIAPK(String BRANCH_NBR, String EMP_ID, String SUPT_SALES_TEAM_ID) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.EMP_ID = EMP_ID;
        this.SUPT_SALES_TEAM_ID = SUPT_SALES_TEAM_ID;
    }

    /** default constructor */
    public TBORG_FAIAPK() {
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getSUPT_SALES_TEAM_ID() {
        return this.SUPT_SALES_TEAM_ID;
    }

    public void setSUPT_SALES_TEAM_ID(String SUPT_SALES_TEAM_ID) {
        this.SUPT_SALES_TEAM_ID = SUPT_SALES_TEAM_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("EMP_ID", getEMP_ID())
            .append("SUPT_SALES_TEAM_ID", getSUPT_SALES_TEAM_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBORG_FAIAPK) ) return false;
        TBORG_FAIAPK castOther = (TBORG_FAIAPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getSUPT_SALES_TEAM_ID(), castOther.getSUPT_SALES_TEAM_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getEMP_ID())
            .append(getSUPT_SALES_TEAM_ID())
            .toHashCode();
    }

}
