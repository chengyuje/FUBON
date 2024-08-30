package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsyssecuitmoduassVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsyssecuitmoduassPK comp_id;

  
    /** nullable persistent field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsyssecumodVO tbsyssecumod;

    /** nullable persistent field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsyssecumoduitemVO tbsyssecumoduitem;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecuitmoduass";
    public String getTableuid(){
    	return TABLE_UID;
    }
    
    /** full constructor */
    public TbsyssecuitmoduassVO(com.systex.jbranch.platform.common.platformdao.table.TbsyssecuitmoduassPK comp_id, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version, com.systex.jbranch.platform.common.platformdao.table.TbsyssecumodVO tbsyssecumod, com.systex.jbranch.platform.common.platformdao.table.TbsyssecumoduitemVO tbsyssecumoduitem) {
        this.comp_id = comp_id;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
        this.tbsyssecumod = tbsyssecumod;
        this.tbsyssecumoduitem = tbsyssecumoduitem;
    }

    /** default constructor */
    public TbsyssecuitmoduassVO() {
    }

    /** minimal constructor */
    public TbsyssecuitmoduassVO(com.systex.jbranch.platform.common.platformdao.table.TbsyssecuitmoduassPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsyssecuitmoduassPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(com.systex.jbranch.platform.common.platformdao.table.TbsyssecuitmoduassPK comp_id) {
        this.comp_id = comp_id;
    }

 
    public com.systex.jbranch.platform.common.platformdao.table.TbsyssecumodVO getTbsyssecumod() {
        return this.tbsyssecumod;
    }

    public void setTbsyssecumod(com.systex.jbranch.platform.common.platformdao.table.TbsyssecumodVO tbsyssecumod) {
        this.tbsyssecumod = tbsyssecumod;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsyssecumoduitemVO getTbsyssecumoduitem() {
        return this.tbsyssecumoduitem;
    }

    public void setTbsyssecumoduitem(com.systex.jbranch.platform.common.platformdao.table.TbsyssecumoduitemVO tbsyssecumoduitem) {
        this.tbsyssecumoduitem = tbsyssecumoduitem;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecuitmoduassVO) ) return false;
        TbsyssecuitmoduassVO castOther = (TbsyssecuitmoduassVO) other;
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
