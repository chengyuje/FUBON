package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_TARGETPK  implements Serializable  {

    /** identifier field */
    private BigDecimal INSURANCECO;

    /** identifier field */
    private String TARGET_CODE;

    /** full constructor */
    public TBJSB_INS_PROD_TARGETPK(BigDecimal INSURANCECO, String TARGET_CODE) {
        this.INSURANCECO = INSURANCECO;
        this.TARGET_CODE = TARGET_CODE;
    }

    /** default constructor */
    public TBJSB_INS_PROD_TARGETPK() {
    }

    public BigDecimal getINSURANCECO() {
        return this.INSURANCECO;
    }

    public void setINSURANCECO(BigDecimal INSURANCECO) {
        this.INSURANCECO = INSURANCECO;
    }

    public String getTARGET_CODE() {
        return this.TARGET_CODE;
    }

    public void setTARGET_CODE(String TARGET_CODE) {
        this.TARGET_CODE = TARGET_CODE;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INSURANCECO", getINSURANCECO())
            .append("TARGET_CODE", getTARGET_CODE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBJSB_INS_PROD_TARGETPK) ) return false;
        TBJSB_INS_PROD_TARGETPK castOther = (TBJSB_INS_PROD_TARGETPK) other;
        return new EqualsBuilder()
            .append(this.getINSURANCECO(), castOther.getINSURANCECO())
            .append(this.getTARGET_CODE(), castOther.getTARGET_CODE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getINSURANCECO())
            .append(getTARGET_CODE())
            .toHashCode();
    }

}
