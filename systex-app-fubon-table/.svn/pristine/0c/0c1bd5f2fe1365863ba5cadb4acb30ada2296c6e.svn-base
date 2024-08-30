package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_GROUPVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPPK comp_id;

    /** nullable persistent field */
    private String GROUP_NAME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_GROUPVO(com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPPK comp_id, String GROUP_NAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.GROUP_NAME = GROUP_NAME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_GROUPVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_GROUPVO(com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getGROUP_NAME() {
        return this.GROUP_NAME;
    }

    public void setGROUP_NAME(String GROUP_NAME) {
        this.GROUP_NAME = GROUP_NAME;
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
        if ( !(other instanceof TBCRM_CUST_GROUPVO) ) return false;
        TBCRM_CUST_GROUPVO castOther = (TBCRM_CUST_GROUPVO) other;
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
