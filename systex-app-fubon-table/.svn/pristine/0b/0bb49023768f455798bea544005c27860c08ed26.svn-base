package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DAILY_SALES_DEPPK  implements Serializable  {

    /** identifier field */
    private String ACC_NBR;

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String CRCY_TYPE;

    /** identifier field */
    private String ID;

    /** identifier field */
    private Timestamp TRADE_DATE;

    /** full constructor */
    public TBPMS_DAILY_SALES_DEPPK(String ACC_NBR, String BRANCH_NBR, String CRCY_TYPE, String ID, Timestamp TRADE_DATE) {
        this.ACC_NBR = ACC_NBR;
        this.BRANCH_NBR = BRANCH_NBR;
        this.CRCY_TYPE = CRCY_TYPE;
        this.ID = ID;
        this.TRADE_DATE = TRADE_DATE;
    }

    /** default constructor */
    public TBPMS_DAILY_SALES_DEPPK() {
    }

    public String getACC_NBR() {
        return this.ACC_NBR;
    }

    public void setACC_NBR(String ACC_NBR) {
        this.ACC_NBR = ACC_NBR;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getCRCY_TYPE() {
        return this.CRCY_TYPE;
    }

    public void setCRCY_TYPE(String CRCY_TYPE) {
        this.CRCY_TYPE = CRCY_TYPE;
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
            .append("ACC_NBR", getACC_NBR())
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("CRCY_TYPE", getCRCY_TYPE())
            .append("ID", getID())
            .append("TRADE_DATE", getTRADE_DATE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_DAILY_SALES_DEPPK) ) return false;
        TBPMS_DAILY_SALES_DEPPK castOther = (TBPMS_DAILY_SALES_DEPPK) other;
        return new EqualsBuilder()
            .append(this.getACC_NBR(), castOther.getACC_NBR())
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getCRCY_TYPE(), castOther.getCRCY_TYPE())
            .append(this.getID(), castOther.getID())
            .append(this.getTRADE_DATE(), castOther.getTRADE_DATE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getACC_NBR())
            .append(getBRANCH_NBR())
            .append(getCRCY_TYPE())
            .append(getID())
            .append(getTRADE_DATE())
            .toHashCode();
    }

}
