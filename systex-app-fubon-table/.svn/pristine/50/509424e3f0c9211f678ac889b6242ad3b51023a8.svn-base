package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_PORTFOLIO_PLAN_FILEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILEPK comp_id;

    /** nullable persistent field */
    private String FILE_NAME;

    /** nullable persistent field */
    private Blob PLAN_PDF_FILE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_PORTFOLIO_PLAN_FILEVO(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILEPK comp_id, String FILE_NAME, Blob PLAN_PDF_FILE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.FILE_NAME = FILE_NAME;
        this.PLAN_PDF_FILE = PLAN_PDF_FILE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_PORTFOLIO_PLAN_FILEVO() {
    }

    /** minimal constructor */
    public TBFPS_PORTFOLIO_PLAN_FILEVO(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILEPK comp_id, Timestamp createtime, String creator) {
        this.comp_id = comp_id;
        this.createtime = createtime;
        this.creator = creator;
    }

    public com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_FILEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getFILE_NAME() {
        return this.FILE_NAME;
    }

    public void setFILE_NAME(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
    }

    public Blob getPLAN_PDF_FILE() {
        return this.PLAN_PDF_FILE;
    }

    public void setPLAN_PDF_FILE(Blob PLAN_PDF_FILE) {
        this.PLAN_PDF_FILE = PLAN_PDF_FILE;
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
        if ( !(other instanceof TBFPS_PORTFOLIO_PLAN_FILEVO) ) return false;
        TBFPS_PORTFOLIO_PLAN_FILEVO castOther = (TBFPS_PORTFOLIO_PLAN_FILEVO) other;
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
