package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSREMOTEHOSTVO extends VOBase {

    /** identifier field */
    private String HOSTID;

    /** nullable persistent field */
    private String IP;

    /** nullable persistent field */
    private BigDecimal PORT;

    /** nullable persistent field */
    private String USERNAME;

    /** nullable persistent field */
    private String PASSWORD;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSREMOTEHOSTVO(String HOSTID, String IP, BigDecimal PORT, String USERNAME, String PASSWORD, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.HOSTID = HOSTID;
        this.IP = IP;
        this.PORT = PORT;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSREMOTEHOSTVO() {
    }

    /** minimal constructor */
    public TBSYSREMOTEHOSTVO(String HOSTID) {
        this.HOSTID = HOSTID;
    }

    public String getHOSTID() {
        return this.HOSTID;
    }

    public void setHOSTID(String HOSTID) {
        this.HOSTID = HOSTID;
    }

    public String getIP() {
        return this.IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public BigDecimal getPORT() {
        return this.PORT;
    }

    public void setPORT(BigDecimal PORT) {
        this.PORT = PORT;
    }

    public String getUSERNAME() {
        return this.USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return this.PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("HOSTID", getHOSTID())
            .toString();
    }

}
