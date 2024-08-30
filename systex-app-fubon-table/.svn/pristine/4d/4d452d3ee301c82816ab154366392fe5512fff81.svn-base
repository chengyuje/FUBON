package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PARA_HOSPITALVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBINS_PARA_HOSPITALPK comp_id;

    /** persistent field */
    private String HOSPITAL_TYPE;

    /** persistent field */
    private String WARD_TYPE;

    /** persistent field */
    private BigDecimal DAY_AMT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_PARA_HOSPITAL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_PARA_HOSPITALVO(com.systex.jbranch.app.common.fps.table.TBINS_PARA_HOSPITALPK comp_id, String HOSPITAL_TYPE, String WARD_TYPE, BigDecimal DAY_AMT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.HOSPITAL_TYPE = HOSPITAL_TYPE;
        this.WARD_TYPE = WARD_TYPE;
        this.DAY_AMT = DAY_AMT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_PARA_HOSPITALVO() {
    }

    /** minimal constructor */
    public TBINS_PARA_HOSPITALVO(com.systex.jbranch.app.common.fps.table.TBINS_PARA_HOSPITALPK comp_id, String HOSPITAL_TYPE, String WARD_TYPE, BigDecimal DAY_AMT) {
        this.comp_id = comp_id;
        this.HOSPITAL_TYPE = HOSPITAL_TYPE;
        this.WARD_TYPE = WARD_TYPE;
        this.DAY_AMT = DAY_AMT;
    }

    public com.systex.jbranch.app.common.fps.table.TBINS_PARA_HOSPITALPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBINS_PARA_HOSPITALPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getHOSPITAL_TYPE() {
        return this.HOSPITAL_TYPE;
    }

    public void setHOSPITAL_TYPE(String HOSPITAL_TYPE) {
        this.HOSPITAL_TYPE = HOSPITAL_TYPE;
    }

    public String getWARD_TYPE() {
        return this.WARD_TYPE;
    }

    public void setWARD_TYPE(String WARD_TYPE) {
        this.WARD_TYPE = WARD_TYPE;
    }

    public BigDecimal getDAY_AMT() {
        return this.DAY_AMT;
    }

    public void setDAY_AMT(BigDecimal DAY_AMT) {
        this.DAY_AMT = DAY_AMT;
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
        if ( !(other instanceof TBINS_PARA_HOSPITALVO) ) return false;
        TBINS_PARA_HOSPITALVO castOther = (TBINS_PARA_HOSPITALVO) other;
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
