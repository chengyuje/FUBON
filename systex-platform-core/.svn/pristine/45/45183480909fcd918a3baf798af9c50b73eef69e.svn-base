package com.systex.jbranch.platform.common.platformdao.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

import java.util.Set;

/**
 * @author Alex Lin
 * @version 2010/01/20 10:52:28 AM
 */
public class TbsysorgVO extends VOBase {
// ------------------------------ FIELDS ------------------------------

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysorg";
    private String divNo;
    private String divName;
    private String description;
    private Set<TbsysuserVO> users;
    private Set<TbsysorgattrVO> attributes;
    private TbsysorgVO parent;
    private Set<TbsysorgVO> children;

// --------------------------- CONSTRUCTORS ---------------------------

    public TbsysorgVO() {
    }

    public TbsysorgVO(String divNo, String divName, String description) {
        this.description = description;
        this.divName = divName;
        this.divNo = divNo;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public String getTableuid() {
        return TABLE_UID;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Set<TbsysorgattrVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<TbsysorgattrVO> attributes) {
        this.attributes = attributes;
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

    public TbsysorgVO getParent() {
        return parent;
    }

    public void setParent(TbsysorgVO parent) {
        this.parent = parent;
    }

    public Set<TbsysuserVO> getUsers() {
        return users;
    }

    public void setUsers(Set<TbsysuserVO> users) {
        this.users = users;
    }

    public Set<TbsysorgVO> getChildren() {
        return children;
    }

    public void setChildren(Set<TbsysorgVO> children) {
        this.children = children;
    }

    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TbsysorgVO that = (TbsysorgVO) o;

        if (divNo != null ? !divNo.equals(that.divNo) : that.divNo != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return divNo != null ? divNo.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysorgVO{" +
                "attributes=" + attributes +
                ", divNo='" + divNo + '\'' +
                ", divName='" + divName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
