package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSSCHDQUERYMASTERVO extends VOBase {

    /** identifier field */
    private Long QUERYID;

    /** persistent field */
    private String PROCESSOR;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSSCHDQUERYMASTER";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSSCHDQUERYMASTERVO(String PROCESSOR, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.PROCESSOR = PROCESSOR;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSSCHDQUERYMASTERVO() {
    }

    /** minimal constructor */
    public TBSYSSCHDQUERYMASTERVO(String PROCESSOR) {
        this.PROCESSOR = PROCESSOR;
    }

    public Long getQUERYID() {
        return this.QUERYID;
    }

    public void setQUERYID(Long QUERYID) {
        this.QUERYID = QUERYID;
    }

    public String getPROCESSOR() {
        return this.PROCESSOR;
    }

    public void setPROCESSOR(String PROCESSOR) {
        this.PROCESSOR = PROCESSOR;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("QUERYID", getQUERYID())
            .toString();
    }

}
