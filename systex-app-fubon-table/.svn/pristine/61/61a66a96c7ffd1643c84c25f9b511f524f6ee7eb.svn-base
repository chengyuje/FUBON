package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_CSM_OPTIONSVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSQM_CSM_OPTIONSPK comp_id;

    /** nullable persistent field */
    private Timestamp UPDATE_DATE;

    /** nullable persistent field */
    private String DESCRIPTION;

    /** nullable persistent field */
    private BigDecimal PRIORITY;

    /** nullable persistent field */
    private String OPTION_TYPE;

    /** nullable persistent field */
    private BigDecimal SCORE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSQM_CSM_OPTIONS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSQM_CSM_OPTIONSVO(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_OPTIONSPK comp_id, Timestamp UPDATE_DATE, String DESCRIPTION, BigDecimal PRIORITY, String OPTION_TYPE, BigDecimal SCORE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.UPDATE_DATE = UPDATE_DATE;
        this.DESCRIPTION = DESCRIPTION;
        this.PRIORITY = PRIORITY;
        this.OPTION_TYPE = OPTION_TYPE;
        this.SCORE = SCORE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSQM_CSM_OPTIONSVO() {
    }

    /** minimal constructor */
    public TBSQM_CSM_OPTIONSVO(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_OPTIONSPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSQM_CSM_OPTIONSPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_OPTIONSPK comp_id) {
        this.comp_id = comp_id;
    }

    public Timestamp getUPDATE_DATE() {
        return this.UPDATE_DATE;
    }

    public void setUPDATE_DATE(Timestamp UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public String getDESCRIPTION() {
        return this.DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public BigDecimal getPRIORITY() {
        return this.PRIORITY;
    }

    public void setPRIORITY(BigDecimal PRIORITY) {
        this.PRIORITY = PRIORITY;
    }

    public String getOPTION_TYPE() {
        return this.OPTION_TYPE;
    }

    public void setOPTION_TYPE(String OPTION_TYPE) {
        this.OPTION_TYPE = OPTION_TYPE;
    }

    public BigDecimal getSCORE() {
        return this.SCORE;
    }

    public void setSCORE(BigDecimal SCORE) {
        this.SCORE = SCORE;
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
        if ( !(other instanceof TBSQM_CSM_OPTIONSVO) ) return false;
        TBSQM_CSM_OPTIONSVO castOther = (TBSQM_CSM_OPTIONSVO) other;
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
