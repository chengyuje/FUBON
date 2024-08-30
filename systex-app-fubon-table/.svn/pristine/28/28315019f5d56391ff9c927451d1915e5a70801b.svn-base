package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_NR097NPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String PRODUCTTYPE;

    /** identifier field */
    private String TYPENUMBER;

    /** full constructor */
    public TBPRD_NR097NPK(String PRODUCTTYPE, String TYPENUMBER) {
        this.PRODUCTTYPE = PRODUCTTYPE;
        this.TYPENUMBER = TYPENUMBER;
    }

    /** default constructor */
    public TBPRD_NR097NPK() {
    }

    public String getPRODUCTTYPE() {
        return this.PRODUCTTYPE;
    }

    public void setPRODUCTTYPE(String PRODUCTTYPE) {
        this.PRODUCTTYPE = PRODUCTTYPE;
    }

    public String getTYPENUMBER() {
        return this.TYPENUMBER;
    }

    public void setTYPENUMBER(String TYPENUMBER) {
        this.TYPENUMBER = TYPENUMBER;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRODUCTTYPE", getPRODUCTTYPE())
            .append("TYPENUMBER", getTYPENUMBER())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_NR097NPK) ) return false;
        TBPRD_NR097NPK castOther = (TBPRD_NR097NPK) other;
        return new EqualsBuilder()
            .append(this.getPRODUCTTYPE(), castOther.getPRODUCTTYPE())
            .append(this.getTYPENUMBER(), castOther.getTYPENUMBER())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRODUCTTYPE())
            .append(getTYPENUMBER())
            .toHashCode();
    }

}
