package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSDXIMPDETAILPK  implements Serializable  {

    /** identifier field */
    private String IMPORTMASTERID;

    /** identifier field */
    private BigDecimal FIELDNO;

    /** identifier field */
    private BigDecimal ORDER;

    /** full constructor */
    public TBSYSDXIMPDETAILPK(String IMPORTMASTERID, BigDecimal FIELDNO, BigDecimal ORDER) {
        this.IMPORTMASTERID = IMPORTMASTERID;
        this.FIELDNO = FIELDNO;
        this.ORDER = ORDER;
    }

    /** default constructor */
    public TBSYSDXIMPDETAILPK() {
    }

    public String getIMPORTMASTERID() {
        return this.IMPORTMASTERID;
    }

    public void setIMPORTMASTERID(String IMPORTMASTERID) {
        this.IMPORTMASTERID = IMPORTMASTERID;
    }

    public BigDecimal getFIELDNO() {
        return this.FIELDNO;
    }

    public void setFIELDNO(BigDecimal FIELDNO) {
        this.FIELDNO = FIELDNO;
    }

    public BigDecimal getORDER() {
        return this.ORDER;
    }

    public void setORDER(BigDecimal ORDER) {
        this.ORDER = ORDER;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("IMPORTMASTERID", getIMPORTMASTERID())
            .append("FIELDNO", getFIELDNO())
            .append("ORDER", getORDER())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSDXIMPDETAILPK) ) return false;
        TBSYSDXIMPDETAILPK castOther = (TBSYSDXIMPDETAILPK) other;
        return new EqualsBuilder()
            .append(this.getIMPORTMASTERID(), castOther.getIMPORTMASTERID())
            .append(this.getFIELDNO(), castOther.getFIELDNO())
            .append(this.getORDER(), castOther.getORDER())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getIMPORTMASTERID())
            .append(getFIELDNO())
            .append(getORDER())
            .toHashCode();
    }

}
