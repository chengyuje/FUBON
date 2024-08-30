package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_REST_DAYPK  implements Serializable  {

    /** identifier field */
    private String PRD_ID;

    /** identifier field */
    private String PTYPE;

    /** identifier field */
    private Timestamp REST_DAY;

    /** identifier field */
    private String STOCK_CODE;

    /** full constructor */
    public TBPRD_REST_DAYPK(String PRD_ID, String PTYPE, Timestamp REST_DAY, String STOCK_CODE) {
        this.PRD_ID = PRD_ID;
        this.PTYPE = PTYPE;
        this.REST_DAY = REST_DAY;
        this.STOCK_CODE = STOCK_CODE;
    }

    /** default constructor */
    public TBPRD_REST_DAYPK() {
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getPTYPE() {
        return this.PTYPE;
    }

    public void setPTYPE(String PTYPE) {
        this.PTYPE = PTYPE;
    }

    public Timestamp getREST_DAY() {
        return this.REST_DAY;
    }

    public void setREST_DAY(Timestamp REST_DAY) {
        this.REST_DAY = REST_DAY;
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
            .append("PTYPE", getPTYPE())
            .append("REST_DAY", getREST_DAY())
            .append("STOCK_CODE", getSTOCK_CODE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_REST_DAYPK) ) return false;
        TBPRD_REST_DAYPK castOther = (TBPRD_REST_DAYPK) other;
        return new EqualsBuilder()
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getPTYPE(), castOther.getPTYPE())
            .append(this.getREST_DAY(), castOther.getREST_DAY())
            .append(this.getSTOCK_CODE(), castOther.getSTOCK_CODE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRD_ID())
            .append(getPTYPE())
            .append(getREST_DAY())
            .append(getSTOCK_CODE())
            .toHashCode();
    }

}
