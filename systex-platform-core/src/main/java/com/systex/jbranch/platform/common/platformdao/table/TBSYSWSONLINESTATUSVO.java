package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSWSONLINESTATUSVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSPK comp_id;

    /** persistent field */
    private String STATUS;

    /** persistent field */
    private String BRCHID;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSWSONLINESTATUSVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSPK comp_id, String STATUS, String BRCHID, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.comp_id = comp_id;
        this.STATUS = STATUS;
        this.BRCHID = BRCHID;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSWSONLINESTATUSVO() {
    }

    /** minimal constructor */
    public TBSYSWSONLINESTATUSVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSPK comp_id, String STATUS, String BRCHID) {
        this.comp_id = comp_id;
        this.STATUS = STATUS;
        this.BRCHID = BRCHID;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getBRCHID() {
        return this.BRCHID;
    }

    public void setBRCHID(String BRCHID) {
        this.BRCHID = BRCHID;
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
        if ( !(other instanceof TBSYSWSONLINESTATUSVO) ) return false;
        TBSYSWSONLINESTATUSVO castOther = (TBSYSWSONLINESTATUSVO) other;
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
