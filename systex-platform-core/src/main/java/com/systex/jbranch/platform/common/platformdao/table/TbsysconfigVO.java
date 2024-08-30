package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysconfigVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsysconfigPK comp_id;

    /** persistent field */
    private String value;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysconfig";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysconfigVO(com.systex.jbranch.platform.common.platformdao.table.TbsysconfigPK comp_id, String value, String creator, Timestamp createtime, String modifier, Timestamp lastupdate) {
        this.comp_id = comp_id;
        this.value = value;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
    }

    /** default constructor */
    public TbsysconfigVO() {
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsysconfigPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(com.systex.jbranch.platform.common.platformdao.table.TbsysconfigPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysconfigVO) ) return false;
        TbsysconfigVO castOther = (TbsysconfigVO) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
