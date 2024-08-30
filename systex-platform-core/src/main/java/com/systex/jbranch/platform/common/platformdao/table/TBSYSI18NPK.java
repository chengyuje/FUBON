package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSI18NPK  implements Serializable  {

    /** identifier field */
    private String LOCALE;

    /** identifier field */
    private String CODE;

    /** full constructor */
    public TBSYSI18NPK(String LOCALE, String CODE) {
        this.LOCALE = LOCALE;
        this.CODE = CODE;
    }

    /** default constructor */
    public TBSYSI18NPK() {
    }

    public String getLOCALE() {
        return this.LOCALE;
    }

    public void setLOCALE(String LOCALE) {
        this.LOCALE = LOCALE;
    }

    public String getCODE() {
        return this.CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("LOCALE", getLOCALE())
            .append("CODE", getCODE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSI18NPK) ) return false;
        TBSYSI18NPK castOther = (TBSYSI18NPK) other;
        return new EqualsBuilder()
            .append(this.getLOCALE(), castOther.getLOCALE())
            .append(this.getCODE(), castOther.getCODE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getLOCALE())
            .append(getCODE())
            .toHashCode();
    }

}
