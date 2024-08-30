package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSI18NVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NPK comp_id;

    /** persistent field */
    private String TEXT;

    /** nullable persistent field */
    private String TYPE;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSI18N";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSI18NVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NPK comp_id, String TEXT, String TYPE, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.TEXT = TEXT;
        this.TYPE = TYPE;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYSI18NVO() {
    }

    /** minimal constructor */
    public TBSYSI18NVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NPK comp_id, String TEXT) {
        this.comp_id = comp_id;
        this.TEXT = TEXT;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTEXT() {
        return this.TEXT;
    }

    public void setTEXT(String TEXT) {
        this.TEXT = TEXT;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
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
        if ( !(other instanceof TBSYSI18NVO) ) return false;
        TBSYSI18NVO castOther = (TBSYSI18NVO) other;
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
