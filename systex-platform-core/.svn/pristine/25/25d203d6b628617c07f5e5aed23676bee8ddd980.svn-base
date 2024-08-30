package com.systex.jbranch.platform.common.platformdao.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

import java.sql.Timestamp;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class TbsysuserVO extends VOBase {

    /** identifier field */
    private String tellerid;

    /** persistent field */
    private String password;

    /** persistent field */
    private String name;

    /** persistent field */
    private String brchid;

    /** persistent field */
    private String status;

    /** persistent field */
    private String useflag;

    /** persistent field */
    private short sessnum;

    /** persistent field */
    private String roleid;

    /** persistent field */
    private String errlevel;

    private Set<TBSYSSECUROLEVO> roles;

    private TbsysorgVO org;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysuser";
    public String getTableuid(){
    	return TABLE_UID;
    }

    /** full constructor */
    public TbsysuserVO(String tellerid, String password, String name, String brchid, String status, String useflag, short sessnum, String roleid, String errlevel, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.tellerid = tellerid;
        this.password = password;
        this.name = name;
        this.brchid = brchid;
        this.status = status;
        this.useflag = useflag;
        this.sessnum = sessnum;
        this.roleid = roleid;
        this.errlevel = errlevel;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsysuserVO() {
    }

    /** minimal constructor */
    public TbsysuserVO(String tellerid, String password, String name, String brchid, String status, String useflag, short sessnum, String roleid, String errlevel) {
        this.tellerid = tellerid;
        this.password = password;
        this.name = name;
        this.brchid = brchid;
        this.status = status;
        this.useflag = useflag;
        this.sessnum = sessnum;
        this.roleid = roleid;
        this.errlevel = errlevel;
    }

    public String getTellerid() {
        return this.tellerid;
    }

    public void setTellerid(String tellerid) {
        this.tellerid = tellerid;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrchid() {
        return this.org != null ? this.org.getDivNo() : null;
    }

    public void setBrchid(String brchid) {
        this.brchid = brchid;
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

    public short getSessnum() {
        return this.sessnum;
    }

    public void setSessnum(short sessnum) {
        this.sessnum = sessnum;
    }

    public String getRoleid() {
        String id;
        if (roles != null && !roles.isEmpty()) {
            id = roles.iterator().next().getROLEID();
        }
        else {
            id = null;
        }
        return id;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getErrlevel() {
        return this.errlevel;
    }

    public void setErrlevel(String errlevel) {
        this.errlevel = errlevel;
    }

    public Set<TBSYSSECUROLEVO> getRoles() {
        return roles;
    }

    public void setRoles(Set<TBSYSSECUROLEVO> roles) {
        this.roles = roles;
    }

    public TbsysorgVO getOrg() {
        return org;
    }

    public void setOrg(TbsysorgVO org) {
        this.org = org;
    }

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysuserVO{" +
                "tellerid='" + tellerid + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", brchid='" + brchid + '\'' +
                ", status='" + status + '\'' +
                ", useflag='" + useflag + '\'' +
                ", sessnum=" + sessnum +
                ", errlevel='" + errlevel + '\'' +
                '}';
    }

}
