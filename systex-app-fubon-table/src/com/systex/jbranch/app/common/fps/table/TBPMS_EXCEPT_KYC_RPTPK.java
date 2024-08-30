package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_EXCEPT_KYC_RPTPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String CUST_ID;

    /** identifier field */
    private String DATA_YEARMON;

    /** identifier field */
    private String PROFILE_TEST_ID;

    /** identifier field */
    private String ROWNAME1;

    /** full constructor */
    public TBPMS_EXCEPT_KYC_RPTPK(String BRANCH_NBR, String CUST_ID, String DATA_YEARMON, String PROFILE_TEST_ID, String ROWNAME1) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.CUST_ID = CUST_ID;
        this.DATA_YEARMON = DATA_YEARMON;
        this.PROFILE_TEST_ID = PROFILE_TEST_ID;
        this.ROWNAME1 = ROWNAME1;
    }

    /** default constructor */
    public TBPMS_EXCEPT_KYC_RPTPK() {
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getDATA_YEARMON() {
        return this.DATA_YEARMON;
    }

    public void setDATA_YEARMON(String DATA_YEARMON) {
        this.DATA_YEARMON = DATA_YEARMON;
    }

    public String getPROFILE_TEST_ID() {
        return this.PROFILE_TEST_ID;
    }

    public void setPROFILE_TEST_ID(String PROFILE_TEST_ID) {
        this.PROFILE_TEST_ID = PROFILE_TEST_ID;
    }

    public String getROWNAME1() {
        return this.ROWNAME1;
    }

    public void setROWNAME1(String ROWNAME1) {
        this.ROWNAME1 = ROWNAME1;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("CUST_ID", getCUST_ID())
            .append("DATA_YEARMON", getDATA_YEARMON())
            .append("PROFILE_TEST_ID", getPROFILE_TEST_ID())
            .append("ROWNAME1", getROWNAME1())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_EXCEPT_KYC_RPTPK) ) return false;
        TBPMS_EXCEPT_KYC_RPTPK castOther = (TBPMS_EXCEPT_KYC_RPTPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getDATA_YEARMON(), castOther.getDATA_YEARMON())
            .append(this.getPROFILE_TEST_ID(), castOther.getPROFILE_TEST_ID())
            .append(this.getROWNAME1(), castOther.getROWNAME1())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getCUST_ID())
            .append(getDATA_YEARMON())
            .append(getPROFILE_TEST_ID())
            .append(getROWNAME1())
            .toHashCode();
    }

}
