package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSOVERPRINTDATASETVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTDATASETPK comp_id;

    /** persistent field */
    private String SCHEMA;

    /** nullable persistent field */
    private String DESCRIPTION;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTDATASET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSOVERPRINTDATASETVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTDATASETPK comp_id, String SCHEMA, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, String DESCRIPTION, Long version) {
        this.comp_id = comp_id;
        this.SCHEMA = SCHEMA;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.DESCRIPTION = DESCRIPTION;
        this.version = version;
    }

    /** default constructor */
    public TBSYSOVERPRINTDATASETVO() {
    }

    /** minimal constructor */
    public TBSYSOVERPRINTDATASETVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTDATASETPK comp_id, String SCHEMA) {
        this.comp_id = comp_id;
        this.SCHEMA = SCHEMA;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTDATASETPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTDATASETPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getSCHEMA() {
        return this.SCHEMA;
    }

    public void setSCHEMA(String SCHEMA) {
        this.SCHEMA = SCHEMA;
    }

    public String getDESCRIPTION() {
        return this.DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
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
        if ( !(other instanceof TBSYSOVERPRINTDATASETVO) ) return false;
        TBSYSOVERPRINTDATASETVO castOther = (TBSYSOVERPRINTDATASETVO) other;
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
