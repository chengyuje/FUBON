package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSPARAMETERVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK comp_id;

    /** persistent field */
    private Integer PARAM_ORDER;

    /** persistent field */
    private String PARAM_NAME;
    
    private String PARAM_NAME_EDIT;
    
    private String PARAM_DESC;

	private String PARAM_STATUS;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETER";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSPARAMETERVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK comp_id, Integer PARAM_ORDER, String PARAM_NAME, String PARAM_NAME_EDIT, String PARAM_DESC, String PARAM_STATUS,  Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PARAM_ORDER = PARAM_ORDER;
        this.PARAM_NAME = PARAM_NAME;
        this.PARAM_NAME_EDIT = PARAM_NAME_EDIT;
        this.PARAM_DESC = PARAM_DESC;
        this.PARAM_STATUS = PARAM_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYSPARAMETERVO() {
    }

    /** minimal constructor */
    public TBSYSPARAMETERVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK comp_id, Integer PARAM_ORDER, String PARAM_NAME) {
        this.comp_id = comp_id;
        this.PARAM_ORDER = PARAM_ORDER;
        this.PARAM_NAME = PARAM_NAME;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getPARAM_ORDER() {
        return this.PARAM_ORDER;
    }

    public void setPARAM_ORDER(Integer PARAM_ORDER) {
        this.PARAM_ORDER = PARAM_ORDER;
    }

    public String getPARAM_NAME() {
        return this.PARAM_NAME;
    }

    public void setPARAM_NAME(String PARAM_NAME) {
        this.PARAM_NAME = PARAM_NAME;
    }
    
    public String getPARAM_NAME_EDIT() {
		return PARAM_NAME_EDIT;
	}

	public void setPARAM_NAME_EDIT(String param_name_edit) {
		PARAM_NAME_EDIT = param_name_edit;
	}

	public String getPARAM_STATUS() {
		return PARAM_STATUS;
	}

	public void setPARAM_STATUS(String param_status) {
		PARAM_STATUS = param_status;
	}

	public String getPARAM_DESC() {
		return PARAM_DESC;
	}

	public void setPARAM_DESC(String param_desc) {
		PARAM_DESC = param_desc;
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
        if ( !(other instanceof TBSYSPARAMETERVO) ) return false;
        TBSYSPARAMETERVO castOther = (TBSYSPARAMETERVO) other;
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
