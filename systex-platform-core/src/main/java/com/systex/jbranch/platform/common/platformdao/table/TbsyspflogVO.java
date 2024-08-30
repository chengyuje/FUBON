package com.systex.jbranch.platform.common.platformdao.table;


import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @author Hibernate CodeGenerator */
public class TbsyspflogVO extends VOBase {

    /** identifier field */
    private Long logindex;

    /** nullable persistent field */
    private String location;

    /** nullable persistent field */
    private String level;

    /** nullable persistent field */
    private String clazz;

    /** nullable persistent field */
    private String method;

    /** nullable persistent field */
    private String linenumber;

    /** nullable persistent field */
    private String branchid;

    /** nullable persistent field */
    private String oribranchid;

    /** nullable persistent field */
    private String workstationid;

    /** nullable persistent field */
    private String sectionid;

    /** nullable persistent field */
    private byte[] teller;

    /** nullable persistent field */
    private String data1;

    /** nullable persistent field */
    private String data2;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyspflog";
    public String getTableuid(){
    	return TABLE_UID;
    }
   
    /** full constructor */
    public TbsyspflogVO(Long logindex, String location, String level, String clazz, String method, String linenumber, String branchid, String oribranchid, String workstationid, String sectionid, byte[] teller, String data1, String data2, Timestamp createtime, String creator, Timestamp lastupdate, String modifier) {
        this.logindex = logindex;
        this.location = location;
        this.level = level;
        this.clazz = clazz;
        this.method = method;
        this.linenumber = linenumber;
        this.branchid = branchid;
        this.oribranchid = oribranchid;
        this.workstationid = workstationid;
        this.sectionid = sectionid;
        this.teller = teller;
        this.data1 = data1;
        this.data2 = data2;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
    }

    /** default constructor */
    public TbsyspflogVO() {
    }

    /** minimal constructor */
    public TbsyspflogVO(Long logindex) {
        this.logindex = logindex;
    }

    public Long getLogindex() {
        return this.logindex;
    }

    public void setLogindex(Long logindex) {
        this.logindex = logindex;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getLinenumber() {
        return this.linenumber;
    }

    public void setLinenumber(String linenumber) {
        this.linenumber = linenumber;
    }

    public String getBranchid() {
        return this.branchid;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public String getOribranchid() {
        return this.oribranchid;
    }

    public void setOribranchid(String oribranchid) {
        this.oribranchid = oribranchid;
    }

    public String getWorkstationid() {
        return this.workstationid;
    }

    public void setWorkstationid(String workstationid) {
        this.workstationid = workstationid;
    }

    public String getSectionid() {
        return this.sectionid;
    }

    public void setSectionid(String sectionid) {
        this.sectionid = sectionid;
    }

    public byte[] getTeller() {
        return this.teller;
    }

    public void setTeller(byte[] teller) {
        this.teller = teller;
    }

    public String getData1() {
        return this.data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return this.data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }


    public String toString() {
        return new ToStringBuilder(this)
            .append("logindex", getLogindex())
            .toString();
    }

}
