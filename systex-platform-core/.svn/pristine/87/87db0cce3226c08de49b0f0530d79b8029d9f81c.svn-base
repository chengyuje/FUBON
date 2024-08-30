package com.systex.jbranch.platform.common.security.privilege.vo;

import com.systex.jbranch.platform.common.platformdao.table.TbsysorgattrVO;

/**
 * @author Alex Lin
 * @version 2010/01/22 11:30:41 AM
 */
public class OrgAttrVO {
// ------------------------------ FIELDS ------------------------------

    private String divNo;
    private String attrId;
    private String value;
    private String description;

// --------------------------- CONSTRUCTORS ---------------------------

    public OrgAttrVO() {
    }

    public OrgAttrVO(TbsysorgattrVO attrVO) {
        this(attrVO.getCompId().getDivNo(), attrVO.getCompId().getAttrId(), attrVO.getValue(), attrVO.getDescription());
    }

    public OrgAttrVO(String attrId, String value) {
        this.attrId = attrId;
        this.value = value;
    }

    public OrgAttrVO(String attrId, String value, String description) {
        this.attrId = attrId;
        this.value = value;
        this.description = description;
    }

    public OrgAttrVO(String divNo, String attrId, String value, String description) {
        this.attrId = attrId;
        this.divNo = divNo;
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

    public String getDivNo() {
        return divNo;
    }

    public void setDivNo(String divNo) {
        this.divNo = divNo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.security.privilege.vo.OrgAttrVO{" +
                "attrId='" + attrId + '\'' +
                ", divNo=" + divNo +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
