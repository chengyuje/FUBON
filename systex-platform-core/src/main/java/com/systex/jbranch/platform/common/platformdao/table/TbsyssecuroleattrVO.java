package com.systex.jbranch.platform.common.platformdao.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/**
 * @author Alex Lin
 * @version 2010/01/22 9:47:06 AM
 */
public class TbsyssecuroleattrVO extends VOBase {
// ------------------------------ FIELDS ------------------------------

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecuroleattr";
    private TbsyssecuroleattrPK compId;
    private String description;
    private String value;

// --------------------------- CONSTRUCTORS ---------------------------

    public TbsyssecuroleattrVO() {
    }

    public TbsyssecuroleattrVO(TbsyssecuroleattrPK compId, String value, String description) {
        this.compId = compId;
        this.description = description;
        this.value = value;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public String getTableuid() {
        return TABLE_UID;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public TbsyssecuroleattrPK getCompId() {
        return compId;
    }

    public void setCompId(TbsyssecuroleattrPK compId) {
        this.compId = compId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

        TbsyssecuroleattrVO that = (TbsyssecuroleattrVO) o;

        if (compId != null ? !compId.equals(that.compId) : that.compId != null) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = compId != null ? compId.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsyssecuroleattrVO{" +
                "compId=" + compId +
                ", description='" + description + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}