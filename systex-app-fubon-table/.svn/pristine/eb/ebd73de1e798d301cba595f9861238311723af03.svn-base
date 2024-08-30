package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_TRACK_PRO_SETVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_TRACK_PRO_SETPK comp_id;

    /** nullable persistent field */
    private String MAIN_COM_NBR_NAME;

    /** nullable persistent field */
    private String REL_COM_NBR;

    /** nullable persistent field */
    private String SDATE;

    /** nullable persistent field */
    private String EDATE;

    /** nullable persistent field */
    private String PRD_NAME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_TRACK_PRO_SET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_TRACK_PRO_SETVO(com.systex.jbranch.app.common.fps.table.TBPMS_TRACK_PRO_SETPK comp_id, String MAIN_COM_NBR_NAME, String REL_COM_NBR, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String SDATE, String EDATE, String PRD_NAME, Long version) {
        this.comp_id = comp_id;
        this.MAIN_COM_NBR_NAME = MAIN_COM_NBR_NAME;
        this.REL_COM_NBR = REL_COM_NBR;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.SDATE = SDATE;
        this.EDATE = EDATE;
        this.PRD_NAME = PRD_NAME;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_TRACK_PRO_SETVO() {
    }

    /** minimal constructor */
    public TBPMS_TRACK_PRO_SETVO(com.systex.jbranch.app.common.fps.table.TBPMS_TRACK_PRO_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_TRACK_PRO_SETPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_TRACK_PRO_SETPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getMAIN_COM_NBR_NAME() {
        return this.MAIN_COM_NBR_NAME;
    }

    public void setMAIN_COM_NBR_NAME(String MAIN_COM_NBR_NAME) {
        this.MAIN_COM_NBR_NAME = MAIN_COM_NBR_NAME;
    }

    public String getREL_COM_NBR() {
        return this.REL_COM_NBR;
    }

    public void setREL_COM_NBR(String REL_COM_NBR) {
        this.REL_COM_NBR = REL_COM_NBR;
    }

    public String getSDATE() {
        return this.SDATE;
    }

    public void setSDATE(String SDATE) {
        this.SDATE = SDATE;
    }

    public String getEDATE() {
        return this.EDATE;
    }

    public void setEDATE(String EDATE) {
        this.EDATE = EDATE;
    }

    public String getPRD_NAME() {
        return this.PRD_NAME;
    }

    public void setPRD_NAME(String PRD_NAME) {
        this.PRD_NAME = PRD_NAME;
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
        if ( !(other instanceof TBPMS_TRACK_PRO_SETVO) ) return false;
        TBPMS_TRACK_PRO_SETVO castOther = (TBPMS_TRACK_PRO_SETVO) other;
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
