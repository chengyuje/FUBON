package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSOVERPRINTBOOKMARKVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBOOKMARKPK comp_id;

    /** nullable persistent field */
    private String DATASOURCE;

    /** nullable persistent field */
    private String DATATYPE;

    /** persistent field */
    private BigDecimal LIMIT_LENGTH;

    /** nullable persistent field */
    private String DESCRIPTION;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBOOKMARK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSOVERPRINTBOOKMARKVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBOOKMARKPK comp_id, String DATASOURCE, String DATATYPE, BigDecimal LIMIT_LENGTH, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, String DESCRIPTION, Long version) {
        this.comp_id = comp_id;
        this.DATASOURCE = DATASOURCE;
        this.DATATYPE = DATATYPE;
        this.LIMIT_LENGTH = LIMIT_LENGTH;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.DESCRIPTION = DESCRIPTION;
        this.version = version;
    }

    /** default constructor */
    public TBSYSOVERPRINTBOOKMARKVO() {
    }

    /** minimal constructor */
    public TBSYSOVERPRINTBOOKMARKVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBOOKMARKPK comp_id, BigDecimal LIMIT_LENGTH) {
        this.comp_id = comp_id;
        this.LIMIT_LENGTH = LIMIT_LENGTH;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBOOKMARKPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTBOOKMARKPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getDATASOURCE() {
        return this.DATASOURCE;
    }

    public void setDATASOURCE(String DATASOURCE) {
        this.DATASOURCE = DATASOURCE;
    }

    public String getDATATYPE() {
        return this.DATATYPE;
    }

    public void setDATATYPE(String DATATYPE) {
        this.DATATYPE = DATATYPE;
    }

    public BigDecimal getLIMIT_LENGTH() {
        return this.LIMIT_LENGTH;
    }

    public void setLIMIT_LENGTH(BigDecimal LIMIT_LENGTH) {
        this.LIMIT_LENGTH = LIMIT_LENGTH;
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
        if ( !(other instanceof TBSYSOVERPRINTBOOKMARKVO) ) return false;
        TBSYSOVERPRINTBOOKMARKVO castOther = (TBSYSOVERPRINTBOOKMARKVO) other;
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
