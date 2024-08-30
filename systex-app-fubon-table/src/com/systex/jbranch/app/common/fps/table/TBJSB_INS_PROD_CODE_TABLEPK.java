package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_CODE_TABLEPK  implements Serializable  {

    /** identifier field */
    private String CD_INDEX;

    /** identifier field */
    private String CD_ITEM;

    /** full constructor */
    public TBJSB_INS_PROD_CODE_TABLEPK(String CD_INDEX, String CD_ITEM) {
        this.CD_INDEX = CD_INDEX;
        this.CD_ITEM = CD_ITEM;
    }

    /** default constructor */
    public TBJSB_INS_PROD_CODE_TABLEPK() {
    }

    public String getCD_INDEX() {
        return this.CD_INDEX;
    }

    public void setCD_INDEX(String CD_INDEX) {
        this.CD_INDEX = CD_INDEX;
    }

    public String getCD_ITEM() {
        return this.CD_ITEM;
    }

    public void setCD_ITEM(String CD_ITEM) {
        this.CD_ITEM = CD_ITEM;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CD_INDEX", getCD_INDEX())
            .append("CD_ITEM", getCD_ITEM())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBJSB_INS_PROD_CODE_TABLEPK) ) return false;
        TBJSB_INS_PROD_CODE_TABLEPK castOther = (TBJSB_INS_PROD_CODE_TABLEPK) other;
        return new EqualsBuilder()
            .append(this.getCD_INDEX(), castOther.getCD_INDEX())
            .append(this.getCD_ITEM(), castOther.getCD_ITEM())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCD_INDEX())
            .append(getCD_ITEM())
            .toHashCode();
    }

}
