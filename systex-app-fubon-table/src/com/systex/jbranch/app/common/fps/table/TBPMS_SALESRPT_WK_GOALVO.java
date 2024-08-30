package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_SALESRPT_WK_GOALVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_SALESRPT_WK_GOALPK comp_id;

    /** nullable persistent field */
    private BigDecimal WK_GOAL_DEP;
    
    /** nullable persistent field */
    private BigDecimal WK_GOAL_INV;
    
    /** nullable persistent field */
    private BigDecimal WK_GOAL_EIP;

public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_SALESRPT_WK_GOAL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_SALESRPT_WK_GOALVO(com.systex.jbranch.app.common.fps.table.TBPMS_SALESRPT_WK_GOALPK comp_id, String EMP_NAME, BigDecimal SERVICE_YEARS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.WK_GOAL_DEP = WK_GOAL_DEP;
        this.WK_GOAL_INV = WK_GOAL_INV;
        this.WK_GOAL_EIP = WK_GOAL_EIP;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_SALESRPT_WK_GOALVO() {
    }

    /** minimal constructor */
    public TBPMS_SALESRPT_WK_GOALVO(com.systex.jbranch.app.common.fps.table.TBPMS_SALESRPT_WK_GOALPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_SALESRPT_WK_GOALPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_SALESRPT_WK_GOALPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getWK_GOAL_DEP() {
		return WK_GOAL_DEP;
	}

	public void setWK_GOAL_DEP(BigDecimal wK_GOAL_DEP) {
		WK_GOAL_DEP = wK_GOAL_DEP;
	}

	public BigDecimal getWK_GOAL_INV() {
		return WK_GOAL_INV;
	}

	public void setWK_GOAL_INV(BigDecimal wK_GOAL_INV) {
		WK_GOAL_INV = wK_GOAL_INV;
	}

	public BigDecimal getWK_GOAL_EIP() {
		return WK_GOAL_EIP;
	}

	public void setWK_GOAL_EIP(BigDecimal wK_GOAL_EIP) {
		WK_GOAL_EIP = wK_GOAL_EIP;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_SALESRPT_WK_GOALVO) ) return false;
        TBPMS_SALESRPT_WK_GOALVO castOther = (TBPMS_SALESRPT_WK_GOALVO) other;
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
