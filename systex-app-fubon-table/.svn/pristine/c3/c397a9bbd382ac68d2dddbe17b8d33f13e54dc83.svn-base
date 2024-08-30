package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_BRG_SETUPVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCRM_BRG_SETUPPK comp_id;

    /** nullable persistent field */
    private String ROLE_LIST;

    /** nullable persistent field */
    private BigDecimal DISCOUNT;

    /** nullable persistent field */
    private String DISCOUNT_RNG_TYPE;

public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_BRG_SETUP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_BRG_SETUPVO(com.systex.jbranch.app.common.fps.table.TBCRM_BRG_SETUPPK comp_id, String ROLE_LIST, BigDecimal DISCOUNT, String DISCOUNT_RNG_TYPE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.ROLE_LIST = ROLE_LIST;
        this.DISCOUNT = DISCOUNT;
        this.DISCOUNT_RNG_TYPE = DISCOUNT_RNG_TYPE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_BRG_SETUPVO() {
    }

    /** minimal constructor */
    public TBCRM_BRG_SETUPVO(com.systex.jbranch.app.common.fps.table.TBCRM_BRG_SETUPPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCRM_BRG_SETUPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCRM_BRG_SETUPPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getROLE_LIST() {
        return this.ROLE_LIST;
    }

    public void setROLE_LIST(String ROLE_LIST) {
        this.ROLE_LIST = ROLE_LIST;
    }

    public BigDecimal getDISCOUNT() {
        return this.DISCOUNT;
    }

    public void setDISCOUNT(BigDecimal DISCOUNT) {
        this.DISCOUNT = DISCOUNT;
    }

    public String getDISCOUNT_RNG_TYPE() {
        return this.DISCOUNT_RNG_TYPE;
    }

    public void setDISCOUNT_RNG_TYPE(String DISCOUNT_RNG_TYPE) {
        this.DISCOUNT_RNG_TYPE = DISCOUNT_RNG_TYPE;
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
        if ( !(other instanceof TBCRM_BRG_SETUPVO) ) return false;
        TBCRM_BRG_SETUPVO castOther = (TBCRM_BRG_SETUPVO) other;
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
