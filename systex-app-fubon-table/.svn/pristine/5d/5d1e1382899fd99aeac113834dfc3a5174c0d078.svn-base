package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_PRJ_ROTATION_MPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String PRJ_ID;

    /** identifier field */
    private String BRANCH_NBR;
    
    /** identifier field */
    private String EMP_ID;

    /** full constructor */
    public TBCRM_TRS_PRJ_ROTATION_MPK(String PRJ_ID, String BRANCH_NBR, String EMP_ID) {
        this.PRJ_ID = PRJ_ID;
        this.BRANCH_NBR = BRANCH_NBR;
        this.EMP_ID = EMP_ID;
    }

    /** default constructor */
    public TBCRM_TRS_PRJ_ROTATION_MPK() {
    }

    public String getPRJ_ID() {
		return PRJ_ID;
	}

	public void setPRJ_ID(String pRJ_ID) {
		PRJ_ID = pRJ_ID;
	}

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("PRJ_ID", getPRJ_ID())
            .append("BRANCH_NBR", getBRANCH_NBR())
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_TRS_PRJ_ROTATION_MPK) ) return false;
        TBCRM_TRS_PRJ_ROTATION_MPK caseother = (TBCRM_TRS_PRJ_ROTATION_MPK) other;
        return new EqualsBuilder()
            .append(this.getPRJ_ID(), caseother.getPRJ_ID())
            .append(this.getBRANCH_NBR(), caseother.getBRANCH_NBR())
            .append(this.getEMP_ID(), caseother.getEMP_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPRJ_ID())
            .append(getBRANCH_NBR())
            .append(getEMP_ID())
            .toHashCode();
    }

}
