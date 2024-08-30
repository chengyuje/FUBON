package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_10CMDT_PLAN_AO_LISTVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_AO_LISTPK comp_id;

    /** nullable persistent field */
    private String CHG_TYPE;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private Timestamp DATA_DATE;

    /** nullable persistent field */
    private String CHG_TYPE2;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_AO_LIST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_10CMDT_PLAN_AO_LISTVO(com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_AO_LISTPK comp_id, String CHG_TYPE, String STATUS, Timestamp DATA_DATE, String CHG_TYPE2, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CHG_TYPE = CHG_TYPE;
        this.STATUS = STATUS;
        this.DATA_DATE = DATA_DATE;
        this.CHG_TYPE2 = CHG_TYPE2;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_10CMDT_PLAN_AO_LISTVO() {
    }

    /** minimal constructor */
    public TBCRM_10CMDT_PLAN_AO_LISTVO(com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_AO_LISTPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_AO_LISTPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_AO_LISTPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCHG_TYPE() {
        return this.CHG_TYPE;
    }

    public void setCHG_TYPE(String CHG_TYPE) {
        this.CHG_TYPE = CHG_TYPE;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public Timestamp getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(Timestamp DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
    }

    public String getCHG_TYPE2() {
        return this.CHG_TYPE2;
    }

    public void setCHG_TYPE2(String CHG_TYPE2) {
        this.CHG_TYPE2 = CHG_TYPE2;
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
        if ( !(other instanceof TBCRM_10CMDT_PLAN_AO_LISTVO) ) return false;
        TBCRM_10CMDT_PLAN_AO_LISTVO castOther = (TBCRM_10CMDT_PLAN_AO_LISTVO) other;
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
