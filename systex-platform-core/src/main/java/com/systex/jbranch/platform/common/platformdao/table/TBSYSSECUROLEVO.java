package com.systex.jbranch.platform.common.platformdao.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

import java.sql.Timestamp;
import java.util.Set;


/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSYSSECUROLEVO extends VOBase {

    /** identifier field */
    private String ROLEID;

    /** persistent field */
    private String NAME;

    /** nullable persistent field */
    private String DESCRIPTION;

    /** nullable persistent field */
    private String EXTEND1;

    /** nullable persistent field */
    private String EXTEND2;

    /** nullable persistent field */
    private String EXTEND3;

    private Set<TbsysuserVO> users;

    private Set<TbsyssecuroleattrVO> attributes;


    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLE";


    public String getTableuid() {
        return TABLE_UID;
    }

    /** full constructor */
    public TBSYSSECUROLEVO(String ROLEID, String NAME, String DESCRIPTION, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String EXTEND1, String EXTEND2, String EXTEND3, Long version) {
        this.ROLEID = ROLEID;
        this.NAME = NAME;
        this.DESCRIPTION = DESCRIPTION;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.EXTEND1 = EXTEND1;
        this.EXTEND2 = EXTEND2;
        this.EXTEND3 = EXTEND3;
        this.version = version;
    }

    /** default constructor */
    public TBSYSSECUROLEVO() {
    }

    /** minimal constructor */
    public TBSYSSECUROLEVO(String ROLEID, String NAME) {
        this.ROLEID = ROLEID;
        this.NAME = NAME;
    }

    public String getROLEID() {
        return this.ROLEID;
    }

    public void setROLEID(String ROLEID) {
        this.ROLEID = ROLEID;
    }

    public String getNAME() {
        return this.NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getDESCRIPTION() {
        return this.DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getEXTEND1() {
        return this.EXTEND1;
    }

    public void setEXTEND1(String EXTEND1) {
        this.EXTEND1 = EXTEND1;
    }

    public String getEXTEND2() {
        return this.EXTEND2;
    }

    public void setEXTEND2(String EXTEND2) {
        this.EXTEND2 = EXTEND2;
    }

    public String getEXTEND3() {
        return this.EXTEND3;
    }

    public void setEXTEND3(String EXTEND3) {
        this.EXTEND3 = EXTEND3;
    }

    public Set<TbsysuserVO> getUsers() {
        return users;
    }

    public void setUsers(Set<TbsysuserVO> users) {
        this.users = users;
    }

    public Set<TbsyssecuroleattrVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<TbsyssecuroleattrVO> attributes) {
        this.attributes = attributes;
    }

    public void checkDefaultValue() {
    }

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO{" +
                "ROLEID='" + ROLEID + '\'' +
                ", NAME='" + NAME + '\'' +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                ", EXTEND1='" + EXTEND1 + '\'' +
                ", EXTEND2='" + EXTEND2 + '\'' +
                ", EXTEND3='" + EXTEND3 + '\'' +
                '}';
    }

}
