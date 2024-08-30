package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_ACTIVITY_GIFTVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_GIFTPK comp_id;

    /** nullable persistent field */
    private BigDecimal GIFT_POINTS;

    /** nullable persistent field */
    private BigDecimal GIFT_REWARD;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_GIFT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_ACTIVITY_GIFTVO(com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_GIFTPK comp_id, BigDecimal GIFT_POINTS, BigDecimal GIFT_REWARD, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.GIFT_POINTS = GIFT_POINTS;
        this.GIFT_REWARD = GIFT_REWARD;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_ACTIVITY_GIFTVO() {
    }

    /** minimal constructor */
    public TBMGM_ACTIVITY_GIFTVO(com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_GIFTPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_GIFTPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_GIFTPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getGIFT_POINTS() {
        return this.GIFT_POINTS;
    }

    public void setGIFT_POINTS(BigDecimal GIFT_POINTS) {
        this.GIFT_POINTS = GIFT_POINTS;
    }

    public BigDecimal getGIFT_REWARD() {
        return this.GIFT_REWARD;
    }

    public void setGIFT_REWARD(BigDecimal GIFT_REWARD) {
        this.GIFT_REWARD = GIFT_REWARD;
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
        if ( !(other instanceof TBMGM_ACTIVITY_GIFTVO) ) return false;
        TBMGM_ACTIVITY_GIFTVO castOther = (TBMGM_ACTIVITY_GIFTVO) other;
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
