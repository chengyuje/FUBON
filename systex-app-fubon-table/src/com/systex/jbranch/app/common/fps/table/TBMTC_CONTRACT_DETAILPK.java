package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMTC_CONTRACT_DETAILPK  implements Serializable  {

    /** identifier field */
    private String CON_NO;

    /** identifier field */
    private String REL_TYPE;

    /** full constructor */
    public TBMTC_CONTRACT_DETAILPK(String CON_NO, String REL_TYPE) {
        this.CON_NO = CON_NO;
        this.REL_TYPE = REL_TYPE;
    }

    /** default constructor */
    public TBMTC_CONTRACT_DETAILPK() {
    }

    public String getCON_NO() {
        return this.CON_NO;
    }

    public void setCON_NO(String CON_NO) {
        this.CON_NO = CON_NO;
    }

    public String getREL_TYPE() {
        return this.REL_TYPE;
    }

    public void setREL_TYPE(String REL_TYPE) {
        this.REL_TYPE = REL_TYPE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CON_NO", getCON_NO())
            .append("REL_TYPE", getREL_TYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBMTC_CONTRACT_DETAILPK) ) return false;
        TBMTC_CONTRACT_DETAILPK castOther = (TBMTC_CONTRACT_DETAILPK) other;
        return new EqualsBuilder()
            .append(this.getCON_NO(), castOther.getCON_NO())
            .append(this.getREL_TYPE(), castOther.getREL_TYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCON_NO())
            .append(getREL_TYPE())
            .toHashCode();
    }

}
