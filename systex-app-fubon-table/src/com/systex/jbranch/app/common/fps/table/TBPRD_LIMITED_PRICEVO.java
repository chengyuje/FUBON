package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_LIMITED_PRICEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICEPK comp_id;

    /** nullable persistent field */
    private BigDecimal LIMITED_PRICE;

    /** nullable persistent field */
    private BigDecimal CHANNEL_FEE;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_LIMITED_PRICEVO(com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICEPK comp_id, BigDecimal LIMITED_PRICE, BigDecimal CHANNEL_FEE, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.LIMITED_PRICE = LIMITED_PRICE;
        this.CHANNEL_FEE = CHANNEL_FEE;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_LIMITED_PRICEVO() {
    }

    /** minimal constructor */
    public TBPRD_LIMITED_PRICEVO(com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICEPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICEPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getLIMITED_PRICE() {
        return this.LIMITED_PRICE;
    }

    public void setLIMITED_PRICE(BigDecimal LIMITED_PRICE) {
        this.LIMITED_PRICE = LIMITED_PRICE;
    }

    public BigDecimal getCHANNEL_FEE() {
        return this.CHANNEL_FEE;
    }

    public void setCHANNEL_FEE(BigDecimal CHANNEL_FEE) {
        this.CHANNEL_FEE = CHANNEL_FEE;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getREVIEW_STATUS() {
        return this.REVIEW_STATUS;
    }

    public void setREVIEW_STATUS(String REVIEW_STATUS) {
        this.REVIEW_STATUS = REVIEW_STATUS;
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
        if ( !(other instanceof TBPRD_LIMITED_PRICEVO) ) return false;
        TBPRD_LIMITED_PRICEVO castOther = (TBPRD_LIMITED_PRICEVO) other;
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
