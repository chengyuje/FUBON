package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_BDS060PK  implements Serializable  {

    /** identifier field */
    private String BDF01;

    /** identifier field */
    private Timestamp BDF05;

    /** identifier field */
    private String BDF0C;

    /** full constructor */
    public TBPMS_BDS060PK(String BDF01, Timestamp BDF05, String BDF0C) {
        this.BDF01 = BDF01;
        this.BDF05 = BDF05;
        this.BDF0C = BDF0C;
    }

    /** default constructor */
    public TBPMS_BDS060PK() {
    }

    public String getBDF01() {
        return this.BDF01;
    }

    public void setBDF01(String BDF01) {
        this.BDF01 = BDF01;
    }

    public Timestamp getBDF05() {
        return this.BDF05;
    }

    public void setBDF05(Timestamp BDF05) {
        this.BDF05 = BDF05;
    }

    public String getBDF0C() {
        return this.BDF0C;
    }

    public void setBDF0C(String BDF0C) {
        this.BDF0C = BDF0C;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BDF01", getBDF01())
            .append("BDF05", getBDF05())
            .append("BDF0C", getBDF0C())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_BDS060PK) ) return false;
        TBPMS_BDS060PK castOther = (TBPMS_BDS060PK) other;
        return new EqualsBuilder()
            .append(this.getBDF01(), castOther.getBDF01())
            .append(this.getBDF05(), castOther.getBDF05())
            .append(this.getBDF0C(), castOther.getBDF0C())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBDF01())
            .append(getBDF05())
            .append(getBDF0C())
            .toHashCode();
    }

}
