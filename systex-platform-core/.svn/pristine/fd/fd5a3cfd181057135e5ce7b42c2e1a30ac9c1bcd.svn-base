package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @author Hibernate CodeGenerator */
public class TbsyssecpriassassoVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsyssecpriassassoPK comp_id;

  
    /** nullable persistent field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsyssecuassVO tbsyssecuass;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecpriassasso";
    public String getTableuid(){
    	return TABLE_UID;
    }
    
    /** full constructor */
    public TbsyssecpriassassoVO(com.systex.jbranch.platform.common.platformdao.table.TbsyssecpriassassoPK comp_id, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version, com.systex.jbranch.platform.common.platformdao.table.TbsyssecuassVO tbsyssecuass) {
        this.comp_id = comp_id;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
        this.tbsyssecuass = tbsyssecuass;
    }

    /** default constructor */
    public TbsyssecpriassassoVO() {
    }

    /** minimal constructor */
    public TbsyssecpriassassoVO(com.systex.jbranch.platform.common.platformdao.table.TbsyssecpriassassoPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsyssecpriassassoPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(com.systex.jbranch.platform.common.platformdao.table.TbsyssecpriassassoPK comp_id) {
        this.comp_id = comp_id;
    }


    public com.systex.jbranch.platform.common.platformdao.table.TbsyssecuassVO getTbsyssecuass() {
        return this.tbsyssecuass;
    }

    public void setTbsyssecuass(com.systex.jbranch.platform.common.platformdao.table.TbsyssecuassVO tbsyssecuass) {
        this.tbsyssecuass = tbsyssecuass;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecpriassassoVO) ) return false;
        TbsyssecpriassassoVO castOther = (TbsyssecpriassassoVO) other;
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
