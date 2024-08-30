package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_ACTIVITY_GIFTPK  implements Serializable  {

    /** identifier field */
    private String ACT_SEQ;

    /** identifier field */
    private String GIFT_SEQ;

    /** full constructor */
    public TBMGM_ACTIVITY_GIFTPK(String ACT_SEQ, String GIFT_SEQ) {
        this.ACT_SEQ = ACT_SEQ;
        this.GIFT_SEQ = GIFT_SEQ;
    }

    /** default constructor */
    public TBMGM_ACTIVITY_GIFTPK() {
    }

    public String getACT_SEQ() {
        return this.ACT_SEQ;
    }

    public void setACT_SEQ(String ACT_SEQ) {
        this.ACT_SEQ = ACT_SEQ;
    }

    public String getGIFT_SEQ() {
        return this.GIFT_SEQ;
    }

    public void setGIFT_SEQ(String GIFT_SEQ) {
        this.GIFT_SEQ = GIFT_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ACT_SEQ", getACT_SEQ())
            .append("GIFT_SEQ", getGIFT_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBMGM_ACTIVITY_GIFTPK) ) return false;
        TBMGM_ACTIVITY_GIFTPK castOther = (TBMGM_ACTIVITY_GIFTPK) other;
        return new EqualsBuilder()
            .append(this.getACT_SEQ(), castOther.getACT_SEQ())
            .append(this.getGIFT_SEQ(), castOther.getGIFT_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getACT_SEQ())
            .append(getGIFT_SEQ())
            .toHashCode();
    }

}
