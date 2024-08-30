package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_SALES_AOCODEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEPK comp_id;

    /** nullable persistent field */
    private String TYPE;

    /** nullable persistent field */
    private String AO_CODE_ATCH_REASON;

    /** nullable persistent field */
    private Timestamp ACTIVE_DATE;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_SALES_AOCODEVO(com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEPK comp_id, String TYPE, String AO_CODE_ATCH_REASON, Timestamp ACTIVE_DATE, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.TYPE = TYPE;
        this.AO_CODE_ATCH_REASON = AO_CODE_ATCH_REASON;
        this.ACTIVE_DATE = ACTIVE_DATE;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBORG_SALES_AOCODEVO() {
    }

    /** minimal constructor */
    public TBORG_SALES_AOCODEVO(com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getAO_CODE_ATCH_REASON() {
        return this.AO_CODE_ATCH_REASON;
    }

    public void setAO_CODE_ATCH_REASON(String AO_CODE_ATCH_REASON) {
        this.AO_CODE_ATCH_REASON = AO_CODE_ATCH_REASON;
    }

    public Timestamp getACTIVE_DATE() {
        return this.ACTIVE_DATE;
    }

    public void setACTIVE_DATE(Timestamp ACTIVE_DATE) {
        this.ACTIVE_DATE = ACTIVE_DATE;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getREVIEW_STATUS() {
        return this.REVIEW_STATUS;
    }

    public void setREVIEW_STATUS(String REVIEW_STATUS) {
        this.REVIEW_STATUS = REVIEW_STATUS;
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
        if ( !(other instanceof TBORG_SALES_AOCODEVO) ) return false;
        TBORG_SALES_AOCODEVO castOther = (TBORG_SALES_AOCODEVO) other;
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
