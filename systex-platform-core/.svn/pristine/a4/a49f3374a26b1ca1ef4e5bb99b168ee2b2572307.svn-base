package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;

/**
 * @author Alex Lin
 * @version 2010/01/22 10:07:38 AM
 */
public class TbsyssecuroleattrPK implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private String attrId;
    private String roleId;

// --------------------------- CONSTRUCTORS ---------------------------

    public TbsyssecuroleattrPK() {
    }

    public TbsyssecuroleattrPK(String roleId, String attrId) {
        this.roleId = roleId;
        this.attrId = attrId;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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

        TbsyssecuroleattrPK pk = (TbsyssecuroleattrPK) o;

        if (attrId != null ? !attrId.equals(pk.attrId) : pk.attrId != null) {
            return false;
        }
        if (roleId != null ? !roleId.equals(pk.roleId) : pk.roleId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = attrId != null ? attrId.hashCode() : 0;
        result = 31 * result + (roleId != null ? roleId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsyssecuroleattrPK{" +
                "attrId='" + attrId + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}