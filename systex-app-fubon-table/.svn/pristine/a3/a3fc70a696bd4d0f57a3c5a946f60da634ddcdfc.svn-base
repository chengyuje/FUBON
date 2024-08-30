package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_BDS650PK  implements Serializable  {

    /** identifier field */
    private String BDPE1;

    /** identifier field */
    private Timestamp BDPE2;

    /** identifier field */
    private Timestamp SNAP_DATE;

    /** full constructor */
    public TBPRD_BDS650PK(String BDPE1, Timestamp BDPE2, Timestamp SNAP_DATE) {
        this.BDPE1 = BDPE1;
        this.BDPE2 = BDPE2;
        this.SNAP_DATE = SNAP_DATE;
    }

    /** default constructor */
    public TBPRD_BDS650PK() {
    }

    public String getBDPE1() {
        return this.BDPE1;
    }

    public void setBDPE1(String BDPE1) {
        this.BDPE1 = BDPE1;
    }

    public Timestamp getBDPE2() {
        return this.BDPE2;
    }

    public void setBDPE2(Timestamp BDPE2) {
        this.BDPE2 = BDPE2;
    }

    public Timestamp getSNAP_DATE() {
        return this.SNAP_DATE;
    }

    public void setSNAP_DATE(Timestamp SNAP_DATE) {
        this.SNAP_DATE = SNAP_DATE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BDPE1", getBDPE1())
            .append("BDPE2", getBDPE2())
            .append("SNAP_DATE", getSNAP_DATE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_BDS650PK) ) return false;
        TBPRD_BDS650PK castOther = (TBPRD_BDS650PK) other;
        return new EqualsBuilder()
            .append(this.getBDPE1(), castOther.getBDPE1())
            .append(this.getBDPE2(), castOther.getBDPE2())
            .append(this.getSNAP_DATE(), castOther.getSNAP_DATE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBDPE1())
            .append(getBDPE2())
            .append(getSNAP_DATE())
            .toHashCode();
    }

}
