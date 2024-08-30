package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSAPSERVERSTATUSVO extends VOBase {

    /** identifier field */
    private String APSVRNAME;

    /** persistent field */
    private String STATUS;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSAPSERVERSTATUS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSAPSERVERSTATUSVO(String APSVRNAME, String STATUS, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.APSVRNAME = APSVRNAME;
        this.STATUS = STATUS;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSAPSERVERSTATUSVO() {
    }

    /** minimal constructor */
    public TBSYSAPSERVERSTATUSVO(String APSVRNAME, String STATUS) {
        this.APSVRNAME = APSVRNAME;
        this.STATUS = STATUS;
    }

    public String getAPSVRNAME() {
        return this.APSVRNAME;
    }

    public void setAPSVRNAME(String APSVRNAME) {
        this.APSVRNAME = APSVRNAME;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("APSVRNAME", getAPSVRNAME())
            .toString();
    }

}
