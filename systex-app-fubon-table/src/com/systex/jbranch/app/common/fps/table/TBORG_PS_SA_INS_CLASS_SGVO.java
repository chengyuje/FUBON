package com.systex.jbranch.app.common.fps.table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_PS_SA_INS_CLASS_SGVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_CLASS_SGPK comp_id;

    /** persistent field */
    private String TNDDATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_CLASS_SG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_PS_SA_INS_CLASS_SGVO(com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_CLASS_SGPK comp_id, String TNDDATE) {
        this.comp_id = comp_id;
        this.TNDDATE = TNDDATE;
    }

    /** default constructor */
    public TBORG_PS_SA_INS_CLASS_SGVO() {
    }

    public com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_CLASS_SGPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_CLASS_SGPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTNDDATE() {
        return this.TNDDATE;
    }

    public void setTNDDATE(String TNDDATE) {
        this.TNDDATE = TNDDATE;
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
        if ( !(other instanceof TBORG_PS_SA_INS_CLASS_SGVO) ) return false;
        TBORG_PS_SA_INS_CLASS_SGVO castOther = (TBORG_PS_SA_INS_CLASS_SGVO) other;
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
