package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_MEMBER_ROLEPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String EMP_ID;

    /** identifier field */
    private String IS_PRIMARY_ROLE;

    /** identifier field */
    private String ROLE_ID;

    /** full constructor */
    public TBORG_MEMBER_ROLEPK(String EMP_ID, String IS_PRIMARY_ROLE, String ROLE_ID) {
        this.EMP_ID = EMP_ID;
        this.IS_PRIMARY_ROLE = IS_PRIMARY_ROLE;
        this.ROLE_ID = ROLE_ID;
    }

    /** default constructor */
    public TBORG_MEMBER_ROLEPK() {
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getIS_PRIMARY_ROLE() {
        return this.IS_PRIMARY_ROLE;
    }

    public void setIS_PRIMARY_ROLE(String IS_PRIMARY_ROLE) {
        this.IS_PRIMARY_ROLE = IS_PRIMARY_ROLE;
    }

    public String getROLE_ID() {
        return this.ROLE_ID;
    }

    public void setROLE_ID(String ROLE_ID) {
        this.ROLE_ID = ROLE_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMP_ID", getEMP_ID())
            .append("IS_PRIMARY_ROLE", getIS_PRIMARY_ROLE())
            .append("ROLE_ID", getROLE_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBORG_MEMBER_ROLEPK) ) return false;
        TBORG_MEMBER_ROLEPK castOther = (TBORG_MEMBER_ROLEPK) other;
        return new EqualsBuilder()
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .append(this.getIS_PRIMARY_ROLE(), castOther.getIS_PRIMARY_ROLE())
            .append(this.getROLE_ID(), castOther.getROLE_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEMP_ID())
            .append(getIS_PRIMARY_ROLE())
            .append(getROLE_ID())
            .toHashCode();
    }

}
