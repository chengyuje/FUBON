package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_XLS_IMP_MAPPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String ID;

    /** identifier field */
    private BigDecimal SRC_COL_IDX;

    /** full constructor */
    public TBSYS_XLS_IMP_MAPPK(String ID, BigDecimal SRC_COL_IDX) {
        this.ID = ID;
        this.SRC_COL_IDX = SRC_COL_IDX;
    }

    /** default constructor */
    public TBSYS_XLS_IMP_MAPPK() {
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public BigDecimal getSRC_COL_IDX() {
        return this.SRC_COL_IDX;
    }

    public void setSRC_COL_IDX(BigDecimal SRC_COL_IDX) {
        this.SRC_COL_IDX = SRC_COL_IDX;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ID", getID())
            .append("SRC_COL_IDX", getSRC_COL_IDX())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYS_XLS_IMP_MAPPK) ) return false;
        TBSYS_XLS_IMP_MAPPK castOther = (TBSYS_XLS_IMP_MAPPK) other;
        return new EqualsBuilder()
            .append(this.getID(), castOther.getID())
            .append(this.getSRC_COL_IDX(), castOther.getSRC_COL_IDX())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getID())
            .append(getSRC_COL_IDX())
            .toHashCode();
    }

}
