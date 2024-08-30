package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_DOC_CHKVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKPK comp_id;

    /** nullable persistent field */
    private String DOC_NAME;

    /** nullable persistent field */
    private String DOC_NAME_OTH;

    /** nullable persistent field */
    private String DOC_LEVEL;

    /** nullable persistent field */
    private String SIGN_INC;

    /** nullable persistent field */
    private String DOC_CHK;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_DOC_CHKVO(com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKPK comp_id, String DOC_NAME, String DOC_NAME_OTH, String DOC_LEVEL, String SIGN_INC, String DOC_CHK, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.DOC_NAME = DOC_NAME;
        this.DOC_NAME_OTH = DOC_NAME_OTH;
        this.DOC_LEVEL = DOC_LEVEL;
        this.SIGN_INC = SIGN_INC;
        this.DOC_CHK = DOC_CHK;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_DOC_CHKVO() {
    }

    /** minimal constructor */
    public TBIOT_DOC_CHKVO(com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getDOC_NAME() {
        return this.DOC_NAME;
    }

    public void setDOC_NAME(String DOC_NAME) {
        this.DOC_NAME = DOC_NAME;
    }

    public String getDOC_NAME_OTH() {
        return this.DOC_NAME_OTH;
    }

    public void setDOC_NAME_OTH(String DOC_NAME_OTH) {
        this.DOC_NAME_OTH = DOC_NAME_OTH;
    }

    public String getDOC_LEVEL() {
        return this.DOC_LEVEL;
    }

    public void setDOC_LEVEL(String DOC_LEVEL) {
        this.DOC_LEVEL = DOC_LEVEL;
    }

    public String getSIGN_INC() {
        return this.SIGN_INC;
    }

    public void setSIGN_INC(String SIGN_INC) {
        this.SIGN_INC = SIGN_INC;
    }

    public String getDOC_CHK() {
        return this.DOC_CHK;
    }

    public void setDOC_CHK(String DOC_CHK) {
        this.DOC_CHK = DOC_CHK;
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
        if ( !(other instanceof TBIOT_DOC_CHKVO) ) return false;
        TBIOT_DOC_CHKVO castOther = (TBIOT_DOC_CHKVO) other;
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
