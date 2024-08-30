package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @author Hibernate CodeGenerator */
public class TbsyssecucryparaVO extends VOBase {

    /** identifier field */
    private String parameterkey;

    /** persistent field */
    private String parametervalue;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecucrypara";
    public String getTableuid(){
    	return TABLE_UID;
    }
   
    /** full constructor */
    public TbsyssecucryparaVO(String parameterkey, String parametervalue, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.parameterkey = parameterkey;
        this.parametervalue = parametervalue;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsyssecucryparaVO() {
    }

    /** minimal constructor */
    public TbsyssecucryparaVO(String parameterkey, String parametervalue) {
        this.parameterkey = parameterkey;
        this.parametervalue = parametervalue;
    }

    public String getParameterkey() {
        return this.parameterkey;
    }

    public void setParameterkey(String parameterkey) {
        this.parameterkey = parameterkey;
    }

    public String getParametervalue() {
        return this.parametervalue;
    }

    public void setParametervalue(String parametervalue) {
        this.parametervalue = parametervalue;
    }


    public String toString() {
        return new ToStringBuilder(this)
            .append("parameterkey", getParameterkey())
            .toString();
    }

}
