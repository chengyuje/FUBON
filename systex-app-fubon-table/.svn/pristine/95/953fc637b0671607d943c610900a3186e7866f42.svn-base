package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_TARGET_MAPPK  implements Serializable  {

    /** identifier field */
    private String E_FIELDNAME;

    /** identifier field */
    private BigDecimal INSURANCECOSERIALNUM;

    /** full constructor */
    public TBJSB_INS_PROD_TARGET_MAPPK(String E_FIELDNAME, BigDecimal INSURANCECOSERIALNUM) {
        this.E_FIELDNAME = E_FIELDNAME;
        this.INSURANCECOSERIALNUM = INSURANCECOSERIALNUM;
    }

    /** default constructor */
    public TBJSB_INS_PROD_TARGET_MAPPK() {
    }

    public String getE_FIELDNAME() {
        return this.E_FIELDNAME;
    }

    public void setE_FIELDNAME(String E_FIELDNAME) {
        this.E_FIELDNAME = E_FIELDNAME;
    }

    public BigDecimal getINSURANCECOSERIALNUM() {
        return this.INSURANCECOSERIALNUM;
    }

    public void setINSURANCECOSERIALNUM(BigDecimal INSURANCECOSERIALNUM) {
        this.INSURANCECOSERIALNUM = INSURANCECOSERIALNUM;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("E_FIELDNAME", getE_FIELDNAME())
            .append("INSURANCECOSERIALNUM", getINSURANCECOSERIALNUM())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBJSB_INS_PROD_TARGET_MAPPK) ) return false;
        TBJSB_INS_PROD_TARGET_MAPPK castOther = (TBJSB_INS_PROD_TARGET_MAPPK) other;
        return new EqualsBuilder()
            .append(this.getE_FIELDNAME(), castOther.getE_FIELDNAME())
            .append(this.getINSURANCECOSERIALNUM(), castOther.getINSURANCECOSERIALNUM())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getE_FIELDNAME())
            .append(getINSURANCECOSERIALNUM())
            .toHashCode();
    }

}
