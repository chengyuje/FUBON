package com.systex.jbranch.platform.common.security.privilege.vo;

import com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO;

import java.sql.Timestamp;
import java.util.Set;

/**
 * 角色VO
 *
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public class RoleVO {
// ------------------------------ FIELDS ------------------------------

    /** 角色ID */
    private String roleid;

    /** 角色名稱 */
    private String name;

    /** 角色的描述 */
    private String description;

    private Timestamp lastUpdate;
    private String modifier;
    private String extend1;
    private String extend2;
    private String extend3;
    private Set<RoleAttrVO> attributes;

// --------------------------- CONSTRUCTORS ---------------------------

    public RoleVO() {
    }

    public RoleVO(TBSYSSECUROLEVO vo) {
        this(vo.getROLEID(), vo.getNAME(), vo.getDESCRIPTION(), vo.getLastupdate(), vo.getModifier());
        this.setExtend1(vo.getEXTEND1());
        this.setExtend2(vo.getEXTEND2());
        this.setExtend3(vo.getEXTEND3());
    }

    public RoleVO(String roleid, String name, String description) {
        this.roleid = roleid;
        this.name = name;
        this.description = description;
    }

    public RoleVO(String roleid, String name, String description, Timestamp lastUpdate, String modifier) {
        this.roleid = roleid;
        this.name = name;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.modifier = modifier;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * 取得角色描述
     * Getter of the property <tt>description</tt>
     *
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * 設定角色描述
     * Setter of the property <tt>description</tt>
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * 取得角色名稱
     * Getter of the property <tt>name</tt>
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * 設定角色名稱
     * Setter of the property <tt>name</tt>
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of the property <tt>roleid</tt>
     *
     * @return Returns the roleid.
     */
    public String getRoleid() {
        return roleid;
    }

    /**
     * Setter of the property <tt>roleid</tt>
     *
     * @param roleid The roleid to set.
     */
    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public Set<RoleAttrVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<RoleAttrVO> attributes) {
        this.attributes = attributes;
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

        RoleVO roleVO = (RoleVO) o;

        return !(roleid != null ? !roleid.equals(roleVO.roleid) : roleVO.roleid != null);
    }

    @Override
    public int hashCode() {
        return roleid != null ? roleid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.security.privilege.vo.RoleVO{" +
                "roleid='" + roleid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", modifier='" + modifier + '\'' +
                ", extend1='" + extend1 + '\'' +
                ", extend2='" + extend2 + '\'' +
                ", extend3='" + extend3 + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
