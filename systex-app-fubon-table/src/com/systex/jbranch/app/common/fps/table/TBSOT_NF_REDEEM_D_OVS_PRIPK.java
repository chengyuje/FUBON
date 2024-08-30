package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSOT_NF_REDEEM_D_OVS_PRIPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private BigDecimal PRD_SEQ_NO;

    /** identifier field */
    private String BATCH_SEQ;

    /** full constructor */
    public TBSOT_NF_REDEEM_D_OVS_PRIPK(BigDecimal PRD_SEQ_NO, String BATCH_SEQ) {
        this.PRD_SEQ_NO = PRD_SEQ_NO;
        this.BATCH_SEQ = BATCH_SEQ;
    }

    /** default constructor */
    public TBSOT_NF_REDEEM_D_OVS_PRIPK() {
    }

    public BigDecimal getPRD_SEQ_NO() {
		return PRD_SEQ_NO;
	}

	public void setPRD_SEQ_NO(BigDecimal pRD_SEQ_NO) {
		PRD_SEQ_NO = pRD_SEQ_NO;
	}

	public String getBATCH_SEQ() {
		return BATCH_SEQ;
	}

	public void setBATCH_SEQ(String bATCH_SEQ) {
		BATCH_SEQ = bATCH_SEQ;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("PRD_SEQ_NO", getPRD_SEQ_NO())
            .append("BATCH_SEQ", getBATCH_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSOT_NF_REDEEM_D_OVS_PRIPK) ) return false;
        TBSOT_NF_REDEEM_D_OVS_PRIPK castOther = (TBSOT_NF_REDEEM_D_OVS_PRIPK) other;
        return new EqualsBuilder()
            .append(this.getPRD_SEQ_NO(), castOther.getPRD_SEQ_NO())
            .append(this.getBATCH_SEQ(), castOther.getBATCH_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRD_SEQ_NO())
            .append(getBATCH_SEQ())
            .toHashCode();
    }

}
