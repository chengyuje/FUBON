package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_10CMDT_EBILL_COMFIRM_NYVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_10CMDT_EBILL_COMFIRM_NYPK comp_id;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private BigDecimal SERVICE_YEARS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_10CMDT_EBILL_COMFIRM_NY";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_10CMDT_EBILL_COMFIRM_NYVO(com.systex.jbranch.app.common.fps.table.TBPMS_10CMDT_EBILL_COMFIRM_NYPK comp_id, String EMP_NAME, BigDecimal SERVICE_YEARS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.EMP_NAME = EMP_NAME;
        this.SERVICE_YEARS = SERVICE_YEARS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_10CMDT_EBILL_COMFIRM_NYVO() {
    }

    /** minimal constructor */
    public TBPMS_10CMDT_EBILL_COMFIRM_NYVO(com.systex.jbranch.app.common.fps.table.TBPMS_10CMDT_EBILL_COMFIRM_NYPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_10CMDT_EBILL_COMFIRM_NYPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_10CMDT_EBILL_COMFIRM_NYPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public BigDecimal getSERVICE_YEARS() {
		return SERVICE_YEARS;
	}

	public void setSERVICE_YEARS(BigDecimal sERVICE_YEARS) {
		SERVICE_YEARS = sERVICE_YEARS;
	}

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_10CMDT_EBILL_COMFIRM_NYVO) ) return false;
        TBPMS_10CMDT_EBILL_COMFIRM_NYVO castOther = (TBPMS_10CMDT_EBILL_COMFIRM_NYVO) other;
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
