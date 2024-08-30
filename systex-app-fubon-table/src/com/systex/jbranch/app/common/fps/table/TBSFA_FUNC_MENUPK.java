package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSFA_FUNC_MENUPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String MENU_ID;

    /** identifier field */
    private String ITEM_ID;

    /** full constructor */
    public TBSFA_FUNC_MENUPK(String MENU_ID, String ITEM_ID) {
        this.MENU_ID = MENU_ID;
        this.ITEM_ID = ITEM_ID;
    }

    /** default constructor */
    public TBSFA_FUNC_MENUPK() {
    }

    public String getMENU_ID() {
        return this.MENU_ID;
    }

    public void setMENU_ID(String MENU_ID) {
        this.MENU_ID = MENU_ID;
    }

    public String getITEM_ID() {
        return this.ITEM_ID;
    }

    public void setITEM_ID(String ITEM_ID) {
        this.ITEM_ID = ITEM_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("MENU_ID", getMENU_ID())
            .append("ITEM_ID", getITEM_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSFA_FUNC_MENUPK) ) return false;
        TBSFA_FUNC_MENUPK castOther = (TBSFA_FUNC_MENUPK) other;
        return new EqualsBuilder()
            .append(this.getMENU_ID(), castOther.getMENU_ID())
            .append(this.getITEM_ID(), castOther.getITEM_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getMENU_ID())
            .append(getITEM_ID())
            .toHashCode();
    }

}
