package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_SUGGESTPK  implements Serializable  {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** identifier field */
    private BigDecimal PARA_NO;

    /** identifier field */
    private String SUGGEST_TYPE;

    /** full constructor */
    public TBPRD_INS_SUGGESTPK(BigDecimal KEY_NO, BigDecimal PARA_NO, String SUGGEST_TYPE) {
        this.KEY_NO = KEY_NO;
        this.PARA_NO = PARA_NO;
        this.SUGGEST_TYPE = SUGGEST_TYPE;
    }

    /** default constructor */
    public TBPRD_INS_SUGGESTPK() {
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public BigDecimal getPARA_NO() {
        return this.PARA_NO;
    }

    public void setPARA_NO(BigDecimal PARA_NO) {
        this.PARA_NO = PARA_NO;
    }

    public String getSUGGEST_TYPE() {
        return this.SUGGEST_TYPE;
    }

    public void setSUGGEST_TYPE(String SUGGEST_TYPE) {
        this.SUGGEST_TYPE = SUGGEST_TYPE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO", getKEY_NO())
            .append("PARA_NO", getPARA_NO())
            .append("SUGGEST_TYPE", getSUGGEST_TYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPRD_INS_SUGGESTPK) ) return false;
        TBPRD_INS_SUGGESTPK castOther = (TBPRD_INS_SUGGESTPK) other;
        return new EqualsBuilder()
            .append(this.getKEY_NO(), castOther.getKEY_NO())
            .append(this.getPARA_NO(), castOther.getPARA_NO())
            .append(this.getSUGGEST_TYPE(), castOther.getSUGGEST_TYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getKEY_NO())
            .append(getPARA_NO())
            .append(getSUGGEST_TYPE())
            .toHashCode();
    }

}
