package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DAILY_SALES_DEP_NPK  implements Serializable  {

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String ID;

    /** identifier field */
    private Timestamp TRADE_DATE;

    /** full constructor */
    public TBPMS_DAILY_SALES_DEP_NPK(String BRANCH_NBR, String ID, Timestamp TRADE_DATE) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.ID = ID;
        this.TRADE_DATE = TRADE_DATE;
    }

    /** default constructor */
    public TBPMS_DAILY_SALES_DEP_NPK() {
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Timestamp getTRADE_DATE() {
        return this.TRADE_DATE;
    }

    public void setTRADE_DATE(Timestamp TRADE_DATE) {
        this.TRADE_DATE = TRADE_DATE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("ID", getID())
            .append("TRADE_DATE", getTRADE_DATE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_DAILY_SALES_DEP_NPK) ) return false;
        TBPMS_DAILY_SALES_DEP_NPK castOther = (TBPMS_DAILY_SALES_DEP_NPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getID(), castOther.getID())
            .append(this.getTRADE_DATE(), castOther.getTRADE_DATE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getID())
            .append(getTRADE_DATE())
            .toHashCode();
    }

}
