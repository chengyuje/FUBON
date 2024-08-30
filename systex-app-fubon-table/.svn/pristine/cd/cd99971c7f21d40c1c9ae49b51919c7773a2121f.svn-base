package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_PORTFOLIO_PLAN_FILE_LOGVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE_LOGPK comp_id;

    /** nullable persistent field */
    private String EXEC_TYPE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE_LOG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_PORTFOLIO_PLAN_FILE_LOGVO(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE_LOGPK comp_id, String EXEC_TYPE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.EXEC_TYPE = EXEC_TYPE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_PORTFOLIO_PLAN_FILE_LOGVO() {
    }

    /** minimal constructor */
    public TBFPS_PORTFOLIO_PLAN_FILE_LOGVO(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE_LOGPK comp_id, Timestamp createtime, String creator) {
        this.comp_id = comp_id;
        this.createtime = createtime;
        this.creator = creator;
    }

    public com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE_LOGPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE_LOGPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getEXEC_TYPE() {
        return this.EXEC_TYPE;
    }

    public void setEXEC_TYPE(String EXEC_TYPE) {
        this.EXEC_TYPE = EXEC_TYPE;
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
        if ( !(other instanceof TBFPS_PORTFOLIO_PLAN_FILE_LOGVO) ) return false;
        TBFPS_PORTFOLIO_PLAN_FILE_LOGVO castOther = (TBFPS_PORTFOLIO_PLAN_FILE_LOGVO) other;
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
