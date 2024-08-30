package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysschdjobclassVO extends VOBase {

    /** identifier field */
    private String classid;

    /** nullable persistent field */
    private String classname;
    
    private String beanname;

public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysschdjobclass";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysschdjobclassVO(String classid, String classname, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.classid = classid;
        this.classname = classname;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TbsysschdjobclassVO() {
    }

    /** minimal constructor */
    public TbsysschdjobclassVO(String classid) {
        this.classid = classid;
    }

    public String getclassid() {
        return this.classid;
    }

    public void setclassid(String classid) {
        this.classid = classid;
    }

    public String getclassname() {
        return this.classname;
    }

    public void setclassname(String classname) {
        this.classname = classname;
    }

    public void checkDefaultValue() {
    }

    
    
    public String getBeanname() {
		return beanname;
	}

	public void setBeanname(String beanname) {
		this.beanname = beanname;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("classid", getclassid())
            .toString();
    }

}
