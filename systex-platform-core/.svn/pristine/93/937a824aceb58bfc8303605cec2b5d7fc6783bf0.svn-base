package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysschdaddetailVO extends VOBase {

	 /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsysschdaddetailPK comp_id;

    /** nullable persistent field */
    private String message;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysschdaddetail";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysschdaddetailVO(com.systex.jbranch.platform.common.platformdao.table.TbsysschdaddetailPK comp_id,  String message, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
    	this.comp_id = comp_id;
        this.message = message;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsysschdaddetailVO() {
    }

    /** minimal constructor */
    public TbsysschdaddetailVO(com.systex.jbranch.platform.common.platformdao.table.TbsysschdaddetailPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsysschdaddetailPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TbsysschdaddetailPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getmessage() {
        return this.message;
    }

    public void setmessage(String message) {
        this.message = message;
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
        if ( !(other instanceof TbsysschdaddetailVO) ) return false;
        TbsysschdaddetailVO castOther = (TbsysschdaddetailVO) other;
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
