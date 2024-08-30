package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_MAPPVIDEO_CHKLISTVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBIOT_MAPPVIDEO_CHKLISTPK comp_id;

    /** nullable persistent field */
    private String CHK_YN;
    /** nullable persistent field */
    private String CHK_EMP_ID;
    /** nullable persistent field */
    private String NP_REASON;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_MAPPVIDEO_CHKLIST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_MAPPVIDEO_CHKLISTVO(com.systex.jbranch.app.common.fps.table.TBIOT_MAPPVIDEO_CHKLISTPK comp_id, String CHK_YN, String CHK_EMP_ID, String NP_REASON, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CHK_YN = CHK_YN;
        this.CHK_EMP_ID = CHK_EMP_ID;
        this.NP_REASON = NP_REASON;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_MAPPVIDEO_CHKLISTVO() {
    }

    /** minimal constructor */
    public TBIOT_MAPPVIDEO_CHKLISTVO(com.systex.jbranch.app.common.fps.table.TBIOT_MAPPVIDEO_CHKLISTPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBIOT_MAPPVIDEO_CHKLISTPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBIOT_MAPPVIDEO_CHKLISTPK comp_id) {
        this.comp_id = comp_id;
    }

	public String getCHK_YN() {
		return CHK_YN;
	}

	public void setCHK_YN(String cHK_YN) {
		CHK_YN = cHK_YN;
	}

	public String getCHK_EMP_ID() {
		return CHK_EMP_ID;
	}

	public void setCHK_EMP_ID(String cHK_EMP_ID) {
		CHK_EMP_ID = cHK_EMP_ID;
	}

	public String getNP_REASON() {
		return NP_REASON;
	}

	public void setNP_REASON(String nP_REASON) {
		NP_REASON = nP_REASON;
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
        if ( !(other instanceof TBIOT_MAPPVIDEO_CHKLISTVO) ) return false;
        TBIOT_MAPPVIDEO_CHKLISTVO castOther = (TBIOT_MAPPVIDEO_CHKLISTVO) other;
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
