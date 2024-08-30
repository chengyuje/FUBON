package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsyssecuitemfunVO extends VOBase {

    /** identifier field */
    private String functionid;

    /** persistent field */
    private String name;

    /** persistent field */
    private String description;

    /** persistent field */
    private String apply;

  
    /** persistent field */
    //private Set tbsyssecufunitemas;
    
    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecuitemfun";
    public String getTableuid(){
    	return TABLE_UID;
    }

    /** full constructor */
    public TbsyssecuitemfunVO(String functionid, String name, String description, String apply, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.functionid = functionid;
        this.name = name;
        this.description = description;
        this.apply = apply;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
        //this.tbsyssecufunitemas = tbsyssecufunitemas;
    }

    /** default constructor */
    public TbsyssecuitemfunVO() {
    }

    /** minimal constructor */
    public TbsyssecuitemfunVO(String functionid, String name, String description, String apply) {
        this.functionid = functionid;
        this.name = name;
        this.description = description;
        this.apply = apply;
        //this.tbsyssecufunitemas = tbsyssecufunitemas;
    }

    public String getFunctionid() {
        return this.functionid;
    }

    public void setFunctionid(String functionid) {
        this.functionid = functionid;
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

//    public Set getTbsyssecufunitemas() {
//        return this.tbsyssecufunitemas;
//    }
//
//    public void setTbsyssecufunitemas(Set tbsyssecufunitemas) {
//        this.tbsyssecufunitemas = tbsyssecufunitemas;
//    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("functionid", getFunctionid())
            .toString();
    }

}
