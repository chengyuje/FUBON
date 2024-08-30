package com.systex.jbranch.platform.common.security.privilege.vo;

import com.systex.jbranch.platform.common.platformdao.table.TbsysorgVO;

import java.util.List;
import java.util.Set;

/**
 * @author Alex Lin
 * @version 2010/01/20 4:41:54 PM
 */
public class OrgVO {
// ------------------------------ FIELDS ------------------------------

    private String divNo;
    private String divName;
    private String description;
    private Set<UserVO> users;
    private Set<OrgAttrVO> attributes;
    private List<OrgVO> children;

// --------------------------- CONSTRUCTORS ---------------------------

    public OrgVO() {
    }

    public OrgVO(TbsysorgVO vo) {
        this(vo.getDivNo(), vo.getDivName(), vo.getDescription());
    }

    public OrgVO(String divNo, String divName, String description) {
        this.divNo = divNo;
        this.divName = divName;
        this.description = description;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Set<OrgAttrVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<OrgAttrVO> attributes) {
        this.attributes = attributes;
    }

    public List<OrgVO> getChildren() {
        return children;
    }

    public void setChildren(List<OrgVO> children) {
        this.children = children;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDivName() {
        return divName;
    }

    public void setDivName(String divName) {
        this.divName = divName;
    }

    public String getDivNo() {
        return divNo;
    }

    public void setDivNo(String divNo) {
        this.divNo = divNo;
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
        return "com.systex.jbranch.platform.common.security.privilege.vo.OrgVO{" +
                "divNo='" + divNo + '\'' +
                ", divName='" + divName + '\'' +
                ", description='" + description + '\'' +
                ", users=" + users +
                ", attributes=" + attributes +
                ", children=" + children +
                '}';
    }
}
