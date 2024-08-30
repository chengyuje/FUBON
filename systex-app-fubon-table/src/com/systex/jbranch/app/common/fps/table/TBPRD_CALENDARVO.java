package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_CALENDARVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPRD_CALENDARPK comp_id;

    /** nullable persistent field */
    private BigDecimal DIVIDEND_RATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_CALENDAR";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_CALENDARVO(com.systex.jbranch.app.common.fps.table.TBPRD_CALENDARPK comp_id, BigDecimal DIVIDEND_RATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.DIVIDEND_RATE = DIVIDEND_RATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_CALENDARVO() {
    }

    /** minimal constructor */
    public TBPRD_CALENDARVO(com.systex.jbranch.app.common.fps.table.TBPRD_CALENDARPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPRD_CALENDARPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPRD_CALENDARPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getDIVIDEND_RATE() {
        return this.DIVIDEND_RATE;
    }

    public void setDIVIDEND_RATE(BigDecimal DIVIDEND_RATE) {
        this.DIVIDEND_RATE = DIVIDEND_RATE;
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
        if ( !(other instanceof TBPRD_CALENDARVO) ) return false;
        TBPRD_CALENDARVO castOther = (TBPRD_CALENDARVO) other;
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
