package com.systex.jbranch.platform.common.security.privilege.vo;

import com.systex.jbranch.platform.common.platformdao.table.TbsysuserVO;

/**
 * @author Alex Lin
 * @version 2010/01/25 4:38:58 PM
 */
public class UserVO {
// ------------------------------ FIELDS ------------------------------

    private String tellerId;
    private String name;
    private String status;
    private String useflag;
    private OrgVO branch;

// --------------------------- CONSTRUCTORS ---------------------------

    public UserVO() {
    }

    public UserVO(TbsysuserVO vo) {
        this(vo.getTellerid(), vo.getName(), vo.getStatus(), vo.getUseflag());
    }

    public UserVO(String tellerId, String name, String status, String useflag) {
        this.tellerId = tellerId;
        this.name = name;
        this.status = status;
        this.useflag = useflag;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public OrgVO getBranch() {
        return branch;
    }

    public void setBranch(OrgVO branch) {
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTellerId() {
        return tellerId;
    }

    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }

    public String getUseflag() {
        return useflag;
    }

    public void setUseflag(String useflag) {
        this.useflag = useflag;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.security.privilege.vo.UserVO{" +
                "tellerId='" + tellerId + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", useflag='" + useflag + '\'' +
                ", branch=" + branch +
                '}';
    }
}
