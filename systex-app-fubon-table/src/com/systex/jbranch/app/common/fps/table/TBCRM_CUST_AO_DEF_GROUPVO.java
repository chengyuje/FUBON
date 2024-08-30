package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_AO_DEF_GROUPVO extends VOBase {

    /** identifier field */
    private String GROUP_ID;

    /** nullable persistent field */
    private String GROUP_NAME;

    /** nullable persistent field */
    private String AO_CODE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AO_DEF_GROUP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_AO_DEF_GROUPVO(String GROUP_ID, String GROUP_NAME, String AO_CODE, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.GROUP_ID = GROUP_ID;
        this.GROUP_NAME = GROUP_NAME;
        this.AO_CODE = AO_CODE;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_AO_DEF_GROUPVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_AO_DEF_GROUPVO(String GROUP_ID) {
        this.GROUP_ID = GROUP_ID;
    }

    public String getGROUP_ID() {
        return this.GROUP_ID;
    }

    public void setGROUP_ID(String GROUP_ID) {
        this.GROUP_ID = GROUP_ID;
    }

    public String getGROUP_NAME() {
        return this.GROUP_NAME;
    }

    public void setGROUP_NAME(String GROUP_NAME) {
        this.GROUP_NAME = GROUP_NAME;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("GROUP_ID", getGROUP_ID())
            .toString();
    }

}
