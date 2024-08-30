package com.systex.jbranch.platform.common.security.privilege.vo;

import com.systex.jbranch.platform.common.platformdao.table.TbsyssecuroleattrVO;

/**
 * @author Alex Lin
 * @version 2010/01/22 11:30:41 AM
 */
public class RoleAttrVO {
// ------------------------------ FIELDS ------------------------------

    private String roleId;
    private String attrId;
    private String value;
    private String description;

// --------------------------- CONSTRUCTORS ---------------------------

    public RoleAttrVO() {
    }

    public RoleAttrVO(TbsyssecuroleattrVO attrVO) {
        this(attrVO.getCompId().getRoleId(), attrVO.getCompId().getAttrId(), attrVO.getValue(), attrVO.getDescription());
    }

    public RoleAttrVO(String attrId, String value) {
        this.attrId = attrId;
        this.value = value;
    }

    public RoleAttrVO(String attrId, String value, String description) {
        this.attrId = attrId;
        this.value = value;
        this.description = description;
    }

    public RoleAttrVO(String roleId, String attrId, String value, String description) {
        this.attrId = attrId;
        this.roleId = roleId;
        this.value = value;
        this.description = description;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoleAttrVO that = (RoleAttrVO) o;

        if (!attrId.equals(that.attrId)) {
            return false;
        }
        if (!roleId.equals(that.roleId)) {
            return false;
        }
        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleId.hashCode();
        result = 31 * result + attrId.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.security.privilege.vo.RoleAttrVO{" +
                "roleId='" + roleId + '\'' +
                ", attrId='" + attrId + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}