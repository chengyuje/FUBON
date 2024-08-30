package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_FUND_TRAININGVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPRD_FUND_TRAININGPK comp_id;

    /** nullable persistent field */
    private Timestamp REG_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_FUND_TRAINING";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_FUND_TRAININGVO(com.systex.jbranch.app.common.fps.table.TBPRD_FUND_TRAININGPK comp_id, Timestamp REG_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REG_DATE = REG_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_FUND_TRAININGVO() {
    }

    /** minimal constructor */
    public TBPRD_FUND_TRAININGVO(com.systex.jbranch.app.common.fps.table.TBPRD_FUND_TRAININGPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPRD_FUND_TRAININGPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPRD_FUND_TRAININGPK comp_id) {
        this.comp_id = comp_id;
    }

    public Timestamp getREG_DATE() {
        return this.REG_DATE;
    }

    public void setREG_DATE(Timestamp REG_DATE) {
        this.REG_DATE = REG_DATE;
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
        if ( !(other instanceof TBPRD_FUND_TRAININGVO) ) return false;
        TBPRD_FUND_TRAININGVO castOther = (TBPRD_FUND_TRAININGVO) other;
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
