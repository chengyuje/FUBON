package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSDXIMPDETAILVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSDXIMPDETAILPK comp_id;

    /** persistent field */
    private BigDecimal FUNCID;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSDXIMPDETAIL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSDXIMPDETAILVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSDXIMPDETAILPK comp_id, BigDecimal FUNCID, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.FUNCID = FUNCID;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYSDXIMPDETAILVO() {
    }

    /** minimal constructor */
    public TBSYSDXIMPDETAILVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSDXIMPDETAILPK comp_id, BigDecimal FUNCID) {
        this.comp_id = comp_id;
        this.FUNCID = FUNCID;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSDXIMPDETAILPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSDXIMPDETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getFUNCID() {
        return this.FUNCID;
    }

    public void setFUNCID(BigDecimal FUNCID) {
        this.FUNCID = FUNCID;
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
        if ( !(other instanceof TBSYSDXIMPDETAILVO) ) return false;
        TBSYSDXIMPDETAILVO castOther = (TBSYSDXIMPDETAILVO) other;
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
