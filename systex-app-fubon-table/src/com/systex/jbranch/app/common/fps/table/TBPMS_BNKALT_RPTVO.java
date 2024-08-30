package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_BNKALT_RPTVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPTPK comp_id;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String TXN_TYP;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String CUST_AO_CODE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_BNKALT_RPTVO(com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPTPK comp_id, String BRANCH_NAME, String EMP_NAME, String AO_CODE, String TXN_TYP, String CUST_ID, String CUST_NAME, String CUST_AO_CODE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.BRANCH_NAME = BRANCH_NAME;
        this.EMP_NAME = EMP_NAME;
        this.AO_CODE = AO_CODE;
        this.TXN_TYP = TXN_TYP;
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.CUST_AO_CODE = CUST_AO_CODE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_BNKALT_RPTVO() {
    }

    /** minimal constructor */
    public TBPMS_BNKALT_RPTVO(com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPTPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPTPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_BNKALT_RPTPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getBRANCH_NAME() {
        return this.BRANCH_NAME;
    }

    public void setBRANCH_NAME(String BRANCH_NAME) {
        this.BRANCH_NAME = BRANCH_NAME;
    }

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getTXN_TYP() {
        return this.TXN_TYP;
    }

    public void setTXN_TYP(String TXN_TYP) {
        this.TXN_TYP = TXN_TYP;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public String getCUST_AO_CODE() {
        return this.CUST_AO_CODE;
    }

    public void setCUST_AO_CODE(String CUST_AO_CODE) {
        this.CUST_AO_CODE = CUST_AO_CODE;
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
        if ( !(other instanceof TBPMS_BNKALT_RPTVO) ) return false;
        TBPMS_BNKALT_RPTVO castOther = (TBPMS_BNKALT_RPTVO) other;
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
