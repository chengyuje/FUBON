package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_CUSTRISK_VOLATILITYVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITYPK comp_id;

    /** nullable persistent field */
    private String VOL_TYPE;

    /** nullable persistent field */
    private BigDecimal VOLATILITY;
    
    /** nullable persistent field */
    private BigDecimal REINV_STOCK_VOL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITY";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_CUSTRISK_VOLATILITYVO(com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITYPK comp_id, String VOL_TYPE, BigDecimal VOLATILITY, BigDecimal REINV_STOCK_VOL, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.VOL_TYPE = VOL_TYPE;
        this.VOLATILITY = VOLATILITY;
        this.REINV_STOCK_VOL = REINV_STOCK_VOL;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_CUSTRISK_VOLATILITYVO() {
    }

    /** minimal constructor */
    public TBFPS_CUSTRISK_VOLATILITYVO(com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITYPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITYPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBFPS_CUSTRISK_VOLATILITYPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getVOL_TYPE() {
        return this.VOL_TYPE;
    }

    public void setVOL_TYPE(String VOL_TYPE) {
        this.VOL_TYPE = VOL_TYPE;
    }

    public BigDecimal getVOLATILITY() {
        return this.VOLATILITY;
    }

    public void setVOLATILITY(BigDecimal VOLATILITY) {
        this.VOLATILITY = VOLATILITY;
    }
    
    public BigDecimal getREINV_STOCK_VOL() {
		return REINV_STOCK_VOL;
	}

	public void setREINV_STOCK_VOL(BigDecimal REINV_STOCK_VOL) {
		this.REINV_STOCK_VOL = REINV_STOCK_VOL;
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
        if ( !(other instanceof TBFPS_CUSTRISK_VOLATILITYVO) ) return false;
        TBFPS_CUSTRISK_VOLATILITYVO castOther = (TBFPS_CUSTRISK_VOLATILITYVO) other;
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
