package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_MAPPVIDEO_CHKLISTPK  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** identifier field */
    private String PREMATCH_SEQ;

    /** identifier field */
    private String CHK_STEP;

    /** identifier field */
    private String CHK_CODE;

    /** full constructor */
    public TBIOT_MAPPVIDEO_CHKLISTPK(String PREMATCH_SEQ, String CHK_STEP, String CHK_CODE) {
        this.PREMATCH_SEQ = PREMATCH_SEQ;
        this.CHK_STEP = CHK_STEP;
        this.CHK_CODE = CHK_CODE;
    }

    /** default constructor */
    public TBIOT_MAPPVIDEO_CHKLISTPK() {
    }

    public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}

	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}

	public String getCHK_STEP() {
		return CHK_STEP;
	}

	public void setCHK_STEP(String cHK_STEP) {
		CHK_STEP = cHK_STEP;
	}

	public String getCHK_CODE() {
		return CHK_CODE;
	}

	public void setCHK_CODE(String cHK_CODE) {
		CHK_CODE = cHK_CODE;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("PREMATCH_SEQ", getPREMATCH_SEQ())
            .append("CHK_STEP", getCHK_STEP())
            .append("CHK_CODE", getCHK_CODE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBIOT_MAPPVIDEO_CHKLISTPK) ) return false;
        TBIOT_MAPPVIDEO_CHKLISTPK castOther = (TBIOT_MAPPVIDEO_CHKLISTPK) other;
        return new EqualsBuilder()
            .append(this.getPREMATCH_SEQ(), castOther.getPREMATCH_SEQ())
            .append(this.getCHK_STEP(), castOther.getCHK_STEP())
            .append(this.getCHK_CODE(), castOther.getCHK_CODE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPREMATCH_SEQ())
            .append(getCHK_STEP())
            .append(getCHK_CODE())
            .toHashCode();
    }

}
