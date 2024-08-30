package com.systex.jbranch.platform.common.security.privilege.vo;

import java.util.Set;

/**
 * @author Alex Lin
 * @version 2010/01/25 4:40:07 PM
 */
public class ProjectVO {
// ------------------------------ FIELDS ------------------------------

    private String projectId;
    private String projectName;
    private String description;
    private Set<UserVO> users;
    private Set<OrgVO> orgs;

// --------------------------- CONSTRUCTORS ---------------------------

    public ProjectVO() {
    }

    public ProjectVO(String projectId, String projectName, String description) {
        this.projectId = projectId;
        this.description = description;
        this.projectName = projectName;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<OrgVO> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<OrgVO> orgs) {
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

    public Set<UserVO> getUsers() {
        return users;
    }

    public void setUsers(Set<UserVO> users) {
        this.users = users;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.security.privilege.vo.ProjectVO{" +
                "description='" + description + '\'' +
                ", projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", users=" + users +
                ", orgs=" + orgs +
                '}';
    }
}
