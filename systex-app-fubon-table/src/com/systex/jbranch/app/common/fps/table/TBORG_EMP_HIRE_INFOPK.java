package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_EMP_HIRE_INFOPK  implements Serializable  {

    /** identifier field */
    private String BRANCH_NBR;

    /** identifier field */
    private String CUST_ID;
    
    /** nullable persistent field */
    private BigDecimal SEQ;

    /** full constructor */
    public TBORG_EMP_HIRE_INFOPK(String BRANCH_NBR, String CUST_ID, BigDecimal SEQ) {
        this.BRANCH_NBR = BRANCH_NBR;
        this.CUST_ID = CUST_ID;
        this.SEQ = SEQ;
    }

    /** default constructor */
    public TBORG_EMP_HIRE_INFOPK() {
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
    
    

    public BigDecimal getSEQ() {
		return SEQ;
	}

	public void setSEQ(BigDecimal sEQ) {
		SEQ = sEQ;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("CUST_ID", getCUST_ID())
            .append("SEQ", getSEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBORG_EMP_HIRE_INFOPK) ) return false;
        TBORG_EMP_HIRE_INFOPK castOther = (TBORG_EMP_HIRE_INFOPK) other;
        return new EqualsBuilder()
            .append(this.getBRANCH_NBR(), castOther.getBRANCH_NBR())
            .append(this.getCUST_ID(), castOther.getCUST_ID())
            .append(this.getSEQ(), castOther.getSEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBRANCH_NBR())
            .append(getCUST_ID())
            .toHashCode();
    }

}
