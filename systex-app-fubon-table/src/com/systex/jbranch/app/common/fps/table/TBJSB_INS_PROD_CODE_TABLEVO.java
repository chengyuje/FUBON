package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_CODE_TABLEVO extends VOBase {

    /** identifier field */
    private TBJSB_INS_PROD_CODE_TABLEPK comp_id;

    /** nullable persistent field */
    private String CD_NM;

    /** nullable persistent field */
    private String CD_DESC;

    /** nullable persistent field */
    private String CD_MEMO;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private String MAINT_USER;

    /** nullable persistent field */
    private Timestamp MAINT_DT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_CODE_TABLE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBJSB_INS_PROD_CODE_TABLEVO(TBJSB_INS_PROD_CODE_TABLEPK comp_id, String CD_NM, String CD_DESC, String CD_MEMO, String STATUS, String MAINT_USER, Timestamp MAINT_DT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CD_NM = CD_NM;
        this.CD_DESC = CD_DESC;
        this.CD_MEMO = CD_MEMO;
        this.STATUS = STATUS;
        this.MAINT_USER = MAINT_USER;
        this.MAINT_DT = MAINT_DT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBJSB_INS_PROD_CODE_TABLEVO() {
    }

    /** minimal constructor */
    public TBJSB_INS_PROD_CODE_TABLEVO(TBJSB_INS_PROD_CODE_TABLEPK comp_id) {
        this.comp_id = comp_id;
    }

    public TBJSB_INS_PROD_CODE_TABLEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(TBJSB_INS_PROD_CODE_TABLEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCD_NM() {
        return this.CD_NM;
    }

    public void setCD_NM(String CD_NM) {
        this.CD_NM = CD_NM;
    }

    public String getCD_DESC() {
        return this.CD_DESC;
    }

    public void setCD_DESC(String CD_DESC) {
        this.CD_DESC = CD_DESC;
    }

    public String getCD_MEMO() {
        return this.CD_MEMO;
    }

    public void setCD_MEMO(String CD_MEMO) {
        this.CD_MEMO = CD_MEMO;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getMAINT_USER() {
        return this.MAINT_USER;
    }

    public void setMAINT_USER(String MAINT_USER) {
        this.MAINT_USER = MAINT_USER;
    }

    public Timestamp getMAINT_DT() {
        return this.MAINT_DT;
    }

    public void setMAINT_DT(Timestamp MAINT_DT) {
        this.MAINT_DT = MAINT_DT;
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
        if ( !(other instanceof TBJSB_INS_PROD_CODE_TABLEVO) ) return false;
        TBJSB_INS_PROD_CODE_TABLEVO castOther = (TBJSB_INS_PROD_CODE_TABLEVO) other;
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
