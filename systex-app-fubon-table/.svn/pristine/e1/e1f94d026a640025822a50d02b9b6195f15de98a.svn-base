package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_PAYMENT_TYPEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPRD_INS_PAYMENT_TYPEPK comp_id;

    /** persistent field */
    private BigDecimal PAY_RATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_PAYMENT_TYPE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_PAYMENT_TYPEVO(com.systex.jbranch.app.common.fps.table.TBPRD_INS_PAYMENT_TYPEPK comp_id, BigDecimal PAY_RATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PAY_RATE = PAY_RATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_PAYMENT_TYPEVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_PAYMENT_TYPEVO(com.systex.jbranch.app.common.fps.table.TBPRD_INS_PAYMENT_TYPEPK comp_id, BigDecimal PAY_RATE) {
        this.comp_id = comp_id;
        this.PAY_RATE = PAY_RATE;
    }

    public com.systex.jbranch.app.common.fps.table.TBPRD_INS_PAYMENT_TYPEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPRD_INS_PAYMENT_TYPEPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getPAY_RATE() {
        return this.PAY_RATE;
    }

    public void setPAY_RATE(BigDecimal PAY_RATE) {
        this.PAY_RATE = PAY_RATE;
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
        if ( !(other instanceof TBPRD_INS_PAYMENT_TYPEVO) ) return false;
        TBPRD_INS_PAYMENT_TYPEVO castOther = (TBPRD_INS_PAYMENT_TYPEVO) other;
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
