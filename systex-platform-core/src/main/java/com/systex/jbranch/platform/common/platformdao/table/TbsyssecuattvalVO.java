package com.systex.jbranch.platform.common.platformdao.table;


import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsyssecuattvalVO extends VOBase {

    /** identifier field */
    private String attributeid;

    /** nullable persistent field */
    private String value;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String extend1;

    /** nullable persistent field */
    private String extend2;

    /** nullable persistent field */
    private String extend3;

    /** persistent field */
    private String pattributeid;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecuattval";
    public String getTableuid(){
    	return TABLE_UID;
    }
   
    /** full constructor */
    public TbsyssecuattvalVO(String attributeid, String value, String description, String extend1, String extend2, String extend3, String pattributeid, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.attributeid = attributeid;
        this.value = value;
        this.description = description;
        this.extend1 = extend1;
        this.extend2 = extend2;
        this.extend3 = extend3;
        this.pattributeid = pattributeid;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsyssecuattvalVO() {
    }

    /** minimal constructor */
    public TbsyssecuattvalVO(String attributeid, String pattributeid) {
        this.attributeid = attributeid;
        this.pattributeid = pattributeid;
    }

    public String getAttributeid() {
        return this.attributeid;
    }

    public void setAttributeid(String attributeid) {
        this.attributeid = attributeid;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtend1() {
        return this.extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return this.extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return this.extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    public String getPattributeid() {
        return this.pattributeid;
    }

    public void setPattributeid(String pattributeid) {
        this.pattributeid = pattributeid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("attributeid", getAttributeid())
            .toString();
    }

}
