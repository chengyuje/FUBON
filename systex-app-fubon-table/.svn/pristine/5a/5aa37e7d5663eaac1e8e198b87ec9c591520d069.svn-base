package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_APPLY_DETAILPK  implements Serializable  {

    /** identifier field */
    private String APPLY_SEQ;

    /** identifier field */
    private String GIFT_SEQ;

    /** full constructor */
    public TBMGM_APPLY_DETAILPK(String APPLY_SEQ, String GIFT_SEQ) {
        this.APPLY_SEQ = APPLY_SEQ;
        this.GIFT_SEQ = GIFT_SEQ;
    }

    /** default constructor */
    public TBMGM_APPLY_DETAILPK() {
    }

    public String getAPPLY_SEQ() {
        return this.APPLY_SEQ;
    }

    public void setAPPLY_SEQ(String APPLY_SEQ) {
        this.APPLY_SEQ = APPLY_SEQ;
    }

    public String getGIFT_SEQ() {
        return this.GIFT_SEQ;
    }

    public void setGIFT_SEQ(String GIFT_SEQ) {
        this.GIFT_SEQ = GIFT_SEQ;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("APPLY_SEQ", getAPPLY_SEQ())
            .append("GIFT_SEQ", getGIFT_SEQ())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBMGM_APPLY_DETAILPK) ) return false;
        TBMGM_APPLY_DETAILPK castOther = (TBMGM_APPLY_DETAILPK) other;
        return new EqualsBuilder()
            .append(this.getAPPLY_SEQ(), castOther.getAPPLY_SEQ())
            .append(this.getGIFT_SEQ(), castOther.getGIFT_SEQ())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAPPLY_SEQ())
            .append(getGIFT_SEQ())
            .toHashCode();
    }

}
