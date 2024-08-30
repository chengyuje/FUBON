package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_SFA_CAMP_RESPONSEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_RESPONSEPK comp_id;

    /** nullable persistent field */
    private String RESPONSE_NAME;

    /** nullable persistent field */
    private String RESPONSE_MEAN;

    /** nullable persistent field */
    private String RESPONSE_ENABLE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_RESPONSE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_SFA_CAMP_RESPONSEVO(com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_RESPONSEPK comp_id, String RESPONSE_NAME, String RESPONSE_MEAN, String RESPONSE_ENABLE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.RESPONSE_NAME = RESPONSE_NAME;
        this.RESPONSE_MEAN = RESPONSE_MEAN;
        this.RESPONSE_ENABLE = RESPONSE_ENABLE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_SFA_CAMP_RESPONSEVO() {
    }

    /** minimal constructor */
    public TBCAM_SFA_CAMP_RESPONSEVO(com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_RESPONSEPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_RESPONSEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_RESPONSEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getRESPONSE_NAME() {
        return this.RESPONSE_NAME;
    }

    public void setRESPONSE_NAME(String RESPONSE_NAME) {
        this.RESPONSE_NAME = RESPONSE_NAME;
    }

    public String getRESPONSE_MEAN() {
        return this.RESPONSE_MEAN;
    }

    public void setRESPONSE_MEAN(String RESPONSE_MEAN) {
        this.RESPONSE_MEAN = RESPONSE_MEAN;
    }

    public String getRESPONSE_ENABLE() {
        return this.RESPONSE_ENABLE;
    }

    public void setRESPONSE_ENABLE(String RESPONSE_ENABLE) {
        this.RESPONSE_ENABLE = RESPONSE_ENABLE;
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
        if ( !(other instanceof TBCAM_SFA_CAMP_RESPONSEVO) ) return false;
        TBCAM_SFA_CAMP_RESPONSEVO castOther = (TBCAM_SFA_CAMP_RESPONSEVO) other;
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
