package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsyssecupriVO extends VOBase {

    /** identifier field */
    private String privilegeid;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    
    /** persistent field */
    //private Set tbsyssecupriattasses;
    
    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecupri";
    public String getTableuid(){
    	return TABLE_UID;
    }

    /** full constructor */
    public TbsyssecupriVO(String privilegeid, String name, String description, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.privilegeid = privilegeid;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
        //this.tbsyssecupriattasses = tbsyssecupriattasses;
    }

    /** default constructor */
    public TbsyssecupriVO() {
    }

    /** minimal constructor */
    public TbsyssecupriVO(String privilegeid, String name) {
        this.privilegeid = privilegeid;
        this.name = name;
        //this.tbsyssecupriattasses = tbsyssecupriattasses;
    }

    public String getPrivilegeid() {
        return this.privilegeid;
    }

    public void setPrivilegeid(String privilegeid) {
        this.privilegeid = privilegeid;
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

  
  
//    public Set getTbsyssecupriattasses() {
//        return this.tbsyssecupriattasses;
//    }
//
//    public void setTbsyssecupriattasses(Set tbsyssecupriattasses) {
//        this.tbsyssecupriattasses = tbsyssecupriattasses;
//    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("privilegeid", getPrivilegeid())
            .toString();
    }

}
