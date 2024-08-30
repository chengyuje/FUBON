package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_PS_SA_INS_EMPFUNDVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_EMPFUNDPK comp_id;

    /** persistent field */
    private Timestamp END_DATE;

    /** persistent field */
    private Timestamp DATA_DATE;

    /** persistent field */
    private String clazz;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_EMPFUND";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_PS_SA_INS_EMPFUNDVO(com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_EMPFUNDPK comp_id, Timestamp END_DATE, Timestamp DATA_DATE, String clazz) {
        this.comp_id = comp_id;
        this.END_DATE = END_DATE;
        this.DATA_DATE = DATA_DATE;
        this.clazz = clazz;
    }

    /** default constructor */
    public TBORG_PS_SA_INS_EMPFUNDVO() {
    }

    public com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_EMPFUNDPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBORG_PS_SA_INS_EMPFUNDPK comp_id) {
        this.comp_id = comp_id;
    }

    public Timestamp getEND_DATE() {
        return this.END_DATE;
    }

    public void setEND_DATE(Timestamp END_DATE) {
        this.END_DATE = END_DATE;
    }

    public Timestamp getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(Timestamp DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
    }

    public String getclazz() {
        return this.clazz;
    }

    public void setclazz(String clazz) {
        this.clazz = clazz;
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
        if ( !(other instanceof TBORG_PS_SA_INS_EMPFUNDVO) ) return false;
        TBORG_PS_SA_INS_EMPFUNDVO castOther = (TBORG_PS_SA_INS_EMPFUNDVO) other;
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
