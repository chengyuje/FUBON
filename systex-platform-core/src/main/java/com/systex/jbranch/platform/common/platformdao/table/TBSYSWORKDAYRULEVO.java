package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSWORKDAYRULEVO extends VOBase {

    /** identifier field */
    private String RULE_ID;

    /** persistent field */
    private String CALENDAR_PROVIDER_ID;

    /** nullable persistent field */
    private String BASE_CALENDAR_ID;

    /** nullable persistent field */
    private String DESCRIPTION;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSWORKDAYRULE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSWORKDAYRULEVO(String RULE_ID, String CALENDAR_PROVIDER_ID, String BASE_CALENDAR_ID, String DESCRIPTION, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.RULE_ID = RULE_ID;
        this.CALENDAR_PROVIDER_ID = CALENDAR_PROVIDER_ID;
        this.BASE_CALENDAR_ID = BASE_CALENDAR_ID;
        this.DESCRIPTION = DESCRIPTION;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSWORKDAYRULEVO() {
    }

    /** minimal constructor */
    public TBSYSWORKDAYRULEVO(String RULE_ID, String CALENDAR_PROVIDER_ID) {
        this.RULE_ID = RULE_ID;
        this.CALENDAR_PROVIDER_ID = CALENDAR_PROVIDER_ID;
    }

    public String getRULE_ID() {
        return this.RULE_ID;
    }

    public void setRULE_ID(String RULE_ID) {
        this.RULE_ID = RULE_ID;
    }

    public String getCALENDAR_PROVIDER_ID() {
        return this.CALENDAR_PROVIDER_ID;
    }

    public void setCALENDAR_PROVIDER_ID(String CALENDAR_PROVIDER_ID) {
        this.CALENDAR_PROVIDER_ID = CALENDAR_PROVIDER_ID;
    }

    public String getBASE_CALENDAR_ID() {
        return this.BASE_CALENDAR_ID;
    }

    public void setBASE_CALENDAR_ID(String BASE_CALENDAR_ID) {
        this.BASE_CALENDAR_ID = BASE_CALENDAR_ID;
    }

    public String getDESCRIPTION() {
        return this.DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("RULE_ID", getRULE_ID())
            .toString();
    }

}
