package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_NR097NVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPRD_NR097NPK comp_id;

    /** nullable persistent field */
    private String PRODUCTTYPEMEAN;

    /** nullable persistent field */
    private String TYPENUMBERMEAN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_NR097N";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_NR097NVO(com.systex.jbranch.app.common.fps.table.TBPRD_NR097NPK comp_id, String PRODUCTTYPEMEAN, String TYPENUMBERMEAN, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PRODUCTTYPEMEAN = PRODUCTTYPEMEAN;
        this.TYPENUMBERMEAN = TYPENUMBERMEAN;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_NR097NVO() {
    }

    /** minimal constructor */
    public TBPRD_NR097NVO(com.systex.jbranch.app.common.fps.table.TBPRD_NR097NPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPRD_NR097NPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPRD_NR097NPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPRODUCTTYPEMEAN() {
        return this.PRODUCTTYPEMEAN;
    }

    public void setPRODUCTTYPEMEAN(String PRODUCTTYPEMEAN) {
        this.PRODUCTTYPEMEAN = PRODUCTTYPEMEAN;
    }

    public String getTYPENUMBERMEAN() {
        return this.TYPENUMBERMEAN;
    }

    public void setTYPENUMBERMEAN(String TYPENUMBERMEAN) {
        this.TYPENUMBERMEAN = TYPENUMBERMEAN;
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
        if ( !(other instanceof TBPRD_NR097NVO) ) return false;
        TBPRD_NR097NVO castOther = (TBPRD_NR097NVO) other;
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
