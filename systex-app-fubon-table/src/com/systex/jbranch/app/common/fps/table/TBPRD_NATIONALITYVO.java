package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_NATIONALITYVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPRD_NATIONALITYPK comp_id;

    /** nullable persistent field */
    private String PROD_ID;

    /** nullable persistent field */
    private String COUNTRY_NAME;

    /** nullable persistent field */
    private String SOU_TYPE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_NATIONALITY";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_NATIONALITYVO(com.systex.jbranch.app.common.fps.table.TBPRD_NATIONALITYPK comp_id, String PROD_ID, String COUNTRY_NAME, String SOU_TYPE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PROD_ID = PROD_ID;
        this.COUNTRY_NAME = COUNTRY_NAME;
        this.SOU_TYPE = SOU_TYPE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_NATIONALITYVO() {
    }

    /** minimal constructor */
    public TBPRD_NATIONALITYVO(com.systex.jbranch.app.common.fps.table.TBPRD_NATIONALITYPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPRD_NATIONALITYPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPRD_NATIONALITYPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPROD_ID() {
        return this.PROD_ID;
    }

    public void setPROD_ID(String PROD_ID) {
        this.PROD_ID = PROD_ID;
    }

    public String getCOUNTRY_NAME() {
        return this.COUNTRY_NAME;
    }

    public void setCOUNTRY_NAME(String COUNTRY_NAME) {
        this.COUNTRY_NAME = COUNTRY_NAME;
    }

    public String getSOU_TYPE() {
        return this.SOU_TYPE;
    }

    public void setSOU_TYPE(String SOU_TYPE) {
        this.SOU_TYPE = SOU_TYPE;
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
        if ( !(other instanceof TBPRD_NATIONALITYVO) ) return false;
        TBPRD_NATIONALITYVO castOther = (TBPRD_NATIONALITYVO) other;
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
