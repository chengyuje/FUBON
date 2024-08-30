package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_STOCKPK  implements Serializable  {
    private static final long serialVersionUID = 1L;

	/** identifier field */
    private String PRD_ID;

    /** identifier field */
    private String STOCK_CODE;

    /** full constructor */
    public TBPRD_STOCKPK(String PRD_ID, String STOCK_CODE) {
        this.PRD_ID = PRD_ID;
        this.STOCK_CODE = STOCK_CODE;
    }

    /** default constructor */
    public TBPRD_STOCKPK() {
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getSTOCK_CODE() {
        return this.STOCK_CODE;
    }

    public void setSTOCK_CODE(String STOCK_CODE) {
        this.STOCK_CODE = STOCK_CODE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRD_ID", getPRD_ID())
            .append("STOCK_CODE", getSTOCK_CODE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_STOCKPK) ) return false;
        TBPRD_STOCKPK castOther = (TBPRD_STOCKPK) other;
        return new EqualsBuilder()
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getSTOCK_CODE(), castOther.getSTOCK_CODE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRD_ID())
            .append(getSTOCK_CODE())
            .toHashCode();
    }

}
