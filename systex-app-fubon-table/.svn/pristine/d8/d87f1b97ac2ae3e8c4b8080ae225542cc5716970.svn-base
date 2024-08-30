package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_APPLY_DETAILVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILPK comp_id;

    /** nullable persistent field */
    private BigDecimal APPLY_NUMBER;

    /** nullable persistent field */
    private BigDecimal APPLY_POINTS;

    /** nullable persistent field */
    private BigDecimal APPLY_REWARD;

    /** nullable persistent field */
    private String DELIVERY_STATUS;

    /** nullable persistent field */
    private Timestamp DELIVERY_DATE;

    /** nullable persistent field */
    private Timestamp ORDER_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAIL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_APPLY_DETAILVO(com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILPK comp_id, BigDecimal APPLY_NUMBER, BigDecimal APPLY_POINTS, BigDecimal APPLY_REWARD, String DELIVERY_STATUS, Timestamp DELIVERY_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Timestamp ORDER_DATE, Long version) {
        this.comp_id = comp_id;
        this.APPLY_NUMBER = APPLY_NUMBER;
        this.APPLY_POINTS = APPLY_POINTS;
        this.APPLY_REWARD = APPLY_REWARD;
        this.DELIVERY_STATUS = DELIVERY_STATUS;
        this.DELIVERY_DATE = DELIVERY_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.ORDER_DATE = ORDER_DATE;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_APPLY_DETAILVO() {
    }

    /** minimal constructor */
    public TBMGM_APPLY_DETAILVO(com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getAPPLY_NUMBER() {
        return this.APPLY_NUMBER;
    }

    public void setAPPLY_NUMBER(BigDecimal APPLY_NUMBER) {
        this.APPLY_NUMBER = APPLY_NUMBER;
    }

    public BigDecimal getAPPLY_POINTS() {
        return this.APPLY_POINTS;
    }

    public void setAPPLY_POINTS(BigDecimal APPLY_POINTS) {
        this.APPLY_POINTS = APPLY_POINTS;
    }

    public BigDecimal getAPPLY_REWARD() {
        return this.APPLY_REWARD;
    }

    public void setAPPLY_REWARD(BigDecimal APPLY_REWARD) {
        this.APPLY_REWARD = APPLY_REWARD;
    }

    public String getDELIVERY_STATUS() {
        return this.DELIVERY_STATUS;
    }

    public void setDELIVERY_STATUS(String DELIVERY_STATUS) {
        this.DELIVERY_STATUS = DELIVERY_STATUS;
    }

    public Timestamp getDELIVERY_DATE() {
        return this.DELIVERY_DATE;
    }

    public void setDELIVERY_DATE(Timestamp DELIVERY_DATE) {
        this.DELIVERY_DATE = DELIVERY_DATE;
    }

    public Timestamp getORDER_DATE() {
        return this.ORDER_DATE;
    }

    public void setORDER_DATE(Timestamp ORDER_DATE) {
        this.ORDER_DATE = ORDER_DATE;
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
        if ( !(other instanceof TBMGM_APPLY_DETAILVO) ) return false;
        TBMGM_APPLY_DETAILVO castOther = (TBMGM_APPLY_DETAILVO) other;
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
