package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_IPO_RPTPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String AO_CODE;

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String DATA_DATE;

    /** identifier field */
    private String PRD_ID;

    /** identifier field */
    private BigDecimal PRJ_SEQ;

    /** full constructor */
    public TBPMS_IPO_RPTPK(String AO_CODE, String BRANCH_NBR, String DATA_DATE, String PRD_ID, BigDecimal PRJ_SEQ) {
        this.AO_CODE = AO_CODE;
        this.BRANCH_NBR = BRANCH_NBR;
        this.DATA_DATE = DATA_DATE;
        this.PRD_ID = PRD_ID;
        this.PRJ_SEQ = PRJ_SEQ;
    }

    /** default constructor */
    public TBPMS_IPO_RPTPK() {
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(String DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public BigDecimal getPRJ_SEQ() {
        return this.PRJ_SEQ;
    }

    public void setPRJ_SEQ(BigDecimal PRJ_SEQ) {
        this.PRJ_SEQ = PRJ_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AO_CODE", getAO_CODE())
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("DATA_DATE", getDATA_DATE())
            .append("PRD_ID", getPRD_ID())
            .append("PRJ_SEQ", getPRJ_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_IPO_RPTPK) ) return false;
        TBPMS_IPO_RPTPK castOther = (TBPMS_IPO_RPTPK) other;
        return new EqualsBuilder()
            .append(this.getAO_CODE(), castOther.getAO_CODE())
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getDATA_DATE(), castOther.getDATA_DATE())
            .append(this.getPRD_ID(), castOther.getPRD_ID())
            .append(this.getPRJ_SEQ(), castOther.getPRJ_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAO_CODE())
            .append(getBRANCH_NBR())
            .append(getDATA_DATE())
            .append(getPRD_ID())
            .append(getPRJ_SEQ())
            .toHashCode();
    }

}
