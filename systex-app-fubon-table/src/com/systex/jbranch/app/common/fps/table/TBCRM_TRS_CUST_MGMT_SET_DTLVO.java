package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_CUST_MGMT_SET_DTLVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET_DTLPK comp_id;

    /** nullable persistent field */
    private BigDecimal CUST_NO_FLEX_PRCNT;

    /** nullable persistent field */
    private BigDecimal CUST_NO_LIMIT_UP;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET_DTL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_CUST_MGMT_SET_DTLVO(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET_DTLPK comp_id, BigDecimal CUST_NO_FLEX_PRCNT, BigDecimal CUST_NO_LIMIT_UP) {
        this.comp_id = comp_id;
        this.CUST_NO_FLEX_PRCNT = CUST_NO_FLEX_PRCNT;
        this.CUST_NO_LIMIT_UP = CUST_NO_LIMIT_UP;
    }

    /** default constructor */
    public TBCRM_TRS_CUST_MGMT_SET_DTLVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_CUST_MGMT_SET_DTLVO(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET_DTLPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET_DTLPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET_DTLPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getCUST_NO_FLEX_PRCNT() {
        return this.CUST_NO_FLEX_PRCNT;
    }

    public void setCUST_NO_FLEX_PRCNT(BigDecimal CUST_NO_FLEX_PRCNT) {
        this.CUST_NO_FLEX_PRCNT = CUST_NO_FLEX_PRCNT;
    }

    public BigDecimal getCUST_NO_LIMIT_UP() {
        return this.CUST_NO_LIMIT_UP;
    }

    public void setCUST_NO_LIMIT_UP(BigDecimal CUST_NO_LIMIT_UP) {
        this.CUST_NO_LIMIT_UP = CUST_NO_LIMIT_UP;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_TRS_CUST_MGMT_SET_DTLVO) ) return false;
        TBCRM_TRS_CUST_MGMT_SET_DTLVO castOther = (TBCRM_TRS_CUST_MGMT_SET_DTLVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
