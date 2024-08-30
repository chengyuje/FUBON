package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSOVERPRINTBFASSVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBFASSPK comp_id;

    /** nullable persistent field */
    private Short ORDER_NUM;

    /** nullable persistent field */
    private String ARGUMENTS;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBFASS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSOVERPRINTBFASSVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBFASSPK comp_id, Short ORDER_NUM, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, String ARGUMENTS, Long version) {
        this.comp_id = comp_id;
        this.ORDER_NUM = ORDER_NUM;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.ARGUMENTS = ARGUMENTS;
        this.version = version;
    }

    /** default constructor */
    public TBSYSOVERPRINTBFASSVO() {
    }

    /** minimal constructor */
    public TBSYSOVERPRINTBFASSVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBFASSPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBFASSPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBFASSPK comp_id) {
        this.comp_id = comp_id;
    }

    public Short getORDER_NUM() {
        return this.ORDER_NUM;
    }

    public void setORDER_NUM(Short ORDER_NUM) {
        this.ORDER_NUM = ORDER_NUM;
    }

    public String getARGUMENTS() {
        return this.ARGUMENTS;
    }

    public void setARGUMENTS(String ARGUMENTS) {
        this.ARGUMENTS = ARGUMENTS;
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
        if ( !(other instanceof TBSYSOVERPRINTBFASSVO) ) return false;
        TBSYSOVERPRINTBFASSVO castOther = (TBSYSOVERPRINTBFASSVO) other;
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
