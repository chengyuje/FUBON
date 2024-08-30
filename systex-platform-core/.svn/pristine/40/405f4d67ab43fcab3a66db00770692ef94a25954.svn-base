package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsyssecumodVO extends VOBase {

    /** identifier field */
    private String moduleid;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String apply;

  
    /** persistent field */
    //private Set tbsyssecuitmoduasses;
    
    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecumod";
    public String getTableuid(){
    	return TABLE_UID;
    }

    /** full constructor */
    public TbsyssecumodVO(String moduleid, String name, String description, String apply, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.moduleid = moduleid;
        this.name = name;
        this.description = description;
        this.apply = apply;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
        //this.tbsyssecuitmoduasses = tbsyssecuitmoduasses;
    }

    /** default constructor */
    public TbsyssecumodVO() {
    }

    /** minimal constructor */
    public TbsyssecumodVO(String moduleid, String name, String apply) {
        this.moduleid = moduleid;
        this.name = name;
        this.apply = apply;
        //this.tbsyssecuitmoduasses = tbsyssecuitmoduasses;
    }

    public String getModuleid() {
        return this.moduleid;
    }

    public void setModuleid(String moduleid) {
        this.moduleid = moduleid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

//    public Set getTbsyssecuitmoduasses() {
//        return this.tbsyssecuitmoduasses;
//    }
//
//    public void setTbsyssecuitmoduasses(Set tbsyssecuitmoduasses) {
//        this.tbsyssecuitmoduasses = tbsyssecuitmoduasses;
//    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("moduleid", getModuleid())
            .toString();
    }

}
