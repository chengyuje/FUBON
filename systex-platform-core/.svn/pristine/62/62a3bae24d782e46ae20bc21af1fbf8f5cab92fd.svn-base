package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;

/**
 * @author Alex Lin
 * @version 2010/01/22 10:07:38 AM
 */
public class TbsysorgattrPK implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private String attrId;
    private String divNo;

// --------------------------- CONSTRUCTORS ---------------------------

    public TbsysorgattrPK() {
    }

    public TbsysorgattrPK(String  divNo, String attrId) {
        this.divNo = divNo;
        this.attrId = attrId;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getDivNo() {
        return divNo;
    }

    public void setDivNo(String divNo) {
        this.divNo = divNo;
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

        TbsysorgattrPK pk = (TbsysorgattrPK) o;

        if (attrId != null ? !attrId.equals(pk.attrId) : pk.attrId != null) {
            return false;
        }
        if (divNo != null ? !divNo.equals(pk.divNo) : pk.divNo != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = attrId != null ? attrId.hashCode() : 0;
        result = 31 * result + (divNo != null ? divNo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysorgattrPK{" +
                "attrId='" + attrId + '\'' +
                ", divNo='" + divNo + '\'' +
                '}';
    }
}
