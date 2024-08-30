package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsyssecupriattassVO extends VOBase {

    /** identifier field */
    private long filterid;

    /** persistent field */
    private String pattributeid;

    /** persistent field */
    private String privilegeid;

    /** persistent field */
    private String value;

    /** persistent field */
    private String flag;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecupriattass";
    public String getTableuid(){
    	return TABLE_UID;
    }

    /** full constructor */
    public TbsyssecupriattassVO(long filterid, String pattributeid, String privilegeid, String value, String flag, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.filterid = filterid;
        this.pattributeid = pattributeid;
        this.privilegeid = privilegeid;
        this.value = value;
        this.flag = flag;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsyssecupriattassVO() {
    }

    /** minimal constructor */
    public TbsyssecupriattassVO(long filterid, String pattributeid, String privilegeid, String value, String flag) {
        this.filterid = filterid;
        this.pattributeid = pattributeid;
        this.privilegeid = privilegeid;
        this.value = value;
        this.flag = flag;
    }

    public long getFilterid() {
        return this.filterid;
    }

    public void setFilterid(long filterid) {
        this.filterid = filterid;
    }

    public String getPattributeid() {
        return this.pattributeid;
    }

    public void setPattributeid(String pattributeid) {
        this.pattributeid = pattributeid;
    }

    public String getPrivilegeid() {
        return this.privilegeid;
    }

    public void setPrivilegeid(String privilegeid) {
        this.privilegeid = privilegeid;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFlag() {
        return this.flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

  
    public String toString() {
        return new ToStringBuilder(this)
            .append("filterid", getFilterid())
            .toString();
    }

}
