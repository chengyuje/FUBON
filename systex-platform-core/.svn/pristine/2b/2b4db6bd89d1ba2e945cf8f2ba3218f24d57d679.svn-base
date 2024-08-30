package com.systex.jbranch.platform.common.platformdao.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

import java.util.Set;

/**
 * @author Alex Lin
 * @version 2010/01/25 4:26:52 PM
 */
public class TbsysprojectVO extends VOBase {
// ------------------------------ FIELDS ------------------------------

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysproject";
    private String projectId;
    private String projectName;
    private String description;
    private Set<TbsysuserVO> users;
    private Set<TbsysorgVO> orgs;

// --------------------------- CONSTRUCTORS ---------------------------

    public TbsysprojectVO() {
    }

    public TbsysprojectVO(String projectId, String projectName, String description) {
        this.description = description;
        this.projectId = projectId;
        this.projectName = projectName;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public String getTableuid() {
        return TABLE_UID;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TbsysorgVO> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<TbsysorgVO> orgs) {
        this.orgs = orgs;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Set<TbsysuserVO> getUsers() {
        return users;
    }

    public void setUsers(Set<TbsysuserVO> users) {
        this.users = users;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysprojectVO{" +
                "description='" + description + '\'' +
                ", projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", users=" + users +
                ", orgs=" + orgs +
                '}';
    }
}
