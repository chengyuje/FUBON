package com.systex.jbranch.platform.common.platformdao.table;


import java.sql.Timestamp;

import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsyssecuattVO extends VOBase {

    /** identifier field */
    private String pattributeid;

    /** persistent field */
    private String name;

    /** persistent field */
    private String type;

    /** nullable persistent field */
    private String description;

  

    /** persistent field */
    //private Set tbsyssecuattvals;
    
    
    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecuatt";
    public String getTableuid(){
    	return TABLE_UID;
    }

    /** full constructor */
    public TbsyssecuattVO(String pattributeid, String name, String type, String description, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.pattributeid = pattributeid;
        this.name = name;
        this.type = type;
        this.description = description;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
        //this.tbsyssecuattvals = tbsyssecuattvals;
    }

    /** default constructor */
    public TbsyssecuattVO() {
    }

    /** minimal constructor */
    public TbsyssecuattVO(String pattributeid, String name, String type, Set tbsyssecuattvals) {
        this.pattributeid = pattributeid;
        this.name = name;
        this.type = type;
        //this.tbsyssecuattvals = tbsyssecuattvals;
    }

    public String getPattributeid() {
        return this.pattributeid;
    }

    public void setPattributeid(String pattributeid) {
        this.pattributeid = pattributeid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public Set getTbsyssecuattvals() {
//        return this.tbsyssecuattvals;
//    }
//
//    public void setTbsyssecuattvals(Set tbsyssecuattvals) {
//        this.tbsyssecuattvals = tbsyssecuattvals;
//    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("pattributeid", getPattributeid())
            .toString();
    }

}
