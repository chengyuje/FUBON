package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSREPORTDETAILPK  implements Serializable  {

    /** identifier field */
    private BigDecimal GEN_ID;

    /** identifier field */
    private Integer PAGENO;

    /** identifier field */
    private String BRNO;

    /** full constructor */
    public TBSYSREPORTDETAILPK(BigDecimal GEN_ID, Integer PAGENO, String BRNO) {
        this.GEN_ID = GEN_ID;
        this.PAGENO = PAGENO;
        this.BRNO = BRNO;
    }

    /** default constructor */
    public TBSYSREPORTDETAILPK() {
    }

    public BigDecimal getGEN_ID() {
        return this.GEN_ID;
    }

    public void setGEN_ID(BigDecimal GEN_ID) {
        this.GEN_ID = GEN_ID;
    }

    public Integer getPAGENO() {
        return this.PAGENO;
    }

    public void setPAGENO(Integer PAGENO) {
        this.PAGENO = PAGENO;
    }

    public String getBRNO() {
        return this.BRNO;
    }

    public void setBRNO(String BRNO) {
        this.BRNO = BRNO;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("GEN_ID", getGEN_ID())
            .append("PAGENO", getPAGENO())
            .append("BRNO", getBRNO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSREPORTDETAILPK) ) return false;
        TBSYSREPORTDETAILPK castOther = (TBSYSREPORTDETAILPK) other;
        return new EqualsBuilder()
            .append(this.getGEN_ID(), castOther.getGEN_ID())
            .append(this.getPAGENO(), castOther.getPAGENO())
            .append(this.getBRNO(), castOther.getBRNO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getGEN_ID())
            .append(getPAGENO())
            .append(getBRNO())
            .toHashCode();
    }

}
