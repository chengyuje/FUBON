package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_UNPLAN_RPT_DTLVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_UNPLAN_RPT_DTLPK comp_id;

    /** nullable persistent field */
    private String DETAIL_TYPE;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private BigDecimal DEPOSIT_AMT;

    /** nullable persistent field */
    private BigDecimal BOND_AMT;

    /** nullable persistent field */
    private BigDecimal NEW_INS_CF;

    /** nullable persistent field */
    private BigDecimal INTEREST_INS_CF;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_UNPLAN_RPT_DTL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_UNPLAN_RPT_DTLVO(com.systex.jbranch.app.common.fps.table.TBPMS_UNPLAN_RPT_DTLPK comp_id, String DETAIL_TYPE, String CUST_NAME, BigDecimal DEPOSIT_AMT, BigDecimal BOND_AMT, BigDecimal NEW_INS_CF, BigDecimal INTEREST_INS_CF, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.DETAIL_TYPE = DETAIL_TYPE;
        this.CUST_NAME = CUST_NAME;
        this.DEPOSIT_AMT = DEPOSIT_AMT;
        this.BOND_AMT = BOND_AMT;
        this.NEW_INS_CF = NEW_INS_CF;
        this.INTEREST_INS_CF = INTEREST_INS_CF;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_UNPLAN_RPT_DTLVO() {
    }

    /** minimal constructor */
    public TBPMS_UNPLAN_RPT_DTLVO(com.systex.jbranch.app.common.fps.table.TBPMS_UNPLAN_RPT_DTLPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_UNPLAN_RPT_DTLPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_UNPLAN_RPT_DTLPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getDETAIL_TYPE() {
        return this.DETAIL_TYPE;
    }

    public void setDETAIL_TYPE(String DETAIL_TYPE) {
        this.DETAIL_TYPE = DETAIL_TYPE;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public BigDecimal getDEPOSIT_AMT() {
        return this.DEPOSIT_AMT;
    }

    public void setDEPOSIT_AMT(BigDecimal DEPOSIT_AMT) {
        this.DEPOSIT_AMT = DEPOSIT_AMT;
    }

    public BigDecimal getBOND_AMT() {
        return this.BOND_AMT;
    }

    public void setBOND_AMT(BigDecimal BOND_AMT) {
        this.BOND_AMT = BOND_AMT;
    }

    public BigDecimal getNEW_INS_CF() {
        return this.NEW_INS_CF;
    }

    public void setNEW_INS_CF(BigDecimal NEW_INS_CF) {
        this.NEW_INS_CF = NEW_INS_CF;
    }

    public BigDecimal getINTEREST_INS_CF() {
        return this.INTEREST_INS_CF;
    }

    public void setINTEREST_INS_CF(BigDecimal INTEREST_INS_CF) {
        this.INTEREST_INS_CF = INTEREST_INS_CF;
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
        if ( !(other instanceof TBPMS_UNPLAN_RPT_DTLVO) ) return false;
        TBPMS_UNPLAN_RPT_DTLVO castOther = (TBPMS_UNPLAN_RPT_DTLVO) other;
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
