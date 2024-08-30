package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsyssecumoduitemVO extends VOBase {

    /** identifier field */
    private String itemid;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String apply;

   
    /** persistent field */
    private Set tbsyssecufunitemas;

    /** persistent field */
    private Set tbsyssecuitmoduasses;
    
    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecumoduitem";
    public String getTableuid(){
    	return TABLE_UID;
    }

    /** full constructor */
    public TbsyssecumoduitemVO(String itemid, String name, String description, String apply, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version, Set tbsyssecufunitemas, Set tbsyssecuitmoduasses) {
        this.itemid = itemid;
        this.description = description;
        this.apply = apply;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
        this.tbsyssecufunitemas = tbsyssecufunitemas;
        this.tbsyssecuitmoduasses = tbsyssecuitmoduasses;
    }

    /** default constructor */
    public TbsyssecumoduitemVO() {
    }

    /** minimal constructor */
    public TbsyssecumoduitemVO(String itemid, String name, String apply, Set tbsyssecufunitemas, Set tbsyssecuitmoduasses) {
        this.itemid = itemid;
        this.apply = apply;
        this.tbsyssecufunitemas = tbsyssecufunitemas;
        this.tbsyssecuitmoduasses = tbsyssecuitmoduasses;
    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApply() {
        return this.apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

 
    public Set getTbsyssecufunitemas() {
        return this.tbsyssecufunitemas;
    }

    public void setTbsyssecufunitemas(Set tbsyssecufunitemas) {
        this.tbsyssecufunitemas = tbsyssecufunitemas;
    }

    public Set getTbsyssecuitmoduasses() {
        return this.tbsyssecuitmoduasses;
    }

    public void setTbsyssecuitmoduasses(Set tbsyssecuitmoduasses) {
        this.tbsyssecuitmoduasses = tbsyssecuitmoduasses;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("itemid", getItemid())
            .toString();
    }

}
