package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_QST_ANSWER_COMPVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWER_COMPPK comp_id;

    /** nullable persistent field */
    private String ANSWER_DESC;
    /** nullable persistent field */
    private String ANSWER_DESC_ENG;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWER_COMP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_QST_ANSWER_COMPVO(com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWER_COMPPK comp_id, String ANSWER_DESC, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version, String ANSWER_DESC_ENG) {
        this.comp_id = comp_id;
        this.ANSWER_DESC = ANSWER_DESC;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
        this.ANSWER_DESC_ENG = ANSWER_DESC_ENG;
    }

    /** default constructor */
    public TBSYS_QST_ANSWER_COMPVO() {
    }

    /** minimal constructor */
    public TBSYS_QST_ANSWER_COMPVO(com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWER_COMPPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWER_COMPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWER_COMPPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getANSWER_DESC() {
        return this.ANSWER_DESC;
    }

    public void setANSWER_DESC(String ANSWER_DESC) {
        this.ANSWER_DESC = ANSWER_DESC;
    }

    public String getANSWER_DESC_ENG() {
		return ANSWER_DESC_ENG;
	}

	public void setANSWER_DESC_ENG(String aNSWER_DESC_ENG) {
		ANSWER_DESC_ENG = aNSWER_DESC_ENG;
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
        if ( !(other instanceof TBSYS_QST_ANSWER_COMPVO) ) return false;
        TBSYS_QST_ANSWER_COMPVO castOther = (TBSYS_QST_ANSWER_COMPVO) other;
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
