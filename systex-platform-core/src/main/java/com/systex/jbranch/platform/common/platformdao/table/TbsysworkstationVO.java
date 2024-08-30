package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsysworkstationVO extends VOBase {

    /** identifier field */
    private String ip;

    /** persistent field */
    private String brchid;

    /** persistent field */
    private String wsid;

    /** persistent field */
    private String status;

    /** persistent field */
    private String useflag;

    /** persistent field */
    private String tellerid;

    /** persistent field */
    private String signonbrchid;

    /** persistent field */
    private String type;
    
    /** persistent field */
    private String mac_address;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysworkstation";
    public String getTableuid(){
    	return TABLE_UID;
    }
    
    /** full constructor */
    public TbsysworkstationVO(String ip, String brchid, String wsid, String status, String useflag, String tellerid, String signonbrchid, String type, String mac_address, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.ip = ip;
        this.brchid = brchid;
        this.wsid = wsid;
        this.status = status;
        this.useflag = useflag;
        this.tellerid = tellerid;
        this.signonbrchid = signonbrchid;
        this.type = type;
        this.mac_address = mac_address;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsysworkstationVO() {
    }

    /** minimal constructor */
    public TbsysworkstationVO(String ip, String brchid, String wsid, String status, String useflag, String tellerid, String signonbrchid, String type, String mac_address) {
        this.ip = ip;
        this.brchid = brchid;
        this.wsid = wsid;
        this.status = status;
        this.useflag = useflag;
        this.tellerid = tellerid;
        this.signonbrchid = signonbrchid;
        this.type = type;
        this.mac_address = mac_address;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrchid() {
        return this.brchid;
    }

    public void setBrchid(String brchid) {
        this.brchid = brchid;
    }

    public String getWsid() {
        return this.wsid;
    }

    public void setWsid(String wsid) {
        this.wsid = wsid;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUseflag() {
        return this.useflag;
    }

    public void setUseflag(String useflag) {
        this.useflag = useflag;
    }

    public String getTellerid() {
        return this.tellerid;
    }

    public void setTellerid(String tellerid) {
        this.tellerid = tellerid;
    }

    public String getSignonbrchid() {
        return this.signonbrchid;
    }

    public void setSignonbrchid(String signonbrchid) {
        this.signonbrchid = signonbrchid;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
	 * @return the mac_address
	 */
	public String getMac_address() {
		return mac_address;
	}

	/**
	 * @param mac_address the mac_address to set
	 */
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TbsysworkstationVO [ip=" + ip + ", brchid=" + brchid
				+ ", wsid=" + wsid + ", status=" + status + ", useflag="
				+ useflag + ", tellerid=" + tellerid + ", signonbrchid="
				+ signonbrchid + ", type=" + type + ", mac_address="
				+ mac_address + "]";
	}


}
