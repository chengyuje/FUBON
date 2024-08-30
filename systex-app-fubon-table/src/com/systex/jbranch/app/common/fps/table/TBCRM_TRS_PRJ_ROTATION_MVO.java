package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_PRJ_ROTATION_MVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MPK comp_id;

    /** nullable persistent field */
    private String REGION_BRANCH_YN;
        
    /** nullable persistent field */
    private String IMP_SUCCESS_YN;
    
    /** nullable persistent field */
    private String IMP_STATUS;
    
    /** nullable persistent field */
    private String IMP_EMP_ID;
    
    /** nullable persistent field */
    private Timestamp IMP_DATETIME;

    /** nullable persistent field */
    private String STEP_STATUS;
    
    /** nullable persistent field */
    private String BRH_REMARKS;
    
    /** nullable persistent field */
    private String RGN_REMARKS;
    
public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_M";


public String getTableuid () {
    return TABLE_UID;
}

    /** constructor */
    public TBCRM_TRS_PRJ_ROTATION_MVO(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MPK comp_id, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_TRS_PRJ_ROTATION_MVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_PRJ_ROTATION_MVO(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getREGION_BRANCH_YN() {
		return REGION_BRANCH_YN;
	}

	public void setREGION_BRANCH_YN(String rEGION_BRANCH_YN) {
		REGION_BRANCH_YN = rEGION_BRANCH_YN;
	}

	public String getIMP_SUCCESS_YN() {
		return IMP_SUCCESS_YN;
	}

	public void setIMP_SUCCESS_YN(String iMP_SUCCESS_YN) {
		IMP_SUCCESS_YN = iMP_SUCCESS_YN;
	}

	public String getIMP_STATUS() {
		return IMP_STATUS;
	}

	public void setIMP_STATUS(String iMP_STATUS) {
		IMP_STATUS = iMP_STATUS;
	}

	public String getIMP_EMP_ID() {
		return IMP_EMP_ID;
	}

	public void setIMP_EMP_ID(String iMP_EMP_ID) {
		IMP_EMP_ID = iMP_EMP_ID;
	}

	public Timestamp getIMP_DATETIME() {
		return IMP_DATETIME;
	}

	public void setIMP_DATETIME(Timestamp iMP_DATETIME) {
		IMP_DATETIME = iMP_DATETIME;
	}

	public String getSTEP_STATUS() {
		return STEP_STATUS;
	}

	public void setSTEP_STATUS(String sTEP_STATUS) {
		STEP_STATUS = sTEP_STATUS;
	}

	public String getBRH_REMARKS() {
		return BRH_REMARKS;
	}

	public void setBRH_REMARKS(String bRH_REMARKS) {
		BRH_REMARKS = bRH_REMARKS;
	}

	public String getRGN_REMARKS() {
		return RGN_REMARKS;
	}

	public void setRGN_REMARKS(String rGN_REMARKS) {
		RGN_REMARKS = rGN_REMARKS;
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
        if ( !(other instanceof TBCRM_TRS_PRJ_ROTATION_MVO) ) return false;
        TBCRM_TRS_PRJ_ROTATION_MVO castOther = (TBCRM_TRS_PRJ_ROTATION_MVO) other;
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
