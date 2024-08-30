package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @author Hibernate CodeGenerator */
public class TbsyssecurolpriassVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassPK comp_id;

   

    /** nullable persistent field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO tbsyssecurole;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecurolpriass";
    public String getTableuid(){
    	return TABLE_UID;
    }
    
    /** full constructor */
    public TbsyssecurolpriassVO(com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassPK comp_id, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version, com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO tbsyssecurole) {
        this.comp_id = comp_id;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
        this.tbsyssecurole = tbsyssecurole;
    }

    /** default constructor */
    public TbsyssecurolpriassVO() {
    }

    /** minimal constructor */
    public TbsyssecurolpriassVO(com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO getTbsyssecurole() {
        return this.tbsyssecurole;
    }

    public void setTbsyssecurole(com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO tbsyssecurole) {
        this.tbsyssecurole = tbsyssecurole;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsyssecurolpriassVO) ) return false;
        TbsyssecurolpriassVO castOther = (TbsyssecurolpriassVO) other;
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
