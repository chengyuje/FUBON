package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_XLS_IMP_MAPVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_MAPPK comp_id;

    /** persistent field */
    private String TAR_COL_NAME;

    /** persistent field */
    private BigDecimal TAR_DATA_TYPE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_MAP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_XLS_IMP_MAPVO(com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_MAPPK comp_id, String TAR_COL_NAME, BigDecimal TAR_DATA_TYPE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.TAR_COL_NAME = TAR_COL_NAME;
        this.TAR_DATA_TYPE = TAR_DATA_TYPE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_XLS_IMP_MAPVO() {
    }

    /** minimal constructor */
    public TBSYS_XLS_IMP_MAPVO(com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_MAPPK comp_id, String TAR_COL_NAME, BigDecimal TAR_DATA_TYPE) {
        this.comp_id = comp_id;
        this.TAR_COL_NAME = TAR_COL_NAME;
        this.TAR_DATA_TYPE = TAR_DATA_TYPE;
    }

    public com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_MAPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_MAPPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTAR_COL_NAME() {
        return this.TAR_COL_NAME;
    }

    public void setTAR_COL_NAME(String TAR_COL_NAME) {
        this.TAR_COL_NAME = TAR_COL_NAME;
    }

    public BigDecimal getTAR_DATA_TYPE() {
        return this.TAR_DATA_TYPE;
    }

    public void setTAR_DATA_TYPE(BigDecimal TAR_DATA_TYPE) {
        this.TAR_DATA_TYPE = TAR_DATA_TYPE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYS_XLS_IMP_MAPVO) ) return false;
        TBSYS_XLS_IMP_MAPVO castOther = (TBSYS_XLS_IMP_MAPVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
